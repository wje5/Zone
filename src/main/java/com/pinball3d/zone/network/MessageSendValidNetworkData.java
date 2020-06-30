package com.pinball3d.zone.network;

import java.util.ArrayList;
import java.util.List;

import com.pinball3d.zone.sphinx.ScreenNeedNetwork;
import com.pinball3d.zone.sphinx.ScreenTerminal;
import com.pinball3d.zone.sphinx.SubscreenNetworkConfig;
import com.pinball3d.zone.sphinx.WorldPos;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageSendValidNetworkData implements IMessage {
	List<WorldPos> list = new ArrayList<WorldPos>();

	public MessageSendValidNetworkData() {

	}

	public MessageSendValidNetworkData(List<WorldPos> list) {
		this.list = list;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		NBTTagCompound tag = ByteBufUtils.readTag(buf);
		NBTTagList taglist = tag.getTagList("list", 10);
		taglist.forEach(e -> {
			list.add(WorldPos.load((NBTTagCompound) e));
		});
	}

	@Override
	public void toBytes(ByteBuf buf) {
		NBTTagCompound tag = new NBTTagCompound();
		NBTTagList taglist = new NBTTagList();
		list.forEach(e -> {
			taglist.appendTag(e.save(new NBTTagCompound()));
		});
		tag.setTag("list", taglist);
		ByteBufUtils.writeTag(buf, tag);
	}

	public static class Handler implements IMessageHandler<MessageSendValidNetworkData, IMessage> {
		@Override
		public IMessage onMessage(MessageSendValidNetworkData message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(new Runnable() {
				@Override
				public void run() {
					GuiScreen screen = Minecraft.getMinecraft().currentScreen;
					if (screen instanceof ScreenTerminal) {
						ScreenTerminal terminal = (ScreenTerminal) screen;
						if (!terminal.subscreens.empty()
								&& terminal.subscreens.get(0) instanceof SubscreenNetworkConfig) {
							((SubscreenNetworkConfig) terminal.subscreens.get(0)).list.setData(message.list);
						}
					}
					if (screen instanceof ScreenNeedNetwork) {
						ScreenNeedNetwork s = (ScreenNeedNetwork) screen;
						if (!s.subscreens.empty() && s.subscreens.get(0) instanceof SubscreenNetworkConfig) {
							((SubscreenNetworkConfig) s.subscreens.get(0)).list.setData(message.list);
						}
					}
				}
			});
			return null;
		}
	}
}