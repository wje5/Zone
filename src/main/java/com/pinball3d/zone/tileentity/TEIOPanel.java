package com.pinball3d.zone.tileentity;

import java.util.Iterator;
import java.util.UUID;

import com.pinball3d.zone.block.BlockProcessingCenter;
import com.pinball3d.zone.sphinx.GlobalNetworkData;
import com.pinball3d.zone.sphinx.HugeItemStack;
import com.pinball3d.zone.sphinx.IDevice;
import com.pinball3d.zone.sphinx.StorageWrapper;
import com.pinball3d.zone.sphinx.WorldPos;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class TEIOPanel extends TileEntity implements INeedNetwork, ITickable, IDevice {
	private WorldPos worldpos;
	private UUID network;
	private IItemHandler inv, global;
	public StorageWrapper storges = new StorageWrapper();
	public int page = 1;

	public TEIOPanel() {
		super();
		inv = new ItemStackHandler(54);
		global = new ItemStackHandler(36);
	}

	public void callUpdate() {
		markDirty();
		IBlockState state = getBlockType().getStateFromMeta(getBlockMetadata());
		world.notifyBlockUpdate(pos, state, state,
				Constants.BlockFlags.SEND_TO_CLIENTS | Constants.BlockFlags.NO_RERENDER);
	}

	public void pageUp() {
		if (page > 1) {
			page--;
			callUpdate();
		}
	}

	public void pageDown() {
		int maxpage = (storges.storges.size() + storges.other.size() - 1) / 36 + 1;
		if (page < maxpage) {
			page++;
			callUpdate();
		}
	}

	@Override
	public void update() {
		markDirty();
		if (!world.isRemote && network != null && worldpos == null) {
			worldpos = GlobalNetworkData.getData(world).getNetwork(network);
			callUpdate();
		}
		if (worldpos != null) {
			if (worldpos.getTileEntity() != null && !((TEProcessingCenter) worldpos.getTileEntity()).isOn()
					&& !world.isRemote) {
				((TEProcessingCenter) worldpos.getTileEntity()).removeNeedNetwork(new WorldPos(pos, world));
				disconnect();
			}
		}
		if (!world.isRemote) {
			updateGlobalStorges();
		}
	}

	public void updateGlobalStorges() {
		if (worldpos != null) {
			int maxpage = (storges.storges.size() + storges.other.size() - 1) / 36 + 1;
			StorageWrapper wrapper = ((TEProcessingCenter) worldpos.getTileEntity()).getNetworkUseableItems();
			for (int i = 0; i < 36; i++) {
				global.extractItem(i, 64, false);
			}
			int index = 0;
			int offset = (page - 1) * 36;
			Iterator<HugeItemStack> it = wrapper.storges.iterator();
			while (it.hasNext()) {
				if (offset > 0) {
					offset--;
					it.next();
					continue;
				}
				ItemStack stack = it.next().stack.copy();
				stack.setCount(1);
				if (index < 36) {
					global.insertItem(index, stack, false);
				}
				index++;
			}
			Iterator<ItemStack> it2 = wrapper.other.iterator();
			while (it2.hasNext()) {
				if (offset > 0) {
					offset--;
					it2.next();
					continue;
				}
				ItemStack stack = it2.next().copy();
				stack.setCount(1);
				if (index < 36) {
					global.insertItem(index, stack, false);
				}
				index++;
			}
			storges = wrapper;
			markDirty();
			if ((storges.storges.size() + storges.other.size() - 1) / 36 + 1 != maxpage) {
				callUpdate();
			}
		}
		int maxpage = (storges.storges.size() + storges.other.size() - 1) / 36 + 1;
		if (page > maxpage) {
			page = maxpage;
			callUpdate();
		}
		if (page < 1) {
			page = 1;
			callUpdate();
		}
	}

	public IItemHandler getGlobalInv() {
		return global;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.equals(capability)) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.equals(capability)) {
			return (T) inv;
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		if (compound.hasKey("networkMost")) {
			network = compound.getUniqueId("network");
		}
		page = compound.getInteger("page");
		super.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		if (network != null) {
			compound.setUniqueId("network", network);
		}
		compound.setInteger("page", page);
		return super.writeToNBT(compound);

	}

	public NBTTagCompound writeNetworkData(NBTTagCompound tag) {
		if (worldpos != null) {
			worldpos.save(tag);
		}
		tag.setTag("storges", storges.writeToNBT(new NBTTagCompound()));
		return tag;
	}

	public void readNetworkData(NBTTagCompound tag) {
		if (tag.hasKey("worldpos")) {
			worldpos = WorldPos.load(tag);
		} else {
			worldpos = null;
		}
		storges.readFromNBT(tag.getCompoundTag("storges"));
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(pos, 1, getUpdateTag());
	}

	@Override
	public void onDataPacket(NetworkManager manager, SPacketUpdateTileEntity packet) {
		handleUpdateTag(packet.getNbtCompound());
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
	public void connect(UUID uuid) {
		network = uuid;
		markDirty();
	}

	@Override
	public UUID getNetwork() {
		return network;
	}

	@Override
	public void setWorldPos(WorldPos pos, UUID uuid) {
		if (uuid.equals(network)) {
			worldpos = pos;
			if (worldpos == null) {
				disconnect();
			}
		}
	}

	@Override
	public WorldPos getNetworkPos() {
		return worldpos;
	}

	@Override
	public void disconnect() {
		network = null;
		worldpos = null;
		markDirty();
		callUpdate();
	}

	@Override
	public boolean isConnected() {
		if (getNetworkPos() != null) {
			if (getNetworkPos().getBlockState().getBlock() instanceof BlockProcessingCenter) {
				return true;
			} else {
				disconnect();
			}
		}
		return false;
	}
}
