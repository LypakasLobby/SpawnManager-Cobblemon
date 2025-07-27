package com.lypaka.spawnmanager.Utils.SpawnerUtils;

import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class GeneratedSpawn {

    private final Pokemon pokemon;
    private final BlockPos spawnLocation;

    public GeneratedSpawn (Pokemon pokemon, @Nullable BlockPos spawnLocation) {

        this.pokemon = pokemon;
        this.spawnLocation = spawnLocation;

    }

    public Pokemon getPokemon() {

        return this.pokemon;

    }

    public BlockPos getSpawnLocation() {

        return this.spawnLocation;

    }

}
