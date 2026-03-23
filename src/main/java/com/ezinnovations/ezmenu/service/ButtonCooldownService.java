package com.ezinnovations.ezmenu.service;

import com.ezinnovations.ezmenu.menu.MenuItemDefinition;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ButtonCooldownService {

    private final Map<UUID, Map<String, Long>> cooldowns = new ConcurrentHashMap<>();

    public CooldownStatus checkAndApply(Player player, String menuId, MenuItemDefinition item) {
        if (!item.hasCooldown()) {
            return CooldownStatus.ready();
        }

        long now = System.currentTimeMillis();
        String key = buildKey(menuId, item.id());
        Map<String, Long> playerCooldowns = cooldowns.computeIfAbsent(player.getUniqueId(), ignored -> new ConcurrentHashMap<>());
        long nextAvailableAt = playerCooldowns.getOrDefault(key, 0L);

        if (nextAvailableAt > now) {
            return CooldownStatus.coolingDown(nextAvailableAt - now);
        }

        playerCooldowns.put(key, now + item.cooldownMillis());
        return CooldownStatus.ready();
    }

    private String buildKey(String menuId, String itemId) {
        return menuId + ':' + itemId;
    }

    public record CooldownStatus(boolean allowed, long remainingMillis) {
        public static CooldownStatus ready() {
            return new CooldownStatus(true, 0L);
        }

        public static CooldownStatus coolingDown(long remainingMillis) {
            return new CooldownStatus(false, Math.max(remainingMillis, 0L));
        }
    }
}
