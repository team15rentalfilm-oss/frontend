package com.iem.frontend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FilmsEntityController extends BaseEntityPageController {

    @GetMapping({"/entities/films", "/films"})
    public String view(Model model) {
        return renderEntityPage("films", model);
    }
}
