package com.lypaka.spawnmanager.SpawnAreas.Spawns;

import java.util.Map;

public class SurfSpawn extends PokemonSpawn {

    private final Map<String, Map<String, Map<String, String>>> spawnData;

    public SurfSpawn (String species, String form, int minLevel, int maxLevel, Map<String, Map<String, Map<String, String>>> spawnData) {

        super(species, form, minLevel, maxLevel);
        this.spawnData = spawnData;

    }

    public Map<String, Map<String, Map<String, String>>> getSpawnData() {

        return this.spawnData;

    }

    public double getShinyChance (String time, String weather) {

        double chance = -2;
        boolean set = false;
        for (Map.Entry<String, Map<String, Map<String, String>>> entry : this.spawnData.entrySet()) {

            if (set) break;
            if (entry.getKey().equalsIgnoreCase(time) || entry.getKey().equalsIgnoreCase("Any")) {

                Map<String, Map<String, String>> value = entry.getValue();
                for (Map.Entry<String, Map<String, String>> e1 : value.entrySet()) {

                    if (e1.getKey().equalsIgnoreCase(weather) || e1.getKey().equalsIgnoreCase("Any")) {

                        Map<String, String> map = e1.getValue();
                        if (map.containsKey("Shiny-Chance")) {

                            chance = Double.parseDouble(map.get("Shiny-Chance"));
                            set = true;
                            break;

                        }

                    }

                }

            }

        }
        return chance;

    }

    public int getGroupSize (String time, String weather) {

        int groupSize = 1;
        boolean set = false;
        for (Map.Entry<String, Map<String, Map<String, String>>> entry : this.spawnData.entrySet()) {

            if (set) break;
            if (entry.getKey().equalsIgnoreCase(time) || entry.getKey().equalsIgnoreCase("Any")) {

                Map<String, Map<String, String>> value = entry.getValue();
                for (Map.Entry<String, Map<String, String>> e1 : value.entrySet()) {

                    if (e1.getKey().equalsIgnoreCase(weather) || e1.getKey().equalsIgnoreCase("Any")) {

                        Map<String, String> map = e1.getValue();
                        if (map.containsKey("Group-Size")) {

                            groupSize = Integer.parseInt(map.get("Group-Size"));
                            set = true;
                            break;

                        }

                    }

                }

            }

        }
        return groupSize;

    }

}