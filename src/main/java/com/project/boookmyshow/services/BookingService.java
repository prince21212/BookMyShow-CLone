package com.project.boookmyshow.services;

import com.project.boookmyshow.models.*;
import com.project.boookmyshow.models.*;
import com.project.boookmyshow.repositories.BookingRepository;
import com.project.boookmyshow.repositories.ShowRepository;
import com.project.boookmyshow.repositories.ShowSeatRepository;
import com.project.boookmyshow.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {
    private BookingRepository bookingRepository;
    private ShowRepository showRepository;
    private ShowSeatRepository showSeatRepository;
    private UserRepository userRepository;
    private PriceCalculator priceCalculator;

    @Autowired
    public BookingService(BookingRepository bookingRepository, ShowRepository showRepository,
                          ShowSeatRepository showSeatRepository, UserRepository userRepository,
                          PriceCalculator priceCalculator) {
        this.bookingRepository = bookingRepository;
        this.showRepository = showRepository;
        this.showSeatRepository = showSeatRepository;
        this.userRepository = userRepository;
        this.priceCalculator = priceCalculator;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Booking bookMovie(Long userId, List<Long> seatIds, Long showId) {
        System.out.println("🔔 Booking initiated by user ID: " + userId + " for show ID: " + showId + " and seats: " + seatIds);

        // start transaction;
        // --- TODAY WE WILL START LOCK HERE
        // 1. Get user with that userId
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            System.out.println("❌ User with ID " + userId + " not found.");
            throw new RuntimeException("User not found");
           // throw  new RuntimeException();
        }

        User bookedBy = userOptional.get();

        // 2. Get show with that showId
        Optional<Show> showOptional = showRepository.findById(showId);

        if (showOptional.isEmpty()) {
            System.out.println("❌ Show with ID " + showId + " not found.");
            throw new RuntimeException("Show not found");
            //throw new RuntimeException();
        }

        Show bookedShow = showOptional.get();

        // -------------- TAKE LOCK ---------------
        // 3. Get ShowSeat with that seatIds

        List<ShowSeat> showSeats = showSeatRepository.findAllById(seatIds);
        System.out.println("🔍 Fetched seats for booking: ");
        for (ShowSeat s : showSeats) {
            System.out.println("Seat ID: " + s.getId() + " | Status: " + s.getShowSeatStatus());
        }
        // 4. Check if all show seats are available
        // 5. If no, throw error
        for (ShowSeat showSeat: showSeats) {
            if (!(showSeat.getShowSeatStatus().equals(ShowSeatStatus.AVAILABLE) ||
                    (showSeat.getShowSeatStatus().equals(ShowSeatStatus.BLOCKED) &&
                            Duration.between(showSeat.getBlockedAt().toInstant(), new Date().toInstant()).toMinutes() > 15
            ))) {
                System.out.println("❌ Seat ID " + showSeat.getId() + " is already booked or recently blocked.");
                throw new RuntimeException("Seat not available");
               // throw new RuntimeException();
            }
        }

        List<ShowSeat> savedShowSeats = new ArrayList<>();

        // 6. If yes, Mark the status of show seats as LOCKED
        for (ShowSeat showSeat: showSeats) {
            showSeat.setShowSeatStatus(ShowSeatStatus.BLOCKED);
            // 7. Save updated show seats to DB
            savedShowSeats.add(showSeatRepository.save(showSeat));
            System.out.println("✅ Seat ID " + showSeat.getId() + " status updated to BLOCKED");

        }

        // ---------------END LOCK -----------------
        // 8. Create corresponding booking object
        Booking booking = new Booking();
        booking.setBookingStatus(BookingStatus.PENDING);
        booking.setShowSeats(savedShowSeats);
        booking.setUser(bookedBy);
        booking.setBookedAt(new Date());
        booking.setShow(bookedShow);
        booking.setAmount(priceCalculator.calculatePrice(savedShowSeats, bookedShow));
        booking.setPayments(new ArrayList<>());

        // 9. Return boking object
        // TODAY WE WILL END LOCK THERE
        Booking savedBooking = bookingRepository.save(booking);
        System.out.println("✅ Booking SUCCESS. Booking ID: " + savedBooking.getId() + " | User: " + userId + " | Seats: " + seatIds);

        return savedBooking;
    }
}
