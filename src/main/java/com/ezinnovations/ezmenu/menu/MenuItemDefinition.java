package com.ezinnovations.ezmenu.menu;

import org.bukkit.Material;

import java.util.List;

public record MenuItemDefinition(
        String id,
        int slot,
        Material material,
        String name,
        List<String> lore,
        List<String> actions,
        String permission,
        boolean noPermissionHidden,
        String denyMessage,
        String sound
) {
    public boolean hasPermission() {
        return permission != null && !permission.isBlank();
    }
}
