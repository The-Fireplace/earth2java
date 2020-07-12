package slexom.earthtojava.mobs;

import net.minecraft.block.ComposterBlock;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.blockplacer.SimpleBlockPlacer;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.feature.BlockClusterFeatureConfig;
import net.minecraft.world.gen.feature.BlockStateFeatureConfig;
import net.minecraft.world.gen.feature.DefaultFlowersFeature;
import net.minecraft.world.gen.feature.LakesFeature;
import net.minecraft.world.gen.placement.ChanceConfig;
import net.minecraft.world.gen.placement.FrequencyConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import slexom.earthtojava.mobs.config.ConfigHolder;
import slexom.earthtojava.mobs.config.E2JModConfig;
import slexom.earthtojava.mobs.entity.passive.GlowSquidEntity;
import slexom.earthtojava.mobs.init.*;
import slexom.earthtojava.mobs.utils.BiomeSpawnHelper;
import slexom.earthtojava.mobs.world.spawner.E2JWanderingTraderSpawner;

import java.util.Random;

//TODO: modificare ricetta vaso mod
// Finire di sistemare il lama allegro e aggiungerlo al nuovo mercante

@Mod(EarthToJavaMobsMod.MOD_ID)
public class EarthToJavaMobsMod {

    public static final String MOD_ID = "earthtojavamobs";

    public EarthToJavaMobsMod() {
        final ModLoadingContext modLoadingContext = ModLoadingContext.get();
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        FluidInit.FLUIDS.register(modEventBus);
        BlockInit.BLOCKS.register(modEventBus);
        TileEntityTypeInit.TILE_ENTITY_TYPES.register(modEventBus);
        EntityTypesInit.ENTITY_TYPES.register(modEventBus);
        ItemInit.ITEMS.register(modEventBus);
        RecipesInit.RECIPES.register(modEventBus);
        modEventBus.register(this);
        modEventBus.addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(new E2JWanderingTraderSpawner());

        modLoadingContext.registerConfig(ModConfig.Type.CLIENT, ConfigHolder.CLIENT_SPEC);
        modLoadingContext.registerConfig(ModConfig.Type.SERVER, ConfigHolder.SERVER_SPEC);
        modLoadingContext.registerConfig(ModConfig.Type.COMMON, ConfigHolder.COMMON_SPEC);

    }

    private static void setMudLakeSpawn() {
        DeferredWorkQueue.runLater(new Runnable() {
            @Override
            public void run() {
                for (Biome biome : ForgeRegistries.BIOMES.getValues()) {
                    biome.addFeature(GenerationStage.Decoration.LOCAL_MODIFICATIONS, new LakesFeature(BlockStateFeatureConfig::deserialize) {
                        @Override
                        public boolean place(IWorld world, ChunkGenerator generator, Random rand, BlockPos pos, BlockStateFeatureConfig config) {
                            DimensionType dimensionType = world.getDimension().getType();
                            boolean dimensionCriteria = false;
                            if (dimensionType == DimensionType.OVERWORLD)
                                dimensionCriteria = true;
                            if (!dimensionCriteria)
                                return false;
                            return super.place(world, generator, rand, pos, config);
                        }
                    }.withConfiguration(new BlockStateFeatureConfig(BlockInit.MUD_BLOCK.get().getDefaultState())).withPlacement(Placement.WATER_LAKE.configure(new ChanceConfig(E2JModConfig.mudLakeFrequency))));
                }
            }
        });
    }

    private static void registerEntitiesSpawn() {
        registerAnimalEntitySpawn(EntityTypesInit.AMBER_CHICKEN_REGISTRY_OBJECT.get(), E2JModConfig.amberChickenSpawnBiomes.toArray(new String[0]), E2JModConfig.amberChickenWeight, E2JModConfig.amberChickenGroupMin, E2JModConfig.amberChickenGroupMax);
        registerAnimalEntitySpawn(EntityTypesInit.ASHEN_COW_REGISTRY_OBJECT.get(), E2JModConfig.ashenCowSpawnBiomes.toArray(new String[0]), E2JModConfig.ashenCowWeight, E2JModConfig.ashenCowGroupMin, E2JModConfig.ashenCowGroupMax);
        registerAnimalEntitySpawn(EntityTypesInit.CLUCKSHROOM_REGISTRY_OBJECT.get(), E2JModConfig.cluckshroomSpawnBiomes.toArray(new String[0]), E2JModConfig.cluckshroomWeight, E2JModConfig.cluckshroomGroupMin, E2JModConfig.cluckshroomGroupMax);
        registerAnimalEntitySpawn(EntityTypesInit.FLECKED_SHEEP_REGISTRY_OBJECT.get(), E2JModConfig.fleckedSheepSpawnBiomes.toArray(new String[0]), E2JModConfig.fleckedSheepWeight, E2JModConfig.fleckedSheepGroupMin, E2JModConfig.fleckedSheepGroupMax);
        registerGlowingSquidSpawn();
        registerAnimalEntitySpawn(EntityTypesInit.HARELEQUIN_RABBIT_REGISTRY_OBJECT.get(), E2JModConfig.harelequinRabbitSpawnBiomes.toArray(new String[0]), E2JModConfig.harelequinRabbitWeight, E2JModConfig.harelequinRabbitGroupMin, E2JModConfig.harelequinRabbitGroupMax);
        registerAnimalEntitySpawn(EntityTypesInit.HORNED_SHEEP_REGISTRY_OBJECT.get(), E2JModConfig.hornedSheepSpawnBiomes.toArray(new String[0]), E2JModConfig.hornedSheepWeight, E2JModConfig.hornedSheepGroupMin, E2JModConfig.hornedSheepGroupMax);
        registerAnimalEntitySpawn(EntityTypesInit.INKY_SHEEP_REGISTRY_OBJECT.get(), E2JModConfig.inkySheepSpawnBiomes.toArray(new String[0]), E2JModConfig.inkySheepWeight, E2JModConfig.inkySheepGroupMin, E2JModConfig.inkySheepGroupMax);
        registerAnimalEntitySpawn(EntityTypesInit.MIDNIGHT_CHICKEN_REGISTRY_OBJECT.get(), E2JModConfig.midnightChickenSpawnBiomes.toArray(new String[0]), E2JModConfig.midnightChickenWeight, E2JModConfig.midnightChickenGroupMin, E2JModConfig.midnightChickenGroupMax);
        registerAnimalEntitySpawn(EntityTypesInit.MOOBLOOM_REGISTRY_OBJECT.get(), E2JModConfig.moobloomSpawnBiomes.toArray(new String[0]), E2JModConfig.moobloomWeight, E2JModConfig.moobloomGroupMin, E2JModConfig.moobloomGroupMax);
        registerAnimalEntitySpawn(EntityTypesInit.MUDDY_FOOT_RABBIT_REGISTRY_OBJECT.get(), E2JModConfig.muddyFootRabbitSpawnBiomes.toArray(new String[0]), E2JModConfig.muddyFootRabbitWeight, E2JModConfig.muddyFootRabbitGroupMin, E2JModConfig.muddyFootRabbitGroupMax);
        registerAnimalEntitySpawn(EntityTypesInit.MUDDY_PIG_REGISTRY_OBJECT.get(), E2JModConfig.muddyPigSpawnBiomes.toArray(new String[0]), E2JModConfig.muddyPigWeight, E2JModConfig.muddyPigGroupMin, E2JModConfig.muddyPigGroupMax);
        registerAnimalEntitySpawn(EntityTypesInit.PALE_PIG_REGISTRY_OBJECT.get(), E2JModConfig.palePigSpawnBiomes.toArray(new String[0]), E2JModConfig.palePigWeight, E2JModConfig.palePigGroupMin, E2JModConfig.palePigGroupMax);
        registerAnimalEntitySpawn(EntityTypesInit.PIEBALD_PIG_REGISTRY_OBJECT.get(), E2JModConfig.piebaldPigSpawnBiomes.toArray(new String[0]), E2JModConfig.piebaldPigWeight, E2JModConfig.piebaldPigGroupMin, E2JModConfig.piebaldPigGroupMax);
        registerAnimalEntitySpawn(EntityTypesInit.ROCKY_SHEEP_REGISTRY_OBJECT.get(), E2JModConfig.rockySheepSpawnBiomes.toArray(new String[0]), E2JModConfig.rockySheepWeight, E2JModConfig.rockySheepGroupMin, E2JModConfig.rockySheepGroupMax);
        registerMonsterEntitySpawn(EntityTypesInit.SKELETON_WOLF_REGISTRY_OBJECT.get(), E2JModConfig.skeletonWolfSpawnBiomes.toArray(new String[0]), E2JModConfig.skeletonWolfWeight, E2JModConfig.skeletonWolfGroupMin, E2JModConfig.skeletonWolfGroupMax);
        registerAnimalEntitySpawn(EntityTypesInit.SPOTTED_PIG_REGISTRY_OBJECT.get(), E2JModConfig.spottedPigSpawnBiomes.toArray(new String[0]), E2JModConfig.spottedPigWeight, E2JModConfig.spottedPigGroupMin, E2JModConfig.spottedPigGroupMax);
        registerAnimalEntitySpawn(EntityTypesInit.STORMY_CHICKEN_REGISTRY_OBJECT.get(), E2JModConfig.stormyChickenSpawnBiomes.toArray(new String[0]), E2JModConfig.stormyChickenWeight, E2JModConfig.stormyChickenGroupMin, E2JModConfig.stormyChickenGroupMax);
        registerAnimalEntitySpawn(EntityTypesInit.SUNSET_COW_REGISTRY_OBJECT.get(), E2JModConfig.sunsetCowSpawnBiomes.toArray(new String[0]), E2JModConfig.sunsetCowWeight, E2JModConfig.sunsetCowGroupMin, E2JModConfig.sunsetCowGroupMax);
        registerMonsterEntitySpawn(EntityTypesInit.TROPICAL_SLIME_REGISTRY_OBJECT.get(), E2JModConfig.tropicalSlimeSpawnBiomes.toArray(new String[0]), E2JModConfig.tropicalSlimeWeight, E2JModConfig.tropicalSlimeGroupMin, E2JModConfig.tropicalSlimeGroupMax);
        registerAnimalEntitySpawn(EntityTypesInit.VESTED_RABBIT_REGISTRY_OBJECT.get(), E2JModConfig.vestedRabbitSpawnBiomes.toArray(new String[0]), E2JModConfig.vestedRabbitWeight, E2JModConfig.vestedRabbitGroupMin, E2JModConfig.vestedRabbitGroupMax);
        registerAnimalEntitySpawn(EntityTypesInit.WOOLY_COW_REGISTRY_OBJECT.get(), E2JModConfig.woolyCowSpawnBiomes.toArray(new String[0]), E2JModConfig.woolyCowWeight, E2JModConfig.woolyCowGroupMin, E2JModConfig.woolyCowGroupMax);
        registerMobEntitySpawn(EntityTypesInit.FURNACE_GOLEM_REGISTRY_OBJECT.get(), E2JModConfig.furnaceGolemSpawnBiomes.toArray(new String[0]), E2JModConfig.furnaceGolemWeight, E2JModConfig.furnaceGolemGroupMin, E2JModConfig.furnaceGolemGroupMax);
        registerMobEntitySpawn(EntityTypesInit.MELON_GOLEM_REGISTRY_OBJECT.get(), E2JModConfig.melonGolemSpawnBiomes.toArray(new String[0]), E2JModConfig.melonGolemWeight, E2JModConfig.melonGolemGroupMin, E2JModConfig.melonGolemGroupMax);
        registerAnimalEntitySpawn(EntityTypesInit.ALBINO_COW_REGISTRY_OBJECT.get(), E2JModConfig.albinoCowSpawnBiomes.toArray(new String[0]), E2JModConfig.albinoCowWeight, E2JModConfig.albinoCowGroupMin, E2JModConfig.albinoCowGroupMax);
        registerMonsterEntitySpawn(EntityTypesInit.BONE_SPIDER_REGISTRY_OBJECT.get(), E2JModConfig.boneSpiderSpawnBiomes.toArray(new String[0]), E2JModConfig.boneSpiderWeight, E2JModConfig.boneSpiderGroupMin, E2JModConfig.boneSpiderGroupMax);
        registerAnimalEntitySpawn(EntityTypesInit.JUMBO_RABBIT_REGISTRY_OBJECT.get(), E2JModConfig.jumboRabbitSpawnBiomes.toArray(new String[0]), E2JModConfig.jumboRabbitWeight, E2JModConfig.jumboRabbitGroupMin, E2JModConfig.jumboRabbitGroupMax);
        registerAnimalEntitySpawn(EntityTypesInit.JOLLY_LLAMA_REGISTRY_OBJECT.get(), E2JModConfig.jollyLlamaSpawnBiomes.toArray(new String[0]), E2JModConfig.jollyLlamaWeight, E2JModConfig.jollyLlamaGroupMin, E2JModConfig.jollyLlamaGroupMax);
    }

    private static void registerAnimalEntitySpawn(EntityType entity, String[] spawnBiomes, int weight, int minGroupCountIn, int maxGroupCountIn) {
        DeferredWorkQueue.runLater(() -> {
            BiomeSpawnHelper.setCreatureSpawnBiomes(entity, spawnBiomes, weight, minGroupCountIn, maxGroupCountIn);
            EntitySpawnPlacementRegistry.register(entity, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::canAnimalSpawn);
        });
    }

    private static void registerMonsterEntitySpawn(EntityType entity, String[] spawnBiomes, int weight, int minGroupCountIn, int maxGroupCountIn) {
        DeferredWorkQueue.runLater(() -> {
            BiomeSpawnHelper.setMonsterSpawnBiomes(entity, spawnBiomes, weight, minGroupCountIn, maxGroupCountIn);
            EntitySpawnPlacementRegistry.register(entity, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MonsterEntity::canMonsterSpawn);
        });
    }

    private static void registerMobEntitySpawn(EntityType entity, String[] spawnBiomes, int weight, int minGroupCountIn, int maxGroupCountIn) {
        DeferredWorkQueue.runLater(() -> {
            BiomeSpawnHelper.setMonsterSpawnBiomes(entity, spawnBiomes, weight, minGroupCountIn, maxGroupCountIn);
            EntitySpawnPlacementRegistry.register(entity, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MobEntity::canSpawnOn);
        });
    }

    private static void registerGlowingSquidSpawn() {
        DeferredWorkQueue.runLater(() -> {
            BiomeSpawnHelper.setWaterCreatureSpawnBiomes(EntityTypesInit.GLOW_SQUID_REGISTRY_OBJECT.get(), E2JModConfig.glowSquidSpawnBiomes.toArray(new String[0]), E2JModConfig.glowSquidWeight, E2JModConfig.glowSquidGroupMin, E2JModConfig.glowSquidGroupMax);
            EntitySpawnPlacementRegistry.register(EntityTypesInit.GLOW_SQUID_REGISTRY_OBJECT.get(), EntitySpawnPlacementRegistry.PlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, GlowSquidEntity::canGlowingSquidSpawn);
        });
    }

    private static void setButtercupSpawn() {
        DeferredWorkQueue.runLater(new Runnable() {
            @Override
            public void run() {
                for (Biome biome : ForgeRegistries.BIOMES.getValues()) {
                    biome.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION,
                            new DefaultFlowersFeature(BlockClusterFeatureConfig::deserialize) {
                                @Override
                                public boolean place(IWorld world, ChunkGenerator generator, Random random, BlockPos pos, BlockClusterFeatureConfig config) {
                                    DimensionType dimensionType = world.getDimension().getType();
                                    boolean dimensionCriteria = false;
                                    if (dimensionType == DimensionType.OVERWORLD)
                                        dimensionCriteria = true;
                                    if (!dimensionCriteria)
                                        return false;
                                    return super.place(world, generator, random, pos, config);
                                }
                            }
                                    .withConfiguration((new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(BlockInit.BUTTERCUP.get().getDefaultState()), new SimpleBlockPlacer())).tries(64).build())
                                    .withPlacement(Placement.COUNT_HEIGHTMAP_32.configure(new FrequencyConfig(5)))
                    );
                }
            }

        });
    }

    private final void registerToComposter(){
        DeferredWorkQueue.runLater(() -> ComposterBlock.registerCompostable(0.65F, BlockInit.BUTTERCUP.get()));
    }

    private void setup(final FMLCommonSetupEvent event) {
        registerToComposter();
        setMudLakeSpawn();
        setButtercupSpawn();
        registerEntitiesSpawn();
    }

    public static class E2JItemGroup extends ItemGroup {
        public static final E2JItemGroup instance = new E2JItemGroup(ItemGroup.GROUPS.length, MOD_ID);

        private E2JItemGroup(int index, String label) {
            super(index, label);
        }

        @Override
        public ItemStack createIcon() {
            return new ItemStack(ItemInit.HORN.get());
        }

    }


}
