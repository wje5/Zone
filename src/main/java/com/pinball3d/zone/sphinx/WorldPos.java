package com.pinball3d.zone.sphinx;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

public class WorldPos {
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

	public World getWorld() {
		FMLCommonHandler handler = FMLCommonHandler.instance();
		if (handler.getEffectiveSide() == Side.SERVER) {
			return handler.getMinecraftServerInstance().getWorld(dim);
		} else {
			World world = Minecraft.getMinecraft().world;
			if (world.provider.getDimension() == dim) {
				return Minecraft.getMinecraft().world;
			} else {
				throw new RuntimeException("DRRRRRRRRR!");
			}
		}
	}

	public BlockPos getPos() {
		return pos;
	}

	public int getDim() {
		return dim;
	}

	public IBlockState getBlockState() {
		return getWorld().getBlockState(pos);
	}

	public TileEntity getTileEntity() {
		return getWorld().getTileEntity(pos);
	}

	public static WorldPos readFromByte(ByteBuf from) {
		return new WorldPos(from.readInt(), from.readInt(), from.readInt(), from.readInt());
	}

	public static WorldPos load(NBTTagCompound tag) {
		if (tag.hasKey("worldpos")) {
			NBTTagCompound subtag = tag.getCompoundTag("worldpos");
			return new WorldPos(subtag.getInteger("x"), subtag.getInteger("y"), subtag.getInteger("z"),
					subtag.getInteger("dim"));
		}
		return null;
	}

	public NBTTagCompound save(NBTTagCompound tag) {
		NBTTagCompound subtag = new NBTTagCompound();
		subtag.setInteger("x", pos.getX());
		subtag.setInteger("y", pos.getY());
		subtag.setInteger("z", pos.getZ());
		subtag.setInteger("dim", dim);
		tag.setTag("worldpos", subtag);
		return tag;
	}

	public void writeToByte(ByteBuf to) {
		to.writeInt(pos.getX());
		to.writeInt(pos.getY());
		to.writeInt(pos.getZ());
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
		int result = super.hashCode();
		result = result * 31 + pos.getX();
		result = result * 31 + pos.getY();
		result = result * 31 + pos.getZ();
		result = result * 31 + dim;
		return result;
	}

	@Override
	public String toString() {
		return "dim:" + dim + " pos:" + pos;
	}
}
