package com.lypaka.spawnmanager.SpawnAreas.SpawnerSettings;

import java.util.Map;

public class NaturalSpawnerSettings {

    private final double autoBattleChance;
    private final boolean clearSpawns;
    private final boolean despawnAfterBattle;
    private final int despawnTimer;
    private final boolean limitSpawns;
    private final Map<String, String> messagesMap;
    private final boolean preventCobblemonSpawns;
    private final double spawnerShinyChance;
    private final int spawnInterval;
    private final int spawnLimit;
    private final int spawnRadius;

    public NaturalSpawnerSettings (double autoBattleChance, boolean clearSpawns, boolean despawnAfterBattle, int despawnTimer, boolean limitSpawns,
                                   Map<String, String> messagesMap, boolean preventCobblemonSpawns, double spawnerShinyChance, int spawnInterval, int spawnLimit, int spawnRadius) {

        this.autoBattleChance = autoBattleChance;
        this.clearSpawns = clearSpawns;
        this.despawnAfterBattle = despawnAfterBattle;
        this.despawnTimer = despawnTimer;
        this.limitSpawns = limitSpawns;
        this.messagesMap = messagesMap;
        this.preventCobblemonSpawns = preventCobblemonSpawns;
        this.spawnerShinyChance = spawnerShinyChance;
        this.spawnInterval = spawnInterval;
        this.spawnLimit = spawnLimit;
        this.spawnRadius = spawnRadius;

    }

    public double getAutoBattleChance() {

        return autoBattleChance;

    }

    public boolean doesClearSpawns() {

        return this.clearSpawns;

    }

    public boolean doesDespawnAfterBattle() {

        return this.despawnAfterBattle;

    }

    public int getDespawnTimer() {

        return this.despawnTimer;

    }

    public boolean doesLimitSpawns() {

        return this.limitSpawns;

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

    public int getSpawnInterval() {

        return this.spawnInterval;

    }

    public int getSpawnLimit() {

        return this.spawnLimit;

    }

    public int getSpawnRadius() {

        return this.spawnRadius;

    }

}
