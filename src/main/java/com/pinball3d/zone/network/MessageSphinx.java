package com.pinball3d.zone.network;

import java.util.UUID;

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

public abstract class MessageSphinx implements IMessage {
	WorldPos pos;
	NBTTagCompound tag;
	UUID uuid;
	int playerDim;

	public MessageSphinx() {

	}

	public MessageSphinx(EntityPlayer player, WorldPos pos, NBTTagCompound tag) {
		this.pos = pos;
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

	public abstract void run(MessageSphinx message, MessageContext ctx);

	public TEProcessingCenter getProcessingCenter() {
		TileEntity te = pos.getTileEntity();
		if (te instanceof TEProcessingCenter) {
			return (TEProcessingCenter) te;
		}
		return null;
	}

	public EntityPlayer getPlayer() {
		return FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(playerDim).getPlayerEntityByUUID(uuid);
	}

	public void doHandler(MessageSphinx message, MessageContext ctx) {
		FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
			if (doCheck()) {
				MessageSphinx.this.run(message, ctx);
			}
		});
	}

	public boolean doCheck() {
		TEProcessingCenter te = getProcessingCenter();
		return te != null && te.isUser(getPlayer());
	}
}
