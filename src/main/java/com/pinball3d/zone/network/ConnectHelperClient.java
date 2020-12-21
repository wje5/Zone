package com.pinball3d.zone.network;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.google.common.collect.Sets;
import com.pinball3d.zone.network.ConnectionHelper.Type;
import com.pinball3d.zone.sphinx.MapHandler;
import com.pinball3d.zone.sphinx.StorageWrapper;
import com.pinball3d.zone.sphinx.WorldPos;
import com.pinball3d.zone.tileentity.TEProcessingCenter.WorkingState;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ConnectHelperClient {
	private static ConnectHelperClient instance = new ConnectHelperClient();
	private Set<Type> types = new HashSet<Type>();
	private boolean hasData;
	private UUID network, networkFromController;
	private StorageWrapper items = new StorageWrapper();
	private String password = "";
	private String adminPassword = "";
	private WorldPos networkPos = WorldPos.ORIGIN;
	private Map<WorldPos, String> playerValidNetworks = new HashMap<WorldPos, String>();
	private Map<WorldPos, String> needNetworkValidNetworks = new HashMap<WorldPos, String>();
	private String name = "";
	private int loadTick, usedStorage, maxStorage;
	private boolean on, inited;
	private WorkingState workingState;

	public ConnectHelperClient() {

	}

	public void setData(UUID network, NBTTagCompound data, Set<Type> types) {
		if (!this.types.equals(types)) {
			return;
		}
		clear();
		this.network = network;
		for (Type e : Type.values()) {
			if (data.hasKey(e.name())) {
				switch (e) {
				case NETWORKUUID:
					network = data.getUniqueId(e.name());
					if (network.getMostSignificantBits() == 0 && network.getLeastSignificantBits() == 0) {
						network = null;
					}
					break;
				case ITEMS:
					if (data.hasKey(e.name())) {
						NBTTagCompound tag = data.getCompoundTag(e.name());
						items = new StorageWrapper(tag);
					}
					break;
				case NETWORKPOS:
					networkPos = new WorldPos(data.getCompoundTag(e.name()));
					break;
				case PASSWORD:
					password = data.getString(e.name());
					break;
				case ADMINPASSWORD:
					adminPassword = data.getString(e.name());
					break;
				case PLAYERVALIDNETWORK:
					NBTTagList taglist = data.getTagList(e.name(), 10);
					taglist.forEach(i -> {
						playerValidNetworks.put(new WorldPos((NBTTagCompound) i),
								((NBTTagCompound) i).getString("name"));
					});
					break;
				case MAP:
					NBTTagCompound tag = data.getCompoundTag(e.name());
					MapHandler.setData(networkPos, tag.getCompoundTag("units"), tag.getIntArray("lines"));
					break;
				case PACK:
					tag = data.getCompoundTag(e.name());
					MapHandler.setPackData(networkPos, tag);
					break;
				case NEEDNETWORKVALIDNETWORK:
					taglist = data.getTagList(e.name(), 10);
					taglist.forEach(i -> {
						needNetworkValidNetworks.put(new WorldPos((NBTTagCompound) i),
								((NBTTagCompound) i).getString("name"));
					});
					break;
				case NETWORKUUIDFROMCONTROLLER:
					networkFromController = data.getUniqueId(e.name());
					if (networkFromController.getMostSignificantBits() == 0
							&& networkFromController.getLeastSignificantBits() == 0) {
						networkFromController = null;
					}
					break;
				case NAME:
					name = data.getString(e.name());
					break;
				case LOADTICK:
					loadTick = data.getInteger(e.name());
					break;
				case ON:
					on = data.getBoolean(e.name());
					break;
				case WORKINGSTATE:
					int ord = data.getInteger(e.name());
					workingState = ord == 0 ? null : WorkingState.values()[ord - 1];
					break;
				case INITED:
					inited = data.getBoolean(e.name());
					break;
				case USEDSTORAGE:
					usedStorage = data.getInteger(e.name());
					break;
				case MAXSTORAGE:
					maxStorage = data.getInteger(e.name());
					break;
				}
			}
		}
		hasData = true;
	}

	public void clear() {
		networkPos = WorldPos.ORIGIN;
		password = "";
		adminPassword = "";
		playerValidNetworks.clear();
		needNetworkValidNetworks.clear();
		name = "";
		loadTick = 0;
		on = false;
		workingState = null;
		inited = false;
		usedStorage = 0;
		maxStorage = 0;
	}

	public void clearHuges() {
		items = null;
		MapHandler.clear();
	}

	public void disconnect() {
		EntityPlayer player = Minecraft.getMinecraft().player;
		if (player != null) {
			NetworkHandler.instance.sendToServer(new MessageConnectionRequest(player, new UUID(0, 0)));
		}
		clear();
		clearHuges();
		this.types.clear();
		hasData = false;
	}

	public void request(UUID network, Type... types) {
		NetworkHandler.instance
				.sendToServer(new MessageConnectionRequest(Minecraft.getMinecraft().player, network, types));
		clear();
		clearHuges();
		this.types = Sets.newHashSet(types);
		hasData = false;
	}

	public void requestController(WorldPos center, Type... types) {
		NetworkHandler.instance
				.sendToServer(new MessageConnectionControllerRequest(Minecraft.getMinecraft().player, center, types));
		clear();
		clearHuges();
		this.types = Sets.newHashSet(types);
		hasData = false;
	}

	public void requestNeedNetwork(WorldPos needNetwork, Type... types) {
		NetworkHandler.instance.sendToServer(
				new MessageConnectionNeedNetworkRequest(Minecraft.getMinecraft().player, needNetwork, types));
		clear();
		clearHuges();
		this.types = Sets.newHashSet(types);
		hasData = false;
	}

	public boolean hasData() {
		return hasData;
	}

	public boolean isConnected() {
		return !networkPos.isOrigin();
	}

	public StorageWrapper getItems() {
		return items;
	}

	public UUID getNetwork() {
		return network;
	}

	public WorldPos getNetworkPos() {
		return networkPos;
	}

	public Map<WorldPos, String> getPlayerValidNetworks() {
		return playerValidNetworks;
	}

	public static ConnectHelperClient getInstance() {
		return instance;
	}

	public String getPassword() {
		return password;
	}

	public String getAdminPassword() {
		return adminPassword;
	}

	public Map<WorldPos, String> getNeedNetworkValidNetworks() {
		return needNetworkValidNetworks;
	}

	public UUID getNetworkFromController() {
		return networkFromController;
	}

	public String getName() {
		return name;
	}

	public int getLoadTick() {
		return loadTick;
	}

	public boolean isOn() {
		return on;
	}

	public WorkingState getWorkingState() {
		return workingState;
	}

	public boolean isInited() {
		return inited;
	}

	public int getUsedStorage() {
		return usedStorage;
	}

	public int getMaxStorage() {
		return maxStorage;
	}
}
