package com.lypaka.spawnmanager.SpawnAreas.SpawnerSettings;

import java.util.List;
import java.util.Map;

public class GrassSpawnerSettings {

    private final boolean autoBattle;
    private final List<String> blockIDs;
    private final boolean despawnAfterBattle;
    private final Map<String, String> messagesMap;
    private final boolean preventCobblemonSpawns;
    private final double spawnerShinyChance;
    private final double spawnChance;
    private final int spawnLimit;
    private final int spawnRadius;

    public GrassSpawnerSettings (boolean autoBattle, List<String> blockIDs, boolean despawnAfterBattle,
                                 Map<String, String> messagesMap, boolean preventCobblemonSpawns, double spawnerShinyChance, double spawnChance,
                                 int spawnLimit, int spawnRadius) {

        this.autoBattle = autoBattle;
        this.blockIDs = blockIDs;
        this.despawnAfterBattle = despawnAfterBattle;
        this.messagesMap = messagesMap;
        this.preventCobblemonSpawns = preventCobblemonSpawns;
        this.spawnerShinyChance = spawnerShinyChance;
        this.spawnChance = spawnChance;
        this.spawnLimit = spawnLimit;
        this.spawnRadius = spawnRadius;

    }

    public boolean doesAutoBattle() {

        return this.autoBattle;

    }

    public List<String> getBlockIDs() {

        return this.blockIDs;

    }

    public boolean doesDespawnAfterBattle() {

        return this.despawnAfterBattle;

    }

    public Map<String, String> getMessagesMap() {

        return this.messagesMap;

    }

    public boolean doesPreventCobblemonSpawns() {

        return this.preventCobblemonSpawns;

    }

    public double getSpawnerShinyChance() {

        return this.spawnerShinyChance;

    }

    public double getSpawnChance() {

        return this.spawnChance;

    }

    public int getSpawnLimit() {

        return this.spawnLimit;

    }

    public int getSpawnRadius() {

        return this.spawnRadius;

    }

}
