package com.ezinnovations.ezmenu.listeners;

import com.ezinnovations.ezmenu.config.ConfigManager;
import com.ezinnovations.ezmenu.menu.MenuActionExecutor;
import com.ezinnovations.ezmenu.menu.MenuDefinition;
import com.ezinnovations.ezmenu.menu.MenuHolder;
import com.ezinnovations.ezmenu.menu.MenuItemDefinition;
import com.ezinnovations.ezmenu.menu.MenuRegistry;
import com.ezinnovations.ezmenu.menu.MenuRenderer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public final class InventoryClickListener implements Listener {

    private final MenuRegistry menuRegistry;
    private final MenuRenderer menuRenderer;
    private final MenuActionExecutor actionExecutor;
    private final ConfigManager configManager;

    public InventoryClickListener(MenuRegistry menuRegistry, MenuRenderer menuRenderer, MenuActionExecutor actionExecutor, ConfigManager configManager) {
        this.menuRegistry = menuRegistry;
        this.menuRenderer = menuRenderer;
        this.actionExecutor = actionExecutor;
        this.configManager = configManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        if (!(event.getView().getTopInventory().getHolder() instanceof MenuHolder holder)) {
            return;
        }

        event.setCancelled(true);

        int slot = event.getRawSlot();
        if (slot < 0 || slot >= event.getView().getTopInventory().getSize()) {
            return;
        }

        MenuDefinition menu = menuRegistry.find(holder.menuId()).orElse(null);
        if (menu == null) {
            if (configManager.isDebugEnabled()) {
                player.sendMessage("§cMenu definition missing for holder: " + holder.menuId());
            }
            return;
        }

        MenuItemDefinition item = menu.getBySlot(slot);
        if (item == null) {
            return;
        }

        actionExecutor.execute(player, item, menuRenderer);
    }
}
