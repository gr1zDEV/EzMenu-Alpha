package com.ezinnovations.ezmenu.menu;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public final class MenuRegistry {

    private final Map<String, MenuDefinition> menus = new LinkedHashMap<>();

    public void registerMenu(MenuDefinition definition) {
        menus.put(normalize(definition.id()), definition);
    }

    public Optional<MenuDefinition> find(String id) {
        return Optional.ofNullable(menus.get(normalize(id)));
    }

    public Collection<MenuDefinition> getAllMenus() {
        return Collections.unmodifiableCollection(menus.values());
    }

    public int getMenuCount() {
        return menus.size();
    }

    public void clear() {
        menus.clear();
    }

    private String normalize(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT);
    }
}
