package ember.qualitycommands.mixin;
import com.google.common.collect.Lists;
import net.minecraft.util.math.MathHelper;
import java.util.List;
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
import net.minecraft.registry.tag.FluidTags;
import org.jetbrains.annotations.Nullable;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ExecuteCommand;
import net.minecraft.command.CommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.server.command.CommandManager;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.world.World;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import ember.qualitycommands.ModComponents;
import ember.qualitycommands.QualityCommands;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Overwrite;
import net.minecraft.entity.Entity;
import net.minecraft.component.type.NbtComponent;
import org.spongepowered.asm.mixin.injection.Redirect;
import net.minecraft.block.Block;
import net.minecraft.world.BlockView;
import com.llamalad7.mixinextras.sugar.Local;
import ember.qualitycommands.util.NbtComponentAccessor;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.util.math.Box;
@Mixin(Entity.class)
public class EntityMixin implements ember.qualitycommands.util.EntityAccessor{
    @Shadow
    private NbtComponent customData;
    @ModifyConstant(constant=@Constant(doubleValue=3.0E7),method="updatePosition")
    private static double TDIOA(double x){
        return QualityCommands.maxWorldSize;
    }
    @ModifyConstant(constant=@Constant(doubleValue=-3.0E7),method="updatePosition")
    private static double TDIOB(double x){
        return -QualityCommands.maxWorldSize;
    }
    @Redirect(method = "move",
              at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;onEntityLand(Lnet/minecraft/world/BlockView;Lnet/minecraft/entity/Entity;)V"))
    private void moveOnEntityLandOverride(Block block, BlockView view,Entity entity){
        if(((NbtComponentAccessor)(Object)this.customData).getNbt().getDouble("land_speed_multiplier_override").isPresent()){
            if(((NbtComponentAccessor)(Object)this.customData).getNbt().getDouble("land_speed_multiplier_override").get()!=0.0){
                entity.setVelocity(entity.getVelocity().multiply(1.0, ((NbtComponentAccessor)(Object)this.customData).getNbt().getDouble("land_speed_multiplier_override").get(), 1.0));
            }else{
                block.onEntityLand(view,entity);
            }
        }else{
                block.onEntityLand(view,entity);
            }
    }
    @Redirect(method = "move",
              at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setVelocity(DDD)V"))
    private void moveOnEntityLandWallOverride(Entity entity,double x,double y,double z, @Local(ordinal=0) boolean bl, @Local(ordinal=1) boolean bl2, @Local(ordinal=2) Vec3d vec3d4){
        if(((NbtComponentAccessor)(Object)this.customData).getNbt().getDouble("horizontal_collision_speed_multiplier_override").isPresent()){
            double d=((NbtComponentAccessor)(Object)this.customData).getNbt().getDouble("horizontal_collision_speed_multiplier_override").get();
            if(d!=0.0){
                entity.setVelocity(bl ? vec3d4.x*d : vec3d4.x, vec3d4.y, bl2 ? vec3d4.z*d : vec3d4.z);
            }else{
                entity.setVelocity(x,y,z);
            }
        }else{
                entity.setVelocity(x,y,z);
            }
    }
    
    @Inject(method="getWidth",at=@At("HEAD"),cancellable=true)
    private void getWidthOverride(CallbackInfoReturnable info){
        if(((NbtComponentAccessor)(Object)this.customData).getNbt().getDouble("width_override").isPresent()){
            if(((NbtComponentAccessor)(Object)this.customData).getNbt().getDouble("width_override").get()>0.0){
                info.setReturnValue((Float)(float)(double)
                ((NbtComponentAccessor)(Object)this.customData).getNbt().getDouble("width_override").get()
                );
            }
        }
    }
    @Inject(method="getDimensions",at=@At("RETURN"),cancellable=true)
    private void getDimensionsModification(CallbackInfoReturnable info){
        float width_override=((EntityDimensions)info.getReturnValue()).width();
        float old_width=width_override;

        if(((NbtComponentAccessor)(Object)this.customData).getNbt().getDouble("width_override").isPresent()){
            if(((NbtComponentAccessor)(Object)this.customData).getNbt().getDouble("width_override").get()>0.0){
                width_override=((float)(double)
                ((NbtComponentAccessor)(Object)this.customData).getNbt().getDouble("width_override").get()
                );
            }
        }
        info.setReturnValue(((EntityDimensions)info.getReturnValue()).scaled(width_override/old_width,1.0F));
    }
    @Shadow
    private Box boundingBox;
    @Inject(method="setBoundingBox",at=@At("TAIL"))
    private void getBoundingBoxModification(CallbackInfo info){
        Box box=((Box)this.boundingBox);
        double old_width=box.maxX-box.minX;
        double center_x=(box.maxX+box.minX)/2;
        double center_z=(box.maxZ+box.minZ)/2;
        double new_width=old_width;

        if(((NbtComponentAccessor)(Object)this.customData).getNbt().getDouble("width_override").isPresent()){
            if(((NbtComponentAccessor)(Object)this.customData).getNbt().getDouble("width_override").get()>0.0){
                new_width=(double)
                ((NbtComponentAccessor)(Object)this.customData).getNbt().getDouble("width_override").get();
            }
        }
        box=box.withMaxX(center_x+new_width/2);
        box=box.withMinX(center_x-new_width/2);
        box=box.withMaxZ(center_z+new_width/2);
        box=box.withMinZ(center_z-new_width/2);
        this.boundingBox=box;
        //info.setReturnValue(box);
    }
    @Override
    public net.minecraft.component.type.NbtComponent getCustomData(){
        if(this.customData==NbtComponent.DEFAULT){
        this.customData= NbtComponent.of(((NbtComponentAccessor)(Object)this.customData).getNbt().copy());
        QualityCommands.LOGGER.info("Default Custom Data detected.");
        }
        return this.customData;
    };
    
}

