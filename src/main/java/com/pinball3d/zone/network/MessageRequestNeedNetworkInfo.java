package com.pinball3d.zone.network;

import com.pinball3d.zone.sphinx.SerialNumber;
import com.pinball3d.zone.tileentity.INeedNetwork;
import com.pinball3d.zone.util.WorldPos;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageRequestNeedNetworkInfo extends MessageSphinx {
	public MessageRequestNeedNetworkInfo() {

	}

	private MessageRequestNeedNetworkInfo(WorldPos pos, NBTTagCompound tag) {
		super(pos, tag);
	}

	public static MessageRequestNeedNetworkInfo newMessage(WorldPos pos, SerialNumber serial) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setTag("serial", serial.writeToNBT(new NBTTagCompound()));
		return new MessageRequestNeedNetworkInfo(pos, tag);
	}

	@Override
	public void run(MessageContext ctx) {
		SerialNumber s = new SerialNumber(tag.getCompoundTag("serial"));
		WorldPos pos = getProcessingCenter().getPosFromSerialNumber(s);
		if (!pos.isOrigin()) {
			TileEntity tileentity = pos.getTileEntity();
			if (tileentity instanceof INeedNetwork) {
				INeedNetwork te = (INeedNetwork) tileentity;
				NBTTagCompound tag = new NBTTagCompound();
				tag.setString("name", te.getName());
				tag.setInteger("state", te.getWorkingState().ordinal());
				tag.setTag("pos", pos.writeToNBT(new NBTTagCompound()));
				NetworkHandler.instance.sendTo(new MessageSendNeedNetworkInfoToClient(s, tag),
						(EntityPlayerMP) getPlayer(ctx));
			}
		}
	}

	public static class Handler implements IMessageHandler<MessageRequestNeedNetworkInfo, IMessage> {
		@Override
		public IMessage onMessage(MessageRequestNeedNetworkInfo message, MessageContext ctx) {
			message.doHandler(ctx);
			return null;
		}
	}
}
