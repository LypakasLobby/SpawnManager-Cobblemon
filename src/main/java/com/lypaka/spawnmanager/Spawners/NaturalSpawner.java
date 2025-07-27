package com.lypaka.spawnmanager.Spawners;

import com.cobblemon.mod.common.CobblemonEntities;
import com.cobblemon.mod.common.battles.BattleBuilder;
import com.cobblemon.mod.common.battles.BattleRegistry;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.lypaka.areamanager.Areas.Area;
import com.lypaka.areamanager.Areas.AreaHandler;
import com.lypaka.areamanager.Regions.Region;
import com.lypaka.lypakautils.Handlers.FancyTextHandler;
import com.lypaka.lypakautils.Handlers.RandomHandler;
import com.lypaka.lypakautils.LypakaUtils;
import com.lypaka.spawnmanager.SpawnAreas.SpawnArea;
import com.lypaka.spawnmanager.SpawnAreas.SpawnAreaHandler;
import com.lypaka.spawnmanager.SpawnManager;
import com.lypaka.spawnmanager.Utils.BattleUtils;
import com.lypaka.spawnmanager.Utils.SpawnerUtils.GeneratedSpawn;
import com.lypaka.spawnmanager.Utils.SpawnerUtils.SpawnGenerator;
import kotlin.Unit;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class NaturalSpawner {

    private static Timer timer = null;
    private static final Map<Area, Integer> spawnAttemptMap = new HashMap<>();
    public static Map<Area, Map<UUID, List<PokemonEntity>>> pokemonSpawnedMap = new HashMap<>();
    public static List<UUID> spawnedPokemonUUIDs = new ArrayList<>(); // used for battle end event listener to check for to despawn Pokemon or not
    public static Map<UUID, List<PokemonEntity>> playerPokemonCount = new HashMap<>();

    public static void startTimer() {

        if (SpawnAreaHandler.areasWithNaturalSpawns == 0) return; // stops this from doing ANYTHING if no use of natural spawners is detected anywhere on the server
        if (timer != null) {

            timer.cancel(); // will stop the timer on reload and restart it, in the event the interval it runs at is changed

        }

        timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {

                for (Map.Entry<Region, List<Area>> entry : SpawnAreaHandler.naturalSpawnerAreas.entrySet()) {

                    Region region = entry.getKey();
                    List<Area> areas = entry.getValue();
                    if (AreaHandler.playersInArea.containsKey(region.getName())) {

                        Map<Area, List<UUID>> p = AreaHandler.playersInArea.get(region.getName());
                        for (Area area : areas) {

                            if (p.containsKey(area)) {

                                // Getting, checking, setting spawn interval values for this specific area based on its config setting
                                SpawnArea spawnArea = SpawnAreaHandler.areaMap.get(area);
                                int spawnIntervalCount = 1;
                                if (spawnAttemptMap.containsKey(area)) {

                                    spawnIntervalCount = spawnAttemptMap.get(area);

                                }
                                if (spawnIntervalCount < spawnArea.getNaturalSpawnerSettings().getSpawnInterval()) {

                                    spawnIntervalCount = spawnIntervalCount + 1;
                                    spawnAttemptMap.put(area, spawnIntervalCount);
                                    continue;

                                } else {

                                    spawnIntervalCount = 1;
                                    spawnAttemptMap.put(area, spawnIntervalCount);

                                }
                                List<UUID> uuids = p.get(area);
                                for (UUID u : uuids) {

                                    AtomicInteger count = new AtomicInteger();
                                    List<PokemonEntity> playersSpawnedPokemon;
                                    if (playerPokemonCount.containsKey(u)) {

                                        count.set(playerPokemonCount.get(u).size());
                                        playersSpawnedPokemon = playerPokemonCount.get(u);

                                    } else {

                                        playersSpawnedPokemon = new ArrayList<>();

                                    }
                                    if (count.get() >= spawnArea.getNaturalSpawnerSettings().getSpawnLimit()) continue;
                                    ServerPlayerEntity player = LypakaUtils.playerMap.getOrDefault(u, null);
                                    if (player != null) {

                                        SpawnManager.spawnerLogicThread.execute(() -> {

                                            List<GeneratedSpawn> toSpawn = SpawnGenerator.generateNaturalSpawn(area, player);
                                            player.getServer().executeSync(() -> {

                                                // spawn Pokemon
                                                for (GeneratedSpawn spawn : toSpawn) {

                                                    Pokemon poke = spawn.getPokemon();
                                                    BlockPos pos = spawn.getSpawnLocation();
                                                    int spawnX = pos.getX();
                                                    int spawnY = pos.getY();
                                                    int spawnZ = pos.getZ();
                                                    List<Area> areasAtSpawn = AreaHandler.getFromLocation(spawnX, spawnY, spawnZ, player.getWorld());
                                                    if (areasAtSpawn.isEmpty()) continue;
                                                    PokemonEntity entity = new PokemonEntity(player.getWorld(), poke, CobblemonEntities.POKEMON);
                                                    if (spawnArea.getNaturalSpawnerSettings().doesClearSpawns()) {

                                                        Map<UUID, List<PokemonEntity>> spawnedMap = new HashMap<>();
                                                        if (pokemonSpawnedMap.containsKey(area)) {

                                                            spawnedMap = pokemonSpawnedMap.get(area);

                                                        }
                                                        List<PokemonEntity> spawnedPokemon = new ArrayList<>();
                                                        if (spawnedMap.containsKey(player.getUuid())) {

                                                            spawnedPokemon = spawnedMap.get(player.getUuid());

                                                        }

                                                        spawnedPokemon.add(entity);
                                                        spawnedMap.put(player.getUuid(), spawnedPokemon);
                                                        pokemonSpawnedMap.put(area, spawnedMap);

                                                    }
                                                    entity.setPosition(spawnX, spawnY + 1.5, spawnZ);
                                                    player.sendMessage(FancyTextHandler.getFormattedText("&eSpawned " + poke.getSpecies().getName() + " at " + spawnX + ", " + spawnY + ", " + spawnZ));
                                                    entity.setGlowing(true);
                                                    player.getWorld().spawnEntity(entity);
                                                    playersSpawnedPokemon.add(entity);
                                                    count.getAndIncrement();
                                                    if (count.get() >= spawnArea.getNaturalSpawnerSettings().getSpawnLimit()) {

                                                        break;

                                                    }
                                                    playerPokemonCount.put(player.getUuid(), playersSpawnedPokemon);
                                                    if (spawnArea.getNaturalSpawnerSettings().doesDespawnAfterBattle()) {

                                                        spawnedPokemonUUIDs.add(entity.getUuid());

                                                    }
                                                    if (spawnArea.getNaturalSpawnerSettings().getDespawnTimer() > 0) {

                                                        entity.setDespawnCounter(spawnArea.getNaturalSpawnerSettings().getDespawnTimer());

                                                    }
                                                    String messageType = "";
                                                    if (poke.getShiny()) {

                                                        messageType = "-Shiny";

                                                    }
                                                    messageType = "Spawn-Message" + messageType;
                                                    String message = spawnArea.getNaturalSpawnerSettings().getMessagesMap().get(messageType);
                                                    if (!message.equalsIgnoreCase("")) {

                                                        player.sendMessage(FancyTextHandler.getFormattedText(message.replace("%pokemon%", poke.getSpecies().getName())), true);

                                                    }
                                                    if (toSpawn.size() == 1) {

                                                        // only one Pokemon spawned, so we check for the auto battle stuff
                                                        if (RandomHandler.getRandomChance(spawnArea.getNaturalSpawnerSettings().getAutoBattleChance())) {

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

                                                        }

                                                    }

                                                }

                                            });

                                        });

                                    }

                                }

                            }

                        }

                    }

                }

            }

        }, 0, 1000L);

    }

}
