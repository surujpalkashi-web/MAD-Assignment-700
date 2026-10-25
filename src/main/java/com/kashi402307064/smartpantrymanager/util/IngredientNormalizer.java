package com.kashi402307064.smartpantrymanager.util;

/**
 * Normalises ingredient names so "Eggs" and "egg" are treated as the same item.
 */
public final class IngredientNormalizer {

    private IngredientNormalizer() {
    }

    public static String normalize(String raw) {
        if (raw == null) {
            return "";
        }
        String value = raw.trim().toLowerCase();

        // basic plural handling for common food words
        if (value.endsWith("oes") && value.length() > 3) {
            // tomatoes -> tomato, potatoes -> potato
            value = value.substring(0, value.length() - 2);
        } else if (value.endsWith("ies") && value.length() > 3) {
            // berries -> berry style words
            value = value.substring(0, value.length() - 3) + "y";
        } else if (value.endsWith("es") && value.length() > 3) {
            value = value.substring(0, value.length() - 2);
        } else if (value.endsWith("s") && value.length() > 2) {
            value = value.substring(0, value.length() - 1);
        }

        return value.trim();
    }
}
