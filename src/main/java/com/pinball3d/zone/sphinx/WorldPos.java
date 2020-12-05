package com.pinball3d.zone.sphinx;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WorldPos {
	public static final WorldPos ORIGIN = new WorldPos(0, 0, 0, 0);
	private BlockPos pos;
	private int dim;

	public WorldPos(int x, int y, int z, World world) {
		this(new BlockPos(x, y, z), world.provider.getDimension());
	}

	public WorldPos(int x, int y, int z, int dim) {
		this(new BlockPos(x, y, z), dim);
	}

	public WorldPos(BlockPos pos, World world) {
		this(pos, world.provider.getDimension());
	}

	public WorldPos(BlockPos pos, int dim) {
		this.pos = pos;
		this.dim = dim;
	}

	public WorldPos(TileEntity te) {
		this(te.getPos(), te.getWorld());
	}

	public WorldPos(Entity entity) {
		this(entity.getPosition(), entity.getEntityWorld());
	}

	public WorldPos(NBTTagCompound tag) {
		readFromNBT(tag);
	}

	public boolean isOrigin() {
		return equals(ORIGIN);
	}

	public World getWorld() {
		if (isOrigin()) {
			return null;
		}
		FMLCommonHandler handler = FMLCommonHandler.instance();
		if (handler.getEffectiveSide() == Side.SERVER) {
			return FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(dim);
		} else {
			return getClientWorld();
		}
	}

	@SideOnly(Side.CLIENT)
	private World getClientWorld() {
		if (isOrigin()) {
			return null;
		}
		World world = Minecraft.getMinecraft().world;
		if (world.provider.getDimension() == dim) {
			return Minecraft.getMinecraft().world;
		} else {
			throw new RuntimeException("DRRRRRRRRR!");
		}
	}

	public BlockPos getPos() {
		if (isOrigin()) {
			return BlockPos.ORIGIN;
		}
		return pos;
	}

	public int getDim() {
		return dim;
	}

	public IBlockState getBlockState() {
		if (isOrigin()) {
			return null;
		}
		return getWorld().getBlockState(pos);
	}

	public TileEntity getTileEntity() {
		if (isOrigin()) {
			return null;
		}
		return getWorld().getTileEntity(pos);
	}

	public void readFromNBT(NBTTagCompound tag) {
		pos = new BlockPos(tag.getInteger("x"), tag.getInteger("y"), tag.getInteger("z"));
		dim = tag.getInteger("dim");
	}

	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag.setInteger("x", pos.getX());
		tag.setInteger("y", pos.getY());
		tag.setInteger("z", pos.getZ());
		tag.setInteger("dim", dim);
		return tag;
	}

	public static WorldPos readFromByte(ByteBuf from) {
		return new WorldPos(BlockPos.fromLong(from.readLong()), from.readInt());
	}

	public void writeToByte(ByteBuf to) {
		to.writeLong(pos.toLong());
		to.writeInt(dim);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof WorldPos)) {
			return false;
		}
		WorldPos o = (WorldPos) obj;
		return o.dim == dim && o.pos.getX() == pos.getX() && o.pos.getY() == pos.getY() && o.pos.getZ() == pos.getZ();
	}

	@Override
	public int hashCode() {
		int result = dim;
		result = result * 31 + pos.getX();
		result = result * 31 + pos.getY();
		result = result * 31 + pos.getZ();
		return result;
	}

	@Override
	public String toString() {
		return "dim:" + dim + " pos:" + pos;
	}

	public int compare(WorldPos p) {
		return dim > p.dim ? 1 : p.dim > dim ? -1 : pos.compareTo(p.getPos());
	}
}
