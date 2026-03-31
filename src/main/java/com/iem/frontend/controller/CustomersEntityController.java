package com.iem.frontend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CustomersEntityController extends BaseEntityPageController {

    @GetMapping({"/entities/customers", "/customers"})
    public String view(Model model) {
        return renderEntityPage("customers", model);
    }
}
