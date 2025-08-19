package ember.qualitycommands;

import net.minecraft.item.Item;
import net.minecraft.registry.Registry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import ember.qualitycommands.commands.TpRelCommand;
import ember.qualitycommands.commands.SilentFunctionCommand;
import net.minecraft.server.command.CommandManager;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModCommands{
	public static void initialize(){
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			TpRelCommand.register(dispatcher);
			SilentFunctionCommand.register(dispatcher);
		});
	}
}