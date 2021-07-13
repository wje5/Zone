package com.pinball3d.zone.sphinx.elite.map;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RegionRenderCacheBuilder;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.chunk.CompiledChunk;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.MathHelper;

public class MapRenderThreadManager {
	private MapRenderManager renderManager;
	private List<MapRenderThread> threads = new ArrayList<MapRenderThread>();
	private BlockingQueue<RegionRenderCacheBuilder> freeBuilders;
	private WorldVertexBufferUploader worldVertexUploader = new WorldVertexBufferUploader();
	private PriorityBlockingQueue<ChunkRenderTaskWrapper> chunkUpdates = new PriorityBlockingQueue<ChunkRenderTaskWrapper>();
	private Queue<PendingUpload> chunkUploads = new PriorityQueue<PendingUpload>();
	private int builderCount;
	private MapRenderThread renderer;

	public MapRenderThreadManager(MapRenderManager renderManager) {
		this.renderManager = renderManager;
		int i = Math.max(1, (int) (Runtime.getRuntime().maxMemory() * 0.3D) / 10485760);
		int j = Math.max(1, MathHelper.clamp(Runtime.getRuntime().availableProcessors(), 1, i / 5));
		builderCount = MathHelper.clamp(j * 10, 1, i);
		if (j > 1) {
			for (int k = 0; k < j; ++k) {
				MapRenderThread thread = new MapRenderThread(renderManager, this);
				thread.start();
				threads.add(thread);
			}
		}
		freeBuilders = new ArrayBlockingQueue<RegionRenderCacheBuilder>(builderCount);
		for (int l = 0; l < builderCount; ++l) {
			freeBuilders.add(new RegionRenderCacheBuilder());
		}
		renderer = new MapRenderThread(renderManager, this);
	}

	public boolean runChunkUploads(long finishTimeNano) {
		boolean flag = false;
		while (true) {
			boolean flag1 = false;
			if (threads.isEmpty()) {
				ChunkRenderTaskWrapper wrapper = chunkUpdates.poll();
				if (wrapper != null) {
					try {
						renderer.processTask(wrapper);
						flag1 = true;
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			synchronized (chunkUploads) {
				if (!chunkUploads.isEmpty()) {
					(chunkUploads.poll()).uploadTask.run();
					flag1 = true;
					flag = true;
				}
			}
			if (finishTimeNano == 0L || !flag1 || finishTimeNano < System.nanoTime()) {
				break;
			}
		}
		return flag;
	}

	public boolean updateChunkLater(ChunkWrapper chunk) {
		chunk.getLockCompileTask().lock();
		boolean flag1;
		try {
			final ChunkRenderTaskWrapper wrapper = chunk.makeTaskWrapper();
			wrapper.appendFinishEvent(new Runnable() {
				@Override
				public void run() {
					MapRenderThreadManager.this.chunkUpdates.remove(wrapper);
				}
			});
			boolean flag = chunkUpdates.offer(wrapper);
			if (!flag) {
				wrapper.finish();
			}
			flag1 = flag;
		} finally {
			chunk.getLockCompileTask().unlock();
		}
		return flag1;
	}

	public boolean updateChunkNow(ChunkWrapper chunk) {
		chunk.getLockCompileTask().lock();
		boolean flag;
		try {
			ChunkRenderTaskWrapper wrapper = chunk.makeTaskWrapper();
			try {
				renderer.processTask(wrapper);
			} catch (InterruptedException e) {

			}
			flag = true;
		} finally {
			chunk.getLockCompileTask().unlock();
		}
		return flag;
	}

	public void stopChunkUpdates() {
		clearChunkUpdates();
		List<RegionRenderCacheBuilder> list = Lists.<RegionRenderCacheBuilder>newArrayList();
		while (list.size() != builderCount) {
			runChunkUploads(Long.MAX_VALUE);
			try {
				list.add(allocateRenderBuilder());
			} catch (InterruptedException var3) {

			}
		}
		freeBuilders.addAll(list);
	}

	public void freeRenderBuilder(RegionRenderCacheBuilder builder) {
		freeBuilders.add(builder);
	}

	public RegionRenderCacheBuilder allocateRenderBuilder() throws InterruptedException {
		return freeBuilders.take();
	}

	public ChunkRenderTaskWrapper getNextChunkUpdate() throws InterruptedException {
		return chunkUpdates.take();
	}

	public boolean updateTransparencyLater(ChunkWrapper chunk) {
		chunk.getLockCompileTask().lock();
		boolean flag;

		try {
			final ChunkRenderTaskWrapper wrapper = chunk.makeCompileTaskTransparency();
			if (wrapper == null) {
				flag = true;
				return flag;
			}
			wrapper.appendFinishEvent(new Runnable() {
				@Override
				public void run() {
					MapRenderThreadManager.this.chunkUpdates.remove(wrapper);
				}
			});
			flag = chunkUpdates.offer(wrapper);
		} finally {
			chunk.getLockCompileTask().unlock();
		}
		return flag;
	}

	public ListenableFuture<Object> uploadChunk(final BlockRenderLayer layer, final BufferBuilder builder,
			ChunkWrapper wrapper, final CompiledChunk chunk, final double distanceSq) {
		if (Minecraft.getMinecraft().isCallingFromMinecraftThread()) {
			uploadDisplayList(builder, wrapper.getDisplayList(layer, chunk), wrapper);
			builder.setTranslation(0.0D, 0.0D, 0.0D);
			return Futures.<Object>immediateFuture(null);
		} else {
			ListenableFutureTask<Object> task = ListenableFutureTask.<Object>create(new Runnable() {
				@Override
				public void run() {
					MapRenderThreadManager.this.uploadChunk(layer, builder, wrapper, chunk, distanceSq);
				}
			}, null);
			synchronized (chunkUploads) {
				chunkUploads.add(new MapRenderThreadManager.PendingUpload(task, distanceSq));
				return task;
			}
		}
	}

	private void uploadDisplayList(BufferBuilder builder, int list, ChunkWrapper wrapper) {
		GlStateManager.glNewList(list, 4864);
		GlStateManager.pushMatrix();
		wrapper.multModelviewMatrix();
		this.worldVertexUploader.draw(builder);
		GlStateManager.popMatrix();
		GlStateManager.glEndList();
	}

	public void clearChunkUpdates() {
		while (!chunkUpdates.isEmpty()) {
			ChunkRenderTaskWrapper wrapper = chunkUpdates.poll();
			if (wrapper != null) {
				wrapper.finish();
			}
		}
	}

	public boolean hasChunkUpdates() {
		return chunkUpdates.isEmpty() && chunkUploads.isEmpty();
	}

	public void stopThreads() {
		clearChunkUpdates();
		for (MapRenderThread thread : threads) {
			thread.notifyStop();
		}
		for (MapRenderThread thread : threads) {
			try {
				thread.interrupt();
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		freeBuilders.clear();
	}

	public boolean hasNoFreeBuilders() {
		return freeBuilders.isEmpty();
	}

	public class PendingUpload implements Comparable<PendingUpload> {
		private final ListenableFutureTask<Object> uploadTask;
		private final double distanceSq;

		public PendingUpload(ListenableFutureTask<Object> uploadTaskIn, double distanceSqIn) {
			this.uploadTask = uploadTaskIn;
			this.distanceSq = distanceSqIn;
		}

		@Override
		public int compareTo(PendingUpload p_compareTo_1_) {
			return Doubles.compare(this.distanceSq, p_compareTo_1_.distanceSq);
		}
	}
}
