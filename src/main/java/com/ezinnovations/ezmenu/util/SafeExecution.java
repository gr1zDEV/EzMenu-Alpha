package com.ezinnovations.ezmenu.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public final class SafeExecution {

    private final Plugin plugin;

    public SafeExecution(Plugin plugin) {
        this.plugin = plugin;
    }

    public void dispatchPlayerCommand(Player player, String command) {
        runForPlayer(player, () -> Bukkit.dispatchCommand(player, command));
    }

    public void dispatchConsoleCommand(String command) {
        runGlobal(() -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
    }

    public void runForPlayer(Player player, Runnable task) {
        try {
            // Folia requires player-affecting work, including player command dispatch,
            // to execute on the owning entity scheduler instead of an arbitrary thread.
            player.getScheduler().run(plugin, scheduledTask -> task.run(), null);
        } catch (NoSuchMethodError ignored) {
            Bukkit.getScheduler().runTask(plugin, task);
        }
    }

    public void runForPlayerLater(Player player, Runnable task, long delayTicks) {
        try {
            // Re-opening a menu still touches player state, so keep delayed refreshes on
            // the player's scheduler when Folia APIs are available.
            player.getScheduler().runDelayed(plugin, scheduledTask -> task.run(), null, delayTicks);
        } catch (NoSuchMethodError ignored) {
            Bukkit.getScheduler().runTaskLater(plugin, task, delayTicks);
        }
    }

    public void runGlobal(Runnable task) {
        try {
            // Console command execution does not belong to a player region, so Folia allows
            // it on the global region scheduler. Fall back to the Bukkit main thread elsewhere.
            Bukkit.getGlobalRegionScheduler().execute(plugin, task);
        } catch (NoSuchMethodError ignored) {
            Bukkit.getScheduler().runTask(plugin, task);
        }
    }
}
