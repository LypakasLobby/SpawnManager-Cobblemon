package com.lypaka.spawnmanager.Listeners;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.battles.actor.PokemonBattleActor;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.lypaka.spawnmanager.Spawners.CaveSpawner;
import com.lypaka.spawnmanager.Spawners.GrassSpawner;
import com.lypaka.spawnmanager.Spawners.NaturalSpawner;
import com.lypaka.spawnmanager.Spawners.SurfSpawner;
import kotlin.Unit;

import java.util.List;

public class KillListener {

    public static void register() {

        CobblemonEvents.BATTLE_VICTORY.subscribe(Priority.NORMAL, event -> {

            NaturalSpawner.playerPokemonCount.entrySet().removeIf(entry -> {

                List<PokemonEntity> spawnedPokemon = entry.getValue();
                for (BattleActor actor : event.getLosers()) {

                    if (actor instanceof PokemonBattleActor pba) {

                        Pokemon pokemon = pba.getPokemon().getOriginalPokemon();
                        spawnedPokemon.removeIf(s -> s.getPokemon().getUuid().toString().equalsIgnoreCase(pokemon.getUuid().toString()));

                    }

                }

                return spawnedPokemon.isEmpty();

            });
            GrassSpawner.playerPokemonCount.entrySet().removeIf(entry -> {

                List<PokemonEntity> spawnedPokemon = entry.getValue();
                for (BattleActor actor : event.getLosers()) {

                    if (actor instanceof PokemonBattleActor pba) {

                        Pokemon pokemon = pba.getPokemon().getOriginalPokemon();
                        spawnedPokemon.removeIf(s -> s.getPokemon().getUuid().toString().equalsIgnoreCase(pokemon.getUuid().toString()));

                    }

                }

                return spawnedPokemon.isEmpty();

            });
            CaveSpawner.playerPokemonCount.entrySet().removeIf(entry -> {

                List<PokemonEntity> spawnedPokemon = entry.getValue();
                for (BattleActor actor : event.getLosers()) {

                    if (actor instanceof PokemonBattleActor pba) {

                        Pokemon pokemon = pba.getPokemon().getOriginalPokemon();
                        spawnedPokemon.removeIf(s -> s.getPokemon().getUuid().toString().equalsIgnoreCase(pokemon.getUuid().toString()));

                    }

                }

                return spawnedPokemon.isEmpty();

            });
            SurfSpawner.playerPokemonCount.entrySet().removeIf(entry -> {

                List<PokemonEntity> spawnedPokemon = entry.getValue();
                for (BattleActor actor : event.getLosers()) {

                    if (actor instanceof PokemonBattleActor pba) {

                        Pokemon pokemon = pba.getPokemon().getOriginalPokemon();
                        spawnedPokemon.removeIf(s -> s.getPokemon().getUuid().toString().equalsIgnoreCase(pokemon.getUuid().toString()));

                    }

                }

                return spawnedPokemon.isEmpty();

            });

            return Unit.INSTANCE;

        });

    }

}
