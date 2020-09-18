package com.pinball3d.zone.tileentity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import com.pinball3d.zone.block.BlockLoader;
import com.pinball3d.zone.block.BlockProcessingCenter;
import com.pinball3d.zone.sphinx.GlobalNetworkData;
import com.pinball3d.zone.sphinx.HugeItemStack;
import com.pinball3d.zone.sphinx.IDevice;
import com.pinball3d.zone.sphinx.IProduction;
import com.pinball3d.zone.sphinx.IStorable;
import com.pinball3d.zone.sphinx.LogisticPack;
import com.pinball3d.zone.sphinx.LogisticPack.Path;
import com.pinball3d.zone.sphinx.StorageWrapper;
import com.pinball3d.zone.sphinx.WorldPos;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class TEProcessingCenter extends TileEntity implements ITickable {
	private boolean on;
	private boolean init;
	private String name = "";
	private String adminPassword = "";
	private String loginPassword = "";
	private int loadTick;
	private int energyTick;
	private int energy;
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

	public TEProcessingCenter() {

	}

	public boolean needInit() {
		return !init;
	}

	public boolean isOn() {
		return on;
	}

	public void callUpdate() {
		markDirty();
		IBlockState state = getBlockType().getStateFromMeta(getBlockMetadata());
		world.notifyBlockUpdate(pos, state, state,
				Constants.BlockFlags.SEND_TO_CLIENTS | Constants.BlockFlags.NO_RERENDER);
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

	public boolean isCorrectAdminPassword(String password) {
		return password.equals(adminPassword);
	}

	public boolean isCorrectLoginPassword(String password) {
		return password.equals(loginPassword);
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

	public void saveWizardData(String adminPassword, String name, String loginPassword) {
		if (adminPassword.length() == 8 && name.length() >= 4 && name.length() <= 8 && loginPassword.length() == 8) {
			this.adminPassword = adminPassword;
			this.name = name;
			this.loginPassword = loginPassword;
			init = true;
			callUpdate();
			markDirty();
		}
	}

	public void setAdminPassword(String password) {
		if (password.length() == 8) {
			adminPassword = password;
			callUpdate();
			markDirty();
		}
	}

	public void setPassword(String password) {
		if (password.length() == 8) {
			loginPassword = password;
			nodes.forEach(e -> {
				TileEntity te = e.getTileEntity();
				if (te instanceof INeedNetwork) {
					((INeedNetwork) te).setPassword(password);
				}
			});
			storages.forEach(e -> {
				TileEntity te = e.getTileEntity();
				if (te instanceof INeedNetwork) {
					((INeedNetwork) te).setPassword(password);
				}
			});
			devices.forEach(e -> {
				TileEntity te = e.getTileEntity();
				if (te instanceof INeedNetwork) {
					((INeedNetwork) te).setPassword(password);
				}
			});
			productions.forEach(e -> {
				TileEntity te = e.getTileEntity();
				if (te instanceof INeedNetwork) {
					((INeedNetwork) te).setPassword(password);
				}
			});
			callUpdate();
			markDirty();
		}
	}

	public void setName(String name) {
		if (name.length() >= 4) {
			this.name = name;
			callUpdate();
			markDirty();
		}
	}

	public void addNeedNetwork(WorldPos pos) {
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
		if (te instanceof TENode) {
			nodes.add(pos);
		} else if (te instanceof IStorable) {
			storages.add(pos);
		} else if (te instanceof IDevice) {
			devices.add(pos);
		} else if (te instanceof IProduction) {
			productions.add(pos);
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
		if (world.provider.getDimension() != dim) {
			return false;
		}
		if (Math.sqrt(pos.distanceSq(x, y, z)) < 25) {
			return true;
		}
		Iterator<WorldPos> it = nodes.iterator();
		while (it.hasNext()) {
			TileEntity te = it.next().getTileEntity();
			if (te instanceof TENode) {
				TENode i = (TENode) te;
				if (i.isConnected() && i.isPointInRange(dim, x, y, z)) {
					return true;
				}
			} else {
				it.remove();
			}
		}
		return false;
	}

	public boolean isDeviceInRange(WorldPos pos) {
		if (world.provider.getDimension() != pos.getDim()) {
			return false;
		}
		if (Math.sqrt(this.pos.distanceSq(pos.getPos().getX(), pos.getPos().getY(), pos.getPos().getZ())) < 25) {
			return true;
		}
		Iterator<WorldPos> it = nodes.iterator();
		while (it.hasNext()) {
			TENode te = (TENode) it.next().getTileEntity();
			if (!te.getPos().equals(pos.getPos()) && te.isConnected()
					&& te.isPointInRange(pos.getDim(), pos.getPos().getX(), pos.getPos().getY(), pos.getPos().getZ())) {
				return true;
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
				wrapper.merge(((IStorable) te).getStorages());
			}
		});
		return wrapper;
	}

	public void requestItems(StorageWrapper wrapper, WorldPos target) {
		TreeSet<WorldPos> sortset = new TreeSet<WorldPos>(new Comparator<WorldPos>() {
			@Override
			public int compare(WorldPos o1, WorldPos o2) {
				double dist1 = o1.getPos().distanceSq(target.getPos());
				double dist2 = o2.getPos().distanceSq(target.getPos());
				return dist1 > dist2 ? 1 : dist1 < dist2 ? -1 : o1.hashCode() > o2.hashCode() ? 1 : -1;
			}
		});
		sortset.addAll(storages);
		sortset.forEach(e -> {
			TileEntity te = e.getTileEntity();
			if (te instanceof INeedNetwork && ((INeedNetwork) te).isConnected() && te instanceof IStorable) {
				StorageWrapper w = ((IStorable) te).extract(wrapper);
				if (!w.isEmpty()) {
					List<Path> l = dijkstra(e);
					for (Path i : l) {
						System.out.println(i.routes);
						if (i.getTarget().equals(target)) {
							LogisticPack pack = new LogisticPack(i.routes, w, new WorldPos(te));
							packs.add(pack);
							System.out.println(pack.routes);
						}
					}
				}
			}
		});
		callUpdate();
	}

	public void dispenceItems(StorageWrapper wrapper, WorldPos pos) {
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
//					packs.add(new LogisticPack(new WorldPos(te), w, pos));
					List<Path> l = dijkstra(pos);
					for (Path i : l) {
						if (i.getTarget().equals(e)) {
							LogisticPack pack = new LogisticPack(i.routes, w, new WorldPos(te));
							packs.add(pack);
						}
					}
				}
			}
		});
		callUpdate();
	}

	public boolean updateNode() {
		boolean flag = false;
		Set<WorldPos> temp = new HashSet<WorldPos>();
		do {
			flag = false;
			Iterator<WorldPos> it = nodes.iterator();
			while (it.hasNext()) {
				WorldPos i = it.next();
				if (i == null || i.getBlockState().getBlock() != BlockLoader.node) {
					continue;
				}
				if (!temp.contains(i) && i.getDim() == world.provider.getDimension()) {
					if (Math.sqrt(pos.distanceSq(i.getPos().getX(), i.getPos().getY(), i.getPos().getZ())) < 25) {
						flag = true;
						temp.add(i);
						((TENode) i.getTileEntity()).setConnected(true);
					} else {
						Iterator<WorldPos> it2 = temp.iterator();
						while (it2.hasNext()) {
							WorldPos nodepos = it2.next();
							if (((TENode) nodepos.getTileEntity()).isPointInRange(i.getDim(), i.getPos().getX(),
									i.getPos().getY(), i.getPos().getZ())) {
								flag = true;
								temp.add(i);
								((TENode) i.getTileEntity()).setConnected(true);
								break;
							}
						}
					}
				}
			}
		} while (flag);
		Set<WorldPos> l = new HashSet<WorldPos>();
		nodes.forEach(e -> {
			TileEntity te = e.getTileEntity();
			if (te instanceof TENode && ((TENode) te).isConnected()) {
				l.add(e);
			}
		});
		if (!temp.equals(l)) {
			Iterator<WorldPos> it = nodes.iterator();
			while (it.hasNext()) {
				WorldPos e = it.next();
				if (!temp.contains(e)) {
					if (e != null && e.getBlockState().getBlock() == BlockLoader.node) {
						((INeedNetwork) e.getTileEntity()).setConnected(false);
					} else {
						it.remove();
					}
				}
			}
			markDirty();
			callUpdate();
			return true;
		}
		return false;
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
		if (needInit()) {
			return WorkingState.UNINIT;
		}
		return WorkingState.WORKING;
	}

	public int getEnergy() {
		return energy;
	}

	public void refreshMap() {
		List<WorldPos> list = new ArrayList<WorldPos>();
		nodes.forEach(e -> {
			TileEntity te = e.getTileEntity();
			if (te instanceof TENode && ((TENode) te).isConnected()) {
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
			double dist = Math.sqrt(this.pos.distanceSq(pos.getPos().getX(), pos.getPos().getY(), pos.getPos().getZ()));
			if (dist < 25) {
				map[0][i + 1] = dist;
				map[i + 1][0] = dist;
			}
		}
		for (int i = 0; i < list.size(); i++) {
			WorldPos pos = list.get(i);
			TileEntity tileentity = pos.getTileEntity();
			if (tileentity instanceof TENode) {
				for (int j = 0; j < list.size(); j++) {
					WorldPos pos2 = list.get(j);
					if (tileentity.getWorld().provider.getDimension() == pos.getDim()) {
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
		for (int i = 0; i < list.size() + 1; i++) {
			for (int j = 0; j < list.size() + 1; j++) {
				System.out.print("\t" + (map[i][j] == Double.MAX_VALUE ? "M" : (int) map[i][j]));
			}
			System.out.println();
		}
	}

	public List<Path> dijkstra(WorldPos pos) {
		if (map == null) {
			refreshMap();
		}
		List<WorldPos> list = new ArrayList<WorldPos>();
		nodes.forEach(e -> {
			TileEntity te = e.getTileEntity();
			if (te instanceof TENode && ((TENode) te).isConnected()) {
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
		if (index == -1) {
			throw new RuntimeException("wrong pos:" + pos);
		}
		double[] dist = map[index];
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
				System.out.println(dist);
				System.out.println(t);
				System.out.println(path);
				List<Path> r = new ArrayList<Path>();
				for (int j = 0; j < path.length; j++) {
					List<WorldPos> l = new ArrayList<WorldPos>();
					for (int k = 0; k < path[j].length; k++) {
						l.add(path[j][k] > 0 ? list.get(path[j][k] - 1) : new WorldPos(this));
					}
					l.add(j > 0 ? list.get(j - 1) : new WorldPos(this));
					r.add(new Path(l, dist[j]));
				}
				return r;
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

	public void updatePack() {
		Iterator<LogisticPack> it = packs.iterator();
		Set<LogisticPack> deads = new HashSet<LogisticPack>();
		while (it.hasNext()) {
			LogisticPack i = it.next();
			if (!isPointInRange(i.dim, i.x, i.y, i.z)) {
				it.remove();
				callUpdate();
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
					System.out.println(te.getPos() + "|" + wrapper);
				}
				it.remove();
			}
		}
		deads.forEach(e -> {
			dispenceItems(e.items, new WorldPos((int) e.x, (int) e.y, (int) e.z, e.dim));
		});
		callUpdate();
	}

	@Override
	public void update() {
		markDirty();
		if (world.isRemote) {
			if (loadTick > 0) {
				loadTick--;
			}
			return;
		}
		if (!((BlockProcessingCenter) blockType).isFullStructure(world, pos)) {
			shutdown();
			return;
		}
		if (uuid == null) {
			if (!world.isRemote) {
				setUUID(GlobalNetworkData.getData(world).getUUID(new WorldPos(getPos(), world)));
				callUpdate();
			}
		}
		if (loadTick > 0) {
			if (consumeEnergy(1)) {
				loadTick--;
				if (loadTick == 0) {
					on = true;
					callUpdate();
				}
			} else {
				shutdown();
			}
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
		if (updateDevice() || map == null) {
			refreshMap();
		}
		updatePack();
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		init = compound.getBoolean("init");
		name = compound.getString("name");
		adminPassword = compound.getString("adminPassword");
		loginPassword = compound.getString("loginPassword");
		loadTick = compound.getInteger("loadTick");
		energyTick = compound.getInteger("energyTick");
		on = compound.getBoolean("on");
		nodes.clear();
		NBTTagList list = compound.getTagList("nodes", 10);
		list.forEach(e -> {
			nodes.add(WorldPos.load((NBTTagCompound) e));
		});
		storages.clear();
		list = compound.getTagList("storges", 10);
		list.forEach(e -> {
			storages.add(WorldPos.load((NBTTagCompound) e));
		});
		devices.clear();
		list = compound.getTagList("devices", 10);
		list.forEach(e -> {
			devices.add(WorldPos.load((NBTTagCompound) e));
		});
		productions.clear();
		list = compound.getTagList("productions", 10);
		list.forEach(e -> {
			productions.add(WorldPos.load((NBTTagCompound) e));
		});
		if (compound.hasKey("uuidMost")) {
			uuid = compound.getUniqueId("uuid");
		}
		packs.clear();
		list = compound.getTagList("packs", 10);
		list.forEach(e -> {
			packs.add(new LogisticPack((NBTTagCompound) e));
		});
		super.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setBoolean("init", init);
		compound.setString("name", name);
		compound.setString("adminPassword", adminPassword);
		compound.setString("loginPassword", loginPassword);
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

	public static enum WorkingState {
		WORKING("sphinx.working"), OFF("sphinx.off"), STARTING("sphinx.starting"), UNINIT("sphinx.uninit");

		String key;

		private WorkingState(String key) {
			this.key = key;
		}

		@Override
		public String toString() {
			return I18n.format(key);
		}
	}
}
