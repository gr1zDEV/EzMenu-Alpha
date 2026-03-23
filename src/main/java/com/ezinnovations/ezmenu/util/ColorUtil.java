package com.ezinnovations.ezmenu.util;

import org.bukkit.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ColorUtil {

    private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("(?i)&?#([0-9a-f]{6})");

    private ColorUtil() {
    }

    public static String color(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        return ChatColor.translateAlternateColorCodes('&', applyHexColors(text));
    }

    private static String applyHexColors(String text) {
        Matcher matcher = HEX_COLOR_PATTERN.matcher(text);
        StringBuilder output = new StringBuilder(text.length() + 32);

        while (matcher.find()) {
            matcher.appendReplacement(output, Matcher.quoteReplacement(toLegacyHexColor(matcher.group(1))));
        }

        matcher.appendTail(output);
        return output.toString();
    }

    private static String toLegacyHexColor(String hex) {
        StringBuilder builder = new StringBuilder(14).append('&').append('x');
        for (char character : hex.toCharArray()) {
            builder.append('&').append(character);
        }
        return builder.toString();
    }
}
