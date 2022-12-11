package com.jazzkuh.gunshell.common.actions.melee;

import com.cryptomorin.xseries.XPotion;
import com.jazzkuh.gunshell.GunshellPlugin;
import com.jazzkuh.gunshell.api.objects.GunshellMelee;
import com.jazzkuh.gunshell.common.actions.melee.abstraction.AbstractMeleeAction;
import com.jazzkuh.gunshell.compatibility.CompatibilityManager;
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

import java.util.List;

public class MeleeDamageAction extends AbstractMeleeAction {
    public MeleeDamageAction(GunshellMelee melee) {
        super(melee);
    }

    @Override
    public void fireAction(LivingEntity entity, Player player, ConfigurationSection configuration) {
        if (entity.isDead()) return;
        if (entity.hasMetadata("NPC")) return;

        CompatibilityManager compatibilityManager = GunshellPlugin.getInstance().getCompatibilityManager();
        if (entity instanceof Player) {
            Player playerTarget = (Player) entity;
            if (playerTarget.getGameMode() == GameMode.SPECTATOR
                    || playerTarget.getGameMode() == GameMode.CREATIVE) return;

            if (compatibilityManager.isExtensionEnabled(CompatibilityManager.Extension.COMBATTAGPLUS)) {
                compatibilityManager.getCombatTagPlusExtension().getTagManager().tag(playerTarget, player);
            }
        }

        if (entity.getLocation().getWorld() != null) {
            entity.getLocation().getWorld().playEffect(entity.getEyeLocation(),
                    Effect.STEP_SOUND, Material.REDSTONE_BLOCK);
        }



        double damage = 0;
        List<String> actions = this.getMelee().getActions();
        for (String action : actions) {
            if (action.toUpperCase().startsWith("DAMAGE")) {
                damage += Integer.parseInt(action.split(":")[1]);
            } else if (action.toUpperCase().startsWith("POTION")) {
                String[] potionInfo = action.split(":");
                XPotion.matchXPotion(potionInfo[1]).ifPresent(potion -> {
                    int level = Integer.parseInt(potionInfo[2]);
                    int duration = Integer.parseInt(potionInfo[3]);

                    if (potion.getPotionEffectType() == null) return;
                    player.addPotionEffect(new PotionEffect(potion.getPotionEffectType(), duration, level));
                });
            }
        }

        damage = PluginUtils.getInstance().applyProtectionModifier(damage, false, entity);

        if (damage > entity.getHealth()) {
            entity.setHealth(0D);
        } else {
            EntityDamageByEntityEvent entityDamageByEntityEvent = new EntityDamageByEntityEvent(player, entity,
                    EntityDamageByEntityEvent.DamageCause.ENTITY_ATTACK, damage);
            entity.setHealth(entity.getHealth() - damage);

            entity.setLastDamageCause(entityDamageByEntityEvent);
            Bukkit.getPluginManager().callEvent(entityDamageByEntityEvent);
        }
    }
}
