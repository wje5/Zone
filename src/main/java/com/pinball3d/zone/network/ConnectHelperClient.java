package com.pinball3d.zone.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

import com.google.common.collect.Sets;
import com.pinball3d.zone.ConfigLoader;
import com.pinball3d.zone.network.ConnectionHelper.Type;
import com.pinball3d.zone.sphinx.ClassifyGroup;
import com.pinball3d.zone.sphinx.SerialNumber;
import com.pinball3d.zone.sphinx.log.Log;
import com.pinball3d.zone.sphinx.map.MapHandler;
import com.pinball3d.zone.tileentity.TEProcessingCenter.UserData;
import com.pinball3d.zone.tileentity.TEProcessingCenter.WorkingState;
import com.pinball3d.zone.util.LimitedQueue;
import com.pinball3d.zone.util.StorageWrapper;
import com.pinball3d.zone.util.WorldPos;

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
	private List<ClassifyGroup> classify = new ArrayList<ClassifyGroup>();
	private List<UserData> users = new ArrayList<UserData>();
	private Queue<Log> logs = new LimitedQueue<Log>(ConfigLoader.sphinxLogCache);
	private SerialNumber needNetworkSerial;

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
				case USEDSTORAGE:
					usedStorage = data.getInteger(e.name());
					break;
				case MAXSTORAGE:
					maxStorage = data.getInteger(e.name());
					break;
				case CLASSIFY:
					if (data.hasKey(e.name())) {
						classify.clear();
						NBTTagList list = data.getTagList(e.name(), 10);
						list.forEach(i -> {
							ClassifyGroup c = new ClassifyGroup((NBTTagCompound) i);
							classify.add(c);
						});
					}
					break;
				case USERS:
					NBTTagList usersList = data.getTagList(e.name(), 10);
					usersList.forEach(j -> {
						users.add(new UserData((NBTTagCompound) j));
					});
					break;
				case LOGS:
					if (data.hasKey(e.name())) {
						logs.clear();
						NBTTagList logsList = data.getTagList(e.name(), 10);
						logsList.forEach(k -> {
							logs.add(Log.readLogFromNBT((NBTTagCompound) k));
						});
					}
					break;
				case NEEDNETWORKSERIAL:
					needNetworkSerial = new SerialNumber(data.getCompoundTag(e.name()));
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
		users.clear();
		needNetworkSerial = null;
	}

	public void clearHuges() {
		items = new StorageWrapper();
		MapHandler.clear();
		classify.clear();
		logs.clear();
	}

	public void disconnect() {
		EntityPlayer player = Minecraft.getMinecraft().player;
		if (player != null) {
			NetworkHandler.instance.sendToServer(new MessageConnectionRequest(player));
		}
		clear();
		clearHuges();
		this.types.clear();
		hasData = false;
	}

	public void requestTerminal(Type... types) {
		EntityPlayer player = Minecraft.getMinecraft().player;
		if (player != null) {
			NetworkHandler.instance.sendToServer(new MessageConnectionRequest(player, types));
		}
		clear();
		clearHuges();
		this.types = Sets.newHashSet(types);
		hasData = false;
	}

	public void requestController(WorldPos controller, Type... types) {
		EntityPlayer player = Minecraft.getMinecraft().player;
		if (player != null) {
			NetworkHandler.instance.sendToServer(new MessageConnectionControllerRequest(player, controller, types));
		}
		clear();
		clearHuges();
		this.types = Sets.newHashSet(types);
		hasData = false;
	}

	public void requestNeedNetwork(WorldPos needNetwork, Type... types) {
		EntityPlayer player = Minecraft.getMinecraft().player;
		if (player != null) {
			NetworkHandler.instance.sendToServer(new MessageConnectionNeedNetworkRequest(player, needNetwork, types));
		}
		clear();
		clearHuges();
		this.types = Sets.newHashSet(types);
		hasData = false;
	}

	public boolean hasData() {
		return hasData;
	}

	public boolean isConnected() {
		return hasData && !networkPos.isOrigin();
	}

	public boolean isAdmin() {
		if (!hasData || users.isEmpty()) {
			return false;
		}
		UUID uuid = Minecraft.getMinecraft().player.getUniqueID();
		Iterator<UserData> it = users.iterator();
		while (it.hasNext()) {
			UserData data = it.next();
			if (data.admin && data.uuid.equals(uuid)) {
				return true;
			}
		}
		return false;
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

	public List<ClassifyGroup> getClassify() {
		return classify;
	}

	public List<UserData> getUsers() {
		return users;
	}

	public Set<Type> getTypes() {
		return types;
	}

	public Queue<Log> getLogs() {
		return logs;
	}

	public SerialNumber getNeedNetworkSerial() {
		return needNetworkSerial;
	}
}
