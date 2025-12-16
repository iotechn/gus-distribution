package com.dobbinsoft.gus.distribution.utils.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonFlattener {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static ObjectNode flatten(JsonNode node) {
        ObjectNode flatNode = objectMapper.createObjectNode();
        Map<String, List<JsonNode>> arraysMap = new HashMap<>();
        flatten("", node, flatNode, arraysMap);

        // Add arrays to the result with '[]' suffix
        for (Map.Entry<String, List<JsonNode>> entry : arraysMap.entrySet()) {
            ArrayNode arrayNode = objectMapper.createArrayNode();
            entry.getValue().forEach(arrayNode::add);
            flatNode.set(entry.getKey() + "[]", arrayNode);
        }

        return flatNode;
    }

    private static void flatten(String prefix, JsonNode node, ObjectNode flatNode, Map<String, List<JsonNode>> arraysMap) {
        if (node.isObject()) {
            for (Map.Entry<String, JsonNode> entry : node.properties()) {
                String newPrefix = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
                flatten(newPrefix, entry.getValue(), flatNode, arraysMap);
            }
        } else if (node.isArray()) {
            // Collect elements of the array for `[]` suffix
            if (!node.isEmpty() && node.get(0).isValueNode()) {
                for (JsonNode arrayElement : node) {
                    arraysMap.computeIfAbsent(prefix, k -> new ArrayList<>()).add(arrayElement);
                }
                // Add the first element to the flat structure
                flatNode.set(prefix, node.get(0));
            } else {
                // Process arrays of objects
                for (JsonNode arrayElement : node) {
                    flatten(prefix, arrayElement, flatNode, arraysMap);
                }
            }
        } else {
            flatNode.set(prefix, node);
            // For value nodes, add them to arraysMap to handle cases where values repeat in similar paths across elements
            arraysMap.computeIfAbsent(prefix, k -> new ArrayList<>()).add(node);
        }
    }
}