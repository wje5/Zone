package com.pinball3d.zone.network;

import com.pinball3d.zone.item.ItemLoader;
import com.pinball3d.zone.sphinx.SphinxUtil;
import com.pinball3d.zone.sphinx.WorldPos;
import com.pinball3d.zone.tileentity.INeedNetwork;
import com.pinball3d.zone.tileentity.TEProcessingCenter;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
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

public class MessageTryConnectToNetwork implements IMessage {
	String name, password;
	boolean isPlayer;
	WorldPos pos, network;

	public MessageTryConnectToNetwork() {

	}

	public MessageTryConnectToNetwork(EntityPlayer player, boolean isPlayer, WorldPos pos, WorldPos network,
			String password) {
		name = player.getName();
		this.isPlayer = isPlayer;
		this.pos = pos;
		this.network = network;
		this.password = password;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		name = ByteBufUtils.readUTF8String(buf);
		isPlayer = buf.readBoolean();
		pos = WorldPos.readFromByte(buf);
		network = WorldPos.readFromByte(buf);
		password = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, name);
		buf.writeBoolean(isPlayer);
		pos.writeToByte(buf);
		network.writeToByte(buf);
		ByteBufUtils.writeUTF8String(buf, password);
	}

	public static class Handler implements IMessageHandler<MessageTryConnectToNetwork, IMessage> {
		@Override
		public IMessage onMessage(MessageTryConnectToNetwork message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(new Runnable() {
				@Override
				public void run() {
					World world = message.network.getWorld();
					EntityPlayerMP player = (EntityPlayerMP) world.getPlayerEntityByName(message.name);
					if (!world.isAreaLoaded(message.network.getPos(), 5)) {
						return;
					}
					TileEntity tileentity = message.network.getTileEntity();
					if (tileentity instanceof TEProcessingCenter) {
						TEProcessingCenter te = (TEProcessingCenter) tileentity;
						if (te.isDeviceInRange(message.pos) && te.isCorrectLoginPassword(message.password)) {
							NetworkHandler.instance.sendTo(
									new MessageConnectNetworkCallback(te.getUUID(), message.network, message.password),
									player);
							if (message.isPlayer) {
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
								tag.setUniqueId("network", te.getUUID());
								tag.setString("password", message.password);
								stack.setTagCompound(tag);
								NBTTagCompound data = SphinxUtil.getValidNetworkData(message.pos, player, true);
								NetworkHandler.instance.sendTo(new MessageSendValidNetworkData(data), player);
							} else {
								TileEntity t = message.pos.getTileEntity();
								if (t instanceof INeedNetwork) {
									((INeedNetwork) t).connect(te.getUUID(), message.password);
									((INeedNetwork) t).setWorldPos(message.network, te.getUUID());
									te.addNeedNetwork(message.pos);
								}
								NBTTagCompound tag = SphinxUtil.getValidNetworkData(message.pos, player, false);
								NetworkHandler.instance.sendTo(new MessageSendValidNetworkData(tag), player);
							}
						} else {
							NetworkHandler.instance.sendTo(new MessageConnectNetworkCallbackWrong(), player);
						}
					}
				}
			});
			return null;
		}
	}
}
