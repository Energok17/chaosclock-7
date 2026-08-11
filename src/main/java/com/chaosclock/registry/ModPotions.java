package com.chaosclock.registry;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModPotions {
    public static final int SURGE_OF_FORTUNE_DURATION = 12000; // 10 min
    public static final int PURE_SURGE_DURATION = 2400;        // 2 min

    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(ForgeRegistries.POTIONS, "chaosclock");
    
    public static final RegistryObject<Potion> SURGE_OF_FORTUNE = POTIONS.register("surge_of_fortune", 
        () -> new Potion("surge_of_fortune", 
            new MobEffectInstance(ModEffects.LUCK_SURGE.get(), SURGE_OF_FORTUNE_DURATION, 0)));

    public static final RegistryObject<Potion> PURE_SURGE_OF_FORTUNE = POTIONS.register("pure_surge_of_fortune", 
        () -> new Potion("pure_surge_of_fortune", 
            new MobEffectInstance(ModEffects.PURE_LUCK_SURGE.get(), PURE_SURGE_DURATION, 0)));

    public static void register(IEventBus bus) {
        POTIONS.register(bus);
    }
}
