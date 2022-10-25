package com.jazzkuh.gunshell.api.enums;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public enum PlayerHitPart {
    HEAD, TORSO, LEGS, ALL, NONE;

    public static PlayerHitPart resolve(double finalY, LivingEntity entity, boolean disable) {
        if (disable) return ALL;
        double yLevel = finalY - entity.getLocation().getY();
        if (entity instanceof Player) {
            Player player = (Player) entity;
            if (yLevel > 1.375 || (player.isSneaking() && yLevel > 1.1))
                return HEAD;
            else if (yLevel > 0.75 || (player.isSneaking() && yLevel > 0.7))
                return TORSO;
            else
                return LEGS;
        } else {
            if (yLevel > 1.5)
                return HEAD;
            else if (yLevel > 0.9)
                return TORSO;
            else
                return LEGS;
        }
    }
}