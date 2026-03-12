package com.ezinnovations.ezmenu.service;

import com.ezinnovations.ezmenu.EzMenu;
import com.ezinnovations.ezmenu.config.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

public final class PlaceholderService {

    private final EzMenu plugin;
    private final ConfigManager configManager;
    private final boolean placeholderApiPresent;

    public PlaceholderService(EzMenu plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.placeholderApiPresent = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI") && configManager.isPlaceholderHookEnabled();

        if (placeholderApiPresent) {
            plugin.getLogger().info("PlaceholderAPI detected and enabled.");
        }
    }

    public String parse(Player player, String input) {
        if (input == null) {
            return "";
        }

        if (!placeholderApiPresent) {
            return input;
        }

        try {
            Class<?> clazz = Class.forName("me.clip.placeholderapi.PlaceholderAPI");
            Method method = clazz.getMethod("setPlaceholders", Player.class, String.class);
            return (String) method.invoke(null, player, input);
        } catch (Exception e) {
            if (configManager.isDebugEnabled()) {
                plugin.getLogger().warning("Placeholder parsing failed: " + e.getMessage());
            }
            return input;
        }
    }
}
