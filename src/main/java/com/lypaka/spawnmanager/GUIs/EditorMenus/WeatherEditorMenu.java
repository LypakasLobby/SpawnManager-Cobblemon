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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeatherEditorMenu {

    private final ServerPlayerEntity player;
    private final TimeEditorMenu timeEditorMenu;
    private final String timeBeingEdited;

    public WeatherEditorMenu (ServerPlayerEntity player, TimeEditorMenu timeEditorMenu, String timeBeingEdited) {

        this.player = player;
        this.timeEditorMenu = timeEditorMenu;
        this.timeBeingEdited = timeBeingEdited;

    }

    public void open() {

        ChestTemplate template = ChestTemplate.builder(3).build();
        GooeyPage page = GooeyPage.builder().template(template).title(FancyTextHandler.getFormattedText("&eEdit Weather")).build();

        ItemStack border = ItemStackHandler.buildFromStringID("minecraft:red_stained_glass_pane");
        border.set(DataComponentTypes.CUSTOM_NAME, FancyTextHandler.getFormattedText(""));
        for (int i = 0; i < 27; i++) {

            page.getTemplate().getSlot(i).setButton(GooeyButton.builder().display(border).build());

        }
        ItemStack clock = ItemStackHandler.buildFromStringID("minecraft:clock");
        clock.set(DataComponentTypes.CUSTOM_NAME, FancyTextHandler.getFormattedText("&eCurrent Time: " + this.timeBeingEdited));
        List<Text> clockLore = new ArrayList<>();
        clockLore.add(FancyTextHandler.getFormattedText("&eClick me to go back to the time menu."));
        clock.set(DataComponentTypes.LORE, new LoreComponent(clockLore));
        page.getTemplate().getSlot(4).setButton(
                GooeyButton.builder()
                        .display(clock)
                        .onClick(this.timeEditorMenu::open)
                        .build()
        );
        
        ItemStack clear = ItemStackHandler.buildFromStringID("cobblemon:sun_stone");
        clear.set(DataComponentTypes.CUSTOM_NAME, FancyTextHandler.getFormattedText("&eClear"));
        List<Text> clearLore = new ArrayList<>();
        clearLore.add(FancyTextHandler.getFormattedText("&eClick me to edit spawn data for " + this.timeBeingEdited + " time, Clear weather"));
        clear.set(DataComponentTypes.LORE, new LoreComponent(clearLore));
        page.getTemplate().getSlot(10).setButton(
                GooeyButton.builder()
                        .display(clear)
                        .onClick(() -> {
                            
                            DataEditorMenu dataEditorMenu = new DataEditorMenu(this.player, this, "Clear");
                            dataEditorMenu.open();
                            
                        })
                        .build()
        );
        
        ItemStack rain = ItemStackHandler.buildFromStringID("cobblemon:water_stone");
        rain.set(DataComponentTypes.CUSTOM_NAME, FancyTextHandler.getFormattedText("&eRain"));
        List<Text> rainLore = new ArrayList<>();
        rainLore.add(FancyTextHandler.getFormattedText("&eClick me to edit spawn data for " + this.timeBeingEdited + " time, Rain weather"));
        rain.set(DataComponentTypes.LORE, new LoreComponent(rainLore));
        page.getTemplate().getSlot(13).setButton(
                GooeyButton.builder()
                        .display(rain)
                        .onClick(() -> {
                            
                            DataEditorMenu dataEditorMenu = new DataEditorMenu(this.player, this, "Rain");
                            dataEditorMenu.open();
                            
                        })
                        .build()
        );
        
        ItemStack storm = ItemStackHandler.buildFromStringID("cobblemon:thunder_stone");
        storm.set(DataComponentTypes.CUSTOM_NAME, FancyTextHandler.getFormattedText("&eStorm"));
        List<Text> stormLore = new ArrayList<>();
        stormLore.add(FancyTextHandler.getFormattedText("&eClick me to edit spawn data for " + this.timeBeingEdited + " time, Storm weather"));
        storm.set(DataComponentTypes.LORE, new LoreComponent(stormLore));
        page.getTemplate().getSlot(16).setButton(
                GooeyButton.builder()
                        .display(storm)
                        .onClick(() -> {
                            
                            DataEditorMenu dataEditorMenu = new DataEditorMenu(this.player, this, "Storm");
                            dataEditorMenu.open();
                            
                        })
                        .build()
        );

        ItemStack arrow = ItemStackHandler.buildFromStringID("minecraft:arrow");
        arrow.set(DataComponentTypes.CUSTOM_NAME, FancyTextHandler.getFormattedText("&eMAIN MENU"));
        List<Text> arrowLore = new ArrayList<>();
        arrowLore.add(FancyTextHandler.getFormattedText("&eClick me to return to the main menu"));
        arrow.set(DataComponentTypes.LORE, new LoreComponent(arrowLore));
        page.getTemplate().getSlot(21).setButton(
                GooeyButton.builder()
                        .display(arrow)
                        .onClick(() -> {

                            Region region = this.timeEditorMenu.getMainMenu().getRegion();
                            Area area = this.timeEditorMenu.getMainMenu().getArea();
                            Pokemon pokemon = this.timeEditorMenu.getMainMenu().getPokemon();
                            String spawner = this.timeEditorMenu.getMainMenu().getSpawner();
                            String form = this.timeEditorMenu.getMainMenu().getForm();
                            int minLevel = this.timeEditorMenu.getMainMenu().getMinLevel();
                            int maxLevel = this.timeEditorMenu.getMainMenu().getMaxLevel();
                            this.timeEditorMenu.getMainMenu().open(this.player, region, area, pokemon, spawner, form, minLevel, maxLevel);

                        })
                        .build()
        );

        ItemStack netherStar = ItemStackHandler.buildFromStringID("minecraft:nether_star");
        netherStar.set(DataComponentTypes.CUSTOM_NAME, FancyTextHandler.getFormattedText("&eANY"));
        List<Text> starLore = new ArrayList<>();
        starLore.add(FancyTextHandler.getFormattedText("&eClick me to set WEATHER to ANY."));
        starLore.add(FancyTextHandler.getFormattedText("&eYou'll also be moved to the weather editor menu from here."));
        netherStar.set(DataComponentTypes.LORE, new LoreComponent(starLore));
        page.getTemplate().getSlot(23).setButton(
                GooeyButton.builder()
                        .display(netherStar)
                        .onClick(() -> {

                            List<String> toRemove = new ArrayList<>();
                            for (Map.Entry<String, Map<String, Map<String, String>>> entry : this.timeEditorMenu.getMainMenu().getSpawnData().entrySet()) {

                                String time = entry.getKey();
                                Map<String, Map<String, String>> d = entry.getValue();
                                for (Map.Entry<String, Map<String, String>> d2 : d.entrySet()) {

                                    String weather = d2.getKey();
                                    toRemove.add(weather);
                                    Map<String, Map<String, String>> updated = new HashMap<>();
                                    updated.put("Any", d2.getValue());
                                    this.timeEditorMenu.getMainMenu().getSpawnData().put(time, updated);

                                }

                            }
                            // this is probably stupid, but I'm doing it this way anyway
                            for (Map.Entry<String, Map<String, Map<String, String>>> entry : this.timeEditorMenu.getMainMenu().getSpawnData().entrySet()) {

                                for (String r : toRemove) {

                                    entry.getValue().remove(r);

                                }

                            }
                            DataEditorMenu dataEditorMenu = new DataEditorMenu(this.player, this, "Any");
                            dataEditorMenu.open();

                        })
                        .build()
        );

        UIManager.openUIForcefully(this.player, page);

    }

    public TimeEditorMenu getTimeEditorMenu() {

        return timeEditorMenu;

    }

    public String getTimeBeingEdited() {

        return this.timeBeingEdited;

    }

}
