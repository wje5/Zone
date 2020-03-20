package com.pinball3d.zone.sphinx;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;

public class ChunkRenderCache {
	private static int highest, lowest;
	private int[] heightMap = new int[256];

	public static void init() {
		highest = 0;
		lowest = 0;
	}

	public ChunkRenderCache(Chunk chunk) {
		for (int x = chunk.x * 16; x < chunk.x * 16 + 16; x++) {
			for (int z = chunk.z * 16; z < chunk.z * 16 + 16; z++) {
				int y = chunk.getHeightValue(x - chunk.x * 16, z - chunk.z * 16) - 1;
				BlockPos pos = new BlockPos(x, y, z);
				IBlockState state = chunk.getBlockState(pos);
				int height = chunk.getHeight(pos);
				heightMap[(x - chunk.x * 16) * 16 + (z - chunk.z * 16)] = height;
				if (height > highest) {
					highest = height;
				}
				if (lowest == 0 || height < lowest) {
					lowest = height;
				}
			}
		}
	}

	public int getColor(int x, int z) {
		int color = heightMap[x * 16 + z] - lowest;
		int range = highest - lowest + 1;
		int f = (int) (255F / range * color);
		int r = (f / 16) ^ 2;
		return r * 0x010000 + f * 0x000100 + f;
	}
}
