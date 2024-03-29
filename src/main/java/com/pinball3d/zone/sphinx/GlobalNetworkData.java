package com.pinball3d.zone.sphinx;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import com.pinball3d.zone.block.BlockProcessingCenter;
import com.pinball3d.zone.util.WorldPos;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class GlobalNetworkData extends WorldSavedData {
	private Map<UUID, WorldPos> map;

	public GlobalNetworkData(String name) {
		super(name);
		map = new HashMap<UUID, WorldPos>();
	}

	public WorldPos getNetworkPos(UUID uuid) {
		WorldPos p = map.get(uuid);
		return p == null ? WorldPos.ORIGIN : p;
	}

	public void setUUID(WorldPos pos, UUID uuid) {
		map.put(uuid, pos);
		checkData();
		markDirty();
	}

	public UUID getNetworkUUID(WorldPos pos) {
		checkData();
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

	public void checkData() {
		Iterator<Entry<UUID, WorldPos>> it = map.entrySet().iterator();
		while (it.hasNext()) {
			WorldPos pos = it.next().getValue();
			if (!(pos.getBlockState().getBlock() instanceof BlockProcessingCenter)) {
				it.remove();
			}
		}
	}

	public Map<UUID, WorldPos> getNetworks() {
		return map;
	}

	public static GlobalNetworkData getData(World world) {
		WorldSavedData data = world.getMapStorage().getOrLoadData(GlobalNetworkData.class, "GlobalNetworkData");
		if (data == null) {
			data = new GlobalNetworkData("GlobalNetworkData");
			world.getMapStorage().setData("GlobalNetworkData", data);
		}
		((GlobalNetworkData) data).checkData();
		return (GlobalNetworkData) data;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		NBTTagList list = nbt.getTagList("data", 10);
		map = new HashMap<UUID, WorldPos>();
		list.forEach(e -> {
			UUID uuid = ((NBTTagCompound) e).getUniqueId("uuid");
			WorldPos pos = new WorldPos((NBTTagCompound) e);
			setUUID(pos, uuid);
		});
		checkData();
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		checkData();
		NBTTagList list = new NBTTagList();
		map.forEach((k, v) -> {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setUniqueId("uuid", k);
			v.writeToNBT(tag);
			list.appendTag(tag);
		});
		nbt.setTag("data", list);
		return nbt;
	}

	public static WorldPos getPos(UUID uuid) {
		return GlobalNetworkData.getData(FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(0))
				.getNetworkPos(uuid);
	}

	public static UUID getUUID(WorldPos pos) {
		return GlobalNetworkData.getData(FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(0))
				.getNetworkUUID(pos);
	}
}
