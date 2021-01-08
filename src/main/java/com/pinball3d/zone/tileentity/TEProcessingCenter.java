package com.pinball3d.zone.tileentity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import com.pinball3d.zone.ChunkHandler;
import com.pinball3d.zone.ChunkHandler.IChunkLoader;
import com.pinball3d.zone.block.BlockLoader;
import com.pinball3d.zone.block.BlockProcessingCenter;
import com.pinball3d.zone.sphinx.ClassifyGroup;
import com.pinball3d.zone.sphinx.GlobalNetworkData;
import com.pinball3d.zone.sphinx.IDevice;
import com.pinball3d.zone.sphinx.INode;
import com.pinball3d.zone.sphinx.IProduction;
import com.pinball3d.zone.sphinx.IStorable;
import com.pinball3d.zone.sphinx.LogisticPack;
import com.pinball3d.zone.sphinx.LogisticPack.Path;
import com.pinball3d.zone.util.HugeItemStack;
import com.pinball3d.zone.util.StorageWrapper;
import com.pinball3d.zone.util.WorldPos;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class TEProcessingCenter extends TileEntity implements ITickable, IChunkLoader {
	private boolean on;
	private String name = "";
	private int loadTick;
	private int energyTick;
	private int energy;
	private boolean loaded;
	public static Comparator<WorldPos> worldPosComparator = new Comparator<WorldPos>() {
		@Override
		public int compare(WorldPos o1, WorldPos o2) {
			return o1.getDim() < o2.getDim() ? -1
					: o1.getDim() > o2.getDim() ? 1
							: o1.getPos().getX() < o2.getPos().getX() ? -1
									: o1.getPos().getX() > o2.getPos().getX() ? 1
											: o1.getPos().getY() < o2.getPos().getY() ? -1
													: o1.getPos().getY() > o2.getPos().getY() ? 1
															: o1.getPos().getZ() < o2.getPos().getZ() ? -1
																	: o1.getPos().getZ() > o2.getPos().getZ() ? 1 : 0;
		}
	};
	private Set<WorldPos> nodes = new TreeSet<WorldPos>(worldPosComparator);
	private Set<WorldPos> storages = new TreeSet<WorldPos>(worldPosComparator);
	private Set<WorldPos> devices = new TreeSet<WorldPos>(worldPosComparator);
	private Set<WorldPos> productions = new TreeSet<WorldPos>(worldPosComparator);
	private Set<LogisticPack> packs = new HashSet<LogisticPack>();
	private UUID uuid;
	private double[][] map;
	private List<ClassifyGroup> classifyGroups = new ArrayList<ClassifyGroup>();
	private boolean mapDirty;
	private Map<WorldPos, List<Path>> dijkstraCache = new HashMap<WorldPos, List<Path>>();

	public TEProcessingCenter() {

	}

	public boolean isOn() {
		return on;
	}

	public void callUpdate() {
		markDirty();
//		IBlockState state = getBlockType().getStateFromMeta(getBlockMetadata());
//		world.notifyBlockUpdate(pos, state, state,
//				Constants.BlockFlags.SEND_TO_CLIENTS | Constants.BlockFlags.NO_RERENDER);
	}

	public void shutdown() {
		on = false;
		BlockProcessingCenter.setState(false, world, pos);
		loadTick = 0;
		energyTick = 0;
		callUpdate();
	}

	public String getName() {
		return name;
	}

	public boolean isLoading() {
		return loadTick > 0;
	}

	public int getLoadTick() {
		return loadTick;
	}

	public void open() {
		if (!on && loadTick <= 0) {
			loadTick = 256;
			BlockProcessingCenter.setState(true, world, pos);
			markDirty();
			callUpdate();
		}
	}

	public void setName(String name) {
		if (name.length() >= 4) {
			this.name = name;
			callUpdate();
			markDirty();
		}
	}

	public void addNeedNetwork(WorldPos pos, EntityPlayer player) {
		// TODO check player permission
		nodes.forEach(e -> {
			if (e.equals(pos)) {
				return;
			}
		});
		storages.forEach(e -> {
			if (e.equals(pos)) {
				return;
			}
		});
		devices.forEach(e -> {
			if (e.equals(pos)) {
				return;
			}
		});
		productions.forEach(e -> {
			if (e.equals(pos)) {
				return;
			}
		});
		TileEntity te = pos.getTileEntity();
		if (te instanceof INode) {
			nodes.add(pos);
			((INeedNetwork) te).connect(getUUID());
			((INeedNetwork) te).setWorldPos(new WorldPos(this), getUUID());
		} else if (te instanceof IStorable) {
			storages.add(pos);
			((INeedNetwork) te).connect(getUUID());
			((INeedNetwork) te).setWorldPos(new WorldPos(this), getUUID());
		} else if (te instanceof IDevice) {
			devices.add(pos);
			((INeedNetwork) te).connect(getUUID());
			((INeedNetwork) te).setWorldPos(new WorldPos(this), getUUID());
		} else if (te instanceof IProduction) {
			productions.add(pos);
			((INeedNetwork) te).connect(getUUID());
			((INeedNetwork) te).setWorldPos(new WorldPos(this), getUUID());
		}
		if (!world.isRemote) {
			refreshMap();
		}
		callUpdate();
		markDirty();
	}

	public void removeNeedNetwork(WorldPos pos) {
		nodes.remove(pos);
		storages.remove(pos);
		devices.remove(pos);
		productions.remove(pos);
		if (!world.isRemote) {
			refreshMap();
		}
		callUpdate();
		markDirty();
	}

	public Set<WorldPos> getNodes() {
		return nodes;
	}

	public Set<WorldPos> getStorages() {
		return storages;
	}

	public Set<WorldPos> getDevices() {
		return devices;
	}

	public Set<WorldPos> getProductions() {
		return productions;
	}

	public Set<LogisticPack> getPacks() {
		return packs;
	}

	public UUID getUUID() {
		return uuid;
	}

	public void setUUID(UUID uuid) {
		this.uuid = uuid;
		markDirty();
		callUpdate();
	}

	public boolean isPointInRange(int dim, double x, double y, double z) {
		if (Math.sqrt(pos.distanceSq(x, y, z)) < 25) {
			return true;
		}
		Iterator<WorldPos> it = nodes.iterator();
		while (it.hasNext()) {
			TileEntity te = it.next().getTileEntity();
			if (te instanceof INode) {
				if (((INeedNetwork) te).isConnected() && ((INode) te).isPointInRange(dim, x, y, z)) {
					return true;
				}
			} else {
				it.remove();
			}
		}
		return false;
	}

	public boolean isDeviceInRange(WorldPos pos) {
		if (pos.getTileEntity() instanceof TEBeaconCore) {
			return true;
		}
		if (Math.sqrt(this.pos.distanceSq(pos.getPos().getX(), pos.getPos().getY(), pos.getPos().getZ())) < 25) {
			return true;
		}
		Iterator<WorldPos> it = nodes.iterator();
		while (it.hasNext()) {
			WorldPos p = it.next();
			TileEntity tileentity = p.getTileEntity();
			if (tileentity instanceof INode) {
				INeedNetwork te = (INeedNetwork) tileentity;
				if (!p.equals(pos) && te.isConnected() && te.getWorkingState() == INeedNetwork.WorkingState.WORKING
						&& ((INode) te).isPointInRange(pos.getDim(), pos.getPos().getX(), pos.getPos().getY(),
								pos.getPos().getZ())) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean consumeEnergy(int amount) {
		BlockPos p = pos.add(0, -3, 0);
		if (world.getBlockState(p).getBlock() == BlockLoader.transmission_module) {
			TETransmissionModule te = (TETransmissionModule) world.getTileEntity(p);
			boolean flag = te.tryUseEnergy(amount, false);
			energy = te.getEnergy();
			callUpdate();
			return flag;
		} else {
			energy = 0;
			callUpdate();
			return false;
		}
	}

	public StorageWrapper getNetworkUseableItems() {
		StorageWrapper wrapper = new StorageWrapper();
		storages.forEach(e -> {
			TileEntity te = e.getTileEntity();
			if (te instanceof INeedNetwork && ((INeedNetwork) te).isConnected() && te instanceof IStorable) {
				StorageWrapper w = ((IStorable) te).getStorages();
				wrapper.merge(w);
			}
		});
		return wrapper;
	}

	public int requestItems(StorageWrapper wrapper, WorldPos target, boolean isSimulate) {
		TreeSet<WorldPos> sortset = new TreeSet<WorldPos>(new Comparator<WorldPos>() {
			@Override
			public int compare(WorldPos o1, WorldPos o2) {
				double dist1 = o1.getPos().distanceSq(target.getPos());
				double dist2 = o2.getPos().distanceSq(target.getPos());
				return dist1 > dist2 ? 1 : dist1 < dist2 ? -1 : o1.hashCode() > o2.hashCode() ? 1 : -1;
			}
		});
		sortset.addAll(storages);
		int time = 0;
		Iterator<WorldPos> it = sortset.iterator();
		while (it.hasNext()) {
			WorldPos e = it.next();
			TileEntity te = e.getTileEntity();
			if (te instanceof INeedNetwork && ((INeedNetwork) te).isConnected() && te instanceof IStorable) {
				StorageWrapper w = ((IStorable) te).extract(wrapper, isSimulate);
				if (!w.isEmpty()) {
					List<Path> l = dijkstra(target);
					for (Path i : l) {
						if (i.getTarget().equals(e)) {
							if (!isSimulate) {
								LogisticPack pack = new LogisticPack(i.flip().routes, w, new WorldPos(te));
								packs.add(pack);
							}
							time = (int) (time < i.distance ? i.distance : time);
						}
					}
				}
			}
		}
		callUpdate();
		return time;
	}

	public StorageWrapper dispenceItems(StorageWrapper wrapper, WorldPos pos) {
		TreeSet<WorldPos> sortset = new TreeSet<WorldPos>(new Comparator<WorldPos>() {
			@Override
			public int compare(WorldPos o1, WorldPos o2) {
				double dist1 = o1.getPos().distanceSq(pos.getPos());
				double dist2 = o2.getPos().distanceSq(pos.getPos());
				return dist1 > dist2 ? 1 : dist1 < dist2 ? -1 : o1.hashCode() > o2.hashCode() ? 1 : -1;
			}
		});
		sortset.addAll(storages);
		sortset.forEach(e -> {
			TileEntity te = e.getTileEntity();
			if (te instanceof INeedNetwork && ((INeedNetwork) te).isConnected() && te instanceof IStorable) {
				StorageWrapper w = ((IStorable) te).insert(wrapper, true);
				if (!w.isEmpty()) {
					List<Path> l = dijkstra(pos);
					for (Path i : l) {
						if (i.getTarget().equals(e)) {
							LogisticPack pack = new LogisticPack(i.routes, w, pos);
							packs.add(pack);
						}
					}
				}
			}
		});
		callUpdate();
		return wrapper;
	}

	public boolean updateNode() {
		boolean flag = false;
		Set<WorldPos> temp = new HashSet<WorldPos>();
		do {
			flag = false;
			Iterator<WorldPos> it = nodes.iterator();
			while (it.hasNext()) {
				WorldPos i = it.next();
				if (temp.contains(i)) {
					continue;
				}
				TileEntity tileentity = i.getTileEntity();
				if (i.getBlockState().getBlock() == BlockLoader.beacon_core) {
					flag = true;
					temp.add(i);
					((INeedNetwork) tileentity).setConnected(true);
					continue;
				}
				if (i == null || !(tileentity instanceof INode)) {
					continue;
				}
				INeedNetwork te = (INeedNetwork) tileentity;
				if (i.getDim() == world.provider.getDimension()) {
					if (Math.sqrt(pos.distanceSq(i.getPos().getX(), i.getPos().getY(), i.getPos().getZ())) < 25) {
						flag = true;
						temp.add(i);
						te.setConnected(true);
					} else {
						Iterator<WorldPos> it2 = temp.iterator();
						while (it2.hasNext()) {
							WorldPos nodepos = it2.next();
							TileEntity nodete = nodepos.getTileEntity();
							if (((INeedNetwork) nodete).getWorkingState() == INeedNetwork.WorkingState.WORKING
									&& ((INode) nodete).isPointInRange(i.getDim(), i.getPos().getX(), i.getPos().getY(),
											i.getPos().getZ())) {
								flag = true;
								temp.add(i);
								te.setConnected(true);
								break;
							}
						}
					}
				}
			}
		} while (flag);
		Set<WorldPos> l = new HashSet<WorldPos>();
		nodes.forEach(e -> {
			l.add(e);
		});
		Set<WorldPos> s = new HashSet<WorldPos>();
		l.forEach(e -> {
			TileEntity tileentity = e.getTileEntity();
			if (tileentity instanceof INode && getUUID().equals(((INeedNetwork) tileentity).getNetwork())) {
				s.add(e);
				((INeedNetwork) tileentity).setConnected(temp.contains(e));
			}
		});
		if (nodes.equals(s)) {
			return false;
		} else {
			nodes = s;
			markDirty();
			callUpdate();
			return true;
		}
	}

	public boolean updateDevice() {
		boolean flag = updateNode();
		Iterator<WorldPos> it = storages.iterator();
		while (it.hasNext()) {
			WorldPos pos = it.next();
			if (!(pos.getTileEntity() instanceof IStorable)) {
				it.remove();
				flag = true;
			} else if (!isDeviceInRange(pos)) {
				INeedNetwork te = (INeedNetwork) pos.getTileEntity();
				flag |= te.isConnected();
				te.setConnected(false);
			} else {
				INeedNetwork te = (INeedNetwork) pos.getTileEntity();
				flag |= !te.isConnected();
				te.setConnected(true);
			}
		}
		it = devices.iterator();
		while (it.hasNext()) {
			WorldPos pos = it.next();
			if (!(pos.getTileEntity() instanceof IDevice)) {
				it.remove();
				flag = true;
			} else if (!isDeviceInRange(pos)) {
				INeedNetwork te = (INeedNetwork) pos.getTileEntity();
				flag |= te.isConnected();
				te.setConnected(false);
			} else {
				INeedNetwork te = (INeedNetwork) pos.getTileEntity();
				flag |= !te.isConnected();
				te.setConnected(true);
			}
		}
		it = productions.iterator();
		while (it.hasNext()) {
			WorldPos pos = it.next();
			if (!(pos.getTileEntity() instanceof IProduction)) {
				it.remove();
				flag = true;
			} else if (!isDeviceInRange(pos)) {
				INeedNetwork te = (INeedNetwork) pos.getTileEntity();
				flag |= te.isConnected();
				te.setConnected(false);
			} else {
				INeedNetwork te = (INeedNetwork) pos.getTileEntity();
				flag |= !te.isConnected();
				te.setConnected(true);
			}
		}
		if (flag) {
			markDirty();
			callUpdate();
			return true;
		}
		return false;
	}

	public StorageWrapper insertToItemHandler(StorageWrapper wrapper, IItemHandler handler) {
		if (handler == null) {
			return wrapper;
		}
		int max = handler.getSlots();
		Iterator<HugeItemStack> it = wrapper.storges.iterator();
		while (it.hasNext()) {
			HugeItemStack hugestack = it.next();
			for (int j = 0; j < max; j++) {
				ItemStack stack = hugestack.stack.copy();
				int amount = hugestack.count >= stack.getMaxStackSize() ? stack.getMaxStackSize() : hugestack.count;
				stack.setCount(amount);
				stack = handler.insertItem(j, stack, false);
				hugestack.count = hugestack.count - amount + (stack.isEmpty() ? 0 : stack.getCount());
				if (hugestack.isEmpty()) {
					it.remove();
					break;
				}
			}
		}

		Iterator<ItemStack> it2 = wrapper.other.iterator();
		while (it2.hasNext()) {
			ItemStack stack = it2.next();
			for (int j = 0; j < max; j++) {
				stack = handler.insertItem(j, stack, false);
				if (stack.isEmpty()) {
					it2.remove();
					break;
				}
			}
		}
		return wrapper;
	}

	public WorkingState getWorkingState() {
		if (loadTick > 0) {
			return WorkingState.STARTING;
		}
		if (!on) {
			return WorkingState.OFF;
		}
		return WorkingState.WORKING;
	}

	public int getEnergy() {
		return energy;
	}

	public int getMaxStorage() {
		int count = 0;
		Iterator<WorldPos> it = storages.iterator();
		while (it.hasNext()) {
			WorldPos pos = it.next();
			TileEntity tileentity = pos.getTileEntity();
			if (tileentity instanceof IStorable
					&& ((INeedNetwork) tileentity).getWorkingState() == INeedNetwork.WorkingState.WORKING) {
				IStorable te = (IStorable) tileentity;
				count += te.getStorage().getSlots();
			}
		}
		return count;
	}

	public int getUsedStorage() {
		int count = 0;
		Iterator<WorldPos> it = storages.iterator();
		while (it.hasNext()) {
			WorldPos pos = it.next();
			TileEntity tileentity = pos.getTileEntity();
			if (tileentity instanceof IStorable
					&& ((INeedNetwork) tileentity).getWorkingState() == INeedNetwork.WorkingState.WORKING) {
				IStorable te = (IStorable) tileentity;
				IItemHandler handler = te.getStorage();
				for (int i = 0; i < handler.getSlots(); i++) {
					if (!handler.getStackInSlot(i).isEmpty()) {
						count++;
					}
				}
			}
		}
		return count;
	}

	public void refreshMap() {
		List<WorldPos> list = new ArrayList<WorldPos>();
		nodes.forEach(e -> {
			TileEntity te = e.getTileEntity();
			if (te instanceof INode && ((INeedNetwork) te).isConnected()) {
				list.add(e);
			}
		});
		storages.forEach(e -> {
			TileEntity te = e.getTileEntity();
			if (te instanceof IStorable && ((INeedNetwork) te).isConnected()) {
				list.add(e);
			}
		});
		devices.forEach(e -> {
			TileEntity te = e.getTileEntity();
			if (te instanceof IDevice && ((INeedNetwork) te).isConnected()) {
				list.add(e);
			}
		});
		productions.forEach(e -> {
			TileEntity te = e.getTileEntity();
			if (te instanceof IProduction && ((INeedNetwork) te).isConnected()) {
				list.add(e);
			}
		});
		map = new double[list.size() + 1][list.size() + 1];
		for (int i = 0; i < list.size() + 1; i++) {
			for (int j = 0; j < list.size() + 1; j++) {
				map[i][j] = Double.MAX_VALUE;
			}
		}
		for (int i = 0; i < list.size(); i++) {
			WorldPos pos = list.get(i);
			TileEntity te = pos.getTileEntity();
			if (te instanceof TEBeaconCore
					&& ((TEBeaconCore) te).getWorkingState() == INeedNetwork.WorkingState.WORKING) {
				map[0][i + 1] = 0;
				map[i + 1][0] = 0;
			}
			double dist = Math.sqrt(this.pos.distanceSq(pos.getPos().getX(), pos.getPos().getY(), pos.getPos().getZ()));
			if (dist < 25) {
				map[0][i + 1] = dist;
				map[i + 1][0] = dist;
			}
		}
		for (int i = 0; i < list.size(); i++) {
			WorldPos pos = list.get(i);
			TileEntity tileentity = pos.getTileEntity();
			if (tileentity instanceof INode) {
				for (int j = 0; j < list.size(); j++) {
					WorldPos pos2 = list.get(j);
					if (pos.getBlockState().getBlock() == BlockLoader.beacon_core
							&& pos2.getBlockState().getBlock() == BlockLoader.beacon_core
							&& ((TEBeaconCore) pos.getTileEntity())
									.getWorkingState() == INeedNetwork.WorkingState.WORKING
							&& ((TEBeaconCore) pos2.getTileEntity())
									.getWorkingState() == INeedNetwork.WorkingState.WORKING) {
						map[i + 1][j + 1] = 0;
						map[j + 1][i + 1] = 0;
					} else if (tileentity.getWorld().provider.getDimension() == pos.getDim()) {
						double dist = Math.sqrt(pos.getPos().distanceSq(pos2.getPos().getX(), pos2.getPos().getY(),
								pos2.getPos().getZ()));
						if (dist < 25) {
							map[i + 1][j + 1] = dist;
							map[j + 1][i + 1] = dist;
						}
					}
				}
			}
		}
		for (int i = 0; i < map.length; i++) {
			map[i][i] = 0;
		}
		dijkstraCache.clear();
		mapDirty = false;
	}

	public void printMap() {
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++) {
				System.out.print("\t" + (map[i][j] == Double.MAX_VALUE ? "M" : (int) map[i][j]));
			}
			System.out.println();
		}
	}

	public List<Path> dijkstra(WorldPos pos) {
		if (map == null) {
			refreshMap();
		}
		if (dijkstraCache.containsKey(pos)) {
			List<Path> l = dijkstraCache.get(pos);
			List<Path> r = new ArrayList<Path>();
			l.forEach(e -> r.add(e.copy()));
			return r;
		}
		List<WorldPos> list = new ArrayList<WorldPos>();
		nodes.forEach(e -> {
			TileEntity te = e.getTileEntity();
			if (te instanceof INode && ((INeedNetwork) te).isConnected()) {
				list.add(e);
			}
		});
		storages.forEach(e -> {
			TileEntity te = e.getTileEntity();
			if (te instanceof IStorable && ((INeedNetwork) te).isConnected()) {
				list.add(e);
			}
		});
		devices.forEach(e -> {
			TileEntity te = e.getTileEntity();
			if (te instanceof IDevice && ((INeedNetwork) te).isConnected()) {
				list.add(e);
			}
		});
		productions.forEach(e -> {
			TileEntity te = e.getTileEntity();
			if (te instanceof IProduction && ((INeedNetwork) te).isConnected()) {
				list.add(e);
			}
		});
		int index = pos.getPos().equals(this.pos) ? 0 : list.indexOf(pos) + 1;
		double[] dist = map[index].clone();
		int[][] path = new int[map.length][0];
		boolean[] t = new boolean[dist.length];
		t[index] = true;
		do {
			double min = Double.MAX_VALUE;
			int minIndex = -1;
			for (int i = 0; i < dist.length; i++) {
				if (!t[i] && dist[i] < min) {
					min = dist[i];
					minIndex = i;
				}
			}
			if (minIndex == -1) {
				List<Path> r = new ArrayList<Path>();
				for (int j = 0; j < path.length; j++) {
					List<WorldPos> l = new ArrayList<WorldPos>();
					l.add(pos);
					for (int k = 0; k < path[j].length; k++) {
						l.add(path[j][k] > 0 ? list.get(path[j][k] - 1) : new WorldPos(this));
					}
					l.add(j > 0 ? list.get(j - 1) : new WorldPos(this));
					r.add(new Path(l, dist[j]));
				}
				dijkstraCache.put(pos, r);
				List<Path> s = new ArrayList<Path>();
				r.forEach(e -> s.add(e.copy()));
				return s;
			}
			t[minIndex] = true;
			for (int i = 0; i < map.length; i++) {
				double d = map[minIndex][i] + dist[minIndex];
				if (d < dist[i]) {
					dist[i] = d;
					path[i] = new int[path[minIndex].length + 1];
					for (int j = 0; j < path[minIndex].length; j++) {
						path[i][j] = path[minIndex][j];
					}
					path[i][path[i].length - 1] = minIndex;
				}
			}
		} while (true);

	}

	public NBTTagCompound genMapData(EntityPlayer player, NBTTagCompound tag) {
		updateDevice();
		NBTTagCompound units = new NBTTagCompound();
		NBTTagList list = new NBTTagList();
		nodes.forEach(e -> {
			if (e.getDim() == player.dimension) {
				INeedNetwork te = (INeedNetwork) e.getTileEntity();
				NBTTagCompound n = e.writeToNBT(new NBTTagCompound());
				n.setInteger("state", te.getWorkingState().ordinal());
				n.setInteger("id", Item.getIdFromItem(Item.getItemFromBlock(e.getBlockState().getBlock())));
				if (te instanceof TEBeaconCore) {
					n.setInteger("type", 1);
				}
				list.appendTag(n);
			}
		});
		units.setTag("nodes", list);
		NBTTagList list2 = new NBTTagList();
		storages.forEach(e -> {
			if (e.getDim() == player.dimension) {
				INeedNetwork te = (INeedNetwork) e.getTileEntity();
				NBTTagCompound n = e.writeToNBT(new NBTTagCompound());
				n.setInteger("state", te.getWorkingState().ordinal());
				n.setInteger("id", Item.getIdFromItem(Item.getItemFromBlock(e.getBlockState().getBlock())));
				list2.appendTag(n);
			}
		});
		units.setTag("storages", list2);
		NBTTagList list3 = new NBTTagList();
		devices.forEach(e -> {
			if (e.getDim() == player.dimension) {
				INeedNetwork te = (INeedNetwork) e.getTileEntity();
				NBTTagCompound n = e.writeToNBT(new NBTTagCompound());
				n.setInteger("state", te.getWorkingState().ordinal());
				n.setInteger("id", Item.getIdFromItem(Item.getItemFromBlock(e.getBlockState().getBlock())));
				list3.appendTag(n);
			}
		});
		units.setTag("devices", list3);
		NBTTagList list4 = new NBTTagList();
		productions.forEach(e -> {
			if (e.getDim() == player.dimension) {
				INeedNetwork te = (INeedNetwork) e.getTileEntity();
				NBTTagCompound n = e.writeToNBT(new NBTTagCompound());
				n.setInteger("state", te.getWorkingState().ordinal());
				n.setInteger("id", Item.getIdFromItem(Item.getItemFromBlock(e.getBlockState().getBlock())));
				list4.appendTag(n);
			}
		});
		units.setTag("productions", list4);
		List<Integer> lines = new ArrayList<Integer>();
		List<WorldPos> l = new ArrayList<WorldPos>();
		WorldPos w = new WorldPos(this);
		if (w.getDim() == player.dimension) {
			l.add(w);
		}
		nodes.forEach(e -> {
			if (e.getDim() == player.dimension) {
				l.add(e);
			}
		});
		storages.forEach(e -> {
			if (e.getDim() == player.dimension) {
				l.add(e);
			}
		});
		devices.forEach(e -> {
			if (e.getDim() == player.dimension) {
				l.add(e);
			}
		});
		productions.forEach(e -> {
			if (e.getDim() == player.dimension) {
				l.add(e);
			}
		});
		for (int i = 0; i < l.size(); i++) {
			WorldPos e = l.get(i);
			for (int j = i + 1; j < l.size(); j++) {
				WorldPos e2 = l.get(j);
				TileEntity te1 = e.getTileEntity();
				TileEntity te2 = e2.getTileEntity();
				if (te1 instanceof TEProcessingCenter && Math
						.sqrt(e.getPos().distanceSq(e2.getPos().getX(), e2.getPos().getY(), e2.getPos().getZ())) < 25) {
					lines.add(i);
					lines.add(j);
				} else if (te1 instanceof INode && ((INode) te1).isPointInRange(e2.getDim(), e2.getPos().getX(),
						e2.getPos().getY(), e2.getPos().getZ())) {
					lines.add(i);
					lines.add(j);
				} else if (te2 instanceof INode && ((INode) te2).isPointInRange(e.getDim(), e.getPos().getX(),
						e.getPos().getY(), e.getPos().getZ())) {
					lines.add(i);
					lines.add(j);
				}
			}
		}
		tag.setTag("units", units);
		tag.setTag("lines", new NBTTagIntArray(lines));
		return tag;
	}

	public NBTTagCompound genPackData(EntityPlayer player, NBTTagCompound tag) {
		NBTTagList list = new NBTTagList();
		Set<LogisticPack> packs = getPacks();
		packs.forEach(e -> {
			list.appendTag(e.writeToNBT(new NBTTagCompound()));
		});
		tag.setTag("list", list);
		return tag;
	}

	public void addClassifyGroup(ClassifyGroup group) {
		classifyGroups.add(group);
	}

	public List<ClassifyGroup> getClassifyGroups() {
		return classifyGroups;
	}

	public Map<WorldPos, Double> getNodesInRange(int dim, double x, double y, double z) {
		Map<WorldPos, Double> map = new HashMap<WorldPos, Double>();
		nodes.forEach(e -> {
			TileEntity te = e.getTileEntity();
			if (((INeedNetwork) te).getWorkingState() == INeedNetwork.WorkingState.WORKING
					&& ((INode) te).isPointInRange(dim, x, y, z)) {
				map.put(e, Math.sqrt(e.getPos().distanceSq(x, y, z)));
			}
		});
		return map;
	}

	public void updatePack(boolean refreshPath) {
		Iterator<LogisticPack> it = packs.iterator();
		Set<LogisticPack> deads = new HashSet<LogisticPack>();
		while (it.hasNext()) {
			LogisticPack i = it.next();
			if (!isPointInRange(i.dim, i.x, i.y, i.z)) {
				it.remove();
				callUpdate();
				continue;
			}
			if (refreshPath) {
				WorldPos target = i.getTarget();
				Map<WorldPos, Double> m = getNodesInRange(i.dim, i.x, i.y, i.z);
				List<Path> l = dijkstra(target);
				double shortest = Double.MAX_VALUE;
				Path path = null;
				Iterator<Path> it2 = l.iterator();
				while (it2.hasNext()) {
					Path p = it2.next();
					if (m.containsKey(p.getTarget())) {
						double dist = p.distance + m.get(p.getTarget());
						if (dist < shortest) {
							shortest = dist;
							path = p;
						}
					}
				}
				if (path != null) {
					i.routes = path.flip().routes;
				}
			}
			if (i.forward(1D)) {
				TileEntity te = i.getTarget().getTileEntity();
				if (te instanceof IStorable) {
					StorageWrapper wrapper = insertToItemHandler(i.items, ((IStorable) te).getStorage());
					if (!wrapper.isEmpty()) {
						deads.add(new LogisticPack(new ArrayList<WorldPos>(), wrapper, i.x, i.y, i.z, i.dim));
					}
				} else if (te != null) {
					StorageWrapper wrapper = insertToItemHandler(i.items,
							te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP));
					wrapper = insertToItemHandler(wrapper,
							te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.NORTH));
					wrapper = insertToItemHandler(wrapper,
							te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.SOUTH));
					wrapper = insertToItemHandler(wrapper,
							te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.WEST));
					wrapper = insertToItemHandler(wrapper,
							te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.EAST));
					wrapper = insertToItemHandler(wrapper,
							te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN));
					if (!wrapper.isEmpty()) {
						deads.add(new LogisticPack(i.routes, wrapper, i.x, i.y, i.z, i.dim));
					}
				}
				it.remove();
			}
		}
		deads.forEach(e -> {
			dispenceItems(e.items, new WorldPos((int) e.x, (int) e.y, (int) e.z, e.dim));
		});
		callUpdate();
	}

	public void markMapDirty() {
		mapDirty = true;
	}

	public void load() {
		if (!loaded) {
			ChunkHandler.instance.loadChunks(new WorldPos(this));
			loaded = true;
		}
	}

	public void unload() {
		if (loaded) {
			ChunkHandler.instance.unloadChunks(new WorldPos(this));
			loaded = false;
		}
	}

	@Override
	public void update() {
		load();
		markDirty();
		callUpdate();
		if (world.isRemote) {
			if (loadTick > 0) {
				loadTick--;
			}
			return;
		}
		WorldPos worldPos = new WorldPos(pos, world);
		if (!((BlockProcessingCenter) blockType).isFullStructure(worldPos)) {
			shutdown();
			return;
		}
		if (uuid == null) {
			setUUID(GlobalNetworkData.getData(world).getUUID(worldPos));
			callUpdate();
		}
		if (loadTick > 0) {
			if (consumeEnergy(1)) {
				loadTick--;
				if (loadTick == 0) {
					loadTick = -1;
					on = true;
					callUpdate();
				}
			} else {
				shutdown();
			}
			return;
		}
		if (!on) {
			return;
		}
		while (energyTick <= 0) {
			if (consumeEnergy(1)) {
				energyTick += 10;
			} else {
				shutdown();
				return;
			}
		}
		energyTick--;
		if (updateDevice() || map == null || mapDirty) {
			refreshMap();
			updatePack(true);
		} else {
			updatePack(false);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		name = compound.getString("name");
		loadTick = compound.getInteger("loadTick");
		energyTick = compound.getInteger("energyTick");
		on = compound.getBoolean("on");
		nodes.clear();
		NBTTagList list = compound.getTagList("nodes", 10);
		list.forEach(e -> {
			nodes.add(new WorldPos((NBTTagCompound) e));
			if (new WorldPos((NBTTagCompound) e).isOrigin()) {
				throw new RuntimeException(compound.toString());
			}
		});
		storages.clear();
		list = compound.getTagList("storges", 10);
		list.forEach(e -> {
			storages.add(new WorldPos((NBTTagCompound) e));
		});
		devices.clear();
		list = compound.getTagList("devices", 10);
		list.forEach(e -> {
			devices.add(new WorldPos((NBTTagCompound) e));
		});
		productions.clear();
		list = compound.getTagList("productions", 10);
		list.forEach(e -> {
			productions.add(new WorldPos((NBTTagCompound) e));
		});
		if (compound.hasKey("uuidMost")) {
			uuid = compound.getUniqueId("uuid");
		}
		packs.clear();
		list = compound.getTagList("packs", 10);
		list.forEach(e -> {
			packs.add(new LogisticPack((NBTTagCompound) e));
		});
		classifyGroups.clear();
		list = compound.getTagList("classifyGroups", 10);
		list.forEach(e -> {
			classifyGroups.add(new ClassifyGroup((NBTTagCompound) e));
		});
		super.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setString("name", name);
		compound.setInteger("loadTick", loadTick);
		compound.setInteger("energyTick", energyTick);
		compound.setBoolean("on", on);
		NBTTagList nodeList = new NBTTagList();
		nodes.forEach(e -> {
			nodeList.appendTag(e.writeToNBT(new NBTTagCompound()));
		});
		compound.setTag("nodes", nodeList);
		NBTTagList storgeList = new NBTTagList();
		storages.forEach(e -> {
			storgeList.appendTag(e.writeToNBT(new NBTTagCompound()));
		});
		compound.setTag("storges", storgeList);
		NBTTagList deviceList = new NBTTagList();
		devices.forEach(e -> {
			deviceList.appendTag(e.writeToNBT(new NBTTagCompound()));
		});
		compound.setTag("devices", deviceList);
		NBTTagList productionList = new NBTTagList();
		productions.forEach(e -> {
			productionList.appendTag(e.writeToNBT(new NBTTagCompound()));
		});
		compound.setTag("productions", productionList);
		if (uuid != null) {
			compound.setUniqueId("uuid", uuid);
		}
		NBTTagList packList = new NBTTagList();
		packs.forEach(e -> {
			packList.appendTag(e.writeToNBT(new NBTTagCompound()));
		});
		compound.setTag("packs", packList);
		NBTTagList classifyList = new NBTTagList();
		classifyGroups.forEach(e -> {
			classifyList.appendTag(e.writeToNBT(new NBTTagCompound()));
		});
		compound.setTag("classifyGroups", classifyList);
		return super.writeToNBT(compound);
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(pos, 1, getUpdateTag());
	}

	@Override
	public void onDataPacket(NetworkManager manager, SPacketUpdateTileEntity packet) {
		handleUpdateTag(packet.getNbtCompound());
	}

	public NBTTagCompound writeNetworkData(NBTTagCompound tag) {
		tag.setInteger("energy", energy);
		return tag;
	}

	public void readNetworkData(NBTTagCompound tag) {
		energy = tag.getInteger("energy");
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return writeNetworkData(writeToNBT(new NBTTagCompound()));
	}

	@Override
	public void handleUpdateTag(NBTTagCompound tag) {
		readFromNBT(tag);
		readNetworkData(tag);
	}

	@Override
	public Set<ChunkPos> getLoadChunks() {
		Set<ChunkPos> s = new HashSet<ChunkPos>();
		s.add(new ChunkPos(pos.getX() >> 4 - 1, pos.getZ() >> 4 - 1));
		s.add(new ChunkPos(pos.getX() >> 4, pos.getZ() >> 4 - 1));
		s.add(new ChunkPos(pos.getX() >> 4 + 1, pos.getZ() >> 4 - 1));
		s.add(new ChunkPos(pos.getX() >> 4, pos.getZ() >> 4 - 1));
		s.add(new ChunkPos(pos));
		s.add(new ChunkPos(pos.getX() >> 4, pos.getZ() >> 4 + 1));
		s.add(new ChunkPos(pos.getX() >> 4 - 1, pos.getZ() >> 4 + 1));
		s.add(new ChunkPos(pos.getX() >> 4, pos.getZ() >> 4 + 1));
		s.add(new ChunkPos(pos.getX() >> 4 + 1, pos.getZ() >> 4 + 1));
		return s;
	}

	public static enum WorkingState {
		WORKING("sphinx.working"), OFF("sphinx.off"), STARTING("sphinx.starting");

		String key;

		private WorkingState(String key) {
			this.key = key;
		}

		@Override
		public String toString() {
			return I18n.format(key);
		}
	}

	public static class LogisticTaskComputeResult {
		public StorageWrapper remain;
		public int tick;

		public LogisticTaskComputeResult(StorageWrapper wrapper, int tick) {
			this.remain = wrapper;
			this.tick = tick;
		}
	}
}
