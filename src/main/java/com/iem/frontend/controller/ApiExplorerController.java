package com.iem.frontend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iem.frontend.catalog.ExplorerCatalog;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_GATEWAY;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Controller
public class ApiExplorerController {

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    private final ObjectMapper objectMapper = new ObjectMapper();

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
}
