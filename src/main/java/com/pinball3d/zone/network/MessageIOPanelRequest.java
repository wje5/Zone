package com.pinball3d.zone.network;

import com.pinball3d.zone.sphinx.StorageWrapper;
import com.pinball3d.zone.sphinx.WorldPos;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageIOPanelRequest extends MessageSphinxAdmin {
	public MessageIOPanelRequest() {

	}

	public MessageIOPanelRequest(String password, WorldPos pos, NBTTagCompound tag) {
		super(password, pos, tag);
	}

	public static MessageIOPanelRequest newMessage(String password, WorldPos network, StorageWrapper req,
			WorldPos target) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setTag("req", req.writeToNBT(new NBTTagCompound()));
		tag.setTag("target", target.writeToNBT(new NBTTagCompound()));
		return new MessageIOPanelRequest(password, network, tag);
	}

	@Override
	public void run(MessageContext ctx) {
		getTileEntity().requestItems(new StorageWrapper(tag.getCompoundTag("req")),
				WorldPos.load(tag.getCompoundTag("target")));
	}

	public static class Handler implements IMessageHandler<MessageIOPanelRequest, IMessage> {
		@Override
		public IMessage onMessage(MessageIOPanelRequest message, MessageContext ctx) {
			message.doHandler(ctx);
			return null;
		}
	}
}
