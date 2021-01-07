package com.pinball3d.zone.sphinx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.pinball3d.zone.block.BlockLoader;
import com.pinball3d.zone.tileentity.INeedNetwork;
import com.pinball3d.zone.tileentity.INeedNetwork.WorkingState;
import com.pinball3d.zone.util.StorageWrapper;
import com.pinball3d.zone.util.WorldPos;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

public class LogisticPack {
	public List<WorldPos> routes;
	public StorageWrapper items;
	public double x, y, z;
	public int dim;

	public LogisticPack(List<WorldPos> path, StorageWrapper wrapper, WorldPos pos) {
		this(path, wrapper, pos.getPos().getX(), pos.getPos().getY(), pos.getPos().getZ(), pos.getDim());
	}

	public LogisticPack(List<WorldPos> path, StorageWrapper wrapper, double x, double y, double z, int dim) {
		this.routes = path;
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
		while (distance > 0 && !routes.isEmpty()) {
			WorldPos next = routes.get(0);
			double xDistance = next.getPos().getX() - x;
			double yDistance = next.getPos().getY() - y;
			double zDistance = next.getPos().getZ() - z;
			double total = Math.sqrt(xDistance * xDistance + yDistance * yDistance + zDistance * zDistance);
			if (total < distance) {
				x = next.getPos().getX();
				y = next.getPos().getY();
				z = next.getPos().getZ();
				distance -= total;
				routes.remove(0);
				while (!routes.isEmpty()) {
					Block block = next.getBlockState().getBlock();
					TileEntity te = next.getTileEntity();
					next = routes.get(0);
					Block block2 = next.getBlockState().getBlock();
					TileEntity te2 = next.getTileEntity();
					if ((block == BlockLoader.processing_center_light || (block == BlockLoader.beacon_core
							&& ((INeedNetwork) te).getWorkingState() == WorkingState.WORKING))
							&& (block2 == BlockLoader.processing_center_light || (block2 == BlockLoader.beacon_core
									&& ((INeedNetwork) te2).getWorkingState() == WorkingState.WORKING))) {
						x = next.getPos().getX();
						y = next.getPos().getY();
						z = next.getPos().getZ();
						dim = next.getDim();
						routes.remove(0);
					} else {
						break;
					}
				}
			} else {
				double scale = distance / total;
				x += xDistance * scale;
				y += yDistance * scale;
				z += zDistance * scale;
				return false;
			}
		}
		return true;
	}

	public WorldPos getTarget() {
		return routes.isEmpty() ? new WorldPos((int) x, (int) y, (int) z, dim) : routes.get(routes.size() - 1);
	}

	public void readFromNBT(NBTTagCompound tag) {
		routes = new ArrayList<WorldPos>();
		NBTTagList list = tag.getTagList("routes", 10);
		list.forEach(e -> {
			routes.add(new WorldPos((NBTTagCompound) e));
		});
		items = new StorageWrapper(tag.getCompoundTag("items"));
		x = tag.getDouble("x");
		y = tag.getDouble("y");
		z = tag.getDouble("z");
		dim = tag.getInteger("dim");
	}

	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		NBTTagList list = new NBTTagList();
		routes.forEach(e -> {
			list.appendTag(e.writeToNBT(new NBTTagCompound()));
		});
		tag.setTag("items", items.writeToNBT(new NBTTagCompound()));
		tag.setDouble("x", x);
		tag.setDouble("y", y);
		tag.setDouble("z", z);
		tag.setInteger("dim", dim);
		return tag;
	}

	@Override
	public String toString() {
		return writeToNBT(new NBTTagCompound()).toString();
	}

	public static class Path {
		public List<WorldPos> routes = new ArrayList<WorldPos>();
		public double distance;

		public Path(List<WorldPos> path, double dist) {
			routes = path;
			distance = dist;
		}

		public WorldPos getTarget() {
			return routes.get(routes.size() - 1);
		}

		public Path flip() {
			Collections.reverse(routes);
			return this;
		}

		public Path copy() {
			List<WorldPos> l = new ArrayList<WorldPos>();
			routes.forEach(e -> l.add(e.copy()));
			return new Path(l, distance);
		}

		@Override
		public String toString() {
			return routes + "|" + distance;
		}
	}
}
