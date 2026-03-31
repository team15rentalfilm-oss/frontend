package com.iem.frontend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StoresEntityController extends BaseEntityPageController {

    @GetMapping({"/entities/stores", "/stores"})
    public String view(Model model) {
        return renderEntityPage("stores", model);
    }
}
