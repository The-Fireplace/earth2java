package slexom.earthtojava.mobs.entity.passive;

import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import slexom.earthtojava.mobs.entity.ai.goal.FurnaceGolemDefendVillageTargetGoal;
import slexom.earthtojava.mobs.entity.ai.goal.FurnaceGolemNearestAttackableTargetGoal;
import slexom.earthtojava.mobs.entity.ai.goal.FurnaceGolemShowVillagerTorchGoal;

import java.util.Random;

public class FurnaceGolemEntity extends IronGolemEntity {
    public static final DataParameter<Boolean> IS_ANGRY = EntityDataManager.createKey(FurnaceGolemEntity.class, DataSerializers.BOOLEAN);
    private int attackTimer;
    private int holdTorchTick;
    private int lastBlink = 0;
    private int nextBlinkInterval = new Random().nextInt(760) + 60;
    private int remainingTick = 0;
    private int internalBlinkTick = 0;

    public FurnaceGolemEntity(EntityType<? extends IronGolemEntity> type, World worldIn) {
        super(type, worldIn);
        experienceValue = 5;
        setNoAI(false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(2, new MoveTowardsTargetGoal(this, 0.9D, 32.0F));
        this.goalSelector.addGoal(2, new ReturnToVillageGoal(this, 0.6D, false));
        this.goalSelector.addGoal(4, new PatrolVillageGoal(this, 0.6D));
        this.goalSelector.addGoal(5, new FurnaceGolemShowVillagerTorchGoal(this));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomWalkingGoal(this, 0.6D));
        this.goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new FurnaceGolemDefendVillageTargetGoal(this));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new FurnaceGolemNearestAttackableTargetGoal(this, MobEntity.class, 5, false, false, (p_213619_0_) -> p_213619_0_ instanceof IMob && !(p_213619_0_ instanceof CreeperEntity) && !(p_213619_0_ instanceof TropicalSlimeEntity)));
    }

    public boolean attackEntityAsMob(Entity entityIn) {
        this.attackTimer = 10;
        this.world.setEntityState(this, (byte) 4);
        float f = (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
        float f1 = f > 0.0F ? f / 2.0F + (float) this.rand.nextInt((int) f) : 0.0F;
        boolean flag = entityIn.attackEntityFrom(DamageSource.ON_FIRE, f1);
        if (flag) {
            entityIn.setMotion(entityIn.getMotion().add(0.0D, 0.4D, 0.0D));
            this.applyEnchantments(this, entityIn);
        }
        this.playSound(SoundEvents.ENTITY_IRON_GOLEM_ATTACK, 1.0F, 1.0F);
        return flag;
    }

    public void livingTick() {
        super.livingTick();

        if (this.holdTorchTick > 0) {
            --this.holdTorchTick;
        }
        if (this.isAngry()) {
            float rand = new Random().nextFloat();
            if (rand > 0.80F && rand <= 0.83F) {
                int x = MathHelper.floor(this.getPosX());
                int y = MathHelper.floor(this.getPosY());
                int z = MathHelper.floor(this.getPosZ());
                BlockPos pos = new BlockPos(x, y - 0.2D, z);
                BlockPos posRandom = pos.add(new Random().nextInt(3) - 1, 0, new Random().nextInt(3) - 1);
                if (!this.world.isAirBlock(posRandom) && this.world.isAirBlock(posRandom.up())) {
                    this.world.setBlockState(posRandom.up(), Blocks.FIRE.getDefaultState(), 3);
                }
            }
        }
        if (this.isInWater()) {
            this.attackEntityFrom(DamageSource.DROWN, 5.0F);
        }

        if (this.remainingTick > 0) {
            --this.remainingTick;
        }
        if (this.internalBlinkTick == (this.lastBlink + this.nextBlinkInterval)) {
            this.lastBlink = this.internalBlinkTick;
            this.nextBlinkInterval = new Random().nextInt(740) + 60;
            this.remainingTick = 4;
        }
        ++this.internalBlinkTick;
    }

    public int getBlinkRemainingTicks() {
        return this.remainingTick;
    }

    protected void registerData() {
        super.registerData();
        this.dataManager.register(IS_ANGRY, false);
    }

    public boolean isAngry() {
        return this.dataManager.get(IS_ANGRY);
    }

    public void setAngry(boolean angry) {
        this.dataManager.set(IS_ANGRY, angry);
    }

    @OnlyIn(Dist.CLIENT)
    public void handleStatusUpdate(byte id) {
        if (id == 11) {
            this.holdTorchTick = 400;
        } else if (id == 34) {
            this.holdTorchTick = 0;
        } else {
            super.handleStatusUpdate(id);
        }

    }

    public void setHoldingTorch(boolean holdingTorch) {
        if (holdingTorch) {
            this.holdTorchTick = 400;
            this.world.setEntityState(this, (byte) 11);
        } else {
            this.holdTorchTick = 0;
            this.world.setEntityState(this, (byte) 34);
        }

    }

    @OnlyIn(Dist.CLIENT)
    public int getHoldTorchTick() {
        return this.holdTorchTick;
    }


}
