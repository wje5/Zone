package com.pinball3d.zone.sphinx;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class ClientMapDataHandler {
	private static Map<Long, ChunkRenderCache> mapCache = new HashMap<Long, ChunkRenderCache>();

	public static void setData(int x, int z, ChunkRenderCache cache) {
		mapCache.put(x * 30000000L + z, cache);
	}

	public static ChunkRenderCache getData(int x, int z) {
		return mapCache.get(x * 30000000L + z);
	}

	@SubscribeEvent
	public static void onChunkLoad(ChunkEvent.Load event) {
		if (event.getWorld().isRemote) {
			ChunkPos pos = event.getChunk().getPos();
			if (!mapCache.containsKey(pos.x * 30000000L + pos.z)) {
				ChunkRenderCache.create(pos.x, pos.z);
			}
		}
	}
}
