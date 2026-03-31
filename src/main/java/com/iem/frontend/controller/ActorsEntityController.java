package com.iem.frontend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ActorsEntityController extends BaseEntityPageController {

    @GetMapping({"/entities/actors", "/actors"})
    public String view(Model model) {
        return renderEntityPage("actors", model);
    }
}
