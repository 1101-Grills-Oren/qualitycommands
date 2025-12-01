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
import net.minecraft.item.BucketItem;
import net.minecraft.world.World;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import ember.qualitycommands.ModComponents;
import net.minecraft.server.function.CommandFunctionManager;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.util.Identifier;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.network.ConnectedClientData;
import ember.qualitycommands.util.EntityAccessor;
import ember.qualitycommands.util.NbtComponentAccessor;
import net.minecraft.component.type.NbtComponent;
@Mixin(PlayerManager.class)
public class PlayerManagerMixin{
    @Inject(method = "remove", at=@At("HEAD"))
    private static void removeInject(ServerPlayerEntity player,CallbackInfo info) {
        //player.getEntityWorld().getCommandFunctionManager().getTag(Identifier.of("quality_commands","on_before_player_leave"));
        //player.getEntityWorld().getCommandFunctionManager().executeAll(player.getEntityWorld().getCommandFunctionManager().getTag(Identifier.of("quality_commands","on_before_player_leave")),Identifier.of("quality_commands","on_before_player_leave"));
        if(((ember.qualitycommands.util.MinecraftServerAccessor)player.getEntityWorld().getServer()).getCommandFunctionManager().getTag(Identifier.of("quality_commands","on_before_player_leave"))!=null){
        for(CommandFunction<ServerCommandSource> function:((ember.qualitycommands.util.MinecraftServerAccessor)player.getEntityWorld().getServer()).getCommandFunctionManager().getTag(Identifier.of("quality_commands","on_before_player_leave"))){
            ((ember.qualitycommands.util.MinecraftServerAccessor)player.getEntityWorld().getServer()).getCommandFunctionManager().execute(function,player.getEntityWorld().getServer().getCommandSource().withEntity(player).withPosition(player.getEntityPos()).withSilent());
        }
        }
        while(((NbtComponentAccessor)(Object)((EntityAccessor)player).getCustomData()).getNbt().getKeys().size()!=0){

            ((NbtComponentAccessor)(Object)((EntityAccessor)player).getCustomData()).getNbt().remove((String)((NbtComponentAccessor)(Object)((EntityAccessor)player).getCustomData()).getNbt().getKeys().toArray()[0]);
        }
        
        
    }
    @Inject(method = "onPlayerConnect", at=@At("TAIL"))
    private static void playerConnectInject(ClientConnection connection, ServerPlayerEntity player,ConnectedClientData clientData,CallbackInfo info) {
        //player.getEntityWorld().getCommandFunctionManager().getTag(Identifier.of("quality_commands","on_before_player_leave"));
        //player.getEntityWorld().getCommandFunctionManager().executeAll(player.getEntityWorld().getCommandFunctionManager().getTag(Identifier.of("quality_commands","on_before_player_leave")),Identifier.of("quality_commands","on_before_player_leave"));
        if(((ember.qualitycommands.util.MinecraftServerAccessor)player.getEntityWorld().getServer()).getCommandFunctionManager().getTag(Identifier.of("quality_commands","on_before_player_join"))!=null){
            for(CommandFunction<ServerCommandSource> function:((ember.qualitycommands.util.MinecraftServerAccessor)player.getEntityWorld().getServer()).getCommandFunctionManager().getTag(Identifier.of("quality_commands","on_before_player_join"))){
                ((ember.qualitycommands.util.MinecraftServerAccessor)player.getEntityWorld().getServer()).getCommandFunctionManager().execute(function,player.getEntityWorld().getServer().getCommandSource().withEntity(player).withPosition(player.getEntityPos()).withSilent());
            }
        }
    }
}
