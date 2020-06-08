package com.pinball3d.zone.sphinx;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;

public class GlobalNetworkData extends WorldSavedData {
	private Map<UUID, WorldPos> map;

	public GlobalNetworkData(String name) {
		super(name);
		map = new HashMap<UUID, WorldPos>();
	}

	public WorldPos getNetwork(UUID uuid) {
		return map.get(uuid);
	}

	public void setUUID(WorldPos pos, UUID uuid) {
		map.put(uuid, pos);
		markDirty();
	}

	public UUID getUUID(WorldPos pos) {
		if (map.containsValue(pos)) {
			Iterator<Entry<UUID, WorldPos>> it = map.entrySet().iterator();
			while (it.hasNext()) {
				Entry<UUID, WorldPos> i = it.next();
				if (i.getValue().equals(pos)) {
					return i.getKey();
				}
			}
		}
		UUID uuid = UUID.randomUUID();
		setUUID(pos, uuid);
		return uuid;
	}

	public static GlobalNetworkData getData(World world) {
		WorldSavedData data = world.getMapStorage().getOrLoadData(GlobalNetworkData.class, "GlobalNetworkData");
		if (data == null) {
			data = new GlobalNetworkData("GlobalNetworkData");
			world.getMapStorage().setData("GlobalNetworkData", data);
		}
		return (GlobalNetworkData) data;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		NBTTagList list = nbt.getTagList("data", 9);
		map = new HashMap<UUID, WorldPos>();
		list.forEach(e -> {
			UUID uuid = ((NBTTagCompound) e).getUniqueId("uuid");
			WorldPos pos = WorldPos.load((NBTTagCompound) e);
			setUUID(pos, uuid);
		});
		System.out.println(nbt);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		NBTTagList list = new NBTTagList();
		map.forEach((k, v) -> {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setUniqueId("uuid", k);
			v.save(tag);
			list.appendTag(tag);
		});
		nbt.setTag("data", list);
		System.out.println(nbt);
		return nbt;
	}
}
