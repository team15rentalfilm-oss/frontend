package com.iem.frontend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LanguagesEntityController extends BaseEntityPageController {

    @GetMapping({"/entities/languages", "/languages"})
    public String view(Model model) {
        return renderEntityPage("languages", model);
    }
}
