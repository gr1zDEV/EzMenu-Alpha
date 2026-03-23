package com.ezinnovations.ezmenu.menu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class MenuDefinition {

    private final String id;
    private final String title;
    private final int size;
    private final String permission;
    private final List<MenuItemDefinition> items = new ArrayList<>();

    public MenuDefinition(String id, String title, int size, String permission) {
        this.id = id;
        this.title = title;
        this.size = size;
        this.permission = permission == null ? "" : permission;
    }

    public void addItem(MenuItemDefinition item) {
        items.add(item);
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
        return Collections.unmodifiableList(items);
    }
}
