package slexom.earthtojava.mobs;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CarvedPumpkinBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.pattern.BlockMaterialMatcher;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.BlockStateMatcher;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.EggEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import slexom.earthtojava.mobs.entity.passive.FurnaceGolemEntity;
import slexom.earthtojava.mobs.init.BlockInit;
import slexom.earthtojava.mobs.init.EntityTypesInit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;
import java.util.function.Predicate;


@EventBusSubscriber(modid = EarthToJavaMobsMod.MOD_ID, bus = EventBusSubscriber.Bus.FORGE)
public final class ForgeEventSubscriber {

    private static final Logger LOGGER = LogManager.getLogger(EarthToJavaMobsMod.MOD_ID + " Mod Event Subscriber");
    private static final Predicate<BlockState> IS_PUMPKIN = (blockState) -> blockState != null && (blockState.getBlock() == Blocks.CARVED_PUMPKIN || blockState.getBlock() == Blocks.JACK_O_LANTERN);

    @SubscribeEvent
    public static void onEggThrown(ProjectileImpactEvent.Throwable event) {
        ThrowableEntity throwable = event.getThrowable();
        World world = throwable.world;
        if (throwable instanceof EggEntity) {
            if (!world.isRemote) {
                if (new Random().nextInt(8) == 0) {
                    int i = 1;
                    if (new Random().nextInt(32) == 0) {
                        i = 4;
                    }
                    for (int j = 0; j < i; ++j) {
                        ChickenEntity chickenentity = getChickenEntity(world);
                        chickenentity.setGrowingAge(-24000);
                        chickenentity.setLocationAndAngles(throwable.getPosX(), throwable.getPosY(), throwable.getPosZ(), throwable.rotationYaw, 0.0F);
                        world.addEntity(chickenentity);
                    }
                }
                world.setEntityState(throwable, (byte) 3);
                throwable.remove();
            }
        }
    }

    private static ChickenEntity getChickenEntity(World world) {
        int chickenType = new Random().nextInt(6);
        ChickenEntity chickenentity;
        switch (chickenType) {
            case 0:
                chickenentity = EntityTypesInit.AMBER_CHICKEN_REGISTRY_OBJECT.get().create(world);
                break;
            case 2:
                chickenentity = EntityTypesInit.MIDNIGHT_CHICKEN_REGISTRY_OBJECT.get().create(world);
                break;
            case 4:
                chickenentity = EntityTypesInit.STORMY_CHICKEN_REGISTRY_OBJECT.get().create(world);
                break;
            default:
                chickenentity = EntityType.CHICKEN.create(world);
                break;
        }
        return chickenentity;
    }

    @SubscribeEvent
    public static void onCarvingMelon(PlayerInteractEvent.RightClickBlock event) {
        PlayerEntity player = event.getPlayer();
        BlockPos pos = event.getPos();
        Hand hand = event.getHand();
        ItemStack itemstack = player.getHeldItem(hand);
        World worldIn = player.world;
        BlockState blockState = worldIn.getBlockState(pos);
        Block block = blockState.getBlock();
        if (itemstack.getItem() == Items.SHEARS && block == Blocks.MELON) {
            if (!worldIn.isRemote) {
                Direction direction = event.getFace();
                Direction direction1 = direction.getAxis() == Direction.Axis.Y ? player.getHorizontalFacing().getOpposite() : direction;
                worldIn.playSound((PlayerEntity) null, pos, SoundEvents.BLOCK_PUMPKIN_CARVE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                worldIn.setBlockState(pos, BlockInit.CARVED_MELON.get().getDefaultState().with(CarvedPumpkinBlock.FACING, direction1), 11);
                ItemEntity itementity = new ItemEntity(worldIn, (double) pos.getX() + 0.5D + (double) direction1.getXOffset() * 0.65D, (double) pos.getY() + 0.1D, (double) pos.getZ() + 0.5D + (double) direction1.getZOffset() * 0.65D, new ItemStack(Items.MELON_SEEDS, 4));
                itementity.setMotion(0.05D * (double) direction1.getXOffset() + worldIn.rand.nextDouble() * 0.02D, 0.05D, 0.05D * (double) direction1.getZOffset() + worldIn.rand.nextDouble() * 0.02D);
                worldIn.addEntity(itementity);
                itemstack.damageItem(1, player, (entity) -> {
                    entity.sendBreakAnimation(hand);
                });
            }
        }
    }

    @SubscribeEvent
    public static void onFurnaceGolemCreation(BlockEvent.EntityPlaceEvent event) {
        BlockState blockState = event.getPlacedBlock();
        Block block = blockState.getBlock();
        if (block == Blocks.IRON_BLOCK || block == Blocks.BLAST_FURNACE || block == Blocks.JACK_O_LANTERN || block == Blocks.CARVED_PUMPKIN) {
            World world = event.getWorld().getWorld();
            BlockPos pos = event.getPos();
            BlockPattern golemPattern = BlockPatternBuilder.start().aisle("~^~", "#@#", "~#~").where('^', CachedBlockInfo.hasState(IS_PUMPKIN)).where('#', CachedBlockInfo.hasState(BlockStateMatcher.forBlock(Blocks.IRON_BLOCK))).where('@', CachedBlockInfo.hasState(BlockStateMatcher.forBlock(Blocks.BLAST_FURNACE))).where('~', CachedBlockInfo.hasState(BlockMaterialMatcher.forMaterial(Material.AIR))).build();
            BlockPattern.PatternHelper patternHelper = golemPattern.match(world, pos);
            if (patternHelper != null) {
                for (int j = 0; j < golemPattern.getPalmLength(); ++j) {
                    for (int k = 0; k < golemPattern.getThumbLength(); ++k) {
                        CachedBlockInfo cachedBlockInfo = patternHelper.translateOffset(j, k, 0);
                        world.setBlockState(cachedBlockInfo.getPos(), Blocks.AIR.getDefaultState(), 2);
                        world.playEvent(2001, cachedBlockInfo.getPos(), Block.getStateId(cachedBlockInfo.getBlockState()));
                    }
                }
                BlockPos blockpos = patternHelper.translateOffset(1, 2, 0).getPos();
                FurnaceGolemEntity furnaceGolemEntity = EntityTypesInit.FURNACE_GOLEM_REGISTRY_OBJECT.get().create(world);
                furnaceGolemEntity.setPlayerCreated(true);
                furnaceGolemEntity.setLocationAndAngles((double) blockpos.getX() + 0.5D, (double) blockpos.getY() + 0.05D, (double) blockpos.getZ() + 0.5D, 0.0F, 0.0F);
                world.addEntity(furnaceGolemEntity);
                for (ServerPlayerEntity serverplayerentity1 : world.getEntitiesWithinAABB(ServerPlayerEntity.class, furnaceGolemEntity.getBoundingBox().grow(5.0D))) {
                    CriteriaTriggers.SUMMONED_ENTITY.trigger(serverplayerentity1, furnaceGolemEntity);
                }
                for (int i1 = 0; i1 < golemPattern.getPalmLength(); ++i1) {
                    for (int j1 = 0; j1 < golemPattern.getThumbLength(); ++j1) {
                        CachedBlockInfo cachedblockinfo1 = patternHelper.translateOffset(i1, j1, 0);
                        world.notifyNeighbors(cachedblockinfo1.getPos(), Blocks.AIR);
                    }
                }
            }
        }
    }

}