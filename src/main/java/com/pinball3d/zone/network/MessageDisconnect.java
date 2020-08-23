package com.pinball3d.zone.network;

import com.pinball3d.zone.sphinx.GlobalNetworkData;
import com.pinball3d.zone.sphinx.WorldPos;
import com.pinball3d.zone.tileentity.INeedNetwork;
import com.pinball3d.zone.tileentity.TEProcessingCenter;

import io.netty.buffer.ByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageDisconnect implements IMessage {
	WorldPos needNetwork;

	public MessageDisconnect() {

	}

	public MessageDisconnect(WorldPos needNetwork) {
		this.needNetwork = needNetwork;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		needNetwork = WorldPos.readFromByte(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		needNetwork.writeToByte(buf);
	}

	public static class Handler implements IMessageHandler<MessageDisconnect, IMessage> {
		@Override
		public IMessage onMessage(MessageDisconnect message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(new Runnable() {
				@Override
				public void run() {
					MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
					World world = message.needNetwork.getWorld();
					if (!world.isAreaLoaded(message.needNetwork.getPos(), 5)) {
						return;
					}
					TileEntity te = message.needNetwork.getTileEntity();
					if (te instanceof INeedNetwork) {
						((INeedNetwork) te).deleteNetwork();
						WorldPos pos = GlobalNetworkData.getData(te.getWorld())
								.getNetwork(((INeedNetwork) te).getNetwork());
						if (pos != null) {
							TEProcessingCenter pc = (TEProcessingCenter) pos.getTileEntity();
							pc.removeNeedNetwork(message.needNetwork);
						}
					}
				}
			});
			return null;
		}
	}
}
