package com.iem.frontend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RentalsEntityController extends BaseEntityPageController {

    @GetMapping({"/entities/rentals", "/rentals"})
    public String view(Model model) {
        return renderEntityPage("rentals", model);
    }
}
