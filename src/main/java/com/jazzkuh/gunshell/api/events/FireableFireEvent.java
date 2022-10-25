package com.jazzkuh.gunshell.api.events;

import com.jazzkuh.gunshell.api.objects.GunshellFireable;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class FireableFireEvent extends Event implements Cancellable {
    private boolean cancelled;
    private final @Getter Player player;
    private final @Getter GunshellFireable fireable;
    private static final HandlerList handlers = new HandlerList();

    public FireableFireEvent(Player player, @NotNull GunshellFireable fireable) {
        this.player = player;
        this.fireable = fireable;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}