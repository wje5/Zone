package com.pinball3d.zone.sphinx;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

public class ChunkRenderCache {
	private static int highest, lowest;
	private int[] heightMap = new int[256];

	public static void init() {
		highest = 0;
		lowest = 0;
	}

	private ChunkRenderCache() {

	}

	public static ChunkRenderCache create(int chunkX, int chunkZ) {
		ChunkRenderCache cache = new ChunkRenderCache();
		World world = Minecraft.getMinecraft().player.world;
		boolean flag = true;
		for (int x = chunkX * 16; x < chunkX * 16 + 16; x++) {
			for (int z = chunkZ * 16; z < chunkZ * 16 + 16; z++) {
				int height = world.getHeight(x, z);
				if (height == 0 && flag) {
					flag = false;
					ChunkRenderCache data = ClientMapDataHandler.getData(chunkX, chunkZ);
					if (data != null) {
						return data;
					}
				}
				cache.heightMap[(x - chunkX * 16) * 16 + (z - chunkZ * 16)] = height;
				if (height > highest) {
					highest = height;
				}
				if (lowest == 0 || height < lowest) {
					lowest = height;
				}
			}
		}
		if (flag) {
			ClientMapDataHandler.setData(chunkX, chunkZ, cache);
		}
		return cache;
	}

	public int getColor(int x, int z) {
		int color = heightMap[x * 16 + z] - lowest;
		int range = highest - lowest + 1;
		int f = (int) (255F / range * color);
		int r = (f / 16) ^ 2;
		return r * 0x010000 + f * 0x000100 + f;
	}
}
