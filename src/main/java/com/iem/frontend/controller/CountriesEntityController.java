package com.iem.frontend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CountriesEntityController extends BaseEntityPageController {

    @GetMapping({"/entities/countries", "/countries"})
    public String view(@RequestParam(name = "previewPage", defaultValue = "0") int previewPage, Model model) {
        return renderEntityPage("countries", previewPage, model);
    }
}
