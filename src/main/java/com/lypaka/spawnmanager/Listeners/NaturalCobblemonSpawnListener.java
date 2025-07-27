package com.lypaka.spawnmanager.Listeners;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.lypaka.areamanager.Areas.Area;
import com.lypaka.areamanager.Areas.AreaHandler;
import com.lypaka.spawnmanager.SpawnAreas.SpawnArea;
import com.lypaka.spawnmanager.SpawnAreas.SpawnAreaHandler;
import com.lypaka.spawnmanager.Spawners.FishSpawner;
import kotlin.Unit;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import java.util.List;

public class NaturalCobblemonSpawnListener {

    public static void register() {

        // ENTITY_SPAWN is cancelable, POKEMON_ENTITY_SPAWN is not because #logic
        CobblemonEvents.POKEMON_ENTITY_SPAWN.subscribe(Priority.NORMAL, event -> {

            PokemonEntity entity = event.getEntity();
            if (entity.getOwner() != null) return Unit.INSTANCE;
            World world = entity.getWorld();
            int x = entity.getBlockX();
            int y = entity.getBlockY();
            int z = entity.getBlockZ();

            List<Area> areas = AreaHandler.getFromLocation(x, y, z, world);
            for (Area area : areas) {

                SpawnArea spawnArea = SpawnAreaHandler.areaMap.get(area);
                if (spawnArea.getNaturalSpawnerSettings().doesPreventCobblemonSpawns()) {

                    Pokemon pokemon = entity.getPokemon();
                    if (!pokemon.getPersistentData().contains("SpawnManagerSpawn")) {

                        entity.remove(Entity.RemovalReason.DISCARDED);
                        FishSpawner.fishSpawnerMap.entrySet().removeIf(e -> e.getKey().toString().equalsIgnoreCase(entity.getUuid().toString()));
                        return Unit.INSTANCE;

                    }

                }

            }

            return Unit.INSTANCE;

        });

        // Commenting this out instead of removing it in the event I might need it later
        /*CobblemonEvents.ENTITY_SPAWN.subscribe(Priority.NORMAL, event -> {

            if (event.getEntity() instanceof PokemonEntity) {

                PokemonEntity entity = (PokemonEntity) event.getEntity();
                if (entity.getOwner() != null) return Unit.INSTANCE;
                World world = entity.getWorld();
                int x = entity.getBlockX();
                int y = entity.getBlockY();
                int z = entity.getBlockZ();

                List<Area> areas = AreaHandler.getFromLocation(x, y, z, world);
                for (Area area : areas) {

                    SpawnArea spawnArea = SpawnAreaHandler.areaMap.get(area);
                    if (spawnArea.getNaturalSpawnerSettings().doesPreventCobblemonSpawns()) {

                        Pokemon pokemon = entity.getPokemon();
                        if (!pokemon.getPersistentData().contains("SpawnManagerSpawn")) {

                            event.cancel();
                            entity.remove(Entity.RemovalReason.DISCARDED);
                            FishSpawner.fishSpawnerMap.entrySet().removeIf(e -> e.getKey().toString().equalsIgnoreCase(entity.getUuid().toString()));
                            return Unit.INSTANCE;

                        }

                    }

                }

            }

            return Unit.INSTANCE;

        });*/

    }
}
