package com.pinball3d.zone.network;

import java.util.UUID;

import com.pinball3d.zone.sphinx.GlobalNetworkData;
import com.pinball3d.zone.sphinx.INeedNetwork;
import com.pinball3d.zone.tileentity.TEProcessingCenter;
import com.pinball3d.zone.util.WorldPos;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public abstract class MessageSphinxNeedNetwork implements IMessage {
	WorldPos pos;
	NBTTagCompound tag;
	UUID uuid;
	int playerDim;

	public MessageSphinxNeedNetwork() {

	}

	public MessageSphinxNeedNetwork(EntityPlayer player, WorldPos needNetwork, NBTTagCompound tag) {
		this.pos = needNetwork;
		this.tag = tag;
		uuid = player.getUniqueID();
		playerDim = player.dimension;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		pos = WorldPos.readFromByte(buf);
		tag = ByteBufUtils.readTag(buf);
		uuid = new UUID(buf.readLong(), buf.readLong());
		playerDim = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		pos.writeToByte(buf);
		ByteBufUtils.writeTag(buf, tag);
		buf.writeLong(uuid.getMostSignificantBits());
		buf.writeLong(uuid.getLeastSignificantBits());
		buf.writeInt(playerDim);
	}

	public abstract void run(MessageContext ctx);

	public TEProcessingCenter getProcessingCenter() {
		TileEntity tileentity = pos.getTileEntity();
		if (tileentity instanceof INeedNetwork) {
			INeedNetwork needNetwork = (INeedNetwork) tileentity;
			UUID network = needNetwork.getNetwork();
			WorldPos pcpos = GlobalNetworkData.getPos(network);
			if (!pcpos.isOrigin()) {
				return (TEProcessingCenter) pcpos.getTileEntity();
			}
		}
		return null;
	}

	public EntityPlayer getPlayer() {
		return FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(playerDim).getPlayerEntityByUUID(uuid);
	}

	public void doHandler(MessageContext ctx) {
		FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
			if (doCheck()) {
				MessageSphinxNeedNetwork.this.run(ctx);
			}
		});
	}

	public boolean doCheck() {
		TEProcessingCenter te = getProcessingCenter();
		return te != null && te.isUser(getPlayer());
	}
}
