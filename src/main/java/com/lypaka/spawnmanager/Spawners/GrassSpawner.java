package com.lypaka.spawnmanager.Spawners;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.CobblemonEntities;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.battles.BattleBuilder;
import com.cobblemon.mod.common.battles.BattleRegistry;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.lypaka.areamanager.Areas.Area;
import com.lypaka.areamanager.Areas.AreaHandler;
import com.lypaka.areamanager.Regions.RegionHandler;
import com.lypaka.lypakautils.API.PlayerLandMovementCallback;
import com.lypaka.lypakautils.Handlers.FancyTextHandler;
import com.lypaka.lypakautils.Handlers.RandomHandler;
import com.lypaka.lypakautils.Handlers.WorldTimeHandler;
import com.lypaka.spawnmanager.Listeners.TickListener;
import com.lypaka.spawnmanager.SpawnAreas.SpawnArea;
import com.lypaka.spawnmanager.SpawnAreas.SpawnAreaHandler;
import com.lypaka.spawnmanager.SpawnAreas.Spawns.AreaSpawns;
import com.lypaka.spawnmanager.SpawnAreas.Spawns.GrassSpawn;
import com.lypaka.spawnmanager.SpawnAreas.Spawns.PokemonSpawn;
import com.lypaka.spawnmanager.SpawnManager;
import com.lypaka.spawnmanager.Utils.BattleUtils;
import com.lypaka.spawnmanager.Utils.ExternalAbilities.*;
import com.lypaka.spawnmanager.Utils.HeldItemUtils;
import com.lypaka.spawnmanager.Utils.MiscUtils;
import com.lypaka.spawnmanager.Utils.PokemonSpawnBuilder;
import com.lypaka.spawnmanager.Utils.SpawnerUtils.GeneratedSpawn;
import com.lypaka.spawnmanager.Utils.SpawnerUtils.SpawnGenerator;
import kotlin.Unit;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class GrassSpawner implements PlayerLandMovementCallback {

    public static List<UUID> spawnedPokemonUUIDs = new ArrayList<>(); // used for battle end event listener to check for to despawn Pokemon or not
    public static Map<UUID, List<PokemonEntity>> playerPokemonCount = new HashMap<>();

    @Override
    public void onPlayerMove (ServerPlayerEntity player, int steps) {

        if (!player.isCreative() && !player.isSpectator()) {

            if (TickListener.timeBetweenGrassSpawns.containsKey(player.getUuid())) return;
            TickListener.timeBetweenGrassSpawns.put(player.getUuid(), 0);
            int x = player.getBlockPos().getX();
            int y = player.getBlockPos().getY();
            int z = player.getBlockPos().getZ();
            World world = player.getWorld();
            BlockPos playerPos = player.getBlockPos();
            BlockState state = world.getBlockState(playerPos);
            String blockID = Registries.BLOCK.getId(state.getBlock()).toString();
            List<Area> areas = AreaHandler.getSortedAreas(x, y, z, world);
            if (areas.isEmpty()) return;

            Map<Area, List<UUID>> p = AreaHandler.playersInArea.get(RegionHandler.getRegionAtPlayer(player).getName());
            for (Area area : areas) {

                SpawnArea currentSpawnArea = SpawnAreaHandler.areaMap.get(area);
                if (currentSpawnArea.getGrassSpawnerSettings().getBlockIDs().contains(blockID)) {

                    if (currentSpawnArea.getGrassSpawnerSettings().doesAutoBattle() && BattleRegistry.INSTANCE.getBattleByParticipatingPlayer(player) != null) break;
                    if (currentSpawnArea.getGrassSpawnerSettings().doesAutoBattle() && !MiscUtils.canPlayerBattle(player)) break;
                    if (p.containsKey(area)) {

                        List<UUID> uuids = p.get(area);
                        List<PokemonEntity> playersSpawnedPokemon = new ArrayList<>();
                        if (uuids.contains(player.getUuid())) {

                            AtomicInteger count = new AtomicInteger(0);
                            for (UUID u : uuids) {

                                if (u.toString().equalsIgnoreCase(player.getUuid().toString())) {

                                    if (playerPokemonCount.containsKey(u)) {

                                        count.set(playerPokemonCount.get(u).size());
                                        playersSpawnedPokemon = playerPokemonCount.get(u);

                                    }
                                    break;

                                }

                            }

                            if (count.get() >= currentSpawnArea.getGrassSpawnerSettings().getSpawnLimit()) continue;

                            List<PokemonEntity> finalPlayersSpawnedPokemon = playersSpawnedPokemon;
                            SpawnManager.spawnerLogicThread.execute(() -> {

                                List<GeneratedSpawn> toSpawn = SpawnGenerator.generateGrassSpawn(area, player);
                                if (toSpawn.isEmpty()) return;
                                if (!currentSpawnArea.getGrassSpawnerSettings().doesAutoBattle()) {

                                    player.getServer().executeSync(() -> {

                                        for (GeneratedSpawn spawn : toSpawn) {

                                            if (spawn == null) continue;
                                            Pokemon poke = spawn.getPokemon();
                                            BlockPos pos = spawn.getSpawnLocation();
                                            int spawnX = pos.getX();
                                            int spawnY = pos.getY();
                                            int spawnZ = pos.getZ();
                                            List<Area> areasAtSpawn = AreaHandler.getFromLocation(spawnX, spawnY, spawnZ, player.getWorld());
                                            if (areasAtSpawn.isEmpty()) continue;
                                            PokemonEntity entity = new PokemonEntity(player.getWorld(), poke, CobblemonEntities.POKEMON);
                                            entity.setPosition(spawnX, spawnY + 1.5, spawnZ);
                                            player.getWorld().spawnEntity(entity);
                                            finalPlayersSpawnedPokemon.add(entity);
                                            count.getAndIncrement();
                                            if (count.get() >= currentSpawnArea.getGrassSpawnerSettings().getSpawnLimit()) {

                                                break;

                                            }
                                            playerPokemonCount.put(player.getUuid(), finalPlayersSpawnedPokemon);
                                            if (currentSpawnArea.getGrassSpawnerSettings().doesDespawnAfterBattle()) {

                                                spawnedPokemonUUIDs.add(entity.getUuid());

                                            }
                                            String messageType = "";
                                            if (poke.getShiny()) {

                                                messageType = "-Shiny";

                                            }
                                            messageType = "Spawn-Message" + messageType;
                                            String message = currentSpawnArea.getGrassSpawnerSettings().getMessagesMap().get(messageType);
                                            if (!message.equalsIgnoreCase("")) {

                                                player.sendMessage(FancyTextHandler.getFormattedText(message.replace("%pokemon%", poke.getSpecies().getName())), true);

                                            }

                                        }

                                    });

                                } else {

                                    player.getServer().executeSync(() -> {

                                        GeneratedSpawn spawn = RandomHandler.getRandomElementFromList(toSpawn);
                                        if (spawn == null) return;
                                        Pokemon poke = spawn.getPokemon();
                                        BlockPos pos = player.getBlockPos();
                                        int spawnX = pos.getX();
                                        int spawnY = pos.getY();
                                        int spawnZ = pos.getZ();
                                        PokemonEntity entity = new PokemonEntity(player.getWorld(), poke, CobblemonEntities.POKEMON);
                                        entity.setPosition(spawnX, spawnY + 1.5, spawnZ);
                                        player.getWorld().spawnEntity(entity);
                                        finalPlayersSpawnedPokemon.add(entity);
                                        count.getAndIncrement();
                                        playerPokemonCount.put(player.getUuid(), finalPlayersSpawnedPokemon);
                                        if (currentSpawnArea.getGrassSpawnerSettings().doesDespawnAfterBattle()) {

                                            spawnedPokemonUUIDs.add(entity.getUuid());

                                        }
                                        String messageType = "";
                                        if (poke.getShiny()) {

                                            messageType = "-Shiny";

                                        }
                                        messageType = "Spawn-Message" + messageType;
                                        String message = currentSpawnArea.getGrassSpawnerSettings().getMessagesMap().get(messageType);
                                        if (!message.equalsIgnoreCase("")) {

                                            player.sendMessage(FancyTextHandler.getFormattedText(message.replace("%pokemon%", poke.getSpecies().getName())), true);

                                        }
                                        Timer t = new Timer();
                                        t.schedule(new TimerTask() {

                                            @Override
                                            public void run() {

                                                if (BattleRegistry.INSTANCE.getBattleByParticipatingPlayer(player) == null) {

                                                    BattleBuilder.INSTANCE.pve(player, entity, null).ifSuccessful(function -> {

                                                        BattleUtils.autoBattlePlayerUUIDs.add(player.getUuid());
                                                        return Unit.INSTANCE;

                                                    });

                                                }
                                            }

                                        }, 10);

                                    });

                                }

                            });

                        }

                    }

                }

            }

        }

    }

}
