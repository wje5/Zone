package com.pinball3d.zone.network.elite;

import com.pinball3d.zone.network.MessageZone;
import com.pinball3d.zone.tileentity.TECableGeneral;
import com.pinball3d.zone.tileentity.TECableGeneral.CableConfig;
import com.pinball3d.zone.tileentity.TECableGeneral.CableConfig.ItemIOType;
import com.pinball3d.zone.util.WorldPos;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageCableConfig extends MessageZone {
	private WorldPos cablePos;
	private EnumFacing facing;
	private boolean enableEnergyTransmit;
	private ItemIOType itemIOType;

	public MessageCableConfig() {

	}

	public MessageCableConfig(WorldPos cablePos, EnumFacing facing, boolean enableEnergyTransmit,
			ItemIOType itemIOType) {
		this.cablePos = cablePos;
		this.facing = facing;
		this.enableEnergyTransmit = enableEnergyTransmit;
		this.itemIOType = itemIOType;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		super.fromBytes(buf);
		cablePos = WorldPos.readFromByte(buf);
		facing = EnumFacing.values()[buf.readInt()];
		enableEnergyTransmit = buf.readBoolean();
		itemIOType = ItemIOType.values()[buf.readInt()];
	}

	@Override
	public void toBytes(ByteBuf buf) {
		super.toBytes(buf);
		cablePos.writeToByte(buf);
		buf.writeInt(facing.ordinal());
		buf.writeBoolean(enableEnergyTransmit);
		buf.writeInt(itemIOType.ordinal());
	}

	@Override
	public void run(MessageContext ctx) {
		EntityPlayerMP player = (EntityPlayerMP) getPlayer(ctx);
		TileEntity te = cablePos.getTileEntity();
		if (te instanceof TECableGeneral) {
			CableConfig config = ((TECableGeneral) te).getConfig(facing);
			config.setEnergyTransmit(enableEnergyTransmit);
			config.setItemIOType(itemIOType);
			te.markDirty();
			System.out.println(te + "|" + cablePos + "|" + enableEnergyTransmit + "|" + itemIOType);
		}
	}
}
