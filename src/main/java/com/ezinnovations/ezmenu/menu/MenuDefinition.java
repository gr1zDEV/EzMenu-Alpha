package com.ezinnovations.ezmenu.menu;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public final class MenuDefinition {

    private final String id;
    private final String title;
    private final int size;
    private final String permission;
    private final Map<Integer, MenuItemDefinition> itemsBySlot = new LinkedHashMap<>();

    public MenuDefinition(String id, String title, int size, String permission) {
        this.id = id;
        this.title = title;
        this.size = size;
        this.permission = permission == null ? "" : permission;
    }

    public void addItem(MenuItemDefinition item) {
        itemsBySlot.put(item.slot(), item);
    }

    public String id() {
        return id;
    }

    public String title() {
        return title;
    }

    public int size() {
        return size;
    }

    public String permission() {
        return permission;
    }

    public Collection<MenuItemDefinition> items() {
        return itemsBySlot.values();
    }

    public MenuItemDefinition getBySlot(int slot) {
        return itemsBySlot.get(slot);
    }
}
