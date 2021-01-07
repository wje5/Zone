package com.pinball3d.zone.network;

import com.pinball3d.zone.sphinx.ContainerIOPanel;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageIOPanelPageChange implements IMessage {
	String name;
	int world;
	boolean flag;

	public MessageIOPanelPageChange() {

	}

	public MessageIOPanelPageChange(EntityPlayer player, boolean flag) {
		name = player.getName();
		world = player.world.provider.getDimension();
		this.flag = flag;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		name = ByteBufUtils.readUTF8String(buf);
		world = buf.readInt();
		flag = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, name);
		buf.writeInt(world);
		buf.writeBoolean(flag);
	}

	public static class Handler implements IMessageHandler<MessageIOPanelPageChange, IMessage> {
		@Override
		public IMessage onMessage(MessageIOPanelPageChange message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
				MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
				World world = server.getWorld(message.world);
				EntityPlayer player = world.getPlayerEntityByName(message.name);
				if (player != null && player.openContainer instanceof ContainerIOPanel) {
					ContainerIOPanel container = (ContainerIOPanel) player.openContainer;
					if (message.flag) {
						container.page = container.page - 1 < 1 ? container.maxPage : container.page - 1;
					} else {
						container.page = container.page + 1 > container.maxPage ? 1 : container.page + 1;
					}
				}
			});
			return null;
		}
	}
}