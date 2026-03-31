package com.iem.frontend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StaffEntityController extends BaseEntityPageController {

    @GetMapping({"/entities/staff", "/staff"})
    public String view(Model model) {
        return renderEntityPage("staff", model);
    }
}
