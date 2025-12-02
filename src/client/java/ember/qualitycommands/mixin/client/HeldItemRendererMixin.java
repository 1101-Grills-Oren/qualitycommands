package ember.qualitycommands.mixin.client;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.util.math.MathHelper;
import java.util.List;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.block.AirBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.MovementType;
import ember.qualitycommands.ModEffects;
import net.minecraft.item.Items;
import net.minecraft.item.Item;
import java.util.Set;
import ember.qualitycommands.ModBlocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.registry.Registries;
import ember.qualitycommands.util.EntityAccessor;
import ember.qualitycommands.util.NbtComponentAccessor;
import net.minecraft.util.Identifier;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.MinecraftClient;
import ember.qualitycommands.util.MinecraftClientAccessor;
import ember.qualitycommands.util.LimbAnimatorAccessor;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.client.render.entity.state.EntityHitboxAndView;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
@Mixin(EntityRenderer.class)


public class EntityRendererMixin<T extends Entity, S extends EntityRenderState>{
    @Redirect(method = "renderArmHoldingItem",
              at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/PlayerEntityRenderer;renderRightArm(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;ILnet/minecraft/util/Identifier;Z)V"))
    private void renderRightArmOverride(PlayerEntityRenderer renderer, MatrixStack matrices, OrderedRenderCommandQueue queue, int light, Identifier skinTexture, boolean sleeveVisible) {
        EntityAccessor playerEntity=MinecraftClient.getInstance().player;
		renderer.renderArm(matrices, queue, light, skinTexture, renderer.getModel().rightArm, sleeveVisible);
        
        Entity identity=playerEntity.getCurrentIdentity();
        EntityRenderer renderer=((MinecraftClientAccessor)MinecraftClient.getInstance()).getEntityRenderManager().getRenderer(identity);
        if(renderer instanceof LivingEntityRenderer livingRenderer){
        EntityModel eModel=renderer.getModel();
        ModelPart targetPart=null;
        try {
            targetPart=eModel.getClass().getDeclaredField("rightArm").get(eModel);
        } catch (NoSuchFieldException e) {
            try {
                targetPart=eModel.getClass().getDeclaredField("rightFrontLeg").get(eModel);
            } catch (NoSuchFieldException f) {
                
            }
        }
        //playerEntityRenderer.renderRightArm(matrices, queue, light, identifier, abstractClientPlayerEntity.isModelPartVisible(PlayerModelPart.RIGHT_SLEEVE));
		



        
        if(((NbtComponentAccessor)(Object)this.getCustomData()).getNbt().getString("model_override").isPresent()){
            double d=((NbtComponentAccessor)(Object)this.getCustomData()).getNbt().getString("model_override").get();
            if(d.length!=0){
                if(targetPart!=null){
                    renderer.renderArm(matrices, queue, light, livingRenderer.getTexture(), targetPart, sleeveVisible);                    
                }
            }else{
                renderer.renderArm(matrices, queue, light, skinTexture, livingRenderer.getModel().rightArm, sleeveVisible);
            }
        }else{
            renderer.renderArm(matrices, queue, light, skinTexture, livingRenderer.getModel().rightArm, sleeveVisible);
        }
        }
    }
}
