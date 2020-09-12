package com.pinball3d.zone.sphinx;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class LogisticPack {
	public WorldPos target;
	public WorldPos next;
	public StorageWrapper items;
	public double x, y, z;
	public int dim;

	public LogisticPack(WorldPos target, StorageWrapper wrapper, WorldPos pos) {
		this(target, wrapper, pos.getPos().getX(), pos.getPos().getY(), pos.getPos().getZ(), pos.getDim());
	}

	public LogisticPack(WorldPos target, StorageWrapper wrapper, double x, double y, double z, int dim) {
		this.target = target;
		items = wrapper;
		this.x = x;
		this.y = y;
		this.z = z;
		this.dim = dim;
	}

	public LogisticPack(NBTTagCompound tag) {
		readFromNBT(tag);
	}

	public boolean forward(double distance) {
		if (tryForward(distance) > 0) {
			return true;
		}
		return false;
	}

	public double tryForward(double distance) {
		double xDistance = target.getPos().getX() - x;
		double yDistance = target.getPos().getY() - y;
		double zDistance = target.getPos().getZ() - z;
		double total = Math.sqrt(xDistance * xDistance + yDistance * yDistance + zDistance * zDistance);
		if (total < distance) {
			x = target.getPos().getX();
			y = target.getPos().getY();
			z = target.getPos().getZ();
			return distance - total;
		}
		double scale = distance / total;
		x += xDistance * scale;
		y += yDistance * scale;
		z += zDistance * scale;
		return 0;
	}

	public boolean check() {
		TileEntity te = target.getTileEntity();
		if (!(te instanceof IStorable) && !(te instanceof IDevice)) {
			return false;
		}
		return true;
	}

	public void readFromNBT(NBTTagCompound tag) {
		target = WorldPos.load(tag.getCompoundTag("target"));
		items = new StorageWrapper(tag.getCompoundTag("items"));
		x = tag.getDouble("x");
		y = tag.getDouble("y");
		z = tag.getDouble("z");
		dim = tag.getInteger("dim");
	}

	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag.setTag("target", target.writeToNBT(new NBTTagCompound()));
		tag.setTag("items", items.writeToNBT(new NBTTagCompound()));
		tag.setDouble("x", x);
		tag.setDouble("y", y);
		tag.setDouble("z", z);
		tag.setInteger("dim", dim);
		return tag;
	}
}
