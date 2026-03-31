package com.iem.frontend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PaymentsEntityController extends BaseEntityPageController {

    @GetMapping({"/entities/payments", "/payments"})
    public String view(Model model) {
        return renderEntityPage("payments", model);
    }
}
