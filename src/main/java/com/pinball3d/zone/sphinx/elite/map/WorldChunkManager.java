package com.pinball3d.zone.sphinx.elite.map;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class WorldChunkManager {
	protected final MapRenderManager renderManager;
	protected final World world;
	protected int countChunksY;
	protected int countChunksX;
	protected int countChunksZ;
	public ChunkWrapper[] renderChunks;

	public WorldChunkManager(World worldIn, int renderDistanceChunks, MapRenderManager renderManager) {
		this.renderManager = renderManager;
		this.world = worldIn;
		setCountChunksXYZ(renderDistanceChunks);
		createRenderChunks();
	}

	public void createRenderChunks() {
		int i = countChunksX * countChunksY * countChunksZ;
		renderChunks = new ChunkWrapper[i];
		for (int k = 0; k < countChunksX; ++k) {
			for (int l = 0; l < countChunksY; ++l) {
				for (int i1 = 0; i1 < countChunksZ; ++i1) {
					int j1 = (i1 * countChunksY + l) * countChunksX + k;
					renderChunks[j1] = new ChunkWrapper(renderManager, world);
					renderChunks[j1].setPosition(k * 16, l * 16, i1 * 16);
				}
			}
		}
	}

	public void deleteGlResources() {
		for (ChunkWrapper renderchunk : renderChunks) {
			renderchunk.deleteGlResources();
		}
	}

	public void setCountChunksXYZ(int renderDistanceChunks) {
		int i = renderDistanceChunks * 2 + 1;
		this.countChunksX = i;
		this.countChunksY = 16;
		this.countChunksZ = i;
	}

	public void updateChunkPositions(double viewEntityX, double viewEntityZ) {
		int i = MathHelper.floor(viewEntityX) - 8;
		int j = MathHelper.floor(viewEntityZ) - 8;
		int k = this.countChunksX * 16;
		for (int l = 0; l < this.countChunksX; ++l) {
			int i1 = this.getBaseCoordinate(i, k, l);
			for (int j1 = 0; j1 < this.countChunksZ; ++j1) {
				int k1 = this.getBaseCoordinate(j, k, j1);
				for (int l1 = 0; l1 < this.countChunksY; ++l1) {
					int i2 = l1 * 16;
					ChunkWrapper renderchunk = this.renderChunks[(j1 * this.countChunksY + l1) * this.countChunksX + l];
					renderchunk.setPosition(i1, i2, k1);
				}
			}
		}
	}

	private int getBaseCoordinate(int p_178157_1_, int p_178157_2_, int p_178157_3_) {
		int i = p_178157_3_ * 16;
		int j = i - p_178157_1_ + p_178157_2_ / 2;
		if (j < 0) {
			j -= p_178157_2_ - 1;
		}
		return i - j / p_178157_2_ * p_178157_2_;
	}

	public void markBlocksForUpdate(int minX, int minY, int minZ, int maxX, int maxY, int maxZ,
			boolean updateImmediately) {
		int i = MathHelper.intFloorDiv(minX, 16);
		int j = MathHelper.intFloorDiv(minY, 16);
		int k = MathHelper.intFloorDiv(minZ, 16);
		int l = MathHelper.intFloorDiv(maxX, 16);
		int i1 = MathHelper.intFloorDiv(maxY, 16);
		int j1 = MathHelper.intFloorDiv(maxZ, 16);

		for (int k1 = i; k1 <= l; ++k1) {
			int l1 = k1 % countChunksX;
			if (l1 < 0) {
				l1 += countChunksX;
			}
			for (int i2 = j; i2 <= i1; ++i2) {
				int j2 = i2 % countChunksY;
				if (j2 < 0) {
					j2 += countChunksY;
				}
				for (int k2 = k; k2 <= j1; ++k2) {
					int l2 = k2 % countChunksZ;
					if (l2 < 0) {
						l2 += countChunksZ;
					}
					int i3 = (l2 * countChunksY + j2) * countChunksX + l1;
					ChunkWrapper renderchunk = renderChunks[i3];
					renderchunk.setNeedsUpdate(updateImmediately);
				}
			}
		}
	}

	public ChunkWrapper getRenderChunk(BlockPos pos) {
		int i = MathHelper.intFloorDiv(pos.getX(), 16);
		int j = MathHelper.intFloorDiv(pos.getY(), 16);
		int k = MathHelper.intFloorDiv(pos.getZ(), 16);

		if (j >= 0 && j < countChunksY) {
			i = i % countChunksX;
			if (i < 0) {
				i += countChunksX;
			}
			k = k % countChunksZ;
			if (k < 0) {
				k += countChunksZ;
			}
			int l = (k * countChunksY + j) * countChunksX + i;
			return renderChunks[l];
		} else {
			return null;
		}
	}
}