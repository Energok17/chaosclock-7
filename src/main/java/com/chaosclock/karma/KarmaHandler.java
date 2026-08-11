package com.chaosclock.karma;

import com.chaosclock.ChaosClockMod;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

@Mod.EventBusSubscriber(modid="chaosclock")
public class KarmaHandler {
    private static final String KARMA_KEY = "ChaosClockKarma";
    public static final int MIN_KARMA = -500;
    public static final int MAX_KARMA = 500;
    private static final Random RANDOM = new Random();

    // Karma value given to a player the very first time they spawn in the world
    public static final int STARTING_KARMA = 100;

    public static int getKarma(Player player) {
        CompoundTag data = player.getPersistentData();
        if (!data.contains(KARMA_KEY)) {
            data.putInt(KARMA_KEY, STARTING_KARMA);
        }
        return data.getInt(KARMA_KEY);
    }

    public static void setKarma(Player player, int value) {
        int clamped = Math.max(MIN_KARMA, Math.min(MAX_KARMA, value));
        player.getPersistentData().putInt(KARMA_KEY, clamped);
    }

    public static void addKarma(Player player, int delta) {
        setKarma(player, getKarma(player) + delta);
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity dead = event.getEntity();
        Entity source = event.getSource().getEntity();

        // Player died → -10
        if (dead instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer)dead;
            addKarma(player, -10);
            return;
        }

        // Killed by player
        if (!(source instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer killer = (ServerPlayer)source;

        MobCategory category = dead.getType().getCategory();

        if (category == MobCategory.MONSTER) {
            // Hostile: random +1 to +5
            int gain = 1 + RANDOM.nextInt(5); // 1, 2, 3, 4 or 5
            addKarma(killer, gain);
        } else if (category == MobCategory.CREATURE
                || category == MobCategory.AMBIENT
                || category == MobCategory.WATER_CREATURE
                || category == MobCategory.WATER_AMBIENT) {
            addKarma(killer, -1);         // peaceful
        }
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer) {
            getKarma(player); // ensure key exists (defaults to 0)
        }
    }
}
