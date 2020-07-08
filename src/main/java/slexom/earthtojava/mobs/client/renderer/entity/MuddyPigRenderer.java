package slexom.earthtojava.mobs.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.PigModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import slexom.earthtojava.mobs.client.renderer.entity.model.MuddyPigModel;
import slexom.earthtojava.mobs.entity.passive.MuddyPigEntity;

@OnlyIn(Dist.CLIENT)
public class MuddyPigRenderer extends MobRenderer<MuddyPigEntity, MuddyPigModel<MuddyPigEntity>> {

    public MuddyPigRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new MuddyPigModel<>(), 0.7F);
        this.addLayer(new SaddleLayer(this));
    }

    public ResourceLocation getEntityTexture(MuddyPigEntity entity) {
        ResourceLocation texture = new ResourceLocation("earthtojavamobs:textures/mobs/pig/muddy_pig/muddy_pig.png");
        ResourceLocation textureBlink = new ResourceLocation("earthtojavamobs:textures/mobs/pig/muddy_pig/muddy_pig_blink.png");
        ResourceLocation textureDried = new ResourceLocation("earthtojavamobs:textures/mobs/pig/muddy_pig/muddy_pig_dried.png");
        ResourceLocation textureDriedBlink = new ResourceLocation("earthtojavamobs:textures/mobs/pig/muddy_pig/muddy_pig_dried_blink.png");
        boolean blink = entity.getBlinkRemainingTicks() > 0;
        return entity.isInMuddyState() ?
                blink ?
                        textureBlink :
                        texture :
                blink ?
                        textureDriedBlink :
                        textureDried;
    }

    public static class SaddleLayer extends LayerRenderer<MuddyPigEntity, MuddyPigModel<MuddyPigEntity>> {
        private final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/pig/pig_saddle.png");
        private final PigModel<MuddyPigEntity> pigModel = new PigModel<>(0.5F);

        public SaddleLayer(IEntityRenderer<MuddyPigEntity, MuddyPigModel<MuddyPigEntity>> p_i50927_1_) {
            super(p_i50927_1_);
        }

        public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, MuddyPigEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (entitylivingbaseIn.getSaddled()) {
                this.getEntityModel().copyModelAttributesTo(this.pigModel);
                this.pigModel.setLivingAnimations(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks);
                this.pigModel.setRotationAngles(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
                IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderType.getEntityCutoutNoCull(TEXTURE));
                this.pigModel.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
    }

}
