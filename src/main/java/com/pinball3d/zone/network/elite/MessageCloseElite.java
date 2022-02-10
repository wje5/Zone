package com.pinball3d.zone.network.elite;

import com.pinball3d.zone.network.MessageZone;
import com.pinball3d.zone.tileentity.TETerminal;
import com.pinball3d.zone.util.WorldPos;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageCloseElite extends MessageZone {
	private WorldPos terminalPos;

	public MessageCloseElite() {

	}

	public MessageCloseElite(WorldPos termianlPos) {
		this.terminalPos = termianlPos;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		super.fromBytes(buf);
		terminalPos = WorldPos.readFromByte(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		super.toBytes(buf);
		terminalPos.writeToByte(buf);
	}

	@Override
	public void run(MessageContext ctx) {
		EntityPlayerMP player = (EntityPlayerMP) getPlayer(ctx);
		TileEntity te = terminalPos.getTileEntity();
		if (te instanceof TETerminal) {
			((TETerminal) te).stopWorking(player.getUniqueID());
		}

	}
}
