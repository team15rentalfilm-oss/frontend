package com.iem.frontend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LanguagesEntityController extends BaseEntityPageController {

    @GetMapping({"/entities/languages", "/languages"})
    public String view(@RequestParam(name = "previewPage", defaultValue = "0") int previewPage, Model model) {
        return renderEntityPage("languages", previewPage, model);
    }
}
