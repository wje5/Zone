package com.pinball3d.zone.tileentity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Sets;
import com.pinball3d.zone.ChunkHandler;
import com.pinball3d.zone.ChunkHandler.IChunkLoader;
import com.pinball3d.zone.Zone;
import com.pinball3d.zone.block.BlockLoader;
import com.pinball3d.zone.block.BlockProcessingCenter;
import com.pinball3d.zone.capability.EnergyIOWrapper;
import com.pinball3d.zone.capability.ZoneEnergyStorage;
import com.pinball3d.zone.sphinx.ClassifyGroup;
import com.pinball3d.zone.sphinx.GlobalNetworkData;
import com.pinball3d.zone.sphinx.IDevice;
import com.pinball3d.zone.sphinx.INeedNetwork;
import com.pinball3d.zone.sphinx.INode;
import com.pinball3d.zone.sphinx.IProduction;
import com.pinball3d.zone.sphinx.IStorable;
import com.pinball3d.zone.sphinx.LogisticPack;
import com.pinball3d.zone.sphinx.LogisticPack.Path;
import com.pinball3d.zone.sphinx.SerialNumber;
import com.pinball3d.zone.sphinx.SerialNumber.Type;
import com.pinball3d.zone.sphinx.crafting.CraftingIngredent;
import com.pinball3d.zone.sphinx.crafting.CraftingIngredentItem;
import com.pinball3d.zone.sphinx.crafting.OreDictionaryData;
import com.pinball3d.zone.sphinx.crafting.RecipeType;
import com.pinball3d.zone.sphinx.crafting.SphinxRecipe;
import com.pinball3d.zone.util.HugeItemStack;
import com.pinball3d.zone.util.StorageWrapper;
import com.pinball3d.zone.util.Util;
import com.pinball3d.zone.util.WorldPos;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.OreIngredient;

public class TEProcessingCenter extends TileEntity implements ITickable, IChunkLoader {
	private boolean on, inited;
	private String name = "Name Undone";
	protected ZoneEnergyStorage energy = new ZoneEnergyStorage(2880000);
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
	private Set<SerialNumber> nodes = new TreeSet<SerialNumber>(SerialNumber.serialNumberComparator);
	private Set<SerialNumber> storages = new TreeSet<SerialNumber>(SerialNumber.serialNumberComparator);
	private Set<SerialNumber> devices = new TreeSet<SerialNumber>(SerialNumber.serialNumberComparator);
	private Set<SerialNumber> productions = new TreeSet<SerialNumber>(SerialNumber.serialNumberComparator);
	private Set<LogisticPack> packs = new HashSet<LogisticPack>();
	private Map<SerialNumber, WorldPos> serialNumberToPos = new TreeMap<SerialNumber, WorldPos>(
			SerialNumber.serialNumberComparator);
	private UUID uuid;
	private double[][] map;
	private Map<Integer, ClassifyGroup> classifyGroups = new TreeMap<Integer, ClassifyGroup>();
	private Map<Integer, RecipeType> recipeTypes = new TreeMap<Integer, RecipeType>();
	private Map<Integer, SphinxRecipe> recipes = new TreeMap<Integer, SphinxRecipe>();
	private Map<Integer, OreDictionaryData> oreDictionarys = new TreeMap<Integer, OreDictionaryData>();
	private boolean mapDirty;
	private Map<WorldPos, List<Path>> dijkstraCache = new HashMap<WorldPos, List<Path>>();
	private Map<UUID, UserData> users = new HashMap<UUID, UserData>();
	private int logId, nodeId, storageId, deviceId, productionId, packId, classifyGroupId, warningStorageFullCD,
			recipeTypeId = 100, recipeId, oreDictionaryId;

	public TEProcessingCenter() {

	}

	public void initSphinx() {
		genSerialNumber(new WorldPos(this), Type.NODE);
		rescanRecipes();
	}

	public boolean isOn() {
		return on;
	}

	public void shutdown() {
		on = false;
		BlockProcessingCenter.setState(false, world, pos);
	}

	public String getName() {
		return name;
	}

	public void open() {
		if (!on) {
			on = true;
			BlockProcessingCenter.setState(true, world, pos);
			markDirty();
		}
	}

	public void setName(String name) {
		if (name.length() >= 4) {
			this.name = name;
			markDirty();
		}
	}

	public SerialNumber genSerialNumber(WorldPos pos, Type type) {
		int id = -1;
		switch (type) {
		case NODE:
			id = nodeId++;
			break;
		case STORAGE:
			id = storageId++;
			break;
		case DEVICE:
			id = deviceId++;
			break;
		case PRODUCTION:
			id = productionId++;
			break;
		}
		SerialNumber s = new SerialNumber(type, id);
		serialNumberToPos.put(s, pos);
		return s;
	}

	public WorldPos getPosFromSerialNumber(SerialNumber s) {
		WorldPos pos = serialNumberToPos.get(s);
		return pos == null ? WorldPos.ORIGIN : pos;
	}

	public SerialNumber getSerialNumberFromPos(WorldPos s) {
		Iterator<Entry<SerialNumber, WorldPos>> it = serialNumberToPos.entrySet().iterator();
		while (it.hasNext()) {
			Entry<SerialNumber, WorldPos> e = it.next();
			if (s.equals(e.getValue())) {
				return e.getKey();
			}
		}
		return null;
	}

	public void addNeedNetwork(WorldPos pos, EntityPlayer player) {
		if (!isUser(player) || serialNumberToPos.containsValue(pos)) {
			return;
		}
		TileEntity te = pos.getTileEntity();
		SerialNumber number = null;
		if (te instanceof INode) {
			number = genSerialNumber(pos, Type.NODE);
			nodes.add(number);
			((INeedNetwork) te).connect(getUUID());
		} else if (te instanceof IStorable) {
			number = genSerialNumber(pos, Type.STORAGE);
			storages.add(number);
			((INeedNetwork) te).connect(getUUID());
		} else if (te instanceof IDevice) {
			number = genSerialNumber(pos, Type.DEVICE);
			devices.add(number);
			((INeedNetwork) te).connect(getUUID());
		} else if (te instanceof IProduction) {
			number = genSerialNumber(pos, Type.PRODUCTION);
			productions.add(number);
			((INeedNetwork) te).connect(getUUID());
		}
		if (number != null) {
//			fireLog(new LogConnectToNetwork(getNextLogId(), player, number, pos));//TODO
		}
		refreshMap();
		markDirty();
	}

	public void removeNeedNetwork(SerialNumber number, EntityPlayer player) {
		if (!serialNumberToPos.containsKey(number)) {
			return;
		}
		WorldPos pos = serialNumberToPos.get(number);
		TileEntity te = pos.getTileEntity();
		if (te instanceof INeedNetwork) {
			((INeedNetwork) te).deleteNetwork();
		}
		nodes.remove(number);
		storages.remove(number);
		devices.remove(number);
		productions.remove(number);
		serialNumberToPos.remove(number);
		if (player != null) {
//			fireLog(new LogDisconnectFromNetwork(getNextLogId(), player, number, pos));//TODO
		} else {
//			fireLog(new LogNeedNetworkDestroyed(getNextLogId(), number, pos));//TODO
		}
		refreshMap();
		markDirty();
	}

	public Set<SerialNumber> getNodes() {
		return nodes;
	}

	public Set<SerialNumber> getStorages() {
		return storages;
	}

	public Set<SerialNumber> getDevices() {
		return devices;
	}

	public Set<SerialNumber> getProductions() {
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
	}

	public boolean isPointInRange(int dim, double x, double y, double z) {
		if (Math.sqrt(pos.distanceSq(x, y, z)) < 25) {
			return true;
		}
		Iterator<SerialNumber> it = nodes.iterator();
		while (it.hasNext()) {
			TileEntity te = getPosFromSerialNumber(it.next()).getTileEntity();
			if (te instanceof INode) {
				if (((INeedNetwork) te).isConnected() && ((INode) te).isPointInRange(dim, x, y, z)) {
					return true;
				}
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
		Iterator<SerialNumber> it = nodes.iterator();
		while (it.hasNext()) {
			WorldPos p = getPosFromSerialNumber(it.next());
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

	public StorageWrapper getNetworkUseableItems() {
		StorageWrapper wrapper = new StorageWrapper();
		storages.forEach(e -> {
			TileEntity te = getPosFromSerialNumber(e).getTileEntity();
			if (te instanceof INeedNetwork && ((INeedNetwork) te).isConnected() && te instanceof IStorable) {
				StorageWrapper w = ((IStorable) te).getStorages();
				wrapper.merge(w);
			}
		});
		return wrapper;
	}

	public void firePack(StorageWrapper w, Path path, WorldPos startPos) {
		LogisticPack pack = new LogisticPack(packId++, path.routes, w, startPos);
		packs.add(pack);
		SerialNumber start = getSerialNumberFromPos(pack.routes.get(0));
		SerialNumber end = getSerialNumberFromPos(pack.routes.get(pack.routes.size() - 1));
		List<SerialNumber> p = new ArrayList<SerialNumber>();
		for (int index = 1; index < pack.routes.size() - 1; index++) {
			SerialNumber j = getSerialNumberFromPos(pack.routes.get(index));
			p.add(j);
		}
//		fireLog(new LogSendPack(getNextLogId(), pack.getId(), pack.items, start, end, p, (int) path.distance));//TODO
	}

	public int requestItems(StorageWrapper wrapper, WorldPos target, boolean isSimulate) {
		TreeSet<SerialNumber> sortset = new TreeSet<SerialNumber>((o1, o2) -> {
			double dist1 = getPosFromSerialNumber(o1).getPos().distanceSq(target.getPos());
			double dist2 = getPosFromSerialNumber(o2).getPos().distanceSq(target.getPos());
			return dist1 > dist2 ? 1 : dist1 < dist2 ? -1 : o1.hashCode() > o2.hashCode() ? 1 : -1;
			// TODO need best solution
		});
		sortset.addAll(storages);
		int time = 0;
		Iterator<SerialNumber> it = sortset.iterator();
		while (it.hasNext()) {
			SerialNumber e = it.next();
			WorldPos pos = getPosFromSerialNumber(e);
			TileEntity te = pos.getTileEntity();
			if (te instanceof INeedNetwork && ((INeedNetwork) te).isConnected() && te instanceof IStorable) {
				StorageWrapper w = ((IStorable) te).extract(wrapper, isSimulate);
				if (!w.isEmpty()) {
					List<Path> l = dijkstra(target);
					for (Path i : l) {
						if (i.getTarget().equals(pos)) {
							time = (int) (time < i.distance ? i.distance : time);
							if (!isSimulate) {
								firePack(w, i.flip(), pos);
							}
						}
					}
				}
			}
		}
		return time;
	}

	public StorageWrapper dispenseItems(StorageWrapper wrapper, WorldPos pos) {
		TreeSet<SerialNumber> sortset = new TreeSet<SerialNumber>((o1, o2) -> {
			double dist1 = getPosFromSerialNumber(o1).getPos().distanceSq(pos.getPos());
			double dist2 = getPosFromSerialNumber(o2).getPos().distanceSq(pos.getPos());
			return dist1 > dist2 ? 1 : dist1 < dist2 ? -1 : o1.hashCode() > o2.hashCode() ? 1 : -1;
			// TODO need best solution
		});
		sortset.addAll(storages);
		sortset.forEach(e -> {
			WorldPos p = getPosFromSerialNumber(e);
			TileEntity te = p.getTileEntity();
			if (te instanceof INeedNetwork && ((INeedNetwork) te).isConnected() && te instanceof IStorable) {
				StorageWrapper w = ((IStorable) te).insert(wrapper, true);
				if (!w.isEmpty()) {
					List<Path> l = dijkstra(pos);
					for (Path i : l) {
						if (i.getTarget().equals(p)) {
							firePack(w, i, pos);
						}
					}
				}
			}
		});
		if (!wrapper.isEmpty()) {
			if (warningStorageFullCD <= 0) {
//				fireLog(new LogStorageFull(getNextLogId()));//TODO
				warningStorageFullCD = 300;
			}
		}
		return wrapper;
	}

	public boolean updateNode() {
		boolean flag = false;
		Set<SerialNumber> temp = new HashSet<SerialNumber>();
		do {
			flag = false;
			Iterator<SerialNumber> it = nodes.iterator();
			while (it.hasNext()) {
				SerialNumber i = it.next();
				if (temp.contains(i)) {
					continue;
				}
				WorldPos p = getPosFromSerialNumber(i);
				TileEntity tileentity = p.getTileEntity();
				if (p.getBlockState().getBlock() == BlockLoader.beacon_core) {
					flag = true;
					temp.add(i);
					((INeedNetwork) tileentity).setConnected(true);
					continue;
				}
				if (p == null || !(tileentity instanceof INode)) {
					continue;
				}
				INeedNetwork te = (INeedNetwork) tileentity;
				if (p.getDim() == world.provider.getDimension()) {
					if (Math.sqrt(pos.distanceSq(p.getPos().getX(), p.getPos().getY(), p.getPos().getZ())) < 25) {
						flag = true;
						temp.add(i);
						te.setConnected(true);
					} else {
						Iterator<SerialNumber> it2 = temp.iterator();
						while (it2.hasNext()) {
							WorldPos nodepos = getPosFromSerialNumber(it2.next());
							TileEntity nodete = nodepos.getTileEntity();
							if (((INeedNetwork) nodete).getWorkingState() == INeedNetwork.WorkingState.WORKING
									&& ((INode) nodete).isPointInRange(p.getDim(), p.getPos().getX(), p.getPos().getY(),
											p.getPos().getZ())) {
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
		Iterator<SerialNumber> it = nodes.iterator();
		flag = false;
		while (it.hasNext()) {
			SerialNumber e = it.next();
			TileEntity tileentity = getPosFromSerialNumber(e).getTileEntity();
			if (tileentity instanceof INode && getUUID().equals(((INeedNetwork) tileentity).getNetwork())) {
				if (temp.contains(e)) {
					flag |= !((INeedNetwork) tileentity).isConnected();
					((INeedNetwork) tileentity).setConnected(true);
				} else {
					flag |= ((INeedNetwork) tileentity).isConnected();
					((INeedNetwork) tileentity).setConnected(false);
				}
			} else {
				it.remove();
				flag = true;
			}
		}
		if (flag) {
			markDirty();
		}
		return flag;
	}

	public boolean updateDevice() {
		boolean flag = updateNode();
		Iterator<SerialNumber> it = storages.iterator();
		Set<SerialNumber> set = new HashSet<SerialNumber>();
		while (it.hasNext()) {
			SerialNumber s = it.next();
			WorldPos pos = getPosFromSerialNumber(s);
			if (!(pos.getTileEntity() instanceof IStorable)) {
				set.add(s);
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
			SerialNumber s = it.next();
			WorldPos pos = getPosFromSerialNumber(s);
			if (!(pos.getTileEntity() instanceof IDevice)) {
				set.add(s);
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
			SerialNumber s = it.next();
			WorldPos pos = getPosFromSerialNumber(s);
			if (!(pos.getTileEntity() instanceof IProduction)) {
				set.add(s);
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
		set.forEach(e -> removeNeedNetwork(e, null));
		if (flag) {
			markDirty();
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
		if (!on) {
			return WorkingState.OFF;
		}
		return WorkingState.WORKING;
	}

	public IEnergyStorage getEnergy() {
		return new EnergyIOWrapper(energy, false, true);
	}

	public int getMaxStorage() {
		int count = 0;
		Iterator<SerialNumber> it = storages.iterator();
		while (it.hasNext()) {
			WorldPos pos = getPosFromSerialNumber(it.next());
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
		Iterator<SerialNumber> it = storages.iterator();
		while (it.hasNext()) {
			WorldPos pos = getPosFromSerialNumber(it.next());
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
			WorldPos p = getPosFromSerialNumber(e);
			TileEntity te = p.getTileEntity();
			if (te instanceof INode && ((INeedNetwork) te).isConnected()) {
				list.add(p);
			}
		});
		storages.forEach(e -> {
			WorldPos p = getPosFromSerialNumber(e);
			TileEntity te = p.getTileEntity();
			if (te instanceof IStorable && ((INeedNetwork) te).isConnected()) {
				list.add(p);
			}
		});
		devices.forEach(e -> {
			WorldPos p = getPosFromSerialNumber(e);
			TileEntity te = p.getTileEntity();
			if (te instanceof IDevice && ((INeedNetwork) te).isConnected()) {
				list.add(p);
			}
		});
		productions.forEach(e -> {
			WorldPos p = getPosFromSerialNumber(e);
			TileEntity te = p.getTileEntity();
			if (te instanceof IProduction && ((INeedNetwork) te).isConnected()) {
				list.add(p);
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
					} else if (tileentity.getWorld().provider.getDimension() == pos2.getDim()) {
						double dist = Math.sqrt(pos.getPos().distanceSq(pos2.getPos().getX(), pos2.getPos().getY(),
								pos2.getPos().getZ()));
						if (((INode) tileentity).isPointInRange(pos2.getDim(), pos2.getPos().getX(),
								pos2.getPos().getY(), pos2.getPos().getZ())) {
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
			WorldPos p = getPosFromSerialNumber(e);
			TileEntity te = p.getTileEntity();
			if (te instanceof INode && ((INeedNetwork) te).isConnected()) {
				list.add(p);
			}
		});
		storages.forEach(e -> {
			WorldPos p = getPosFromSerialNumber(e);
			TileEntity te = p.getTileEntity();
			if (te instanceof IStorable && ((INeedNetwork) te).isConnected()) {
				list.add(p);
			}
		});
		devices.forEach(e -> {
			WorldPos p = getPosFromSerialNumber(e);
			TileEntity te = p.getTileEntity();
			if (te instanceof IDevice && ((INeedNetwork) te).isConnected()) {
				list.add(p);
			}
		});
		productions.forEach(e -> {
			WorldPos p = getPosFromSerialNumber(e);
			TileEntity te = p.getTileEntity();
			if (te instanceof IProduction && ((INeedNetwork) te).isConnected()) {
				list.add(p);
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
			WorldPos p = getPosFromSerialNumber(e);
			if (p.getDim() == player.dimension) {
				INeedNetwork te = (INeedNetwork) p.getTileEntity();
				NBTTagCompound n = new NBTTagCompound();
				n.setTag("pos", p.writeToNBT(new NBTTagCompound()));
				e.check(this);
				n.setTag("serial", e.writeToNBT(new NBTTagCompound()));
				n.setInteger("state", te.getWorkingState().ordinal());
				n.setInteger("id", Item.getIdFromItem(Item.getItemFromBlock(p.getBlockState().getBlock())));
				if (te instanceof TEBeaconCore) {
					n.setInteger("type", 1);
				}
				list.appendTag(n);
			}
		});
		units.setTag("nodes", list);
		NBTTagList list2 = new NBTTagList();
		storages.forEach(e -> {
			WorldPos p = getPosFromSerialNumber(e);
			if (p.getDim() == player.dimension) {
				INeedNetwork te = (INeedNetwork) p.getTileEntity();
				NBTTagCompound n = new NBTTagCompound();
				n.setTag("pos", p.writeToNBT(new NBTTagCompound()));
				e.check(this);
				n.setTag("serial", e.writeToNBT(new NBTTagCompound()));
				n.setInteger("state", te.getWorkingState().ordinal());
				n.setInteger("id", Item.getIdFromItem(Item.getItemFromBlock(p.getBlockState().getBlock())));
				list2.appendTag(n);
			}
		});
		units.setTag("storages", list2);
		NBTTagList list3 = new NBTTagList();
		devices.forEach(e -> {
			WorldPos p = getPosFromSerialNumber(e);
			if (p.getDim() == player.dimension) {
				INeedNetwork te = (INeedNetwork) p.getTileEntity();
				NBTTagCompound n = new NBTTagCompound();
				n.setTag("pos", p.writeToNBT(new NBTTagCompound()));
				e.check(this);
				n.setTag("serial", e.writeToNBT(new NBTTagCompound()));
				n.setInteger("state", te.getWorkingState().ordinal());
				n.setInteger("id", Item.getIdFromItem(Item.getItemFromBlock(p.getBlockState().getBlock())));
				list3.appendTag(n);
			}
		});
		units.setTag("devices", list3);
		NBTTagList list4 = new NBTTagList();
		productions.forEach(e -> {
			WorldPos p = getPosFromSerialNumber(e);
			if (p.getDim() == player.dimension) {
				INeedNetwork te = (INeedNetwork) p.getTileEntity();
				NBTTagCompound n = new NBTTagCompound();
				n.setTag("pos", p.writeToNBT(new NBTTagCompound()));
				e.check(this);
				n.setTag("serial", e.writeToNBT(new NBTTagCompound()));
				n.setInteger("state", te.getWorkingState().ordinal());
				n.setInteger("id", Item.getIdFromItem(Item.getItemFromBlock(p.getBlockState().getBlock())));
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
			WorldPos p = getPosFromSerialNumber(e);
			if (p.getDim() == player.dimension) {
				l.add(p);
			}
		});
		storages.forEach(e -> {
			WorldPos p = getPosFromSerialNumber(e);
			if (p.getDim() == player.dimension) {
				l.add(p);
			}
		});
		devices.forEach(e -> {
			WorldPos p = getPosFromSerialNumber(e);
			if (p.getDim() == player.dimension) {
				l.add(p);
			}
		});
		productions.forEach(e -> {
			WorldPos p = getPosFromSerialNumber(e);
			if (p.getDim() == player.dimension) {
				l.add(p);
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
		classifyGroups.put(classifyGroupId++, group);
	}

	public Map<Integer, ClassifyGroup> getClassifyGroups() {
		return classifyGroups;
	}

	public Map<UUID, UserData> getUsers() {
		return users;
	}

	public void addUser(UserData user) {
		users.put(user.uuid, user);
	}

	public boolean isUser(EntityPlayer player) {
		return isUser(player.getUniqueID());
	}

	public boolean isUser(UUID uuid) {
		UserData data = users.get(uuid);
		return data != null && !data.reviewing;
	}

	public boolean hasUser(UUID uuid) {
		return users.containsKey(uuid);
	}

	public boolean isAdmin(EntityPlayer player) {
		return isAdmin(player.getUniqueID());
	}

	public boolean isAdmin(UUID uuid) {
		UserData data = users.get(uuid);
		return data != null && !data.reviewing && data.admin;
	}

	public Map<WorldPos, Double> getNodesInRange(int dim, double x, double y, double z) {
		Map<WorldPos, Double> map = new HashMap<WorldPos, Double>();
		nodes.forEach(e -> {
			WorldPos p = getPosFromSerialNumber(e);
			TileEntity te = p.getTileEntity();
			if (((INeedNetwork) te).getWorkingState() == INeedNetwork.WorkingState.WORKING
					&& ((INode) te).isPointInRange(dim, x, y, z)) {
				map.put(p, Math.sqrt(p.getPos().distanceSq(x, y, z)));
			}
		});
		return map;
	}

	public void updatePack(boolean refreshPath) {
		Iterator<LogisticPack> it = packs.iterator();
		Map<LogisticPack, LogisticPack> deads = new HashMap<LogisticPack, LogisticPack>();
		while (it.hasNext()) {
			LogisticPack i = it.next();
			if (!isPointInRange(i.dim, i.x, i.y, i.z)) {
				List<SerialNumber> l = i.path.stream().map(this::getSerialNumberFromPos).collect(Collectors.toList());
//				fireLog(new LogPackLost(getNextLogId(), i.getId(), i.items, l.get(0), l.subList(1, l.size()),
//						new WorldPos((int) i.x, (int) i.y, (int) i.z, i.dim)));//TODO
				it.remove();
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
				List<SerialNumber> l = i.path.stream().map(this::getSerialNumberFromPos).collect(Collectors.toList());
				TileEntity te = i.getTarget().getTileEntity();
				if (te instanceof IStorable) {
					StorageWrapper wrapper = insertToItemHandler(i.items.copy(), ((IStorable) te).getStorage());
					if (!wrapper.isEmpty()) {
						deads.put(new LogisticPack(-1, new ArrayList<WorldPos>(), wrapper, i.x, i.y, i.z, i.dim), i);
					} else {
//						fireLog(new LogRecvPack(getNextLogId(), i.getId(), i.items, l.get(0), l.get(l.size() - 1),
//								l.subList(1, l.size() - 1), packId));TODO
					}
				} else if (te != null) {
					StorageWrapper wrapper = insertToItemHandler(i.items.copy(),
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
						deads.put(new LogisticPack(-1, new ArrayList<WorldPos>(), wrapper, i.x, i.y, i.z, i.dim), i);
					} else {
//						fireLog(new LogRecvPack(getNextLogId(), i.getId(), i.items, l.get(0), l.get(l.size() - 1),
//								l.subList(1, l.size() - 1), packId));//TODO
					}
				} else {
					// ??????
					deads.put(new LogisticPack(-1, new ArrayList<WorldPos>(), i.items, i.x, i.y, i.z, i.dim), i);
				}
				it.remove();
			}
		}
		deads.forEach((k, v) -> {
			StorageWrapper w = dispenseItems(k.items.copy(), new WorldPos((int) k.x, (int) k.y, (int) k.z, k.dim));
			if (!w.isEmpty()) {
				List<SerialNumber> l = v.path.stream().map(this::getSerialNumberFromPos).collect(Collectors.toList());
//				fireLog(new LogRecvPackFull(getNextLogId(), v.getId(), v.items, l.get(0), l.get(l.size() - 1),
//						l.subList(1, l.size() - 1), packId, k.items));//TODO
			} else {
				List<SerialNumber> l = v.path.stream().map(this::getSerialNumberFromPos).collect(Collectors.toList());
//				fireLog(new LogRecvPack(getNextLogId(), v.getId(), v.items, l.get(0), l.get(l.size() - 1),
//						l.subList(1, l.size() - 1), packId));//TODO
			}
		});
	}

	public void markMapDirty() {
		mapDirty = true;
	}

	public void fireLog(String log) {
//		logCache.add(log);//TODO
	}

	public int getNextLogId() {
		return logId++;
	}

	public int[] rescanRecipes() {
		int[] d = initOreDictionary();
		int recipeAdd = 0;
		Iterator<IRecipe> it = CraftingManager.REGISTRY.iterator();
		while (it.hasNext()) {
			IRecipe i = it.next();
			CraftingIngredent[] data = new CraftingIngredent[10];
			NonNullList<Ingredient> list = i.getIngredients();
			if (!list.isEmpty()) {
				tag: for (int j = 0; j < list.size(); j++) {
					Ingredient in = list.get(j);
					ItemStack[] s = in.getMatchingStacks();
					if (s.length > 1) {
						if (!(in instanceof OreIngredient)) {
							Set<CraftingIngredentItem> set = Stream.of(s).map(e -> new CraftingIngredentItem(e))
									.collect(Collectors.toSet());
							Iterator<OreDictionaryData> it2 = getOreDictionarys().values().iterator();
							while (it2.hasNext()) {
								OreDictionaryData o = it2.next();
								if (o.getName() == null) {
									Set<CraftingIngredentItem> set2 = Sets.newHashSet(o.getItems());
									Collections.addAll(set2, o.getDisableItems());
									if (set2.equals(set)) {
										continue tag;
									}
								}
							}
							addOreDictionary(new OreDictionaryData(
									null, Stream.of(s).map(e -> new CraftingIngredentItem(e))
											.collect(Collectors.toList()).toArray(new CraftingIngredentItem[] {}),
									new CraftingIngredentItem[] {}));
							d[0]++;
						}
					} else if (s.length == 1) {
						ItemStack stack = s[0];
						data[j] = new CraftingIngredentItem(stack);
					}
				}
				data[9] = new CraftingIngredentItem(i.getRecipeOutput());
				SphinxRecipe r = new SphinxRecipe(RecipeType.BasicType.MINECRAFT_WORKBENCH.ordinal(), data);
				if (addRecipe(r)) {
					recipeAdd++;
				}
			}
		}
//		fireLog(new LogRescanRecipesFinish(getNextLogId(), recipeAdd, d[0], d[1]));//TODO
		return new int[] { recipeAdd, d[0], d[1] };
	}

	public boolean addRecipe(SphinxRecipe recipe) {
		Iterator<SphinxRecipe> it = recipes.values().iterator();
		while (it.hasNext()) {
			SphinxRecipe i = it.next();
			if (i.equals(recipe)) {
				return false;
			}
		}
		recipes.put(recipeId++, recipe);
		return true;
	}

	public void initRecipeType() {
		recipeTypes.clear();
		recipeTypeId = 100;
		recipeTypes.put(RecipeType.BasicType.MINECRAFT_WORKBENCH.ordinal(), new RecipeType(
				RecipeType.BasicType.MINECRAFT_WORKBENCH.ordinal(), "tile.workbench.name", true, 1, new int[] { 10 }));
		recipeTypes.put(RecipeType.BasicType.MINECRAFT_FURNACE.ordinal(), new RecipeType(
				RecipeType.BasicType.MINECRAFT_FURNACE.ordinal(), "tile.furnace.name", true, 1, new int[] { 3 }));
	}

	public int[] initOreDictionary() {
		int add = 0, change = 0;
		String[] names = OreDictionary.getOreNames();
		for (String name : names) {
			NonNullList<ItemStack> list = OreDictionary.getOres(name);
			NonNullList<ItemStack> list2 = NonNullList.create();
			for (ItemStack stack : list) {
				if (stack.isEmpty()) {
					continue;
				}
				if (stack.getMetadata() == net.minecraftforge.oredict.OreDictionary.WILDCARD_VALUE) {
					stack.getItem().getSubItems(net.minecraft.creativetab.CreativeTabs.SEARCH, list2);
				} else {
					list2.add(stack);
				}
			}
			List<CraftingIngredentItem> l = list2.stream().map(e -> new CraftingIngredentItem(e))
					.collect(Collectors.toList());
			boolean flag = true;
			for (Entry<Integer, OreDictionaryData> entry : oreDictionarys.entrySet()) {
				OreDictionaryData data = entry.getValue();
				if (name.equals(data.getName())) {
					List<CraftingIngredentItem> l1 = Stream.of(data.getItems()).filter(e -> l.contains(e))
							.collect(Collectors.toList());
					List<CraftingIngredentItem> l2 = Stream.of(data.getDisableItems()).filter(e -> l.contains(e))
							.collect(Collectors.toList());
					l.forEach(e -> {
						if (!l1.contains(e) && !l2.contains(e)) {
							l1.add(e);
						}
					});
					OreDictionaryData o = new OreDictionaryData(name, l1.toArray(new CraftingIngredentItem[] {}),
							l2.toArray(new CraftingIngredentItem[] {}));
					if (!o.equals(data)) {
						addOreDictionary(o);
						change++;
					}
					flag = false;
					break;
				}
			}
			if (flag) {
				addOreDictionary(new OreDictionaryData(name, l.toArray(new CraftingIngredentItem[] {}),
						new CraftingIngredentItem[] {}));
				add++;
			}
		}
		return new int[] { add, change };
	}

	public void addOreDictionary(OreDictionaryData data) {
		Iterator<Entry<Integer, OreDictionaryData>> it = oreDictionarys.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Integer, OreDictionaryData> i = it.next();
			if (data.getName() != null && data.getName().equals(i.getValue().getName())) {
				oreDictionarys.put(i.getKey(), data);
				return;
			}
		}
		oreDictionarys.put(oreDictionaryId++, data);
	}

	public int newOreDictionary() {
		oreDictionarys.put(oreDictionaryId,
				new OreDictionaryData(null, new CraftingIngredentItem[] {}, new CraftingIngredentItem[] {}));
		return oreDictionaryId++;
	}

	public Map<Integer, OreDictionaryData> getOreDictionarys() {
		return oreDictionarys;
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
		if (world.isRemote) {
			return;
		}
		WorldPos worldPos = new WorldPos(pos, world);
		if (!inited) {
			initSphinx();
			inited = true;
		}
//		System.out.println(energy.getEnergyStored() + "|" + energy.getMaxEnergyStored());
		if (((BlockProcessingCenter) blockType).isFullStructure(worldPos) != null && on) {
			shutdown();
//			fireLog(new LogSphinxShutdownStructure(getNextLogId()));// TODO
			return;
		}
		if (uuid == null) {
			setUUID(GlobalNetworkData.getUUID(worldPos));
		}
		if (!on) {
			Iterator<LogisticPack> it = packs.iterator();
			while (it.hasNext()) {
				LogisticPack pack = it.next();
				it.remove();
				List<SerialNumber> l = pack.path.stream().map(this::getSerialNumberFromPos)
						.collect(Collectors.toList());
//				fireLog(new LogPackLost(getNextLogId(), pack.getId(), pack.items, l.get(0), l.subList(1, l.size()),
//						new WorldPos((int) pack.x, (int) pack.y, (int) pack.z, pack.dim)));//TODO
			}
			return;
		}
		if (energy.extractEnergy(480, true) == 480) {
			energy.extractEnergy(480, false);
		} else {
			shutdown();
//				fireLog(new LogSphinxShutdownEnergy(getNextLogId()));// TODO
			return;
		}
		boolean flag = updateDevice();
		if (flag || map == null || mapDirty) {
			refreshMap();
			updatePack(true);
		} else {
			updatePack(false);
		}
		warningStorageFullCD--;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		name = compound.getString("name");
		energy.readFromNBT(compound.getCompoundTag("energy"));
		on = compound.getBoolean("on");
		logId = compound.getInteger("logId");
		nodeId = compound.getInteger("nodeId");
		storageId = compound.getInteger("storageId");
		deviceId = compound.getInteger("deviceId");
		productionId = compound.getInteger("productionId");
		packId = compound.getInteger("packId");
		classifyGroupId = compound.getInteger("classifyGroupId");
		inited = compound.getBoolean("inited");
		warningStorageFullCD = compound.getInteger("warningStorageFullCD");
		recipeTypeId = compound.getInteger("recipeTypeId");
		oreDictionaryId = compound.getInteger("oreDictionaryId");
		nodes.clear();
		NBTTagList list = compound.getTagList("nodes", 10);
		list.forEach(e -> {
			nodes.add(new SerialNumber((NBTTagCompound) e));
		});
		storages.clear();
		list = compound.getTagList("storges", 10);
		list.forEach(e -> {
			storages.add(new SerialNumber((NBTTagCompound) e));
		});
		devices.clear();
		list = compound.getTagList("devices", 10);
		list.forEach(e -> {
			devices.add(new SerialNumber((NBTTagCompound) e));
		});
		productions.clear();
		list = compound.getTagList("productions", 10);
		list.forEach(e -> {
			productions.add(new SerialNumber((NBTTagCompound) e));
		});
		serialNumberToPos.clear();
		list = compound.getTagList("serialNumbers", 10);
		list.forEach(e -> {
			serialNumberToPos.put(new SerialNumber((NBTTagCompound) ((NBTTagCompound) e).getTag("number")),
					new WorldPos((NBTTagCompound) ((NBTTagCompound) e).getTag("pos")));
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
			try {
				classifyGroups.put(((NBTTagCompound) e).getInteger("id"),
						new ClassifyGroup(((NBTTagCompound) e).getCompoundTag("group")));
			} catch (Exception ex) {
				Zone.logger.error("Classify Group " + Util.DATA_CORRUPTION
						+ " has throw an exception trying to read state. It's network data will be removed. Tag:{}", e,
						ex);
			}
		});
		users.clear();
		list = compound.getTagList("users", 10);
		list.forEach(e -> {
			try {
				UserData user = new UserData((NBTTagCompound) e);
				users.put(user.uuid, user);
			} catch (Exception ex) {
				Zone.logger.error("User " + Util.DATA_CORRUPTION
						+ " has throw an exception trying to read state. It's network data will be removed. Tag:{}", e,
						ex);
			}
		});
//		logCache.clear();//TODO
//		list = compound.getTagList("logs", 10);
//		list.forEach(e -> {
//			try {
//				logCache.add(Log.readLogFromNBT((NBTTagCompound) e));
//			} catch (Exception ex) {
//				Zone.logger.error("Log " + Util.DATA_CORRUPTION
//						+ " has throw an exception trying to read state. It's network data will be removed. Tag:{}", e,
//						ex);
//			}
//		});
		recipeTypes.clear();
		list = compound.getTagList("recipeTypes", 10);
		list.forEach(e -> {
			try {
				recipeTypes.put(((NBTTagCompound) e).getInteger("id"),
						new RecipeType(((NBTTagCompound) e).getCompoundTag("data")));
			} catch (Exception ex) {
				Zone.logger.error("Recipe Type " + Util.DATA_CORRUPTION
						+ " has throw an exception trying to read state. It's network data will be removed. Tag:{}", e,
						ex);
			}
		});
		recipes.clear();
		list = compound.getTagList("recipes", 10);
		list.forEach(e -> {
			try {
				recipes.put(((NBTTagCompound) e).getInteger("id"),
						new SphinxRecipe(((NBTTagCompound) e).getCompoundTag("data")));
			} catch (Exception ex) {
				Zone.logger.error("Recipe " + Util.DATA_CORRUPTION
						+ " has throw an exception trying to read state. It's network data will be removed. Tag:{}", e,
						ex);
			}
		});
		oreDictionarys.clear();
		list = compound.getTagList("oreDictionarys", 10);
		list.forEach(e -> {
			try {
				oreDictionarys.put(((NBTTagCompound) e).getInteger("id"),
						new OreDictionaryData(((NBTTagCompound) e).getCompoundTag("data")));
			} catch (Exception ex) {
				Zone.logger.error("Ore Dictionary Data " + Util.DATA_CORRUPTION
						+ " has throw an exception trying to read state. It's network data will be removed. Tag:{}", e,
						ex);
			}
		});
		super.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		if (name != null) {
			compound.setString("name", name);
		}
		compound.setTag("energy", energy.writeToNBT(new NBTTagCompound()));
		compound.setBoolean("on", on);
		compound.setInteger("logId", logId);
		compound.setInteger("nodeId", nodeId);
		compound.setInteger("storageId", storageId);
		compound.setInteger("deviceId", deviceId);
		compound.setInteger("productionId", productionId);
		compound.setInteger("packId", packId);
		compound.setInteger("classifyGroupId", classifyGroupId);
		compound.setBoolean("inited", inited);
		compound.setInteger("warningStorageFullCD", warningStorageFullCD);
		compound.setInteger("recipeTypeId", recipeTypeId);
		compound.setInteger("recipeId", recipeId);
		compound.setInteger("oreDictionaryId", oreDictionaryId);
		NBTTagList nodeList = new NBTTagList();
		nodes.forEach(e -> {
			e.check(this);
			nodeList.appendTag(e.writeToNBT(new NBTTagCompound()));
		});
		compound.setTag("nodes", nodeList);
		NBTTagList storgeList = new NBTTagList();
		storages.forEach(e -> {
			e.check(this);
			storgeList.appendTag(e.writeToNBT(new NBTTagCompound()));
		});
		compound.setTag("storges", storgeList);
		NBTTagList deviceList = new NBTTagList();
		devices.forEach(e -> {
			e.check(this);
			deviceList.appendTag(e.writeToNBT(new NBTTagCompound()));
		});
		compound.setTag("devices", deviceList);
		NBTTagList productionList = new NBTTagList();
		productions.forEach(e -> {
			e.check(this);
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
		NBTTagList serialNumberList = new NBTTagList();
		serialNumberToPos.forEach((k, v) -> {
			NBTTagCompound t = new NBTTagCompound();
			k.check(this);
			t.setTag("number", k.writeToNBT(new NBTTagCompound()));
			t.setTag("pos", v.writeToNBT(new NBTTagCompound()));
			serialNumberList.appendTag(t);
		});
		compound.setTag("serialNumbers", serialNumberList);
		NBTTagList classifyList = new NBTTagList();
		classifyGroups.forEach((k, v) -> {
			try {
				NBTTagCompound t = new NBTTagCompound();
				t.setInteger("id", k);
				t.setTag("group", v.writeToNBT(new NBTTagCompound()));
				classifyList.appendTag(t);
			} catch (Exception ex) {
				String name = Util.DATA_CORRUPTION;
				try {
					name = v.getName();
				} catch (Exception ex2) {

				}
				Zone.logger.error(
						"Classify Group {} has throw an exception trying to write state. It's network data will be removed.",
						name, ex);
			}
		});
		compound.setTag("classifyGroups", classifyList);
		NBTTagList usersList = new NBTTagList();
		users.values().forEach(e -> {
			try {
				usersList.appendTag(e.writeToNBT(new NBTTagCompound()));
			} catch (Exception ex) {
				String name = Util.DATA_CORRUPTION;
				try {
					name = e.name;
				} catch (Exception ex2) {

				}
				Zone.logger.error(
						"User {} has throw an exception trying to write state. It's network data will be removed.",
						name, ex);
			}
		});
		compound.setTag("users", usersList);
		NBTTagList logsList = new NBTTagList();
//		logCache.forEach(e -> {
//			try {
//				logsList.appendTag(e.writeToNBT(new NBTTagCompound()));
//			} catch (Exception ex) {
//				String name = Util.DATA_CORRUPTION;
//				try {
//					name = e.getId() + "";
//				} catch (Exception ex2) {
//
//				}
//				Zone.logger.error(
//						"Log {} has throw an exception trying to write state. It's network data will be removed.", name,
//						ex);
//			}
//		});//TODO
		compound.setTag("logs", logsList);
		NBTTagList recipeTypeList = new NBTTagList();
		recipeTypes.forEach((k, v) -> {
			try {
				NBTTagCompound t = new NBTTagCompound();
				t.setInteger("id", k);
				t.setTag("data", v.writeToNBT(new NBTTagCompound()));
				recipeTypeList.appendTag(t);
			} catch (Exception ex) {
				String name = Util.DATA_CORRUPTION;
				try {
					name = v.isI18N() ? I18n.format(v.getName()) : v.getName();
				} catch (Exception ex2) {

				}
				Zone.logger.error(
						"Recipe Type {} has throw an exception trying to write state. It's network data will be removed.",
						name, ex);
			}
		});
		compound.setTag("recipeTypes", recipeTypeList);
		NBTTagList recipeList = new NBTTagList();
		recipes.forEach((k, v) -> {
			try {
				NBTTagCompound t = new NBTTagCompound();
				t.setInteger("id", k);
				t.setTag("data", v.writeToNBT(new NBTTagCompound()));
				recipeList.appendTag(t);
			} catch (Exception ex) {
				String name = Util.DATA_CORRUPTION;
				Zone.logger.error(
						"Recipe {} has throw an exception trying to write state. It's network data will be removed.",
						name, ex);
			}
		});
		compound.setTag("recipes", recipeList);
		NBTTagList oreDictionaryList = new NBTTagList();
		oreDictionarys.forEach((k, v) -> {
			try {
				NBTTagCompound t = new NBTTagCompound();
				t.setInteger("id", k);
				t.setTag("data", v.writeToNBT(new NBTTagCompound()));
				oreDictionaryList.appendTag(t);
			} catch (Exception ex) {
				String name = Util.DATA_CORRUPTION;
				try {
					name = v.getName() == null ? Util.DATA_CORRUPTION : v.getName();
				} catch (Exception ex2) {

				}
				Zone.logger.error(
						"Recipe {} has throw an exception trying to write state. It's network data will be removed.",
						name, ex);
			}
		});
		compound.setTag("oreDictionarys", oreDictionaryList);
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
//		tag.setInteger("energy", energy);
		return tag;
	}

	public void readNetworkData(NBTTagCompound tag) {
//		energy = tag.getInteger("energy");
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
		WORKING("sphinx.working"), OFF("sphinx.off");

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

	public static class UserData {
		public UUID uuid;
		public String name, email = "";
		public boolean admin, reviewing, online;
		public WorldPos pos = WorldPos.ORIGIN;

		public UserData(UUID uuid, String name, boolean admin, boolean reviewing, boolean online) {
			this.uuid = uuid;
			this.name = name;
			this.admin = admin;
			this.reviewing = reviewing;
			this.online = online;
		}

		public UserData(EntityPlayer player, boolean admin, boolean reviewing, boolean online) {
			this(player.getUniqueID(), player.getName(), admin, reviewing, online);
			pos = new WorldPos(player);
		}

		public UserData(NBTTagCompound tag) {
			readFromNBT(tag);
		}

		public void updateData() {
			EntityPlayer player = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList()
					.getPlayerByUUID(uuid);
			if (player != null) {
				online = true;
				pos = new WorldPos(player);
			} else {
				online = false;
				pos = WorldPos.ORIGIN;
			}
		}

		public void readFromNBT(NBTTagCompound tag) {
			uuid = tag.getUniqueId("uuid");
			name = tag.getString("name");
			email = tag.getString("email");
			admin = tag.getBoolean("admin");
			reviewing = tag.getBoolean("reviewing");
			online = tag.getBoolean("online");
			pos = new WorldPos(tag.getCompoundTag("pos"));
		}

		public NBTTagCompound writeToNBT(NBTTagCompound tag) {
			tag.setUniqueId("uuid", uuid);
			tag.setString("name", name);
			tag.setString("email", email);
			tag.setBoolean("admin", admin);
			tag.setBoolean("reviewing", reviewing);
			tag.setBoolean("online", online);
			tag.setTag("pos", pos.writeToNBT(new NBTTagCompound()));
			return tag;
		}
	}
}
