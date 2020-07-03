package net.slexom.earthtojavamobs.entity.passive;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;
import net.slexom.earthtojavamobs.entity.base.E2JBasePigEntity;
import net.slexom.earthtojavamobs.init.BlockInit;


public class MuddyPigEntity extends E2JBasePigEntity<MuddyPigEntity> {

    private static final DataParameter<Boolean> MUDDY_STATE = EntityDataManager.createKey(MuddyPigEntity.class, DataSerializers.BOOLEAN);
    private static final Ingredient TEMPTATION_ITEMS = Ingredient.fromItems(Items.CARROT, Items.POTATO, Items.BEETROOT);
    private int outOfMud = 0;
    private int finallyInMud = 0;

    private boolean isShaking;
    private float timeWolfIsShaking;
    private float prevTimeWolfIsShaking;

    public MuddyPigEntity(EntityType<MuddyPigEntity> type, World world) {
        super(type, world);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.25D));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.2D, Ingredient.fromItems(Items.CARROT_ON_A_STICK), false));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.2D, TEMPTATION_ITEMS, false));
        this.goalSelector.addGoal(4, new MuddyPigEntity.GoToMudGoal(this, 1.2D));
        this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
        this.goalSelector.addGoal(8, new RandomWalkingGoal(this, 1.0D, 100));
    }

    @Override
    public void livingTick() {
        super.livingTick();
        int i = MathHelper.floor(this.getPosX());
        int j = MathHelper.floor(this.getPosY());
        int k = MathHelper.floor(this.getPosZ());
        BlockPos blockPos = new BlockPos(i, j, k);
        boolean condition = this.world.getFluidState(blockPos).getBlockState().getBlock().equals(BlockInit.MUD_BLOCK.get());
        if (condition) {
            if (!isInMuddyState()) {
                if(!isShaking) {
                    this.isShaking = true;
                    this.timeWolfIsShaking = 0.0F;
                    this.prevTimeWolfIsShaking = 0.0F;
                    this.world.setEntityState(this, (byte) 8);
                }
                if (++finallyInMud > 60) {
                    setMuddyState(true);
                    finallyInMud = 0;
                    resetShake();
                }
            }
        } else {
            if (isInMuddyState()) {
                outOfMud++;
                if (outOfMud > 60) {
                    setMuddyState(false);
                    outOfMud = 0;
                }
            }
        }
    }

    private void resetShake() {
        this.isShaking = false;
        this.prevTimeWolfIsShaking = 0.0F;
        this.timeWolfIsShaking = 0.0F;
    }

    public void tick() {
        super.tick();
        if (this.isAlive()) {
            if (isShaking) {
                this.prevTimeWolfIsShaking = this.timeWolfIsShaking;
                this.timeWolfIsShaking += 0.033F;
                if (this.prevTimeWolfIsShaking >= 2.0F) {
                    this.resetShake();
                }
            }
        }
    }

    public void onDeath(DamageSource cause) {
        this.resetShake();
        super.onDeath(cause);
    }

    protected void registerData() {
        super.registerData();
        this.dataManager.register(MUDDY_STATE, false);
    }

    public boolean isInMuddyState() {
        return this.dataManager.get(MUDDY_STATE);
    }

    public void setMuddyState(boolean inMud) {
        this.dataManager.set(MUDDY_STATE, inMud);
    }

    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putBoolean("IsInMud", this.isInMuddyState());
    }

    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.setMuddyState(compound.getBoolean("IsInMud"));
    }

    public static class GoToMudGoal extends MoveToBlockGoal {
        private final MuddyPigEntity muddyPig;

        public GoToMudGoal(MuddyPigEntity entity, double speedIn) {
            super(entity, speedIn, 16, 3);
            this.muddyPig = entity;
            this.field_203112_e = -1;
        }

        public boolean shouldExecute() {
            return !this.muddyPig.isInMuddyState() && super.shouldExecute();
        }

        public boolean shouldContinueExecuting() {
            return !this.muddyPig.isInMuddyState() && this.timeoutCounter <= 600 && this.shouldMoveTo(this.muddyPig.world, this.destinationBlock);
        }

        public boolean shouldMove() {
            return this.timeoutCounter % 100 == 0;
        }

        @Override
        protected boolean shouldMoveTo(IWorldReader worldIn, BlockPos pos) {
            Block block = worldIn.getBlockState(pos).getBlock();
            return block == BlockInit.MUD_BLOCK.get();
        }
    }

//    static class RollGoal extends Goal {
//        private final MuddyPigEntity muddyPigEntity;
//
//        public RollGoal(MuddyPigEntity entity) {
//            this.muddyPigEntity = entity;
//            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
//        }
//
//        /**
//         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
//         * method as well.
//         */
//        public boolean shouldExecute() {
//            if ((this.muddyPigEntity.isChild() || this.muddyPigEntity.isPlayful()) && this.muddyPigEntity.onGround) {
//                if (!this.muddyPigEntity.canPerformAction()) {
//                    return false;
//                } else {
//                    float f = this.muddyPigEntity.rotationYaw * ((float)Math.PI / 180F);
//                    int i = 0;
//                    int j = 0;
//                    float f1 = -MathHelper.sin(f);
//                    float f2 = MathHelper.cos(f);
//                    if ((double)Math.abs(f1) > 0.5D) {
//                        i = (int)((float)i + f1 / Math.abs(f1));
//                    }
//
//                    if ((double)Math.abs(f2) > 0.5D) {
//                        j = (int)((float)j + f2 / Math.abs(f2));
//                    }
//
//                    if (this.muddyPigEntity.world.getBlockState((new BlockPos(this.muddyPigEntity)).add(i, -1, j)).isAir()) {
//                        return true;
//                    } else if (this.muddyPigEntity.isPlayful() && this.muddyPigEntity.rand.nextInt(60) == 1) {
//                        return true;
//                    } else {
//                        return this.muddyPigEntity.rand.nextInt(500) == 1;
//                    }
//                }
//            } else {
//                return false;
//            }
//        }
//
//        /**
//         * Returns whether an in-progress EntityAIBase should continue executing
//         */
//        public boolean shouldContinueExecuting() {
//            return false;
//        }
//
//        /**
//         * Execute a one shot task or start executing a continuous task
//         */
//        public void startExecuting() {
//            this.muddyPigEntity.func_213576_v(true);
//        }
//
//        public boolean isPreemptible() {
//            return false;
//        }
//    }



    @OnlyIn(Dist.CLIENT)
    public void handleStatusUpdate(byte id) {
        if (id == 8) {
            this.isShaking = true;
            this.timeWolfIsShaking = 0.0F;
            this.prevTimeWolfIsShaking = 0.0F;
        } else {
            super.handleStatusUpdate(id);
        }

    }

    @OnlyIn(Dist.CLIENT)
    public float getShakeAngle(float p_70923_1_, float p_70923_2_) {
        float f = (MathHelper.lerp(p_70923_1_, this.prevTimeWolfIsShaking, this.timeWolfIsShaking) + p_70923_2_) / 1.8F;
        if (f < 0.0F) {
            f = 0.0F;
        } else if (f > 1.0F) {
            f = 1.0F;
        }

        return MathHelper.sin(f * (float) Math.PI) * MathHelper.sin(f * (float) Math.PI * 11.0F) * 0.15F * (float) Math.PI;
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
 