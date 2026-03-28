package com.iem.frontend.controller;

import com.iem.frontend.catalog.ExplorerCatalog;
import com.iem.frontend.catalog.ExplorerCatalog.MemberDefinition;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Controller
public class UIController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("members", ExplorerCatalog.members());
        model.addAttribute("baseUrl", ExplorerCatalog.BASE_API_URL);
        model.addAttribute("openApiPath", ExplorerCatalog.OPEN_API_PATH);
        return "index";
    }

    @GetMapping("/member/{name}")
    public String memberPage(@PathVariable String name, Model model) {
        MemberDefinition member = ExplorerCatalog.member(name);
        if (member == null) {
            throw new ResponseStatusException(NOT_FOUND, "Unknown member: " + name);
        }

        model.addAttribute("member", member);
        model.addAttribute("entities", ExplorerCatalog.entitiesForMember(name));
        model.addAttribute("baseUrl", ExplorerCatalog.BASE_API_URL);
        model.addAttribute("openApiPath", ExplorerCatalog.OPEN_API_PATH);
        return "member";
    }
}
