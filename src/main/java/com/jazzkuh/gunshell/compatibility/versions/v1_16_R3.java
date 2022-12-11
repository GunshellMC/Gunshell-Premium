package com.jazzkuh.gunshell.compatibility.versions;

import com.cryptomorin.xseries.XMaterial;
import com.jazzkuh.gunshell.api.enums.PlayerHitPart;
import com.jazzkuh.gunshell.api.objects.GunshellRayTraceResult;
import com.jazzkuh.gunshell.common.configuration.DefaultConfig;
import com.jazzkuh.gunshell.compatibility.CompatibilityLayer;
import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.PacketPlayOutBlockBreakAnimation;
import net.minecraft.server.v1_16_R3.PacketPlayOutGameStateChange;
import net.minecraft.server.v1_16_R3.PacketPlayOutSetSlot;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class v1_16_R3 implements CompatibilityLayer {
    @Override
    public GunshellRayTraceResult performRayTrace(LivingEntity player, Vector direction, double range) {
        RayTraceResult result = player.getWorld()
                .rayTrace(player.getEyeLocation(), direction, range, FluidCollisionMode.NEVER, true, DefaultConfig.HITBOX_INCREASE.asDouble(), entity ->
                        entity != player);
        if (result == null) {
            return new GunshellRayTraceResult(Optional.empty(), Optional.empty(), null, PlayerHitPart.NONE);
        }

        if (result.getHitBlock() != null) {
            return new GunshellRayTraceResult(Optional.empty(), Optional.of(result.getHitBlock()), result.getHitBlockFace(), PlayerHitPart.NONE);
        }

        if (result.getHitEntity() == null) {
            return new GunshellRayTraceResult(Optional.empty(), Optional.empty(), null, PlayerHitPart.NONE);
        }

        Entity entity = result.getHitEntity();
        if (!(entity instanceof LivingEntity) || entity instanceof ArmorStand) {
            return new GunshellRayTraceResult(Optional.empty(), Optional.empty(), null, PlayerHitPart.NONE);
        }
        LivingEntity livingEntity = (LivingEntity) entity;
        PlayerHitPart playerHitPart = PlayerHitPart.resolve(result.getHitPosition().getY(), livingEntity, false);
        return new GunshellRayTraceResult(Optional.of(livingEntity), Optional.empty(), null, playerHitPart);
    }

    @Override
    public String getRayTraceResult(Player player, int range) {
        RayTraceResult result = player.getWorld()
                .rayTrace(player.getEyeLocation(), player.getLocation().getDirection(), range, FluidCollisionMode.NEVER, true, DefaultConfig.HITBOX_INCREASE.asDouble(), null);
        return result != null ? result.toString() : "No result found";
    }

    @Override
    public void showEndCreditScene(Player player) {
        PacketPlayOutGameStateChange gameStateChange = new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.e, 1f);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(gameStateChange);
    }

    @Override
    public void showDemoMenu(Player player) {
        PacketPlayOutGameStateChange gameStateChange = new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.f, 0f);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(gameStateChange);
    }

    @Override
    public void sendPumpkinEffect(Player player, boolean forRemoval) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        org.bukkit.inventory.ItemStack itemStack = XMaterial.AIR.parseItem();
        if (!forRemoval) {
            itemStack = XMaterial.PUMPKIN.parseItem();
        }

        craftPlayer.getHandle().playerConnection.sendPacket(new PacketPlayOutSetSlot(0, 5,
                CraftItemStack.asNMSCopy(itemStack)));
    }

    @Override
    public boolean isPassable(Block block) {
        return block.isPassable();
    }
}
