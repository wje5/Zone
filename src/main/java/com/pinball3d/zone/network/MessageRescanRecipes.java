package com.pinball3d.zone.network;

import com.pinball3d.zone.sphinx.log.LogRescanRecipes;
import com.pinball3d.zone.tileentity.TEProcessingCenter;
import com.pinball3d.zone.util.WorldPos;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageRescanRecipes extends MessageSphinxAdmin {
	public MessageRescanRecipes() {

	}

	private MessageRescanRecipes(WorldPos pos, NBTTagCompound tag) {
		super(pos, tag);
	}

	public static MessageRescanRecipes newMessage(WorldPos pos) {
		NBTTagCompound tag = new NBTTagCompound();
		return new MessageRescanRecipes(pos, tag);
	}

	@Override
	public void run(MessageContext ctx) {
		TEProcessingCenter te = getProcessingCenter();
		te.fireLog(new LogRescanRecipes(te.getNextLogId(), getPlayer(ctx)));
		int[] r = te.rescanRecipes();
		NetworkHandler.instance.sendTo(new MessageRescanRecipesFinish(r[0], r[1], r[2]),
				(EntityPlayerMP) getPlayer(ctx));
	}

	public static class Handler implements IMessageHandler<MessageRescanRecipes, IMessage> {
		@Override
		public IMessage onMessage(MessageRescanRecipes message, MessageContext ctx) {
			message.doHandler(ctx);
			return null;
		}
	}
}