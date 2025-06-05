package net.duck.magicmod.block.pedestal.recipe;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AltarRecipe {
    private final List<Item> inputItems; // from mini pedestals
    private final Item catalystItem;     // from main altar
    private final ItemStack result;

    public AltarRecipe(List<Item> inputItems, Item catalystItem, ItemStack result) {
        this.inputItems = inputItems;
        this.catalystItem = catalystItem;
        this.result = result;
    }

    public boolean matches(List<ItemStack> inputs, ItemStack altarItem) {
        List<Item> remainingInputs = new ArrayList<>(inputs.stream().map(ItemStack::getItem).toList());

        for (Item required : inputItems) {
            if (!remainingInputs.remove(required)) {
                return false;
            }
        }

        return remainingInputs.isEmpty() && altarItem.getItem() == catalystItem;
    }

    public ItemStack getResult() {
        return result.copy();
    }
}