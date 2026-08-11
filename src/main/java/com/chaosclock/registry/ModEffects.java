package com.chaosclock.registry;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, "chaosclock");
    
    public static final RegistryObject<MobEffect> LUCK_SURGE = EFFECTS.register("luck_surge", 
        () -> new LuckSurgeEffect(MobEffectCategory.BENEFICIAL, 0xFFD700)); // gold
    
    public static final RegistryObject<MobEffect> PURE_LUCK_SURGE = EFFECTS.register("pure_luck_surge", 
        () -> new PureLuckSurgeEffect(MobEffectCategory.BENEFICIAL, 0xE040FB)); // bright purple/pink

    public static void register(IEventBus bus) {
        EFFECTS.register(bus);
    }

    public static class LuckSurgeEffect extends MobEffect {
        public LuckSurgeEffect(MobEffectCategory category, int color) {
            super(category, color);
        }
    }

    public static class PureLuckSurgeEffect extends MobEffect {
        public PureLuckSurgeEffect(MobEffectCategory category, int color) {
            super(category, color);
        }
    }
}
