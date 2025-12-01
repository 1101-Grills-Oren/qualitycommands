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

@Mixin(EntityRenderer.class)
public class EntityRendererMixin<T extends Entity, S extends EntityRenderState>{
    @Inject(method = "getAndUpdateRenderState", at = @At("RETURN"), cancellable = true)
	private void getAndUpdateRenderStateModifier(T entity, float tickProgress,CallbackInfoReturnable info) {
		EntityRenderState entityRenderState=(EntityRenderState)info.getReturnValue();
        if(((NbtComponentAccessor)(Object)((EntityAccessor)entity).getCustomData()).getNbt().getString("model_override").isPresent()){
            if(((NbtComponentAccessor)(Object)((EntityAccessor)entity).getCustomData()).getNbt().getString("model_override").get().length()!=0){
                if(Registries.ENTITY_TYPE.containsId(Identifier.of(((NbtComponentAccessor)(Object)((EntityAccessor)entity).getCustomData()).getNbt().getString("model_override").get()))){
                    EntityType<?> newType=Registries.ENTITY_TYPE.get(Identifier.of(((NbtComponentAccessor)(Object)((EntityAccessor)entity).getCustomData()).getNbt().getString("model_override").get()));
                    entityRenderState.entityType=newType;
                }
                
            }
        }

		info.setReturnValue(entityRenderState);
	}
}
