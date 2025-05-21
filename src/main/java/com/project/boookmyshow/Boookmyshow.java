package com.project.boookmyshow;

import com.project.boookmyshow.controllers.BookingController;
import com.project.boookmyshow.controllers.UserController;
import com.project.boookmyshow.dtos.BookMovieRequestDto;
import com.project.boookmyshow.dtos.BookMovieResponseDto;
import com.project.boookmyshow.dtos.ResponseStatus;
import com.project.boookmyshow.dtos.SignUpRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.List;

@EnableJpaAuditing
@SpringBootApplication
public class Boookmyshow implements CommandLineRunner {
    @Autowired
    private UserController userController;

    @Autowired
    BookingController bookingController;
    @Override
    public void run(String... args) throws Exception {
        SignUpRequestDto signUpRequestDto = new SignUpRequestDto();
        signUpRequestDto.setEmail("prince6@sams.com");
        signUpRequestDto.setPassword("password");
        userController.signUp(signUpRequestDto);
        try {
            userController.signUp(signUpRequestDto);
            System.out.println("✅ Signup Success for: " + signUpRequestDto.getEmail());
        } catch (Exception e) {
            System.out.println("❌ Signup Failed: " + e.getMessage());
        }

        // Booking logic
        BookMovieRequestDto bookingRequest = new BookMovieRequestDto();
        bookingRequest.setUserId(1L); // replace with actual user ID from your DB
        bookingRequest.setShowId(2L); // replace with valid show ID
        bookingRequest.setShowSeatIds(List.of(10L, 11L)); // valid seat IDs



        BookMovieResponseDto response = bookingController.bookMovie(bookingRequest);

        if (response.getResponseStatus() == ResponseStatus.SUCCESS) {
            System.out.println("🎟️ Booking Successful");
            System.out.println("🔢 Booking ID: " + response.getBookingId());
            System.out.println("💰 Amount: ₹" + response.getAmount());
        } else {
            System.out.println("❌ Booking Failed");
        }
    }

    public static void main(String[] args) {

//        BaseModel bm = new BaseModel();
//        bm.
        SpringApplication.run(Boookmyshow.class, args);
//        System.out.println("Hi");


    }

}
