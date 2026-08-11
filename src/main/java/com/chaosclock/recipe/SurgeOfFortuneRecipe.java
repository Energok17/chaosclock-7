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
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

public class SurgeOfFortuneRecipe extends CustomRecipe {

    public SurgeOfFortuneRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    private boolean isWaterBottle(ItemStack stack) {
        return stack.getItem() == Items.POTION && PotionUtils.getPotion(stack) == Potions.WATER;
    }

    @Override
    public boolean matches(CraftingContainer inv, Level level) {
        for (int row = 0; row < 3; ++row) {
            ItemStack left = inv.getItem(row * 3);
            ItemStack mid = inv.getItem(row * 3 + 1);
            ItemStack right = inv.getItem(row * 3 + 2);
            boolean rowMatches = left.getItem() == Items.GOLD_NUGGET && this.isWaterBottle(mid) && right.getItem() == Items.GOLD_NUGGET;
            if (!rowMatches) continue;
            boolean restEmpty = true;
            for (int i = 0; i < inv.getContainerSize(); ++i) {
                if (i == row * 3 || i == row * 3 + 1 || i == row * 3 + 2 || inv.getItem(i).isEmpty()) continue;
                restEmpty = false;
                break;
            }
            if (!restEmpty) continue;
            return true;
        }
        return false;
    }

    @Override
    public ItemStack assemble(CraftingContainer inv, RegistryAccess registryAccess) {
        ItemStack result = new ItemStack((ItemLike) Items.POTION);
        PotionUtils.setPotion(result, (Potion) ModPotions.SURGE_OF_FORTUNE.get());
        return result;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer inv) {
        NonNullList<ItemStack> remaining = NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);
        for (int i = 0; i < remaining.size(); ++i) {
            ItemStack stack = inv.getItem(i);
            if (!this.isWaterBottle(stack)) continue;
            remaining.set(i, new ItemStack((ItemLike) Items.GLASS_BOTTLE));
        }
        return remaining;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 3;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        ItemStack result = new ItemStack((ItemLike) Items.POTION);
        PotionUtils.setPotion(result, (Potion) ModPotions.SURGE_OF_FORTUNE.get());
        return result;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return (RecipeSerializer<?>) ModRecipes.SURGE_OF_FORTUNE_SERIALIZER.get();
    }
}
