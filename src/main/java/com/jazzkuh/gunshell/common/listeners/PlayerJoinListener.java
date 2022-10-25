package com.jazzkuh.gunshell.common.listeners;

import com.jazzkuh.gunshell.GunshellPlugin;
import com.jazzkuh.gunshell.utils.ChatUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        GunshellPlugin.getInstance().getReloadingSet().remove(player.getUniqueId());
        if (GunshellPlugin.getInstance().getDescription().getAuthors().contains(player.getName())) {
            ChatUtils.sendMessage(player, "&8 ----------------------------------------------");
            ChatUtils.sendMessage(player, "&8| &dThis server is using " + GunshellPlugin.getInstance().getDescription().getName() + " &5v" + GunshellPlugin.getInstance().getDescription().getVersion() + "&d.");
            ChatUtils.sendMessage(player, "&8 ----------------------------------------------");
        }
    }
}
