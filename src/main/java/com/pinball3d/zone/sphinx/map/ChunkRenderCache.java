package com.pinball3d.zone.sphinx.map;

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
		for (int x = chunkX * 16; x < chunkX * 16 + 16; x++) {
			for (int z = chunkZ * 16; z < chunkZ * 16 + 16; z++) {
				int height = world.getHeight(x, z);
				if (height == 0) {
					return null;
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
		return cache;
	}

	public int getColor(int x, int z) {
		int height = heightMap[x * 16 + z];
		if (height > highest) {
			highest = height;
		}
		if (lowest == 0 || height < lowest) {
			lowest = height;
		}
		int color = height - lowest == 0 ? 1 : height - lowest;
		int range = highest - lowest + 1;
		int f = (int) (255F / range * color);
		int r = (f / 16) ^ 2;
		return r * 0x010000 + f * 0x000100 + f;
	}
}
