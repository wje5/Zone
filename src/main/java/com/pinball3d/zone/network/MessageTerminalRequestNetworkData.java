package com.pinball3d.zone.network;

import java.util.UUID;

import com.pinball3d.zone.item.ItemLoader;
import com.pinball3d.zone.sphinx.GlobalNetworkData;
import com.pinball3d.zone.sphinx.WorldPos;
import com.pinball3d.zone.tileentity.TEProcessingCenter;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageTerminalRequestNetworkData implements IMessage {
	UUID uuid;
	String name, password;
	int dim;

	public MessageTerminalRequestNetworkData() {

	}

	public MessageTerminalRequestNetworkData(UUID uuid, EntityPlayer player, String password) {
		this.uuid = uuid;
		name = player.getName();
		this.password = password;
		dim = player.dimension;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		uuid = new UUID(buf.readLong(), buf.readLong());
		name = ByteBufUtils.readUTF8String(buf);
		password = ByteBufUtils.readUTF8String(buf);
		dim = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(uuid.getMostSignificantBits());
		buf.writeLong(uuid.getLeastSignificantBits());
		ByteBufUtils.writeUTF8String(buf, name);
		ByteBufUtils.writeUTF8String(buf, password);
		buf.writeInt(dim);
	}

	public static class Handler implements IMessageHandler<MessageTerminalRequestNetworkData, IMessage> {
		@Override
		public IMessage onMessage(MessageTerminalRequestNetworkData message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(new Runnable() {
				@Override
				public void run() {
					World world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(message.dim);
					WorldPos pos = GlobalNetworkData.getData(world).getNetwork(message.uuid);
					EntityPlayerMP player = (EntityPlayerMP) world.getPlayerEntityByName(message.name);
					if (pos != null) {
						TileEntity tileentity = pos.getTileEntity();
						if (tileentity instanceof TEProcessingCenter) {
							TEProcessingCenter te = (TEProcessingCenter) tileentity;
							if (te.isOn() && !te.needInit() && te.isDeviceInRange(new WorldPos(player))
									&& te.isCorrectLoginPassword(message.password)) {
								NetworkHandler.instance.sendTo(new MessageSendNetworkDataToTerminal(pos, message.uuid),
										player);
								return;
							}
						}
					}
					NetworkHandler.instance.sendTo(new MessageSendNetworkDataToTerminal(null, message.uuid), player);
					ItemStack stack = player.getHeldItemMainhand();
					if (stack.getItem() != ItemLoader.terminal) {
						stack = player.getHeldItemOffhand();
					}
					if (stack.getItem() == ItemLoader.terminal) {
						NBTTagCompound tag = stack.getTagCompound();
						if (tag == null) {
							tag = new NBTTagCompound();
							stack.setTagCompound(tag);
						}
						tag.removeTag("networkMost");
						tag.removeTag("networkLeast");
						tag.removeTag("password");
					}
				}
			});
			return null;
		}
	}
}
