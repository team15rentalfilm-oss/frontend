package com.iem.frontend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CountriesEntityController extends BaseEntityPageController {

    @GetMapping({"/entities/countries", "/countries"})
    public String view(Model model) {
        return renderEntityPage("countries", model);
    }
}
