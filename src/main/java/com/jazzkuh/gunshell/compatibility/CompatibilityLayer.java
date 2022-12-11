package com.jazzkuh.gunshell.compatibility;

import com.jazzkuh.gunshell.api.objects.GunshellRayTraceResult;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public interface CompatibilityLayer {
    GunshellRayTraceResult performRayTrace(LivingEntity player, Vector direction, double range);
    String getRayTraceResult(Player player, int range);
    void showEndCreditScene(Player player);
    void showDemoMenu(Player player);
    void sendPumpkinEffect(Player player, boolean forRemoval);
    void displayDestroyStage(Block block, int stage);
    boolean isPassable(Block block);
}
