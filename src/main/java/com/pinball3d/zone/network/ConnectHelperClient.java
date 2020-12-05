package com.pinball3d.zone.network;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.pinball3d.zone.network.ConnectionHelper.Type;
import com.pinball3d.zone.sphinx.MapHandler;
import com.pinball3d.zone.sphinx.StorageWrapper;
import com.pinball3d.zone.sphinx.WorldPos;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ConnectHelperClient {
	private static ConnectHelperClient instance = new ConnectHelperClient();
	private UUID network;
	private StorageWrapper items;
	private boolean isCorrectPassword, isCorrectAdminPassword;
	private WorldPos networkPos = WorldPos.ORIGIN;
	private Map<WorldPos, String> playerValidNetworks = new HashMap<WorldPos, String>();
	private Map<WorldPos, String> needNetworkValidNetworks = new HashMap<WorldPos, String>();

	public ConnectHelperClient() {

	}

	public void setData(UUID network, NBTTagCompound data) {
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
					NBTTagCompound tag = data.getCompoundTag(e.name());
					items = new StorageWrapper(tag);
					break;
				case NETWORKPOS:
					networkPos = new WorldPos(data.getCompoundTag(e.name()));
					break;
				case ISCORRECTPASSWORD:
					isCorrectPassword = data.getBoolean(e.name());
					break;
				case ISCORRECTADMINPASSWORD:
					isCorrectAdminPassword = data.getBoolean(e.name());
					break;
				case PLAYERVALIDNETWORK:
					NBTTagList taglist = data.getTagList(e.name(), 10);
					taglist.forEach(i -> {
						playerValidNetworks.put(new WorldPos((NBTTagCompound) i),
								((NBTTagCompound) i).getString("name"));
					});
					break;
				case MAP:
					tag = data.getCompoundTag(e.name());
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
				}
			}
		}
	}

	public void clear() {
		items = null;
		networkPos = WorldPos.ORIGIN;
		isCorrectPassword = false;
		isCorrectAdminPassword = false;
		playerValidNetworks.clear();
		needNetworkValidNetworks.clear();
	}

	public void disconnect(UUID network) {
		NetworkHandler.instance.sendToServer(new MessageConnectionRequest(Minecraft.getMinecraft().player, network));
		clear();
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

	public boolean isCorrectAdminPassword() {
		return isCorrectAdminPassword;
	}

	public boolean isCorrectPassword() {
		return isCorrectPassword;
	}

	public Map<WorldPos, String> getNeedNetworkValidNetworks() {
		return needNetworkValidNetworks;
	}
}
