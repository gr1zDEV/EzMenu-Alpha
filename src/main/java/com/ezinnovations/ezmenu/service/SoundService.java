package com.ezinnovations.ezmenu.service;

import com.ezinnovations.ezmenu.EzMenu;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public final class SoundService {

    private final EzMenu plugin;

    public SoundService(EzMenu plugin) {
        this.plugin = plugin;
    }

    public void play(Player player, String soundName) {
        Sound sound = parseSound(soundName);
        if (sound == null) {
            return;
        }

        player.playSound(player.getLocation(), sound, 1.0F, 1.0F);
    }

    private Sound parseSound(String soundName) {
        if (soundName == null || soundName.isBlank()) {
            return null;
        }

        try {
            return Sound.valueOf(soundName.trim().toUpperCase());
        } catch (IllegalArgumentException ignored) {
            plugin.getLogger().warning("Invalid sound in menu config: " + soundName);
            return null;
        }
    }
}
