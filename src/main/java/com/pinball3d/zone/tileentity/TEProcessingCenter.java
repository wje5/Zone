package com.pinball3d.zone.tileentity;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
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
import com.pinball3d.zone.sphinx.StorageWrapper;
import com.pinball3d.zone.sphinx.WorldPos;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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
	private Set<WorldPos> nodes = new HashSet<WorldPos>();
	private Set<WorldPos> storages = new HashSet<WorldPos>();
	private Set<WorldPos> devices = new HashSet<WorldPos>();
	private Set<WorldPos> productions = new HashSet<WorldPos>();
	private Set<LogisticPack> packs = new HashSet<LogisticPack>();
	private UUID uuid;

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
		markDirty();
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
		callUpdate();
		markDirty();
	}

	public void removeNeedNetwork(WorldPos pos) {
		nodes.remove(pos);
		storages.remove(pos);
		devices.remove(pos);
		productions.remove(pos);
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
			if (((TENode) it.next().getTileEntity()).isPointInRange(dim, x, y, z)) {
				return true;
			}
		}
		return false;
	}

	public boolean isDeviceInRange(World world, BlockPos pos) {
		if (this.world.provider.getDimension() != world.provider.getDimension()) {
			return false;
		}
		if (Math.sqrt(this.pos.distanceSq(pos.getX(), pos.getY(), pos.getZ())) < 25) {
			return true;
		}
		Iterator<WorldPos> it = nodes.iterator();
		while (it.hasNext()) {
			TENode te = (TENode) it.next().getTileEntity();
			if (!te.getPos().equals(pos)
					&& te.isPointInRange(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ())) {
				return true;
			}
		}
		return false;
	}

	public boolean consumeEnergy(int amount) {
		BlockPos p = pos.add(0, -3, 0);
		if (world.getBlockState(p).getBlock() == BlockLoader.transmission_module) {
			return ((TETransmissionModule) world.getTileEntity(p)).tryUseEnergy(amount, false);
		}
		return false;
	}

	public StorageWrapper getNetworkUseableItems() {
		StorageWrapper wrapper = new StorageWrapper();
		storages.forEach(e -> {
			TileEntity te = e.getTileEntity();
			if (te instanceof IStorable) {
				wrapper.merge(((IStorable) te).getStorages());
			}
		});
		return wrapper;
	}

	public void requestItems(StorageWrapper wrapper, WorldPos target) {
		System.out.println(wrapper.storges.iterator().next().writeToNBT(new NBTTagCompound()));
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
			packs.add(new LogisticPack(target, ((IStorable) e.getTileEntity()).extract(wrapper),
					new WorldPos(e.getTileEntity())));
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
			packs.add(new LogisticPack(new WorldPos(e.getTileEntity().getPos(), e.getTileEntity().getWorld()),
					((IStorable) e.getTileEntity()).insert(wrapper, true), pos));
		});
		callUpdate();
	}

	public void updateNode() {
		boolean flag = false;
		Set<WorldPos> temp = new HashSet<WorldPos>();
		do {
			Iterator<WorldPos> it = nodes.iterator();
			while (it.hasNext()) {
				flag = false;
				WorldPos i = it.next();
				if (i == null || i.getBlockState().getBlock() != BlockLoader.node) {
					continue;
				}
				if (!temp.contains(i) && i.getDim() == world.provider.getDimension()) {
					if (Math.sqrt(pos.distanceSq(i.getPos().getX(), i.getPos().getY(), i.getPos().getZ())) < 25) {
						flag = true;
						temp.add(i);
					} else {
						Iterator<WorldPos> it2 = temp.iterator();
						while (it2.hasNext()) {
							WorldPos nodepos = it2.next();
							if (nodepos.getBlockState().getBlock() == BlockLoader.node
									&& ((TENode) nodepos.getTileEntity()).isPointInRange(i.getDim(), i.getPos().getX(),
											i.getPos().getY(), i.getPos().getZ())) {
								flag = true;
								temp.add(i);
								break;
							}
						}
					}
				}
			}
		} while (flag);
		if (!temp.equals(nodes)) {
			nodes = temp;
			markDirty();
			callUpdate();
		}
	}

	public void updateDevice() {
		boolean flag = false;
		updateNode();
		Iterator<WorldPos> it = storages.iterator();
		while (it.hasNext()) {
			WorldPos pos = it.next();
			if (!(pos.getTileEntity() instanceof IStorable)) {
				it.remove();
				flag = true;
			} else if (!isDeviceInRange(pos.getWorld(), pos.getPos())) {
				((INeedNetwork) pos.getTileEntity()).setConnected(false);
				flag = true;
			} else {
				((INeedNetwork) pos.getTileEntity()).setConnected(true);
			}
		}
		it = devices.iterator();
		while (it.hasNext()) {
			WorldPos pos = it.next();
			if (!(pos.getTileEntity() instanceof IDevice)) {
				it.remove();
				flag = true;
			} else if (!isDeviceInRange(pos.getWorld(), pos.getPos())) {
				((INeedNetwork) pos.getTileEntity()).setConnected(false);
				flag = true;
			} else {
				((INeedNetwork) pos.getTileEntity()).setConnected(true);
			}
		}
		it = productions.iterator();
		while (it.hasNext()) {
			WorldPos pos = it.next();
			if (!(pos.getTileEntity() instanceof IProduction)) {
				it.remove();
				flag = true;
			} else if (!isDeviceInRange(pos.getWorld(), pos.getPos())) {
				((INeedNetwork) pos.getTileEntity()).setConnected(false);
				flag = true;
			} else {
				((INeedNetwork) pos.getTileEntity()).setConnected(true);
			}
		}
		if (flag) {
			markDirty();
			callUpdate();
		}
	}

	public StorageWrapper insertToItemHandler(StorageWrapper wrapper, IItemHandler handler) {
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

	public void updatePack() {
		Iterator<LogisticPack> it = packs.iterator();
		while (it.hasNext()) {
			LogisticPack i = it.next();
			if (i.forward(1D)) {
				TileEntity te = i.target.getTileEntity();
				IItemHandler handler;
				if (te instanceof IStorable) {
					StorageWrapper wrapper = insertToItemHandler(i.items, ((IStorable) te).getStorage());
				} else if (te != null) {
					StorageWrapper wrapper = insertToItemHandler(i.items,
							te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP));
					wrapper = insertToItemHandler(wrapper,
							te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.NORTH));
				}
				it.remove();
			}
			callUpdate();
		}
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
		updateDevice();
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
				break;
			}
		}
		energyTick--;
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
		return tag;
	}

	public void readNetworkData(NBTTagCompound tag) {

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
}
