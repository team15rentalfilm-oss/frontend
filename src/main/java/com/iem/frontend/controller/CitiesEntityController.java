package com.iem.frontend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CitiesEntityController extends BaseEntityPageController {

    @GetMapping({"/entities/cities", "/cities"})
    public String view(Model model) {
        return renderEntityPage("cities", model);
    }
}
