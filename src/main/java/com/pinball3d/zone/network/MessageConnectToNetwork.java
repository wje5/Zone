package com.pinball3d.zone.network;

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

public class MessageConnectToNetwork implements IMessage {
	WorldPos network, needNetwork;

	public MessageConnectToNetwork() {

	}

	public MessageConnectToNetwork(WorldPos network, WorldPos needNetwork) {
		this.network = network;
		this.needNetwork = needNetwork;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		network = WorldPos.readFromByte(buf);
		needNetwork = WorldPos.readFromByte(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		network.writeToByte(buf);
		needNetwork.writeToByte(buf);
	}

	public static class Handler implements IMessageHandler<MessageConnectToNetwork, IMessage> {
		@Override
		public IMessage onMessage(MessageConnectToNetwork message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(new Runnable() {
				@Override
				public void run() {
					MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
					World world = message.needNetwork.getWorld(server);
					if (!world.isAreaLoaded(message.needNetwork.getPos(), 5)) {
						return;
					}
					TileEntity te = message.needNetwork.getTileEntity(server);
					TileEntity te2 = message.network.getTileEntity(server);

					if (te instanceof INeedNetwork && te2 instanceof TEProcessingCenter) {
						((INeedNetwork) te).connect(message.network);
					}
				}
			});

			return null;
		}
	}
}
