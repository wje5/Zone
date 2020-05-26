package com.pinball3d.zone.network;

import com.pinball3d.zone.sphinx.WorldPos;
import com.pinball3d.zone.tileentity.TEProcessingCenter;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public abstract class MessageSphinx implements IMessage {
	String password;
	WorldPos pos;
	NBTTagCompound tag;

	public MessageSphinx() {

	}

	public MessageSphinx(String password, WorldPos pos, NBTTagCompound tag) {
		this.password = password;
		this.pos = pos;
		this.tag = tag;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		password = ByteBufUtils.readUTF8String(buf);
		pos = WorldPos.readFromByte(buf);
		tag = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, password);
		pos.writeToByte(buf);
		ByteBufUtils.writeTag(buf, tag);
	}

	public abstract void run(MessageContext ctx);

	public TEProcessingCenter getTileEntity() {
		TileEntity te = pos.getTileEntity(FMLCommonHandler.instance().getMinecraftServerInstance());
		if (te instanceof TEProcessingCenter) {
			return (TEProcessingCenter) te;
		}
		return null;
	}

	public void doHandler(MessageContext ctx) {
		FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(new Runnable() {
			@Override
			public void run() {
				if (doCheck(MessageSphinx.this)) {
					MessageSphinx.this.run(ctx);
				}
			}
		});
	}

	public boolean doCheck(MessageSphinx message) {
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		World world = message.pos.getWorld(server);
		if (!world.isAreaLoaded(message.pos.getPos(), 5)) {
			return false;
		}
		TileEntity tileentity = message.pos.getTileEntity(FMLCommonHandler.instance().getMinecraftServerInstance());
		if (tileentity instanceof TEProcessingCenter) {
			TEProcessingCenter te = (TEProcessingCenter) tileentity;
			if (te.isCorrectAdminPassword(message.password)) {
				return true;
			}
		}
		return false;
	}
}
