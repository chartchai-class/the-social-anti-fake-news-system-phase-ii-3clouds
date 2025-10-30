package se331.backend.rest.controller;

import org.springframework.web.bind.annotation.*;

@RestController
public class RootController {

    @GetMapping("/")
    public String root() {
        return "Backend API is running successfully!";
    }
}