package com.lypaka.spawnmanager.Utils.SpawnerUtils;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.battles.BattleRegistry;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.lypaka.areamanager.Areas.Area;
import com.lypaka.areamanager.Areas.AreaHandler;
import com.lypaka.areamanager.Regions.Region;
import com.lypaka.lypakautils.Handlers.RandomHandler;
import com.lypaka.lypakautils.Handlers.WorldTimeHandler;
import com.lypaka.spawnmanager.ConfigGetters;
import com.lypaka.spawnmanager.SpawnAreas.SpawnArea;
import com.lypaka.spawnmanager.SpawnAreas.SpawnAreaHandler;
import com.lypaka.spawnmanager.SpawnAreas.Spawns.*;
import com.lypaka.spawnmanager.SpawnManager;
import com.lypaka.spawnmanager.Utils.ExternalAbilities.*;
import com.lypaka.spawnmanager.Utils.HeldItemUtils;
import com.lypaka.spawnmanager.Utils.MiscUtils;
import com.lypaka.spawnmanager.Utils.PokemonSpawnBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.util.*;

public class SpawnGenerator {

    private static final List<String> TIME_PRIORITY = Arrays.asList("Day", "Morning", "Dawn", "Dusk", "Midnight");

    public static List<GeneratedSpawn> generateSurfSpawn (Area area, ServerPlayerEntity player) {

        try {

            List<GeneratedSpawn> toSpawn = new ArrayList<>();
            int x = player.getBlockPos().getX();
            int y = player.getBlockPos().getY();
            int z = player.getBlockPos().getZ();
            World world = player.getWorld();
            String time = "Night";
            List<String> times = WorldTimeHandler.getCurrentTimeValues(world);
            for (String p : TIME_PRIORITY) {

                if (times.stream().anyMatch(t -> t.equalsIgnoreCase(p))) {

                    time = p;
                    break;

                }

            }
            String weather = "Clear";
            if (world.isRaining()) {

                weather = "Rain";

            } else if (world.isThundering()) {

                weather = "Storm";

            }
            String location = "land";
            int spawnX;
            int spawnY;
            int spawnZ;
            BlockPos pos = player.getBlockPos();
            BlockState state = world.getBlockState(pos);
            String blockID = Registries.BLOCK.getId(state.getBlock()).toString();
            if (blockID.equalsIgnoreCase("air")) location = "air";
            if (blockID.contains("water") || blockID.contains("lava")) location = "water";
            if (y <= area.getUnderground()) location = "underground";
            Heightmap.Type type = location.equalsIgnoreCase("water") ? Heightmap.Type.OCEAN_FLOOR : Heightmap.Type.WORLD_SURFACE;
            Pokemon playersPokemon = null;
            PlayerPartyStore party = Cobblemon.INSTANCE.getStorage().getParty(player);
            for (int i = 0; i < 6; i++) {

                Pokemon p = party.get(i);
                if (p != null) {

                    playersPokemon = p;
                    break;

                }

            }
            List<Area> areas = AreaHandler.getSortedAreas(x, y, z, world);
            double modifier = 1.0;
            if (ArenaTrap.applies(playersPokemon) || Illuminate.applies(playersPokemon) || NoGuard.applies(playersPokemon)) {

                modifier = 2.0;

            } else if (Infiltrator.applies(playersPokemon) || QuickFeet.applies(playersPokemon) || Stench.applies(playersPokemon) || WhiteSmoke.applies(playersPokemon)) {

                modifier = 0.5;

            }
            for (int i = 0; i < areas.size(); i++) {

                Area currentArea = areas.get(i);
                SpawnArea currentSpawnArea = SpawnAreaHandler.areaMap.get(currentArea);
                if (!RandomHandler.getRandomChance(currentSpawnArea.getSurfSpawnerSettings().getSpawnChance())) continue;
                if (currentSpawnArea.getSurfSpawnerSettings().getBlockIDs().contains(blockID)) {

                    if (currentSpawnArea.getSurfSpawnerSettings().doesAutoBattle() && BattleRegistry.INSTANCE.getBattleByParticipatingPlayer(player) != null) break;
                    if (currentSpawnArea.getSurfSpawnerSettings().doesAutoBattle() && !MiscUtils.canPlayerBattle(player)) break;

                    int radius = currentSpawnArea.getSurfSpawnerSettings().getSpawnRadius();
                    AreaSpawns spawns = SpawnAreaHandler.areaSpawnMap.get(currentSpawnArea);
                    if (!spawns.getSurfSpawns().isEmpty()) {

                        Map<PokemonSpawn, Double> pokemon = PokemonSpawnBuilder.buildSurfSpawnsList(time, weather, spawns, modifier);
                        List<PokemonSpawn> list = new ArrayList<>(pokemon.keySet());
                        if (list.isEmpty()) {

                            if (ConfigGetters.debugModeEnabled) {

                                SpawnManager.logger.info("[SPAWNMANAGER DEBUG] Spawn list is empty! Possible reasons: low spawn chances, player location isn't matching Pokemon spawn location, time and/or weather checks not passing");

                            }
                            continue;

                        }
                        PokemonSpawn spawnedPokemon;
                        // to help with some spawn variety in the event multiple Pokemon have really high spawn chances and pass through the chance check a lot
                        int attempts = 0;
                        do {

                            if (list.size() == 1) {

                                spawnedPokemon = list.getFirst();

                            } else {

                                spawnedPokemon = RandomHandler.getRandomElementFromList(list);


                            }
                            if (spawnedPokemon == null) {

                                attempts++;

                            } else {

                                break;

                            }

                        } while (attempts < 10);

                        if (spawnedPokemon == null) {

                            continue;

                        }
                        Map<Pokemon, PokemonSpawn> mapForHustle = new HashMap<>();
                        Map<Pokemon, Double> pokemonShinyChances = new HashMap<>();
                        SurfSpawn surfSpawn = (SurfSpawn) spawnedPokemon;
                        int spawnAmount = surfSpawn.getGroupSize(time, weather);
                        for (int j = 0; j < spawnAmount; j++) {

                            Pokemon pokemonObject = PokemonSpawnBuilder.buildPokemonFromPokemonSpawn(spawnedPokemon);
                            do {

                                if (RandomHandler.getRandomChance(50)) {

                                    spawnX = x + RandomHandler.getRandomNumberBetween(1, radius);

                                } else {

                                    spawnX = x - RandomHandler.getRandomNumberBetween(1, radius);

                                }
                                if (RandomHandler.getRandomChance(50)) {

                                    spawnZ = z + RandomHandler.getRandomNumberBetween(1, radius);

                                } else {

                                    spawnZ = z - RandomHandler.getRandomNumberBetween(1, radius);

                                }
                                if (location.equalsIgnoreCase("underground")) {

                                    spawnY = y;

                                } else {

                                    Chunk chunk = player.getWorld().getChunk(spawnX >> 4, spawnZ >> 4);
                                    Heightmap heightmap = chunk.getHeightmap(type);
                                    int relativeX = spawnX & 15;
                                    int relativeZ = spawnZ & 15;
                                    spawnY = heightmap.get(relativeX, relativeZ);

                                }

                            } while (!AreaHandler.getFromLocation(spawnX, spawnY, spawnZ, player.getWorld()).contains(currentArea));
                            BlockPos spawnLocation = new BlockPos(spawnX, spawnY, spawnZ);
                            toSpawn.add(new GeneratedSpawn(pokemonObject, spawnLocation));
                            mapForHustle.put(pokemonObject, spawnedPokemon);
                            double shinyChance = currentSpawnArea.getSurfSpawnerSettings().getSpawnerShinyChance() > 0 ? currentSpawnArea.getSurfSpawnerSettings().getSpawnerShinyChance() : Double.parseDouble(String.valueOf(Cobblemon.INSTANCE.getConfig().getShinyRate()));
                            double pokemonShinyChance = surfSpawn.getShinyChance(time, weather);
                            shinyChance = pokemonShinyChance > 0 ? pokemonShinyChance : shinyChance;
                            if (pokemonShinyChance == 0.00) {

                                shinyChance = 0.00;

                            }
                            pokemonShinyChances.put(pokemonObject, shinyChance);

                        }
                        for (GeneratedSpawn spawn : toSpawn) {

                            Pokemon poke = spawn.getPokemon();
                            if (Intimidate.applies(playersPokemon) || KeenEye.applies(playersPokemon)) {

                                poke = Intimidate.tryIntimidate(poke, playersPokemon);
                                if (poke == null) continue;

                            }
                            if (FlashFire.applies(playersPokemon)) {

                                poke = FlashFire.tryFlashFire(poke, pokemon);

                            } else if (Harvest.applies(playersPokemon)) {

                                poke = Harvest.tryHarvest(poke, pokemon);

                            } else if (LightningRod.applies(playersPokemon) || Static.applies(playersPokemon)) {

                                poke = LightningRod.tryLightningRod(poke, pokemon);

                            } else if (MagnetPull.applies(playersPokemon)) {

                                poke = MagnetPull.tryMagnetPull(poke, pokemon);

                            } else if (StormDrain.applies(playersPokemon)) {

                                poke = StormDrain.tryStormDrain(poke, pokemon);

                            }

                            if (CuteCharm.applies(playersPokemon)) {

                                CuteCharm.tryApplyCuteCharmEffect(poke, playersPokemon);

                            } else if (Synchronize.applies(playersPokemon)) {

                                Synchronize.applySynchronize(poke, playersPokemon);

                            }

                            int level = poke.getLevel();
                            if (Hustle.applies(playersPokemon) || Pressure.applies(playersPokemon) || VitalSpirit.applies(playersPokemon)) {

                                level = Hustle.tryHustle(level, mapForHustle.get(poke));

                            }
                            poke.setLevel(level);
                            HeldItemUtils.tryApplyHeldItem(poke, playersPokemon);
                            double shinyChance = pokemonShinyChances.get(poke);
                            MiscUtils.tryShiny(poke, shinyChance);

                        }

                    }

                }

            }

            return toSpawn;

        } catch (Exception e) {

            System.out.println("Exception during spawn generation: " + e);
            e.printStackTrace();
            return Collections.emptyList();

        }

    }

    public static List<GeneratedSpawn> generateCaveSpawn (Area area, ServerPlayerEntity player) {

        try {

            List<GeneratedSpawn> toSpawn = new ArrayList<>();
            int x = player.getBlockPos().getX();
            int y = player.getBlockPos().getY();
            int z = player.getBlockPos().getZ();
            World world = player.getWorld();
            String time = "Night";
            List<String> times = WorldTimeHandler.getCurrentTimeValues(world);
            for (String p : TIME_PRIORITY) {

                if (times.stream().anyMatch(t -> t.equalsIgnoreCase(p))) {

                    time = p;
                    break;

                }

            }
            String weather = "Clear";
            if (world.isRaining()) {

                weather = "Rain";

            } else if (world.isThundering()) {

                weather = "Storm";

            }
            String location = "land";
            int spawnX;
            int spawnY;
            int spawnZ;
            BlockPos pos = new BlockPos(x, y - 1, z);
            BlockState state = world.getBlockState(pos);
            String blockID = Registries.BLOCK.getId(state.getBlock()).toString();
            if (blockID.equalsIgnoreCase("air")) location = "air";
            if (blockID.contains("water") || blockID.contains("lava")) location = "water";
            if (y <= area.getUnderground()) location = "underground";
            Heightmap.Type type = location.equalsIgnoreCase("water") ? Heightmap.Type.OCEAN_FLOOR : Heightmap.Type.WORLD_SURFACE;
            Pokemon playersPokemon = null;
            PlayerPartyStore party = Cobblemon.INSTANCE.getStorage().getParty(player);
            for (int i = 0; i < 6; i++) {

                Pokemon p = party.get(i);
                if (p != null) {

                    playersPokemon = p;
                    break;

                }

            }
            List<Area> areas = AreaHandler.getSortedAreas(x, y, z, world);
            double modifier = 1.0;
            if (ArenaTrap.applies(playersPokemon) || Illuminate.applies(playersPokemon) || NoGuard.applies(playersPokemon)) {

                modifier = 2.0;

            } else if (Infiltrator.applies(playersPokemon) || QuickFeet.applies(playersPokemon) || Stench.applies(playersPokemon) || WhiteSmoke.applies(playersPokemon)) {

                modifier = 0.5;

            }
            for (int i = 0; i < areas.size(); i++) {

                Area currentArea = areas.get(i);
                SpawnArea currentSpawnArea = SpawnAreaHandler.areaMap.get(currentArea);
                if (!RandomHandler.getRandomChance(currentSpawnArea.getCaveSpawnerSettings().getSpawnChance())) continue;
                if (currentSpawnArea.getCaveSpawnerSettings().getBlockIDs().contains(blockID)) {

                    if (currentSpawnArea.getCaveSpawnerSettings().doesAutoBattle() && BattleRegistry.INSTANCE.getBattleByParticipatingPlayer(player) != null) break;
                    if (currentSpawnArea.getCaveSpawnerSettings().doesAutoBattle() && !MiscUtils.canPlayerBattle(player)) break;

                    int radius = currentSpawnArea.getCaveSpawnerSettings().getSpawnRadius();
                    AreaSpawns spawns = SpawnAreaHandler.areaSpawnMap.get(currentSpawnArea);
                    if (!spawns.getCaveSpawns().isEmpty()) {

                        Map<PokemonSpawn, Double> pokemon = PokemonSpawnBuilder.buildCaveSpawnList(time, weather, location, spawns, modifier);
                        List<PokemonSpawn> list = new ArrayList<>(pokemon.keySet());
                        if (list.isEmpty()) {

                            if (ConfigGetters.debugModeEnabled) {

                                SpawnManager.logger.info("[SPAWNMANAGER DEBUG] Spawn list is empty! Possible reasons: low spawn chances, player location isn't matching Pokemon spawn location, time and/or weather checks not passing");

                            }
                            continue;

                        }
                        PokemonSpawn spawnedPokemon;
                        // to help with some spawn variety in the event multiple Pokemon have really high spawn chances and pass through the chance check a lot
                        int attempts = 0;
                        do {

                            if (list.size() == 1) {

                                spawnedPokemon = list.getFirst();

                            } else {

                                spawnedPokemon = RandomHandler.getRandomElementFromList(list);


                            }
                            if (spawnedPokemon == null) {

                                attempts++;

                            } else {

                                break;

                            }

                        } while (attempts < 10);

                        if (spawnedPokemon == null) {

                            continue;

                        }
                        Map<Pokemon, PokemonSpawn> mapForHustle = new HashMap<>();
                        Map<Pokemon, Double> pokemonShinyChances = new HashMap<>();
                        CaveSpawn caveSpawn = (CaveSpawn) spawnedPokemon;
                        int spawnAmount = caveSpawn.getGroupSize(time, weather);
                        for (int j = 0; j < spawnAmount; j++) {

                            Pokemon pokemonObject = PokemonSpawnBuilder.buildPokemonFromPokemonSpawn(spawnedPokemon);
                            do {

                                if (RandomHandler.getRandomChance(50)) {

                                    spawnX = x + RandomHandler.getRandomNumberBetween(1, radius);

                                } else {

                                    spawnX = x - RandomHandler.getRandomNumberBetween(1, radius);

                                }
                                if (RandomHandler.getRandomChance(50)) {

                                    spawnZ = z + RandomHandler.getRandomNumberBetween(1, radius);

                                } else {

                                    spawnZ = z - RandomHandler.getRandomNumberBetween(1, radius);

                                }
                                if (location.equalsIgnoreCase("underground")) {

                                    spawnY = y;

                                } else {

                                    Chunk chunk = player.getWorld().getChunk(spawnX >> 4, spawnZ >> 4);
                                    Heightmap heightmap = chunk.getHeightmap(type);
                                    int relativeX = spawnX & 15;
                                    int relativeZ = spawnZ & 15;
                                    spawnY = heightmap.get(relativeX, relativeZ);

                                }

                            } while (!AreaHandler.getFromLocation(spawnX, spawnY, spawnZ, player.getWorld()).contains(currentArea));
                            BlockPos spawnLocation = new BlockPos(spawnX, spawnY, spawnZ);
                            toSpawn.add(new GeneratedSpawn(pokemonObject, spawnLocation));
                            mapForHustle.put(pokemonObject, spawnedPokemon);
                            double shinyChance = currentSpawnArea.getCaveSpawnerSettings().getSpawnerShinyChance() > 0 ? currentSpawnArea.getCaveSpawnerSettings().getSpawnerShinyChance() : Double.parseDouble(String.valueOf(Cobblemon.INSTANCE.getConfig().getShinyRate()));
                            double pokemonShinyChance = caveSpawn.getShinyChance(time, weather);
                            shinyChance = pokemonShinyChance > 0 ? pokemonShinyChance : shinyChance;
                            if (pokemonShinyChance == 0.00) {

                                shinyChance = 0.00;

                            }
                            pokemonShinyChances.put(pokemonObject, shinyChance);

                        }
                        for (GeneratedSpawn spawn : toSpawn) {

                            Pokemon poke = spawn.getPokemon();
                            if (Intimidate.applies(playersPokemon) || KeenEye.applies(playersPokemon)) {

                                poke = Intimidate.tryIntimidate(poke, playersPokemon);
                                if (poke == null) continue;

                            }
                            if (FlashFire.applies(playersPokemon)) {

                                poke = FlashFire.tryFlashFire(poke, pokemon);

                            } else if (Harvest.applies(playersPokemon)) {

                                poke = Harvest.tryHarvest(poke, pokemon);

                            } else if (LightningRod.applies(playersPokemon) || Static.applies(playersPokemon)) {

                                poke = LightningRod.tryLightningRod(poke, pokemon);

                            } else if (MagnetPull.applies(playersPokemon)) {

                                poke = MagnetPull.tryMagnetPull(poke, pokemon);

                            } else if (StormDrain.applies(playersPokemon)) {

                                poke = StormDrain.tryStormDrain(poke, pokemon);

                            }

                            if (CuteCharm.applies(playersPokemon)) {

                                CuteCharm.tryApplyCuteCharmEffect(poke, playersPokemon);

                            } else if (Synchronize.applies(playersPokemon)) {

                                Synchronize.applySynchronize(poke, playersPokemon);

                            }

                            int level = poke.getLevel();
                            if (Hustle.applies(playersPokemon) || Pressure.applies(playersPokemon) || VitalSpirit.applies(playersPokemon)) {

                                level = Hustle.tryHustle(level, mapForHustle.get(poke));

                            }
                            poke.setLevel(level);
                            HeldItemUtils.tryApplyHeldItem(poke, playersPokemon);
                            double shinyChance = pokemonShinyChances.get(poke);
                            MiscUtils.tryShiny(poke, shinyChance);

                        }

                    }

                }

            }

            return toSpawn;

        } catch (Exception e) {

            System.out.println("Exception during spawn generation: " + e);
            e.printStackTrace();
            return Collections.emptyList();

        }

    }

    public static List<GeneratedSpawn> generateGrassSpawn (Area area, ServerPlayerEntity player) {

        try {

            List<GeneratedSpawn> toSpawn = new ArrayList<>();
            int x = player.getBlockPos().getX();
            int y = player.getBlockPos().getY();
            int z = player.getBlockPos().getZ();
            World world = player.getWorld();
            String time = "Night";
            List<String> times = WorldTimeHandler.getCurrentTimeValues(world);
            for (String p : TIME_PRIORITY) {

                if (times.stream().anyMatch(t -> t.equalsIgnoreCase(p))) {

                    time = p;
                    break;

                }

            }
            String weather = "Clear";
            if (world.isRaining()) {

                weather = "Rain";

            } else if (world.isThundering()) {

                weather = "Storm";

            }
            String location = "land";
            int spawnX;
            int spawnY;
            int spawnZ;
            BlockPos pos = player.getBlockPos();
            BlockState state = world.getBlockState(pos);
            String blockID = Registries.BLOCK.getId(state.getBlock()).toString();
            if (blockID.equalsIgnoreCase("air")) location = "air";
            if (blockID.contains("water") || blockID.contains("lava")) location = "water";
            if (y <= area.getUnderground()) location = "underground";
            Heightmap.Type type = location.equalsIgnoreCase("water") ? Heightmap.Type.OCEAN_FLOOR : Heightmap.Type.WORLD_SURFACE;
            Pokemon playersPokemon = null;
            PlayerPartyStore party = Cobblemon.INSTANCE.getStorage().getParty(player);
            for (int i = 0; i < 6; i++) {

                Pokemon p = party.get(i);
                if (p != null) {

                    playersPokemon = p;
                    break;

                }

            }
            List<Area> areas = AreaHandler.getSortedAreas(x, y, z, world);
            double modifier = 1.0;
            if (ArenaTrap.applies(playersPokemon) || Illuminate.applies(playersPokemon) || NoGuard.applies(playersPokemon)) {

                modifier = 2.0;

            } else if (Infiltrator.applies(playersPokemon) || QuickFeet.applies(playersPokemon) || Stench.applies(playersPokemon) || WhiteSmoke.applies(playersPokemon)) {

                modifier = 0.5;

            }
            for (int i = 0; i < areas.size(); i++) {

                Area currentArea = areas.get(i);
                SpawnArea currentSpawnArea = SpawnAreaHandler.areaMap.get(currentArea);
                if (!RandomHandler.getRandomChance(currentSpawnArea.getGrassSpawnerSettings().getSpawnChance())) continue;
                if (currentSpawnArea.getGrassSpawnerSettings().getBlockIDs().contains(blockID)) {

                    if (currentSpawnArea.getGrassSpawnerSettings().doesAutoBattle() && BattleRegistry.INSTANCE.getBattleByParticipatingPlayer(player) != null) break;
                    if (currentSpawnArea.getGrassSpawnerSettings().doesAutoBattle() && !MiscUtils.canPlayerBattle(player)) break;

                    int radius = currentSpawnArea.getGrassSpawnerSettings().getSpawnRadius();
                    AreaSpawns spawns = SpawnAreaHandler.areaSpawnMap.get(currentSpawnArea);
                    if (!spawns.getGrassSpawns().isEmpty()) {

                        Map<PokemonSpawn, Double> pokemon = PokemonSpawnBuilder.buildGrassSpawnsList(time, weather, location, spawns, modifier);
                        List<PokemonSpawn> list = new ArrayList<>(pokemon.keySet());
                        if (list.isEmpty()) {

                            if (ConfigGetters.debugModeEnabled) {

                                SpawnManager.logger.info("[SPAWNMANAGER DEBUG] Spawn list is empty! Possible reasons: low spawn chances, player location isn't matching Pokemon spawn location, time and/or weather checks not passing");

                            }
                            continue;

                        }
                        PokemonSpawn spawnedPokemon;
                        // to help with some spawn variety in the event multiple Pokemon have really high spawn chances and pass through the chance check a lot
                        int attempts = 0;
                        do {

                            if (list.size() == 1) {

                                spawnedPokemon = list.getFirst();

                            } else {

                                spawnedPokemon = RandomHandler.getRandomElementFromList(list);


                            }
                            if (spawnedPokemon == null) {

                                attempts++;

                            } else {

                                break;

                            }

                        } while (attempts < 10);

                        if (spawnedPokemon == null) {

                            continue;

                        }
                        Map<Pokemon, PokemonSpawn> mapForHustle = new HashMap<>();
                        Map<Pokemon, Double> pokemonShinyChances = new HashMap<>();
                        GrassSpawn grassSpawn = (GrassSpawn) spawnedPokemon;
                        int spawnAmount = grassSpawn.getGroupSize(time, weather);
                        for (int j = 0; j < spawnAmount; j++) {

                            Pokemon pokemonObject = PokemonSpawnBuilder.buildPokemonFromPokemonSpawn(spawnedPokemon);
                            do {

                                if (RandomHandler.getRandomChance(50)) {

                                    spawnX = x + RandomHandler.getRandomNumberBetween(1, radius);

                                } else {

                                    spawnX = x - RandomHandler.getRandomNumberBetween(1, radius);

                                }
                                if (RandomHandler.getRandomChance(50)) {

                                    spawnZ = z + RandomHandler.getRandomNumberBetween(1, radius);

                                } else {

                                    spawnZ = z - RandomHandler.getRandomNumberBetween(1, radius);

                                }
                                if (location.equalsIgnoreCase("underground")) {

                                    spawnY = y;

                                } else {

                                    Chunk chunk = player.getWorld().getChunk(spawnX >> 4, spawnZ >> 4);
                                    Heightmap heightmap = chunk.getHeightmap(type);
                                    int relativeX = spawnX & 15;
                                    int relativeZ = spawnZ & 15;
                                    spawnY = heightmap.get(relativeX, relativeZ);

                                }

                            } while (!AreaHandler.getFromLocation(spawnX, spawnY, spawnZ, player.getWorld()).contains(currentArea));
                            BlockPos spawnLocation = new BlockPos(spawnX, spawnY, spawnZ);
                            toSpawn.add(new GeneratedSpawn(pokemonObject, spawnLocation));
                            mapForHustle.put(pokemonObject, spawnedPokemon);
                            double shinyChance = currentSpawnArea.getGrassSpawnerSettings().getSpawnerShinyChance() > 0 ? currentSpawnArea.getGrassSpawnerSettings().getSpawnerShinyChance() : Double.parseDouble(String.valueOf(Cobblemon.INSTANCE.getConfig().getShinyRate()));
                            double pokemonShinyChance = grassSpawn.getShinyChance(time, weather);
                            shinyChance = pokemonShinyChance > 0 ? pokemonShinyChance : shinyChance;
                            if (pokemonShinyChance == 0.00) {

                                shinyChance = 0.00;

                            }
                            pokemonShinyChances.put(pokemonObject, shinyChance);

                        }
                        for (GeneratedSpawn spawn : toSpawn) {

                            Pokemon poke = spawn.getPokemon();
                            if (Intimidate.applies(playersPokemon) || KeenEye.applies(playersPokemon)) {

                                poke = Intimidate.tryIntimidate(poke, playersPokemon);
                                if (poke == null) continue;

                            }
                            if (FlashFire.applies(playersPokemon)) {

                                poke = FlashFire.tryFlashFire(poke, pokemon);

                            } else if (Harvest.applies(playersPokemon)) {

                                poke = Harvest.tryHarvest(poke, pokemon);

                            } else if (LightningRod.applies(playersPokemon) || Static.applies(playersPokemon)) {

                                poke = LightningRod.tryLightningRod(poke, pokemon);

                            } else if (MagnetPull.applies(playersPokemon)) {

                                poke = MagnetPull.tryMagnetPull(poke, pokemon);

                            } else if (StormDrain.applies(playersPokemon)) {

                                poke = StormDrain.tryStormDrain(poke, pokemon);

                            }

                            if (CuteCharm.applies(playersPokemon)) {

                                CuteCharm.tryApplyCuteCharmEffect(poke, playersPokemon);

                            } else if (Synchronize.applies(playersPokemon)) {

                                Synchronize.applySynchronize(poke, playersPokemon);

                            }

                            int level = poke.getLevel();
                            if (Hustle.applies(playersPokemon) || Pressure.applies(playersPokemon) || VitalSpirit.applies(playersPokemon)) {

                                level = Hustle.tryHustle(level, mapForHustle.get(poke));

                            }
                            poke.setLevel(level);
                            HeldItemUtils.tryApplyHeldItem(poke, playersPokemon);
                            double shinyChance = pokemonShinyChances.get(poke);
                            MiscUtils.tryShiny(poke, shinyChance);

                        }

                    }

                }

            }

            return toSpawn;

        } catch (Exception e) {

            System.out.println("Exception during spawn generation: " + e);
            e.printStackTrace();
            return Collections.emptyList();

        }

    }

    public static List<GeneratedSpawn> generateFishSpawn (ServerPlayerEntity player) {

        try {

            List<GeneratedSpawn> toSpawn = new ArrayList<>();
            int x = player.getBlockPos().getX();
            int y = player.getBlockPos().getY();
            int z = player.getBlockPos().getZ();
            World world = player.getWorld();
            String time = "Night";
            List<String> times = WorldTimeHandler.getCurrentTimeValues(world);
            for (String p : TIME_PRIORITY) {

                if (times.stream().anyMatch(t -> t.equalsIgnoreCase(p))) {

                    time = p;
                    break;

                }

            }
            String weather = "Clear";
            if (world.isRaining()) {

                weather = "Rain";

            } else if (world.isThundering()) {

                weather = "Storm";

            }

            Pokemon playersPokemon = null;
            PlayerPartyStore party = Cobblemon.INSTANCE.getStorage().getParty(player);
            for (int i = 0; i < 6; i++) {

                Pokemon p = party.get(i);
                if (p != null) {

                    playersPokemon = p;
                    break;

                }

            }
            List<Area> areas = AreaHandler.getSortedAreas(x, y, z, world);
            double modifier = 1.0;
            if (ArenaTrap.applies(playersPokemon) || Illuminate.applies(playersPokemon) || NoGuard.applies(playersPokemon)) {

                modifier = 2.0;

            } else if (Infiltrator.applies(playersPokemon) || QuickFeet.applies(playersPokemon) || Stench.applies(playersPokemon) || WhiteSmoke.applies(playersPokemon)) {

                modifier = 0.5;

            }
            for (int i = 0; i < areas.size(); i++) {

                Area currentArea = areas.get(i);
                SpawnArea currentSpawnArea = SpawnAreaHandler.areaMap.get(currentArea);
                AreaSpawns spawns = SpawnAreaHandler.areaSpawnMap.get(currentSpawnArea);
                if (!spawns.getFishSpawns().isEmpty()) {

                    Map<PokemonSpawn, Double> pokemon = PokemonSpawnBuilder.buildFishSpawns(time, weather, spawns, modifier);
                    List<PokemonSpawn> list = new ArrayList<>(pokemon.keySet());
                    if (list.isEmpty()) {

                        if (ConfigGetters.debugModeEnabled) {

                            SpawnManager.logger.info("[SPAWNMANAGER DEBUG] Spawn list is empty! Possible reasons: low spawn chances, player location isn't matching Pokemon spawn location, time and/or weather checks not passing");

                        }
                        continue;

                    }
                    PokemonSpawn spawnedPokemon;
                    // to help with some spawn variety in the event multiple Pokemon have really high spawn chances and pass through the chance check a lot
                    int attempts = 0;
                    do {

                        if (list.size() == 1) {

                            spawnedPokemon = list.getFirst();

                        } else {

                            spawnedPokemon = RandomHandler.getRandomElementFromList(list);


                        }
                        if (spawnedPokemon == null) {

                            attempts++;

                        } else {

                            break;

                        }

                    } while (attempts < 10);

                    if (spawnedPokemon == null) {

                        continue;

                    }
                    Map<Pokemon, PokemonSpawn> mapForHustle = new HashMap<>();
                    FishSpawn fishSpawn = (FishSpawn) spawnedPokemon;
                    Pokemon pokemonObject = PokemonSpawnBuilder.buildPokemonFromPokemonSpawn(spawnedPokemon);
                    toSpawn.add(new GeneratedSpawn(pokemonObject, null));
                    mapForHustle.put(pokemonObject, spawnedPokemon);
                    double shinyChance = currentSpawnArea.getFishSpawnerSettings().getSpawnerShinyChance() > 0 ? currentSpawnArea.getFishSpawnerSettings().getSpawnerShinyChance() : Double.parseDouble(String.valueOf(Cobblemon.INSTANCE.getConfig().getShinyRate()));
                    double pokemonShinyChance = fishSpawn.getShinyChance(time, weather);
                    shinyChance = pokemonShinyChance > 0 ? pokemonShinyChance : shinyChance;
                    if (pokemonShinyChance == 0.00) {

                        shinyChance = 0.00;

                    }
                    for (GeneratedSpawn spawn : toSpawn) {

                        Pokemon poke = spawn.getPokemon();
                        if (Intimidate.applies(playersPokemon) || KeenEye.applies(playersPokemon)) {

                            poke = Intimidate.tryIntimidate(poke, playersPokemon);
                            if (poke == null) continue;

                        }
                        if (FlashFire.applies(playersPokemon)) {

                            poke = FlashFire.tryFlashFire(poke, pokemon);

                        } else if (Harvest.applies(playersPokemon)) {

                            poke = Harvest.tryHarvest(poke, pokemon);

                        } else if (LightningRod.applies(playersPokemon) || Static.applies(playersPokemon)) {

                            poke = LightningRod.tryLightningRod(poke, pokemon);

                        } else if (MagnetPull.applies(playersPokemon)) {

                            poke = MagnetPull.tryMagnetPull(poke, pokemon);

                        } else if (StormDrain.applies(playersPokemon)) {

                            poke = StormDrain.tryStormDrain(poke, pokemon);

                        }

                        if (CuteCharm.applies(playersPokemon)) {

                            CuteCharm.tryApplyCuteCharmEffect(poke, playersPokemon);

                        } else if (Synchronize.applies(playersPokemon)) {

                            Synchronize.applySynchronize(poke, playersPokemon);

                        }

                        int level = poke.getLevel();
                        if (Hustle.applies(playersPokemon) || Pressure.applies(playersPokemon) || VitalSpirit.applies(playersPokemon)) {

                            level = Hustle.tryHustle(level, mapForHustle.get(poke));

                        }
                        poke.setLevel(level);
                        HeldItemUtils.tryApplyHeldItem(poke, playersPokemon);
                        MiscUtils.tryShiny(poke, shinyChance);

                    }

                }

            }

            return toSpawn;

        } catch (Exception e) {

            System.out.println("Exception during spawn generation: " + e);
            e.printStackTrace();
            return Collections.emptyList();

        }

    }

    public static List<GeneratedSpawn> generateNaturalSpawn (Area area, ServerPlayerEntity player) {

        try {

            List<GeneratedSpawn> toSpawn = new ArrayList<>();
            int x = player.getBlockPos().getX();
            int y = player.getBlockPos().getY();
            int z = player.getBlockPos().getZ();
            World world = player.getWorld();
            String time = "Night";
            List<String> times = WorldTimeHandler.getCurrentTimeValues(world);
            for (String p : TIME_PRIORITY) {

                if (times.stream().anyMatch(t -> t.equalsIgnoreCase(p))) {

                    time = p;
                    break;

                }

            }
            String weather = "Clear";
            if (world.isRaining()) {

                weather = "Rain";

            } else if (world.isThundering()) {

                weather = "Storm";

            }
            String location = "land";
            int spawnX;
            int spawnY;
            int spawnZ;
            BlockPos pos = player.getBlockPos();
            BlockState state = world.getBlockState(pos);
            String blockID = Registries.BLOCK.getId(state.getBlock()).toString();
            if (blockID.equalsIgnoreCase("air")) location = "air";
            if (blockID.contains("water") || blockID.contains("lava")) location = "water";
            if (y <= area.getUnderground()) location = "underground";
            Heightmap.Type type = location.equalsIgnoreCase("water") ? Heightmap.Type.OCEAN_FLOOR : Heightmap.Type.WORLD_SURFACE;
            Pokemon playersPokemon = null;
            PlayerPartyStore party = Cobblemon.INSTANCE.getStorage().getParty(player);
            for (int i = 0; i < 6; i++) {

                Pokemon p = party.get(i);
                if (p != null) {

                    playersPokemon = p;
                    break;

                }

            }
            List<Area> areas = AreaHandler.getSortedAreas(x, y, z, world);
            double modifier = 1.0;
            if (ArenaTrap.applies(playersPokemon) || Illuminate.applies(playersPokemon) || NoGuard.applies(playersPokemon)) {

                modifier = 2.0;

            } else if (Infiltrator.applies(playersPokemon) || QuickFeet.applies(playersPokemon) || Stench.applies(playersPokemon) || WhiteSmoke.applies(playersPokemon)) {

                modifier = 0.5;

            }
            for (int i = 0; i < areas.size(); i++) {

                Area currentArea = areas.get(i);
                SpawnArea currentSpawnArea = SpawnAreaHandler.areaMap.get(currentArea);
                int radius = currentSpawnArea.getNaturalSpawnerSettings().getSpawnRadius();
                if (currentSpawnArea.getNaturalSpawnerSettings().doesLimitSpawns()) {

                    if (BattleRegistry.INSTANCE.getBattleByParticipatingPlayer(player) != null) break;

                }
                AreaSpawns spawns = SpawnAreaHandler.areaSpawnMap.get(currentSpawnArea);
                if (!spawns.getNaturalSpawns().isEmpty()) {

                    Map<PokemonSpawn, Double> pokemon = PokemonSpawnBuilder.buildNaturalSpawnsList(time, weather, location, spawns, modifier);
                    List<PokemonSpawn> list = new ArrayList<>(pokemon.keySet());
                    if (list.isEmpty()) {

                        if (ConfigGetters.debugModeEnabled) {

                            SpawnManager.logger.info("[SPAWNMANAGER DEBUG] Spawn list is empty! Possible reasons: low spawn chances, player location isn't matching Pokemon spawn location, time and/or weather checks not passing");

                        }
                        continue;

                    }
                    PokemonSpawn spawnedPokemon;
                    // to help with some spawn variety in the event multiple Pokemon have really high spawn chances and pass through the chance check a lot
                    int attempts = 0;
                    do {

                        if (list.size() == 1) {

                            spawnedPokemon = list.getFirst();

                        } else {

                            spawnedPokemon = RandomHandler.getRandomElementFromList(list);


                        }
                        if (spawnedPokemon == null) {

                            attempts++;

                        } else {

                            break;

                        }

                    } while (attempts < 10);

                    if (spawnedPokemon == null) {

                        continue;

                    }
                    Map<Pokemon, PokemonSpawn> mapForHustle = new HashMap<>();
                    Map<Pokemon, Double> pokemonShinyChances = new HashMap<>();
                    NaturalSpawn naturalSpawn = (NaturalSpawn) spawnedPokemon;
                    int spawnAmount = naturalSpawn.getGroupSize(time, weather);
                    for (int j = 0; j < spawnAmount; j++) {

                        Pokemon pokemonObject = PokemonSpawnBuilder.buildPokemonFromPokemonSpawn(spawnedPokemon);
                        do {

                            if (RandomHandler.getRandomChance(50)) {

                                spawnX = x + RandomHandler.getRandomNumberBetween(1, radius);

                            } else {

                                spawnX = x - RandomHandler.getRandomNumberBetween(1, radius);

                            }
                            if (RandomHandler.getRandomChance(50)) {

                                spawnZ = z + RandomHandler.getRandomNumberBetween(1, radius);

                            } else {

                                spawnZ = z - RandomHandler.getRandomNumberBetween(1, radius);

                            }
                            if (location.equalsIgnoreCase("underground")) {

                                spawnY = y;

                            } else {

                                Chunk chunk = player.getWorld().getChunk(spawnX >> 4, spawnZ >> 4);
                                Heightmap heightmap = chunk.getHeightmap(type);
                                int relativeX = spawnX & 15;
                                int relativeZ = spawnZ & 15;
                                spawnY = heightmap.get(relativeX, relativeZ);

                            }

                        } while (!AreaHandler.getFromLocation(spawnX, spawnY, spawnZ, player.getWorld()).contains(currentArea));
                        BlockPos spawnLocation = new BlockPos(spawnX, spawnY, spawnZ);
                        toSpawn.add(new GeneratedSpawn(pokemonObject, spawnLocation));
                        mapForHustle.put(pokemonObject, spawnedPokemon);
                        double shinyChance = currentSpawnArea.getNaturalSpawnerSettings().getSpawnerShinyChance() > 0 ? currentSpawnArea.getNaturalSpawnerSettings().getSpawnerShinyChance() : Double.parseDouble(String.valueOf(Cobblemon.INSTANCE.getConfig().getShinyRate()));
                        double pokemonShinyChance = naturalSpawn.getShinyChance(time, weather);
                        shinyChance = pokemonShinyChance > 0 ? pokemonShinyChance : shinyChance;
                        if (pokemonShinyChance == 0.00) {

                            shinyChance = 0.00;

                        }
                        pokemonShinyChances.put(pokemonObject, shinyChance);

                    }
                    for (GeneratedSpawn spawn : toSpawn) {

                        Pokemon poke = spawn.getPokemon();
                        if (Intimidate.applies(playersPokemon) || KeenEye.applies(playersPokemon)) {

                            poke = Intimidate.tryIntimidate(poke, playersPokemon);
                            if (poke == null) continue;

                        }
                        if (FlashFire.applies(playersPokemon)) {

                            poke = FlashFire.tryFlashFire(poke, pokemon);

                        } else if (Harvest.applies(playersPokemon)) {

                            poke = Harvest.tryHarvest(poke, pokemon);

                        } else if (LightningRod.applies(playersPokemon) || Static.applies(playersPokemon)) {

                            poke = LightningRod.tryLightningRod(poke, pokemon);

                        } else if (MagnetPull.applies(playersPokemon)) {

                            poke = MagnetPull.tryMagnetPull(poke, pokemon);

                        } else if (StormDrain.applies(playersPokemon)) {

                            poke = StormDrain.tryStormDrain(poke, pokemon);

                        }

                        if (CuteCharm.applies(playersPokemon)) {

                            CuteCharm.tryApplyCuteCharmEffect(poke, playersPokemon);

                        } else if (Synchronize.applies(playersPokemon)) {

                            Synchronize.applySynchronize(poke, playersPokemon);

                        }

                        int level = poke.getLevel();
                        if (Hustle.applies(playersPokemon) || Pressure.applies(playersPokemon) || VitalSpirit.applies(playersPokemon)) {

                            level = Hustle.tryHustle(level, mapForHustle.get(poke));

                        }
                        poke.setLevel(level);
                        HeldItemUtils.tryApplyHeldItem(poke, playersPokemon);
                        double shinyChance = pokemonShinyChances.get(poke);
                        MiscUtils.tryShiny(poke, shinyChance);

                    }

                }

            }

            return toSpawn;

        } catch (Exception e) {

            System.out.println("Exception during spawn generation: " + e);
            e.printStackTrace();
            return Collections.emptyList();

        }

    }

}
