package com.lypaka.spawnmanager.SpawnAreas.Spawns;

import com.lypaka.lypakautils.ConfigurationLoaders.BasicConfigManager;
import com.lypaka.lypakautils.ConfigurationLoaders.ComplexConfigManager;
import com.lypaka.spawnmanager.SpawnAreas.SpawnArea;

import java.util.List;

public class AreaSpawns {

    private final SpawnArea area;
    private List<CaveSpawn> caveSpawns;
    private List<FishSpawn> fishSpawns;
    private List<GrassSpawn> grassSpawns;
    private List<HeadbuttSpawn> headbuttSpawns;
    private List<NaturalSpawn> naturalSpawns;
    private List<RockSmashSpawn> rockSmashSpawns;
    private List<SurfSpawn> surfSpawns;
    private BasicConfigManager configManager;

    public AreaSpawns (SpawnArea area, List<CaveSpawn> caveSpawns, List<FishSpawn> fish, List<GrassSpawn> grass, List<HeadbuttSpawn> headbutt, List<NaturalSpawn> natural, List<RockSmashSpawn> rockSmash,
                       List<SurfSpawn> surf, BasicConfigManager configManager) {

        this.area = area;
        this.caveSpawns = caveSpawns;
        this.fishSpawns = fish;
        this.grassSpawns = grass;
        this.headbuttSpawns = headbutt;
        this.naturalSpawns = natural;
        this.rockSmashSpawns = rockSmash;
        this.surfSpawns = surf;
        this.configManager = configManager;

    }

    public SpawnArea getArea() {

        return this.area;

    }

    public List<CaveSpawn> getCaveSpawns() {

        return this.caveSpawns;

    }

    public void setCaveSpawns (List<CaveSpawn> spawns) {

        this.caveSpawns = spawns;

    }

    public List<FishSpawn> getFishSpawns() {

        return this.fishSpawns;

    }

    public void setFishSpawns (List<FishSpawn> spawns) {

        this.fishSpawns = spawns;

    }

    public List<GrassSpawn> getGrassSpawns() {

        return this.grassSpawns;

    }

    public void setGrassSpawns (List<GrassSpawn> spawns) {

        this.grassSpawns = spawns;

    }

    public List<HeadbuttSpawn> getHeadbuttSpawns() {

        return this.headbuttSpawns;

    }

    public void setHeadbuttSpawns (List<HeadbuttSpawn> spawns) {

        this.headbuttSpawns = spawns;

    }

    public List<NaturalSpawn> getNaturalSpawns() {

        return this.naturalSpawns;

    }

    public void setNaturalSpawns (List<NaturalSpawn> spawns) {

        this.naturalSpawns = spawns;

    }

    public List<RockSmashSpawn> getRockSmashSpawns() {

        return this.rockSmashSpawns;

    }

    public void setRockSmashSpawns (List<RockSmashSpawn> spawns) {

        this.rockSmashSpawns = spawns;

    }

    public List<SurfSpawn> getSurfSpawns() {

        return this.surfSpawns;

    }

    public void setSurfSpawns (List<SurfSpawn> spawns) {

        this.surfSpawns = spawns;

    }

    public BasicConfigManager getConfigManager() {

        return this.configManager;

    }

}
