package com.pinball3d.zone.network;

import java.util.UUID;

import com.pinball3d.zone.sphinx.GlobalNetworkData;
import com.pinball3d.zone.sphinx.WorldPos;
import com.pinball3d.zone.tileentity.INeedNetwork;
import com.pinball3d.zone.tileentity.TEProcessingCenter;

import io.netty.buffer.ByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageConnectToNetwork implements IMessage {
	WorldPos network, needNetwork;
	String password;

	public MessageConnectToNetwork() {

	}

	public MessageConnectToNetwork(WorldPos network, WorldPos needNetwork, String password) {
		this.network = network;
		this.needNetwork = needNetwork;
		this.password = password;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		network = WorldPos.readFromByte(buf);
		needNetwork = WorldPos.readFromByte(buf);
		password = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		network.writeToByte(buf);
		needNetwork.writeToByte(buf);
		ByteBufUtils.writeUTF8String(buf, password);
	}

	public static class Handler implements IMessageHandler<MessageConnectToNetwork, IMessage> {
		@Override
		public IMessage onMessage(MessageConnectToNetwork message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(new Runnable() {
				@Override
				public void run() {
					MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
					World world = message.needNetwork.getWorld();
					if (!world.isAreaLoaded(message.needNetwork.getPos(), 5)) {
						return;
					}
					TileEntity te1 = message.needNetwork.getTileEntity();
					TileEntity te2 = message.network.getTileEntity();
					if (te1 instanceof INeedNetwork && te2 instanceof TEProcessingCenter) {
						INeedNetwork te = (INeedNetwork) te1;
						WorldPos pos3 = te.getNetworkPos();
						if (pos3 != null) {
							TEProcessingCenter pc = (TEProcessingCenter) pos3.getTileEntity();
							pc.removeNeedNetwork(message.needNetwork);
						} else if (te.getNetwork() != null) {
							pos3 = GlobalNetworkData.getData(((TileEntity) te).getWorld()).getNetwork(te.getNetwork());
							TEProcessingCenter pc = (TEProcessingCenter) pos3.getTileEntity();
							pc.removeNeedNetwork(message.needNetwork);
						}
						TEProcessingCenter pc = (TEProcessingCenter) te2;
						if (pc.isCorrectLoginPassword(message.password)) {
							UUID uuid = GlobalNetworkData.getData(world).getUUID(message.network);
							te.connect(uuid, message.password);
							pc.addNeedNetwork(message.needNetwork);
						}
					}
				}
			});
			return null;
		}
	}
}
