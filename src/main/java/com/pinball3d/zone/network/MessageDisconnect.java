package com.pinball3d.zone.network;

import com.pinball3d.zone.network.ConnectionHelper.Connect;
import com.pinball3d.zone.sphinx.GlobalNetworkData;
import com.pinball3d.zone.tileentity.INeedNetwork;
import com.pinball3d.zone.tileentity.TEProcessingCenter;
import com.pinball3d.zone.util.WorldPos;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageDisconnect implements IMessage {
	String name;
	WorldPos needNetwork;

	public MessageDisconnect() {

	}

	public MessageDisconnect(EntityPlayer player, WorldPos needNetwork) {
		name = player.getName();
		this.needNetwork = needNetwork;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		name = ByteBufUtils.readUTF8String(buf);
		needNetwork = WorldPos.readFromByte(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, name);
		needNetwork.writeToByte(buf);
	}

	public static class Handler implements IMessageHandler<MessageDisconnect, IMessage> {
		@Override
		public IMessage onMessage(MessageDisconnect message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
				World world = message.needNetwork.getWorld();
				EntityPlayerMP player = (EntityPlayerMP) world.getPlayerEntityByName(message.name);
				TileEntity te = message.needNetwork.getTileEntity();
				if (te instanceof INeedNetwork) {
					WorldPos pos = GlobalNetworkData.getData(te.getWorld())
							.getNetwork(((INeedNetwork) te).getNetwork());
					((INeedNetwork) te).deleteNetwork();
					if (pos != null) {
						TEProcessingCenter pc = (TEProcessingCenter) pos.getTileEntity();
						pc.removeNeedNetwork(message.needNetwork);
					}
					Connect c = ConnectionHelper.getConnect(player.getUniqueID());
					c.network = null;
				}
			});
			return null;
		}
	}
}
