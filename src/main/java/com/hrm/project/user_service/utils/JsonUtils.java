package com.hrm.project.user_service.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

/**
 * Utility class for JSON serialization and deserialization.
 */
public final class JsonUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private JsonUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Converts a Map into its JSON String representation.
     *
     * @param map source map
     * @return JSON string
     */
    public static String mapToJson(Map<String, Object> map) {

        try {
            return OBJECT_MAPPER.writeValueAsString(map);
        } catch (JsonProcessingException exception) {
            throw new RuntimeException("Failed to convert map to JSON", exception);
        }
    }

    /**
     * Converts a JSON String into a Map.
     *
     * @param json JSON string
     * @return deserialized map
     */
    public static Map<String, Object> jsonToMap(String json) {

        try {
            return OBJECT_MAPPER.readValue(
                    json,
                    new TypeReference<Map<String, Object>>() {
                    }
            );
        } catch (JsonProcessingException exception) {
            throw new RuntimeException("Failed to convert JSON to map", exception);
        }
    }
}