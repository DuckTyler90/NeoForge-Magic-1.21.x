package net.duck.magicmod.block.pedestal.recipe;

import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class AltarRecipeManager {
    private static final List<AltarRecipe> RECIPES = new ArrayList<>();

    public static void registerRecipe(AltarRecipe recipe) {
        RECIPES.add(recipe);
    }

    public static ItemStack tryCraft(List<ItemStack> inputs, ItemStack altarItem) {
        for (AltarRecipe recipe : RECIPES) {
            if (recipe.matches(inputs, altarItem)) {
                return recipe.getResult();
            }
        }
        return ItemStack.EMPTY;
    }


    public static List<AltarRecipe> getAllRecipes() {
        return RECIPES;
    }
}