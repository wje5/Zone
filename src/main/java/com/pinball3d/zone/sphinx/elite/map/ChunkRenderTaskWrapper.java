package com.pinball3d.zone.sphinx.elite.map;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import com.google.common.primitives.Doubles;

import net.minecraft.client.renderer.RegionRenderCacheBuilder;
import net.minecraft.client.renderer.chunk.ChunkCompileTaskGenerator.Status;
import net.minecraft.client.renderer.chunk.ChunkCompileTaskGenerator.Type;
import net.minecraft.client.renderer.chunk.CompiledChunk;

public class ChunkRenderTaskWrapper implements Comparable<ChunkRenderTaskWrapper> {
	private MapRenderManager renderManager;
	private ChunkWrapper chunk;
	private Type type;
	private Status status = Status.PENDING;
	private CompiledChunk compiledChunk;
	private ReentrantLock lock = new ReentrantLock();
	private List<Runnable> onFinish = new ArrayList<Runnable>();
	private boolean finished;
	private double distanceSq;
	private RegionRenderCacheBuilder cacheBuilder;

	public ChunkRenderTaskWrapper(MapRenderManager renderManager, ChunkWrapper chunk, Type type, double distanceSq) {
		this.renderManager = renderManager;
		this.chunk = chunk;
		this.type = type;
		this.distanceSq = distanceSq;
	}

	public Status getStatus() {
		return status;
	}

	public ChunkWrapper getChunk() {
		return chunk;
	}

	public CompiledChunk getCompiledChunk() {
		return compiledChunk;
	}

	public void setCompiledChunk(CompiledChunk compiledChunk) {
		this.compiledChunk = compiledChunk;
	}

	public RegionRenderCacheBuilder getCacheBuilder() {
		return cacheBuilder;
	}

	public void setCacheBuilder(RegionRenderCacheBuilder cacheBuilder) {
		this.cacheBuilder = cacheBuilder;
	}

	public void setStatus(Status status) {
		lock.lock();
		try {
			this.status = status;
		} finally {
			lock.unlock();
		}
	}

	public void finish() {
		lock.lock();
		try {
			if (type == Type.REBUILD_CHUNK && status != Status.DONE) {
				chunk.setNeedsUpdate(false);
			}
			finished = true;
			status = Status.DONE;
			onFinish.forEach(e -> e.run());
		} finally {
			lock.unlock();
		}
	}

	public void appendFinishEvent(Runnable r) {
		lock.lock();
		try {
			if (finished) {
				r.run();
			} else {
				onFinish.add(r);
			}
		} finally {
			lock.unlock();
		}
	}

	public ReentrantLock getLock() {
		return lock;
	}

	public Type getType() {
		return type;
	}

	public boolean isFinished() {
		return this.finished;
	}

	@Override
	public int compareTo(ChunkRenderTaskWrapper wrapper) {
		return Doubles.compare(distanceSq, wrapper.distanceSq);
	}

	public double getDistanceSq() {
		return this.distanceSq;
	}
}
