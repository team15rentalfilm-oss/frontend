package com.iem.frontend.catalog;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;

public final class ExplorerCatalog {

    public static final String BASE_API_URL = "http://localhost:8081";
    public static final String OPEN_API_PATH = "/v3/api-docs";

    private static final Map<String, MemberDefinition> MEMBERS = buildMembers();
    private static final Map<String, EntityDefinition> ENTITIES = buildEntities();

    private ExplorerCatalog() {
    }

    public static List<MemberDefinition> members() {
        return List.copyOf(MEMBERS.values());
    }

    public static MemberDefinition member(String key) {
        return MEMBERS.get(key);
    }

    public static List<EntityDefinition> entitiesForMember(String memberKey) {
        MemberDefinition member = member(memberKey);
        if (member == null) {
            return List.of();
        }
        return member.entityKeys().stream()
                .map(ENTITIES::get)
                .toList();
    }

    public static EntityDefinition entity(String key) {
        return ENTITIES.get(key);
    }

    public static List<EntityDefinition> entities() {
        return List.copyOf(ENTITIES.values());
    }

    private static Map<String, MemberDefinition> buildMembers() {
        Map<String, MemberDefinition> members = new LinkedHashMap<>();
        members.put("anwesha", new MemberDefinition(
                "anwesha",
                "Anwesha Ghosh",
                "/images/anwesha.svg",
                "Films and categories",
                "#a64b2a",
                "Works on the catalog side of the rental system.",
                List.of("films", "categories")
        ));
        members.put("harshita", new MemberDefinition(
                "harshita",
                "Harshita Dujari",
                "/images/harshita.svg",
                "Actors, languages, and film actor links",
                "#0e5f76",
                "Covers performer records and actor to film mappings.",
                List.of("actors", "languages", "filmActor")
        ));
        members.put("mafuj", new MemberDefinition(
                "mafuj",
                "Mafuj Gazi",
                "/images/mafuj.svg",
                "Customers",
                "#0b6e4f",
                "Handles customer records exposed by the current backend contract.",
                List.of("customers")
        ));
        members.put("shreyash", new MemberDefinition(
                "shreyash",
                "Shreyash Singh",
                "/images/shreyash.svg",
                "Stores, inventory, and film texts",
                "#7a3e00",
                "Owns store-facing inventory and searchable film text data.",
                List.of("stores", "inventories", "filmTexts")
        ));
        members.put("utsav", new MemberDefinition(
                "utsav",
                "Utsav Sarda",
                "/images/utsav.jpg",
                "Staff, payments, and rentals",
                "#7a1f3d",
                "Maintains operational workflows for staff activity and transactions.",
                List.of("staff", "payments", "rentals")
        ));
        return Collections.unmodifiableMap(new LinkedHashMap<>(members));
    }

    private static Map<String, EntityDefinition> buildEntities() {
        Map<String, EntityDefinition> entities = new LinkedHashMap<>();

        entities.put("actors", new EntityDefinition(
                "actors",
                "Actors",
                "harshita",
                "Browse actor data and run create, update, search, or delete calls.",
                "ActorDTO",
                List.of("firstName", "lastName"),
                "/api/actors",
                List.of(
                        endpoint("List actors", "GET", "/api/actors", null, null, "ActorDTO[]", "GET /api/actors -> ActorDTO[]"),
                        endpoint("Get actor by id", "GET", "/api/actors/{id}", "Replace {id} with an actor id.", null, "ActorDTO", "GET /api/actors/{id} -> ActorDTO"),
                        endpoint("Search actors by name", "GET", "/api/actors/search?name=", "Provide a full or partial name.", null, "ActorDTO[]", "GET /api/actors/search?name=... -> ActorDTO[]"),
                        endpoint("Create actor", "POST", "/api/actors", null, "ActorDTO", "ActorDTO", "POST /api/actors body: ActorDTO -> ActorDTO"),
                        endpoint("Replace actor", "PUT", "/api/actors/{id}", "Replace {id} with an actor id.", "ActorDTO", "ActorDTO", "PUT /api/actors/{id} body: ActorDTO -> ActorDTO"),
                        endpoint("Update actor fields", "PATCH", "/api/actors/{id}", "Replace {id} with an actor id.", "partial ActorDTO", "ActorDTO", "PATCH /api/actors/{id} body: partial ActorDTO -> ActorDTO"),
                        endpoint("Delete actor", "DELETE", "/api/actors/{id}", "Replace {id} with an actor id.", null, null, "DELETE /api/actors/{id}")
                )
        ));

        entities.put("categories", new EntityDefinition(
                "categories",
                "Categories",
                "anwesha",
                "Manage film category values with full CRUD access.",
                "CategoryDTO",
                List.of("id", "name"),
                "/api/categories",
                List.of(
                        endpoint("List categories", "GET", "/api/categories", null, null, "CategoryDTO[]", "GET /api/categories -> CategoryDTO[]"),
                        endpoint("Get category by id", "GET", "/api/categories/{id}", "Replace {id} with a category id.", null, "CategoryDTO", "GET /api/categories/{id} -> CategoryDTO"),
                        endpoint("Create category", "POST", "/api/categories", null, "CategoryDTO", "CategoryDTO", "POST /api/categories body: CategoryDTO -> CategoryDTO"),
                        endpoint("Replace category", "PUT", "/api/categories/{id}", "Replace {id} with a category id.", "CategoryDTO", "CategoryDTO", "PUT /api/categories/{id} body: CategoryDTO -> CategoryDTO"),
                        endpoint("Delete category", "DELETE", "/api/categories/{id}", "Replace {id} with a category id.", null, null, "DELETE /api/categories/{id}")
                )
        ));

        entities.put("customers", new EntityDefinition(
                "customers",
                "Customers",
                "mafuj",
                "View, search, create, update, and delete customers.",
                "Customer",
                List.of("customerId", "storeId", "firstName", "lastName", "email", "addressId", "active", "createDate", "lastUpdate"),
                "/api/customers",
                List.of(
                        endpoint("List customers", "GET", "/api/customers", null, null, "Customer[]", "GET /api/customers -> Customer[]"),
                        endpoint("Get customer by id", "GET", "/api/customers/{id}", "Replace {id} with a customer id.", null, "Customer", "GET /api/customers/{id} -> Customer"),
                        endpoint("Search customers", "GET", "/api/customers/search?firstName=&lastName=&email=&storeId=", "Use any combination of firstName, lastName, email, and storeId.", null, "Customer[]", "GET /api/customers/search?firstName=&lastName=&email=&storeId= -> Customer[]"),
                        endpoint("Create customer", "POST", "/api/customers", null, "Customer", "Customer", "POST /api/customers body: Customer -> Customer"),
                        endpoint("Replace customer", "PUT", "/api/customers/{id}", "Replace {id} with a customer id.", "Customer", "Customer", "PUT /api/customers/{id} body: Customer -> Customer"),
                        endpoint("Update customer fields", "PATCH", "/api/customers/{id}", "Replace {id} with a customer id.", "partial Customer", "Customer", "PATCH /api/customers/{id} body: partial Customer -> Customer"),
                        endpoint("Delete customer", "DELETE", "/api/customers/{id}", "Replace {id} with a customer id.", null, null, "DELETE /api/customers/{id}")
                )
        ));

        entities.put("filmActor", new EntityDefinition(
                "filmActor",
                "Film Actor",
                "harshita",
                "Work with the actor-film join table and inspect enriched detail rows.",
                "FilmActorDTO",
                List.of("actorId", "filmId"),
                "/api/film-actor",
                List.of(
                        endpoint("List film actor links", "GET", "/api/film-actor", null, null, "FilmActorDTO[]", "GET /api/film-actor -> FilmActorDTO[]"),
                        endpoint("List film actor details", "GET", "/api/film-actor/details", "Returns FilmActorDetailsDTO rows with actor and film names.", null, "FilmActorDetailsDTO[]", "GET /api/film-actor/details -> FilmActorDetailsDTO[]"),
                        endpoint("Create film actor link", "POST", "/api/film-actor", null, "FilmActorDTO", "FilmActorDTO", "POST /api/film-actor body: FilmActorDTO -> FilmActorDTO"),
                        endpoint("Delete film actor link", "DELETE", "/api/film-actor?actorId=&filmId=", "Provide both actorId and filmId in the query string.", null, null, "DELETE /api/film-actor?actorId=&filmId=")
                )
        ));

        entities.put("films", new EntityDefinition(
                "films",
                "Films",
                "anwesha",
                "Explore the film catalog and perform full record maintenance.",
                "FilmDTO",
                List.of("id", "title", "description", "releaseYear", "languageId", "originalLanguageId", "rentalDuration", "rentalRate", "length", "replacementCost", "rating", "specialFeatures", "categoryIds"),
                "/api/films",
                List.of(
                        endpoint("List films", "GET", "/api/films", null, null, "FilmDTO[]", "GET /api/films -> FilmDTO[]"),
                        endpoint("Get film by id", "GET", "/api/films/{id}", "Replace {id} with a film id.", null, "FilmDTO", "GET /api/films/{id} -> FilmDTO"),
                        endpoint("Search films", "GET", "/api/films/search?title=&year=", "Use title, year, or both query parameters.", null, "FilmDTO[]", "GET /api/films/search?title=&year= -> FilmDTO[]"),
                        endpoint("Create film", "POST", "/api/films", null, "FilmDTO", "FilmDTO", "POST /api/films body: FilmDTO -> FilmDTO"),
                        endpoint("Replace film", "PUT", "/api/films/{id}", "Replace {id} with a film id.", "FilmDTO", "FilmDTO", "PUT /api/films/{id} body: FilmDTO -> FilmDTO"),
                        endpoint("Update film fields", "PATCH", "/api/films/{id}", "Replace {id} with a film id.", "partial FilmDTO", "FilmDTO", "PATCH /api/films/{id} body: partial FilmDTO -> FilmDTO"),
                        endpoint("Delete film", "DELETE", "/api/films/{id}", "Replace {id} with a film id.", null, null, "DELETE /api/films/{id}")
                )
        ));

        entities.put("filmTexts", new EntityDefinition(
                "filmTexts",
                "Film Texts",
                "shreyash",
                "Inspect text-search records through the `/api/v1` contract.",
                "FilmText",
                List.of("filmId", "title", "description"),
                "/api/v1/film-texts",
                List.of(
                        endpoint("List film texts", "GET", "/api/v1/film-texts", null, null, "FilmText[]", "GET /api/v1/film-texts -> FilmText[]"),
                        endpoint("Filter film texts", "GET", "/api/v1/film-texts?filmId=&title=&description=", "Use any combination of filmId, title, or description.", null, "FilmText[]", "GET /api/v1/film-texts?filmId=&title=&description= -> FilmText[]"),
                        endpoint("Get film text by id", "GET", "/api/v1/film-texts/{id}", "Replace {id} with a film id.", null, "FilmText", "GET /api/v1/film-texts/{id} -> FilmText"),
                        endpoint("Create film text", "POST", "/api/v1/film-texts", null, "FilmText", "FilmText", "POST /api/v1/film-texts body: FilmText -> FilmText"),
                        endpoint("Replace film text", "PUT", "/api/v1/film-texts/{id}", "Replace {id} with a film id.", "FilmText", "FilmText", "PUT /api/v1/film-texts/{id} body: FilmText -> FilmText"),
                        endpoint("Update film text fields", "PATCH", "/api/v1/film-texts/{id}", "Replace {id} with a film id. The partial body should not include filmId.", "partial FilmText (except filmId)", "FilmText", "PATCH /api/v1/film-texts/{id} body: partial FilmText (except filmId) -> FilmText"),
                        endpoint("Delete film text", "DELETE", "/api/v1/film-texts/{id}", "Replace {id} with a film id.", null, null, "DELETE /api/v1/film-texts/{id}")
                )
        ));

        entities.put("inventories", new EntityDefinition(
                "inventories",
                "Inventories",
                "shreyash",
                "Manage inventory rows through the `/api/v1` inventory endpoints.",
                "InventoryDTO",
                List.of("inventoryId", "filmId", "storeId", "lastUpdate"),
                "/api/v1/inventories",
                List.of(
                        endpoint("List inventories", "GET", "/api/v1/inventories", null, null, "InventoryDTO[]", "GET /api/v1/inventories -> InventoryDTO[]"),
                        endpoint("Filter inventories", "GET", "/api/v1/inventories?inventoryId=&filmId=&storeId=", "Use inventoryId, filmId, storeId, or any combination.", null, "InventoryDTO[]", "GET /api/v1/inventories?inventoryId=&filmId=&storeId= -> InventoryDTO[]"),
                        endpoint("Get inventory by id", "GET", "/api/v1/inventories/{id}", "Replace {id} with an inventory id.", null, "InventoryDTO", "GET /api/v1/inventories/{id} -> InventoryDTO"),
                        endpoint("Create inventory", "POST", "/api/v1/inventories", null, "InventoryDTO", "InventoryDTO", "POST /api/v1/inventories body: InventoryDTO -> InventoryDTO"),
                        endpoint("Replace inventory", "PUT", "/api/v1/inventories/{id}", "Replace {id} with an inventory id.", "InventoryDTO", "InventoryDTO", "PUT /api/v1/inventories/{id} body: InventoryDTO -> InventoryDTO"),
                        endpoint("Update inventory fields", "PATCH", "/api/v1/inventories/{id}", "Replace {id} with an inventory id.", "{filmId?,storeId?}", "InventoryDTO", "PATCH /api/v1/inventories/{id} body: {filmId?,storeId?} -> InventoryDTO"),
                        endpoint("Delete inventory", "DELETE", "/api/v1/inventories/{id}", "Replace {id} with an inventory id.", null, null, "DELETE /api/v1/inventories/{id}")
                )
        ));

        entities.put("languages", new EntityDefinition(
                "languages",
                "Languages",
                "harshita",
                "Look up, search, and maintain language entries.",
                "LanguageDTO",
                List.of("languageId", "name"),
                "/api/languages",
                List.of(
                        endpoint("List languages", "GET", "/api/languages", null, null, "LanguageDTO[]", "GET /api/languages -> LanguageDTO[]"),
                        endpoint("Get language by id", "GET", "/api/languages/{id}", "Replace {id} with a language id.", null, "LanguageDTO", "GET /api/languages/{id} -> LanguageDTO"),
                        endpoint("Search languages", "GET", "/api/languages/search?name=", "Provide a language name or prefix.", null, "LanguageDTO[]", "GET /api/languages/search?name=... -> LanguageDTO[]"),
                        endpoint("Create language", "POST", "/api/languages", null, "LanguageDTO", "LanguageDTO", "POST /api/languages body: LanguageDTO -> LanguageDTO"),
                        endpoint("Replace language", "PUT", "/api/languages/{id}", "Replace {id} with a language id.", "LanguageDTO", "LanguageDTO", "PUT /api/languages/{id} body: LanguageDTO -> LanguageDTO"),
                        endpoint("Update language fields", "PATCH", "/api/languages/{id}", "Replace {id} with a language id.", "partial LanguageDTO", "LanguageDTO", "PATCH /api/languages/{id} body: partial LanguageDTO -> LanguageDTO"),
                        endpoint("Delete language", "DELETE", "/api/languages/{id}", "Replace {id} with a language id.", null, null, "DELETE /api/languages/{id}")
                )
        ));

        entities.put("payments", new EntityDefinition(
                "payments",
                "Payments",
                "utsav",
                "Review payment records and call the filtered payment endpoints.",
                "PaymentDTO",
                List.of("paymentId", "customerId", "staffId", "rentalId", "amount", "paymentDate", "lastUpdate"),
                "/api/payments",
                List.of(
                        endpoint("List payments", "GET", "/api/payments", null, null, "PaymentDTO[]", "GET /api/payments -> PaymentDTO[]"),
                        endpoint("Filter payments", "GET", "/api/payments?customerId=", "Use one of customerId, staffId, or rentalId as a query parameter.", null, "PaymentDTO[]", "GET /api/payments?customerId= | ?staffId= | ?rentalId= -> PaymentDTO[]"),
                        endpoint("Get payment by id", "GET", "/api/payments/{id}", "Replace {id} with a payment id.", null, "PaymentDTO", "GET /api/payments/{id} -> PaymentDTO"),
                        endpoint("Create payment", "POST", "/api/payments", null, "PaymentDTO", "PaymentDTO", "POST /api/payments body: PaymentDTO -> PaymentDTO"),
                        endpoint("Replace payment", "PUT", "/api/payments/{id}", "Replace {id} with a payment id.", "PaymentDTO", "PaymentDTO", "PUT /api/payments/{id} body: PaymentDTO -> PaymentDTO"),
                        endpoint("Delete payment", "DELETE", "/api/payments/{id}", "Replace {id} with a payment id.", null, null, "DELETE /api/payments/{id}")
                )
        ));

        entities.put("rentals", new EntityDefinition(
                "rentals",
                "Rentals",
                "utsav",
                "Drive rental operations and use the available filter combinations.",
                "RentalDTO",
                List.of("rentalId", "rentalDate", "inventoryId", "customerId", "returnDate", "staffId", "lastUpdate"),
                "/api/rentals",
                List.of(
                        endpoint("List rentals", "GET", "/api/rentals", null, null, "RentalDTO[]", "GET /api/rentals -> RentalDTO[]"),
                        endpoint("Filter rentals", "GET", "/api/rentals?customerId=", "Use one of customerId, inventoryId, or staffId as a query parameter.", null, "RentalDTO[]", "GET /api/rentals?customerId= | ?inventoryId= | ?staffId= -> RentalDTO[]"),
                        endpoint("Get rental by id", "GET", "/api/rentals/{id}", "Replace {id} with a rental id.", null, "RentalDTO", "GET /api/rentals/{id} -> RentalDTO"),
                        endpoint("Create rental", "POST", "/api/rentals", null, "RentalDTO", "RentalDTO", "POST /api/rentals body: RentalDTO -> RentalDTO"),
                        endpoint("Replace rental", "PUT", "/api/rentals/{id}", "Replace {id} with a rental id.", "RentalDTO", "RentalDTO", "PUT /api/rentals/{id} body: RentalDTO -> RentalDTO"),
                        endpoint("Delete rental", "DELETE", "/api/rentals/{id}", "Replace {id} with a rental id.", null, null, "DELETE /api/rentals/{id}")
                )
        ));

        entities.put("staff", new EntityDefinition(
                "staff",
                "Staff",
                "utsav",
                "Manage staff records through the `/api/v1` staff endpoints.",
                "StaffDTO",
                List.of("staffId", "firstName", "lastName", "addressId", "email", "storeId", "active", "username"),
                "/api/v1/staff",
                List.of(
                        endpoint("List staff", "GET", "/api/v1/staff", null, null, "StaffDTO[]", "GET /api/v1/staff -> StaffDTO[]"),
                        endpoint("Filter staff", "GET", "/api/v1/staff?staffId=&firstName=&lastName=&addressId=&email=&storeId=&active=&username=", "Use one or more filter parameters.", null, "StaffDTO[]", "GET /api/v1/staff?staffId=&firstName=&lastName=&addressId=&email=&storeId=&active=&username= -> StaffDTO[]"),
                        endpoint("Get staff by id", "GET", "/api/v1/staff/{id}", "Replace {id} with a staff id.", null, "StaffDTO", "GET /api/v1/staff/{id} -> StaffDTO"),
                        endpoint("Create staff", "POST", "/api/v1/staff", null, "StaffDTO", "StaffDTO", "POST /api/v1/staff body: StaffDTO -> StaffDTO"),
                        endpoint("Replace staff", "PUT", "/api/v1/staff/{id}", "Replace {id} with a staff id.", "StaffDTO", "StaffDTO", "PUT /api/v1/staff/{id} body: StaffDTO -> StaffDTO"),
                        endpoint("Update staff fields", "PATCH", "/api/v1/staff/{id}", "Replace {id} with a staff id.", "partial StaffDTO", "StaffDTO", "PATCH /api/v1/staff/{id} body: partial StaffDTO -> StaffDTO"),
                        endpoint("Delete staff", "DELETE", "/api/v1/staff/{id}", "Replace {id} with a staff id.", null, null, "DELETE /api/v1/staff/{id}")
                )
        ));

        entities.put("stores", new EntityDefinition(
                "stores",
                "Stores",
                "shreyash",
                "Maintain physical store records and manager assignments.",
                "StoreDTO",
                List.of("storeId", "managerStaffId", "addressId", "lastUpdate"),
                "/api/stores",
                List.of(
                        endpoint("List stores", "GET", "/api/stores", null, null, "StoreDTO[]", "GET /api/stores -> StoreDTO[]"),
                        endpoint("Get store by id", "GET", "/api/stores/{id}", "Replace {id} with a store id.", null, "StoreDTO", "GET /api/stores/{id} -> StoreDTO"),
                        endpoint("Create store", "POST", "/api/stores", null, "StoreDTO", "StoreDTO", "POST /api/stores body: StoreDTO -> StoreDTO"),
                        endpoint("Replace store", "PUT", "/api/stores/{id}", "Replace {id} with a store id.", "StoreDTO", "StoreDTO", "PUT /api/stores/{id} body: StoreDTO -> StoreDTO"),
                        endpoint("Delete store", "DELETE", "/api/stores/{id}", "Replace {id} with a store id.", null, null, "DELETE /api/stores/{id}")
                )
        ));

        return Collections.unmodifiableMap(new LinkedHashMap<>(entities));
    }

    private static EndpointDefinition endpoint(
            String label,
            String method,
            String path,
            String notes,
            String bodySchema,
            String responseSchema,
            String displayLine
    ) {
        return new EndpointDefinition(label, method, path, notes, bodySchema, responseSchema, displayLine);
    }

    public record MemberDefinition(
            String key,
            String name,
            String imagePath,
            String role,
            String accent,
            String intro,
            List<String> entityKeys
    ) {
    }

    public record EntityDefinition(
            String key,
            String displayName,
            String memberKey,
            String description,
            String schemaName,
            List<String> schemaFields,
            String collectionPath,
            List<EndpointDefinition> endpoints
    ) {
    }

    public record EndpointDefinition(
            String label,
            String method,
            String path,
            String notes,
            String bodySchema,
            String responseSchema,
            String displayLine
    ) {
    }
}
