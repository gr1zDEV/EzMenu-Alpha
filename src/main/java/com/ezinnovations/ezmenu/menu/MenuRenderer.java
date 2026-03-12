package com.ezinnovations.ezmenu.menu;

import com.ezinnovations.ezmenu.config.ConfigManager;
import com.ezinnovations.ezmenu.service.PlaceholderService;
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

    public MenuRenderer(MenuRegistry menuRegistry, PlaceholderService placeholderService, ConfigManager configManager) {
        this.menuRegistry = menuRegistry;
        this.placeholderService = placeholderService;
        this.configManager = configManager;
    }

    public boolean openMenu(Player player, String menuId) {
        return menuRegistry.find(menuId).map(menu -> {
            if (!menu.permission().isBlank() && !player.hasPermission(menu.permission())) {
                player.sendMessage(ColorUtil.color(configManager.getMessage("no-permission")));
                return false;
            }

            String title = placeholderService.parse(player, menu.title());
            Inventory inventory = Bukkit.createInventory(new MenuHolder(menu.id()), menu.size(), ColorUtil.color(title));

            for (MenuItemDefinition item : menu.items()) {
                if (item.hasPermission() && !player.hasPermission(item.permission()) && item.noPermissionHidden()) {
                    continue;
                }

                ItemStack stack = ItemBuilder.of(item.material())
                        .name(ColorUtil.color(placeholderService.parse(player, item.name())))
                        .lore(item.lore().stream().map(line -> ColorUtil.color(placeholderService.parse(player, line))).toList())
                        .build();
                inventory.setItem(item.slot(), stack);
            }

            player.openInventory(inventory);
            return true;
        }).orElse(false);
    }
}
