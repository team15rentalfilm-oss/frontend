package com.iem.frontend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iem.frontend.catalog.ExplorerCatalog;
import com.iem.frontend.catalog.ExplorerCatalog.EndpointDefinition;
import com.iem.frontend.catalog.ExplorerCatalog.EntityDefinition;
import com.iem.frontend.catalog.ExplorerCatalog.MemberDefinition;
import org.springframework.ui.Model;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
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

    private static final int PREVIEW_LIMIT = 8;
    private static final Pattern PATH_VARIABLE_PATTERN = Pattern.compile("\\{([^/{}]+)}");

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    private final ObjectMapper objectMapper = new ObjectMapper();

    protected String renderEntityPage(String entityKey, int previewPage, Model model) {
        EntityDefinition entity = ExplorerCatalog.entity(entityKey);
        if (entity == null) {
            throw new ResponseStatusException(NOT_FOUND, "Unknown entity: " + entityKey);
        }

        MemberDefinition member = ExplorerCatalog.member(entity.memberKey());
        int normalizedPreviewPage = Math.max(previewPage, 0);
        PreviewData previewData = fetchPreview(entity.collectionPath(), normalizedPreviewPage);

        model.addAttribute("entity", entity);
        model.addAttribute("member", member);
        model.addAttribute("baseUrl", ExplorerCatalog.BASE_API_URL);
        model.addAttribute("openApiPath", ExplorerCatalog.OPEN_API_PATH);
        model.addAttribute("previewColumns", previewData.columns().isEmpty() ? entity.schemaFields().stream().limit(6).toList() : previewData.columns());
        model.addAttribute("previewRows", previewData.rows());
        model.addAttribute("previewError", previewData.error());
        model.addAttribute("previewHasPagination", previewData.paged());
        model.addAttribute("previewPageNumber", previewData.pageNumber() + 1);
        model.addAttribute("previewTotalPages", previewData.totalPages());
        model.addAttribute("previewTotalElements", previewData.totalElements());
        model.addAttribute("previewHasPrevious", previewData.pageNumber() > 0);
        model.addAttribute("previewHasNext", previewData.pageNumber() + 1 < previewData.totalPages());
        model.addAttribute("previewPreviousUrl", "/entities/" + entityKey + "?previewPage=" + Math.max(previewData.pageNumber() - 1, 0));
        model.addAttribute("previewNextUrl", "/entities/" + entityKey + "?previewPage=" + (previewData.pageNumber() + 1));
        model.addAttribute("endpointConfigsJson", toEndpointConfigJson(entity));
        return "entity-list";
    }

    private PreviewData fetchPreview(String collectionPath, int previewPage) {
        try {
            String previewPath = resolvePreviewPath(collectionPath, previewPage);
            HttpRequest request = HttpRequest.newBuilder(URI.create(ExplorerCatalog.BASE_API_URL + previewPath))
                    .timeout(Duration.ofSeconds(10))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                return new PreviewData(List.of(), List.of(), describePreviewFailure(response), false, 0, 1, 0);
            }

            JsonNode root = objectMapper.readTree(response.body());
            if (root == null || root.isNull()) {
                return new PreviewData(List.of(), List.of(), "Preview returned no data.", false, 0, 1, 0);
            }

            boolean paged = root.isObject() && root.has("content") && root.get("content").isArray();
            int pageNumber = paged ? root.path("number").asInt(previewPage) : 0;
            int totalPages = paged ? Math.max(root.path("totalPages").asInt(1), 1) : 1;
            long totalElements = paged ? root.path("totalElements").asLong(0) : 0;

            if (paged) {
                root = root.get("content");
            }

            List<Map<String, Object>> rows = new ArrayList<>();
            if (root.isArray()) {
                for (JsonNode node : root) {
                    rows.add(normalizeRow(node));
                    if (rows.size() == PREVIEW_LIMIT) {
                        break;
                    }
                }
            } else {
                rows.add(normalizeRow(root));
            }

            List<String> columns = choosePreviewColumns(rows);
            return new PreviewData(columns, rows, null, paged, pageNumber, totalPages, totalElements);
        } catch (IOException | InterruptedException ex) {
            if (ex instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            return new PreviewData(List.of(), List.of(), "Preview unavailable. Confirm the backend is running on " + ExplorerCatalog.BASE_API_URL + ".", false, 0, 1, 0);
        }
    }

    private String describePreviewFailure(HttpResponse<String> response) {
        String backendError = extractBackendErrorMessage(response.body());
        if (backendError != null && backendError.contains("Could not initialize proxy")) {
            return "Preview unavailable. Backend lazy-loading failed for Address -> City -> Country. Fetch or map nested data inside the transaction before returning the response.";
        }
        return "Preview unavailable. Backend returned HTTP " + response.statusCode() + ".";
    }

    private String extractBackendErrorMessage(String body) {
        if (body == null || body.isBlank()) {
            return null;
        }
        try {
            JsonNode root = objectMapper.readTree(body);
            if (root.hasNonNull("message")) {
                return root.get("message").asText();
            }
            if (root.hasNonNull("detail")) {
                return root.get("detail").asText();
            }
            if (root.hasNonNull("error")) {
                return root.get("error").asText();
            }
        } catch (JsonProcessingException ignored) {
            return body;
        }
        return body;
    }

    private String resolvePreviewPath(String collectionPath, int previewPage) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(collectionPath);
        if (collectionPath.contains("page=")) {
            builder.replaceQueryParam("page", previewPage);
        }
        if (collectionPath.contains("size=")) {
            builder.replaceQueryParam("size", PREVIEW_LIMIT);
        }
        return builder.build(true).toUriString();
    }

    private List<String> choosePreviewColumns(List<Map<String, Object>> rows) {
        if (rows.isEmpty()) {
            return List.of();
        }

        List<String> candidates = new ArrayList<>(rows.get(0).keySet());
        List<String> preferred = List.of(
                "id", "filmId", "actorId", "countryId", "cityId", "categoryId", "inventoryId",
                "paymentId", "rentalId", "staffId", "storeId", "customerId", "title", "name",
                "country", "city", "firstName", "lastName", "language", "releaseYear",
                "rating", "email", "active"
        );

        List<String> columns = new ArrayList<>();
        for (String preferredColumn : preferred) {
            if (candidates.contains(preferredColumn) && !columns.contains(preferredColumn)) {
                columns.add(preferredColumn);
            }
            if (columns.size() == 6) {
                return columns;
            }
        }

        for (String candidate : candidates) {
            if (!columns.contains(candidate)) {
                columns.add(candidate);
            }
            if (columns.size() == 6) {
                break;
            }
        }
        return columns;
    }

    private Map<String, Object> normalizeRow(JsonNode node) {
        if (node.isObject()) {
            Map<String, Object> row = objectMapper.convertValue(node, new TypeReference<LinkedHashMap<String, Object>>() {
            });
            row.replaceAll((key, value) -> simplifyValue(value));
            return row;
        }

        Map<String, Object> row = new LinkedHashMap<>();
        row.put("value", simplifyValue(objectMapper.convertValue(node, Object.class)));
        return row;
    }

    private Object simplifyValue(Object value) {
        if (value instanceof Map<?, ?> mapValue) {
            return summarizeMap(mapValue);
        }
        if (value instanceof List<?> listValue) {
            return summarizeList(listValue);
        }
        return value;
    }

    private String summarizeMap(Map<?, ?> value) {
        if (value.containsKey("title")) {
            return String.valueOf(value.get("title"));
        }
        if (value.containsKey("name")) {
            return String.valueOf(value.get("name"));
        }
        if (value.containsKey("country")) {
            return String.valueOf(value.get("country"));
        }
        if (value.containsKey("city")) {
            return String.valueOf(value.get("city"));
        }
        if (value.containsKey("firstName") && value.containsKey("lastName")) {
            return String.valueOf(value.get("firstName")) + " " + String.valueOf(value.get("lastName"));
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            return String.valueOf(value);
        }
    }

    private String summarizeList(List<?> value) {
        if (value.isEmpty()) {
            return "None";
        }

        String summary = value.stream()
                .map(this::simplifyValue)
                .map(String::valueOf)
                .limit(4)
                .reduce((left, right) -> left + ", " + right)
                .orElse("");

        if (value.size() > 4) {
            summary = summary + ", +" + (value.size() - 4) + " more";
        }
        return summary;
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

    private record PreviewData(
            List<String> columns,
            List<Map<String, Object>> rows,
            String error,
            boolean paged,
            int pageNumber,
            int totalPages,
            long totalElements
    ) {
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
