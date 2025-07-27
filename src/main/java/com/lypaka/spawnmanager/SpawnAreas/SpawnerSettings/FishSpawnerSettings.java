package com.lypaka.spawnmanager.SpawnAreas.SpawnerSettings;

public class FishSpawnerSettings {

    private final boolean clearSpawns;
    private final boolean despawnAfterBattle;
    private final int despawnTimer;
    private final double spawnerShinyChance;

    public FishSpawnerSettings (boolean clearSpawns, boolean despawnAfterBattle, int despawnTimer, double spawnerShinyChance) {

        this.clearSpawns = clearSpawns;
        this.despawnAfterBattle = despawnAfterBattle;
        this.despawnTimer = despawnTimer;
        this.spawnerShinyChance = spawnerShinyChance;

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

    public double getSpawnerShinyChance() {

        return this.spawnerShinyChance;

    }

}
