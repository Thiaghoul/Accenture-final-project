package com.group5.taskFlow.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Priority {

    LOW,
    MEDIUM,
    HIGH,
    URGENT;

    @JsonCreator
    public static Priority fromString(String value) {
        if (value == null) {
            return null;
        }
        return switch (value.toLowerCase()) {
            case "baixa" -> LOW;
            case "media" -> MEDIUM;
            case "alta" -> HIGH;
            case "urgente" -> URGENT;
            default -> Priority.valueOf(value.toUpperCase());
        };
    }
}
