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
        String buttonType,
        boolean switchDefaultOn,
        String switchOnName,
        String switchOffName,
        List<String> switchOnLore,
        List<String> switchOffLore,
        List<String> switchOnActions,
        List<String> switchOffActions,
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

    public boolean isSwitchButton() {
        return buttonType != null && buttonType.equalsIgnoreCase("switch");
    }

    public String displayName(boolean switchOn) {
        String switchName = switchOn ? switchOnName : switchOffName;
        return switchName != null && !switchName.isBlank() ? switchName : name;
    }

    public List<String> displayLore(boolean switchOn) {
        List<String> switchLore = switchOn ? switchOnLore : switchOffLore;
        return switchLore != null && !switchLore.isEmpty() ? switchLore : lore;
    }

    public List<String> actionsForState(boolean switchOn) {
        List<String> switchActions = switchOn ? switchOnActions : switchOffActions;
        return switchActions != null && !switchActions.isEmpty() ? switchActions : actions;
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
