package com.pinball3d.zone.network;

import java.util.UUID;

import com.pinball3d.zone.sphinx.GlobalNetworkData;
import com.pinball3d.zone.sphinx.WorldPos;
import com.pinball3d.zone.tileentity.TEProcessingCenter;

import io.netty.buffer.ByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageRegisterSphinx implements IMessage {
	WorldPos worldpos;

	public MessageRegisterSphinx() {

	}

	public MessageRegisterSphinx(WorldPos worldpos) {
		this.worldpos = worldpos;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		worldpos = WorldPos.readFromByte(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		worldpos.writeToByte(buf);
	}

	public static class Handler implements IMessageHandler<MessageRegisterSphinx, IMessage> {
		@Override
		public IMessage onMessage(MessageRegisterSphinx message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(new Runnable() {
				@Override
				public void run() {
					MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
					World world = message.worldpos.getWorld();
					if (!world.isAreaLoaded(message.worldpos.getPos(), 5)) {
						return;
					}
					TileEntity te = message.worldpos.getTileEntity();
					if (te instanceof TEProcessingCenter) {
						UUID uuid = GlobalNetworkData.getData(te.getWorld()).getUUID(message.worldpos);
						((TEProcessingCenter) te).setUUID(uuid);
						NetworkHandler.instance.sendToAll(new MessageSendUUIDToClient(message.worldpos, uuid));
					}
				}
			});

			return null;
		}
	}
}
