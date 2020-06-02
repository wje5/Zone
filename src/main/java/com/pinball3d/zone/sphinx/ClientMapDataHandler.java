package com.pinball3d.zone.sphinx;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class ClientMapDataHandler {
	private static Map<Integer, Map<Long, ChunkRenderCache>> mapCache = new HashMap<Integer, Map<Long, ChunkRenderCache>>();

	public static void setData(int worldId, int x, int z, ChunkRenderCache cache) {
		getMap(worldId).put(x * 30000000L + z, cache);
	}

	public static ChunkRenderCache getData(int worldId, int x, int z) {
		ChunkRenderCache data = getMap(worldId).get(x * 30000000L + z);
		if (data == null) {
			data = ChunkRenderCache.create(x, z);
			if (data == null) {
				return null;
			} else {
				setData(worldId, x, z, data);
			}
		}
		return data;
	}

	private static Map<Long, ChunkRenderCache> getMap(int worldId) {
		Map<Long, ChunkRenderCache> map = mapCache.get(worldId);
		if (map == null) {
			map = new HashMap<Long, ChunkRenderCache>();
			mapCache.put(worldId, map);
		}
		return map;
	}

	@SubscribeEvent
	public static void onChunkLoad(ChunkEvent.Load event) {
		if (event.getWorld().isRemote) {
			ChunkPos pos = event.getChunk().getPos();
			int dim = event.getWorld().provider.getDimension();
			if (!getMap(dim).containsKey(pos.x * 30000000L + pos.z)) {
				ChunkRenderCache data = ChunkRenderCache.create(pos.x, pos.z);
				if (data != null) {
					setData(dim, pos.x, pos.z, data);
					System.out.println(data);
				}
			}
		}
	}
}
