package com.veloverse.model;

import java.util.Arrays;
import java.util.List;

/**
 * Turns a text column that stores one item per line (for example a list of advantages)
 * into a list, so templates can show it as bullet points.
 */
final class TextLines {

    private TextLines() {
    }

    static List<String> split(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return Arrays.stream(text.split("\\R"))
                .map(String::strip)
                .filter(line -> !line.isEmpty())
                .toList();
    }
}
