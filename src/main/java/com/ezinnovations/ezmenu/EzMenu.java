package com.ezinnovations.ezmenu;

import com.ezinnovations.ezmenu.commands.EzMenuCommand;
import com.ezinnovations.ezmenu.config.ConfigManager;
import com.ezinnovations.ezmenu.listeners.InventoryClickListener;
import com.ezinnovations.ezmenu.menu.MenuActionExecutor;
import com.ezinnovations.ezmenu.menu.MenuRegistry;
import com.ezinnovations.ezmenu.menu.MenuRenderer;
import com.ezinnovations.ezmenu.service.ButtonCooldownService;
import com.ezinnovations.ezmenu.service.PlaceholderService;
import com.ezinnovations.ezmenu.service.SoundService;
import com.ezinnovations.ezmenu.service.SwitchStateService;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class EzMenu extends JavaPlugin {

    private ConfigManager configManager;
    private MenuRegistry menuRegistry;
    private PlaceholderService placeholderService;
    private SoundService soundService;
    private ButtonCooldownService buttonCooldownService;
    private SwitchStateService switchStateService;
    private MenuRenderer menuRenderer;
    private MenuActionExecutor menuActionExecutor;

    @Override
    public void onEnable() {
        this.configManager = new ConfigManager(this);
        this.configManager.initialize();

        this.placeholderService = new PlaceholderService(this, configManager);
        this.soundService = new SoundService(this);
        this.menuRegistry = new MenuRegistry();
        this.buttonCooldownService = new ButtonCooldownService();
        this.switchStateService = new SwitchStateService();

        reloadPluginState();

        this.menuActionExecutor = new MenuActionExecutor(this, menuRegistry, placeholderService, soundService, configManager);
        this.menuRenderer = new MenuRenderer(menuRegistry, placeholderService, configManager, switchStateService);

        getServer().getPluginManager().registerEvents(
                new InventoryClickListener(menuRegistry, menuRenderer, menuActionExecutor, configManager, placeholderService, buttonCooldownService, switchStateService),
                this
        );

        EzMenuCommand commandHandler = new EzMenuCommand(this, menuRegistry, menuRenderer, configManager);
        registerCommand("ezmenu", commandHandler);
        registerCommand("menu", commandHandler);

        getLogger().info("EzMenu enabled. Loaded " + menuRegistry.getMenuCount() + " menu(s).");
    }

    @Override
    public void onDisable() {
        getLogger().info("EzMenu disabled.");
    }

    public void reloadPluginState() {
        configManager.reloadAll();
        menuRegistry.clear();
        configManager.getMenuConfigLoader().loadMenus(menuRegistry);
    }

    private void registerCommand(String name, EzMenuCommand commandHandler) {
        PluginCommand command = getCommand(name);
        if (command == null) {
            getLogger().warning("Command '" + name + "' missing from plugin.yml.");
            return;
        }

        command.setExecutor(commandHandler);
        command.setTabCompleter(commandHandler);
    }

    public MenuRenderer getMenuRenderer() {
        return menuRenderer;
    }
}
