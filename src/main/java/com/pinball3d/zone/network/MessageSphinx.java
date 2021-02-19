package com.pinball3d.zone.network;

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

	public MessageSphinx() {

	}

	public MessageSphinx(WorldPos pos, NBTTagCompound tag) {
		this.pos = pos;
		this.tag = tag;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		pos = WorldPos.readFromByte(buf);
		tag = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		pos.writeToByte(buf);
		ByteBufUtils.writeTag(buf, tag);
	}

	public abstract void run(MessageContext ctx);

	public TEProcessingCenter getProcessingCenter() {
		TileEntity te = pos.getTileEntity();
		if (te instanceof TEProcessingCenter) {
			return (TEProcessingCenter) te;
		}
		return null;
	}

	public EntityPlayer getPlayer(MessageContext ctx) {
		return ctx.getServerHandler().player;
	}

	public void doHandler(MessageContext ctx) {
		FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
			if (doCheck(ctx)) {
				MessageSphinx.this.run(ctx);
			}
		});
	}

	public boolean doCheck(MessageContext ctx) {
		TEProcessingCenter te = getProcessingCenter();
		return te != null && te.isUser(getPlayer(ctx));
	}
}
