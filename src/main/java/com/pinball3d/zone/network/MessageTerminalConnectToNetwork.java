package com.pinball3d.zone.network;

import com.pinball3d.zone.item.ItemLoader;
import com.pinball3d.zone.sphinx.WorldPos;
import com.pinball3d.zone.tileentity.TEProcessingCenter;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageTerminalConnectToNetwork implements IMessage {
	WorldPos network;
	String name, password;

	public MessageTerminalConnectToNetwork() {

	}

	public MessageTerminalConnectToNetwork(WorldPos network, String name, String password) {
		this.network = network;
		this.name = name;
		this.password = password;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		network = WorldPos.readFromByte(buf);
		name = ByteBufUtils.readUTF8String(buf);
		password = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		network.writeToByte(buf);
		ByteBufUtils.writeUTF8String(buf, name);
		ByteBufUtils.writeUTF8String(buf, password);
	}

	public static class Handler implements IMessageHandler<MessageTerminalConnectToNetwork, IMessage> {
		@Override
		public IMessage onMessage(MessageTerminalConnectToNetwork message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(new Runnable() {
				@Override
				public void run() {
					World world = message.network.getWorld();
					if (!world.isAreaLoaded(message.network.getPos(), 5)) {
						return;
					}
					TileEntity te = message.network.getTileEntity();

					if (te instanceof TEProcessingCenter) {
						EntityPlayer player = te.getWorld().getPlayerEntityByName(message.name);
						if (player != null) {
							ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
							if (stack.getItem() != ItemLoader.terminal) {
								stack = player.getHeldItem(EnumHand.OFF_HAND);
								if (stack.getItem() != ItemLoader.terminal) {
									return;
								}
							}
							NBTTagCompound tag = stack.getTagCompound();
							if (tag == null) {
								tag = new NBTTagCompound();
							}
							tag.setUniqueId("network", ((TEProcessingCenter) te).getUUID());
							tag.setString("password", message.password);
							stack.setTagCompound(tag);
						}
					}
				}
			});
			return null;
		}
	}
}