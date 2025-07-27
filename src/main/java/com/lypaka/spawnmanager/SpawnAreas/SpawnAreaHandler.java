package com.lypaka.spawnmanager.SpawnAreas;

import com.lypaka.areamanager.Areas.Area;
import com.lypaka.areamanager.Regions.Region;
import com.lypaka.areamanager.Regions.RegionHandler;
import com.lypaka.lypakautils.ConfigurationLoaders.BasicConfigManager;
import com.lypaka.lypakautils.ConfigurationLoaders.ComplexConfigManager;
import com.lypaka.lypakautils.ConfigurationLoaders.ConfigUtils;
import com.lypaka.shadow.configurate.objectmapping.ObjectMappingException;
import com.lypaka.shadow.google.common.reflect.TypeToken;
import com.lypaka.spawnmanager.ConfigGetters;
import com.lypaka.spawnmanager.SpawnAreas.SpawnerSettings.*;
import com.lypaka.spawnmanager.SpawnAreas.Spawns.*;
import com.lypaka.spawnmanager.SpawnManager;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class SpawnAreaHandler {

    public static Map<Area, SpawnArea> areaMap = new HashMap<>();
    public static Map<SpawnArea, AreaSpawns> areaSpawnMap = new HashMap<>();
    public static int areasWithNaturalSpawns = 0;
    public static Map<Region, List<Area>> naturalSpawnerAreas = new HashMap<>();

    public static void addFile (String region, Area area, String spawner, String pokemon, String form, int minLevel, int maxLevel, Map<String, Map<String, Map<String, String>>> spawnData) throws ObjectMappingException {

        if (ConfigGetters.debugModeEnabled) {

            SpawnManager.logger.info("[SPAWNMANAGER DEBUG] Adding pokemon: " + spawner + " / " + pokemon + " / " + form + " / " + minLevel + " / " + maxLevel + " / " + spawnData);

        }
        // we need to get the list of file names for our desired spawner, add our Pokemon to that list, and run the code to generate the file
        SpawnArea spawnArea = areaMap.get(area);
        AreaSpawns areaSpawns = areaSpawnMap.get(spawnArea);
        BasicConfigManager bcm = areaSpawns.getConfigManager();
        Path dir = ConfigUtils.checkDir(Paths.get("./config/spawnmanager/regions/" + region + "/" + area.getName()));

        if (!pokemon.contains(".conf")) pokemon = pokemon + ".conf";
        List<String> spawns;
        ComplexConfigManager ccm;
        switch (spawner.toLowerCase()) {

            case "cave":
                spawns = new ArrayList<>(bcm.getConfigNode(0, "Cave-Spawns").getList(TypeToken.of(String.class)));
                spawns.add(pokemon);
                bcm.getConfigNode(0, "Cave-Spawns").setValue(spawns);
                bcm.save();
                ccm = new ComplexConfigManager(spawns, "cave-spawns", "caveSpawnTemplate.conf", dir, SpawnManager.class, SpawnManager.MOD_NAME, SpawnManager.MOD_ID, SpawnManager.logger);
                ccm.init();
                List<CaveSpawn> caveSpawnList = areaSpawns.getCaveSpawns();
                for (int i = 0; i < spawns.size(); i++) {

                    String entry = spawns.get(i);
                    String species = entry.replace(".conf", "");
                    String entryForm;
                    int entryMin;
                    int entryMax;
                    Map<String, Map<String, Map<String, String>>> entrySpawnData;
                    if (entry.equalsIgnoreCase(pokemon)) {

                        // this is the Pokémon we just added
                        entryForm = form;
                        entryMin = minLevel;
                        entryMax = maxLevel;
                        entrySpawnData = spawnData;
                        ccm.getConfigNode(i, "Pokemon-Data", "Species").setValue(species);
                        ccm.getConfigNode(i, "Pokemon-Data", "Form").setValue(entryForm);
                        ccm.getConfigNode(i, "Pokemon-Data", "Min-Level").setValue(entryMin);
                        ccm.getConfigNode(i, "Pokemon-Data", "Max-Level").setValue(entryMax);
                        ccm.getConfigNode(i, "Spawn-Data").setValue(entrySpawnData);

                    } else {

                        entryForm = ccm.getConfigNode(i, "Pokemon-Data", "Form").getString();
                        entryMin = ccm.getConfigNode(i, "Pokemon-Data", "Min-Level").getInt();
                        entryMax = ccm.getConfigNode(i, "Pokemon-Data", "Max-Level").getInt();
                        entrySpawnData = ccm.getConfigNode(i, "Spawn-Data").getValue(new TypeToken<>() {});

                    }
                    CaveSpawn caveSpawn = new CaveSpawn(species, entryForm, entryMin, entryMax, entrySpawnData);
                    caveSpawnList.add(caveSpawn);

                }
                ccm.save(); // so we save our new Pokemon
                areaSpawns.setCaveSpawns(caveSpawnList);
                break;

            case "fish":
                spawns = new ArrayList<>(bcm.getConfigNode(0, "Fish-Spawns").getList(TypeToken.of(String.class)));
                spawns.add(pokemon);
                bcm.getConfigNode(0, "Fish-Spawns").setValue(spawns);
                bcm.save();
                ccm = new ComplexConfigManager(spawns, "fish-spawns", "fishSpawnTemplate.conf", dir, SpawnManager.class, SpawnManager.MOD_NAME, SpawnManager.MOD_ID, SpawnManager.logger);
                ccm.init();
                List<FishSpawn> fishSpawnList = areaSpawns.getFishSpawns();
                for (int i = 0; i < spawns.size(); i++) {

                    String entry = spawns.get(i);
                    String species = entry.replace(".conf", "");
                    String entryForm;
                    int entryMin;
                    int entryMax;
                    Map<String, Map<String, Map<String, String>>> entrySpawnData;
                    if (entry.equalsIgnoreCase(pokemon)) {

                        // this is the Pokémon we just added
                        entryForm = form;
                        entryMin = minLevel;
                        entryMax = maxLevel;
                        entrySpawnData = spawnData;
                        ccm.getConfigNode(i, "Pokemon-Data", "Species").setValue(species);
                        ccm.getConfigNode(i, "Pokemon-Data", "Form").setValue(entryForm);
                        ccm.getConfigNode(i, "Pokemon-Data", "Min-Level").setValue(entryMin);
                        ccm.getConfigNode(i, "Pokemon-Data", "Max-Level").setValue(entryMax);
                        ccm.getConfigNode(i, "Spawn-Data").setValue(entrySpawnData);

                    } else {

                        entryForm = ccm.getConfigNode(i, "Pokemon-Data", "Form").getString();
                        entryMin = ccm.getConfigNode(i, "Pokemon-Data", "Min-Level").getInt();
                        entryMax = ccm.getConfigNode(i, "Pokemon-Data", "Max-Level").getInt();
                        entrySpawnData = ccm.getConfigNode(i, "Spawn-Data").getValue(new TypeToken<>() {});

                    }

                    FishSpawn fishSpawn = new FishSpawn(species, entryForm, entryMin, entryMax, entrySpawnData);
                    fishSpawnList.add(fishSpawn);

                }
                ccm.save();
                areaSpawns.setFishSpawns(fishSpawnList);
                break;

            case "grass":
                spawns = new ArrayList<>(bcm.getConfigNode(0, "Grass-Spawns").getList(TypeToken.of(String.class)));
                spawns.add(pokemon);
                bcm.getConfigNode(0, "Grass-Spawns").setValue(spawns);
                bcm.save();
                ccm = new ComplexConfigManager(spawns, "grass-spawns", "grassSpawnTemplate.conf", dir, SpawnManager.class, SpawnManager.MOD_NAME, SpawnManager.MOD_ID, SpawnManager.logger);
                ccm.init();
                List<GrassSpawn> grassSpawnList = areaSpawns.getGrassSpawns();
                for (int i = 0; i < spawns.size(); i++) {

                    String entry = spawns.get(i);
                    String species = entry.replace(".conf", "");
                    String entryForm;
                    int entryMin;
                    int entryMax;
                    Map<String, Map<String, Map<String, String>>> entrySpawnData;
                    if (entry.equalsIgnoreCase(pokemon)) {

                        // this is the Pokémon we just added
                        entryForm = form;
                        entryMin = minLevel;
                        entryMax = maxLevel;
                        entrySpawnData = spawnData;
                        ccm.getConfigNode(i, "Pokemon-Data", "Species").setValue(species);
                        ccm.getConfigNode(i, "Pokemon-Data", "Form").setValue(entryForm);
                        ccm.getConfigNode(i, "Pokemon-Data", "Min-Level").setValue(entryMin);
                        ccm.getConfigNode(i, "Pokemon-Data", "Max-Level").setValue(entryMax);
                        ccm.getConfigNode(i, "Spawn-Data").setValue(entrySpawnData);

                    } else {

                        entryForm = ccm.getConfigNode(i, "Pokemon-Data", "Form").getString();
                        entryMin = ccm.getConfigNode(i, "Pokemon-Data", "Min-Level").getInt();
                        entryMax = ccm.getConfigNode(i, "Pokemon-Data", "Max-Level").getInt();
                        entrySpawnData = ccm.getConfigNode(i, "Spawn-Data").getValue(new TypeToken<>() {});

                    }

                    GrassSpawn grassSpawn = new GrassSpawn(species, entryForm, entryMin, entryMax, entrySpawnData);
                    grassSpawnList.add(grassSpawn);

                }
                ccm.save();
                areaSpawns.setGrassSpawns(grassSpawnList);
                break;

            case "headbutt":
                spawns = new ArrayList<>(bcm.getConfigNode(0, "Headbutt-Spawns").getList(TypeToken.of(String.class)));
                spawns.add(pokemon);
                bcm.getConfigNode(0, "Headbutt-Spawns").setValue(spawns);
                bcm.save();
                ccm = new ComplexConfigManager(spawns, "headbutt-spawns", "headbuttSpawnTemplate.conf", dir, SpawnManager.class, SpawnManager.MOD_NAME, SpawnManager.MOD_ID, SpawnManager.logger);
                ccm.init();
                List<HeadbuttSpawn> headbuttSpawnList = areaSpawns.getHeadbuttSpawns();
                for (int i = 0; i < spawns.size(); i++) {

                    String entry = spawns.get(i);
                    String species = entry.replace(".conf", "");
                    String entryForm;
                    int entryMin;
                    int entryMax;
                    Map<String, Map<String, Map<String, String>>> entrySpawnData;
                    if (entry.equalsIgnoreCase(pokemon)) {

                        // this is the Pokémon we just added
                        entryForm = form;
                        entryMin = minLevel;
                        entryMax = maxLevel;
                        entrySpawnData = spawnData;
                        ccm.getConfigNode(i, "Pokemon-Data", "Species").setValue(species);
                        ccm.getConfigNode(i, "Pokemon-Data", "Form").setValue(entryForm);
                        ccm.getConfigNode(i, "Pokemon-Data", "Min-Level").setValue(entryMin);
                        ccm.getConfigNode(i, "Pokemon-Data", "Max-Level").setValue(entryMax);
                        ccm.getConfigNode(i, "Spawn-Data").setValue(entrySpawnData);

                    } else {

                        entryForm = ccm.getConfigNode(i, "Pokemon-Data", "Form").getString();
                        entryMin = ccm.getConfigNode(i, "Pokemon-Data", "Min-Level").getInt();
                        entryMax = ccm.getConfigNode(i, "Pokemon-Data", "Max-Level").getInt();
                        entrySpawnData = ccm.getConfigNode(i, "Spawn-Data").getValue(new TypeToken<>() {});

                    }

                    HeadbuttSpawn headbuttSpawn = new HeadbuttSpawn(species, entryForm, entryMin, entryMax, entrySpawnData);
                    headbuttSpawnList.add(headbuttSpawn);

                }
                ccm.save();
                areaSpawns.setHeadbuttSpawns(headbuttSpawnList);
                break;

            case "natural":
                spawns = new ArrayList<>(bcm.getConfigNode(0, "Natural-Spawns").getList(TypeToken.of(String.class)));
                spawns.add(pokemon);
                bcm.getConfigNode(0, "Natural-Spawns").setValue(spawns);
                bcm.save();
                ccm = new ComplexConfigManager(spawns, "natural-spawns", "naturalSpawnTemplate.conf", dir, SpawnManager.class, SpawnManager.MOD_NAME, SpawnManager.MOD_ID, SpawnManager.logger);
                ccm.init();
                List<NaturalSpawn> naturalSpawnsList = areaSpawns.getNaturalSpawns();
                for (int i = 0; i < spawns.size(); i++) {

                    String entry = spawns.get(i);
                    String species = entry.replace(".conf", "");
                    String entryForm;
                    int entryMin;
                    int entryMax;
                    Map<String, Map<String, Map<String, String>>> entrySpawnData;
                    if (entry.equalsIgnoreCase(pokemon)) {

                        // this is the Pokémon we just added
                        entryForm = form;
                        entryMin = minLevel;
                        entryMax = maxLevel;
                        entrySpawnData = spawnData;
                        ccm.getConfigNode(i, "Pokemon-Data", "Species").setValue(species);
                        ccm.getConfigNode(i, "Pokemon-Data", "Form").setValue(entryForm);
                        ccm.getConfigNode(i, "Pokemon-Data", "Min-Level").setValue(entryMin);
                        ccm.getConfigNode(i, "Pokemon-Data", "Max-Level").setValue(entryMax);
                        ccm.getConfigNode(i, "Spawn-Data").setValue(entrySpawnData);

                    } else {

                        entryForm = ccm.getConfigNode(i, "Pokemon-Data", "Form").getString();
                        entryMin = ccm.getConfigNode(i, "Pokemon-Data", "Min-Level").getInt();
                        entryMax = ccm.getConfigNode(i, "Pokemon-Data", "Max-Level").getInt();
                        entrySpawnData = ccm.getConfigNode(i, "Spawn-Data").getValue(new TypeToken<>() {});

                    }

                    NaturalSpawn naturalSpawn = new NaturalSpawn(species, entryForm, entryMin, entryMax, entrySpawnData);
                    naturalSpawnsList.add(naturalSpawn);

                }
                ccm.save();
                areaSpawns.setNaturalSpawns(naturalSpawnsList);
                break;

            case "rocksmash":
                spawns = new ArrayList<>(bcm.getConfigNode(0, "Rock-Smash-Spawns").getList(TypeToken.of(String.class)));
                spawns.add(pokemon);
                bcm.getConfigNode(0, "Rock-Smash-Spawns").setValue(spawns);
                bcm.save();
                ccm = new ComplexConfigManager(spawns, "rock-smash-spawns", "rockSmashTemplate.conf", dir, SpawnManager.class, SpawnManager.MOD_NAME, SpawnManager.MOD_ID, SpawnManager.logger);
                ccm.init();
                List<RockSmashSpawn> rockSmashSpawnList = areaSpawns.getRockSmashSpawns();
                for (int i = 0; i < spawns.size(); i++) {

                    String entry = spawns.get(i);
                    String species = entry.replace(".conf", "");
                    String entryForm;
                    int entryMin;
                    int entryMax;
                    Map<String, Map<String, Map<String, String>>> entrySpawnData;
                    if (entry.equalsIgnoreCase(pokemon)) {

                        // this is the Pokémon we just added
                        entryForm = form;
                        entryMin = minLevel;
                        entryMax = maxLevel;
                        entrySpawnData = spawnData;
                        ccm.getConfigNode(i, "Pokemon-Data", "Species").setValue(species);
                        ccm.getConfigNode(i, "Pokemon-Data", "Form").setValue(entryForm);
                        ccm.getConfigNode(i, "Pokemon-Data", "Min-Level").setValue(entryMin);
                        ccm.getConfigNode(i, "Pokemon-Data", "Max-Level").setValue(entryMax);
                        ccm.getConfigNode(i, "Spawn-Data").setValue(entrySpawnData);

                    } else {

                        entryForm = ccm.getConfigNode(i, "Pokemon-Data", "Form").getString();
                        entryMin = ccm.getConfigNode(i, "Pokemon-Data", "Min-Level").getInt();
                        entryMax = ccm.getConfigNode(i, "Pokemon-Data", "Max-Level").getInt();
                        entrySpawnData = ccm.getConfigNode(i, "Spawn-Data").getValue(new TypeToken<>() {});

                    }

                    RockSmashSpawn rockSmashSpawn = new RockSmashSpawn(species, entryForm, entryMin, entryMax, entrySpawnData);
                    rockSmashSpawnList.add(rockSmashSpawn);

                }
                ccm.save();
                areaSpawns.setRockSmashSpawns(rockSmashSpawnList);
                break;

            default:
                spawns = new ArrayList<>(bcm.getConfigNode(0, "Surf-Spawns").getList(TypeToken.of(String.class)));
                spawns.add(pokemon);
                bcm.getConfigNode(0, "Surf-Spawns").setValue(spawns);
                bcm.save();
                ccm = new ComplexConfigManager(spawns, "surf-spawns", "surfTemplate.conf", dir, SpawnManager.class, SpawnManager.MOD_NAME, SpawnManager.MOD_ID, SpawnManager.logger);
                ccm.init();
                List<SurfSpawn> surfSpawnList = areaSpawns.getSurfSpawns();
                for (int i = 0; i < spawns.size(); i++) {

                    String entry = spawns.get(i);
                    String species = entry.replace(".conf", "");
                    String entryForm;
                    int entryMin;
                    int entryMax;
                    Map<String, Map<String, Map<String, String>>> entrySpawnData;
                    if (entry.equalsIgnoreCase(pokemon)) {

                        // this is the Pokémon we just added
                        entryForm = form;
                        entryMin = minLevel;
                        entryMax = maxLevel;
                        entrySpawnData = spawnData;
                        ccm.getConfigNode(i, "Pokemon-Data", "Species").setValue(species);
                        ccm.getConfigNode(i, "Pokemon-Data", "Form").setValue(entryForm);
                        ccm.getConfigNode(i, "Pokemon-Data", "Min-Level").setValue(entryMin);
                        ccm.getConfigNode(i, "Pokemon-Data", "Max-Level").setValue(entryMax);
                        ccm.getConfigNode(i, "Spawn-Data").setValue(entrySpawnData);

                    } else {

                        entryForm = ccm.getConfigNode(i, "Pokemon-Data", "Form").getString();
                        entryMin = ccm.getConfigNode(i, "Pokemon-Data", "Min-Level").getInt();
                        entryMax = ccm.getConfigNode(i, "Pokemon-Data", "Max-Level").getInt();
                        entrySpawnData = ccm.getConfigNode(i, "Spawn-Data").getValue(new TypeToken<>() {});

                    }

                    SurfSpawn surfSpawn = new SurfSpawn(species, entryForm, entryMin, entryMax, entrySpawnData);
                    surfSpawnList.add(surfSpawn);

                }
                ccm.save();
                areaSpawns.setSurfSpawns(surfSpawnList);
                break;

        }

    }

    public static void loadAreas() throws IOException, ObjectMappingException {

        SpawnManager.logger.info("[SpawnManager] Loading data..");
        areasWithNaturalSpawns = 0;
        String[] files = new String[]{"settings.conf"};
        for (Map.Entry<String, Region> regionMap : RegionHandler.regionMap.entrySet()) {

            String regionName = regionMap.getKey();
            Region region = regionMap.getValue();
            List<Area> areas = region.getAreas();
            for (Area area : areas) {

                boolean needsSaving = false;
                String areaName = area.getName();
                SpawnManager.logger.info("[SpawnManager] Creating spawn files for area: " + areaName);
                Path dir = ConfigUtils.checkDir(Paths.get("./config/spawnmanager/regions/" + regionName + "/" + areaName));
                BasicConfigManager bcm = new BasicConfigManager(files, dir, SpawnManager.class, SpawnManager.MOD_NAME, SpawnManager.MOD_ID, SpawnManager.logger);
                bcm.init();

                boolean autoCaveBattle = true;
                List<String> caveBlockIDs = new ArrayList<>();
                boolean despawnAfterCaveBattle = true;
                Map<String, String> caveMessages = new HashMap<>();
                caveMessages.put("Spawn-Message", "&eA wild &b%pokemon%&e appeared!");
                caveMessages.put("Spawn-Message-Shiny", "&eA wild &4%pokemon%&e appeared!");
                double caveSpawnChance = 0.35;
                if (bcm.getConfigNode(0, "Cave-Spawner").isVirtual()) {

                    needsSaving = true;
                    bcm.getConfigNode(0, "Cave-Spawner", "Auto-Battle").setValue(autoCaveBattle);
                    bcm.getConfigNode(0, "Cave-Spawner", "Block-IDs").setValue(caveBlockIDs);
                    bcm.getConfigNode(0, "Cave-Spawner", "Despawn-After-Battle").setValue(despawnAfterCaveBattle);
                    bcm.getConfigNode(0, "Cave-Spawner", "Messages").setValue(caveMessages);
                    bcm.getConfigNode(0, "Cave-Spawner", "Spawn-Attempt-Chance").setValue(caveSpawnChance);
                    bcm.getConfigNode(0, "Cave-Spawns").setValue(new ArrayList<>());

                } else {

                    autoCaveBattle = bcm.getConfigNode(0, "Cave-Spawner", "Auto-Battle").getBoolean();
                    caveBlockIDs = bcm.getConfigNode(0, "Cave-Spawner", "Block-IDs").getList(TypeToken.of(String.class));
                    despawnAfterCaveBattle = bcm.getConfigNode(0, "Cave-Spawner", "Despawn-After-Battle").getBoolean();
                    caveMessages = bcm.getConfigNode(0, "Cave-Spawner", "Messages").getValue(new TypeToken<Map<String, String>>() {});
                    caveSpawnChance = bcm.getConfigNode(0, "Cave-Spawner", "Spawn-Attempt-Chance").getDouble();

                }
                double caveShinyChance = -1;
                if (bcm.getConfigNode(0, "Cave-Spawner", "Shiny-Chance").isVirtual()) {

                    if (!needsSaving) needsSaving = true;
                    bcm.getConfigNode(0, "Cave-Spawner", "Shiny-Chance").setValue(caveShinyChance);
                    bcm.getConfigNode(0, "Grass-Spawner", "Shiny-Chance").setValue(caveShinyChance);
                    bcm.getConfigNode(0, "Natural-Spawner", "Shiny-Chance").setValue(caveShinyChance);
                    bcm.getConfigNode(0, "Surf-Spawner", "Shiny-Chance").setValue(caveShinyChance);

                } else {

                    caveShinyChance = bcm.getConfigNode(0, "Cave-Spawner", "Shiny-Chance").getDouble();

                }
                boolean cavePreventCobblemonSpawns = true;
                if (bcm.getConfigNode(0, "Cave-Spawner", "Prevent-Cobblemon-Spawns").isVirtual()) {

                    if (!needsSaving) needsSaving = true;
                    bcm.getConfigNode(0, "Cave-Spawner", "Prevent-Cobblemon-Spawns").setValue(true);

                } else {

                    cavePreventCobblemonSpawns = bcm.getConfigNode(0, "Cave-Spawner", "Prevent-Cobblemon-Spawns").getBoolean();

                }
                int caveSpawnLimit = 20;
                if (bcm.getConfigNode(0, "Cave-Spawner", "Spawn-Limit").isVirtual()) {

                    if (!needsSaving) needsSaving = true;
                    bcm.getConfigNode(0, "Cave-Spawner", "Spawn-Limit").setValue(20);

                } else {

                    caveSpawnLimit = bcm.getConfigNode(0, "Cave-Spawner", "Spawn-Limit").getInt();

                }
                int caveSpawnRadius = 15;
                if (bcm.getConfigNode(0, "Cave-Spawner", "Spawn-Radius").isVirtual()) {

                    if (!needsSaving) needsSaving = true;
                    bcm.getConfigNode(0, "Cave-Spawner", "Spawn-Radius").setValue(15);

                } else {

                    caveSpawnRadius = bcm.getConfigNode(0, "Cave-Spawner", "Spawn-Radius").getInt();

                }
                CaveSpawnerSettings caveSpawnerSettings = new CaveSpawnerSettings(autoCaveBattle, caveBlockIDs, despawnAfterCaveBattle, caveMessages, cavePreventCobblemonSpawns, caveShinyChance, caveSpawnChance, caveSpawnLimit, caveSpawnRadius);

                boolean clearFishSpawns = bcm.getConfigNode(0, "Fish-Spawner", "Clear-Spawns").getBoolean();
                boolean despawnAfterFishBattle = bcm.getConfigNode(0, "Fish-Spawner", "Despawn-After-Battle").getBoolean();
                int fishDespawnTimer = bcm.getConfigNode(0, "Fish-Spawner", "Despawn-Timer").getInt();
                double fishShinyChance = 0.01;
                if (bcm.getConfigNode(0, "Fish-Spawner", "Shiny-Chance").isVirtual()) {

                    if (!needsSaving) needsSaving = true;
                    bcm.getConfigNode(0, "Fish-Spawner", "Shiny-Chance").setValue(0.01);

                } else {

                    fishShinyChance = bcm.getConfigNode(0, "Fish-Spawner", "Shiny-Chance").getDouble();

                }
                FishSpawnerSettings fishSpawnerSettings = new FishSpawnerSettings(clearFishSpawns, despawnAfterFishBattle, fishDespawnTimer, fishShinyChance);

                boolean autoGrassBattle = bcm.getConfigNode(0, "Grass-Spawner", "Auto-Battle").getBoolean();
                List<String> blockIDs = bcm.getConfigNode(0, "Grass-Spawner", "Block-IDs").getList(TypeToken.of(String.class));
                boolean despawnAfterGrassBattle = bcm.getConfigNode(0, "Grass-Spawner", "Despawn-After-Battle").getBoolean();
                Map<String, String> grassMessages = bcm.getConfigNode(0, "Grass-Spawner", "Messages").getValue(new TypeToken<Map<String, String>>() {});
                boolean grassPreventCobblemonSpawns = true;
                if (bcm.getConfigNode(0, "Grass-Spawner", "Prevent-Cobblemon-Spawns").isVirtual()) {

                    if (!needsSaving) needsSaving = true;
                    bcm.getConfigNode(0, "Grass-Spawner", "Prevent-Cobblemon-Spawns").setValue(true);

                } else {

                    grassPreventCobblemonSpawns = bcm.getConfigNode(0, "Grass-Spawner", "Prevent-Cobblemon-Spawns").getBoolean();

                }
                double grassShinyChance = -1;
                if (!bcm.getConfigNode(0, "Grass-Spawner", "Shiny-Chance").isVirtual()) {

                    grassShinyChance = bcm.getConfigNode(0, "Grass-Spawner", "Shiny-Chance").getDouble();

                } else {

                    if (!needsSaving) needsSaving = true;
                    bcm.getConfigNode(0, "Grass-Spawner", "Shiny-Chance").setValue(0.01);

                }
                double grassSpawnChance = bcm.getConfigNode(0, "Grass-Spawner", "Spawn-Attempt-Chance").getDouble();
                int grassSpawnLimit = 20;
                if (bcm.getConfigNode(0, "Grass-Spawner", "Spawn-Limit").isVirtual()) {

                    if (!needsSaving) needsSaving = true;
                    bcm.getConfigNode(0, "Grass-Spawner", "Spawn-Limit").setValue(20);

                } else {

                    grassSpawnLimit = bcm.getConfigNode(0, "Grass-Spawner", "Spawn-Limit").getInt();

                }
                int grassSpawnRadius = 15;
                if (bcm.getConfigNode(0, "Grass-Spawner", "Spawn-Radius").isVirtual()) {

                    if (!needsSaving) needsSaving = true;
                    bcm.getConfigNode(0, "Grass-Spawner", "Spawn-Radius").setValue(15);

                } else {

                    grassSpawnRadius = bcm.getConfigNode(0, "Grass-Spawner", "Spawn-Radius").getInt();

                }
                GrassSpawnerSettings grassSpawnerSettings = new GrassSpawnerSettings(autoGrassBattle, blockIDs, despawnAfterGrassBattle, grassMessages, grassPreventCobblemonSpawns, grassShinyChance, grassSpawnChance, grassSpawnLimit, grassSpawnRadius);

                double headbuttAutoBattleChance = bcm.getConfigNode(0, "Headbutt-Spawner", "Auto-Battle-Chance").getDouble();
                boolean clearHeadbuttSpawns = bcm.getConfigNode(0, "Headbutt-Spawner", "Clear-Spawns").getBoolean();
                int headbuttCooldown = bcm.getConfigNode(0, "Headbutt-Spawner", "Cooldown").getInt();
                List<String> customHeadbuttTreeIDs = bcm.getConfigNode(0, "Headbutt-Spawner", "Custom-Headbutt-Tree-IDs").getList(TypeToken.of(String.class));
                boolean despawnHeadbuttAfterBattle = bcm.getConfigNode(0, "Headbutt-Spawner", "Despawn-After-Battle").getBoolean();
                int headbuttDespawnTimer = bcm.getConfigNode(0, "Headbutt-Spawner", "Despawn-Timer").getInt();
                boolean reduceHeadbuttPP = bcm.getConfigNode(0, "Headbutt-Spawner", "Reduce-PP").getBoolean();
                boolean requireHeadbuttMove = bcm.getConfigNode(0, "Headbutt-Spawner", "Require-Move").getBoolean();
                boolean usePixelmonsHeadbuttSystem = bcm.getConfigNode(0, "Headbutt-Spawner", "Use-Pixelmons-System").getBoolean();
                HeadbuttSpawnerSettings headbuttSpawnerSettings = new HeadbuttSpawnerSettings(headbuttAutoBattleChance, clearHeadbuttSpawns, headbuttCooldown, customHeadbuttTreeIDs,
                        despawnHeadbuttAfterBattle, headbuttDespawnTimer, reduceHeadbuttPP, requireHeadbuttMove, usePixelmonsHeadbuttSystem);

                double naturalAutoBattleChance = bcm.getConfigNode(0, "Natural-Spawner", "Auto-Battle-Chance").getDouble();
                boolean clearNaturalSpawns = bcm.getConfigNode(0, "Natural-Spawner", "Clear-Spawns").getBoolean();
                boolean despawnNaturalAfterBattle = bcm.getConfigNode(0, "Natural-Spawner", "Despawn-After-Battle").getBoolean();
                int naturalDespawnTimer = bcm.getConfigNode(0, "Natural-Spawner", "Despawn-Timer").getInt();
                boolean limitSpawns = bcm.getConfigNode(0, "Natural-Spawner", "Limit-Spawns").getBoolean();
                Map<String, String> messages = bcm.getConfigNode(0, "Natural-Spawner", "Messages").getValue(new TypeToken<Map<String, String>>() {});
                boolean preventCobblemonSpawns = bcm.getConfigNode(0, "Natural-Spawner", "Prevent-Cobblemon-Spawns").getBoolean();
                int spawnInterval = bcm.getConfigNode(0, "Natural-Spawner", "Spawn-Interval").getInt();
                int naturalSpawnLimit = 20;
                if (bcm.getConfigNode(0, "Natural-Spawner", "Spawn-Limit").isVirtual()) {

                    if (!needsSaving) needsSaving = true;
                    bcm.getConfigNode(0, "Natural-Spawner", "Spawn-Limit").setValue(20);

                } else {

                    naturalSpawnLimit = bcm.getConfigNode(0, "Natural-Spawner", "Spawn-Limit").getInt();

                }
                double naturalShinyChance = -1;
                if (!bcm.getConfigNode(0, "Natural-Spawner", "Shiny-Chance").isVirtual()) {

                    naturalShinyChance = bcm.getConfigNode(0, "Natural-Spawner", "Shiny-Chance").getDouble();

                }
                int naturalSpawnRadius = 15;
                if (!bcm.getConfigNode(0, "Natural-Spawner", "Spawn-Radius").isVirtual()) {

                    naturalSpawnRadius = bcm.getConfigNode(0, "Natural-Spawner", "Spawn-Radius").getInt();

                } else {

                    if (!needsSaving) needsSaving = true;
                    bcm.getConfigNode(0, "Natural-Spawner", "Spawn-Radius").setValue(15);

                }
                NaturalSpawnerSettings naturalSpawnerSettings = new NaturalSpawnerSettings(naturalAutoBattleChance, clearNaturalSpawns, despawnNaturalAfterBattle, naturalDespawnTimer, limitSpawns, messages, preventCobblemonSpawns, naturalShinyChance, spawnInterval, naturalSpawnLimit, naturalSpawnRadius);

                double rockSmashAutoBattleChance = bcm.getConfigNode(0, "Rock-Smash-Spawner", "Auto-Battle-Chance").getDouble();
                boolean clearRockSmashSpawns = bcm.getConfigNode(0, "Rock-Smash-Spawner", "Clear-Spawns").getBoolean();
                int rockSmashCooldown = bcm.getConfigNode(0, "Rock-Smash-Spawner", "Cooldown").getInt();
                List<String> customRockSmashIDs = bcm.getConfigNode(0, "Rock-Smash-Spawner", "Custom-Rock-Smash-Rock-IDs").getList(TypeToken.of(String.class));
                boolean despawnRockSmashAfterBattle = bcm.getConfigNode(0, "Rock-Smash-Spawner", "Despawn-After-Battle").getBoolean();
                int rockSmashDespawnTimer = bcm.getConfigNode(0, "Rock-Smash-Spawner", "Despawn-Timer").getInt();
                boolean reduceRockSmashPP = bcm.getConfigNode(0, "Rock-Smash-Spawner", "Reduce-PP").getBoolean();
                boolean requireRockSmashMove = bcm.getConfigNode(0, "Rock-Smash-Spawner", "Require-Move").getBoolean();
                boolean usePixelmonsRockSmashSystem = bcm.getConfigNode(0, "Rock-Smash-Spawner", "Use-Pixelmons-System").getBoolean();
                RockSmashSpawnerSettings rockSmashSpawnerSettings = new RockSmashSpawnerSettings(rockSmashAutoBattleChance, clearRockSmashSpawns, rockSmashCooldown, customRockSmashIDs,
                        despawnRockSmashAfterBattle, rockSmashDespawnTimer, reduceRockSmashPP, requireRockSmashMove, usePixelmonsRockSmashSystem);

                boolean autoSurfBattle = bcm.getConfigNode(0, "Surf-Spawner", "Auto-Battle").getBoolean();
                List<String> surfBlockIDs = bcm.getConfigNode(0, "Surf-Spawner", "Block-IDs").getList(TypeToken.of(String.class));
                boolean despawnAfterSurfBattle = bcm.getConfigNode(0, "Surf-Spawner", "Despawn-After-Battle").getBoolean();
                Map<String, String> surfMessages = bcm.getConfigNode(0, "Surf-Spawner", "Messages").getValue(new TypeToken<Map<String, String>>() {});
                boolean surfPreventCobblemonSpawns = true;
                if (bcm.getConfigNode(0, "Surf-Spawner", "Prevent-Cobblemon-Spawns").isVirtual()) {

                    if (!needsSaving) needsSaving = true;
                    bcm.getConfigNode(0, "Surf-Spawner", "Prevent-Cobblemon-Spawns").setValue(true);

                } else {

                    surfPreventCobblemonSpawns = bcm.getConfigNode(0, "Surf-Spawner", "Prevent-Cobblemon-Spawns").getBoolean();

                }
                double surfSpawnChance = bcm.getConfigNode(0, "Surf-Spawner", "Spawn-Attempt-Chance").getDouble();
                double surfShinyChance = -1;
                if (!bcm.getConfigNode(0, "Surf-Spawner", "Shiny-Chance").isVirtual()) {

                    surfShinyChance = bcm.getConfigNode(0, "Surf-Spawner", "Shiny-Chance").getDouble();

                } else {

                    if (!needsSaving) needsSaving = true;

                }
                int surfSpawnLimit = 20;
                if (bcm.getConfigNode(0, "Surf-Spawner", "Spawn-Limit").isVirtual()) {

                    if (!needsSaving) needsSaving = true;
                    bcm.getConfigNode(0, "Surf-Spawner", "Spawn-Limit").setValue(20);

                } else {

                    surfSpawnLimit = bcm.getConfigNode(0, "Surf-Spawner", "Spawn-Limit").getInt();

                }
                int surfSpawnRadius = 15;
                if (bcm.getConfigNode(0, "Surf-Spawner", "Spawn-Radius").isVirtual()) {

                    if (!needsSaving) needsSaving = true;
                    bcm.getConfigNode(0, "Surf-Spawner", "Spawn-Radius").setValue(15);

                } else {

                    surfSpawnRadius = bcm.getConfigNode(0, "Surf-Spawner", "Spawn-Radius").getInt();

                }
                SurfSpawnerSettings surfSpawnerSettings = new SurfSpawnerSettings(autoSurfBattle, surfBlockIDs, despawnAfterSurfBattle, surfMessages, surfPreventCobblemonSpawns, surfShinyChance, surfSpawnChance, surfSpawnLimit, surfSpawnRadius);

                SpawnArea a = new SpawnArea(area, caveSpawnerSettings, fishSpawnerSettings, grassSpawnerSettings, headbuttSpawnerSettings, naturalSpawnerSettings, rockSmashSpawnerSettings, surfSpawnerSettings);
                a.create();

                List<String> caveSpawns = bcm.getConfigNode(0, "Cave-Spawns").getList(TypeToken.of(String.class));
                ComplexConfigManager ccm = new ComplexConfigManager(caveSpawns, "cave-spawns", "caveSpawnTemplate.conf", dir, SpawnManager.class, SpawnManager.MOD_NAME, SpawnManager.MOD_ID, SpawnManager.logger);
                ccm.init();
                List<CaveSpawn> caveSpawnList = new ArrayList<>();
                for (int i = 0; i < caveSpawns.size(); i++) {

                    String species = ccm.getConfigNode(i, "Pokemon-Data", "Species").getString();
                    String form = ccm.getConfigNode(i, "Pokemon-Data", "Form").getString();
                    int minLevel = ccm.getConfigNode(i, "Pokemon-Data", "Min-Level").getInt();
                    int maxLevel = ccm.getConfigNode(i, "Pokemon-Data", "Max-Level").getInt();
                    Map<String, Map<String, Map<String, String>>> spawnData = ccm.getConfigNode(i, "Spawn-Data").getValue(new TypeToken<Map<String, Map<String, Map<String, String>>>>() {});

                    CaveSpawn caveSpawn = new CaveSpawn(species, form, minLevel, maxLevel, spawnData);
                    caveSpawnList.add(caveSpawn);

                }

                List<String> fishSpawns = bcm.getConfigNode(0, "Fish-Spawns").getList(TypeToken.of(String.class));
                ComplexConfigManager fcm = new ComplexConfigManager(fishSpawns, "fish-spawns", "fishSpawnTemplate.conf", dir, SpawnManager.class, SpawnManager.MOD_NAME, SpawnManager.MOD_ID, SpawnManager.logger);
                fcm.init();
                List<FishSpawn> fishSpawnsList = new ArrayList<>();
                for (int i = 0; i < fishSpawns.size(); i++) {

                    String species = fcm.getConfigNode(i, "Pokemon-Data", "Species").getString();
                    String form = fcm.getConfigNode(i, "Pokemon-Data", "Form").getString();
                    int minLevel = fcm.getConfigNode(i, "Pokemon-Data", "Min-Level").getInt();
                    int maxLevel = fcm.getConfigNode(i, "Pokemon-Data", "Max-Level").getInt();
                    Map<String, Map<String, Map<String, String>>> spawnData = fcm.getConfigNode(i, "Spawn-Data").getValue(new TypeToken<Map<String, Map<String, Map<String, String>>>>() {});

                    FishSpawn fishSpawn = new FishSpawn(species, form, minLevel, maxLevel, spawnData);
                    fishSpawnsList.add(fishSpawn);

                }

                List<String> grassSpawns = bcm.getConfigNode(0, "Grass-Spawns").getList(TypeToken.of(String.class));
                ComplexConfigManager gcm = new ComplexConfigManager(grassSpawns, "grass-spawns", "grassSpawnTemplate.conf", dir, SpawnManager.class, SpawnManager.MOD_NAME, SpawnManager.MOD_ID, SpawnManager.logger);
                gcm.init();
                List<GrassSpawn> grassSpawnsList = new ArrayList<>();
                for (int i = 0; i < grassSpawns.size(); i++) {

                    String species = gcm.getConfigNode(i, "Pokemon-Data", "Species").getString();
                    String form = gcm.getConfigNode(i, "Pokemon-Data", "Form").getString();
                    int minLevel = gcm.getConfigNode(i, "Pokemon-Data", "Min-Level").getInt();
                    int maxLevel = gcm.getConfigNode(i, "Pokemon-Data", "Max-Level").getInt();
                    Map<String, Map<String, Map<String, String>>> spawnData = gcm.getConfigNode(i, "Spawn-Data").getValue(new TypeToken<Map<String, Map<String, Map<String, String>>>>() {});

                    GrassSpawn grassSpawn = new GrassSpawn(species, form, minLevel, maxLevel, spawnData);
                    grassSpawnsList.add(grassSpawn);

                }

                List<String> headbuttSpawns = bcm.getConfigNode(0, "Headbutt-Spawns").getList(TypeToken.of(String.class));
                ComplexConfigManager hcm = new ComplexConfigManager(headbuttSpawns, "headbutt-spawns", "headbuttSpawnTemplate.conf", dir, SpawnManager.class, SpawnManager.MOD_NAME, SpawnManager.MOD_ID, SpawnManager.logger);
                hcm.init();
                List<HeadbuttSpawn> headbuttSpawnsList = new ArrayList<>();
                for (int i = 0; i < headbuttSpawns.size(); i++) {

                    String species = hcm.getConfigNode(i, "Pokemon-Data", "Species").getString();
                    String form = hcm.getConfigNode(i, "Pokemon-Data", "Form").getString();
                    int minLevel = hcm.getConfigNode(i, "Pokemon-Data", "Min-Level").getInt();
                    int maxLevel = hcm.getConfigNode(i, "Pokemon-Data", "Max-Level").getInt();
                    Map<String, Map<String, Map<String, String>>> spawnData = hcm.getConfigNode(i, "Spawn-Data").getValue(new TypeToken<Map<String, Map<String, Map<String, String>>>>() {});

                    HeadbuttSpawn headbuttSpawn = new HeadbuttSpawn(species, form, minLevel, maxLevel, spawnData);
                    headbuttSpawnsList.add(headbuttSpawn);

                }

                List<String> naturalSpawns = bcm.getConfigNode(0, "Natural-Spawns").getList(TypeToken.of(String.class));
                ComplexConfigManager ncm = new ComplexConfigManager(naturalSpawns, "natural-spawns", "naturalSpawnTemplate.conf", dir, SpawnManager.class, SpawnManager.MOD_NAME, SpawnManager.MOD_ID, SpawnManager.logger);
                ncm.init();
                List<NaturalSpawn> naturalSpawnsList = new ArrayList<>();
                for (int i = 0; i < naturalSpawns.size(); i++) {

                    String species = ncm.getConfigNode(i, "Pokemon-Data", "Species").getString();
                    String form = ncm.getConfigNode(i, "Pokemon-Data", "Form").getString();
                    int minLevel = ncm.getConfigNode(i, "Pokemon-Data", "Min-Level").getInt();
                    int maxLevel = ncm.getConfigNode(i, "Pokemon-Data", "Max-Level").getInt();

                    Map<String, Map<String, Map<String, String>>> spawnData = ncm.getConfigNode(i, "Spawn-Data").getValue(new TypeToken<Map<String, Map<String, Map<String, String>>>>() {});
                    NaturalSpawn naturalSpawn = new NaturalSpawn(species, form, minLevel, maxLevel, spawnData);
                    naturalSpawnsList.add(naturalSpawn);

                }

                if (!naturalSpawnsList.isEmpty()) {

                    areasWithNaturalSpawns++;
                    List<Area> areaList = new ArrayList<>();
                    if (naturalSpawnerAreas.containsKey(region)) {

                        areaList = naturalSpawnerAreas.get(region);

                    }
                    if (!areaList.contains(area)) {

                        areaList.add(area);

                    }
                    naturalSpawnerAreas.put(region, areaList);

                }

                List<String> rockSmashSpawns = bcm.getConfigNode(0, "Rock-Smash-Spawns").getList(TypeToken.of(String.class));
                ComplexConfigManager rcm = new ComplexConfigManager(rockSmashSpawns, "rock-smash-spawns", "rockSmashTemplate.conf", dir, SpawnManager.class, SpawnManager.MOD_NAME, SpawnManager.MOD_ID, SpawnManager.logger);
                rcm.init();
                List<RockSmashSpawn> rockSmashSpawnsList = new ArrayList<>();
                for (int i = 0; i < rockSmashSpawns.size(); i++) {

                    String species = rcm.getConfigNode(i, "Pokemon-Data", "Species").getString();
                    String form = rcm.getConfigNode(i, "Pokemon-Data", "Form").getString();
                    int minLevel = rcm.getConfigNode(i, "Pokemon-Data", "Min-Level").getInt();
                    int maxLevel = rcm.getConfigNode(i, "Pokemon-Data", "Max-Level").getInt();

                    Map<String, Map<String, Map<String, String>>> spawnData = rcm.getConfigNode(i, "Spawn-Data").getValue(new TypeToken<Map<String, Map<String, Map<String, String>>>>() {});
                    RockSmashSpawn rockSmashSpawn = new RockSmashSpawn(species, form, minLevel, maxLevel, spawnData);
                    rockSmashSpawnsList.add(rockSmashSpawn);

                }

                List<String> surfSpawns = bcm.getConfigNode(0, "Surf-Spawns").getList(TypeToken.of(String.class));
                ComplexConfigManager scm = new ComplexConfigManager(surfSpawns, "surf-spawns", "surfSpawnTemplate.conf", dir, SpawnManager.class, SpawnManager.MOD_NAME, SpawnManager.MOD_ID, SpawnManager.logger);
                scm.init();
                List<SurfSpawn> surfSpawnsList = new ArrayList<>();
                for (int i = 0; i < surfSpawns.size(); i++) {

                    String species = scm.getConfigNode(i, "Pokemon-Data", "Species").getString();
                    String form = scm.getConfigNode(i, "Pokemon-Data", "Form").getString();
                    int minLevel = scm.getConfigNode(i, "Pokemon-Data", "Min-Level").getInt();
                    int maxLevel = scm.getConfigNode(i, "Pokemon-Data", "Max-Level").getInt();
                    Map<String, Map<String, Map<String, String>>> spawnData = scm.getConfigNode(i, "Spawn-Data").getValue(new TypeToken<Map<String, Map<String, Map<String, String>>>>() {});

                    SurfSpawn surfSpawn = new SurfSpawn(species, form, minLevel, maxLevel, spawnData);
                    surfSpawnsList.add(surfSpawn);

                }

                AreaSpawns spawns = new AreaSpawns(a, caveSpawnList, fishSpawnsList, grassSpawnsList, headbuttSpawnsList, naturalSpawnsList, rockSmashSpawnsList, surfSpawnsList, bcm);
                areaSpawnMap.put(a, spawns);

                if (needsSaving) {

                    bcm.save();

                }

            }

        }

    }

}
