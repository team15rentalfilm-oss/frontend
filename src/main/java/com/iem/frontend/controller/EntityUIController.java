package com.iem.frontend.controller;

import com.iem.frontend.dto.StoreDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

@Controller
public class EntityUIController {

    // This makes HTTP calls to your backend project
    private final RestTemplate restTemplate = new RestTemplate();

    // Assuming your backend project is still running on port 8081
    private final String BACKEND_API = "http://localhost:8081/api";


    // ==========================================
    // UTSAV'S ENTITIES
    // ==========================================

    @GetMapping("/payments")
    public String viewPayments(Model model) {
        // 1. Fetch JSON array from Backend
        Object[] payments = restTemplate.getForObject(BACKEND_API + "/payments", Object[].class);
        // 2. Add it to the model so Thymeleaf can use ${payments}
        model.addAttribute("payments", payments);
        // 3. Return the HTML file name (payments.html)
        return "payments";
    }

    @GetMapping("/staff")
    public String viewStaff(Model model) {
        Object[] staff = restTemplate.getForObject(BACKEND_API + "/staff", Object[].class);
        model.addAttribute("staff", staff);
        return "staff";
    }

    @GetMapping("/rentals")
    public String viewRentals(Model model) {
        Object[] rentals = restTemplate.getForObject(BACKEND_API + "/rentals", Object[].class);
        model.addAttribute("rentals", rentals);
        return "rentals";
    }

    // ==========================================
    // SHREYASH'S ENTITIES
    // ==========================================

    @GetMapping("/stores")
    public String viewStores(Model model) {
        // Use StoreDTO[].class instead of Object[].class
        Object[] stores = restTemplate.getForObject(BACKEND_API + "/stores", Object[].class);
        model.addAttribute("stores", stores);
        return "stores";
    }

    // Just copy and paste this block for the remaining 12 entities!
    // Make sure the model.addAttribute("name") matches the variable in your HTML th:each loop.
}
