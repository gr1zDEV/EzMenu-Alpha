package com.ezinnovations.ezmenu.menu;

import com.ezinnovations.ezmenu.config.ConfigManager;
import com.ezinnovations.ezmenu.service.PlaceholderService;
import com.ezinnovations.ezmenu.service.SwitchStateService;
import com.ezinnovations.ezmenu.util.ColorUtil;
import com.ezinnovations.ezmenu.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class MenuRenderer {

    private final MenuRegistry menuRegistry;
    private final PlaceholderService placeholderService;
    private final ConfigManager configManager;
    private final SwitchStateService switchStateService;

    public MenuRenderer(MenuRegistry menuRegistry,
                        PlaceholderService placeholderService,
                        ConfigManager configManager,
                        SwitchStateService switchStateService) {
        this.menuRegistry = menuRegistry;
        this.placeholderService = placeholderService;
        this.configManager = configManager;
        this.switchStateService = switchStateService;
    }

    public boolean openMenu(Player player, String menuId) {
        return menuRegistry.find(menuId).map(menu -> {
            if (!menu.permission().isBlank() && !player.hasPermission(menu.permission())) {
                player.sendMessage(ColorUtil.color(configManager.getMessage("no-permission")));
                return false;
            }

            String title = placeholderService.parse(player, menu.title());
            MenuHolder holder = new MenuHolder(menu.id());
            Inventory inventory = Bukkit.createInventory(holder, menu.size(), ColorUtil.color(title));

            for (MenuItemDefinition item : menu.items()) {
                if (item.hasPermission() && !player.hasPermission(item.permission()) && item.noPermissionHidden()) {
                    continue;
                }

                if (item.hasShowIfPlaceholder() && !matchesPlaceholderVisibility(player, item.showIfPlaceholder())) {
                    continue;
                }

                boolean switchOn = item.isSwitchButton() && switchStateService.isEnabled(player, menu.id(), item);
                String displayName = item.displayName(switchOn);
                java.util.List<String> displayLore = item.displayLore(switchOn);

                ItemStack stack = ItemBuilder.of(item.material())
                        .name(ColorUtil.color(placeholderService.parse(player, displayName)))
                        .lore(displayLore.stream().map(line -> ColorUtil.color(placeholderService.parse(player, line))).toList())
                        .glow(item.glow())
                        .build();
                inventory.setItem(item.slot(), stack);
                holder.trackVisibleItem(item);
            }

            player.openInventory(inventory);
            return true;
        }).orElse(false);
    }

    private boolean matchesPlaceholderVisibility(Player player, String showIfPlaceholder) {
        String condition = showIfPlaceholder.trim();
        String delimiter = condition.contains("==") ? "==" : "=";
        int delimiterIndex = condition.indexOf(delimiter);

        if (delimiterIndex > -1) {
            String placeholder = condition.substring(0, delimiterIndex).trim();
            String expectedValue = condition.substring(delimiterIndex + delimiter.length()).trim();

            String parsedValue = placeholderService.parse(player, placeholder).trim();
            return parsedValue.equalsIgnoreCase(stripWrappingQuotes(expectedValue));
        }

        String parsedValue = placeholderService.parse(player, condition).trim();
        return parsedValue.equalsIgnoreCase("true")
                || parsedValue.equalsIgnoreCase("yes")
                || parsedValue.equals("1")
                || parsedValue.equalsIgnoreCase("on");
    }

    private String stripWrappingQuotes(String value) {
        if (value.length() >= 2) {
            if ((value.startsWith("\"") && value.endsWith("\""))
                    || (value.startsWith("'") && value.endsWith("'"))) {
                return value.substring(1, value.length() - 1);
            }
        }

        return value;
    }
}
