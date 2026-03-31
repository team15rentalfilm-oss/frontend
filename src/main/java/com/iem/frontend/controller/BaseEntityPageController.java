package com.iem.frontend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iem.frontend.catalog.ExplorerCatalog;
import com.iem.frontend.catalog.ExplorerCatalog.EndpointDefinition;
import com.iem.frontend.catalog.ExplorerCatalog.EntityDefinition;
import com.iem.frontend.catalog.ExplorerCatalog.MemberDefinition;
import org.springframework.ui.Model;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.http.HttpStatus.NOT_FOUND;

abstract class BaseEntityPageController {

    private static final Pattern PATH_VARIABLE_PATTERN = Pattern.compile("\\{([^/{}]+)}");

    private final ObjectMapper objectMapper = new ObjectMapper();

    protected String renderEntityPage(String entityKey, Model model) {
        EntityDefinition entity = ExplorerCatalog.entity(entityKey);
        if (entity == null) {
            throw new ResponseStatusException(NOT_FOUND, "Unknown entity: " + entityKey);
        }

        MemberDefinition member = ExplorerCatalog.member(entity.memberKey());

        model.addAttribute("entity", entity);
        model.addAttribute("member", member);
        model.addAttribute("baseUrl", ExplorerCatalog.BASE_API_URL);
        model.addAttribute("openApiPath", ExplorerCatalog.OPEN_API_PATH);
        model.addAttribute("endpointConfigsJson", toEndpointConfigJson(entity));
        return "entity-list";
    }

    private String toEndpointConfigJson(EntityDefinition entity) {
        List<Map<String, Object>> endpointConfigs = entity.endpoints().stream()
                .map(endpoint -> {
                    Map<String, Object> config = new LinkedHashMap<>();
                    config.put("label", endpoint.label());
                    config.put("method", endpoint.method());
                    config.put("path", endpoint.path());
                    config.put("notes", endpoint.notes());
                    config.put("bodySchema", endpoint.bodySchema());
                    config.put("responseSchema", endpoint.responseSchema());
                    config.put("displayLine", endpoint.displayLine());
                    config.put("pathFields", buildPathFields(endpoint));
                    config.put("queryFields", buildQueryFields(endpoint));
                    config.put("bodyFields", buildBodyFields(entity, endpoint));
                    return config;
                })
                .toList();

        try {
            return objectMapper.writeValueAsString(endpointConfigs);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Could not serialize endpoint configuration.", ex);
        }
    }

    private List<FieldConfig> buildPathFields(EndpointDefinition endpoint) {
        List<FieldConfig> fields = new ArrayList<>();
        Matcher matcher = PATH_VARIABLE_PATTERN.matcher(endpoint.path());
        while (matcher.find()) {
            String field = matcher.group(1);
            fields.add(fieldConfig(field, inferFieldType(field), false));
        }
        return fields;
    }

    private List<FieldConfig> buildQueryFields(EndpointDefinition endpoint) {
        int index = endpoint.path().indexOf('?');
        if (index < 0 || index == endpoint.path().length() - 1) {
            return List.of();
        }

        String query = endpoint.path().substring(index + 1);
        return Arrays.stream(query.split("&"))
                .map(part -> part.contains("=") ? part.substring(0, part.indexOf('=')) : part)
                .filter(part -> !part.isBlank())
                .map(field -> fieldConfig(field, inferFieldType(field), true))
                .toList();
    }

    private List<FieldConfig> buildBodyFields(EntityDefinition entity, EndpointDefinition endpoint) {
        if (endpoint.bodySchema() == null) {
            return List.of();
        }

        return switch (entity.key() + ":" + endpoint.method()) {
            case "addresses:POST", "addresses:PUT" -> addressFields(false);
            case "addresses:PATCH" -> addressFields(true);
            case "cities:POST", "cities:PUT" -> cityFields(false);
            case "cities:PATCH" -> cityFields(true);
            case "customers:POST", "customers:PUT" -> customerFields(false);
            case "customers:PATCH" -> customerFields(true);
            case "films:POST", "films:PUT" -> filmFields(false);
            case "films:PATCH" -> filmFields(true);
            case "staff:POST", "staff:PUT" -> staffFields(false);
            case "staff:PATCH" -> staffFields(true);
            case "stores:POST", "stores:PUT" -> storeFields(false);
            case "inventory:PATCH" -> List.of(
                    fieldConfig("filmId", "number", true),
                    fieldConfig("storeId", "number", true)
            );
            default -> defaultBodyFields(entity, endpoint);
        };
    }

    private List<FieldConfig> defaultBodyFields(EntityDefinition entity, EndpointDefinition endpoint) {
        boolean partialBody = endpoint.method().equals("PATCH")
                || endpoint.bodySchema().toLowerCase(Locale.ROOT).contains("patch");

        List<String> fields = partialBody
                ? entity.schemaFields().stream()
                .filter(field -> !field.equals("id") && !field.equals("createDate") && !field.equals("lastUpdate"))
                .toList()
                : entity.schemaFields();

        return fields.stream()
                .map(field -> fieldConfig(field, inferFieldType(field), partialBody))
                .toList();
    }

    private List<FieldConfig> addressFields(boolean partial) {
        return List.of(
                fieldConfig("address", "text", partial),
                fieldConfig("address2", "text", true),
                fieldConfig("district", "text", partial),
                fieldConfig("postalCode", "text", partial),
                fieldConfig("phone", "text", partial),
                fieldConfig("city", "text", partial),
                fieldConfig("country", "text", partial)
        );
    }

    private List<FieldConfig> cityFields(boolean partial) {
        return List.of(
                fieldConfig("city", "text", partial),
                fieldConfig("country", "text", partial)
        );
    }

    private List<FieldConfig> customerFields(boolean partial) {
        return List.of(
                fieldConfig("firstName", "text", partial),
                fieldConfig("lastName", "text", partial),
                fieldConfig("email", "email", true),
                fieldConfig("storeId", "number", partial),
                fieldConfig("active", "boolean", true),
                fieldConfig("address", "text", partial),
                fieldConfig("address2", "text", true),
                fieldConfig("district", "text", partial),
                fieldConfig("postalCode", "text", partial),
                fieldConfig("phone", "text", partial),
                fieldConfig("city", "text", partial),
                fieldConfig("country", "text", partial)
        );
    }

    private List<FieldConfig> filmFields(boolean partial) {
        return List.of(
                fieldConfig("title", "text", partial),
                fieldConfig("description", "text", true),
                fieldConfig("releaseYear", "number", true),
                fieldConfig("language", "text", partial),
                fieldConfig("categories", "list", partial),
                fieldConfig("actors", "list", partial),
                fieldConfig("rentalDuration", "number", true),
                fieldConfig("rentalRate", "decimal", true),
                fieldConfig("length", "number", true),
                fieldConfig("replacementCost", "decimal", true),
                fieldConfig("rating", "text", true),
                fieldConfig("specialFeatures", "list", true)
        );
    }

    private List<FieldConfig> staffFields(boolean partial) {
        return List.of(
                fieldConfig("firstName", "text", partial),
                fieldConfig("lastName", "text", partial),
                fieldConfig("email", "email", true),
                fieldConfig("storeId", "number", partial),
                fieldConfig("active", "boolean", true),
                fieldConfig("username", "text", partial),
                fieldConfig("password", "text", partial),
                fieldConfig("picture", "text", true),
                fieldConfig("address", "text", partial),
                fieldConfig("address2", "text", true),
                fieldConfig("district", "text", partial),
                fieldConfig("postalCode", "text", partial),
                fieldConfig("phone", "text", partial),
                fieldConfig("city", "text", partial),
                fieldConfig("country", "text", partial)
        );
    }

    private List<FieldConfig> storeFields(boolean partial) {
        return List.of(
                fieldConfig("managerStaffId", "number", partial),
                fieldConfig("address", "text", partial),
                fieldConfig("address2", "text", true),
                fieldConfig("district", "text", partial),
                fieldConfig("postalCode", "text", partial),
                fieldConfig("phone", "text", partial),
                fieldConfig("city", "text", partial),
                fieldConfig("country", "text", partial)
        );
    }

    private FieldConfig fieldConfig(String field, String type, boolean optional) {
        return new FieldConfig(field, labelize(field), type, optional, placeholderFor(field, type));
    }

    private String inferFieldType(String field) {
        String normalized = field.toLowerCase(Locale.ROOT);
        if (normalized.endsWith("ids") || normalized.contains("features")) {
            return "list";
        }
        if (normalized.endsWith("id") || normalized.contains("year") || normalized.contains("duration") || normalized.contains("length")) {
            return "number";
        }
        if (normalized.contains("amount") || normalized.contains("rate") || normalized.contains("cost")) {
            return "decimal";
        }
        if (normalized.contains("active")) {
            return "boolean";
        }
        if (normalized.contains("date") || normalized.contains("update")) {
            return "datetime";
        }
        if (normalized.contains("email")) {
            return "email";
        }
        return "text";
    }

    private String labelize(String field) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < field.length(); i++) {
            char current = field.charAt(i);
            if (i == 0) {
                builder.append(Character.toUpperCase(current));
                continue;
            }
            if (Character.isUpperCase(current)) {
                builder.append(' ');
            }
            builder.append(current);
        }
        return builder.toString();
    }

    private String placeholderFor(String field, String type) {
        return switch (type) {
            case "decimal" -> "0.00";
            case "datetime" -> "2026-03-28T00:00:00";
            case "email" -> "name@example.com";
            case "list" -> "value1, value2";
            default -> "Enter " + field;
        };
    }

    private record FieldConfig(
            String name,
            String label,
            String type,
            boolean optional,
            String placeholder
    ) {
    }
}
