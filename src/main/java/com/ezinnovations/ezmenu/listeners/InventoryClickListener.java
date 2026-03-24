package com.ezinnovations.ezmenu.listeners;

import com.ezinnovations.ezmenu.config.ConfigManager;
import com.ezinnovations.ezmenu.menu.MenuActionExecutor;
import com.ezinnovations.ezmenu.menu.MenuDefinition;
import com.ezinnovations.ezmenu.menu.MenuHolder;
import com.ezinnovations.ezmenu.menu.MenuItemDefinition;
import com.ezinnovations.ezmenu.menu.MenuRegistry;
import com.ezinnovations.ezmenu.menu.MenuRenderer;
import com.ezinnovations.ezmenu.service.ButtonCooldownService;
import com.ezinnovations.ezmenu.service.PlaceholderService;
import com.ezinnovations.ezmenu.service.SwitchStateService;
import com.ezinnovations.ezmenu.util.ColorUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Locale;

public final class InventoryClickListener implements Listener {

    private final MenuRegistry menuRegistry;
    private final MenuRenderer menuRenderer;
    private final MenuActionExecutor actionExecutor;
    private final ConfigManager configManager;
    private final PlaceholderService placeholderService;
    private final ButtonCooldownService cooldownService;
    private final SwitchStateService switchStateService;

    public InventoryClickListener(MenuRegistry menuRegistry,
                                  MenuRenderer menuRenderer,
                                  MenuActionExecutor actionExecutor,
                                  ConfigManager configManager,
                                  PlaceholderService placeholderService,
                                  ButtonCooldownService cooldownService,
                                  SwitchStateService switchStateService) {
        this.menuRegistry = menuRegistry;
        this.menuRenderer = menuRenderer;
        this.actionExecutor = actionExecutor;
        this.configManager = configManager;
        this.placeholderService = placeholderService;
        this.cooldownService = cooldownService;
        this.switchStateService = switchStateService;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        if (!(event.getView().getTopInventory().getHolder() instanceof MenuHolder holder)) {
            return;
        }

        event.setCancelled(true);

        int slot = event.getRawSlot();
        if (slot < 0 || slot >= event.getView().getTopInventory().getSize()) {
            return;
        }

        MenuDefinition menu = menuRegistry.find(holder.menuId()).orElse(null);
        if (menu == null) {
            if (configManager.isDebugEnabled()) {
                player.sendMessage("§cMenu definition missing for holder: " + holder.menuId());
            }
            return;
        }

        MenuItemDefinition item = holder.getVisibleItem(slot);
        if (item == null) {
            return;
        }

        ButtonCooldownService.CooldownStatus cooldownStatus = cooldownService.checkAndApply(player, menu.id(), item);
        if (!cooldownStatus.allowed()) {
            player.sendMessage(ColorUtil.color(resolveCooldownMessage(player, item, cooldownStatus.remainingMillis())));
            return;
        }

        if (item.isSwitchButton()) {
            boolean toggledOn = switchStateService.toggle(player, menu.id(), item);
            actionExecutor.execute(player, menu.id(), item, menuRenderer, item.actionsForState(toggledOn));
            menuRenderer.openMenu(player, menu.id());
            return;
        }

        actionExecutor.execute(player, menu.id(), item, menuRenderer);
    }

    private String resolveCooldownMessage(Player player, MenuItemDefinition item, long remainingMillis) {
        String template = item.hasCooldownMessage()
                ? item.cooldownMessage()
                : configManager.getMessage("button-cooldown");

        String remainingSeconds = formatRemainingSeconds(remainingMillis);
        String parsedTemplate = placeholderService.parse(player, template);
        return parsedTemplate
                .replace("{seconds}", remainingSeconds)
                .replace("{cooldown}", remainingSeconds)
                .replace("{item}", item.id());
    }

    private String formatRemainingSeconds(long remainingMillis) {
        double seconds = remainingMillis / 1000D;
        String formatted = String.format(Locale.US, "%.1f", seconds);
        if (formatted.endsWith(".0")) {
            return formatted.substring(0, formatted.length() - 2);
        }
        return formatted;
    }
}
