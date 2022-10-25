package com.jazzkuh.gunshell.api.interfaces;

import com.jazzkuh.gunshell.utils.ItemBuilder;
import org.bukkit.inventory.ItemStack;

public interface GunshellWeaponImpl {
    ItemBuilder getItem(int durability);
    ItemStack getItemStack(int durability);
}
