package slexom.earthtojava.mobs.entity.ai.goal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import slexom.earthtojava.mobs.entity.ai.controller.TropicalSlimeMovementController;
import slexom.earthtojava.mobs.entity.passive.TropicalSlimeEntity;

import java.util.EnumSet;

public class TropicalSlimeAttackGoal extends Goal {
    private final TropicalSlimeEntity slime;
    private int growTieredTimer;

    public TropicalSlimeAttackGoal(TropicalSlimeEntity slimeIn) {
        this.slime = slimeIn;
        this.setMutexFlags(EnumSet.of(Flag.LOOK));
    }

    public boolean shouldExecute() {
        LivingEntity livingentity = this.slime.getAttackTarget();
        if (livingentity == null) {
            return false;
        } else if (!livingentity.isAlive()) {
            return false;
        } else {
            return livingentity instanceof PlayerEntity && ((PlayerEntity) livingentity).abilities.disableDamage ? false : this.slime.getMoveHelper() instanceof TropicalSlimeMovementController;
        }
    }

    public void startExecuting() {
        this.growTieredTimer = 300;
        super.startExecuting();
    }

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

    public void tick() {
        this.slime.faceEntity(this.slime.getAttackTarget(), 10.0F, 10.0F);
        ((TropicalSlimeMovementController) this.slime.getMoveHelper()).setDirection(this.slime.rotationYaw, this.slime.canDamagePlayer());
    }
}
