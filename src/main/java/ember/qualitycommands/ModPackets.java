package ember.qualitycommands;

import net.minecraft.item.Item;
import net.minecraft.registry.Registry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import ember.qualitycommands.commands.TpRelCommand;
//import ember.qualitycommands.commands.SilentFunctionCommand;
import ember.qualitycommands.commands.AccelerateCommand;
import ember.qualitycommands.commands.AccelerateToPosCommand;
import ember.qualitycommands.commands.AccelerateAltCommand;
import ember.qualitycommands.commands.ConvertToEntityCommand;
import ember.qualitycommands.commands.ForLoopCommand;
import ember.qualitycommands.commands.HealCommand;
import ember.qualitycommands.commands.RunMultipleCommand;
import ember.qualitycommands.commands.WithCommand;
import ember.qualitycommands.commands.AirCommand;
import ember.qualitycommands.commands.ModifyCustomEntityDataCommand;
import ember.qualitycommands.packets.CustomEntityDataS2CPacket;
import net.minecraft.server.command.CommandManager;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import ember.qualitycommands.packets.CustomEntityDataS2CPacketPayload;
import ember.qualitycommands.packets.CustomEntityStringDataS2CPacketPayload;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
public class ModPackets{
	public static void initialize(){
		PayloadTypeRegistry.playS2C().register(CustomEntityDataS2CPacketPayload.ID, CustomEntityDataS2CPacketPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(CustomEntityStringDataS2CPacketPayload.ID, CustomEntityStringDataS2CPacketPayload.CODEC);
	}
	//public static final PacketType<CustomEntityDataS2CPacket> SET_ENTITY_DOUBLE_DATA = s2c("set_custom_data_double");

	private static <T extends Packet<ClientPlayPacketListener>> PacketType<T> s2c(String id) {
		return new PacketType<>(NetworkSide.CLIENTBOUND, Identifier.of(QualityCommands.MOD_ID,id));
	}

	private static <T extends Packet<ServerPlayPacketListener>> PacketType<T> c2s(String id) {
		return new PacketType<>(NetworkSide.SERVERBOUND, Identifier.of(QualityCommands.MOD_ID,id));
	}
	public static final Identifier CUSTOM_STRING_DATA_ID=Identifier.of(QualityCommands.MOD_ID,"set_custom_data_string");
	public static final Identifier CUSTOM_DOUBLE_DATA_ID=Identifier.of(QualityCommands.MOD_ID,"set_custom_data_double");
}