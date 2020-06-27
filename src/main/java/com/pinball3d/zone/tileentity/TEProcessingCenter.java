package com.pinball3d.zone.tileentity;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import com.pinball3d.zone.block.BlockLoader;
import com.pinball3d.zone.block.BlockProcessingCenter;
import com.pinball3d.zone.sphinx.GlobalNetworkData;
import com.pinball3d.zone.sphinx.WorldPos;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.util.Constants;

public class TEProcessingCenter extends TileEntity implements ITickable {
	private boolean on;
	private boolean init;
	private String name = "";
	private String adminPassword = "";
	private String loginPassword = "";
	private int loadTick;
	private Set<WorldPos> nodes = new HashSet<WorldPos>();
	private UUID uuid;

	public TEProcessingCenter() {

	}

	public boolean needInit() {
		return !init;
	}

	public boolean isOn() {
		return on;
	}

	public void shutdown() {
		on = false;
		BlockProcessingCenter.setState(false, world, pos);
		markDirty();
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
		if (blockType == BlockLoader.processing_center && loadTick <= 0) {
			loadTick = 256;
			BlockProcessingCenter.setState(true, world, pos);
			markDirty();
		}
	}

	public void saveWizardData(String adminPassword, String name, String loginPassword) {
		if (adminPassword.length() == 8 && name.length() >= 4 && name.length() <= 8 && loginPassword.length() == 8) {
			this.adminPassword = adminPassword;
			this.name = name;
			this.loginPassword = loginPassword;
			init = true;
			markDirty();
		}
	}

	public void setAdminPassword(String password) {
		if (password.length() == 8) {
			adminPassword = password;
			markDirty();
		}
	}

	public void setPassword(String password) {
		if (password.length() == 8) {
			loginPassword = password;
			markDirty();
		}
	}

	public void setName(String name) {
		if (name.length() >= 4) {
			this.name = name;
			markDirty();
		}
	}

	public void addNode(WorldPos pos) {
		nodes.forEach(e -> {
			if (e.equals(pos)) {
				return;
			}
		});
		nodes.add(pos);
		markDirty();
	}

	public void removeNode(WorldPos pos) {
		nodes.remove(pos);
		markDirty();
	}

	public Set<WorldPos> getNodes() {
		return nodes;
	}

	public UUID getUUID() {
		return uuid;
	}

	public void setUUID(UUID uuid) {
		this.uuid = uuid;
		markDirty();
	}

	public boolean isPointInRange(int dim, double x, double y, double z) {
		if (world.provider.getDimension() != dim) {
			return false;
		}
		if (Math.sqrt(pos.distanceSq(x, y, z)) < 25) {
			return true;
		}
		Consumer<WorldPos> k = e -> {
		};
		Iterator<WorldPos> it = nodes.iterator();
		while (it.hasNext()) {
			if (((TENode) it.next().getTileEntity()).isPointInRange(dim, x, y, z)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void update() {
		markDirty();
		if (!((BlockProcessingCenter) blockType).isFullStructure(world, pos)) {
			shutdown();
			return;
		}
		if (uuid == null) {
			if (!world.isRemote) {
				setUUID(GlobalNetworkData.getData(world).getUUID(new WorldPos(getPos(), world)));
				IBlockState state = getBlockType().getStateFromMeta(getBlockMetadata());
				world.notifyBlockUpdate(pos, state, state,
						Constants.BlockFlags.SEND_TO_CLIENTS | Constants.BlockFlags.NO_RERENDER);
			}
		}
		if (loadTick > 0) {
			loadTick--;
			if (loadTick == 0) {
				on = true;
			}
		}
		if (!on) {
			return;
		}
		updateDevice();
	}

	public void updateDevice() {
		nodes.forEach(e -> {
			// TODO
		});
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		init = compound.getBoolean("init");
		name = compound.getString("name");
		adminPassword = compound.getString("adminPassword");
		loginPassword = compound.getString("loginPassword");
		loadTick = compound.getInteger("loadTick");
		on = compound.getBoolean("on");
		NBTTagList list = compound.getTagList("nodes", 9);
		list.forEach(e -> {
			nodes.add(WorldPos.load((NBTTagCompound) e));
		});
		if (compound.hasKey("uuidMost")) {
			uuid = compound.getUniqueId("uuid");
		}
		super.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setBoolean("init", init);
		compound.setString("name", name);
		compound.setString("adminPassword", adminPassword);
		compound.setString("loginPassword", loginPassword);
		compound.setInteger("loadTick", loadTick);
		compound.setBoolean("on", on);
		NBTTagList list = new NBTTagList();
		nodes.forEach(e -> {
			list.appendTag(e.save(new NBTTagCompound()));
		});
		if (uuid != null) {
			compound.setUniqueId("uuid", uuid);
		}
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
