package com.chaosclock.registry;

import com.chaosclock.recipe.PureSurgeOfFortuneRecipe;
import com.chaosclock.recipe.SurgeOfFortuneRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, "chaosclock");
    
    public static final RegistryObject<SimpleCraftingRecipeSerializer<SurgeOfFortuneRecipe>> SURGE_OF_FORTUNE_SERIALIZER = 
        SERIALIZERS.register("surge_of_fortune", () -> new SimpleCraftingRecipeSerializer<>(SurgeOfFortuneRecipe::new));

    public static final RegistryObject<SimpleCraftingRecipeSerializer<PureSurgeOfFortuneRecipe>> PURE_SURGE_OF_FORTUNE_SERIALIZER = 
        SERIALIZERS.register("pure_surge_of_fortune", () -> new SimpleCraftingRecipeSerializer<>(PureSurgeOfFortuneRecipe::new));

    public static void register(IEventBus bus) {
        SERIALIZERS.register(bus);
    }
}
