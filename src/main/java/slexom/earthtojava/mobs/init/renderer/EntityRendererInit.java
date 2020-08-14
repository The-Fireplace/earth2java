package slexom.earthtojava.mobs.init.renderer;

import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.entity.EntityType;
import slexom.earthtojava.mobs.client.renderer.entity.*;
import slexom.earthtojava.mobs.client.renderer.tileentity.RainbowBedTileEntityRenderer;
import slexom.earthtojava.mobs.init.BlockEntityTypeInit;
import slexom.earthtojava.mobs.init.EntityTypesInit;

public class EntityRendererInit {

    public static void init() {
        registerEntitiesRenderer();
        registerProjectileRenderer();
        registerTileEntityRenderer();
    }

    private static void registerTileEntityRenderer() {
        BlockEntityRendererRegistry.INSTANCE.register(BlockEntityTypeInit.RAINBOW_BED, RainbowBedTileEntityRenderer::new);
    }

    private static void registerProjectileRenderer() {
        EntityRendererRegistry.INSTANCE.register(EntityTypesInit.MELON_SEED_PROJECTILE_REGISTRY_OBJECT, (dispatcher, context) -> new FlyingItemEntityRenderer<>(dispatcher, MinecraftClient.getInstance().getItemRenderer()));
        EntityRendererRegistry.INSTANCE.register(EntityTypesInit.BONE_SHARD_REGISTRY_OBJECT, (dispatcher, context) -> new FlyingItemEntityRenderer<>(dispatcher, MinecraftClient.getInstance().getItemRenderer()));
    }

    private static void registerEntitiesRenderer() {
        registerBaseVariantEntitiesRenderer();
        registerSpecialVariantEntitiesRenderer();
    }

    private static void registerBaseVariantEntitiesRenderer() {
        registerChickenEntityRenderer(EntityTypesInit.AMBER_CHICKEN_REGISTRY_OBJECT, EntityTypesInit.AMBER_CHICKEN_REGISTRY_NAME);
        registerChickenEntityRenderer(EntityTypesInit.BRONZED_CHICKEN_REGISTRY_OBJECT, EntityTypesInit.BRONZED_CHICKEN_REGISTRY_NAME);
        registerChickenEntityRenderer(EntityTypesInit.MIDNIGHT_CHICKEN_REGISTRY_OBJECT, EntityTypesInit.MIDNIGHT_CHICKEN_REGISTRY_NAME);
        registerChickenEntityRenderer(EntityTypesInit.STORMY_CHICKEN_REGISTRY_OBJECT, EntityTypesInit.STORMY_CHICKEN_REGISTRY_NAME);
        registerCowEntityRenderer(EntityTypesInit.ALBINO_COW_REGISTRY_OBJECT, EntityTypesInit.ALBINO_COW_REGISTRY_NAME);
        registerCowEntityRenderer(EntityTypesInit.ASHEN_COW_REGISTRY_OBJECT, EntityTypesInit.ASHEN_COW_REGISTRY_NAME);
        registerCowEntityRenderer(EntityTypesInit.SUNSET_COW_REGISTRY_OBJECT, EntityTypesInit.SUNSET_COW_REGISTRY_NAME);
        registerPigEntityRenderer(EntityTypesInit.PALE_PIG_REGISTRY_OBJECT, EntityTypesInit.PALE_PIG_REGISTRY_NAME);
        registerPigEntityRenderer(EntityTypesInit.PIEBALD_PIG_REGISTRY_OBJECT, EntityTypesInit.PIEBALD_PIG_REGISTRY_NAME);
        registerPigEntityRenderer(EntityTypesInit.PINK_FOOTED_PIG_REGISTRY_OBJECT, EntityTypesInit.PINK_FOOTED_PIG_REGISTRY_NAME);
        registerPigEntityRenderer(EntityTypesInit.SPOTTED_PIG_REGISTRY_OBJECT, EntityTypesInit.SPOTTED_PIG_REGISTRY_NAME);
        registerOneColorSheepEntityRenderer(EntityTypesInit.FLECKED_SHEEP_REGISTRY_OBJECT, EntityTypesInit.FLECKED_SHEEP_REGISTRY_NAME);
        registerOneColorSheepEntityRenderer(EntityTypesInit.INKY_SHEEP_REGISTRY_OBJECT, EntityTypesInit.INKY_SHEEP_REGISTRY_NAME);
        registerOneColorSheepEntityRenderer(EntityTypesInit.ROCKY_SHEEP_REGISTRY_OBJECT, EntityTypesInit.ROCKY_SHEEP_REGISTRY_NAME);
        registerRabbitEntityRenderer(EntityTypesInit.HARELEQUIN_RABBIT_REGISTRY_OBJECT, EntityTypesInit.HARELEQUIN_RABBIT_REGISTRY_NAME);
        registerRabbitEntityRenderer(EntityTypesInit.MUDDY_FOOT_RABBIT_REGISTRY_OBJECT, EntityTypesInit.MUDDY_FOOT_RABBIT_REGISTRY_NAME);
        registerRabbitEntityRenderer(EntityTypesInit.VESTED_RABBIT_REGISTRY_OBJECT, EntityTypesInit.VESTED_RABBIT_REGISTRY_NAME);
    }

    private static void registerSpecialVariantEntitiesRenderer() {
        EntityRendererRegistry.INSTANCE.register(EntityTypesInit.CLUCKSHROOM_REGISTRY_OBJECT, (dispatcher, context) -> new CluckshroomRenderer(dispatcher));
        EntityRendererRegistry.INSTANCE.register(EntityTypesInit.GLOW_SQUID_REGISTRY_OBJECT, (dispatcher, context) -> new GlowSquidRenderer(dispatcher));
        EntityRendererRegistry.INSTANCE.register(EntityTypesInit.HORNED_SHEEP_REGISTRY_OBJECT, (dispatcher, context) -> new HornedSheepRenderer(dispatcher));
        EntityRendererRegistry.INSTANCE.register(EntityTypesInit.MOOBLOOM_REGISTRY_OBJECT, (dispatcher, context) -> new MoobloomRenderer(dispatcher));
        EntityRendererRegistry.INSTANCE.register(EntityTypesInit.MUDDY_PIG_REGISTRY_OBJECT, (dispatcher, context) -> new MuddyPigRenderer(dispatcher));
        EntityRendererRegistry.INSTANCE.register(EntityTypesInit.SKELETON_WOLF_REGISTRY_OBJECT, (dispatcher, context) -> new SkeletonWolfRenderer(dispatcher));
        EntityRendererRegistry.INSTANCE.register(EntityTypesInit.TROPICAL_SLIME_REGISTRY_OBJECT, (dispatcher, context) -> new TropicalSlimeRenderer(dispatcher));
        EntityRendererRegistry.INSTANCE.register(EntityTypesInit.WOOLY_COW_REGISTRY_OBJECT, (dispatcher, context) -> new WoolyCowRenderer(dispatcher));
        EntityRendererRegistry.INSTANCE.register(EntityTypesInit.FURNACE_GOLEM_REGISTRY_OBJECT, (dispatcher, context) -> new FurnaceGolemRenderer(dispatcher));
        EntityRendererRegistry.INSTANCE.register(EntityTypesInit.MELON_GOLEM_REGISTRY_OBJECT, (dispatcher, context) -> new MelonGolemRenderer(dispatcher));
        EntityRendererRegistry.INSTANCE.register(EntityTypesInit.JUMBO_RABBIT_REGISTRY_OBJECT, (dispatcher, context) -> new JumboRabbitRenderer(dispatcher, EntityTypesInit.JUMBO_RABBIT_REGISTRY_NAME));
        EntityRendererRegistry.INSTANCE.register(EntityTypesInit.JOLLY_LLAMA_REGISTRY_OBJECT, (dispatcher, context) -> new JollyLlamaRenderer(dispatcher));
        EntityRendererRegistry.INSTANCE.register(EntityTypesInit.BONE_SPIDER_REGISTRY_OBJECT, (dispatcher, context) -> new BoneSpiderRenderer(dispatcher));
        EntityRendererRegistry.INSTANCE.register(EntityTypesInit.WANDERING_TRADER_REGISTRY_OBJECT, (dispatcher, context) -> new E2JWanderingTraderRenderer(dispatcher));
        EntityRendererRegistry.INSTANCE.register(EntityTypesInit.RAINBOW_SHEEP_REGISTRY_OBJECT, (dispatcher, context) -> new RainbowSheepRenderer(dispatcher));
    }

    private static void registerChickenEntityRenderer(EntityType<?> entity, String identifier) {
        EntityRendererRegistry.INSTANCE.register(entity, (dispatcher, context) -> new E2JChickenRenderer(dispatcher, identifier));
    }

    private static void registerCowEntityRenderer(EntityType<?> entity, String identifier) {
        EntityRendererRegistry.INSTANCE.register(entity, (dispatcher, context) -> new E2JCowRenderer(dispatcher, identifier));
    }

    private static void registerOneColorSheepEntityRenderer(EntityType<?> entity, String identifier) {
        EntityRendererRegistry.INSTANCE.register(entity, (dispatcher, context) -> new E2JOneColorSheepRenderer<>(dispatcher, identifier));
    }

    private static void registerPigEntityRenderer(EntityType<?> entity, String identifier) {
        EntityRendererRegistry.INSTANCE.register(entity, (dispatcher, context) -> new E2JPigRenderer(dispatcher, identifier));
    }

    private static void registerRabbitEntityRenderer(EntityType<?> entity, String identifier) {
        EntityRendererRegistry.INSTANCE.register(entity, (dispatcher, context) -> new E2JRabbitRenderer(dispatcher, identifier));
    }


}
