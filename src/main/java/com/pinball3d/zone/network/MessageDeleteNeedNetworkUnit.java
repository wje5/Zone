package com.pinball3d.zone.network;

import com.pinball3d.zone.sphinx.GlobalNetworkData;
import com.pinball3d.zone.tileentity.INeedNetwork;
import com.pinball3d.zone.tileentity.TEProcessingCenter;
import com.pinball3d.zone.util.WorldPos;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageDeleteNeedNetworkUnit implements IMessage {
	WorldPos needNetwork;

	public MessageDeleteNeedNetworkUnit() {

	}

	public MessageDeleteNeedNetworkUnit(WorldPos needNetwork) {
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

	public static class Handler implements IMessageHandler<MessageDeleteNeedNetworkUnit, IMessage> {
		@Override
		public IMessage onMessage(MessageDeleteNeedNetworkUnit message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
				World world = message.needNetwork.getWorld();
				if (!world.isAreaLoaded(message.needNetwork.getPos(), 5)) {
					return;
				}
				TileEntity te = message.needNetwork.getTileEntity();
				if (te instanceof INeedNetwork) {
					WorldPos pos = GlobalNetworkData.getData(te.getWorld())
							.getNetwork(((INeedNetwork) te).getNetwork());
					((INeedNetwork) te).deleteNetwork();
					if (pos != null) {
						TEProcessingCenter pc = (TEProcessingCenter) pos.getTileEntity();
//						pc.removeNeedNetwork(message.needNetwork); FIXME or delete class...?
					}
				}
			});
			return null;
		}
	}
}
