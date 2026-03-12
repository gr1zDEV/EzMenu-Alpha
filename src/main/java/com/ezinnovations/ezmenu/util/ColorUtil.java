package com.ezinnovations.ezmenu.util;

import org.bukkit.ChatColor;

public final class ColorUtil {

    private ColorUtil() {
    }

    public static String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text == null ? "" : text);
    }
}
