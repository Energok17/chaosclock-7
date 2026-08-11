package com.chaosclock.command;

import com.chaosclock.event.ChaosEvents;
import com.chaosclock.karma.KarmaHandler;
import com.chaosclock.timer.PlayerTimerHandler;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.Locale;
import java.util.Map;

public class ChaosClockCommand {

    // Prices for forcing events
    private static final Map<String, Integer> FORCE_PRICES = Map.of(
            "rare", 25,
            "epic", 50,
            "legendary", 100,
            "divine", 200
    );

    private static final Map<String, ChaosEvents.Rarity> RARITY_MAP = Map.of(
            "rare", ChaosEvents.Rarity.RARE,
            "epic", ChaosEvents.Rarity.EPIC,
            "legendary", ChaosEvents.Rarity.LEGENDARY,
            "divine", ChaosEvents.Rarity.DIVINE
    );

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

        // /chaosclock interval <seconds>
        dispatcher.register(
            Commands.literal("chaosclock")
                .then(Commands.literal("interval")
                    .requires(source -> source.hasPermission(2))
                    .then(Commands.argument("seconds", IntegerArgumentType.integer(1))
                        .executes(ctx -> {
                            int seconds = IntegerArgumentType.getInteger(ctx, "seconds");
                            int ticks = seconds * 20;
                            MinecraftServer server = ctx.getSource().getServer();
                            PlayerTimerHandler.setIntervalTicks(ticks, server.getPlayerList().getPlayers());
                            ctx.getSource().sendSuccess(() -> Component.literal("[ChaosClock] Інтервал змінено на " + seconds + " сек. для всіх гравців."), true);
                            return 1;
                        })
                    )
                )
                .then(Commands.literal("karma")
                    .executes(ctx -> showKarma(ctx.getSource()))
                    .then(Commands.literal("shop")
                        .executes(ctx -> showShop(ctx.getSource()))
                    )
                )
        );

        // /karma
        dispatcher.register(
            Commands.literal("karma")
                .executes(ctx -> showKarma(ctx.getSource()))
                .then(Commands.literal("set")
                    .requires(s -> s.hasPermission(2))
                    .then(Commands.argument("value", IntegerArgumentType.integer(KarmaHandler.MIN_KARMA, KarmaHandler.MAX_KARMA))
                        .executes(ctx -> {
                            ServerPlayer player = ctx.getSource().getPlayerOrException();
                            int value = IntegerArgumentType.getInteger(ctx, "value");
                            KarmaHandler.setKarma(player, value);
                            ctx.getSource().sendSuccess(() -> Component.literal("§7[Karma] Удачу встановлено на §a" + value), true);
                            return 1;
                        })
                    )
                )
                .then(Commands.literal("shop")
                    .executes(ctx -> showShop(ctx.getSource()))
                )
        );

        // /force <rarity>
        dispatcher.register(
            Commands.literal("force")
                .then(Commands.argument("rarity", StringArgumentType.word())
                    .suggests((ctx, builder) -> {
                        FORCE_PRICES.keySet().forEach(builder::suggest);
                        return builder.buildFuture();
                    })
                    .executes(ctx -> {
                        String rarityKey = StringArgumentType.getString(ctx, "rarity").toLowerCase(Locale.ROOT);
                        return forceEvent(ctx.getSource(), rarityKey);
                    })
                )
        );

        // /karmashop (alias)
        dispatcher.register(
            Commands.literal("karmashop")
                .executes(ctx -> showShop(ctx.getSource()))
        );
    }

    private static int showKarma(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        int karma = KarmaHandler.getKarma(player);
        String color;
        String note;
        if (karma >= 300) {
            color = "§d";
            note = " §7(звичайні/незвичайні івенти більше не випадають)";
        } else if (karma >= 0) {
            color = "§b";
            note = "";
        } else if (karma > -300) {
            color = "§e";
            note = "";
        } else {
            color = "§c";
            note = " §7(епічні/легендарні івенти більше не випадають)";
        }

        source.sendSuccess(() -> Component.literal("§7[Karma] Твоя удача: " + color + karma + " §7(від -500 до +500)" + note), false);
        return 1;
    }

    private static int showShop(CommandSourceStack source) {
        source.sendSuccess(() -> Component.literal("§6=== Karma Shop ==="), false);
        source.sendSuccess(() -> Component.literal("§e/force rare      §7→ §a25  §7карми  (Rare івент)"), false);
        source.sendSuccess(() -> Component.literal("§e/force epic      §7→ §a50  §7карми  (Epic івент)"), false);
        source.sendSuccess(() -> Component.literal("§e/force legendary §7→ §a100 §7карми  (Legendary івент)"), false);
        source.sendSuccess(() -> Component.literal("§e/force divine    §7→ §a200 §7карми  (Divine івент)"), false);
        source.sendSuccess(() -> Component.literal("§7Використовуй §e/force <рідкість>"), false);
        return 1;
    }

    private static int forceEvent(CommandSourceStack source, String rarityKey) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();

        if (!FORCE_PRICES.containsKey(rarityKey)) {
            source.sendFailure(Component.literal("§cНевідома рідкість! Доступні: rare, epic, legendary, divine"));
            return 0;
        }

        int price = FORCE_PRICES.get(rarityKey);
        int current = KarmaHandler.getKarma(player);

        if (current < price) {
            source.sendFailure(Component.literal("§cНедостатньо карми! Потрібно §e" + price + "§c, у тебе лише §e" + current));
            return 0;
        }

        ChaosEvents.Rarity target = RARITY_MAP.get(rarityKey);
        ServerLevel level = player.serverLevel();

        boolean success = ChaosEvents.triggerEventOfRarity(level, player, target);

        if (success) {
            KarmaHandler.addKarma(player, -price);
            int left = KarmaHandler.getKarma(player);
            source.sendSuccess(() -> Component.literal("§aВикликано " + rarityKey.toUpperCase() + " івент! §7(-" + price + " карми, залишилось: §e" + left + "§7)"), false);
            return 1;
        } else {
            source.sendFailure(Component.literal("§cНе вдалося викликати івент цієї рідкості (немає таких івентів)."));
            return 0;
        }
    }
}
