package com.pinball3d.zone.network;

import com.pinball3d.zone.sphinx.StorageWrapper;
import com.pinball3d.zone.sphinx.WorldPos;
import com.pinball3d.zone.tileentity.TEIOPanel;
import com.pinball3d.zone.tileentity.TEProcessingCenter;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageIOPanelSendItemToStorage extends MessageSphinx {
	public MessageIOPanelSendItemToStorage() {

	}

	public MessageIOPanelSendItemToStorage(String password, WorldPos pos, NBTTagCompound tag) {
		super(password, pos, tag);
	}

	public static MessageIOPanelSendItemToStorage newMessage(String password, WorldPos network, WorldPos panelpos) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setTag("panelpos", panelpos.writeToNBT(new NBTTagCompound()));
		return new MessageIOPanelSendItemToStorage(password, network, tag);
	}

	@Override
	public void run(MessageContext ctx) {
		TileEntity tileentity = WorldPos.load(tag.getCompoundTag("panelpos")).getTileEntity();
		if (tileentity instanceof TEIOPanel) {
			TEIOPanel te = (TEIOPanel) tileentity;
			TEProcessingCenter pc = (TEProcessingCenter) pos.getTileEntity();
			StorageWrapper wrapper = new StorageWrapper(te.inv, false);
			if (!wrapper.isEmpty()) {
				pc.insertToItemHandler(pc.dispenceItems(wrapper, new WorldPos(te.getPos(), te.getWorld())), te.inv);
			}
		}
	}

	public static class Handler implements IMessageHandler<MessageIOPanelSendItemToStorage, IMessage> {
		@Override
		public IMessage onMessage(MessageIOPanelSendItemToStorage message, MessageContext ctx) {
			message.doHandler(ctx);
			return null;
		}
	}
}
