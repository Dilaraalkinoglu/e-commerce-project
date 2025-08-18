package com.dilaraalk.common.util;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

public class SlugUtil {

    public static String generateSlug(String input) {
        if (input == null || input.isBlank()) {
            return "";
        }
        String nowhitespace = input.trim().replaceAll("\\s+", "-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = Pattern.compile("[^\\w-]")
                .matcher(normalized)
                .replaceAll("")
                .toLowerCase(Locale.ENGLISH);
        return slug;
    }

    public static String generateUniqueSlug(String baseName, java.util.function.Predicate<String> existsChecker) {
        String baseSlug = generateSlug(baseName);
        String slug = baseSlug;
        int counter = 1;
        while (existsChecker.test(slug)) { 
            slug = baseSlug + "-" + counter++;
        }
        return slug;
    }
}
