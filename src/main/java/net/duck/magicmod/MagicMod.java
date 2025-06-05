package net.duck.magicmod;

import net.duck.magicmod.block.ModBlockEntities;
import net.duck.magicmod.block.ModBlocks;
import net.duck.magicmod.block.entity.renderer.MiniPedestalBlockEntityRenderer;
import net.duck.magicmod.block.entity.renderer.PedestalBlockEntityRenderer;
import net.duck.magicmod.block.pedestal.PedestalBlockEntity;
import net.duck.magicmod.block.pedestal.recipe.ModRecipes;
import net.duck.magicmod.item.ModCreativeModeTabs;
import net.duck.magicmod.item.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
@Mod(MagicMod.MOD_ID)
public class MagicMod
{
    public static final String MOD_ID = "magicmod";
    private static final Logger LOGGER = LogUtils.getLogger();

    public MagicMod(IEventBus modEventBus, ModContainer modContainer)
    {
        NeoForge.EVENT_BUS.register(this);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);

        ModCreativeModeTabs.register(modEventBus);

        ModBlockEntities.register(modEventBus);

        ModRecipes.registerAltarRecipes();
    }

    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    @EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(ModBlockEntities.PEDESTAL_BE.get(), PedestalBlockEntityRenderer::new);
            event.registerBlockEntityRenderer(ModBlockEntities.MINI_PEDESTAL_BE.get(), MiniPedestalBlockEntityRenderer::new);
        }
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {

    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        System.out.println("Recipes loaded successfully!");
    }
}