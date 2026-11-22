package com.lypaka.spawnmanager.Listeners;

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.pokemon.Species;
import com.lypaka.areamanager.API.AreaEvents.AreaLeaveCallback;
import com.lypaka.areamanager.API.FinishedLoadingCallback;
import com.lypaka.lypakautils.API.PlayerLandMovementCallback;
import com.lypaka.lypakautils.API.PlayerWaterMovementCallback;
import com.lypaka.spawnmanager.Commands.AddCommand;
import com.lypaka.spawnmanager.Spawners.*;
import com.lypaka.spawnmanager.Utils.BattleUtils;
import com.lypaka.spawnmanager.Utils.SpawnerUtils.DespawnChecker;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;

public class ServerStartedListener implements ServerLifecycleEvents.ServerStarted {

    @Override
    public void onServerStarted (MinecraftServer minecraftServer) {

        AreaLeaveCallback.EVENT.register(new AreaListener());
        BattleEndListener.registerAll();
        ServerPlayConnectionEvents.DISCONNECT.register(new DisconnectListener());
        FinishedLoadingCallback.EVENT.register(new LoadListener());
        FinishedLoadingCallback.EVENT.invoker().onFinishedLoading();
        NaturalCobblemonSpawnListener.register();
        ServerTickEvents.END_SERVER_TICK.register(new TickListener());
        PlayerLandMovementCallback.EVENT.register(new GrassSpawner());
        PlayerLandMovementCallback.EVENT.register(new CaveSpawner());
        PlayerWaterMovementCallback.EVENT.register(new SurfSpawner());
        //NaturalSpawner.startTimer();
        NaturalSpawner.startTimer();
        FishSpawner.registerReel();
        BattleUtils.register();
        DespawnChecker.startChecker();
        CaptureListener.register();
        KillListener.register();

        PokemonSpecies.getSpecies().forEach(e -> AddCommand.species.add(e.getName()));

    }

}
