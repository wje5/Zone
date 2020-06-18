package com.pinball3d.zone.network;

import java.util.UUID;

import com.pinball3d.zone.sphinx.GlobalNetworkData;
import com.pinball3d.zone.sphinx.WorldPos;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageTerminalRequestNetworkData implements IMessage {
	UUID uuid;
	String name;

	public MessageTerminalRequestNetworkData() {

	}

	public MessageTerminalRequestNetworkData(UUID uuid, String name) {
		this.uuid = uuid;
		this.name = name;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		uuid = new UUID(buf.readLong(), buf.readLong());
		name = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(uuid.getMostSignificantBits());
		buf.writeLong(uuid.getLeastSignificantBits());
		ByteBufUtils.writeUTF8String(buf, name);
	}

	public static class Handler implements IMessageHandler<MessageTerminalRequestNetworkData, IMessage> {
		@Override
		public IMessage onMessage(MessageTerminalRequestNetworkData message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(new Runnable() {
				@Override
				public void run() {
					World world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(0);
					WorldPos pos = GlobalNetworkData.getData(world).getNetwork(message.uuid);
					NetworkHandler.instance.sendTo(new MessageSendNetworkDataToTerminal(pos, message.uuid),
							(EntityPlayerMP) world.getPlayerEntityByName(message.name));
				}
			});
			return null;
		}
	}
}
