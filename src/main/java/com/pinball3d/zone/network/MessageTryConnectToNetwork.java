package com.pinball3d.zone.network;

import java.util.UUID;

import com.pinball3d.zone.item.ItemLoader;
import com.pinball3d.zone.network.ConnectionHelper.Connect;
import com.pinball3d.zone.tileentity.INeedNetwork;
import com.pinball3d.zone.tileentity.TEProcessingCenter;
import com.pinball3d.zone.tileentity.TEProcessingCenter.UserData;
import com.pinball3d.zone.util.WorldPos;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageTryConnectToNetwork implements IMessage {
	UUID uuid;
	boolean isPlayer;
	WorldPos pos, network;

	public MessageTryConnectToNetwork() {

	}

	public MessageTryConnectToNetwork(EntityPlayer player, boolean isPlayer, WorldPos pos, WorldPos network) {
		uuid = player.getUniqueID();
		this.isPlayer = isPlayer;
		this.pos = pos;
		this.network = network;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		uuid = new UUID(buf.readLong(), buf.readLong());
		isPlayer = buf.readBoolean();
		pos = WorldPos.readFromByte(buf);
		network = WorldPos.readFromByte(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(uuid.getMostSignificantBits());
		buf.writeLong(uuid.getLeastSignificantBits());
		buf.writeBoolean(isPlayer);
		pos.writeToByte(buf);
		network.writeToByte(buf);
	}

	public static class Handler implements IMessageHandler<MessageTryConnectToNetwork, IMessage> {
		@Override
		public IMessage onMessage(MessageTryConnectToNetwork message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
				World world = message.pos.getWorld();
				EntityPlayerMP player = (EntityPlayerMP) world.getPlayerEntityByUUID(message.uuid);
				TileEntity tileentity = message.network.getTileEntity();
				if (tileentity instanceof TEProcessingCenter) {
					TEProcessingCenter te = (TEProcessingCenter) tileentity;
					if (te.isUser(player)) {
						NetworkHandler.instance.sendTo(new MessageConnectNetworkCallback(true), player);
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
							stack.setTagCompound(tag);
						} else {
							TileEntity t = message.pos.getTileEntity();
							if (t instanceof INeedNetwork) {
								te.addNeedNetwork(message.pos, player);
							}
						}
						Connect c = ConnectionHelper.getConnect(player.getUniqueID());
						if (c != null) {
							c.network = te.getUUID();
						}
					} else {
						te.addUser(new UserData(player, false, true, true));
						NetworkHandler.instance.sendTo(new MessageConnectNetworkCallback(false), player);
					}
				}
			});
			return null;
		}
	}
}