package com.iem.frontend.controller;

import com.iem.frontend.dto.*;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class EntityUIController {

    private final RestTemplate restTemplate = new RestTemplate();

    private final String BACKEND_API = "http://localhost:8081/api";

    // UTSAV'S ENTITIES

    @GetMapping("/payments")
    public String viewPayments(Model model) {
        PaymentDTO[] payments = restTemplate.getForObject(BACKEND_API + "/payments", PaymentDTO[].class);
        model.addAttribute("payments", payments);
        return "payments";
    }

    @GetMapping("/staff")
    public String viewStaff(Model model) {
        return renderEntityList(
                model,
                "Staff Management",
                "staff",
                List.of("staffId", "firstName", "lastName", "addressId", "email", "storeId", "active", "username", "lastUpdate"),
                StaffDTO[].class
        );
    }

    @GetMapping("/rentals")
    public String viewRentals(Model model) {
        return renderEntityList(
                model,
                "Rental Management",
                "rentals",
                List.of("rentalId", "rentalDate", "inventoryId", "customerId", "returnDate", "staffId", "lastUpdate"),
                RentalDTO[].class
        );
    }

    // SHREYASH'S ENTITIES

    @GetMapping("/stores")
    public String viewStores(Model model) {
        StoreDTO[] stores = restTemplate.getForObject(BACKEND_API + "/stores", StoreDTO[].class);
        model.addAttribute("stores", stores);
        return "stores";
    }

    @GetMapping("/inventory")
    public String viewInventory(Model model) {
        return renderEntityList(
                model,
                "Inventory Management",
                "inventory",
                List.of("inventoryId", "filmId", "storeId", "lastUpdate"),
                InventoryDTO[].class
        );
    }

    @GetMapping("/film-text")
    public String viewFilmText(Model model) {
        return renderEntityList(
                model,
                "Film Text Management",
                "filmText",
                List.of("filmId", "title", "description"),
                FilmTextDTO[].class
        );
    }

    // HARSHITA'S ENTITIES

    @GetMapping("/languages")
    public String viewLanguages(Model model) {
        return renderEntityList(
                model,
                "Language Management",
                "languages",
                List.of("languageId", "name", "lastUpdate"),
                LanguageDTO[].class
        );
    }

    @GetMapping("/film-actors")
    public String viewFilmActors(Model model) {
        return renderEntityList(
                model,
                "Film Actor Management",
                "filmActors",
                List.of("actorId", "filmId", "lastUpdate"),
                FilmActorDTO[].class
        );
    }

    @GetMapping("/actors")
    public String viewActors(Model model) {
        return renderEntityList(
                model,
                "Actor Management",
                "actors",
                List.of("actorId", "firstName", "lastName", "lastUpdate"),
                ActorDTO[].class
        );
    }

    // MAFUJ'S ENTITIES

    @GetMapping("/addresses")
    public String viewAddresses(Model model) {
        return renderEntityList(
                model,
                "Address Management",
                "addresses",
                List.of("addressId", "address", "address2", "district", "cityId", "postalCode", "phone", "location", "lastUpdate"),
                AddressDTO[].class
        );
    }

    @GetMapping("/cities")
    public String viewCities(Model model) {
        return renderEntityList(
                model,
                "City Management",
                "cities",
                List.of("cityId", "city", "countryId", "lastUpdate"),
                CityDTO[].class
        );
    }

    @GetMapping("/countries")
    public String viewCountries(Model model) {
        return renderEntityList(
                model,
                "Country Management",
                "countries",
                List.of("countryId", "country", "lastUpdate"),
                CountryDTO[].class
        );
    }

    @GetMapping("/customers")
    public String viewCustomers(Model model) {
        return renderEntityList(
                model,
                "Customer Management",
                "customers",
                List.of("customerId", "storeId", "firstName", "lastName", "email", "addressId", "active", "createDate", "lastUpdate"),
                CustomerDTO[].class
        );
    }

    // ANWESHA'S ENTITIES

    @GetMapping("/films")
    public String viewFilms(Model model) {
        return renderEntityList(
                model,
                "Film Management",
                "films",
                List.of("filmId", "title", "description", "releaseYear", "languageId", "originalLanguageId", "rentalDuration", "rentalRate", "length", "replacementCost", "rating", "specialFeatures", "lastUpdate"),
                FilmDTO[].class
        );
    }

    @GetMapping("/film-categories")
    public String viewFilmCategories(Model model) {
        return renderEntityList(
                model,
                "Film Category Management",
                "filmCategories",
                List.of("filmId", "categoryId", "lastUpdate"),
                FilmCategoryDTO[].class
        );
    }

    @GetMapping("/categories")
    public String viewCategories(Model model) {
        return renderEntityList(
                model,
                "Category Management",
                "categories",
                List.of("categoryId", "name", "lastUpdate"),
                CategoryDTO[].class
        );
    }

    private <T> String renderEntityList(
            Model model,
            String pageTitle,
            String entityName,
            List<String> columns,
            Class<T[]> responseType
    ) {
        T[] response = restTemplate.getForObject(BACKEND_API + "/" + entityName, responseType);
        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("entityName", entityName);
        model.addAttribute("columns", columns);
        model.addAttribute(
                "rows",
                response == null
                        ? List.of()
                        : Arrays.stream(response).map(row -> toRowMap(row, columns)).toList()
        );
        return "entity-list";
    }

    private Map<String, Object> toRowMap(Object source, List<String> columns) {
        BeanWrapper beanWrapper = new BeanWrapperImpl(source);
        Map<String, Object> row = new LinkedHashMap<>();
        for (String column : columns) {
            row.put(column, beanWrapper.getPropertyValue(column));
        }
        return row;
    }
}
