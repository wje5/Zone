package com.pinball3d.zone.network;

import com.pinball3d.zone.entity.EntityBullet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageBullet implements IMessage {
	String name;
	int world;
	double x, y, z, motionX, motionY, motionZ;

	public MessageBullet() {

	}

	public MessageBullet(EntityPlayer player, double x, double y, double z, double motionX, double motionY,
			double motionZ) {
		name = player.getName();
		world = player.world.provider.getDimension();
		this.x = x;
		this.y = y;
		this.z = z;
		this.motionX = motionX;
		this.motionY = motionY;
		this.motionZ = motionZ;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		name = ByteBufUtils.readUTF8String(buf);
		world = buf.readInt();
		x = buf.readDouble();
		y = buf.readDouble();
		z = buf.readDouble();
		motionX = buf.readDouble();
		motionY = buf.readDouble();
		motionZ = buf.readDouble();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, name);
		buf.writeInt(world);
		buf.writeDouble(x);
		buf.writeDouble(y);
		buf.writeDouble(z);
		buf.writeDouble(motionX);
		buf.writeDouble(motionY);
		buf.writeDouble(motionZ);
	}

	public static class Handler implements IMessageHandler<MessageBullet, IMessage> {
		@Override
		public IMessage onMessage(MessageBullet message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(new Runnable() {
				@Override
				public void run() {
					MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
					World world = server.getWorld(message.world);
					if (!world.isAreaLoaded(new BlockPos(message.x, message.y, message.z), 3)) {
						return;
					}
					EntityBullet bullet = new EntityBullet(world, world.getPlayerEntityByName(message.name), 0, 0, 0);
					bullet.posX = message.x;
					bullet.posY = message.y;
					bullet.posZ = message.z;
					bullet.motionX = message.motionX;
					bullet.motionY = message.motionY;
					bullet.motionZ = message.motionZ;
					world.spawnEntity(bullet);
				}
			});

			return null;
		}
	}
}
