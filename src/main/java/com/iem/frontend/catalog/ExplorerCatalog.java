package com.iem.frontend.catalog;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ExplorerCatalog {

    public static final String BASE_API_URL = "http://localhost:8089";
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
                "/images/anwesha.jpeg",
                "Films and categories",
                "#a64b2a",
                "Owns the catalog-facing resources for films and category organization.",
                List.of("films", "categories")
        ));

        members.put("harshita", new MemberDefinition(
                "harshita",
                "Harshita Dujari",
                "/images/harshita.jpeg",
                "Actors, film relationships, and languages",
                "#0e5f76",
                "Maintains performer, language, and film-actor relationship records used throughout the catalog.",
                List.of("actors", "film-actor", "languages")
        ));

        members.put("mafuj", new MemberDefinition(
                "mafuj",
                "Mafuj Gazi",
                "/images/mafuj.jpeg",
                "Location reference data",
                "#0b6e4f",
                "Maintains the address, city, and country reference data used by customer and store workflows.",
                List.of("addresses", "cities", "countries")
        ));

        members.put("shreyash", new MemberDefinition(
                "shreyash",
                "Shreyash Singh",
                "/images/shreyash.jpeg",
                "Customers, stores, and inventory",
                "#7a3e00",
                "Owns customer records together with store operations and inventory availability across locations.",
                List.of("customers", "stores", "inventory")
        ));

        members.put("utsav", new MemberDefinition(
                "utsav",
                "Utsav Sarda",
                "/images/utsav.JPG",
                "Staff, payments, and rentals",
                "#7a1f3d",
                "Maintains operational workflows for staff activity, payments, and rental lifecycle endpoints.",
                List.of("staff", "payments", "rentals")
        ));

        return Collections.unmodifiableMap(new LinkedHashMap<>(members));
    }

    private static Map<String, EntityDefinition> buildEntities() {
        Map<String, EntityDefinition> entities = new LinkedHashMap<>();

        entities.put("actors", entity(
                "actors",
                "Actors",
                "harshita",
                "Browse, search, create, and replace actor records.",
                "ActorRequestDTO",
                List.of("firstName", "lastName"),
                "/api/actors",
                List.of(
                        endpoint("Create actor", "POST", "/api/actors", null, "ActorRequestDTO", "ActorDTO", "POST /api/actors body: ActorRequestDTO -> ActorDTO"),
                        endpoint("List actors", "GET", "/api/actors", null, null, "ActorDTO[]", "GET /api/actors -> ActorDTO[]"),
                        endpoint("Get actor by id", "GET", "/api/actors/{id}", "Replace {id} with an actor id.", null, "ActorDTO", "GET /api/actors/{id} -> ActorDTO"),
                        endpoint("Search actors by name", "GET", "/api/actors/search?name=", "Provide a full or partial name.", null, "ActorDTO[]", "GET /api/actors/search?name=... -> ActorDTO[]"),
                        endpoint("Replace actor", "PUT", "/api/actors/{id}", "Replace {id} with an actor id.", "ActorRequestDTO", "ActorDTO", "PUT /api/actors/{id} body: ActorRequestDTO -> ActorDTO")
                )
        ));

        entities.put("film-actor", entity(
                "film-actor",
                "FilmActor",
                "harshita",
                "Explore film-to-actor relationship endpoints from either actor name or film title.",
                "FilmActorQueryDTO",
                List.of("name", "title"),
                "/api/film-actor/films-by-actor?name=PENELOPE",
                List.of(
                        endpoint("Get films by actor", "GET", "/api/film-actor/films-by-actor?name=", "Provide an actor name.", null, "FilmResponseDTO[]", "GET /api/film-actor/films-by-actor?name=... -> FilmResponseDTO[]"),
                        endpoint("Get actors by film", "GET", "/api/film-actor/actors-by-film?title=", "Provide a film title.", null, "ActorResponseDTO[]", "GET /api/film-actor/actors-by-film?title=... -> ActorResponseDTO[]")
                )
        ));

        entities.put("addresses", entity(
                "addresses",
                "Addresses",
                "mafuj",
                "Manage address records and address-oriented search endpoints.",
                "AddressRequestDTO",
                List.of("address", "address2", "district", "postalCode", "phone", "city", "country"),
                "/api/addresses",
                List.of(
                        endpoint("Create address", "POST", "/api/addresses", null, "AddressRequestDTO", "AddressDTO", "POST /api/addresses body: AddressRequestDTO -> AddressDTO"),
                        endpoint("List addresses", "GET", "/api/addresses", null, null, "AddressDTO[]", "GET /api/addresses -> AddressDTO[]"),
                        endpoint("Get address by id", "GET", "/api/addresses/{id}", "Replace {id} with an address id.", null, "AddressDTO", "GET /api/addresses/{id} -> AddressDTO"),
                        endpoint("Replace address", "PUT", "/api/addresses/{id}", "Replace {id} with an address id.", "AddressRequestDTO", "AddressDTO", "PUT /api/addresses/{id} body: AddressRequestDTO -> AddressDTO"),
                        endpoint("Patch address", "PATCH", "/api/addresses/{id}", "Replace {id} with an address id.", "AddressRequestDTO", "AddressDTO", "PATCH /api/addresses/{id} body: AddressRequestDTO -> AddressDTO"),
                        endpoint("Search addresses by country", "GET", "/api/addresses/country?name=", "Provide a country name.", null, "AddressDTO[]", "GET /api/addresses/country?name=... -> AddressDTO[]"),
                        endpoint("Search addresses by address line", "GET", "/api/addresses/search/address?value=", "Provide an address value.", null, "AddressDTO[]", "GET /api/addresses/search/address?value=... -> AddressDTO[]"),
                        endpoint("Search addresses by district", "GET", "/api/addresses/search/district?value=", "Provide a district value.", null, "AddressDTO[]", "GET /api/addresses/search/district?value=... -> AddressDTO[]"),
                        endpoint("Search addresses by city", "GET", "/api/addresses/search/city?value=", "Provide a city value.", null, "AddressDTO[]", "GET /api/addresses/search/city?value=... -> AddressDTO[]")
                )
        ));

        entities.put("categories", entity(
                "categories",
                "Categories",
                "anwesha",
                "Manage category values used to classify films.",
                "CategoryRequestDTO",
                List.of("name"),
                "/api/categories",
                List.of(
                        endpoint("Create category", "POST", "/api/categories", null, "CategoryRequestDTO", "CategoryDTO", "POST /api/categories body: CategoryRequestDTO -> CategoryDTO"),
                        endpoint("List categories", "GET", "/api/categories", null, null, "CategoryDTO[]", "GET /api/categories -> CategoryDTO[]"),
                        endpoint("Get category by id", "GET", "/api/categories/{id}", "Replace {id} with a category id.", null, "CategoryDTO", "GET /api/categories/{id} -> CategoryDTO"),
                        endpoint("Replace category", "PUT", "/api/categories/{id}", "Replace {id} with a category id.", "CategoryRequestDTO", "CategoryDTO", "PUT /api/categories/{id} body: CategoryRequestDTO -> CategoryDTO")
                )
        ));

        entities.put("cities", entity(
                "cities",
                "Cities",
                "mafuj",
                "Maintain city reference data and city search endpoints.",
                "CityRequestDTO",
                List.of("city", "country"),
                "/api/cities",
                List.of(
                        endpoint("Create city", "POST", "/api/cities", null, "CityRequestDTO", "CityDTO", "POST /api/cities body: CityRequestDTO -> CityDTO"),
                        endpoint("List cities", "GET", "/api/cities", null, null, "CityDTO[]", "GET /api/cities -> CityDTO[]"),
                        endpoint("Get city by id", "GET", "/api/cities/{id}", "Replace {id} with a city id.", null, "CityDTO", "GET /api/cities/{id} -> CityDTO"),
                        endpoint("Replace city", "PUT", "/api/cities/{id}", "Replace {id} with a city id.", "CityRequestDTO", "CityDTO", "PUT /api/cities/{id} body: CityRequestDTO -> CityDTO"),
                        endpoint("Patch city", "PATCH", "/api/cities/{id}", "Replace {id} with a city id.", "CityPatchDTO", "CityDTO", "PATCH /api/cities/{id} body: CityPatchDTO -> CityDTO"),
                        endpoint("Search cities by city", "GET", "/api/cities/search/city?city=", "Provide a city name.", null, "CityDTO[]", "GET /api/cities/search/city?city=... -> CityDTO[]"),
                        endpoint("Search cities by country", "GET", "/api/cities/search/country?country=", "Provide a country name.", null, "CityDTO[]", "GET /api/cities/search/country?country=... -> CityDTO[]")
                )
        ));

        entities.put("countries", entity(
                "countries",
                "Countries",
                "mafuj",
                "Maintain country reference data and country search.",
                "CountryRequestDTO",
                List.of("country"),
                "/api/countries",
                List.of(
                        endpoint("Create country", "POST", "/api/countries", null, "CountryRequestDTO", "CountryDTO", "POST /api/countries body: CountryRequestDTO -> CountryDTO"),
                        endpoint("List countries", "GET", "/api/countries", null, null, "CountryDTO[]", "GET /api/countries -> CountryDTO[]"),
                        endpoint("Get country by id", "GET", "/api/countries/{id}", "Replace {id} with a country id.", null, "CountryDTO", "GET /api/countries/{id} -> CountryDTO"),
                        endpoint("Search countries", "GET", "/api/countries/search?name=", "Provide a country name.", null, "CountryDTO[]", "GET /api/countries/search?name=... -> CountryDTO[]"),
                        endpoint("Replace country", "PUT", "/api/countries/{id}", "Replace {id} with a country id.", "CountryRequestDTO", "CountryDTO", "PUT /api/countries/{id} body: CountryRequestDTO -> CountryDTO")
                )
        ));

        entities.put("customers", entity(
                "customers",
                "Customers",
                "shreyash",
                "View, search, create, and update customer records.",
                "CustomerRequestDTO",
                List.of("firstName", "lastName", "email", "storeId", "active", "address", "address2", "district", "postalCode", "phone", "city", "country"),
                "/api/customers",
                List.of(
                        endpoint("Create customer", "POST", "/api/customers", null, "CustomerRequestDTO", "CustomerDTO", "POST /api/customers body: CustomerRequestDTO -> CustomerDTO"),
                        endpoint("List customers", "GET", "/api/customers", null, null, "CustomerDTO[]", "GET /api/customers -> CustomerDTO[]"),
                        endpoint("Get customer by id", "GET", "/api/customers/{id}", "Replace {id} with a customer id.", null, "CustomerDTO", "GET /api/customers/{id} -> CustomerDTO"),
                        endpoint("Replace customer", "PUT", "/api/customers/{id}", "Replace {id} with a customer id.", "CustomerRequestDTO", "CustomerDTO", "PUT /api/customers/{id} body: CustomerRequestDTO -> CustomerDTO"),
                        endpoint("Patch customer", "PATCH", "/api/customers/{id}", "Replace {id} with a customer id.", "CustomerPatchDTO", "CustomerDTO", "PATCH /api/customers/{id} body: CustomerPatchDTO -> CustomerDTO"),
                        endpoint("Search customers by first name", "GET", "/api/customers/search/first-name?firstName=", "Provide a first name.", null, "CustomerDTO[]", "GET /api/customers/search/first-name?firstName=... -> CustomerDTO[]"),
                        endpoint("Search customers by last name", "GET", "/api/customers/search/last-name?lastName=", "Provide a last name.", null, "CustomerDTO[]", "GET /api/customers/search/last-name?lastName=... -> CustomerDTO[]"),
                        endpoint("Search customers by email", "GET", "/api/customers/search/email?email=", "Provide an email address.", null, "CustomerDTO[]", "GET /api/customers/search/email?email=... -> CustomerDTO[]"),
                        endpoint("Search customers by active status", "GET", "/api/customers/search/active?active=", "Provide true or false.", null, "CustomerDTO[]", "GET /api/customers/search/active?active=... -> CustomerDTO[]"),
                        endpoint("Search customers by city", "GET", "/api/customers/search/city?city=", "Provide a city name.", null, "CustomerDTO[]", "GET /api/customers/search/city?city=... -> CustomerDTO[]"),
                        endpoint("Search customers by country", "GET", "/api/customers/search/country?country=", "Provide a country name.", null, "CustomerDTO[]", "GET /api/customers/search/country?country=... -> CustomerDTO[]")
                )
        ));

        entities.put("films", entity(
                "films",
                "Films",
                "anwesha",
                "Explore the film catalog, pagination, and search filters.",
                "FilmRequestDTO",
                List.of("title", "description", "releaseYear", "language", "categories", "actors", "rentalDuration", "rentalRate", "length", "replacementCost", "rating", "specialFeatures"),
                "/api/films?page=0&size=100",
                List.of(
                        endpoint("Create film", "POST", "/api/films", null, "FilmRequestDTO", "FilmDTO", "POST /api/films body: FilmRequestDTO -> FilmDTO"),
                        endpoint("List films", "GET", "/api/films?page=&size=", "Use page and size for pagination.", null, "Page<FilmDTO>", "GET /api/films?page=&size= -> Page<FilmDTO>"),
                        endpoint("Get film by id", "GET", "/api/films/{id}", "Replace {id} with a film id.", null, "FilmDTO", "GET /api/films/{id} -> FilmDTO"),
                        endpoint("Replace film", "PUT", "/api/films/{id}", "Replace {id} with a film id.", "FilmRequestDTO", "FilmDTO", "PUT /api/films/{id} body: FilmRequestDTO -> FilmDTO"),
                        endpoint("Patch film", "PATCH", "/api/films/{id}", "Replace {id} with a film id.", "FilmPatchDTO", "FilmDTO", "PATCH /api/films/{id} body: FilmPatchDTO -> FilmDTO"),
                        endpoint("Search films", "GET", "/api/films/search?title=&year=&category=&actor=&page=&size=", "Use any combination of title, year, category, actor, page, and size.", null, "Page<FilmDTO>", "GET /api/films/search?title=&year=&category=&actor=&page=&size= -> Page<FilmDTO>")
                )
        ));

        entities.put("inventory", entity(
                "inventory",
                "Inventory",
                "shreyash",
                "View inventory availability and inventory detail endpoints.",
                "InventoryRequestDTO",
                List.of("filmId", "storeId"),
                "/api/inventory?page=0&size=100",
                List.of(
                        endpoint("Create inventory item", "POST", "/api/inventory", null, "InventoryRequestDTO", "InventoryDTO", "POST /api/inventory body: InventoryRequestDTO -> InventoryDTO"),
                        endpoint("List inventory", "GET", "/api/inventory?filmId=&storeId=&page=&size=&sort=", "Use optional filmId, storeId, page, size, and sort filters.", null, "Page<InventoryDTO>", "GET /api/inventory?filmId=&storeId=&page=&size=&sort= -> Page<InventoryDTO>"),
                        endpoint("Get inventory by id", "GET", "/api/inventory/{id}", "Replace {id} with an inventory id.", null, "InventoryDTO", "GET /api/inventory/{id} -> InventoryDTO"),
                        endpoint("Get inventory details", "GET", "/api/inventory/{id}/details", "Replace {id} with an inventory id.", null, "InventoryDetailsDTO", "GET /api/inventory/{id}/details -> InventoryDetailsDTO"),
                        endpoint("Replace inventory", "PUT", "/api/inventory/{id}", "Replace {id} with an inventory id.", "InventoryRequestDTO", "InventoryDTO", "PUT /api/inventory/{id} body: InventoryRequestDTO -> InventoryDTO"),
                        endpoint("Patch inventory", "PATCH", "/api/inventory/{id}", "Replace {id} with an inventory id.", "InventoryRequestDTO", "InventoryDTO", "PATCH /api/inventory/{id} body: InventoryRequestDTO -> InventoryDTO"),
                        endpoint("List inventory by film", "GET", "/api/inventory/film/{filmId}?page=&size=&sort=", "Replace {filmId} and use page, size, or sort as needed.", null, "Page<InventoryDTO>", "GET /api/inventory/film/{filmId}?page=&size=&sort= -> Page<InventoryDTO>"),
                        endpoint("List inventory by store", "GET", "/api/inventory/store/{storeId}?page=&size=&sort=", "Replace {storeId} and use page, size, or sort as needed.", null, "Page<InventoryDTO>", "GET /api/inventory/store/{storeId}?page=&size=&sort= -> Page<InventoryDTO>")
                )
        ));

        entities.put("languages", entity(
                "languages",
                "Languages",
                "harshita",
                "Look up, search, create, and replace language entries.",
                "LanguageRequestDTO",
                List.of("name"),
                "/api/languages",
                List.of(
                        endpoint("Create language", "POST", "/api/languages", null, "LanguageRequestDTO", "LanguageDTO", "POST /api/languages body: LanguageRequestDTO -> LanguageDTO"),
                        endpoint("List languages", "GET", "/api/languages", null, null, "LanguageDTO[]", "GET /api/languages -> LanguageDTO[]"),
                        endpoint("Get language by id", "GET", "/api/languages/{id}", "Replace {id} with a language id.", null, "LanguageDTO", "GET /api/languages/{id} -> LanguageDTO"),
                        endpoint("Search languages", "GET", "/api/languages/search?name=", "Provide a language name or prefix.", null, "LanguageDTO[]", "GET /api/languages/search?name=... -> LanguageDTO[]"),
                        endpoint("Replace language", "PUT", "/api/languages/{id}", "Replace {id} with a language id.", "LanguageRequestDTO", "LanguageDTO", "PUT /api/languages/{id} body: LanguageRequestDTO -> LanguageDTO")
                )
        ));

        entities.put("payments", entity(
                "payments",
                "Payments",
                "utsav",
                "Inspect payment records across list, detail, search, and relation-specific endpoints.",
                "PaymentRequestDTO",
                List.of("customerId", "staffId", "rentalId", "amount", "paymentDate"),
                "/api/payments?page=0&size=100",
                List.of(
                        endpoint("Create payment", "POST", "/api/payments", null, "PaymentRequestDTO", "PaymentDTO", "POST /api/payments body: PaymentRequestDTO -> PaymentDTO"),
                        endpoint("List payments", "GET", "/api/payments?page=&size=&sort=", "Use page, size, and sort for pagination.", null, "Page<PaymentDTO>", "GET /api/payments?page=&size=&sort= -> Page<PaymentDTO>"),
                        endpoint("Get payment by id", "GET", "/api/payments/{id}", "Replace {id} with a payment id.", null, "PaymentDTO", "GET /api/payments/{id} -> PaymentDTO"),
                        endpoint("Patch payment", "PATCH", "/api/payments/{id}", "Replace {id} with a payment id.", "PaymentPatchDTO", "PaymentDTO", "PATCH /api/payments/{id} body: PaymentPatchDTO -> PaymentDTO"),
                        endpoint("Search payments by customer name", "GET", "/api/payments/search/customer?name=", "Provide a customer name.", null, "PaymentDTO[]", "GET /api/payments/search/customer?name=... -> PaymentDTO[]"),
                        endpoint("Get payments by rental", "GET", "/api/payments/rental/{rentalId}", "Replace {rentalId} with a rental id.", null, "PaymentDTO[]", "GET /api/payments/rental/{rentalId} -> PaymentDTO[]"),
                        endpoint("Get payments by customer", "GET", "/api/payments/customer/{customerId}?page=&size=&sort=", "Replace {customerId} and use page, size, or sort as needed.", null, "Page<PaymentDTO>", "GET /api/payments/customer/{customerId}?page=&size=&sort= -> Page<PaymentDTO>"),
                        endpoint("Get payments by staff", "GET", "/api/payments/staff/{staffId}?page=&size=&sort=", "Replace {staffId} and use page, size, or sort as needed.", null, "Page<PaymentDTO>", "GET /api/payments/staff/{staffId}?page=&size=&sort= -> Page<PaymentDTO>"),
                        endpoint("Search payments by staff name", "GET", "/api/payments/search/staff?name=&page=&size=&sort=", "Use staff name with optional page, size, and sort.", null, "Page<PaymentDTO>", "GET /api/payments/search/staff?name=&page=&size=&sort= -> Page<PaymentDTO>")
                )
        ));

        entities.put("rentals", entity(
                "rentals",
                "Rentals",
                "utsav",
                "Manage rental lifecycle endpoints including return operations.",
                "RentalRequestDTO",
                List.of("rentalDate", "inventoryId", "customerId", "staffId", "returnDate"),
                "/api/rentals?page=0&size=100",
                List.of(
                        endpoint("Create rental", "POST", "/api/rentals", null, "RentalRequestDTO", "RentalDTO", "POST /api/rentals body: RentalRequestDTO -> RentalDTO"),
                        endpoint("Mark rental returned", "POST", "/api/rentals/{id}/return", "Replace {id} with a rental id.", null, "RentalDTO", "POST /api/rentals/{id}/return -> RentalDTO"),
                        endpoint("List rentals", "GET", "/api/rentals?page=&size=&sort=", "Use page, size, and sort for pagination.", null, "Page<RentalDTO>", "GET /api/rentals?page=&size=&sort= -> Page<RentalDTO>"),
                        endpoint("Get rental by id", "GET", "/api/rentals/{id}", "Replace {id} with a rental id.", null, "RentalDTO", "GET /api/rentals/{id} -> RentalDTO"),
                        endpoint("Search rentals by customer name", "GET", "/api/rentals/search/customer?name=", "Provide a customer name.", null, "RentalDTO[]", "GET /api/rentals/search/customer?name=... -> RentalDTO[]"),
                        endpoint("Get rentals by customer", "GET", "/api/rentals/customer/{customerId}?page=&size=&sort=", "Replace {customerId} and use page, size, or sort as needed.", null, "Page<RentalDTO>", "GET /api/rentals/customer/{customerId}?page=&size=&sort= -> Page<RentalDTO>"),
                        endpoint("Get rentals by inventory", "GET", "/api/rentals/inventory/{inventoryId}?page=&size=&sort=", "Replace {inventoryId} and use page, size, or sort as needed.", null, "Page<RentalDTO>", "GET /api/rentals/inventory/{inventoryId}?page=&size=&sort= -> Page<RentalDTO>"),
                        endpoint("Get rentals by staff", "GET", "/api/rentals/staff/{staffId}?page=&size=&sort=", "Replace {staffId} and use page, size, or sort as needed.", null, "Page<RentalDTO>", "GET /api/rentals/staff/{staffId}?page=&size=&sort= -> Page<RentalDTO>")
                )
        ));

        entities.put("staff", entity(
                "staff",
                "Staff",
                "utsav",
                "Manage staff records with pagination, search, and location filters.",
                "StaffRequestDTO",
                List.of("firstName", "lastName", "addressId", "email", "storeId", "active", "username", "password", "picture"),
                "/api/staff?page=0&size=100",
                List.of(
                        endpoint("Create staff", "POST", "/api/staff", null, "StaffRequestDTO", "StaffDTO", "POST /api/staff body: StaffRequestDTO -> StaffDTO"),
                        endpoint("List staff", "GET", "/api/staff?page=&size=&sort=", "Use page, size, and sort for pagination.", null, "Page<StaffDTO>", "GET /api/staff?page=&size=&sort= -> Page<StaffDTO>"),
                        endpoint("Get staff by id", "GET", "/api/staff/{id}", "Replace {id} with a staff id.", null, "StaffDTO", "GET /api/staff/{id} -> StaffDTO"),
                        endpoint("Search staff", "GET", "/api/staff/search?name=&storeId=&page=&size=&sort=", "Use name, storeId, page, size, and sort as needed.", null, "Page<StaffDTO>", "GET /api/staff/search?name=&storeId=&page=&size=&sort= -> Page<StaffDTO>"),
                        endpoint("Filter staff by city", "GET", "/api/staff/city?city=&page=&size=&sort=", "Use city with optional page, size, and sort.", null, "Page<StaffDTO>", "GET /api/staff/city?city=&page=&size=&sort= -> Page<StaffDTO>"),
                        endpoint("Filter staff by country", "GET", "/api/staff/country?country=&page=&size=&sort=", "Use country with optional page, size, and sort.", null, "Page<StaffDTO>", "GET /api/staff/country?country=&page=&size=&sort= -> Page<StaffDTO>"),
                        endpoint("Replace staff", "PUT", "/api/staff/{id}", "Replace {id} with a staff id.", "StaffRequestDTO", "StaffDTO", "PUT /api/staff/{id} body: StaffRequestDTO -> StaffDTO"),
                        endpoint("Patch staff", "PATCH", "/api/staff/{id}", "Replace {id} with a staff id.", "StaffPatchDTO", "StaffDTO", "PATCH /api/staff/{id} body: StaffPatchDTO -> StaffDTO")
                ),
                List.of("staffId", "firstName", "lastName", "email")
        ));

        entities.put("stores", entity(
                "stores",
                "Stores",
                "shreyash",
                "Maintain store records and manager assignments.",
                "StoreRequestDTO",
                List.of("managerStaffId", "address", "address2", "district", "postalCode", "phone", "city", "country"),
                "/api/stores",
                List.of(
                        endpoint("Create store", "POST", "/api/stores", null, "StoreRequestDTO", "StoreDTO", "POST /api/stores body: StoreRequestDTO -> StoreDTO"),
                        endpoint("List stores", "GET", "/api/stores", null, null, "StoreDTO[]", "GET /api/stores -> StoreDTO[]"),
                        endpoint("Get store by id", "GET", "/api/stores/{id}", "Replace {id} with a store id.", null, "StoreDTO", "GET /api/stores/{id} -> StoreDTO"),
                        endpoint("Replace store", "PUT", "/api/stores/{id}", "Replace {id} with a store id.", "StoreRequestDTO", "StoreDTO", "PUT /api/stores/{id} body: StoreRequestDTO -> StoreDTO")
                )
        ));

        return Collections.unmodifiableMap(new LinkedHashMap<>(entities));
    }

    private static EntityDefinition entity(
            String key,
            String displayName,
            String memberKey,
            String description,
            String schemaName,
            List<String> schemaFields,
            String collectionPath,
            List<EndpointDefinition> endpoints
    ) {
        return new EntityDefinition(key, displayName, memberKey, description, schemaName, schemaFields, collectionPath, endpoints, List.of());
    }

    private static EntityDefinition entity(
            String key,
            String displayName,
            String memberKey,
            String description,
            String schemaName,
            List<String> schemaFields,
            String collectionPath,
            List<EndpointDefinition> endpoints,
            List<String> allowedSortFields
    ) {
        return new EntityDefinition(key, displayName, memberKey, description, schemaName, schemaFields, collectionPath, endpoints, allowedSortFields);
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
            List<EndpointDefinition> endpoints,
            List<String> allowedSortFields
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
