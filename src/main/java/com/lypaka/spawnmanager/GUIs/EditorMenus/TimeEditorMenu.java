package com.lypaka.spawnmanager.GUIs.EditorMenus;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import ca.landonjw.gooeylibs2.api.page.GooeyPage;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TimeEditorMenu {

    private final ServerPlayerEntity player;
    private final SpawnDataEditorMainMenu mainMenu;

    public TimeEditorMenu (ServerPlayerEntity player, SpawnDataEditorMainMenu mainMenu) {

        this.player = player;
        this.mainMenu = mainMenu;

    }

    public void open() {

        Map<String, Map<String, Map<String, String>>> spawnData = this.mainMenu.getSpawnData();
        ChestTemplate template = ChestTemplate.builder(3).build();
        GooeyPage page = GooeyPage.builder().title(FancyTextHandler.getFormattedText("&eEdit Time")).template(template).build();
        ItemStack border = ItemStackHandler.buildFromStringID("minecraft:gray_stained_glass_pane");
        border.set(DataComponentTypes.CUSTOM_NAME, FancyTextHandler.getFormattedText(""));
        for (int i = 0; i < 27; i++) {

            page.getTemplate().getSlot(i).setButton(GooeyButton.builder().display(border).build());

        }

        ItemStack goldBlock = ItemStackHandler.buildFromStringID("minecraft:gold_block");
        goldBlock.set(DataComponentTypes.CUSTOM_NAME, FancyTextHandler.getFormattedText("&eDay"));
        List<Text> goldLore = new ArrayList<>();
        goldLore.add(FancyTextHandler.getFormattedText("&eClick me to edit data in the Day category."));
        goldBlock.set(DataComponentTypes.LORE, new LoreComponent(goldLore));
        page.getTemplate().getSlot(11).setButton(
                GooeyButton.builder()
                        .display(goldBlock)
                        .onClick(() -> {

                            WeatherEditorMenu weatherEditorMenu = new WeatherEditorMenu(this.player, this, "Day");
                            weatherEditorMenu.open();

                        })
                        .build()
        );

        ItemStack netherStar = ItemStackHandler.buildFromStringID("minecraft:nether_star");
        netherStar.set(DataComponentTypes.CUSTOM_NAME, FancyTextHandler.getFormattedText("&eANY"));
        List<Text> starLore = new ArrayList<>();
        starLore.add(FancyTextHandler.getFormattedText("&eClick me to set TIME to ANY."));
        starLore.add(FancyTextHandler.getFormattedText("&eYou'll also be moved to the weather editor menu from here."));
        netherStar.set(DataComponentTypes.LORE, new LoreComponent(starLore));
        page.getTemplate().getSlot(13).setButton(
                GooeyButton.builder()
                        .display(netherStar)
                        .onClick(() -> {

                            if (this.mainMenu.getSpawnData().containsKey("Day")) {

                                Map<String, Map<String, String>> everythingElse = this.mainMenu.getSpawnData().get("Day");
                                this.mainMenu.getSpawnData().put("Any", everythingElse);

                            }
                            this.mainMenu.getSpawnData().entrySet().removeIf(entry -> entry.getKey().equalsIgnoreCase("Day"));
                            this.mainMenu.getSpawnData().entrySet().removeIf(entry -> entry.getKey().equalsIgnoreCase("Night"));
                            WeatherEditorMenu weatherEditorMenu = new WeatherEditorMenu(this.player, this, "Any");
                            weatherEditorMenu.open();

                        })
                        .build()
        );

        ItemStack coalBlock = ItemStackHandler.buildFromStringID("minecraft:coal_block");
        coalBlock.set(DataComponentTypes.CUSTOM_NAME, FancyTextHandler.getFormattedText("&eNight"));
        List<Text> coalLore = new ArrayList<>();
        coalLore.add(FancyTextHandler.getFormattedText("&eClick me to edit data in the Night category."));
        coalBlock.set(DataComponentTypes.LORE, new LoreComponent(coalLore));
        page.getTemplate().getSlot(15).setButton(
                GooeyButton.builder()
                        .display(coalBlock)
                        .onClick(() -> {

                            WeatherEditorMenu weatherEditorMenu = new WeatherEditorMenu(this.player, this, "Night");
                            weatherEditorMenu.open();

                        })
                        .build()
        );

        ItemStack arrow = ItemStackHandler.buildFromStringID("minecraft:arrow");
        arrow.set(DataComponentTypes.CUSTOM_NAME, FancyTextHandler.getFormattedText("&eMAIN MENU"));
        List<Text> arrowLore = new ArrayList<>();
        arrowLore.add(FancyTextHandler.getFormattedText("&eClick me to return to the main menu"));
        arrow.set(DataComponentTypes.LORE, new LoreComponent(arrowLore));
        page.getTemplate().getSlot(22).setButton(
                GooeyButton.builder()
                        .display(arrow)
                        .onClick(() -> {

                            Region region = this.mainMenu.getRegion();
                            Area area = this.mainMenu.getArea();
                            Pokemon pokemon = this.mainMenu.getPokemon();
                            String spawner = this.mainMenu.getSpawner();
                            String form = this.mainMenu.getForm();
                            int minLevel = this.mainMenu.getMinLevel();
                            int maxLevel = this.mainMenu.getMaxLevel();
                            this.mainMenu.open(this.player, region, area, pokemon, spawner, form, minLevel, maxLevel);

                        })
                        .build()
        );

        UIManager.openUIForcefully(this.player, page);

    }

    public SpawnDataEditorMainMenu getMainMenu() {

        return this.mainMenu;

    }

}
