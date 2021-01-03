package com.pinball3d.zone.network;

import com.pinball3d.zone.inventory.ContainerIOPanel;
import com.pinball3d.zone.tileentity.TEProcessingCenter;
import com.pinball3d.zone.util.WorldPos;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageUpdateIOPanelGui implements IMessage {
	String name;
	int world;

	public MessageUpdateIOPanelGui() {

	}

	public MessageUpdateIOPanelGui(EntityPlayer player) {
		name = player.getName();
		world = player.world.provider.getDimension();
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		name = ByteBufUtils.readUTF8String(buf);
		world = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, name);
		buf.writeInt(world);
	}

	public static class Handler implements IMessageHandler<MessageUpdateIOPanelGui, IMessage> {
		@Override
		public IMessage onMessage(MessageUpdateIOPanelGui message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
				MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
				World world = server.getWorld(message.world);
				EntityPlayer player = world.getPlayerEntityByName(message.name);
				if (player != null && player.openContainer instanceof ContainerIOPanel) {
					ContainerIOPanel container = (ContainerIOPanel) player.openContainer;
					WorldPos pos = container.tileEntity.getNetworkPos();
					if (container.tileEntity.isConnected() && pos != null) {
						TileEntity tileEntity = pos.getTileEntity();
						if (pos.getTileEntity() instanceof TEProcessingCenter) {
							container.setData(((TEProcessingCenter) tileEntity).getNetworkUseableItems());
						}
					}
				}
			});
			return null;
		}
	}
}
