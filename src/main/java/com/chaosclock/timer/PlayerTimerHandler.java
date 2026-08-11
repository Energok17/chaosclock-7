package com.chaosclock.timer;

import com.chaosclock.event.ChaosEvents;
import com.chaosclock.event.ChaosScheduler;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;

@Mod.EventBusSubscriber(modid = "chaosclock")
public class PlayerTimerHandler {
    private static volatile int intervalTicks = 1200;
    private static final Map<UUID, Integer> COUNTDOWNS = new ConcurrentHashMap<>();

    public static int getIntervalTicks() {
        return intervalTicks;
    }

    public static void setIntervalTicks(int ticks, Iterable<ServerPlayer> onlinePlayers) {
        intervalTicks = Math.max(1, ticks);
        for (ServerPlayer player : onlinePlayers) {
            COUNTDOWNS.put(player.getUUID(), intervalTicks);
        }
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer player2) {
            COUNTDOWNS.put(player2.getUUID(), intervalTicks);
        }
    }

    @SubscribeEvent
    public static void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        COUNTDOWNS.remove(event.getEntity().getUUID());
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        ChaosScheduler.tick();
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            return;
        }
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            int remaining = COUNTDOWNS.getOrDefault(player.getUUID(), intervalTicks);
            if (--remaining <= 0) {
                ServerLevel level = player.serverLevel();
                ChaosEvents.triggerRandomEvent(level, player);
                remaining = intervalTicks;
            }
            COUNTDOWNS.put(player.getUUID(), remaining);
        }
    }
}
