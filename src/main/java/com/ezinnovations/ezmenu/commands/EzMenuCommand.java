package com.ezinnovations.ezmenu.commands;

import com.ezinnovations.ezmenu.EzMenu;
import com.ezinnovations.ezmenu.config.ConfigManager;
import com.ezinnovations.ezmenu.menu.MenuDefinition;
import com.ezinnovations.ezmenu.menu.MenuRegistry;
import com.ezinnovations.ezmenu.menu.MenuRenderer;
import com.ezinnovations.ezmenu.util.ColorUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class EzMenuCommand implements CommandExecutor, TabCompleter {

    private final EzMenu plugin;
    private final MenuRegistry menuRegistry;
    private final MenuRenderer menuRenderer;
    private final ConfigManager configManager;

    public EzMenuCommand(EzMenu plugin, MenuRegistry menuRegistry, MenuRenderer menuRenderer, ConfigManager configManager) {
        this.plugin = plugin;
        this.menuRegistry = menuRegistry;
        this.menuRenderer = menuRenderer;
        this.configManager = configManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ColorUtil.color(configManager.getMessage("invalid-usage").replace("{usage}", "/" + label + " <open|reload|list>")));
            return true;
        }

        String sub = args[0].toLowerCase(Locale.ROOT);
        switch (sub) {
            case "open" -> handleOpen(sender, label, args);
            case "reload" -> handleReload(sender);
            case "list" -> handleList(sender);
            default -> sender.sendMessage(ColorUtil.color(configManager.getMessage("invalid-usage").replace("{usage}", "/" + label + " <open|reload|list>")));
        }

        return true;
    }

    private void handleOpen(CommandSender sender, String label, String[] args) {
        if (!sender.hasPermission("ezmenu.open")) {
            sender.sendMessage(ColorUtil.color(configManager.getMessage("no-permission")));
            return;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ColorUtil.color(configManager.getMessage("player-only")));
            return;
        }

        if (args.length < 2) {
            sender.sendMessage(ColorUtil.color(configManager.getMessage("invalid-usage").replace("{usage}", "/" + label + " open <menu>")));
            return;
        }

        String menuId = args[1];
        boolean opened = menuRenderer.openMenu(player, menuId);
        if (!opened) {
            player.sendMessage(ColorUtil.color(configManager.getMessage("menu-not-found").replace("{menu}", menuId)));
        }
    }

    private void handleReload(CommandSender sender) {
        if (!sender.hasPermission("ezmenu.reload")) {
            sender.sendMessage(ColorUtil.color(configManager.getMessage("no-permission")));
            return;
        }

        plugin.reloadPluginState();
        sender.sendMessage(ColorUtil.color(configManager.getMessage("reload-success")));
    }

    private void handleList(CommandSender sender) {
        if (!sender.hasPermission("ezmenu.list")) {
            sender.sendMessage(ColorUtil.color(configManager.getMessage("no-permission")));
            return;
        }

        List<String> visibleMenus = new ArrayList<>();
        for (MenuDefinition menu : menuRegistry.getAllMenus()) {
            if (!(sender instanceof Player player) || menu.permission().isBlank() || player.hasPermission(menu.permission()) || sender.hasPermission("ezmenu.admin")) {
                visibleMenus.add(menu.id());
            }
        }

        sender.sendMessage(ColorUtil.color(configManager.getMessage("menu-list-header")));
        if (visibleMenus.isEmpty()) {
            sender.sendMessage(ColorUtil.color("&7-"));
            return;
        }
        sender.sendMessage(ColorUtil.color("&a" + String.join("&8, &a", visibleMenus)));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return List.of("open", "reload", "list").stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase(Locale.ROOT)))
                    .toList();
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("open")) {
            return menuRegistry.getAllMenus().stream()
                    .map(MenuDefinition::id)
                    .filter(id -> id.toLowerCase(Locale.ROOT).startsWith(args[1].toLowerCase(Locale.ROOT)))
                    .toList();
        }

        return List.of();
    }
}
