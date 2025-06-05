package net.duck.magicmod.item;

import net.duck.magicmod.MagicMod;
import net.duck.magicmod.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;


import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MOD_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MagicMod.MOD_ID);

    public static final Supplier<CreativeModeTab> WIP_ITEMS_TAB = CREATIVE_MOD_TAB.register("wip_items_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModBlocks.TEST_BLOCK.get()))
                    .title(Component.translatable("creativetab.magicmod.test_items"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModItems.TEST_ITEM);
                    }).build());

    public static final Supplier<CreativeModeTab> WIP_BLOCKS_TAB = CREATIVE_MOD_TAB.register("wip_blocks_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModBlocks.TEST_BLOCK.get()))
                    .withTabsBefore(ResourceLocation.fromNamespaceAndPath(MagicMod.MOD_ID, "wip_items_tab"))
                    .title(Component.translatable("creativetab.magicmod.test_blocks"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModBlocks.TEST_BLOCK);
                        output.accept(ModBlocks.PEDESTAL);
                        output.accept(ModBlocks.MINI_PEDESTAL);
                    }).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MOD_TAB.register(eventBus);
    }
}
