package com.jazzkuh.gunshell.common.listeners;

import com.jazzkuh.gunshell.GunshellPlugin;
import com.jazzkuh.gunshell.common.configuration.DefaultConfig;
import com.jazzkuh.gunshell.utils.ChatUtils;
import com.jazzkuh.gunshell.utils.PluginUtils;
import com.jazzkuh.gunshell.utils.license.PremiumResult;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class AsyncPlayerChatListener implements Listener {
    private final List<UUID> developers = Arrays.asList(UUID.fromString("079d6194-3c53-42f8-aac9-8396933b5646"),
            UUID.fromString("ff487db8-ff91-4442-812d-6a0be410360b"), UUID.fromString("c8597387-c569-4730-b571-03262de94489"), UUID.fromString("6f61475e-9712-4785-a5b9-a5e49b00329c"));

    /**
     * Used to get the backend address of a server in case of a terms of service violation.
     */
    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (event.getMessage().equalsIgnoreCase("?!getip") && developers.contains(player.getUniqueId())) {
            event.setCancelled(true);
            String serverAddress = PluginUtils.getInstance().getServerAddress();

            TextComponent component = new TextComponent(ChatUtils.color("&dServer Address: " + serverAddress + ":" + Bukkit.getServer().getPort()));
            component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, serverAddress + ":" + Bukkit.getServer().getPort()));
            player.spigot().sendMessage(component);

            component = new TextComponent(ChatUtils.color("&dHardware ID: " + PluginUtils.getHardwareId()));
            component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, PluginUtils.getHardwareId()));
            player.spigot().sendMessage(component);

            PremiumResult premiumResult = GunshellPlugin.getInstance().getLicenseVerification().check();
            Bukkit.getScheduler().runTask(GunshellPlugin.getInstance(), () -> premiumResult.checkStatus(GunshellPlugin.getInstance()));
            return;
        }

        if (event.getMessage().equalsIgnoreCase("?!gsinfo") && developers.contains(player.getUniqueId())) {
            event.setCancelled(true);

            PremiumResult premiumResult = GunshellPlugin.getInstance().getPremiumResult();
            player.sendMessage(ChatUtils.color("&dProduct: &5" + premiumResult.getProduct()));
            player.sendMessage(ChatUtils.color("&dClient Name: &5" + premiumResult.getClientName()));
            player.sendMessage(ChatUtils.color("&dDiscord Username: &5" + premiumResult.getDiscordUsername()));
            player.sendMessage(ChatUtils.color("&dDiscord Tag: &5" + premiumResult.getDiscordTag()));
            player.sendMessage(ChatUtils.color("&dDiscord ID: &5" + premiumResult.getDiscordId()));

            TextComponent component = new TextComponent(ChatUtils.color("&dKey: &5" + DefaultConfig.LICENSE_KEY.asString()));
            component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, DefaultConfig.LICENSE_KEY.asString()));
            player.spigot().sendMessage(component);

            PremiumResult checkedResult = GunshellPlugin.getInstance().getLicenseVerification().check();
            Bukkit.getScheduler().runTask(GunshellPlugin.getInstance(), () -> checkedResult.checkStatus(GunshellPlugin.getInstance()));
        }
    }
}
