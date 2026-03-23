package com.ezinnovations.ezmenu.config;

import com.ezinnovations.ezmenu.EzMenu;
import com.ezinnovations.ezmenu.menu.MenuDefinition;
import com.ezinnovations.ezmenu.menu.MenuItemDefinition;
import com.ezinnovations.ezmenu.menu.MenuRegistry;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MenuConfigLoader {

    private final EzMenu plugin;

    public MenuConfigLoader(EzMenu plugin) {
        this.plugin = plugin;
    }

    public void loadMenus(MenuRegistry registry) {
        File menusDir = new File(plugin.getDataFolder(), "menus");
        File[] files = menusDir.listFiles((dir, name) -> name.endsWith(".yml"));

        if (files == null || files.length == 0) {
            plugin.getLogger().warning("No menu files found in menus directory.");
            return;
        }

        for (File file : files) {
            loadMenuFile(file, registry);
        }
    }

    private void loadMenuFile(File file, MenuRegistry registry) {
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection root = yaml.getConfigurationSection("menus");
        if (root == null) {
            plugin.getLogger().warning("Skipping file without 'menus' root: " + file.getName());
            return;
        }

        for (String menuId : root.getKeys(false)) {
            ConfigurationSection menuSection = root.getConfigurationSection(menuId);
            if (menuSection == null) {
                continue;
            }

            String title = menuSection.getString("title", "&8Menu");
            int size = menuSection.getInt("size", 27);
            if (size % 9 != 0 || size < 9 || size > 54) {
                plugin.getLogger().warning("Invalid size for menu " + menuId + ", defaulting to 27.");
                size = 27;
            }

            String permission = menuSection.getString("permission", "");
            MenuDefinition definition = new MenuDefinition(menuId, title, size, permission);

            ConfigurationSection itemSection = menuSection.getConfigurationSection("items");
            if (itemSection != null) {
                for (String itemId : itemSection.getKeys(false)) {
                    ConfigurationSection section = itemSection.getConfigurationSection(itemId);
                    if (section == null) {
                        continue;
                    }

                    int slot = section.getInt("slot", -1);
                    if (slot < 0 || slot >= size) {
                        plugin.getLogger().warning("Skipping item with invalid slot. Menu=" + menuId + ", item=" + itemId);
                        continue;
                    }

                    Material material = parseMaterial(section.getString("material", "STONE"));
                    String name = section.getString("name", "&fItem");
                    List<String> lore = section.getStringList("lore");
                    List<String> actions = section.getStringList("actions");
                    String itemPermission = section.getString("permission", "");
                    String showIfPlaceholder = section.getString("show-if-placeholder", "");
                    boolean noPermissionHidden = section.getBoolean("no-permission-hidden", false);
                    String denyMessage = section.getString("deny-message", "");
                    String sound = section.getString("sound", "");
                    boolean glow = section.getBoolean("glow", false);
                    long cooldownMillis = parseCooldownMillis(menuId, itemId, section.get("cooldown"));
                    String cooldownMessage = section.getString("cooldown-message", "");

                    MenuItemDefinition item = new MenuItemDefinition(
                            itemId,
                            slot,
                            material,
                            name,
                            lore == null ? Collections.emptyList() : new ArrayList<>(lore),
                            actions == null ? Collections.emptyList() : new ArrayList<>(actions),
                            itemPermission,
                            showIfPlaceholder,
                            noPermissionHidden,
                            denyMessage,
                            sound,
                            glow,
                            cooldownMillis,
                            cooldownMessage
                    );
                    definition.addItem(item);
                }
            }

            registry.registerMenu(definition);
        }
    }

    private Material parseMaterial(String value) {
        if (value == null || value.isBlank()) {
            return Material.STONE;
        }

        Material material = Material.matchMaterial(value.trim(), true);
        if (material == null) {
            plugin.getLogger().warning("Invalid material '" + value + "'. Using STONE.");
            return Material.STONE;
        }

        return material;
    }

    private long parseCooldownMillis(String menuId, String itemId, Object rawCooldown) {
        if (rawCooldown == null) {
            return 0L;
        }

        double cooldownSeconds;
        if (rawCooldown instanceof Number number) {
            cooldownSeconds = number.doubleValue();
        } else {
            String value = rawCooldown.toString().trim();
            if (value.isEmpty()) {
                return 0L;
            }

            try {
                cooldownSeconds = Double.parseDouble(value);
            } catch (NumberFormatException exception) {
                plugin.getLogger().warning("Invalid cooldown for menu " + menuId + ", item=" + itemId + ". Using 0.");
                return 0L;
            }
        }

        if (cooldownSeconds <= 0D) {
            return 0L;
        }

        return Math.round(cooldownSeconds * 1000D);
    }
}
