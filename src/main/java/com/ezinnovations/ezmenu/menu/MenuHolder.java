package com.ezinnovations.ezmenu.menu;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.HashMap;
import java.util.Map;

public final class MenuHolder implements InventoryHolder {

    private final String menuId;
    private final Map<Integer, MenuItemDefinition> visibleItemsBySlot = new HashMap<>();

    public MenuHolder(String menuId) {
        this.menuId = menuId;
    }

    public String menuId() {
        return menuId;
    }

    public void trackVisibleItem(MenuItemDefinition item) {
        visibleItemsBySlot.put(item.slot(), item);
    }

    public MenuItemDefinition getVisibleItem(int slot) {
        return visibleItemsBySlot.get(slot);
    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}
