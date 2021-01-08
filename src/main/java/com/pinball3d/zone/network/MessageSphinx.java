package com.pinball3d.zone.network;

import java.util.UUID;

import com.pinball3d.zone.tileentity.TEProcessingCenter;
import com.pinball3d.zone.util.WorldPos;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
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

	public TEProcessingCenter getTileEntity() {
		TileEntity te = pos.getTileEntity();
		if (te instanceof TEProcessingCenter) {
			return (TEProcessingCenter) te;
		}
		return null;
	}

	public void doHandler(MessageContext ctx) {
		FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
			if (doCheck(MessageSphinx.this)) {
				MessageSphinx.this.run(ctx);
			}
		});
	}

	public boolean doCheck(MessageSphinx message) {
		World world = message.pos.getWorld();
		if (!world.isAreaLoaded(message.pos.getPos(), 5)) {
			return false;
		}
		TileEntity tileentity = message.pos.getTileEntity();
		if (tileentity instanceof TEProcessingCenter) {
			TEProcessingCenter te = (TEProcessingCenter) tileentity;
			EntityPlayer player = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(message.playerDim)
					.getPlayerEntityByUUID(uuid);
//			if (te.isCorrectLoginPassword(message.password)) {TODO
			return true;
		}
		return false;
	}
}
