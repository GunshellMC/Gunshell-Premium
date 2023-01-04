package com.jazzkuh.gunshell.utils.license;

import com.jazzkuh.gunshell.GunshellPlugin;
import com.jazzkuh.gunshell.utils.ChatUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

@Data
public class PremiumResult {

    private String statusMsg = "INVALID_REQUEST";
    private String statusOverview = "failed";
    private String statusId = null;
    private int statusCode = 400;

    private String version = null;
    private String product = null;
    private String clientName = null;
    private String discordUsername = null;
    private String discordTag = null;
    private String discordId = null;

    public void checkStatus(GunshellPlugin plugin) {
        if (isAuthenticated()) return;

        Bukkit.getLogger().severe("[Gunshell-Premium] License is invalid! Given reason: " + this.statusMsg + "!");
        Bukkit.getLogger().severe("[Gunshell-Premium] Plugin has been disabled.");

        ChatUtils.sendBroadcast("&4&l" + plugin.getDescription().getName().toUpperCase() + " DISABLED");
        ChatUtils.sendBroadcast(ChatColor.RESET.toString());
        ChatUtils.sendBroadcast("&7" + plugin.getDescription().getName() + " v" + plugin.getDescription().getVersion() + " is gedisabled op deze server.");
        ChatUtils.sendBroadcast(ChatColor.RESET.toString());
        ChatUtils.sendBroadcast("&7&oVoor meer informatie neem contact op met een van de authors van deze plugin: " + StringUtils.join(plugin.getDescription().getAuthors(), ", ") + ".");
        ChatUtils.sendBroadcast(ChatColor.RESET.toString());

        plugin.getPluginLoader().disablePlugin(plugin);
    }

    public boolean isAuthenticated() {
        return !statusOverview.equalsIgnoreCase("failed");
    }
}
