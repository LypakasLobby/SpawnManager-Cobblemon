package com.lypaka.spawnmanager.Utils;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.lypaka.lypakautils.Handlers.RandomHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class MiscUtils {

    public static boolean canPlayerBattle (ServerPlayerEntity player) {

        PlayerPartyStore storage = Cobblemon.INSTANCE.getStorage().getParty(player);
        int aliveCount = 0;
        for (int i = 0; i < 6; i++) {

            Pokemon p = storage.get(i);
            if (p != null) {

                if (!p.isFainted()) aliveCount++;

            }

        }

        return aliveCount > 0;

    }

    public static void tryShiny (Pokemon pokemon, double chance) {

        if (chance >= 1) {

            if (RandomHandler.getRandomNumberBetween(1, chance) == 1) {

                pokemon.setShiny(true);

            }

        } else {

            if (RandomHandler.getRandomChance(chance)) {

                pokemon.setShiny(true);

            }

        }

    }

}
