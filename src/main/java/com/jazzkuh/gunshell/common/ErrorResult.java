package com.jazzkuh.gunshell.common;

import com.jazzkuh.gunshell.GunshellPlugin;
import com.jazzkuh.gunshell.utils.ChatUtils;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class ErrorResult {
    private final @Getter boolean disabled;
    private final @Getter boolean developmentFeatures;

    public ErrorResult(boolean disabled, boolean developmentFeatures) {
        this.disabled = disabled;
        this.developmentFeatures = developmentFeatures;
    }

    public void checkStatus(GunshellPlugin plugin, boolean broadcast) {
        if (this.isDisabled()) {
            Bukkit.getLogger().severe(plugin.getDescription().getName() + " v" + plugin.getDescription().getVersion() + " has been blacklisted from this server. Please contact one of the authors for more information.");

            if (broadcast) {
                for (int i = 0; i <= 20; i++) {
                    ChatUtils.sendBroadcast(ChatColor.RESET.toString());
                }
                ChatUtils.sendBroadcast("&4&l" + plugin.getDescription().getName().toUpperCase() + " BLACKLIST");
                ChatUtils.sendBroadcast(ChatColor.RESET.toString());
                ChatUtils.sendBroadcast("&7" + plugin.getDescription().getName() + " v" + plugin.getDescription().getVersion() + " is geblacklist van deze server omdat de desbetreffende server zich niet heeft gehouden aan de terms of service die staan aangegeven op de spigot pagina.");
                ChatUtils.sendBroadcast(ChatColor.RESET.toString());
                ChatUtils.sendBroadcast("&7&oVoor meer informatie neem contact op met een van de authors van deze plugin: " + StringUtils.join(plugin.getDescription().getAuthors(), ", ") + ".");
                ChatUtils.sendBroadcast(ChatColor.RESET.toString());
            }
            plugin.getPluginLoader().disablePlugin(plugin);
        }
    }
}