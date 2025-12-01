package ember.qualitycommands;
//import jdk.javadoc.internal.doclets.formats.html.markup.Text;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.minecraft.text.Text;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import ember.qualitycommands.ModBlocks;
import net.minecraft.client.render.BlockRenderLayer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.block.RedstoneWireBlock;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.network.packet.CustomPayload;
import ember.qualitycommands.packets.CustomEntityStringDataS2CPacketPayload;
import ember.qualitycommands.packets.CustomEntityDataS2CPacketPayload;
import ember.qualitycommands.packets.CustomEntityDataS2CPacket;
import ember.qualitycommands.util.NbtComponentAccessor;
import ember.qualitycommands.util.EntityAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.registry.Registries;
/*import ember.qualitycommands.blocks.AbstractColoredRedstoneWireBlock;
import ember.qualitycommands.blocks.RedRedstoneWireBlock;
import ember.qualitycommands.blocks.GreenRedstoneWireBlock;
import ember.qualitycommands.blocks.BlueRedstoneWireBlock;*/
import net.minecraft.block.Blocks;
@Environment(EnvType.CLIENT)
public class QualityCommandsClient implements ClientModInitializer{
    /*public void onUpdateCustomData(CustomEntityDataS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, (ClientPlayNetworkHandler)(Object)this, ((ClientPlayNetworkHandler)(Object)this).client.getPacketApplyBatcher());
		Entity entity = this.world.getEntityById(packet.getEntityId());
		if (entity != null) {
            NbtComponent n=((EntityAccessor)entity).getCustomData();
            for (ember.qualitycommands.packets.CustomEntityDataS2CPacket.Entry entry : packet.getEntries()) {
                ((NbtComponentAccessor)(Object)n).getNbt().putDouble(entry.key(),entry.value());
            }
		}
	}*/
    public void onUpdateCustomData(CustomEntityDataS2CPacketPayload packet) {
		Entity entity = MinecraftClient.getInstance().world.getEntityById(packet.entityid());
		if (entity != null) {
            NbtComponent n=((EntityAccessor)entity).getCustomData();
            for (ember.qualitycommands.packets.CustomEntityDataS2CPacket.Entry entry : packet.entries()) {
                ((NbtComponentAccessor)(Object)n).getNbt().putDouble(entry.key(),entry.value());
            }
		}
	}
    public void onUpdateCustomData(CustomEntityStringDataS2CPacketPayload packet) {
		Entity entity = MinecraftClient.getInstance().world.getEntityById(packet.entityid());
		if (entity != null) {
            NbtComponent n=((EntityAccessor)entity).getCustomData();
            for (ember.qualitycommands.packets.CustomEntityDataS2CPacket.EntryString entry : packet.entries()) {
                ((NbtComponentAccessor)(Object)n).getNbt().putString(entry.key(),entry.value());
            }
		}
	}
    		// In your client-only initializer method
		
    /*static float dashSpeed=1;
    public static float getDashSpeed(){
        return QualityCommandsClient.dashSpeed;
    }
    public static void setDashSpeed(float value){
        QualityCommandsClient.dashSpeed=value;
    }
    private static KeyBinding keyBindingA=KeyBindingHelper.registerKeyBinding(new KeyBinding(
        "key.qualitycommands.dash",
        InputUtil.Type.KEYSYM,
        InputUtil.GLFW_KEY_O,
        "category.qualitycommands.test"
    ));
    private static KeyBinding keyBindingB=KeyBindingHelper.registerKeyBinding(new KeyBinding(
        "key.qualitycommands.dashplus",
        InputUtil.Type.KEYSYM,
        InputUtil.GLFW_KEY_C,
        "category.qualitycommands.test"
    ));
    private static KeyBinding keyBindingC=KeyBindingHelper.registerKeyBinding(new KeyBinding(
        "key.qualitycommands.dashminus",
        InputUtil.Type.KEYSYM,
        InputUtil.GLFW_KEY_V,
        "category.qualitycommands.test"
    ));*/
    @Override
    public void onInitializeClient(){
        ClientPlayNetworking.registerGlobalReceiver(CustomEntityDataS2CPacketPayload.ID, (payload, context) -> {
			context.client().execute(() -> {
				this.onUpdateCustomData(payload);
                QualityCommands.LOGGER.info("Packet Recieved!");
			});
		});
        ClientPlayNetworking.registerGlobalReceiver(CustomEntityStringDataS2CPacketPayload.ID, (payload, context) -> {
			context.client().execute(() -> {
				this.onUpdateCustomData(payload);
                QualityCommands.LOGGER.info("Packet Recieved!");
			});
		});
        /*ColorProviderRegistry.BLOCK.register(
			(state, world, pos, tintIndex) -> RedRedstoneWireBlock.getWireColor((Integer)state.get(RedstoneWireBlock.POWER)), ModBlocks.REDSTONE_WIRE_COLORED.get(0)
		);
        ColorProviderRegistry.BLOCK.register(
			(state, world, pos, tintIndex) -> GreenRedstoneWireBlock.getWireColor((Integer)state.get(RedstoneWireBlock.POWER)), ModBlocks.REDSTONE_WIRE_COLORED.get(1)
		);
        ColorProviderRegistry.BLOCK.register(
			(state, world, pos, tintIndex) -> BlueRedstoneWireBlock.getWireColor((Integer)state.get(RedstoneWireBlock.POWER)), ModBlocks.REDSTONE_WIRE_COLORED.get(2)
		);
        // To make some parts of the block transparent (like glass, saplings and doors):
        BlockRenderLayerMap.putBlock(ModBlocks.REDSTONE_WIRE_COLORED.get(0), BlockRenderLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(ModBlocks.REDSTONE_WIRE_COLORED.get(1), BlockRenderLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(ModBlocks.REDSTONE_WIRE_COLORED.get(2), BlockRenderLayer.CUTOUT);*/
 
        // To make some parts of the block translucent (like ice, stained glass and portal)
        //BlockRenderLayerMap.putBlock(TutorialBlocks.MY_BLOCK, BlockRenderLayer.TRANSLUCENT);
        /*ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyBindingA.wasPressed()){
                //client.player.sendMessage(Text.literal("Key was pressed"),false);
                PacketByteBuf buf=PacketByteBufs.create();
                buf.writeString("execute positioned 0 0 0 positioned ^ ^ ^%f run setVel @s ~-0.5 ~0.0 ~-0.5".formatted((QualityCommandsClient.getDashSpeed())));
                ClientPlayNetworking.send(CommandTriggerPacket.PACKET_ID, buf);
            }//execute positioned 0 0 0 positioned ^ ^ ^1 run accelerate @s ~-0.5 ~0.0 ~-0.5
        });
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyBindingB.wasPressed()){
                setDashSpeed(getDashSpeed()-0.1F);
                client.player.sendMessage(Text.literal("Dash Strength %f".formatted(QualityCommandsClient.getDashSpeed())),false);
            }//execute positioned 0 0 0 positioned ^ ^ ^1 run accelerate @s ~-0.5 ~0.0 ~-0.5
        });
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyBindingC.wasPressed()){
                setDashSpeed(getDashSpeed()+0.1F);
                client.player.sendMessage(Text.literal("Dash Strength %f".formatted(QualityCommandsClient.getDashSpeed())),false);
            }//execute positioned 0 0 0 positioned ^ ^ ^1 run accelerate @s ~-0.5 ~0.0 ~-0.5
        });*/
    }
}