package com.lypaka.spawnmanager.Commands;

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Species;
import com.lypaka.areamanager.Areas.Area;
import com.lypaka.areamanager.Areas.AreaHandler;
import com.lypaka.areamanager.Regions.Region;
import com.lypaka.areamanager.Regions.RegionHandler;
import com.lypaka.lypakautils.Handlers.FancyTextHandler;
import com.lypaka.lypakautils.Handlers.PermissionHandler;
import com.lypaka.lypakautils.Handlers.RandomHandler;
import com.lypaka.shadow.configurate.objectmapping.ObjectMappingException;
import com.lypaka.spawnmanager.GUIs.EditorMenus.SpawnDataEditorMainMenu;
import com.lypaka.spawnmanager.SpawnAreas.SpawnAreaHandler;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddCommand {

    public static List<String> species = new ArrayList<>();

    public AddCommand (CommandDispatcher<ServerCommandSource> dispatcher) {

        for (String a : SpawnManagerCommand.ALIASES) {

            dispatcher.register(
                    CommandManager.literal(a)
                            .then(
                                    CommandManager.literal("add")
                                            .then(
                                                    CommandManager.argument("region", StringArgumentType.word())
                                                            .suggests(
                                                                    (context, builder) -> CommandSource.suggestMatching(RegionHandler.regionMap.keySet(), builder)
                                                            )
                                                            .then(
                                                                    CommandManager.argument("area", StringArgumentType.string())
                                                                            .then(
                                                                                    CommandManager.argument("spawner", StringArgumentType.word())
                                                                                            .suggests(
                                                                                                    (context, builder) -> CommandSource.suggestMatching(Arrays.asList("grass", "surf", "headbutt", "rocksmash", "natural", "fish", "cave"), builder)
                                                                                            )
                                                                                            .then(
                                                                                                    CommandManager.argument("pokemon", StringArgumentType.string())
                                                                                                            .suggests(
                                                                                                                    (context, builder) -> CommandSource.suggestMatching(species, builder)
                                                                                                            )
                                                                                                            .then(
                                                                                                                    CommandManager.argument("form", StringArgumentType.word())
                                                                                                                            .suggests(
                                                                                                                                    (context, builder) -> CommandSource.suggestMatching(Arrays.asList("default", "galarian", "alolan", "hisuian", "paldean"), builder)
                                                                                                                            )
                                                                                                                            .then(
                                                                                                                                    CommandManager.argument("minLevel", IntegerArgumentType.integer(1))
                                                                                                                                            .then(
                                                                                                                                                    CommandManager.argument("maxLevel", IntegerArgumentType.integer())
                                                                                                                                                            .executes(c -> {

                                                                                                                                                                if (c.getSource().getEntity() instanceof ServerPlayerEntity player) {

                                                                                                                                                                    if (!PermissionHandler.hasPermission(player, "spawnmanager.command.admin")) {

                                                                                                                                                                        player.sendMessage(FancyTextHandler.getFormattedText("&cYou don't have permission to use this command!"));
                                                                                                                                                                        return 0;

                                                                                                                                                                    }

                                                                                                                                                                    String regionName = StringArgumentType.getString(c, "region");
                                                                                                                                                                    String areaName = StringArgumentType.getString(c, "area");
                                                                                                                                                                    String spawner = StringArgumentType.getString(c, "spawner");
                                                                                                                                                                    String pokemonFile = StringArgumentType.getString(c, "pokemon");
                                                                                                                                                                    String form = StringArgumentType.getString(c, "form");
                                                                                                                                                                    int minLevel = IntegerArgumentType.getInteger(c, "minLevel");
                                                                                                                                                                    int maxLevel = IntegerArgumentType.getInteger(c, "maxLevel");

                                                                                                                                                                    // a safety check to just confirm the Region we wanna fuck with exists
                                                                                                                                                                    Region region = RegionHandler.getFromName(regionName);
                                                                                                                                                                    if (region == null) {

                                                                                                                                                                        c.getSource().sendMessage(FancyTextHandler.getFormattedText("&cInvalid Region!"));
                                                                                                                                                                        return 0;

                                                                                                                                                                    }

                                                                                                                                                                    // a safety check to just confirm the Area we wanna fuck with exists, which is more likely because I can't do tab stuff for it
                                                                                                                                                                    Area area = AreaHandler.getFromName(regionName, areaName);
                                                                                                                                                                    if (area == null) {

                                                                                                                                                                        c.getSource().sendMessage(FancyTextHandler.getFormattedText("&cInvalid Area!"));
                                                                                                                                                                        return 0;

                                                                                                                                                                    }

                                                                                                                                                                    if (!pokemonFile.contains(".conf")) pokemonFile = pokemonFile + ".conf";
                                                                                                                                                                    Species pokemonSpecies = PokemonSpecies.INSTANCE.getByName(pokemonFile.replace(".conf", "").toLowerCase());
                                                                                                                                                                    Pokemon pokemon = pokemonSpecies.create(RandomHandler.getRandomNumberBetween(minLevel, maxLevel));
                                                                                                                                                                    SpawnDataEditorMainMenu menu = new SpawnDataEditorMainMenu();
                                                                                                                                                                    menu.open(player, region, area, pokemon, spawner, form, minLevel, maxLevel);

                                                                                                                                                                }

                                                                                                                                                                return 0;

                                                                                                                                                            })
                                                                                                                                            )
                                                                                                                            )
                                                                                                            )

                                                                                            )

                                                                            )

                                                            )

                                            )

                            )

            );

        }

    }

}
