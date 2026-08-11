package com.chaosclock.recipe;

import com.chaosclock.registry.ModPotions;
import com.chaosclock.registry.ModRecipes;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class PureSurgeOfFortuneRecipe extends CustomRecipe {

    public PureSurgeOfFortuneRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    private boolean isSurgePotion(ItemStack stack) {
        if (stack.getItem() != Items.POTION) return false;
        Potion potion = PotionUtils.getPotion(stack);
        return potion == ModPotions.SURGE_OF_FORTUNE.get();
    }

    @Override
    public boolean matches(CraftingContainer inv, Level level) {
        int count = 0;
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (stack.isEmpty()) continue;
            if (isSurgePotion(stack)) {
                count++;
            } else {
                return false; // something else in the grid
            }
        }
        return count == 9;
    }

    @Override
    public ItemStack assemble(CraftingContainer inv, RegistryAccess registryAccess) {
        ItemStack result = new ItemStack(Items.POTION);
        PotionUtils.setPotion(result, ModPotions.PURE_SURGE_OF_FORTUNE.get());
        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 9;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        ItemStack result = new ItemStack(Items.POTION);
        PotionUtils.setPotion(result, ModPotions.PURE_SURGE_OF_FORTUNE.get());
        return result;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.PURE_SURGE_OF_FORTUNE_SERIALIZER.get();
    }
}
