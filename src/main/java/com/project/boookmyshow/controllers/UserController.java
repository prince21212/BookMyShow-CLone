package com.project.boookmyshow.controllers;

import com.project.boookmyshow.dtos.ResponseStatus;
import com.project.boookmyshow.dtos.SignUpRequestDto;
import com.project.boookmyshow.dtos.SignUpResponseDto;
import com.project.boookmyshow.models.User;
import com.project.boookmyshow.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class UserController {
    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    public SignUpResponseDto signUp(SignUpRequestDto request) {
        User user;
        SignUpResponseDto response = new SignUpResponseDto();

        try {
            user = userService.signUp(request.getEmail(), request.getPassword());
            response.setResponseStatus(ResponseStatus.SUCCESS);
            response.setUserId(user.getId());
        } catch (Exception e) {
            response.setResponseStatus(ResponseStatus.FAILURE);
        }

        return response;
    }
}
