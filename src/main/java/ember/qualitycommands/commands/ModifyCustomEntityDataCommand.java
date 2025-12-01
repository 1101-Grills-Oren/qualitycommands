package ember.qualitycommands.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.command.argument.EntityArgumentType;
//import net.minecraft.command.argument.RegistryEntryReferenceArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import ember.qualitycommands.util.EntityAccessor;
import ember.qualitycommands.util.NbtComponentAccessor;
import net.minecraft.component.type.NbtComponent;
import ember.qualitycommands.packets.CustomEntityDataS2CPacket;
import ember.qualitycommands.packets.CustomEntityDataS2CPacketPayload;
import java.util.List;
import net.minecraft.server.network.ServerPlayerEntity;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.server.world.ServerWorld;

import ember.qualitycommands.packets.CustomEntityStringDataS2CPacketPayload;
public class ModifyCustomEntityDataCommand {
	private static final SimpleCommandExceptionType INVULNERABLE_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.damage.invulnerable"));

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("custom_attribute")
				.requires(source -> source.hasPermissionLevel(2))
				.then(
					CommandManager.argument("target", EntityArgumentType.entity())
						.then(
							CommandManager.literal("reset")
                                        .executes(
                                            context -> executeReset(
                                                context.getSource(),
                                                EntityArgumentType.getEntity(context, "target")
                                            )
                                        )
							
                            
						)
						.then(
							CommandManager.literal("horizontal_collision_speed_multiplier_override").then(
							CommandManager.argument("value", FloatArgumentType.floatArg())
						    
                                        .executes(
                                            context -> execute(
                                                context.getSource(),
                                                EntityArgumentType.getEntity(context, "target"),
												"horizontal_collision_speed_multiplier_override",
                                                FloatArgumentType.getFloat(context,"value")
                                            )
                                        )
							)
                            
						)
						.then(
							CommandManager.literal("land_speed_multiplier_override").then(
							CommandManager.argument("value", FloatArgumentType.floatArg())
						    
                                        .executes(
                                            context -> execute( 
                                                context.getSource(),
                                                EntityArgumentType.getEntity(context, "target"),
												"land_speed_multiplier_override",
                                                FloatArgumentType.getFloat(context,"value")
                                            )
                                        )
							)
                            
						).then(
							CommandManager.literal("width_override").then(
							CommandManager.argument("value", FloatArgumentType.floatArg())
						    
                                        .executes(
                                            context -> execute( 
                                                context.getSource(),
                                                EntityArgumentType.getEntity(context, "target"),
												"width_override",
                                                FloatArgumentType.getFloat(context,"value")
                                            )
                                        )
							)
                            
						).then(
							CommandManager.literal("height_override").then(
							CommandManager.argument("value", FloatArgumentType.floatArg())
						    
                                        .executes(
                                            context -> execute( 
                                                context.getSource(),
                                                EntityArgumentType.getEntity(context, "target"),
												"height_override",
                                                FloatArgumentType.getFloat(context,"value")
                                            )
                                        )
							)
                            
						).then(
							CommandManager.literal("model_override").then(
							CommandManager.argument("value", StringArgumentType.string())
						    
                                        .executes(
                                            context -> executeString( 
                                                context.getSource(),
                                                EntityArgumentType.getEntity(context, "target"),
												"model_override",
                                                StringArgumentType.getString(context,"value")
                                            )
                                        )
							)
                            
						)
				)
		);
	}

	private static int execute(CommandSource source, Entity target,String targetS, float amount) throws CommandSyntaxException {
		NbtComponent n=((EntityAccessor)target).getCustomData();
		((NbtComponentAccessor)(Object)n).getNbt().putDouble(targetS,amount);
		for (ServerPlayerEntity player : PlayerLookup.tracking(target)) {
			ServerPlayNetworking.send(player, new CustomEntityDataS2CPacketPayload(target.getId(),List.of(new CustomEntityDataS2CPacket.Entry(targetS,amount))));
			ember.qualitycommands.QualityCommands.LOGGER.info("Packet Sent!");
        }
		if(target instanceof PlayerEntity player){
			ServerPlayNetworking.send((ServerPlayerEntity)player, new CustomEntityDataS2CPacketPayload(target.getId(),List.of(new CustomEntityDataS2CPacket.Entry(targetS,amount))));
			ember.qualitycommands.QualityCommands.LOGGER.info("Packet Sent! (to owner)");
		}
		//if (target.damage(source.getWorld(), damageSource, amount)) {
			//source.sendFeedback(() -> Text.translatable("commands.damage.success", amount, target.getDisplayName()), true);
			//return 1;
		//} else {
		//	throw INVULNERABLE_EXCEPTION.create();
		//}
		return 1;
	}
	private static int executeString(CommandSource source, Entity target,String targetS, String amount) throws CommandSyntaxException {
		NbtComponent n=((EntityAccessor)target).getCustomData();
		((NbtComponentAccessor)(Object)n).getNbt().putString(targetS,amount);
		for (ServerPlayerEntity player : PlayerLookup.tracking(target)) {
			ServerPlayNetworking.send(player, new CustomEntityStringDataS2CPacketPayload(target.getId(),List.of(new CustomEntityDataS2CPacket.EntryString(targetS,amount))));
			ember.qualitycommands.QualityCommands.LOGGER.info("Packet Sent!");
        }
		if(target instanceof PlayerEntity player){
			ServerPlayNetworking.send((ServerPlayerEntity)player, new CustomEntityStringDataS2CPacketPayload(target.getId(),List.of(new CustomEntityDataS2CPacket.EntryString(targetS,amount))));
			ember.qualitycommands.QualityCommands.LOGGER.info("Packet Sent! (to owner)");
		}
		//if (target.damage(source.getWorld(), damageSource, amount)) {
			//source.sendFeedback(() -> Text.translatable("commands.damage.success", amount, target.getDisplayName()), true);
			//return 1;
		//} else {
		//	throw INVULNERABLE_EXCEPTION.create();
		//}
		return 1;
	}
	private static int executeReset(CommandSource source, Entity target) throws CommandSyntaxException {
		NbtComponent n=((EntityAccessor)target).getCustomData();
		while(((NbtComponentAccessor)(Object)n).getNbt().getKeys().size()!=0){
            ((NbtComponentAccessor)(Object)n).getNbt().remove((String)((NbtComponentAccessor)(Object)n).getNbt().getKeys().toArray()[0]);
        }
		
		//if (target.damage(source.getWorld(), damageSource, amount)) {
			//source.sendFeedback(() -> Text.translatable("commands.damage.success", amount, target.getDisplayName()), true);
			//return 1;
		//} else {
		//	throw INVULNERABLE_EXCEPTION.create();
		//}
		return 1;
	}
}
