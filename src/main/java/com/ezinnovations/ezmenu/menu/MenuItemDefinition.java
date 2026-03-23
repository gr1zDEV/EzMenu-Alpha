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
        String showIfPlaceholder,
        boolean noPermissionHidden,
        String denyMessage,
        String sound,
        boolean glow,
        long cooldownMillis,
        String cooldownMessage
) {
    public boolean hasPermission() {
        return permission != null && !permission.isBlank();
    }

    public boolean hasShowIfPlaceholder() {
        return showIfPlaceholder != null && !showIfPlaceholder.isBlank();
    }

    public boolean hasCooldown() {
        return cooldownMillis > 0L;
    }

    public boolean hasCooldownMessage() {
        return cooldownMessage != null && !cooldownMessage.isBlank();
    }
}
