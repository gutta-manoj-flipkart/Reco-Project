package org.example.models;

public enum Type {
    KEY_UPDATE,
    KEY_DELETE;

    public static Type fromString(String string) {
        try {
            return Type.valueOf(string);
        } catch (IllegalArgumentException e) {
            // Handle the case where the string does not match any enum
            throw new IllegalArgumentException("Unknown enum type: " + string);
        }
    }
}
