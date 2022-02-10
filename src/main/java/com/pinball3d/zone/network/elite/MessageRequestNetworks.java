package com.pinball3d.zone.network.elite;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.pinball3d.zone.network.MessageZone;
import com.pinball3d.zone.network.NetworkHandler;
import com.pinball3d.zone.sphinx.SphinxUtil;
import com.pinball3d.zone.sphinx.elite.ScreenChooseNetwork;
import com.pinball3d.zone.tileentity.TETerminal;
import com.pinball3d.zone.util.Pair;
import com.pinball3d.zone.util.WorldPos;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageRequestNetworks extends MessageZone {
	private WorldPos terminalPos;

	public MessageRequestNetworks() {

	}

	public MessageRequestNetworks(WorldPos termianlPos) {
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
			if (((TETerminal) te).startWorking(player)) {
				NBTTagList list = SphinxUtil.getValidNetworkData(terminalPos, player, false);
				NetworkHandler.instance.sendTo(new PostBack(terminalPos, list), player);
			} else {
				System.out.println("Failed to Start");
			}
		}
	}

	public static class PostBack extends MessageZone {
		private NBTTagList list;
		private WorldPos terminalPos;

		public PostBack() {

		}

		public PostBack(WorldPos terminalPos, NBTTagList list) {
			this.terminalPos = terminalPos;
			this.list = list;
		}

		@Override
		public void fromBytes(ByteBuf buf) {
			super.fromBytes(buf);
			list = ByteBufUtils.readTag(buf).getTagList("list", 10);
			terminalPos = WorldPos.readFromByte(buf);
		}

		@Override
		public void toBytes(ByteBuf buf) {
			super.toBytes(buf);
			NBTTagCompound tag = new NBTTagCompound();
			tag.setTag("list", list);
			ByteBufUtils.writeTag(buf, tag);
			terminalPos.writeToByte(buf);
		}

		@Override
		public void run(MessageContext ctx) {
			List<Pair<UUID, String>> l = new ArrayList<Pair<UUID, String>>();
			list.forEach(e -> l.add(new Pair<UUID, String>((((NBTTagCompound) e).getUniqueId("uuid")),
					((NBTTagCompound) e).getString("name"))));
			Minecraft.getMinecraft().displayGuiScreen(new ScreenChooseNetwork(terminalPos, l));
		}
	}
}
