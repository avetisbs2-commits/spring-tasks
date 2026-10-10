package com.example.usermvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Opens the user list when you visit http://localhost:8081/
 *
 * PROVIDED CODE - students do not need to change it.
 */
@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "redirect:/users";
    }
}
