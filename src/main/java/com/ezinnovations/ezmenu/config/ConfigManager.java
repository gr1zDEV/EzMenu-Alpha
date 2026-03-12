package com.ezinnovations.ezmenu.config;

import com.ezinnovations.ezmenu.EzMenu;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public final class ConfigManager {

    private final EzMenu plugin;
    private File messagesFile;
    private File soundsFile;
    private FileConfiguration messages;
    private FileConfiguration sounds;
    private MenuConfigLoader menuConfigLoader;

    public ConfigManager(EzMenu plugin) {
        this.plugin = plugin;
    }

    public void initialize() {
        plugin.saveDefaultConfig();
        saveResourceIfMissing("messages.yml");
        saveResourceIfMissing("sounds.yml");
        ensureMenusDirectory();
        this.menuConfigLoader = new MenuConfigLoader(plugin);
    }

    public void reloadAll() {
        plugin.reloadConfig();

        this.messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        this.soundsFile = new File(plugin.getDataFolder(), "sounds.yml");

        this.messages = YamlConfiguration.loadConfiguration(messagesFile);
        this.sounds = YamlConfiguration.loadConfiguration(soundsFile);

        ensureMenusDirectory();
    }

    public String getMessage(String key) {
        return messages.getString("messages." + key, "&cMissing message: " + key);
    }

    public boolean isDebugEnabled() {
        return plugin.getConfig().getBoolean("debug", false);
    }

    public boolean isPlaceholderHookEnabled() {
        return plugin.getConfig().getBoolean("hooks.placeholderapi", true);
    }

    public MenuConfigLoader getMenuConfigLoader() {
        return menuConfigLoader;
    }

    public File getMenusDirectory() {
        return new File(plugin.getDataFolder(), "menus");
    }

    private void ensureMenusDirectory() {
        File menuDir = getMenusDirectory();
        if (!menuDir.exists() && !menuDir.mkdirs()) {
            plugin.getLogger().warning("Unable to create menus directory.");
        }

        saveMenuDefault("menus/main.yml");
        saveMenuDefault("menus/server.yml");
        saveMenuDefault("menus/admin.yml");
    }

    private void saveMenuDefault(String path) {
        File file = new File(plugin.getDataFolder(), path);
        if (file.exists()) {
            return;
        }

        plugin.saveResource(path, false);
    }

    private void saveResourceIfMissing(String path) {
        File file = new File(plugin.getDataFolder(), path);
        if (file.exists()) {
            return;
        }

        plugin.saveResource(path, false);
    }

    public void saveMessages() {
        save(messages, messagesFile);
    }

    public void saveSounds() {
        save(sounds, soundsFile);
    }

    private void save(FileConfiguration config, File file) {
        if (config == null || file == null) {
            return;
        }

        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save file: " + file.getName(), e);
        }
    }
}
