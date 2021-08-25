package com.pinball3d.zone.network.elite;

import java.util.ArrayList;
import java.util.List;

import com.pinball3d.zone.network.MessageZone;
import com.pinball3d.zone.network.NetworkHandler;
import com.pinball3d.zone.sphinx.SphinxUtil;
import com.pinball3d.zone.sphinx.elite.ScreenChooseNetwork;
import com.pinball3d.zone.util.Pair;
import com.pinball3d.zone.util.WorldPos;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
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
		NBTTagList list = SphinxUtil.getValidNetworkData(terminalPos, player, false);
		NetworkHandler.instance.sendTo(new PostBack(list), player);
	}

	public static class PostBack extends MessageZone {
		private NBTTagList list;

		public PostBack() {

		}

		public PostBack(NBTTagList list) {
			this.list = list;
		}

		@Override
		public void fromBytes(ByteBuf buf) {
			super.fromBytes(buf);
			list = ByteBufUtils.readTag(buf).getTagList("list", 10);
		}

		@Override
		public void toBytes(ByteBuf buf) {
			super.toBytes(buf);
			NBTTagCompound tag = new NBTTagCompound();
			tag.setTag("list", list);
			ByteBufUtils.writeTag(buf, tag);
		}

		@Override
		public void run(MessageContext ctx) {
			List<Pair<String, WorldPos>> l = new ArrayList<Pair<String, WorldPos>>();
			list.forEach(e -> l.add(new Pair<String, WorldPos>(((NBTTagCompound) e).getString("name"),
					new WorldPos((NBTTagCompound) e))));
			Minecraft.getMinecraft().displayGuiScreen(new ScreenChooseNetwork(l));
		}
	}
}
