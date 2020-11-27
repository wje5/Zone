package com.pinball3d.zone.network;

import java.util.UUID;

import com.pinball3d.zone.network.ConnectionHelper.Type;
import com.pinball3d.zone.sphinx.StorageWrapper;
import com.pinball3d.zone.sphinx.WorldPos;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ConnectHelperClient {
	public static ConnectHelperClient instance = new ConnectHelperClient();
	public UUID network;
	public StorageWrapper items;
	public boolean isConnected;
	public WorldPos networkPos;

	public ConnectHelperClient() {

	}

	public void setData(UUID network, NBTTagCompound data) {
		clear();
		if (!network.equals(this.network)) {
			this.network = network;
		}
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
				}
			}
		}
	}

	public void clear() {
		items = null;
		isConnected = false;
		networkPos = null;
	}
}
