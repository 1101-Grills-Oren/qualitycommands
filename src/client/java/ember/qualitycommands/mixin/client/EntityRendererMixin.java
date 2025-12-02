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
@Mixin(EntityRenderer.class)
public class EntityRendererMixin<T extends Entity, S extends EntityRenderState>{
    @Inject(method = "getAndUpdateRenderState", at = @At("RETURN"), cancellable = true)
	private void getAndUpdateRenderStateModifier(T entity, float tickProgress,CallbackInfoReturnable info) {
		EntityRenderState entityRenderState=(EntityRenderState)info.getReturnValue();
        if(((NbtComponentAccessor)(Object)((EntityAccessor)entity).getCustomData()).getNbt().getString("model_override").isPresent()){
            if(((NbtComponentAccessor)(Object)((EntityAccessor)entity).getCustomData()).getNbt().getString("model_override").get().length()!=0){
                if(Registries.ENTITY_TYPE.containsId(Identifier.of(((NbtComponentAccessor)(Object)((EntityAccessor)entity).getCustomData()).getNbt().getString("model_override").get()))){
                    if(((EntityAccessor)entity).getCurrentIdentity()!=null){
                //Sync identity to entity
                Entity identity=((EntityAccessor)entity).getCurrentIdentity();
                EntityRenderer renderer=((MinecraftClientAccessor)MinecraftClient.getInstance()).getEntityRenderManager().getRenderer(identity);
                EntityRenderer currentRenderer=((MinecraftClientAccessor)MinecraftClient.getInstance()).getEntityRenderManager().getRenderer(entity);
                EntityHitboxAndView oldHitbox=entityRenderState.hitbox;
                {
            //living only:
            
            identity.setPos(
                ((Entity)entity).getEntityPos().x,
                ((Entity)entity).getEntityPos().y,
                ((Entity)entity).getEntityPos().z
            );
            if(identity instanceof EnderDragonEntity dragonIdentity){
                identity.setYaw(entity.getYaw()+189);
            }else{
                identity.setYaw(entity.getYaw());
            }
            ((EntityAccessor)identity).setLastPosition(
                ((Entity)entity).getLastRenderPos()
            );
            if((identity instanceof LivingEntity livingIdentity)&&(entity instanceof LivingEntity livingEntity)){
            LimbAnimatorAccessor target = (LimbAnimatorAccessor) livingIdentity.limbAnimator;
            LimbAnimatorAccessor source = (LimbAnimatorAccessor) livingEntity.limbAnimator;

            target.setPrevSpeed(source.getPrevSpeed());
            target.setSpeed(source.getSpeed());
            //target.setPos(source.getPos());

            livingIdentity.handSwinging = livingEntity.handSwinging;//LivingEntity only
            livingIdentity.handSwingTicks = livingEntity.handSwingTicks;//living only
            livingIdentity.lastHandSwingProgress = livingEntity.lastHandSwingProgress;//living only
            livingIdentity.handSwingProgress = livingEntity.handSwingProgress;//living only
            livingIdentity.bodyYaw = livingEntity.bodyYaw;//living only
            livingIdentity.lastBodyYaw = livingEntity.lastBodyYaw;//living only
            livingIdentity.headYaw = livingEntity.headYaw;//living only
            livingIdentity.lastHeadYaw = livingEntity.lastHeadYaw;//living only
            livingIdentity.preferredHand = livingEntity.preferredHand;//livingonly
            livingIdentity.setCurrentHand(livingEntity.getActiveHand());//living only
            }
            identity.age = ((Entity)entity).age;//all
            identity.setOnGround(((Entity)entity).isOnGround());//all entities
            identity.setVelocity(((Entity)entity).getVelocity());//all entities
            identity.setSneaking(((Entity)entity).isSneaking());//all entities
            identity.setSprinting(((Entity)entity).isSprinting());//all entities
            identity.setSwimming(((Entity)entity).isSwimming());//all entities
            identity.setPose(((Entity)entity).getPose());//all entities

            ((EntityAccessor) identity).setVehicle(((Entity)entity).getVehicle());
            ((EntityAccessor) identity).setTouchingWater(((Entity)entity).isTouchingWater());

            if (identity instanceof PhantomEntity) {
                identity.setPitch(-((Entity)entity).getPitch());
                identity.lastPitch = -((Entity)entity).lastPitch;//used to be prevPitch
            } else {
                identity.setPitch(((Entity)entity).getPitch());
                identity.lastPitch = ((Entity)entity).lastPitch;
            }
            //living only
            if((entity instanceof LivingEntity livingEntity)&&(identity instanceof LivingEntity livingIdentity)){
                //if (IdentityConfig.getInstance().identitiesEquipItems()) {
                    livingIdentity.equipStack(EquipmentSlot.MAINHAND, livingEntity.getEquippedStack(EquipmentSlot.MAINHAND));
                    livingIdentity.equipStack(EquipmentSlot.OFFHAND, livingEntity.getEquippedStack(EquipmentSlot.OFFHAND));
                //}

                //if (IdentityConfig.getInstance().identitiesEquipArmor()) {
                    livingIdentity.equipStack(EquipmentSlot.HEAD, livingEntity.getEquippedStack(EquipmentSlot.HEAD));
                    livingIdentity.equipStack(EquipmentSlot.CHEST, livingEntity.getEquippedStack(EquipmentSlot.CHEST));
                    livingIdentity.equipStack(EquipmentSlot.LEGS, livingEntity.getEquippedStack(EquipmentSlot.LEGS));
                    livingIdentity.equipStack(EquipmentSlot.FEET, livingEntity.getEquippedStack(EquipmentSlot.FEET));
                //}
            }

            if(entity instanceof LivingEntity){
                if (identity instanceof MobEntity) {
                    ((MobEntity) identity).setAttacking(((LivingEntity)entity).isUsingItem());
                }
            }

            /*identity.setPose(entity.getPose());

            identity.setCurrentHand(entity.getActiveHand() == null ? Hand.MAIN_HAND : entity.getActiveHand());
            ((LivingEntityCompatAccessor) identity).callSetLivingFlag(1, entity.isUsingItem());
            identity.getItemUseTime();
            ((LivingEntityCompatAccessor) identity).callTickActiveItemStack();*/

            /*EntityUpdater updater = EntityUpdaters.getUpdater((EntityType<? extends LivingEntity>) identity.getType());
            if (updater != null) {
                updater.update(player, identity);
            }*/



                }
                entityRenderState=renderer.createRenderState();


                renderer.updateRenderState(((EntityAccessor)entity).getCurrentIdentity(),entityRenderState,tickProgress);
                entityRenderState.hitbox=oldHitbox;
                        //currentRenderer.updateRenderState(entity,entityRenderState,tickProgress);
                    }
                }
            }
        }
		info.setReturnValue(entityRenderState);
	}
}
