package com.pinball3d.zone.network;

import com.pinball3d.zone.Zone;
import com.pinball3d.zone.inventory.GuiElementLoader;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageOpenIOPanelGui implements IMessage {
	String name;
	int world, x, y, z;

	public MessageOpenIOPanelGui() {

	}

	public MessageOpenIOPanelGui(EntityPlayer player, int x, int y, int z) {
		name = player.getName();
		world = player.world.provider.getDimension();
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		name = ByteBufUtils.readUTF8String(buf);
		world = buf.readInt();
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, name);
		buf.writeInt(world);
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
	}

	public static class Handler implements IMessageHandler<MessageOpenIOPanelGui, IMessage> {
		@Override
		public IMessage onMessage(MessageOpenIOPanelGui message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(new Runnable() {
				@Override
				public void run() {
					MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
					World world = server.getWorld(message.world);
					EntityPlayer player = world.getPlayerEntityByName(message.name);
					if (player != null) {
						player.openGui(Zone.instance, GuiElementLoader.IO_PANEL, world, message.x, message.y,
								message.z);
					}
				}
			});

			return null;
		}
	}
}
