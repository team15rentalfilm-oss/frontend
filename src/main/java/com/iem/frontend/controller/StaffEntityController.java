package com.iem.frontend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class StaffEntityController extends BaseEntityPageController {

    @GetMapping({"/entities/staff", "/staff"})
    public String view(@RequestParam(name = "previewPage", defaultValue = "0") int previewPage, Model model) {
        return renderEntityPage("staff", previewPage, model);
    }
}
