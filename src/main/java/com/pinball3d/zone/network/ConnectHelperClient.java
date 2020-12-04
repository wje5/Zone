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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ConnectHelperClient {
	public static ConnectHelperClient instance = new ConnectHelperClient();
	public UUID network;
	public StorageWrapper items;
	public boolean isConnected, isCorrectPassword, isCorrectAdminPassword;
	public WorldPos networkPos;
	public Map<WorldPos, String> playerValidNetworks = new HashMap<WorldPos, String>();

	public ConnectHelperClient() {

	}

	public void setData(UUID network, NBTTagCompound data) {
		clear();
		this.network = network;
		for (Type e : Type.values()) {
			if (data.hasKey(e.name())) {
				switch (e) {
				case ITEMS:
					NBTTagCompound tag = data.getCompoundTag(e.name());
					items = new StorageWrapper(tag);
					break;
				case ISCONNECTED:
					isConnected = data.getBoolean(e.name());
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
					playerValidNetworks.clear();
					tag = data.getCompoundTag(e.name());
					break;
				case MAP:
					tag = data.getCompoundTag(e.name());
					MapHandler.setData(networkPos, tag.getCompoundTag("units"), tag.getIntArray("lines"));
					break;
				}
			}
		}
	}

	public void clear() {
		items = null;
		isConnected = false;
		networkPos = WorldPos.ORIGIN;
		isCorrectPassword = false;
		isCorrectAdminPassword = false;
		playerValidNetworks.clear();
	}

	public void disconnect(UUID network) {
		NetworkHandler.instance
				.sendToServer(new MessageConnectionRequest(Minecraft.getMinecraft().player, network, null));
		clear();
	}
}
