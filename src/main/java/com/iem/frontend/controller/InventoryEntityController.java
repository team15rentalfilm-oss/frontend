package com.iem.frontend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InventoryEntityController extends BaseEntityPageController {

    @GetMapping({"/entities/inventory", "/inventory", "/inventories"})
    public String view(Model model) {
        return renderEntityPage("inventory", model);
    }
}
