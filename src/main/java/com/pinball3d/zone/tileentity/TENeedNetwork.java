package com.pinball3d.zone.tileentity;

import java.util.UUID;

import com.pinball3d.zone.sphinx.GlobalNetworkData;
import com.pinball3d.zone.sphinx.WorldPos;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.util.Constants;

public class TENeedNetwork extends TileEntity implements INeedNetwork, ITickable {
	protected WorldPos worldpos;
	protected UUID network;
	protected String password = "";
	protected boolean connected = true;

	public void callUpdate() {
		markDirty();
		IBlockState state = getBlockType().getStateFromMeta(getBlockMetadata());
		world.notifyBlockUpdate(pos, state, state,
				Constants.BlockFlags.SEND_TO_CLIENTS | Constants.BlockFlags.NO_RERENDER);
	}

	@Override
	public void update() {
		if (world.isRemote) {
			return;
		}
		if (network != null && worldpos == null) {
			worldpos = GlobalNetworkData.getData(world).getNetwork(network);
			callUpdate();
		}
		if (worldpos != null) {
			if (worldpos.getTileEntity() instanceof TEProcessingCenter) {
				if (((TEProcessingCenter) worldpos.getTileEntity()).isOn()) {
					work();
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
	public void connect(UUID uuid, String password) {
		network = uuid;
		worldpos = null;
		this.password = password;
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
				deleteNetwork();
			}
			markDirty();
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
	}

	@Override
	public void deleteNetwork() {
		network = null;
		worldpos = null;
		password = "";
		markDirty();
		callUpdate();
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		if (compound.hasKey("networkMost")) {
			network = compound.getUniqueId("network");
		}
		password = compound.getString("password");
		super.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		if (network != null) {
			compound.setUniqueId("network", network);
		}
		compound.setString("password", password);
		return super.writeToNBT(compound);

	}

	public NBTTagCompound writeNetworkData(NBTTagCompound tag) {
		if (worldpos != null) {
			worldpos.writeToNBT(tag);
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
}
