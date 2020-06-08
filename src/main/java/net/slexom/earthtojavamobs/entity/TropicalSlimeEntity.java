
package net.slexom.earthtojavamobs.entity;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effects;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.registries.ForgeRegistries;
import net.slexom.earthtojavamobs.EarthtojavamobsModElements;
import net.slexom.earthtojavamobs.client.renderer.entity.TropicalSlimeRenderer;

import java.text.MessageFormat;
import java.util.EnumSet;

@EarthtojavamobsModElements.ModElement.Tag
public class TropicalSlimeEntity extends EarthtojavamobsModElements.ModElement {
    public static EntityType entity = null;
    private static final String registryNameEntity = "tropical_slime";
    private static final String registryNameSpawnEgg = MessageFormat.format("{0}_spawn_egg", registryNameEntity);

    public TropicalSlimeEntity(EarthtojavamobsModElements instance) {
        super(instance, 181);
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
    }

    @Override
    public void initElements() {
        entity = (EntityType.Builder.<CustomEntity>create(CustomEntity::new, EntityClassification.CREATURE).setShouldReceiveVelocityUpdates(true)
                .setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(CustomEntity::new).size(0.51F, 0.51F).immuneToFire()).build(registryNameEntity)
                .setRegistryName(registryNameEntity);
        elements.entities.add(() -> entity);
        elements.items.add(
                () -> new SpawnEggItem(entity, 0x0e496e, 0x8ed3ff, new Item.Properties().group(ItemGroup.MISC)).setRegistryName(registryNameSpawnEgg));
    }

    @Override
    public void init(FMLCommonSetupEvent event) {
        DeferredWorkQueue.runLater(new Runnable() {
            @Override
            public void run() {
                for (Biome biome : ForgeRegistries.BIOMES.getValues()) {
                    boolean biomeCriteria = false;
                    if (ForgeRegistries.BIOMES.getKey(biome).equals(new ResourceLocation("beach")))
                        biomeCriteria = true;
                    if (!biomeCriteria)
                        continue;
                    biome.getSpawns(EntityClassification.MONSTER).add(new Biome.SpawnListEntry(entity, 10, 1, 4));
                }
                EntitySpawnPlacementRegistry.register(entity, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                        MonsterEntity::canMonsterSpawn);
            }
        });
    }

    @SubscribeEvent
    public void registerModels(ModelRegistryEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(entity, TropicalSlimeRenderer::new);
    }

    public static class CustomEntity extends CreatureEntity {
        public float squishAmount;
        public float squishFactor;
        public float prevSquishFactor;
        private boolean wasOnGround;
        private int size;

        public CustomEntity(FMLPlayMessages.SpawnEntity packet, World world) {
            this(entity, world);
        }

        public CustomEntity(EntityType<CustomEntity> type, World world) {
            super(type, world);
            this.size = 4;
            experienceValue = this.size;
            setNoAI(false);
            this.moveController = new CustomEntity.MoveHelperController(this);
        }

        @Override
        protected void registerGoals() {
            super.registerGoals();
            this.goalSelector.addGoal(1, new SwimGoal(this));
            this.goalSelector.addGoal(1, new CustomEntity.FloatGoal(this));
            this.goalSelector.addGoal(2, new CustomEntity.AttackGoal(this));
            this.goalSelector.addGoal(3, new CustomEntity.FaceRandomGoal(this));
            this.goalSelector.addGoal(5, new CustomEntity.HopGoal(this));
            this.targetSelector.addGoal(3, (new HurtByTargetGoal(this)));
        }

        @Override
        public CreatureAttribute getCreatureAttribute() {
            return super.getCreatureAttribute();
        }

        protected void dropSpecialItems(DamageSource source, int looting, boolean recentlyHitIn) {
            super.dropSpecialItems(source, looting, recentlyHitIn);
        }

        protected void registerAttributes() {
            super.registerAttributes();
            this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue((double) (16.0D));
            this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue((double) 0.6);
            this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0D);
        }

        public boolean processInteract(PlayerEntity player, Hand hand) {
            ItemStack itemstack = player.getHeldItem(hand);
            if (itemstack.getItem() == Items.BUCKET && !player.abilities.isCreativeMode && !this.isChild()) {
                if (!this.world.isRemote) {
                    this.world.addParticle(ParticleTypes.EXPLOSION, this.getPosX(), this.getPosYHeight(0.5D), this.getPosZ(), 0.0D, 0.0D, 0.0D);
                    this.remove();
                    player.playSound(SoundEvents.ENTITY_SLIME_SQUISH, 1.0F, 1.0F);
                    itemstack.shrink(1);
                    if (itemstack.isEmpty()) {
                        player.setHeldItem(hand, new ItemStack(Items.TROPICAL_FISH_BUCKET));
                    } else if (!player.inventory.addItemStackToInventory(new ItemStack(Items.TROPICAL_FISH_BUCKET))) {
                        player.dropItem(new ItemStack(Items.TROPICAL_FISH_BUCKET), false);
                    }
                    return true;
                } else {
                    return super.processInteract(player, hand);
                }

            } else {
                return super.processInteract(player, hand);
            }
        }

        protected IParticleData getSquishParticle() {
            return ParticleTypes.DRIPPING_WATER;
        }

        public void tick() {
            this.squishFactor += (this.squishAmount - this.squishFactor) * 0.5F;
            this.prevSquishFactor = this.squishFactor;
            super.tick();
            if (this.onGround && !this.wasOnGround) {
                int i = this.size;
                if (spawnCustomParticles()) i = 0; // don't spawn particles if it's handled by the implementation itself
                for (int j = 0; j < i * 8; ++j) {
                    float f = this.rand.nextFloat() * ((float) Math.PI * 2F);
                    float f1 = this.rand.nextFloat() * 0.5F + 0.5F;
                    float f2 = MathHelper.sin(f) * (float) i * 0.5F * f1;
                    float f3 = MathHelper.cos(f) * (float) i * 0.5F * f1;
                    this.world.addParticle(this.getSquishParticle(), this.getPosX() + (double) f2, this.getPosY(), this.getPosZ() + (double) f3, 0.0D, 0.0D, 0.0D);
                }
                this.playSound(this.getSquishSound(), this.getSoundVolume(), ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) / 0.8F);
                this.squishAmount = -0.5F;
            } else if (!this.onGround && this.wasOnGround) {
                this.squishAmount = 1.0F;
            }
            this.wasOnGround = this.onGround;
            this.alterSquishAmount();
        }

        protected void alterSquishAmount() {
            this.squishAmount *= 0.6F;
        }

        protected int getJumpDelay() {
            return this.rand.nextInt(20) + 10;
        }

        public EntityType<? extends TropicalSlimeEntity.CustomEntity> getType() {
            return (EntityType<? extends TropicalSlimeEntity.CustomEntity>) super.getType();
        }

        public void applyEntityCollision(Entity entityIn) {
            super.applyEntityCollision(entityIn);
            if (entityIn instanceof IronGolemEntity && this.canDamagePlayer()) {
                this.dealDamage((LivingEntity) entityIn);
            }
        }

        public void onCollideWithPlayer(PlayerEntity entityIn) {
            if (this.canDamagePlayer()) {
                this.dealDamage(entityIn);
            }

        }

        protected void dealDamage(LivingEntity entityIn) {
            if (this.isAlive()) {
                int i = this.size;
                if (this.getDistanceSq(entityIn) < 0.6D * (double) i * 0.6D * (double) i && this.canEntityBeSeen(entityIn) && entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), this.func_225512_er_())) {
                    this.playSound(SoundEvents.ENTITY_SLIME_ATTACK, 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
                    this.applyEnchantments(this, entityIn);
                }
            }

        }

        protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
            return 0.625F * sizeIn.height;
        }

        protected boolean canDamagePlayer() {
            return this.isServerWorld();
        }

        protected float func_225512_er_() {
            return (float) this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getValue();
        }

        protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
            return SoundEvents.ENTITY_SLIME_HURT;
        }

        protected SoundEvent getDeathSound() {
            return SoundEvents.ENTITY_SLIME_DEATH;
        }

        protected SoundEvent getSquishSound() {
            return SoundEvents.ENTITY_SLIME_SQUISH;
        }

        protected ResourceLocation getLootTable() {
            return this.getType().getLootTable();
        }

        public int getVerticalFaceSpeed() {
            return 0;
        }

        protected boolean makesSoundOnJump() {
            return true;
        }

        protected void jump() {
            Vec3d vec3d = this.getMotion();
            this.setMotion(vec3d.x, (double) this.getJumpUpwardsMotion(), vec3d.z);
            this.isAirBorne = true;
        }

        protected SoundEvent getJumpSound() {
            return SoundEvents.ENTITY_SLIME_JUMP;
        }


        protected boolean spawnCustomParticles() {
            return false;
        }


        static class AttackGoal extends Goal {
            private final CustomEntity slime;
            private int growTieredTimer;

            public AttackGoal(CustomEntity slimeIn) {
                this.slime = slimeIn;
                this.setMutexFlags(EnumSet.of(Goal.Flag.LOOK));
            }

            /**
             * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
             * method as well.
             */
            public boolean shouldExecute() {
                LivingEntity livingentity = this.slime.getAttackTarget();
                if (livingentity == null) {
                    return false;
                } else if (!livingentity.isAlive()) {
                    return false;
                } else {
                    return livingentity instanceof PlayerEntity && ((PlayerEntity) livingentity).abilities.disableDamage ? false : this.slime.getMoveHelper() instanceof CustomEntity.MoveHelperController;
                }
            }

            /**
             * Execute a one shot task or start executing a continuous task
             */
            public void startExecuting() {
                this.growTieredTimer = 300;
                super.startExecuting();
            }

            /**
             * Returns whether an in-progress EntityAIBase should continue executing
             */
            public boolean shouldContinueExecuting() {
                LivingEntity livingentity = this.slime.getAttackTarget();
                if (livingentity == null) {
                    return false;
                } else if (!livingentity.isAlive()) {
                    return false;
                } else if (livingentity instanceof PlayerEntity && ((PlayerEntity) livingentity).abilities.disableDamage) {
                    return false;
                } else {
                    return --this.growTieredTimer > 0;
                }
            }

            /**
             * Keep ticking a continuous task that has already been started
             */
            public void tick() {
                this.slime.faceEntity(this.slime.getAttackTarget(), 10.0F, 10.0F);
                ((CustomEntity.MoveHelperController) this.slime.getMoveHelper()).setDirection(this.slime.rotationYaw, this.slime.canDamagePlayer());
            }
        }

        static class FaceRandomGoal extends Goal {
            private final CustomEntity slime;
            private float chosenDegrees;
            private int nextRandomizeTime;

            public FaceRandomGoal(CustomEntity slimeIn) {
                this.slime = slimeIn;
                this.setMutexFlags(EnumSet.of(Goal.Flag.LOOK));
            }

            /**
             * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
             * method as well.
             */
            public boolean shouldExecute() {
                return this.slime.getAttackTarget() == null && (this.slime.onGround || this.slime.isInWater() || this.slime.isInLava() || this.slime.isPotionActive(Effects.LEVITATION)) && this.slime.getMoveHelper() instanceof CustomEntity.MoveHelperController;
            }

            /**
             * Keep ticking a continuous task that has already been started
             */
            public void tick() {
                if (--this.nextRandomizeTime <= 0) {
                    this.nextRandomizeTime = 40 + this.slime.getRNG().nextInt(60);
                    this.chosenDegrees = (float) this.slime.getRNG().nextInt(360);
                }

                ((CustomEntity.MoveHelperController) this.slime.getMoveHelper()).setDirection(this.chosenDegrees, false);
            }
        }

        static class FloatGoal extends Goal {
            private final CustomEntity slime;

            public FloatGoal(CustomEntity slimeIn) {
                this.slime = slimeIn;
                this.setMutexFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
                slimeIn.getNavigator().setCanSwim(true);
            }

            /**
             * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
             * method as well.
             */
            public boolean shouldExecute() {
                return (this.slime.isInWater() || this.slime.isInLava()) && this.slime.getMoveHelper() instanceof CustomEntity.MoveHelperController;
            }

            public void tick() {
                if (this.slime.getRNG().nextFloat() < 0.8F) {
                    this.slime.getJumpController().setJumping();
                }
                ((CustomEntity.MoveHelperController) this.slime.getMoveHelper()).setSpeed(1.2D);
            }
        }

        static class HopGoal extends Goal {
            private final CustomEntity slime;

            public HopGoal(CustomEntity slimeIn) {
                this.slime = slimeIn;
                this.setMutexFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
            }

            public boolean shouldExecute() {
                return !this.slime.isPassenger();
            }

            public void tick() {
                ((CustomEntity.MoveHelperController) this.slime.getMoveHelper()).setSpeed(1.0D);
            }
        }

        static class MoveHelperController extends MovementController {
            private float yRot;
            private int jumpDelay;
            private final CustomEntity slime;
            private boolean isAggressive;

            public MoveHelperController(CustomEntity slimeIn) {
                super(slimeIn);
                this.slime = slimeIn;
                this.yRot = 180.0F * slimeIn.rotationYaw / (float) Math.PI;
            }

            public void setDirection(float yRotIn, boolean aggressive) {
                this.yRot = yRotIn;
                this.isAggressive = aggressive;
            }

            public void setSpeed(double speedIn) {
                this.speed = speedIn;
                this.action = MovementController.Action.MOVE_TO;
            }

            public void tick() {
                this.mob.rotationYaw = this.limitAngle(this.mob.rotationYaw, this.yRot, 90.0F);
                this.mob.rotationYawHead = this.mob.rotationYaw;
                this.mob.renderYawOffset = this.mob.rotationYaw;
                if (this.action != MovementController.Action.MOVE_TO) {
                    this.mob.setMoveForward(0.0F);
                } else {
                    this.action = MovementController.Action.WAIT;
                    if (this.mob.onGround) {
                        this.mob.setAIMoveSpeed((float) (this.speed * this.mob.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue()));
                        if (this.jumpDelay-- <= 0) {
                            this.jumpDelay = this.slime.getJumpDelay();
                            if (this.isAggressive) {
                                this.jumpDelay /= 3;
                            }

                            this.slime.getJumpController().setJumping();
                            if (this.slime.makesSoundOnJump()) {
                                this.slime.playSound(this.slime.getJumpSound(), this.slime.getSoundVolume(), ((this.slime.getRNG().nextFloat() - this.slime.getRNG().nextFloat()) * 0.2F + 1.0F) * 0.8F);
                            }
                        } else {
                            this.slime.moveStrafing = 0.0F;
                            this.slime.moveForward = 0.0F;
                            this.mob.setAIMoveSpeed(0.0F);
                        }
                    } else {
                        this.mob.setAIMoveSpeed((float) (this.speed * this.mob.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue()));
                    }

                }
            }
        }

//
//        public static boolean func_223366_c(EntityType<SlimeEntity> p_223366_0_, IWorld p_223366_1_, SpawnReason reason, BlockPos p_223366_3_, Random randomIn) {
//            if (p_223366_1_.getWorldInfo().getGenerator().handleSlimeSpawnReduction(randomIn, p_223366_1_) && randomIn.nextInt(4) != 1) {
//                return false;
//            } else {
//                if (p_223366_1_.getDifficulty() != Difficulty.PEACEFUL) {
//                    Biome biome = p_223366_1_.getBiome(p_223366_3_);
//                    if (biome == Biomes.BEACH && p_223366_3_.getY() > 60 && p_223366_3_.getY() < 80 && randomIn.nextFloat() < 0.5F && randomIn.nextFloat() < p_223366_1_.getCurrentMoonPhaseFactor() && p_223366_1_.getLight(p_223366_3_) <= randomIn.nextInt(8)) {
//                        return canSpawnOn(p_223366_0_, p_223366_1_, reason, p_223366_3_, randomIn);
//                    }
//                }
//                return false;
//            }
//        }


    }
}
