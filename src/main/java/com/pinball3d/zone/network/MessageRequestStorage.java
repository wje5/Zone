package com.pinball3d.zone.network;

import com.pinball3d.zone.sphinx.StorageWrapper;
import com.pinball3d.zone.sphinx.WorldPos;
import com.pinball3d.zone.tileentity.TEProcessingCenter;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageRequestStorage implements IMessage {
	String name;
	int world;
	WorldPos network;

	public MessageRequestStorage() {

	}

	public MessageRequestStorage(EntityPlayer player, WorldPos network) {
		name = player.getName();
		world = player.world.provider.getDimension();
		this.network = network;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		name = ByteBufUtils.readUTF8String(buf);
		world = buf.readInt();
		network = WorldPos.readFromByte(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, name);
		buf.writeInt(world);
		network.writeToByte(buf);
	}

	public static class Handler implements IMessageHandler<MessageRequestStorage, IMessage> {
		@Override
		public IMessage onMessage(MessageRequestStorage message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(new Runnable() {
				@Override
				public void run() {
					MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
					World world = server.getWorld(message.world);
					EntityPlayer player = world.getPlayerEntityByName(message.name);
					if (player != null) {
						if (message.network != null) {
							TileEntity tileEntity = message.network.getTileEntity();
							if (message.network.getTileEntity() instanceof TEProcessingCenter) {
								TEProcessingCenter te = (TEProcessingCenter) tileEntity;
								StorageWrapper data = te.getNetworkUseableItems();
								int usedStorage = te.getUsedStorage();
								int maxStorage = te.getMaxStorage();
								NetworkHandler.instance.sendTo(
										new MessageSendStorageToClient(data, usedStorage, maxStorage),
										(EntityPlayerMP) player);
							}
						}
					}
				}
			});
			return null;
		}
	}
}
