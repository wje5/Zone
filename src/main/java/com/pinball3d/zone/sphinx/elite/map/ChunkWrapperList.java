package com.pinball3d.zone.sphinx.elite.map;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;

public class ChunkWrapperList {
	private double cameraX;
	private double cameraY;
	private double cameraZ;
	protected List<ChunkWrapper> chunks = new ArrayList<ChunkWrapper>(17424);
	protected boolean inited;

	public void init(double cameraX, double cameraY, double cameraZ) {
		inited = true;
		chunks.clear();
		this.cameraX = cameraX;
		this.cameraY = cameraY;
		this.cameraZ = cameraZ;
	}

	public void preRenderChunk(ChunkWrapper chunk) {
		BlockPos blockpos = chunk.getPosition();
		GlStateManager.translate((float) (blockpos.getX() - cameraX), (float) (blockpos.getY() - cameraY),
				(float) (blockpos.getZ() - cameraZ));
	}

	public void addChunk(ChunkWrapper chunk, BlockRenderLayer layer) {
		this.chunks.add(chunk);
	}

	public void renderChunkLayer(BlockRenderLayer layer) {
		if (this.inited) {
			for (ChunkWrapper renderchunk : this.chunks) {
				GlStateManager.pushMatrix();
				preRenderChunk(renderchunk);
				GlStateManager.callList(renderchunk.getDisplayList(layer, renderchunk.getCompiledChunk()));
				GlStateManager.popMatrix();
			}
			GlStateManager.resetColor();
			chunks.clear();
		}
	}
}
