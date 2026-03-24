package com.ezinnovations.ezmenu.service;

import com.ezinnovations.ezmenu.menu.MenuItemDefinition;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class SwitchStateService {

    private final Map<UUID, Map<String, Boolean>> statesByPlayer = new ConcurrentHashMap<>();

    public boolean isEnabled(Player player, String menuId, MenuItemDefinition item) {
        return statesByPlayer
                .computeIfAbsent(player.getUniqueId(), key -> new ConcurrentHashMap<>())
                .getOrDefault(stateKey(menuId, item.id()), item.switchDefaultOn());
    }

    public boolean toggle(Player player, String menuId, MenuItemDefinition item) {
        Map<String, Boolean> playerStates = statesByPlayer.computeIfAbsent(player.getUniqueId(), key -> new ConcurrentHashMap<>());
        String stateKey = stateKey(menuId, item.id());
        boolean currentState = playerStates.getOrDefault(stateKey, item.switchDefaultOn());
        boolean newState = !currentState;
        playerStates.put(stateKey, newState);
        return newState;
    }

    private String stateKey(String menuId, String itemId) {
        return menuId + ":" + itemId;
    }
}
