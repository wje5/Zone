package com.pinball3d.zone.network;

import com.pinball3d.zone.inventory.ContainerIOPanel;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageIOPanelSearchChange implements IMessage {
	String name, search;
	int world;

	public MessageIOPanelSearchChange() {

	}

	public MessageIOPanelSearchChange(EntityPlayer player, String text) {
		name = player.getName();
		world = player.world.provider.getDimension();
		search = text;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		name = ByteBufUtils.readUTF8String(buf);
		world = buf.readInt();
		search = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, name);
		buf.writeInt(world);
		ByteBufUtils.writeUTF8String(buf, search);
	}

	public static class Handler implements IMessageHandler<MessageIOPanelSearchChange, IMessage> {
		@Override
		public IMessage onMessage(MessageIOPanelSearchChange message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
				MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
				World world = server.getWorld(message.world);
				EntityPlayer player = world.getPlayerEntityByName(message.name);
				if (player != null && player.openContainer instanceof ContainerIOPanel) {
					ContainerIOPanel container = (ContainerIOPanel) player.openContainer;
					container.search = message.search;
				}
			});
			return null;
		}
	}
}