package com.lypaka.spawnmanager.Utils;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.battles.actor.PlayerBattleActor;
import kotlin.Unit;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.*;

public class BattleUtils {

    public static List<UUID> autoBattlePlayerUUIDs = new ArrayList<>();

    // Cobblemon should REALLY look into a method to retrieve player party members back into their PokeBalls if the entity exists. Would have made this clusterfuck not have to be a clusterfuck.
    public static void register() {

        CobblemonEvents.BATTLE_VICTORY.subscribe(Priority.NORMAL, event -> {

            Map<UUID, PlayerBattleActor> map = new HashMap<>();
            event.getWinners().forEach(winner -> {

                if (winner instanceof PlayerBattleActor pba) {

                    ServerPlayerEntity player = pba.getEntity();
                    if (player != null) {

                        map.put(player.getUuid(), pba);

                    }

                }

            });
            List<UUID> toPing = new ArrayList<>();
            for (Map.Entry<UUID, PlayerBattleActor> e : map.entrySet()) {

                if (autoBattlePlayerUUIDs.contains(e.getKey())) {

                    toPing.add(e.getKey());

                }

            }
            autoBattlePlayerUUIDs.removeIf(e -> {

                if (toPing.contains(e)) {

                    PlayerBattleActor pba = map.get(e);
                    pba.getActivePokemon().forEach(battlePokemon -> {

                        if (battlePokemon.getBattlePokemon() != null) {

                            if (battlePokemon.getBattlePokemon().getEntity() != null) {

                                battlePokemon.getBattlePokemon().getEntity().remove(Entity.RemovalReason.DISCARDED);

                            }

                        }

                    });

                    return true;

                }

                return false;

            });

            return Unit.INSTANCE;

        });

    }

}
