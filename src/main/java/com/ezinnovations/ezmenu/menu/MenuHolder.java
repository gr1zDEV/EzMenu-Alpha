package com.ezinnovations.ezmenu.menu;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public record MenuHolder(String menuId) implements InventoryHolder {
    @Override
    public Inventory getInventory() {
        return null;
    }
}
