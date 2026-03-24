package com.ezinnovations.ezmenu.menu;

import com.ezinnovations.ezmenu.EzMenu;
import com.ezinnovations.ezmenu.config.ConfigManager;
import com.ezinnovations.ezmenu.service.PlaceholderService;
import com.ezinnovations.ezmenu.service.SoundService;
import com.ezinnovations.ezmenu.util.ColorUtil;
import com.ezinnovations.ezmenu.util.SafeExecution;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Locale;

public final class MenuActionExecutor {

    private final EzMenu plugin;
    private final MenuRegistry menuRegistry;
    private final PlaceholderService placeholderService;
    private final SoundService soundService;
    private final ConfigManager configManager;
    private final SafeExecution safeExecution;

    public MenuActionExecutor(EzMenu plugin,
                              MenuRegistry menuRegistry,
                              PlaceholderService placeholderService,
                              SoundService soundService,
                              ConfigManager configManager) {
        this.plugin = plugin;
        this.menuRegistry = menuRegistry;
        this.placeholderService = placeholderService;
        this.soundService = soundService;
        this.configManager = configManager;
        this.safeExecution = new SafeExecution(plugin);
    }

    public void execute(Player player, String currentMenuId, MenuItemDefinition item, MenuRenderer renderer) {
        execute(player, currentMenuId, item, renderer, item.actions());
    }

    public void execute(Player player,
                        String currentMenuId,
                        MenuItemDefinition item,
                        MenuRenderer renderer,
                        List<String> actions) {
        if (item.hasPermission() && !player.hasPermission(item.permission())) {
            if (!item.denyMessage().isBlank()) {
                player.sendMessage(ColorUtil.color(placeholderService.parse(player, item.denyMessage())));
            } else {
                player.sendMessage(ColorUtil.color(configManager.getMessage("no-permission")));
            }
            return;
        }

        if (!item.sound().isBlank()) {
            soundService.play(player, item.sound());
        }

        for (String rawAction : actions) {
            String parsedAction = placeholderService.parse(player, rawAction).replace("{player}", player.getName());
            executeSingle(player, currentMenuId, parsedAction, renderer);
        }
    }

    private void executeSingle(Player player, String currentMenuId, String action, MenuRenderer renderer) {
        String[] split = action.split(":", 2);
        String type = split[0].toLowerCase(Locale.ROOT);
        String value = split.length > 1 ? split[1] : "";

        switch (type) {
            case "open" -> {
                boolean opened = renderer.openMenu(player, value);
                if (!opened) {
                    player.sendMessage(ColorUtil.color(configManager.getMessage("menu-not-found").replace("{menu}", value)));
                }
            }
            case "player-command" -> dispatchPlayerCommand(player, stripLeadingSlash(value));
            case "console-command" -> dispatchConsoleCommand(stripLeadingSlash(value));
            case "message" -> player.sendMessage(ColorUtil.color(value));
            case "close" -> player.closeInventory();
            case "refresh" -> {
                if (!currentMenuId.isBlank()) {
                    safeExecution.runForPlayerLater(player, () -> {
                        boolean reopened = renderer.openMenu(player, currentMenuId);
                        if (!reopened) {
                            player.sendMessage(ColorUtil.color(configManager.getMessage("menu-not-found").replace("{menu}", currentMenuId)));
                        }
                    }, configManager.getRefreshDelayTicks());
                }
            }
            default -> {
                if (configManager.isDebugEnabled()) {
                    plugin.getLogger().warning("Unknown action: " + action);
                }
            }
        }
    }

    private void dispatchPlayerCommand(Player player, String command) {
        if (command.isBlank()) {
            return;
        }

        safeExecution.dispatchPlayerCommand(player, command);
    }

    private void dispatchConsoleCommand(String command) {
        if (command.isBlank()) {
            return;
        }

        safeExecution.dispatchConsoleCommand(command);
    }

    private String stripLeadingSlash(String command) {
        if (command == null) {
            return "";
        }
        return command.startsWith("/") ? command.substring(1) : command;
    }
}
