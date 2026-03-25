package com.iem.frontend.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class UIController {

    // 1. Route for the homepage
    @GetMapping("/")
    public String home() {
        return "index";
    }

    // 2. Dynamic route for all team members
    // If you click /member/utsav, it returns the "utsav.html" template
    @GetMapping("/member/{name}")
    public String memberPage(@PathVariable String name) {
        return name;
    }
}
