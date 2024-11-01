package com.jazzkuh.gunshell.common.actions.ammunition;

import com.cryptomorin.xseries.XPotion;
import com.jazzkuh.gunshell.GunshellPlugin;
import com.jazzkuh.gunshell.api.enums.PlayerHitPart;
import com.jazzkuh.gunshell.api.objects.GunshellAmmunition;
import com.jazzkuh.gunshell.api.objects.GunshellFireable;
import com.jazzkuh.gunshell.api.objects.GunshellRayTraceResult;
import com.jazzkuh.gunshell.common.actions.ammunition.abstraction.AbstractAmmunitionAction;
import com.jazzkuh.gunshell.common.configuration.PlaceHolder;
import com.jazzkuh.gunshell.common.configuration.lang.MessagesConfig;
import com.jazzkuh.gunshell.compatibility.CompatibilityManager;
import com.jazzkuh.gunshell.compatibility.extensions.WorldGuardExtension;
import com.jazzkuh.gunshell.utils.PluginUtils;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.codemc.worldguardwrapper.flag.WrappedState;

import java.util.List;

public class DamageAction extends AbstractAmmunitionAction {
    public DamageAction(GunshellFireable fireable, GunshellAmmunition ammunition) {
        super(fireable, ammunition);
    }

    @Override
    public void fireAction(Player player, GunshellRayTraceResult rayTraceResult, ConfigurationSection configuration) {
        if (rayTraceResult.getOptionalLivingEntity().isEmpty()) return;
        LivingEntity livingEntity = rayTraceResult.getOptionalLivingEntity().get();
        if (livingEntity.isDead()) return;
        if (livingEntity.hasMetadata("NPC")) return;

        if (!this.isInMinimumRange(livingEntity, player, getFireable())) return;

        CompatibilityManager compatibilityManager = GunshellPlugin.getInstance().getCompatibilityManager();
        if (compatibilityManager.isExtensionEnabled(CompatibilityManager.Extension.WORLDGUARD)
                && compatibilityManager.getWorldGuardExtension().isFlagState(player, livingEntity.getLocation(),
                WorldGuardExtension.GunshellFlag.GUNSHELL_USE_WEAPONS, WrappedState.DENY)) return;

        if (livingEntity instanceof Player) {
            Player playerTarget = (Player) livingEntity;
            if (playerTarget.getGameMode() == GameMode.SPECTATOR
                    || playerTarget.getGameMode() == GameMode.CREATIVE) return;

            if (compatibilityManager.isExtensionEnabled(CompatibilityManager.Extension.COMBATTAGPLUS)) {
                compatibilityManager.getCombatTagPlusExtension().getTagManager().tag(playerTarget, player);
            }

            MessagesConfig.BULLET_HIT_BY_PLAYER.get(playerTarget,
                    new PlaceHolder("Name", player.getName()));
        }

        if (livingEntity.getLocation().getWorld() != null) {
            livingEntity.getLocation().getWorld().playEffect(livingEntity.getEyeLocation(),
                    Effect.STEP_SOUND, Material.REDSTONE_BLOCK);
        }

        MessagesConfig.BULLET_HIT_OTHER.get(player,
                new PlaceHolder("Name", livingEntity.getName()));

        double damage = 0;
        List<String> actions = this.getFireable().getActions().get(rayTraceResult.getPlayerHitPart());
        for (String action : actions) {
            if (action.toUpperCase().startsWith("DAMAGE")) {
                damage += Integer.parseInt(action.split(":")[1]);
            } else if (action.toUpperCase().startsWith("POTION")) {
                String[] potionInfo = action.split(":");
                XPotion.matchXPotion(potionInfo[1]).ifPresent(potion -> {
                    int level = Integer.parseInt(potionInfo[2]) - 1;
                    int duration = Integer.parseInt(potionInfo[3]);

                    if (potion.getPotionEffectType() == null) return;
                    livingEntity.addPotionEffect(new PotionEffect(potion.getPotionEffectType(), duration * 20, level));
                });
            }
        }

        if (rayTraceResult.getPlayerHitPart() == PlayerHitPart.HEAD) {
            MessagesConfig.BULLET_HIT_OTHER_HEADSHOT.get(player,
                    new PlaceHolder("Name", livingEntity.getName()));
        }

        damage = PluginUtils.getInstance().applyProtectionModifier(damage, rayTraceResult.getPlayerHitPart() == PlayerHitPart.HEAD, livingEntity);
        PluginUtils.getInstance().performRecoil(livingEntity, 0F, this.getFireable().getKnockbackAmount());

        if (damage > livingEntity.getHealth()) {
            livingEntity.setHealth(0D);
        } else {
            EntityDamageByEntityEvent entityDamageByEntityEvent = new EntityDamageByEntityEvent(player, livingEntity,
                    EntityDamageByEntityEvent.DamageCause.ENTITY_ATTACK, damage);
            livingEntity.setHealth(livingEntity.getHealth() - damage);

            livingEntity.setLastDamageCause(entityDamageByEntityEvent);
            Bukkit.getPluginManager().callEvent(entityDamageByEntityEvent);
        }
    }
}
