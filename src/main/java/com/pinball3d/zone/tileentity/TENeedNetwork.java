package com.pinball3d.zone.tileentity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.pinball3d.zone.ChunkHandler;
import com.pinball3d.zone.ChunkHandler.IChunkLoader;
import com.pinball3d.zone.sphinx.GlobalNetworkData;
import com.pinball3d.zone.util.WorldPos;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.common.util.Constants;

public class TENeedNetwork extends TileEntity implements INeedNetwork, ITickable, IChunkLoader {
	protected WorldPos worldpos = WorldPos.ORIGIN;
	protected UUID network;
	protected String password = "";
	protected boolean connected = false;
	private boolean loaded;

	@SuppressWarnings("deprecation")
	public void callUpdate() {
		markDirty();
		IBlockState state = getBlockType().getStateFromMeta(getBlockMetadata());
		world.notifyBlockUpdate(pos, state, state,
				Constants.BlockFlags.SEND_TO_CLIENTS | Constants.BlockFlags.NO_RERENDER);
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
		if (world.isRemote) {
			return;
		}
		if (network != null && worldpos.isOrigin()) {
			worldpos = GlobalNetworkData.getData(world).getNetwork(network);
			callUpdate();
		}
		if (!worldpos.isOrigin()) {
			if (worldpos.getTileEntity() instanceof TEProcessingCenter) {
				if (((TEProcessingCenter) worldpos.getTileEntity()).isOn()) {
					if (connected) {
						work();
					}
				} else {
					setConnected(false);
				}
			} else {
				deleteNetwork();
				callUpdate();
			}
		}
	}

	public void work() {

	}

	@Override
	public void connect(UUID uuid) {
		network = uuid;
		worldpos = WorldPos.ORIGIN;
		connected = true;
		callUpdate();
	}

	@Override
	public UUID getNetwork() {
		return network;
	}

	@Override
	public void setWorldPos(WorldPos pos, UUID uuid) {
		if (uuid.equals(network)) {
			worldpos = pos;
			if (worldpos.isOrigin()) {
				deleteNetwork();
			}
			callUpdate();
		}
	}

	@Override
	public WorldPos getNetworkPos() {
		return worldpos;
	}

	@Override
	public boolean isConnected() {
		return connected;
	}

	@Override
	public void setConnected(boolean connected) {
		this.connected = connected;
		callUpdate();
	}

	@Override
	public void deleteNetwork() {
		network = null;
		worldpos = WorldPos.ORIGIN;
		password = "";
		connected = false;
		callUpdate();
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
		password = compound.getString("password");
		connected = compound.getBoolean("connected");
		super.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		if (network != null) {
			compound.setUniqueId("network", network);
		}
		compound.setString("password", password);
		compound.setBoolean("connected", connected);
		return super.writeToNBT(compound);

	}

	public NBTTagCompound writeNetworkData(NBTTagCompound tag) {
		if (!worldpos.isOrigin()) {
			tag.setTag("networkPos", worldpos.writeToNBT(new NBTTagCompound()));
		}
		return tag;
	}

	public void readNetworkData(NBTTagCompound tag) {
		if (tag.hasKey("networkPos")) {
			worldpos = new WorldPos(tag.getCompoundTag("networkPos"));
		} else {
			worldpos = WorldPos.ORIGIN;
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
	public Set<ChunkPos> getLoadChunks() {
		Set<ChunkPos> s = new HashSet<ChunkPos>();
		s.add(new ChunkPos(pos));
		return s;
	}
}
