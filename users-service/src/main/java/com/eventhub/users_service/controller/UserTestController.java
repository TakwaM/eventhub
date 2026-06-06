package com.eventhub.users_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserTestController {

    @GetMapping("/user/test")
    public String userTest() {
        return "USER OK";
    }
}
