package com.pinball3d.zone.network;

import com.pinball3d.zone.SoundUtil;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessagePlaySoundAtPos implements IMessage {
	int x, y, z, id;

	public MessagePlaySoundAtPos() {

	}

	public MessagePlaySoundAtPos(BlockPos pos, int id) {
		x = pos.getX();
		y = pos.getY();
		z = pos.getZ();
		this.id = id;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		id = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeInt(id);
	}

	public static class Handler implements IMessageHandler<MessagePlaySoundAtPos, IMessage> {
		@Override
		public IMessage onMessage(MessagePlaySoundAtPos message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
				World world = Minecraft.getMinecraft().world;
				BlockPos pos = new BlockPos(message.x, message.y, message.z);
				if (!world.isAreaLoaded(pos, 16)) {
					return;
				}
				world.playSound(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F,
						SoundUtil.getSoundEventFromId(message.id), SoundCategory.BLOCKS, 1.0F, 1.0F, false);
			});
			return null;
		}
	}
}
