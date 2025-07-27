package com.lypaka.spawnmanager.GUIs.EditorMenus;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.button.ButtonClick;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import ca.landonjw.gooeylibs2.api.page.GooeyPage;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate;
import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.lypaka.areamanager.Areas.Area;
import com.lypaka.areamanager.Regions.Region;
import com.lypaka.lypakautils.Handlers.FancyTextHandler;
import com.lypaka.lypakautils.Handlers.ItemStackHandler;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataEditorMenu {

    private final ServerPlayerEntity player;
    private final WeatherEditorMenu weatherEditorMenu;
    private final String weatherBeingEdited;
    private final String currentTime;
    private final Map<String, String> data;

    public DataEditorMenu (ServerPlayerEntity player, WeatherEditorMenu weatherEditorMenu, String weatherBeingEdited) {

        this.player = player;
        this.weatherEditorMenu = weatherEditorMenu;
        this.weatherBeingEdited = weatherBeingEdited;
        this.currentTime = this.weatherEditorMenu.getTimeBeingEdited();
        this.data = this.weatherEditorMenu.getTimeEditorMenu().getMainMenu().getSpawnData().get(currentTime).get(this.weatherBeingEdited);

    }

    public void open() {

        ChestTemplate template = ChestTemplate.builder(5).build();
        GooeyPage page = GooeyPage.builder().template(template).title(FancyTextHandler.getFormattedText("&eEdit data")).build();
        ItemStack border = ItemStackHandler.buildFromStringID("minecraft:blue_stained_glass_pane");
        border.set(DataComponentTypes.CUSTOM_NAME, FancyTextHandler.getFormattedText(""));
        for (int i = 0; i < 45; i++) {

            page.getTemplate().getSlot(i).setButton(GooeyButton.builder().display(border).build());

        }

        // 11, 13, 15

        ItemStack metronome = ItemStackHandler.buildFromStringID("cobblemon:metronome");
        metronome.set(DataComponentTypes.CUSTOM_NAME, FancyTextHandler.getFormattedText("&eGroup Size"));
        List<Text> sizeLore = new ArrayList<>();
        String currentSize = this.data.get("Group-Size");
        sizeLore.add(FancyTextHandler.getFormattedText("&eCurrent Value: &a" + currentSize));
        sizeLore.add(FancyTextHandler.getFormattedText("&eLeft click to increase by 1"));
        sizeLore.add(FancyTextHandler.getFormattedText("&eShift left click to increase by 5"));
        sizeLore.add(FancyTextHandler.getFormattedText("&eRight click to decrease by 1"));
        sizeLore.add(FancyTextHandler.getFormattedText("&eShift right click to decrease by 5"));
        metronome.set(DataComponentTypes.LORE, new LoreComponent(sizeLore));
        page.getTemplate().getSlot(10).setButton(
                GooeyButton.builder()
                        .display(metronome)
                        .onClick(clickAction -> {

                            if (clickAction.getClickType() == ButtonClick.LEFT_CLICK) {

                                increaseSize(1);

                            } else if (clickAction.getClickType() == ButtonClick.SHIFT_LEFT_CLICK) {

                                increaseSize(5);

                            } else if (clickAction.getClickType() == ButtonClick.RIGHT_CLICK) {

                                decreaseSize(1);

                            } else if (clickAction.getClickType() == ButtonClick.SHIFT_RIGHT_CLICK) {

                                decreaseSize(5);

                            }
                            open();

                        })
                        .build()
        );

        ItemStack luckyEgg = ItemStackHandler.buildFromStringID("cobblemon:lucky_egg");
        luckyEgg.set(DataComponentTypes.CUSTOM_NAME, FancyTextHandler.getFormattedText("&eSpawn Chance"));
        List<Text> chanceLore = new ArrayList<>();
        String currentChance = this.data.get("Spawn-Chance");
        chanceLore.add(FancyTextHandler.getFormattedText("&eCurrent Value: &a " + currentChance));
        chanceLore.add(FancyTextHandler.getFormattedText("&eLeft click to increase by 0.01"));
        chanceLore.add(FancyTextHandler.getFormattedText("&eShift left click to increase by 0.05"));
        chanceLore.add(FancyTextHandler.getFormattedText("&eRight click to decrease by 0.01"));
        chanceLore.add(FancyTextHandler.getFormattedText("&eShift right click to decrease by 0.05"));
        luckyEgg.set(DataComponentTypes.LORE, new LoreComponent(chanceLore));
        page.getTemplate().getSlot(12).setButton(
                GooeyButton.builder()
                        .display(luckyEgg)
                        .onClick(clickAction -> {

                            if (clickAction.getClickType() == ButtonClick.LEFT_CLICK) {

                                increaseChance(0.01);

                            } else if (clickAction.getClickType() == ButtonClick.SHIFT_LEFT_CLICK) {

                                increaseChance(0.05);

                            } else if (clickAction.getClickType() == ButtonClick.RIGHT_CLICK) {

                                decreaseChance(0.01);

                            } else if (clickAction.getClickType() == ButtonClick.SHIFT_RIGHT_CLICK) {

                                decreaseChance(0.05);

                            }
                            open();

                        })
                        .build()
        );

        ItemStack ringTarget = ItemStackHandler.buildFromStringID("cobblemon:ring_target");
        ringTarget.set(DataComponentTypes.CUSTOM_NAME, FancyTextHandler.getFormattedText("&eSpawn Location"));
        String currentLocation = this.data.get("Spawn-Location");
        List<Text> locationLore = new ArrayList<>();
        locationLore.add(FancyTextHandler.getFormattedText("&eCurrent Value: &a" + currentLocation));
        locationLore.add(FancyTextHandler.getFormattedText("&eClick me to toggle to the next value."));
        ringTarget.set(DataComponentTypes.LORE, new LoreComponent(locationLore));
        page.getTemplate().getSlot(14).setButton(
                GooeyButton.builder()
                        .display(ringTarget)
                        .onClick(() -> {

                            toggleNextLocation();
                            open();

                        })
                        .build()
        );

        ItemStack shinyStone = ItemStackHandler.buildFromStringID("cobblemon:shiny_stone");
        shinyStone.set(DataComponentTypes.CUSTOM_NAME, FancyTextHandler.getFormattedText("&eShiny Chance"));
        String currentShiny = this.data.getOrDefault("Shiny-Chance", String.valueOf(Cobblemon.INSTANCE.getConfig().getShinyRate()));
        List<Text> shinyLore = new ArrayList<>();
        shinyLore.add(FancyTextHandler.getFormattedText("&eCurrent Value: &a" + currentShiny));
        shinyLore.add(FancyTextHandler.getFormattedText("&eLeft click to increase by 0.01"));
        shinyLore.add(FancyTextHandler.getFormattedText("&eShift left click to increase by 0.05"));
        shinyLore.add(FancyTextHandler.getFormattedText("&eRight click to decrease by 0.01"));
        shinyLore.add(FancyTextHandler.getFormattedText("&eShift right click to decrease by 0.05"));
        shinyStone.set(DataComponentTypes.LORE, new LoreComponent(shinyLore));
        page.getTemplate().getSlot(16).setButton(
                GooeyButton.builder()
                        .display(shinyStone)
                        .onClick(clickAction -> {

                            if (clickAction.getClickType() == ButtonClick.LEFT_CLICK) {

                                increaseShiny(0.01);

                            } else if (clickAction.getClickType() == ButtonClick.SHIFT_LEFT_CLICK) {

                                increaseShiny(0.05);

                            } else if (clickAction.getClickType() == ButtonClick.RIGHT_CLICK) {

                                decreaseShiny(0.01);

                            } else if (clickAction.getClickType() == ButtonClick.SHIFT_RIGHT_CLICK) {

                                decreaseShiny(0.05);

                            }
                            open();

                        })
                        .build()
        );

        // 29, 31, 33

        ItemStack arrow = ItemStackHandler.buildFromStringID("minecraft:arrow");
        arrow.set(DataComponentTypes.CUSTOM_NAME, FancyTextHandler.getFormattedText("&eMAIN MENU"));
        List<Text> arrowLore = new ArrayList<>();
        arrowLore.add(FancyTextHandler.getFormattedText("&eClick me to return to the main menu"));
        arrow.set(DataComponentTypes.LORE, new LoreComponent(arrowLore));
        page.getTemplate().getSlot(29).setButton(
                GooeyButton.builder()
                        .display(arrow)
                        .onClick(() -> {

                            Region region = this.weatherEditorMenu.getTimeEditorMenu().getMainMenu().getRegion();
                            Area area = this.weatherEditorMenu.getTimeEditorMenu().getMainMenu().getArea();
                            Pokemon pokemon = this.weatherEditorMenu.getTimeEditorMenu().getMainMenu().getPokemon();
                            String spawner = this.weatherEditorMenu.getTimeEditorMenu().getMainMenu().getSpawner();
                            String form = this.weatherEditorMenu.getTimeEditorMenu().getMainMenu().getForm();
                            int minLevel = this.weatherEditorMenu.getTimeEditorMenu().getMainMenu().getMinLevel();
                            int maxLevel = this.weatherEditorMenu.getTimeEditorMenu().getMainMenu().getMaxLevel();
                            this.weatherEditorMenu.getTimeEditorMenu().getMainMenu().open(this.player, region, area, pokemon, spawner, form, minLevel, maxLevel);

                        })
                        .build()
        );

        ItemStack clock = ItemStackHandler.buildFromStringID("minecraft:clock");
        clock.set(DataComponentTypes.CUSTOM_NAME, FancyTextHandler.getFormattedText("&eCurrent Time: " + this.currentTime));
        List<Text> clockLore = new ArrayList<>();
        clockLore.add(FancyTextHandler.getFormattedText("&eClick me to go back to the time menu."));
        clock.set(DataComponentTypes.LORE, new LoreComponent(clockLore));
        page.getTemplate().getSlot(31).setButton(
                GooeyButton.builder()
                        .display(clock)
                        .onClick(() -> {

                            this.weatherEditorMenu.getTimeEditorMenu().open();

                        })
                        .build()
        );

        ItemStack wool = ItemStackHandler.buildFromStringID("minecraft:white_wool");
        wool.set(DataComponentTypes.CUSTOM_NAME, FancyTextHandler.getFormattedText("&eCurrent Weather: " + this.weatherBeingEdited));
        List<Text> woolLore = new ArrayList<>();
        woolLore.add(FancyTextHandler.getFormattedText("&eClick me to go back to the weather menu."));
        wool.set(DataComponentTypes.LORE, new LoreComponent(woolLore));
        page.getTemplate().getSlot(33).setButton(
                GooeyButton.builder()
                        .display(wool)
                        .onClick(this.weatherEditorMenu::open)
                        .build()
        );

        UIManager.openUIForcefully(this.player, page);

    }

    private void increaseSize (int amount) {

        int current = Integer.parseInt(this.data.get("Group-Size"));
        int updated = current + amount;
        this.data.put("Group-Size", String.valueOf(updated));

    }

    private void decreaseSize (int amount) {

        int current = Integer.parseInt(this.data.get("Group-Size"));
        int updated = Math.max(0, current - amount);
        this.data.put("Group-Size", String.valueOf(updated));

    }

    private void increaseChance (double amount) {

        double current = Double.parseDouble(this.data.get("Spawn-Chance"));
        double updated = Math.min(1.0, current + amount);
        DecimalFormat df = new DecimalFormat("#.##");
        double newChance = Double.parseDouble(df.format(updated));
        this.data.put("Spawn-Chance", String.valueOf(newChance));

    }

    private void decreaseChance (double amount) {

        double current = Double.parseDouble(this.data.get("Spawn-Chance"));
        double updated = Math.max(0.01, current - amount);
        DecimalFormat df = new DecimalFormat("#.##");
        double newChance = Double.parseDouble(df.format(updated));
        this.data.put("Spawn-Chance", String.valueOf(newChance));

    }

    private void increaseShiny (double amount) {

        double current = Double.parseDouble(this.data.getOrDefault("Shiny-Chance", String.valueOf(Cobblemon.INSTANCE.getConfig().getShinyRate())));
        double updated = current + amount;
        DecimalFormat df = new DecimalFormat("#.##");
        double newChance = Double.parseDouble(df.format(updated));
        this.data.put("Shiny-Chance", String.valueOf(newChance));

    }

    private void decreaseShiny (double amount) {

        double current = Double.parseDouble(this.data.getOrDefault("Shiny-Chance", String.valueOf(Cobblemon.INSTANCE.getConfig().getShinyRate())));
        double updated = Math.max(-1, current - amount);
        DecimalFormat df = new DecimalFormat("#.##");
        double newChance = Double.parseDouble(df.format(updated));
        this.data.put("Shiny-Chance", String.valueOf(newChance));

    }

    private void toggleNextLocation() {

        String current = this.data.get("Spawn-Location");
        if (current.equalsIgnoreCase("land")) {

            this.data.put("Spawn-Location", "water");

        } else if (current.equalsIgnoreCase("water")) {

            this.data.put("Spawn-Location", "air");

        } else if (current.equalsIgnoreCase("air")) {

            this.data.put("Spawn-Location", "underground");

        } else {

            this.data.put("Spawn-Location", "land");

        }

    }

}
