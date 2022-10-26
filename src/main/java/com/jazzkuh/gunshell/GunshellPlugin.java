package com.jazzkuh.gunshell;

import com.jazzkuh.gunshell.api.enums.PlayerTempModification;
import com.jazzkuh.gunshell.common.WeaponRegistry;
import com.jazzkuh.gunshell.common.commands.GunshellCMD;
import com.jazzkuh.gunshell.common.configuration.DefaultConfig;
import com.jazzkuh.gunshell.common.configuration.lang.MessagesConfig;
import com.jazzkuh.gunshell.common.listeners.*;
import com.jazzkuh.gunshell.compatibility.CompatibilityLayer;
import com.jazzkuh.gunshell.compatibility.CompatibilityManager;
import com.jazzkuh.gunshell.utils.PluginUtils;
import com.jazzkuh.gunshell.utils.config.ConfigurationFile;
import com.jazzkuh.gunshell.utils.license.LicenseVerification;
import com.jazzkuh.gunshell.utils.license.PremiumResult;
import de.slikey.effectlib.EffectManager;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class GunshellPlugin extends JavaPlugin {

    private static @Getter @Setter(AccessLevel.PRIVATE) GunshellPlugin instance;
    private static @Getter ConfigurationFile messages;
    private @Getter @Setter(AccessLevel.PRIVATE) EffectManager effectManager;
    private @Getter @Setter(AccessLevel.PRIVATE) WeaponRegistry weaponRegistry;
    private @Getter @Setter(AccessLevel.PRIVATE) CompatibilityManager compatibilityManager;
    private @Getter @Setter(AccessLevel.PRIVATE) CompatibilityLayer compatibilityLayer;
    private @Getter @Setter HashMap<String, Long> weaponCooldownMap = new HashMap<>();
    private @Getter @Setter HashMap<UUID, Long> grabCooldownMap = new HashMap<>();
    private @Getter @Setter HashMap<String, Long> meleeCooldownMap = new HashMap<>();
    private @Getter @Setter HashMap<UUID, Long> meleeGrabCooldownMap = new HashMap<>();
    private @Getter @Setter HashMap<UUID, Long> throwableCooldownMap = new HashMap<>();
    private @Getter @Setter HashMap<UUID, PlayerTempModification> modifiedPlayerMap = new HashMap<>();
    private @Getter @Setter Set<UUID> reloadingSet = new HashSet<>();
    private @Getter @Setter Set<Block> undoList = new HashSet<>();
    private @Getter @Setter HashMap<ArmorStand, Integer> activeThrowables = new HashMap<>();
    private @Getter @Setter LicenseVerification licenseVerification;
    private @Getter @Setter PremiumResult premiumResult;

    @Override
    public void onLoad() {
        setCompatibilityManager(new CompatibilityManager());
        this.getCompatibilityManager().registerExtensions();
        this.getCompatibilityManager().loadExtensions();
    }

    @Override
    public void onEnable() {
        setInstance(this);

        setEffectManager(new EffectManager(this));
        setCompatibilityLayer(this.getCompatibilityManager().getCompatibilityLayer());
        new PluginUtils();

        this.getCompatibilityManager().enableExtensions();

        DefaultConfig.init();
        messages = new ConfigurationFile(this, "messages.yml", false);
        MessagesConfig.init();
        messages.saveConfig();

//        this.licenseVerification = new LicenseVerification(URI.create("https://premium.gunshell.nl/api/premium"), DefaultConfig.LICENSE_KEY.asString(), this.getDescription().getVersion(), "lesnmtubypejipnnlontopxjlbbkirdtafaueymxna");
//        this.premiumResult = licenseVerification.check();
//        this.premiumResult.checkStatus(this);
//        if (!premiumResult.isAuthenticated()) return;

        setWeaponRegistry(new WeaponRegistry(this));
        this.weaponRegistry.registerFireables("weapons", "builtin.yml");
        this.weaponRegistry.registerAmmunition("ammunition", "builtin.yml");
        this.weaponRegistry.registerThrowables("throwables", "builtin.yml");
        this.weaponRegistry.registerMelees("melee", "builtin.yml");

        new GunshellCMD().register(this);

        Bukkit.getPluginManager().registerEvents(new PlayerInteractListener(), this);
        Bukkit.getPluginManager().registerEvents(new FireablePreFireListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerItemHeldListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerSwapHandListener(), this);
        Bukkit.getPluginManager().registerEvents(new ProjectileHitListener(), this);
        Bukkit.getPluginManager().registerEvents(new ThrowablePreFireListener(), this);
        Bukkit.getPluginManager().registerEvents(new EntityDamageByEntityListener(), this);
        Bukkit.getPluginManager().registerEvents(new FireableToggleScopeListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerRestoreModifiedListener(), this);
        Bukkit.getPluginManager().registerEvents(new AsyncPlayerChatListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerArmorStandManipulateListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(), this);

        this.getLogger().info(this.getDescription().getName() + " v" + this.getDescription().getVersion() + " has been enabled!");
//        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
//            this.premiumResult = licenseVerification.check();
//
//            Bukkit.getScheduler().runTask(GunshellPlugin.getInstance(), () -> this.premiumResult.checkStatus(this));
//        }, 10 * 60 * 20, 10 * 60 * 20);
    }

    @Override
    public void onDisable() {
        this.getCompatibilityManager().disableExtensions();
        for (ArmorStand armorStand : this.activeThrowables.keySet()) {
            Bukkit.getScheduler().cancelTask(this.activeThrowables.get(armorStand));
            armorStand.remove();
        }

        for (Block block : this.undoList) {
            block.setType(Material.AIR);
        }

        this.getLogger().info(this.getDescription().getName() + " v" + this.getDescription().getVersion() + " has been disabled!");
    }
}
