package com.iem.frontend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AddressesEntityController extends BaseEntityPageController {

    @GetMapping({"/entities/addresses", "/addresses"})
    public String view(Model model) {
        return renderEntityPage("addresses", model);
    }
}
