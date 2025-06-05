package net.duck.magicmod.block.pedestal.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class ModRecipes {
    public static void registerAltarRecipes() {
        AltarRecipeManager.registerRecipe(new AltarRecipe(
                List.of(Items.IRON_INGOT, Items.GOLD_INGOT), // mini pedestal items
                Items.GLASS,                                 // catalyst item on altar
                new ItemStack(Items.DIAMOND)                 // result
        ));

        AltarRecipeManager.registerRecipe(new AltarRecipe(
                List.of(Items.DIAMOND, Items.DIAMOND), // mini pedestal items
                Items.GLASS,                           // catalyst item on altar
                new ItemStack(Items.NETHERITE_INGOT)   // result
        ));
    }
}