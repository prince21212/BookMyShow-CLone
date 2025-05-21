package com.project.boookmyshow.controllers;

import com.project.boookmyshow.dtos.BookMovieRequestDto;
import com.project.boookmyshow.dtos.BookMovieResponseDto;
import com.project.boookmyshow.dtos.ResponseStatus;
import com.project.boookmyshow.models.Booking;
import com.project.boookmyshow.services.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// @Controller // @Service // @Repository -> just a tag to tell spring that this is a special class
@RestController // ✅ Converts return values to JSON automatically
@RequestMapping("/booking") // ✅ Base route for all endpoints in this controller
public class BookingController {
    private BookingService bookingService;

    @Autowired // -> Automatically find an object of the params, create it and send here
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/form")
    public String showForm() {
        return "bookingForm"; // Thymeleaf template name without `.html`
    }

    @PostMapping("/book")
    public BookMovieResponseDto bookMovie(BookMovieRequestDto request) {
        BookMovieResponseDto response = new BookMovieResponseDto();
        System.out.println("📩 BookingController: Booking request received.");
        System.out.println("➡️  User ID: " + request.getUserId());
        System.out.println("➡️  Show ID: " + request.getShowId());
        System.out.println("➡️  Show Seat IDs: " + request.getShowSeatIds());
        Booking booking;

        try {
            booking = bookingService.bookMovie(
                    request.getUserId(),
                    request.getShowSeatIds(),
                    request.getShowId()
            );

            response.setResponseStatus(ResponseStatus.SUCCESS);
            response.setBookingId(booking.getId());
            response.setAmount(booking.getAmount());
            System.out.println("✅ Booking Successful!");
            System.out.println("🆔 Booking ID: " + booking.getId());
            System.out.println("💰 Amount Charged: ₹" + booking.getAmount());
        } catch (Exception e) {
            response.setResponseStatus(ResponseStatus.FAILURE);
            System.out.println("❌ Booking Failed: " + e.getMessage());

        }

        return response;
    }

}
