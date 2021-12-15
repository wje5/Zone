package com.pinball3d.zone.sphinx.elite.map;

import java.util.List;
import java.util.concurrent.CancellationException;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RegionRenderCacheBuilder;
import net.minecraft.client.renderer.chunk.ChunkCompileTaskGenerator;
import net.minecraft.client.renderer.chunk.CompiledChunk;
import net.minecraft.crash.CrashReport;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MapRenderThread extends Thread {
	private static final Logger LOGGER = LogManager.getLogger();
	private MapRenderManager renderManager;
	private boolean shouldRun = true;
	private MapRenderThreadManager manager;
	private RegionRenderCacheBuilder regionRenderCacheBuilder;

	public MapRenderThread(MapRenderManager renderManager, MapRenderThreadManager manager) {
		this(renderManager, manager, null);
	}

	public MapRenderThread(MapRenderManager renderManager, MapRenderThreadManager manager,
			RegionRenderCacheBuilder regionRenderCacheBuilder) {
		this.renderManager = renderManager;
		this.manager = manager;
		this.regionRenderCacheBuilder = regionRenderCacheBuilder;
	}

	@Override
	public void run() {
		while (shouldRun) {
			try {
				processTask(manager.getNextChunkUpdate());
			} catch (InterruptedException e) {
				return;
			} catch (Exception e2) {
				e2.printStackTrace();
				return;
			}
		}
	}

	public void processTask(ChunkRenderTaskWrapper wrapper) throws InterruptedException {
		wrapper.getLock().lock();
		try {
			if (wrapper.getStatus() != ChunkCompileTaskGenerator.Status.PENDING) {
				if (!wrapper.isFinished()) {
					LOGGER.warn("Chunk render task was {} when I expected it to be pending; ignoring task",
							wrapper.getStatus());
				}
				return;
			}
			BlockPos blockpos = new BlockPos(renderManager.cameraX, renderManager.cameraY, renderManager.cameraZ);
			BlockPos blockpos1 = wrapper.getChunk().getPosition();
			if (blockpos1.add(8, 8, 8).distanceSq(blockpos) > 576.0D) {
				World world = wrapper.getChunk().getWorld();
				BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(blockpos1);
				if (!isChunkExisting(pos.setPos(blockpos1).move(EnumFacing.WEST, 16), world)
						|| !isChunkExisting(pos.setPos(blockpos1).move(EnumFacing.NORTH, 16), world)
						|| !isChunkExisting(pos.setPos(blockpos1).move(EnumFacing.EAST, 16), world)
						|| !isChunkExisting(pos.setPos(blockpos1).move(EnumFacing.SOUTH, 16), world)) {
					return;
				}
			}
			wrapper.setStatus(ChunkCompileTaskGenerator.Status.COMPILING);
		} finally {
			wrapper.getLock().unlock();
		}
		wrapper.setCacheBuilder(getRegionRenderCacheBuilder());
		float f = renderManager.cameraX;
		float f1 = renderManager.cameraY;
		float f2 = renderManager.cameraZ;
		ChunkCompileTaskGenerator.Type type = wrapper.getType();

		if (type == ChunkCompileTaskGenerator.Type.REBUILD_CHUNK) {
			wrapper.getChunk().rebuildChunk(f, f1, f2, wrapper);
		} else if (type == ChunkCompileTaskGenerator.Type.RESORT_TRANSPARENCY) {
			wrapper.getChunk().resortTransparency(f, f1, f2, wrapper);
		}

		wrapper.getLock().lock();

		try {
			if (wrapper.getStatus() != ChunkCompileTaskGenerator.Status.COMPILING) {
				if (!wrapper.isFinished()) {
					LOGGER.warn("Chunk render task was {} when I expected it to be compiling; aborting task",
							wrapper.getStatus());
				}
//				synchronized (MapRenderThread.class) {
//					m--;
//					System.out.println("free:" + wrapper.getStatus() + "|" + m);
//				}
				freeRenderBuilder(wrapper);
				return;
			}
			wrapper.setStatus(ChunkCompileTaskGenerator.Status.UPLOADING);
		} finally {
			wrapper.getLock().unlock();
		}

		CompiledChunk compiledchunk = wrapper.getCompiledChunk();
		List<ListenableFuture<Object>> list = Lists.newArrayList();

		if (type == ChunkCompileTaskGenerator.Type.REBUILD_CHUNK) {
			for (BlockRenderLayer layer : BlockRenderLayer.values()) {
				if (compiledchunk.isLayerStarted(layer)) {
					list.add(manager.uploadChunk(layer, wrapper.getCacheBuilder().getWorldRendererByLayer(layer),
							wrapper.getChunk(), compiledchunk, wrapper.getDistanceSq()));
				}
			}
		} else if (type == ChunkCompileTaskGenerator.Type.RESORT_TRANSPARENCY) {
			list.add(manager.uploadChunk(BlockRenderLayer.TRANSLUCENT,
					wrapper.getCacheBuilder().getWorldRendererByLayer(BlockRenderLayer.TRANSLUCENT), wrapper.getChunk(),
					compiledchunk, wrapper.getDistanceSq()));
		}

		ListenableFuture<List<Object>> listenablefuture = Futures.allAsList(list);
		wrapper.appendFinishEvent(new Runnable() {
			@Override
			public void run() {
				listenablefuture.cancel(false);
			}
		});
		Futures.addCallback(listenablefuture, new FutureCallback<List<Object>>() {
			@Override
			public void onSuccess(@Nullable List<Object> p_onSuccess_1_) {
				MapRenderThread.this.freeRenderBuilder(wrapper);
//				synchronized (MapRenderThread.class) {
//					System.out.println("freesuccess" + m);
//					m--;
//				}
				wrapper.getLock().lock();
				label49: {
					try {
						if (wrapper.getStatus() == ChunkCompileTaskGenerator.Status.UPLOADING) {
							wrapper.setStatus(ChunkCompileTaskGenerator.Status.DONE);
							break label49;
						}

						if (!wrapper.isFinished()) {
							MapRenderThread.LOGGER.warn(
									"Chunk render task was {} when I expected it to be uploading; aborting task",
									wrapper.getStatus());
						}
					} finally {
						wrapper.getLock().unlock();
					}
					return;
				}
				wrapper.getChunk().setCompiledChunk(compiledchunk);
			}

			@Override
			public void onFailure(Throwable e) {
				MapRenderThread.this.freeRenderBuilder(wrapper);
//				synchronized (MapRenderThread.class) {
//					System.out.println("freefail" + m);
//					m--;
//				}
				if (!(e instanceof CancellationException) && !(e instanceof InterruptedException)) {
					Minecraft.getMinecraft().crashed(CrashReport.makeCrashReport(e, "Rendering chunk"));
				}
			}
		});
	}

	private boolean isChunkExisting(BlockPos pos, World world) {
		return !world.getChunkFromChunkCoords(pos.getX() >> 4, pos.getZ() >> 4).isEmpty();
	}

	private RegionRenderCacheBuilder getRegionRenderCacheBuilder() throws InterruptedException {
		return regionRenderCacheBuilder != null ? regionRenderCacheBuilder : manager.allocateRenderBuilder();
	}

	private void freeRenderBuilder(ChunkRenderTaskWrapper wrapper) {
		if (regionRenderCacheBuilder == null) {
			manager.freeRenderBuilder(wrapper.getCacheBuilder());
		}
	}

	public void notifyStop() {
		shouldRun = false;
	}
}
