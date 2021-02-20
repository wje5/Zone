package com.pinball3d.zone.network;

import com.pinball3d.zone.sphinx.container.ContainerIOPanel;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageIOPanelTransferPlayerInventory implements IMessage {
	String name;
	int world;
	boolean flag;

	public MessageIOPanelTransferPlayerInventory() {

	}

	public MessageIOPanelTransferPlayerInventory(EntityPlayer player, boolean flag) {
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

	public static class Handler implements IMessageHandler<MessageIOPanelTransferPlayerInventory, IMessage> {
		@Override
		public IMessage onMessage(MessageIOPanelTransferPlayerInventory message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
				MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
				World world = server.getWorld(message.world);
				EntityPlayer player = world.getPlayerEntityByName(message.name);
				if (player != null && player.openContainer instanceof ContainerIOPanel) {
					if (message.flag) {
						for (int i = 54; i < 90; i++) {
							player.openContainer.transferStackInSlot(player, i);
						}
					} else {
						for (int i = 0; i < 54; i++) {
							player.openContainer.transferStackInSlot(player, i);

						}
					}
				}
			});
			return null;
		}
	}
}