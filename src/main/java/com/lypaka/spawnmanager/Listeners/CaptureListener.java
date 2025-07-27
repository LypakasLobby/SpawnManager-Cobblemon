package com.lypaka.spawnmanager.Listeners;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.lypaka.lypakautils.LypakaUtils;
import com.lypaka.spawnmanager.Spawners.CaveSpawner;
import com.lypaka.spawnmanager.Spawners.GrassSpawner;
import com.lypaka.spawnmanager.Spawners.NaturalSpawner;
import com.lypaka.spawnmanager.Spawners.SurfSpawner;
import kotlin.Unit;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;
import java.util.UUID;

public class CaptureListener {

    public static void register() {

        CobblemonEvents.POKEMON_CAPTURED.subscribe(Priority.NORMAL, event -> {

            Pokemon pokemon = event.getPokemon();
            NaturalSpawner.playerPokemonCount.entrySet().removeIf(entry -> {

                List<PokemonEntity> spawnedPokemon = entry.getValue();
                spawnedPokemon.removeIf(entity -> entity.getPokemon().getUuid().toString().equalsIgnoreCase(pokemon.getUuid().toString()));
                return spawnedPokemon.isEmpty();

            });
            GrassSpawner.playerPokemonCount.entrySet().removeIf(entry -> {

                List<PokemonEntity> spawnedPokemon = entry.getValue();
                spawnedPokemon.removeIf(entity -> entity.getPokemon().getUuid().toString().equalsIgnoreCase(pokemon.getUuid().toString()));
                return spawnedPokemon.isEmpty();

            });
            CaveSpawner.playerPokemonCount.entrySet().removeIf(entry -> {

                List<PokemonEntity> spawnedPokemon = entry.getValue();
                spawnedPokemon.removeIf(entity -> entity.getPokemon().getUuid().toString().equalsIgnoreCase(pokemon.getUuid().toString()));
                return spawnedPokemon.isEmpty();

            });
            SurfSpawner.playerPokemonCount.entrySet().removeIf(entry -> {

                List<PokemonEntity> spawnedPokemon = entry.getValue();
                spawnedPokemon.removeIf(entity -> entity.getPokemon().getUuid().toString().equalsIgnoreCase(pokemon.getUuid().toString()));
                return spawnedPokemon.isEmpty();

            });

            return Unit.INSTANCE;

        });

    }

}
