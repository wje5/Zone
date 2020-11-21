package com.pinball3d.zone.network;

import com.pinball3d.zone.item.ItemLoader;
import com.pinball3d.zone.sphinx.SphinxUtil;
import com.pinball3d.zone.sphinx.WorldPos;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageTerminalDisconnect implements IMessage {
	String name;
	int world;

	public MessageTerminalDisconnect() {

	}

	public MessageTerminalDisconnect(EntityPlayer player) {
		name = player.getName();
		world = player.dimension;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		name = ByteBufUtils.readUTF8String(buf);
		world = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, name);
		buf.writeInt(world);
	}

	public static class Handler implements IMessageHandler<MessageTerminalDisconnect, IMessage> {
		@Override
		public IMessage onMessage(MessageTerminalDisconnect message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
				MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
				World world = server.getWorld(message.world);
				EntityPlayer player = world.getPlayerEntityByName(message.name);
				if (player != null) {
					ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
					if (stack.getItem() == ItemLoader.terminal) {
						NBTTagCompound tag = stack.getTagCompound();
						if (tag == null) {
							tag = new NBTTagCompound();
							stack.setTagCompound(tag);
						}
						tag.removeTag("networkMost");
						tag.removeTag("networkLeast");
					} else {
						stack = player.getHeldItem(EnumHand.OFF_HAND);
						if (stack.getItem() == ItemLoader.terminal) {
							NBTTagCompound tag = stack.getTagCompound();
							if (tag == null) {
								tag = new NBTTagCompound();
								stack.setTagCompound(tag);
							}
							tag.removeTag("networkMost");
							tag.removeTag("networkLeast");
						}
					}
					NBTTagCompound tag = SphinxUtil.getValidNetworkData(new WorldPos(player), player, true);
					NetworkHandler.instance.sendTo(new MessageSendValidNetworkData(tag), (EntityPlayerMP) player);
				}
			});
			return null;
		}
	}
}