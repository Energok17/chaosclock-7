/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.event.RegisterCommandsEvent
 *  net.minecraftforge.eventbus.api.IEventBus
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod
 *  net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.chaosclock;

import com.chaosclock.command.ChaosClockCommand;
import com.chaosclock.registry.ModEffects;
import com.chaosclock.registry.ModPotions;
import com.chaosclock.registry.ModRecipes;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(value="chaosclock")
public class ChaosClockMod {
    public static final String MOD_ID = "chaosclock";
    public static final Logger LOGGER = LoggerFactory.getLogger((String)"chaosclock");

    public ChaosClockMod() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModEffects.register(modBus);
        ModPotions.register(modBus);
        ModRecipes.register(modBus);
        MinecraftForge.EVENT_BUS.register((Object)this);
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        ChaosClockCommand.register((CommandDispatcher<CommandSourceStack>)event.getDispatcher());
    }
}

