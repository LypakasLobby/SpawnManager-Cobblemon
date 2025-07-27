package com.lypaka.spawnmanager.GUIs.EditorMenus;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import ca.landonjw.gooeylibs2.api.page.GooeyPage;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate;
import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.item.PokemonItem;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.lypaka.areamanager.Areas.Area;
import com.lypaka.areamanager.Regions.Region;
import com.lypaka.lypakautils.Handlers.FancyTextHandler;
import com.lypaka.lypakautils.Handlers.ItemStackHandler;
import com.lypaka.shadow.configurate.objectmapping.ObjectMappingException;
import com.lypaka.spawnmanager.SpawnAreas.SpawnArea;
import com.lypaka.spawnmanager.SpawnAreas.SpawnAreaHandler;
import com.lypaka.spawnmanager.SpawnAreas.Spawns.AreaSpawns;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpawnDataEditorMainMenu {

    private Map<String, Map<String, Map<String, String>>> spawnData = null;
    private Region region;
    private Area area;
    private Pokemon pokemon;
    private String spawner;
    private String form;
    private int minLevel;
    private int maxLevel;

    public void open (ServerPlayerEntity player, Region region, Area area, Pokemon pokemon, String spawner, String form, int minLevel, int maxLevel) {

        this.region = region;
        this.area = area;
        this.pokemon = pokemon;
        this.spawner = spawner;
        this.form = form;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
        ChestTemplate template = ChestTemplate.builder(3).build();
        GooeyPage page = GooeyPage.builder().template(template).title(FancyTextHandler.getFormattedText("&aSpawn Data Editor")).build();
        for (int i = 0; i < 27; i++) {

            ItemStack border = ItemStackHandler.buildFromStringID("minecraft:green_stained_glass_pane");
            border.set(DataComponentTypes.CUSTOM_NAME, FancyTextHandler.getFormattedText(""));
            page.getTemplate().getSlot(i).setButton(GooeyButton.builder().display(border).build());

        }
        if (this.spawnData == null || this.spawnData.isEmpty()) {

            this.spawnData = new HashMap<>();
            // building default spawn data map
            Map<String, String> map1 = new HashMap<>();
            map1.put("Group-Size", "1");
            map1.put("Spawn-Chance", "0.30");
            map1.put("Spawn-Location", "land");
            map1.put("Shiny-Chance", String.valueOf(getSpawnersShinyRate(area, spawner)));
            Map<String, Map<String, String>> map2 = new HashMap<>();
            map2.put("Clear", map1);
            map2.put("Rain", map1);
            map2.put("Storm", map1);
            this.spawnData.put("Day", map2);
            this.spawnData.put("Night", map2);

        }

        ItemStack sprite = PokemonItem.from(pokemon);
        sprite.set(DataComponentTypes.CUSTOM_NAME, FancyTextHandler.getFormattedText("&e" + pokemon.getSpecies().getName()));
        page.getTemplate().getSlot(11).setButton(GooeyButton.builder().display(sprite).build());

        ItemStack scroll = ItemStackHandler.buildFromStringID("minecraft:skull_banner_pattern");
        scroll.set(DataComponentTypes.CUSTOM_NAME, FancyTextHandler.getFormattedText("&eSpawn Data:"));
        List<Text> lore = new ArrayList<>();
        lore.add(FancyTextHandler.getFormattedText("&eForm: &a" + form));
        lore.add(FancyTextHandler.getFormattedText("&eLevel Range: &a" + minLevel + " - " + maxLevel));
        lore.add(FancyTextHandler.getFormattedText("&bSpawn Data:"));
        lore.add(FancyTextHandler.getFormattedText(""));
        String data = "&eTime: &a%time%, &eWeather: &a%weather% &e--> &eGroup Size: &a%groupSize%, &eSpawn Chance: &a%spawnChance%, &eSpawn Location: &a%spawnLocation%, &eShiny Chance: &a%shinyChance%";
        for (Map.Entry<String, Map<String, Map<String, String>>> entry : this.spawnData.entrySet()) {

            String time = entry.getKey();
            Map<String, Map<String, String>> d = entry.getValue();
            for (Map.Entry<String, Map<String, String>> d2 : d.entrySet()) {

                String weather = d2.getKey();
                Map<String, String> d3 = d2.getValue();
                String groupSize = d3.getOrDefault("Group-Size", "1");
                String spawnChance = d3.get("Spawn-Chance");
                String spawnLocation = d3.get("Spawn-Location");
                String shinyChance = d3.getOrDefault("Shiny-Chance", String.valueOf(getSpawnersShinyRate(area, spawner)));
                lore.add(FancyTextHandler.getFormattedText(data
                        .replace("%time%", time)
                        .replace("%weather%", weather)
                        .replace("%groupSize%", groupSize)
                        .replace("%spawnChance%", spawnChance)
                        .replace("%spawnLocation%", spawnLocation)
                        .replace("%shinyChance%", shinyChance)
                ));
                lore.add(FancyTextHandler.getFormattedText(""));

            }

        }

        lore.add(FancyTextHandler.getFormattedText("&eCLICK ME TO EDIT"));
        scroll.set(DataComponentTypes.LORE, new LoreComponent(lore));
        page.getTemplate().getSlot(13).setButton(
                GooeyButton.builder()
                        .display(scroll)
                        .onClick(() -> {

                            TimeEditorMenu timeEditorMenu = new TimeEditorMenu(player, this);
                            timeEditorMenu.open();

                        })
                        .build()
        );

        ItemStack star = ItemStackHandler.buildFromStringID("minecraft:nether_star");
        star.set(DataComponentTypes.CUSTOM_NAME, FancyTextHandler.getFormattedText("&aCONFIRM"));
        List<Text> starLore = new ArrayList<>();
        starLore.add(FancyTextHandler.getFormattedText("&eClick me to confirm this spawn data and create the spawn file."));
        star.set(DataComponentTypes.LORE, new LoreComponent(starLore));
        page.getTemplate().getSlot(15).setButton(
                GooeyButton.builder()
                        .display(star)
                        .onClick(() -> {

                            try {

                                SpawnAreaHandler.addFile(region.getName(), area, spawner, pokemon.getSpecies().getName(), form, minLevel, maxLevel, this.spawnData);
                                UIManager.closeUI(player);
                                player.sendMessage(FancyTextHandler.getFormattedText("&aSuccessfully created spawn."));

                            } catch (ObjectMappingException e) {

                                throw new RuntimeException(e);

                            }

                        })
                        .build()
        );

        UIManager.openUIForcefully(player, page);

    }

    public Map<String, Map<String, Map<String, String>>> getSpawnData() {

        return this.spawnData;

    }

    public Region getRegion() {

        return this.region;

    }

    public Area getArea() {

        return this.area;

    }

    public Pokemon getPokemon() {

        return this.pokemon;

    }

    public String getForm() {

        return this.form;

    }

    public String getSpawner() {

        return this.spawner;

    }

    public int getMinLevel() {

        return this.minLevel;

    }

    public int getMaxLevel() {

        return this.maxLevel;

    }

    private double getSpawnersShinyRate (Area area, String spawner) {

        double value = Cobblemon.INSTANCE.getConfig().getShinyRate();
        SpawnArea spawnArea = SpawnAreaHandler.areaMap.getOrDefault(area, null);
        if (spawnArea == null) {

            return value;

        }

        switch (spawner.toLowerCase()) {

            case "cave":
                value = spawnArea.getCaveSpawnerSettings().getSpawnerShinyChance();
                break;

            case "grass":
                value = spawnArea.getGrassSpawnerSettings().getSpawnerShinyChance();
                break;

            case "surf":
                value = spawnArea.getSurfSpawnerSettings().getSpawnerShinyChance();
                break;

            case "natural":
                value = spawnArea.getNaturalSpawnerSettings().getSpawnerShinyChance();
                break;

            default:
                break;

        }

        return value;

    }

}
