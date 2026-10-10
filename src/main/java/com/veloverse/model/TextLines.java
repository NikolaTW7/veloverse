package com.veloverse.model;

import java.util.Arrays;
import java.util.List;

/**
 * Turns a text column that stores one item per line (for example a list of advantages)
 * into a list, so templates can show it as bullet points.
 *
 * <p>The class has no access modifier (package-private), so only the model classes can use it,
 * and it is final, so it cannot be extended. It is a helper class with only a static method.
 */
final class TextLines {

    // Private constructor: nobody needs to create a TextLines object
    private TextLines() {
    }

    /**
     * Splits the text on every line break ("\\R" matches \n, \r\n and other line breaks),
     * trims spaces from each line and leaves out empty lines.
     * Null or blank text gives an empty list, so templates never have to check for null.
     */
    static List<String> split(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        // A stream processes the lines one after another: strip each line, keep the non-empty ones,
        // then collect them into a list that cannot be changed
        return Arrays.stream(text.split("\\R"))
                .map(String::strip)
                .filter(line -> !line.isEmpty())
                .toList();
    }
}
