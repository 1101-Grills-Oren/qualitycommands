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
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.Entity;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.registry.Registries;
import java.util.ArrayList;
import ember.qualitycommands.util.EntityAccessor;
import ember.qualitycommands.util.EnderDragonEntityAccessor;
import java.util.function.Function;
import java.util.function.BiFunction;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
/*import ember.qualitycommands.blocks.AbstractColoredRedstoneWireBlock;
import ember.qualitycommands.blocks.RedRedstoneWireBlock;
import ember.qualitycommands.blocks.GreenRedstoneWireBlock;
import ember.qualitycommands.blocks.BlueRedstoneWireBlock;*/
import net.minecraft.util.Identifier;
import net.minecraft.block.Blocks;
@Environment(EnvType.CLIENT)
public class QualityCommandsClient implements ClientModInitializer{
    /*public void onUpdateCustomData(CustomEntityDataS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, (ClientPlayNetworkHandler)(Object)entity, ((ClientPlayNetworkHandler)(Object)entity).client.getPacketApplyBatcher());
		Entity entity = entity.world.getEntityById(packet.getEntityId());
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
		}else{
            QualityCommands.LOGGER.info("Entity null.");
        }
	}
    public void onUpdateCustomData(CustomEntityStringDataS2CPacketPayload packet) {
		Entity entity = MinecraftClient.getInstance().world.getEntityById(packet.entityid());
		if (entity != null) {
            NbtComponent n=((EntityAccessor)entity).getCustomData();
            for (ember.qualitycommands.packets.CustomEntityDataS2CPacket.EntryString entry : packet.entries()) {
                ((NbtComponentAccessor)(Object)n).getNbt().putString(entry.key(),entry.value());
                if(entry.key().matches("model_override")){
                    ((EntityAccessor)entity).setCurrentIdentity(entry.value());
                }
            }
            
		}else{
            QualityCommands.LOGGER.info("Entity null.");
        }
	}
    public static ArrayList<BiFunction<Entity,Entity,Entity>> visualPatchValues=new ArrayList(0);
    public static ArrayList<Identifier> visualPatchKeys=new ArrayList(0);
    public static void addVisualPatch(BiFunction<Entity,Entity,Entity> value,Identifier id){
        visualPatchKeys.ensureCapacity(visualPatchKeys.size()+1);
        visualPatchValues.ensureCapacity(visualPatchValues.size()+1);
        visualPatchKeys.add(id);
        visualPatchValues.add(value);
    }
    static{
	addVisualPatch((identity,entity)->{
		if(identity instanceof EnderDragonEntity dragonIdentity){
                dragonIdentity.yawAcceleration+=MathHelper.wrapDegrees(entity.getYaw()-identity.getYaw())*0.1F;
            }
			return identity;
	},Identifier.of("minecraft:ender_dragon"));
	/*
	
    addVisualPatch((e)->{
            EnderDragonEntity entity=(EnderDragonEntity)e;
            ((EntityAccessor)entity).runAddAirTravelEffects();
		if (entity.getEntityWorld().isClient()) {
			entity.setHealth(entity.getHealth());
			if (!entity.isSilent() && !entity.getPhaseManager().getCurrent().isSittingOrHovering() && ((EnderDragonEntityAccessor)entity).setTicksUntilNextGrowl(((EnderDragonEntityAccessor)entity).getTicksUntilNextGrowl()-1) < 0) {
				entity.getEntityWorld()
					.playSoundClient(
						entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ENTITY_ENDER_DRAGON_GROWL, entity.getSoundCategory(), 2.5F, 0.8F + entity.getRandom().nextFloat() * 0.3F, false
					);
				((EnderDragonEntityAccessor)entity).setTicksUntilNextGrowl(200 + entity.getRandom().nextInt(200));
			}
		}

		/*if (entity.fight == null && entity.getEntityWorld() instanceof ServerWorld serverWorld) {
			EnderDragonFight enderDragonFight = serverWorld.getEnderDragonFight();
			if (enderDragonFight != null && entity.getUuid().equals(enderDragonFight.getDragonUuid())) {
				entity.fight = enderDragonFight;
			}
		}*v/

		entity.lastWingPosition = entity.wingPosition;
		if (entity.isDead()) {
			float f = (entity.getRandom().nextFloat() - 0.5F) * 8.0F;
			float g = (entity.getRandom().nextFloat() - 0.5F) * 4.0F;
			float h = (entity.getRandom().nextFloat() - 0.5F) * 8.0F;
			entity.getEntityWorld().addParticleClient(ParticleTypes.EXPLOSION, entity.getX() + f, entity.getY() + 2.0 + g, entity.getZ() + h, 0.0, 0.0, 0.0);
		} else {
			((EnderDragonEntityAccessor)entity).runTickWithEndCrystals();
			Vec3d vec3d = entity.getVelocity();
			float g = 0.2F / ((float)vec3d.horizontalLength() * 10.0F + 1.0F);
			g *= (float)Math.pow(2.0, vec3d.y);
			if (entity.getPhaseManager().getCurrent().isSittingOrHovering()) {
				entity.wingPosition += 0.1F;
			} else if (entity.slowedDownByBlock) {
				entity.wingPosition += g * 0.5F;
			} else {
				entity.wingPosition += g;
			}

			entity.setYaw(MathHelper.wrapDegrees(entity.getYaw()));
        }



            this.bodyYaw = this.getYaw();
				Vec3d[] vec3ds = new Vec3d[this.parts.length];

				for (int q = 0; q < this.parts.length; q++) {
					vec3ds[q] = new Vec3d(this.parts[q].getX(), this.parts[q].getY(), this.parts[q].getZ());
				}

				float r = (float)(this.frameTracker.getFrame(5).y() - this.frameTracker.getFrame(10).y()) * 10.0F * (float) (Math.PI / 180.0);
				float s = MathHelper.cos(r);
				float t = MathHelper.sin(r);
				float u = this.getYaw() * (float) (Math.PI / 180.0);
				float v = MathHelper.sin(u);
				float w = MathHelper.cos(u);
				this.movePart(this.body, v * 0.5F, 0.0, -w * 0.5F);
				this.movePart(this.rightWing, w * 4.5F, 2.0, v * 4.5F);
				this.movePart(this.leftWing, w * -4.5F, 2.0, v * -4.5F);
				float x = MathHelper.sin(this.getYaw() * (float) (Math.PI / 180.0) - this.yawAcceleration * 0.01F);
				float y = MathHelper.cos(this.getYaw() * (float) (Math.PI / 180.0) - this.yawAcceleration * 0.01F);
				float z = this.getHeadVerticalMovement();
				this.movePart(this.head, x * 6.5F * s, z + t * 6.5F, -y * 6.5F * s);
				this.movePart(this.neck, x * 5.5F * s, z + t * 5.5F, -y * 5.5F * s);
				EnderDragonFrameTracker.Frame frame = this.frameTracker.getFrame(5);

				for (int aa = 0; aa < 3; aa++) {
					EnderDragonPart enderDragonPart = null;
					if (aa == 0) {
						enderDragonPart = this.tail1;
					}

					if (aa == 1) {
						enderDragonPart = this.tail2;
					}

					if (aa == 2) {
						enderDragonPart = this.tail3;
					}

					EnderDragonFrameTracker.Frame frame2 = this.frameTracker.getFrame(12 + aa * 2);
					float ab = this.getYaw() * (float) (Math.PI / 180.0) + this.wrapYawChange(frame2.yRot() - frame.yRot()) * (float) (Math.PI / 180.0);
					float ac = MathHelper.sin(ab);
					float mx = MathHelper.cos(ab);
					float n = 1.5F;
					float o = (aa + 1) * 2.0F;
					this.movePart(enderDragonPart, -(v * 1.5F + ac * o) * s, frame2.y() - frame.y() - (o + 1.5F) * t + 1.5, (w * 1.5F + mx * o) * s);
				}


				for (int aa = 0; aa < this.parts.length; aa++) {
					this.parts[aa].lastX = vec3ds[aa].x;
					this.parts[aa].lastY = vec3ds[aa].y;
					this.parts[aa].lastZ = vec3ds[aa].z;
					this.parts[aa].lastRenderX = vec3ds[aa].x;
					this.parts[aa].lastRenderY = vec3ds[aa].y;
					this.parts[aa].lastRenderZ = vec3ds[aa].z;
				}


            return entity;
        },
        Identifier.of("minecraft:ender_dragon"));*/
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
				onUpdateCustomData(payload);
                QualityCommands.LOGGER.info("Packet Recieved!");
			});
		});
        ClientPlayNetworking.registerGlobalReceiver(CustomEntityStringDataS2CPacketPayload.ID, (payload, context) -> {
			context.client().execute(() -> {
				onUpdateCustomData(payload);
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