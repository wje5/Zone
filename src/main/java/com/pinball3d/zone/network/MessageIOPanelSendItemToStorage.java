package com.pinball3d.zone.network;

import com.pinball3d.zone.tileentity.TEIOPanel;
import com.pinball3d.zone.tileentity.TEProcessingCenter;
import com.pinball3d.zone.util.StorageWrapper;
import com.pinball3d.zone.util.WorldPos;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageIOPanelSendItemToStorage extends MessageSphinxNeedNetwork {
	public MessageIOPanelSendItemToStorage() {

	}

	public MessageIOPanelSendItemToStorage(EntityPlayer player, WorldPos pos, NBTTagCompound tag) {
		super(player, pos, tag);
	}

	public static MessageIOPanelSendItemToStorage newMessage(EntityPlayer player, WorldPos needNetwork) {
		return new MessageIOPanelSendItemToStorage(player, needNetwork, new NBTTagCompound());
	}

	@Override
	public void run(MessageContext ctx) {
		TileEntity tileentity = pos.getTileEntity();
		if (tileentity instanceof TEIOPanel) {
			TEIOPanel te = (TEIOPanel) tileentity;
			TEProcessingCenter pc = getProcessingCenter();
			StorageWrapper wrapper = new StorageWrapper(te.inv, false);
			if (!wrapper.isEmpty()) {
				StorageWrapper wrapper2 = pc.dispenceItems(wrapper, new WorldPos(te.getPos(), te.getWorld()));
				if (!wrapper2.isEmpty()) {
					EntityPlayer player = getPlayer();
					if (player != null) {
						NetworkHandler.instance.sendTo(new MessageErrorStorageFull(), (EntityPlayerMP) player);
					}
				}
				pc.insertToItemHandler(wrapper2, te.inv);
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
