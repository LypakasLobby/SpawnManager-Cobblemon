package com.lypaka.spawnmanager.Utils.SpawnerUtils;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.lypaka.lypakautils.LypakaUtils;
import com.lypaka.spawnmanager.SpawnManager;
import com.lypaka.spawnmanager.Spawners.CaveSpawner;
import com.lypaka.spawnmanager.Spawners.GrassSpawner;
import com.lypaka.spawnmanager.Spawners.NaturalSpawner;
import com.lypaka.spawnmanager.Spawners.SurfSpawner;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class DespawnChecker {

    public static void startChecker() {

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {

                SpawnManager.spawnerLogicThread.execute(() -> {

                    NaturalSpawner.playerPokemonCount.entrySet().removeIf(entry -> {

                        UUID playerUUID = entry.getKey();
                        ServerPlayerEntity player = LypakaUtils.playerMap.getOrDefault(playerUUID, null);
                        if (player == null) {

                            return true;

                        }
                        List<PokemonEntity> entities = entry.getValue();
                        entities.removeIf(entity -> !entity.isAlive());

                        return entities.isEmpty();

                    });
                    GrassSpawner.playerPokemonCount.entrySet().removeIf(entry -> {

                        UUID playerUUID = entry.getKey();
                        ServerPlayerEntity player = LypakaUtils.playerMap.getOrDefault(playerUUID, null);
                        if (player == null) {

                            return true;

                        }
                        List<PokemonEntity> entities = entry.getValue();
                        entities.removeIf(entity -> !entity.isAlive());

                        return entities.isEmpty();

                    });
                    CaveSpawner.playerPokemonCount.entrySet().removeIf(entry -> {

                        UUID playerUUID = entry.getKey();
                        ServerPlayerEntity player = LypakaUtils.playerMap.getOrDefault(playerUUID, null);
                        if (player == null) {

                            return true;

                        }
                        List<PokemonEntity> entities = entry.getValue();
                        entities.removeIf(entity -> !entity.isAlive());

                        return entities.isEmpty();

                    });
                    SurfSpawner.playerPokemonCount.entrySet().removeIf(entry -> {

                        UUID playerUUID = entry.getKey();
                        ServerPlayerEntity player = LypakaUtils.playerMap.getOrDefault(playerUUID, null);
                        if (player == null) {

                            return true;

                        }
                        List<PokemonEntity> entities = entry.getValue();
                        entities.removeIf(entity -> !entity.isAlive());

                        return entities.isEmpty();

                    });

                });

            }

        }, 0, 1000L);

    }

}
