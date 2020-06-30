package net.slexom.earthtojavamobs;

import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.registries.IForgeRegistry;
import net.slexom.earthtojavamobs.client.renderer.tileentity.RainbowBedItemStackTileEntityRenderer;
import net.slexom.earthtojavamobs.config.ConfigHelper;
import net.slexom.earthtojavamobs.config.ConfigHolder;
import net.slexom.earthtojavamobs.init.BlockInit;
import net.slexom.earthtojavamobs.item.ModdedSpawnEggItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(modid = EarthtojavamobsMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModEventSubscriber {
    private static final Logger LOGGER = LogManager.getLogger(EarthtojavamobsMod.MOD_ID + " Mod Event Subscriber");

    @SubscribeEvent
    public static void onRegisterItems(final RegistryEvent.Register<Item> event) {
        final IForgeRegistry<Item> registry = event.getRegistry();
        final ItemGroup ITEM_GROUP = EarthtojavamobsMod.E2JItemGroup.instance;
        BlockInit.BLOCKS.getEntries().stream()
                .filter(block -> !(block.get() instanceof FlowingFluidBlock))
                .map(RegistryObject::get)
                .forEach(block -> {
                    final Item.Properties properties = new Item.Properties().group(ITEM_GROUP);
                    final Item.Properties hiddenBlockProperties = new Item.Properties().group(null);
                    if (block == BlockInit.MELON_GOLEM_HEAD_BLINK.get() || block == BlockInit.MELON_GOLEM_HEAD_SHOOT.get()) {
                        final BlockItem blockItem = new BlockItem(block, hiddenBlockProperties);
                        blockItem.setRegistryName(block.getRegistryName());
                        registry.register(blockItem);
                    } else if (block == BlockInit.RAINBOW_BED.get()) {
                        final Item.Properties bedProperties = new Item.Properties().setISTER(() -> RainbowBedItemStackTileEntityRenderer::new).group(ITEM_GROUP);
                        final BlockItem blockItem = new BlockItem(block, bedProperties);
                        blockItem.setRegistryName(block.getRegistryName());
                        registry.register(blockItem);
                    } else {
                        final BlockItem blockItem = new BlockItem(block, properties);
                        blockItem.setRegistryName(block.getRegistryName());
                        registry.register(blockItem);
                    }
                });
    }

    @SubscribeEvent
    public static void onModConfigEvent(final ModConfig.ModConfigEvent event) {
        final ModConfig config = event.getConfig();
        if (config.getSpec() == ConfigHolder.COMMON_SPEC) {
            ConfigHelper.bakeCommon(config);
            LOGGER.debug("Baked common config");
        }
//        if (config.getSpec() == ConfigHolder.CLIENT_SPEC) {
//            ConfigHelper.bakeClient(config);
//            LOGGER.debug("Baked client config");
//        } else if (config.getSpec() == ConfigHolder.SERVER_SPEC) {
//            ConfigHelper.bakeServer(config);
//            LOGGER.debug("Baked server config");
//        } else if (config.getSpec() == ConfigHolder.COMMON_SPEC) {
//            ConfigHelper.bakeCommon(config);
//            LOGGER.debug("Baked common config");
//        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPostRegisterEntities(final RegistryEvent.Register<EntityType<?>> event) {
        ModdedSpawnEggItem.initUnaddedEggs();
    }

    @SubscribeEvent
    public static void bedAtlas(TextureStitchEvent.Pre event) {
        ResourceLocation rainbowBedTexture = new ResourceLocation(EarthtojavamobsMod.MOD_ID, "entity/bed/rainbow");
        if (event.getMap().getTextureLocation() == Atlases.BED_ATLAS) {
            event.addSprite(rainbowBedTexture);
        }
    }
}