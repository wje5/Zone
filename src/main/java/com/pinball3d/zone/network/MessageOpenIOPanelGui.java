package com.pinball3d.zone.network;

import java.util.UUID;

import com.pinball3d.zone.Zone;
import com.pinball3d.zone.inventory.GuiElementLoader;
import com.pinball3d.zone.sphinx.GlobalNetworkData;
import com.pinball3d.zone.tileentity.TEIOPanel;
import com.pinball3d.zone.tileentity.TEProcessingCenter;
import com.pinball3d.zone.util.WorldPos;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageOpenIOPanelGui implements IMessage {
	UUID uuid;
	int world, x, y, z;
	boolean flag;

	public MessageOpenIOPanelGui() {

	}

	public MessageOpenIOPanelGui(EntityPlayer player, int x, int y, int z, boolean flag) {
		uuid = player.getUniqueID();
		world = player.world.provider.getDimension();
		this.x = x;
		this.y = y;
		this.z = z;
		this.flag = flag;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		uuid = new UUID(buf.readLong(), buf.readLong());
		world = buf.readInt();
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		flag = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(uuid.getMostSignificantBits());
		buf.writeLong(uuid.getLeastSignificantBits());
		buf.writeInt(world);
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeBoolean(flag);
	}

	public static class Handler implements IMessageHandler<MessageOpenIOPanelGui, IMessage> {
		@Override
		public IMessage onMessage(MessageOpenIOPanelGui message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
				MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
				World world = server.getWorld(message.world);
				EntityPlayer player = world.getPlayerEntityByUUID(message.uuid);
				if (player != null) {
					TileEntity tileentity = world.getTileEntity(new BlockPos(message.x, message.y, message.z));
					if (tileentity instanceof TEIOPanel) {
						UUID network = ((TEIOPanel) tileentity).getNetwork();
						boolean flag = false;
						if (network == null) {
							flag = true;
						} else {
							WorldPos pos = GlobalNetworkData.getPos(network);
							if (pos.isOrigin()) {
								flag = true;
							} else {
								TEProcessingCenter te = (TEProcessingCenter) pos.getTileEntity();
								flag = te.isUser(player);
							}
						}
						if (flag) {
							player.openGui(Zone.instance,
									message.flag ? GuiElementLoader.SPHINX_IO_PANEL
											: GuiElementLoader.SPHINX_NEED_NETWORK_IO_PANEL,
									world, message.x, message.y, message.z);
						}
					}
				}
			});
			return null;
		}
	}
}
