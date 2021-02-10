package com.pinball3d.zone.tileentity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.pinball3d.zone.ChunkHandler;
import com.pinball3d.zone.ChunkHandler.IChunkLoader;
import com.pinball3d.zone.sphinx.GlobalNetworkData;
import com.pinball3d.zone.util.WorldPos;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.ChunkPos;

public class TENeedNetwork extends TileEntity implements INeedNetwork, ITickable, IChunkLoader {
	protected UUID network;
	protected boolean connected = false;
	private boolean loaded;

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
		if (world.isRemote) {
			return;
		}
		if (network != null) {
			WorldPos pcpos = GlobalNetworkData.getPos(network);
			if (!pcpos.isOrigin() && pcpos.getTileEntity() instanceof TEProcessingCenter) {
				if (((TEProcessingCenter) pcpos.getTileEntity()).isOn()) {
					if (connected) {
						work();
					}
				} else {
					setConnected(false);
				}
			} else {
				deleteNetwork();
			}
		}
	}

	public void work() {

	}

	@Override
	public void connect(UUID uuid) {
		network = uuid;
		connected = true;
	}

	@Override
	public UUID getNetwork() {
		return network;
	}

	@Override
	public boolean isConnected() {
		return connected;
	}

	@Override
	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	@Override
	public void deleteNetwork() {
		network = null;
		connected = false;
	}

	@Override
	public WorkingState getWorkingState() {
		return isConnected() ? WorkingState.WORKING : WorkingState.DISCONNECTED;
	}

	@Override
	public String getName() {
		return getBlockType().getLocalizedName();
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		if (compound.hasKey("networkMost")) {
			network = compound.getUniqueId("network");
		}
		connected = compound.getBoolean("connected");
		super.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		if (network != null) {
			compound.setUniqueId("network", network);
		}
		compound.setBoolean("connected", connected);
		return super.writeToNBT(compound);

	}

	@Override
	public Set<ChunkPos> getLoadChunks() {
		Set<ChunkPos> s = new HashSet<ChunkPos>();
		s.add(new ChunkPos(pos));
		return s;
	}
}
