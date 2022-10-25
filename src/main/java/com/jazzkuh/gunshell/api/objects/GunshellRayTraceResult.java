package com.jazzkuh.gunshell.api.objects;

import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;

import java.util.Optional;

public class GunshellRayTraceResult {
    private final @Getter Optional<LivingEntity> optionalLivingEntity;
    private final @Getter Optional<Block> optionalBlock;
    private final @Getter BlockFace blockFace;
    private final @Getter boolean headshot;

    public GunshellRayTraceResult(Optional<LivingEntity> optionalLivingEntity, Optional<Block> optionalBlock, BlockFace blockFace, boolean headshot) {
        this.optionalLivingEntity = optionalLivingEntity;
        this.optionalBlock = optionalBlock;
        this.blockFace = blockFace;
        this.headshot = headshot;
    }

    public String toString() {
        return "GunshellRayTraceResult{" +
                "optionalLivingEntity=" + optionalLivingEntity +
                ", optionalBlock=" + optionalBlock +
                ", blockFace=" + blockFace +
                ", headshot=" + headshot +
                '}';
    }
}
