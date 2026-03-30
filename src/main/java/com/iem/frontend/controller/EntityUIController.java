package com.iem.frontend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iem.frontend.catalog.ExplorerCatalog;
import com.iem.frontend.catalog.ExplorerCatalog.EndpointDefinition;
import com.iem.frontend.catalog.ExplorerCatalog.EntityDefinition;
import com.iem.frontend.catalog.ExplorerCatalog.MemberDefinition;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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

import static org.springframework.http.HttpStatus.BAD_GATEWAY;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Controller
public class EntityUIController {

    private static final int PREVIEW_LIMIT = 8;
    private static final Pattern PATH_VARIABLE_PATTERN = Pattern.compile("\\{([^/{}]+)}");

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/entities/{entityKey}")
    public String viewEntity(@PathVariable String entityKey, Model model) {
        EntityDefinition entity = ExplorerCatalog.entity(entityKey);
        if (entity == null) {
            throw new ResponseStatusException(NOT_FOUND, "Unknown entity: " + entityKey);
        }

        MemberDefinition member = ExplorerCatalog.member(entity.memberKey());
        PreviewData previewData = fetchPreview(entity.collectionPath());

        model.addAttribute("entity", entity);
        model.addAttribute("member", member);
        model.addAttribute("baseUrl", ExplorerCatalog.BASE_API_URL);
        model.addAttribute("openApiPath", ExplorerCatalog.OPEN_API_PATH);
        model.addAttribute("previewColumns", previewData.columns().isEmpty() ? entity.schemaFields() : previewData.columns());
        model.addAttribute("previewRows", previewData.rows());
        model.addAttribute("previewError", previewData.error());
        model.addAttribute("endpointConfigsJson", toEndpointConfigJson(entity));
        return "entity-list";
    }

    @GetMapping({
            "/actors",
            "/addresses",
            "/categories",
            "/cities",
            "/countries",
            "/customers",
            "/films",
            "/inventory",
            "/inventories",
            "/languages",
            "/payments",
            "/rentals",
            "/staff",
            "/stores"
    })
    public String legacyPluralRoute(HttpServletRequest request) {
        return redirectLegacyPath(request);
    }

    @PostMapping(value = "/ui/api-explorer/execute", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<ApiExplorerResponse> execute(@RequestBody ApiExplorerRequest request) {
        String method = request.method() == null ? "" : request.method().trim().toUpperCase(Locale.ROOT);
        String path = request.path() == null ? "" : request.path().trim();

        if (method.isEmpty() || path.isEmpty()) {
            return ResponseEntity.ok()
                    .body(ApiExplorerResponse.error(BAD_REQUEST.value(), "Method and path are required."));
        }
        if (!path.startsWith("/")) {
            return ResponseEntity.ok()
                    .body(ApiExplorerResponse.error(BAD_REQUEST.value(), "Path must start with '/'."));
        }

        try {
            HttpRequest backendRequest = buildBackendRequest(method, path, request.body());
            HttpResponse<String> backendResponse = httpClient.send(backendRequest, HttpResponse.BodyHandlers.ofString());

            return ResponseEntity.ok()
                    .body(new ApiExplorerResponse(
                            backendResponse.statusCode(),
                            method,
                            path,
                            prettyBody(backendResponse.body()),
                            backendResponse.headers().map(),
                            null
                    ));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.ok()
                    .body(ApiExplorerResponse.error(BAD_REQUEST.value(), ex.getMessage()));
        } catch (IOException | InterruptedException ex) {
            if (ex instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            return ResponseEntity.ok()
                    .body(ApiExplorerResponse.error(BAD_GATEWAY.value(), "Backend request failed: " + ex.getMessage()));
        }
    }

    private String redirectLegacyPath(HttpServletRequest request) {
        String entityKey = switch (request.getRequestURI()) {
            case "/actors" -> "actors";
            case "/addresses" -> "addresses";
            case "/categories" -> "categories";
            case "/cities" -> "cities";
            case "/countries" -> "countries";
            case "/customers" -> "customers";
            case "/films" -> "films";
            case "/inventory", "/inventories" -> "inventory";
            case "/languages" -> "languages";
            case "/payments" -> "payments";
            case "/rentals" -> "rentals";
            case "/staff" -> "staff";
            case "/stores" -> "stores";
            default -> null;
        };

        if (entityKey == null) {
            throw new ResponseStatusException(NOT_FOUND, "Unknown legacy route: " + request.getRequestURI());
        }
        return "redirect:/entities/" + entityKey;
    }

    private HttpRequest buildBackendRequest(String method, String path, String body) {
        URI uri;
        try {
            uri = new URI(ExplorerCatalog.BASE_API_URL + path);
        } catch (URISyntaxException ex) {
            throw new IllegalArgumentException("Invalid path or query string.");
        }

        HttpRequest.Builder builder = HttpRequest.newBuilder(uri)
                .timeout(Duration.ofSeconds(20))
                .header("Accept", "application/json");

        boolean hasBody = body != null && !body.isBlank();
        if (hasBody) {
            builder.header("Content-Type", "application/json");
        }

        return switch (method) {
            case "GET" -> builder.GET().build();
            case "DELETE" -> builder.DELETE().build();
            case "POST", "PUT", "PATCH" -> builder.method(method, HttpRequest.BodyPublishers.ofString(hasBody ? body : "")).build();
            default -> throw new IllegalArgumentException("Unsupported method: " + method);
        };
    }

    private PreviewData fetchPreview(String collectionPath) {
        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(ExplorerCatalog.BASE_API_URL + collectionPath))
                    .timeout(Duration.ofSeconds(10))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                return new PreviewData(List.of(), List.of(), "Preview unavailable. Backend returned HTTP " + response.statusCode() + ".");
            }

            JsonNode root = objectMapper.readTree(response.body());
            if (root == null || root.isNull()) {
                return new PreviewData(List.of(), List.of(), "Preview returned no data.");
            }

            if (root.isObject() && root.has("content") && root.get("content").isArray()) {
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

            List<String> columns = rows.isEmpty()
                    ? List.of()
                    : new ArrayList<>(rows.get(0).keySet());

            return new PreviewData(columns, rows, null);
        } catch (IOException | InterruptedException ex) {
            if (ex instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            return new PreviewData(List.of(), List.of(), "Preview unavailable. Confirm the backend is running on " + ExplorerCatalog.BASE_API_URL + ".");
        }
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
        if (value instanceof Map<?, ?> || value instanceof List<?>) {
            try {
                return objectMapper.writeValueAsString(value);
            } catch (JsonProcessingException ex) {
                return String.valueOf(value);
            }
        }
        return value;
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
                    config.put("queryFields", buildQueryFields(entity, endpoint));
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

    private List<FieldConfig> buildQueryFields(EntityDefinition entity, EndpointDefinition endpoint) {
        return buildQueryFieldsFromPath(endpoint.path());
    }

    private List<FieldConfig> buildQueryFieldsFromPath(String path) {
        int index = path.indexOf('?');
        if (index < 0 || index == path.length() - 1) {
            return List.of();
        }

        String query = path.substring(index + 1);
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

        List<String> fields = switch (entity.key() + ":" + endpoint.method()) {
            case "inventory:PATCH" -> List.of("filmId", "storeId");
            default -> endpoint.method().equals("PATCH")
                    || endpoint.bodySchema().toLowerCase(Locale.ROOT).contains("patch")
                    ? entity.schemaFields().stream()
                    .filter(field -> !field.equals("id") && !field.equals("createDate") && !field.equals("lastUpdate"))
                    .toList()
                    : entity.schemaFields();
        };

        return fields.stream()
                .map(field -> fieldConfig(field, inferFieldType(field), false))
                .toList();
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
            case "number" -> "Enter " + field;
            case "decimal" -> "0.00";
            case "datetime" -> "2026-03-28T00:00:00";
            case "email" -> "name@example.com";
            case "list" -> "value1, value2";
            default -> "Enter " + field;
        };
    }

    private String prettyBody(String body) {
        if (body == null || body.isBlank()) {
            return "";
        }
        try {
            return objectMapper.readTree(body).toPrettyString();
        } catch (JsonProcessingException ex) {
            return body;
        }
    }

    private record PreviewData(List<String> columns, List<Map<String, Object>> rows, String error) {
    }

    public record ApiExplorerRequest(String method, String path, String body) {
    }

    public record ApiExplorerResponse(
            int status,
            String method,
            String path,
            String body,
            Map<String, List<String>> headers,
            String error
    ) {
        public static ApiExplorerResponse error(int status, String errorMessage) {
            return new ApiExplorerResponse(status, null, null, null, Map.of(), errorMessage);
        }
    }

    public record FieldConfig(
            String name,
            String label,
            String type,
            boolean optional,
            String placeholder
    ) {
    }
}
