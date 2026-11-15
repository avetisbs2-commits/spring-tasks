package com.example.oauthlogin.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** Complete public endpoint and page. */
@Controller
public class HomeController {
    @GetMapping("/")
    public String home() {
        return "home";
    }
}
