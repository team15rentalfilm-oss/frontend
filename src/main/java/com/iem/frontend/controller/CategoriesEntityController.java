package com.iem.frontend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CategoriesEntityController extends BaseEntityPageController {

    @GetMapping({"/entities/categories", "/categories"})
    public String view(Model model) {
        return renderEntityPage("categories", model);
    }
}
