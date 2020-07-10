package com.pinball3d.zone.tileentity;

import java.util.UUID;

import com.pinball3d.zone.block.BlockProcessingCenter;
import com.pinball3d.zone.sphinx.GlobalNetworkData;
import com.pinball3d.zone.sphinx.IStorable;
import com.pinball3d.zone.sphinx.StorageWrapper;
import com.pinball3d.zone.sphinx.WorldPos;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class TEStorageChest extends TileEntity implements INeedNetwork, ITickable, IStorable {
	private WorldPos worldpos;
	private UUID network;
	private ItemStackHandler inv;

	public TEStorageChest() {
		super();
		inv = new ItemStackHandler(81);
	}

	@Override
	public void update() {
		markDirty();
		if (world.isRemote) {
			return;
		}
		if (network != null && worldpos == null) {
			worldpos = GlobalNetworkData.getData(world).getNetwork(network);
			callUpdate();
		}
		if (worldpos != null) {
			if (worldpos.getTileEntity() != null && !((TEProcessingCenter) worldpos.getTileEntity()).isOn()) {
				((TEProcessingCenter) worldpos.getTileEntity()).removeNeedNetwork(new WorldPos(pos, world));
				disconnect();
			}
		}
	}

	public void callUpdate() {
		IBlockState state = getBlockType().getStateFromMeta(getBlockMetadata());
		world.notifyBlockUpdate(pos, state, state,
				Constants.BlockFlags.SEND_TO_CLIENTS | Constants.BlockFlags.NO_RERENDER);
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
		inv.deserializeNBT(compound.getCompoundTag("inv"));
		super.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		if (network != null) {
			compound.setUniqueId("network", network);
		}
		compound.setTag("inv", inv.serializeNBT());
		return super.writeToNBT(compound);

	}

	public NBTTagCompound writeNetworkData(NBTTagCompound tag) {
		if (worldpos != null) {
			worldpos.save(tag);
		}
		return tag;
	}

	public void readNetworkData(NBTTagCompound tag) {
		if (tag.hasKey("worldpos")) {
			worldpos = WorldPos.load(tag);
		} else {
			worldpos = null;
		}
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
			markDirty();
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

	@Override
	public StorageWrapper getStorges() {
		return new StorageWrapper(inv);
	}
}
