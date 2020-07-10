package com.pinball3d.zone.network;

import com.pinball3d.zone.sphinx.WorldPos;
import com.pinball3d.zone.tileentity.TEIOPanel;

import io.netty.buffer.ByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageIOPanelPageChange implements IMessage {
	WorldPos pos;
	boolean flag;

	public MessageIOPanelPageChange() {

	}

	public MessageIOPanelPageChange(WorldPos pos, boolean flag) {
		this.pos = pos;
		this.flag = flag;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		pos = WorldPos.readFromByte(buf);
		flag = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		pos.writeToByte(buf);
		buf.writeBoolean(flag);
	}

	public static class Handler implements IMessageHandler<MessageIOPanelPageChange, IMessage> {
		@Override
		public IMessage onMessage(MessageIOPanelPageChange message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(new Runnable() {
				@Override
				public void run() {
					MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
					World world = message.pos.getWorld();
					if (!world.isAreaLoaded(message.pos.getPos(), 5)) {
						return;
					}
					TileEntity te = message.pos.getTileEntity();
					if (te instanceof TEIOPanel) {
						if (message.flag) {
							((TEIOPanel) te).pageUp();
						} else {
							((TEIOPanel) te).pageDown();
						}
					}
				}
			});
			return null;
		}
	}
}
