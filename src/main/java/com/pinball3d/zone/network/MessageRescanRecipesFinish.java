package com.pinball3d.zone.network;

import com.pinball3d.zone.gui.GuiContainerZone;
import com.pinball3d.zone.sphinx.subscreen.SubscreenMessageBox;
import com.pinball3d.zone.util.Util;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageRescanRecipesFinish implements IMessage {
	int recipeAdd, add, change;

	public MessageRescanRecipesFinish() {

	}

	public MessageRescanRecipesFinish(int recipeAdd, int add, int change) {
		this.recipeAdd = recipeAdd;
		this.add = add;
		this.change = change;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		recipeAdd = buf.readInt();
		add = buf.readInt();
		change = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(recipeAdd);
		buf.writeInt(add);
		buf.writeInt(change);
	}

	public static class Handler implements IMessageHandler<MessageRescanRecipesFinish, IMessage> {
		@Override
		public IMessage onMessage(MessageRescanRecipesFinish message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
				GuiScreen screen = Minecraft.getMinecraft().currentScreen;
				if (screen instanceof GuiContainerZone) {
					GuiContainerZone s = (GuiContainerZone) screen;
					s.putScreen(
							new SubscreenMessageBox(s, I18n.format("sphinx.rescan_recipes"), Util.formatAndAntiEscape(
									"sphinx.rescan_recipes_finish", message.recipeAdd, message.add, message.change)));
				}
			});
			return null;
		}
	}
}
