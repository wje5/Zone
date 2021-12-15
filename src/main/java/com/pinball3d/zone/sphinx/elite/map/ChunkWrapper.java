package com.pinball3d.zone.sphinx.elite.map;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.FloatBuffer;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import com.google.common.collect.Sets;
import com.pinball3d.zone.core.LoadingPluginZone;
import com.pinball3d.zone.sphinx.elite.EliteMainwindow;
import com.pinball3d.zone.sphinx.elite.panels.PanelMap;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.chunk.ChunkCompileTaskGenerator;
import net.minecraft.client.renderer.chunk.CompiledChunk;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class ChunkWrapper {
	public static int updatedCount;
	private World world;
	private ChunkCache chunkCache;
	private MutableBlockPos position = new MutableBlockPos(-1, -1, -1);
	private ReentrantLock lockCompileTask = new ReentrantLock();
	private ReentrantLock lockCompiledChunk = new ReentrantLock();
	public CompiledChunk compiledChunk = CompiledChunk.DUMMY;
	private ChunkRenderTaskWrapper wrapper;
	public AxisAlignedBB boundingBox;
	private Set<TileEntity> tileEntities = Sets.<TileEntity>newHashSet();
	private FloatBuffer modelviewMatrix = GLAllocation.createDirectFloatBuffer(16);
	private VertexBuffer[] vertexBuffers = new VertexBuffer[BlockRenderLayer.values().length];
	private int frameIndex = -1;
	private BlockPos.MutableBlockPos[] mapEnumFacing = new BlockPos.MutableBlockPos[6];
	private boolean needsUpdate = true, needsImmediateUpdate;
	private final int baseDisplayList = GLAllocation.generateDisplayLists(BlockRenderLayer.values().length);
	private static Method method;

	public ChunkWrapper(World world) {
		for (int i = 0; i < mapEnumFacing.length; i++) {
			mapEnumFacing[i] = new BlockPos.MutableBlockPos();
		}
		this.world = world;
	}

	public int getDisplayList(BlockRenderLayer layer, CompiledChunk chunk) {
		return !chunk.isLayerEmpty(layer) ? baseDisplayList + layer.ordinal() : -1;
	}

	public boolean setFrameIndex(int index) {
		if (frameIndex == index) {
			return false;
		} else {
			frameIndex = index;
			return true;
		}
	}

	public VertexBuffer getVertexBufferByLayer(int layer) {
		return this.vertexBuffers[layer];
	}

	public void rebuildChunk(float x, float y, float z, ChunkRenderTaskWrapper wrapper) {
		CompiledChunk compiledchunk = new CompiledChunk();
		wrapper.getLock().lock();
		try {
			if (wrapper.getStatus() != ChunkCompileTaskGenerator.Status.COMPILING) {
				return;
			}
			wrapper.setCompiledChunk(compiledchunk);
		} finally {
			wrapper.getLock().unlock();
		}
		VisGraph graph = new VisGraph();
		Set<TileEntity> set = new HashSet<TileEntity>();
		if (!chunkCache.isEmpty()) {
			updatedCount++;
			boolean[] usedLayers = new boolean[BlockRenderLayer.values().length];
			BlockRendererDispatcher dispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
			for (BlockPos.MutableBlockPos pos : BlockPos.getAllInBoxMutable(position, position.add(15, 15, 15))) {
				IBlockState state = chunkCache.getBlockState(pos);
				Block block = state.getBlock();
				if (state.isOpaqueCube()) {
					graph.setOpaqueCube(pos);
				}
				if (block.hasTileEntity(state)) {
					TileEntity tileentity = chunkCache.getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK);
					if (tileentity != null) {
						TileEntitySpecialRenderer<TileEntity> tesr = TileEntityRendererDispatcher.instance
								.<TileEntity>getRenderer(tileentity);
						if (tesr != null) {
							if (tesr.isGlobalRenderer(tileentity)) {
								set.add(tileentity);
							} else {
								compiledchunk.addTileEntity(tileentity);
							}
						}
					}
				}
				for (BlockRenderLayer layer : BlockRenderLayer.values()) {
					if (!block.canRenderInLayer(state, layer)) {
						continue;
					}
					net.minecraftforge.client.ForgeHooksClient.setRenderLayer(layer);
					int j = layer.ordinal();
					if (block.getDefaultState().getRenderType() != EnumBlockRenderType.INVISIBLE) {
						BufferBuilder bufferbuilder = wrapper.getCacheBuilder().getWorldRendererByLayerId(j);
						if (!compiledchunk.isLayerStarted(layer)) {
							compiledchunk.setLayerStarted(layer);
							preRender(bufferbuilder, position);
						}
						usedLayers[j] |= dispatcher.renderBlock(state, pos, chunkCache, bufferbuilder);
					}
				}
				net.minecraftforge.client.ForgeHooksClient.setRenderLayer(null);
			}
			for (BlockRenderLayer blockrenderlayer : BlockRenderLayer.values()) {
				if (usedLayers[blockrenderlayer.ordinal()]) {
					setLayerUsed(compiledchunk, blockrenderlayer);
				}
				if (compiledchunk.isLayerStarted(blockrenderlayer)) {
					postRender(blockrenderlayer, x, y, z,
							wrapper.getCacheBuilder().getWorldRendererByLayer(blockrenderlayer), compiledchunk);
				}
			}
		}
		compiledchunk.setVisibility(graph.computeVisibility());
		this.lockCompileTask.lock();
		try {
			Set<TileEntity> s = new HashSet<TileEntity>();
			Set<TileEntity> s2 = Sets.newHashSet(tileEntities);
			s.removeAll(tileEntities);
			s2.removeAll(set);
			tileEntities.clear();
			tileEntities.addAll(set);
//			renderGlobal.updateTileEntities(s2, s);TODO
		} finally {
			lockCompileTask.unlock();
		}
	}

	private void preRender(BufferBuilder buffer, BlockPos pos) {
		buffer.begin(7, DefaultVertexFormats.BLOCK);
		buffer.setTranslation((-pos.getX()), (-pos.getY()), (-pos.getZ()));
	}

	private void postRender(BlockRenderLayer layer, float x, float y, float z, BufferBuilder buffer,
			CompiledChunk compiledChunkIn) {
		if (layer == BlockRenderLayer.TRANSLUCENT && !compiledChunkIn.isLayerEmpty(layer)) {
			buffer.sortVertexData(x, y, z);
			compiledChunkIn.setState(buffer.getVertexState());
		}
		buffer.finishDrawing();
	}

	private void initModelviewMatrix() {
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();
		GlStateManager.translate(-8.0F, -8.0F, -8.0F);
		GlStateManager.scale(1.000001F, 1.000001F, 1.000001F);
		GlStateManager.translate(8.0F, 8.0F, 8.0F);
		GlStateManager.getFloat(2982, modelviewMatrix);
		GlStateManager.popMatrix();
	}

	public void multModelviewMatrix() {
		GlStateManager.multMatrix(modelviewMatrix);
	}

	public static void setLayerUsed(CompiledChunk chunk, BlockRenderLayer layer) {
		try {
			if (method == null) {
				method = chunk.getClass().getDeclaredMethod(
						LoadingPluginZone.runtimeDeobf ? "func_178486_a" : "setLayerUsed", BlockRenderLayer.class);
				method.setAccessible(true);
			}
			method.invoke(chunk, layer);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public ChunkRenderTaskWrapper makeTaskWrapper() {
		lockCompileTask.lock();
		ChunkRenderTaskWrapper w;
		try {
			finishCompileTask();
			wrapper = new ChunkRenderTaskWrapper(this, ChunkCompileTaskGenerator.Type.REBUILD_CHUNK, getDistanceSq());
			rebuildWorldView();
			w = wrapper;
		} finally {
			lockCompileTask.unlock();
		}
		return w;
	}

	public void setPosition(int x, int y, int z) {
		if (x != position.getX() || y != position.getY() || z != position.getZ()) {
			stopCompileTask();
			position.setPos(x, y, z);
			boundingBox = new AxisAlignedBB(x, y, z, x + 16, y + 16, z + 16);

			for (EnumFacing enumfacing : EnumFacing.values()) {
				mapEnumFacing[enumfacing.ordinal()].setPos(position).move(enumfacing, 16);
			}
			initModelviewMatrix();
		}
	}

	public void resortTransparency(float x, float y, float z, ChunkRenderTaskWrapper wrapper) {
		CompiledChunk compiledchunk = wrapper.getCompiledChunk();

		if (compiledchunk.getState() != null && !compiledchunk.isLayerEmpty(BlockRenderLayer.TRANSLUCENT)) {
			preRender(wrapper.getCacheBuilder().getWorldRendererByLayer(BlockRenderLayer.TRANSLUCENT), position);
			wrapper.getCacheBuilder().getWorldRendererByLayer(BlockRenderLayer.TRANSLUCENT)
					.setVertexState(compiledchunk.getState());
			postRender(BlockRenderLayer.TRANSLUCENT, x, y, z,
					wrapper.getCacheBuilder().getWorldRendererByLayer(BlockRenderLayer.TRANSLUCENT), compiledchunk);
		}
	}

	public void setNeedsUpdate(boolean immediate) {
		if (needsUpdate) {
			immediate |= needsImmediateUpdate;
		}
		needsUpdate = true;
		needsImmediateUpdate = immediate;
	}

	public void clearNeedsUpdate() {
		needsUpdate = false;
		needsImmediateUpdate = false;
	}

	public boolean needsUpdate() {
		return needsUpdate;
	}

	public boolean needsImmediateUpdate() {
		return needsUpdate && needsImmediateUpdate;
	}

	public CompiledChunk getCompiledChunk() {
		return compiledChunk;
	}

	public void setCompiledChunk(CompiledChunk compiledChunkIn) {
		lockCompiledChunk.lock();
		try {
			compiledChunk = compiledChunkIn;
		} finally {
			lockCompiledChunk.unlock();
		}
	}

	public void stopCompileTask() {
		finishCompileTask();
		compiledChunk = CompiledChunk.DUMMY;
	}

	public void deleteGlResources() {
		stopCompileTask();
		world = null;
		for (int i = 0; i < BlockRenderLayer.values().length; ++i) {
			if (vertexBuffers[i] != null) {
				vertexBuffers[i].deleteGlBuffers();
			}
		}
		GLAllocation.deleteDisplayLists(baseDisplayList, BlockRenderLayer.values().length);
	}

	public ChunkRenderTaskWrapper makeCompileTaskTransparency() {
		lockCompileTask.lock();
		ChunkRenderTaskWrapper w;
		try {
			if (wrapper == null || wrapper.getStatus() != ChunkCompileTaskGenerator.Status.PENDING) {
				if (wrapper != null && wrapper.getStatus() != ChunkCompileTaskGenerator.Status.DONE) {
					wrapper.finish();
					wrapper = null;
				}
				wrapper = new ChunkRenderTaskWrapper(this, ChunkCompileTaskGenerator.Type.RESORT_TRANSPARENCY,
						getDistanceSq());
				wrapper.setCompiledChunk(compiledChunk);
				w = wrapper;
				return w;
			}
			w = null;
		} finally {
			lockCompileTask.unlock();
		}
		return w;
	}

	public double getDistanceSq() {
		Minecraft mc = Minecraft.getMinecraft();
		if (mc.currentScreen instanceof EliteMainwindow) {
			PanelMap map = ((EliteMainwindow) mc.currentScreen).getMapPanel();
			if (map != null) {
				MapRenderManager manager = map.getRenderManager();
				double d0 = boundingBox.minX + 8.0D - manager.cameraX;
				double d1 = boundingBox.minY + 8.0D - manager.cameraY;
				double d2 = boundingBox.minZ + 8.0D - manager.cameraZ;
				return d0 * d0 + d1 * d1 + d2 * d2;
			}
		}
		return Integer.MAX_VALUE;
	}

	public BlockPos getPosition() {
		return position;
	}

	private void rebuildWorldView() {
		ChunkCache cache = new ChunkCache(world, position.add(-1, -1, -1), position.add(16, 16, 16), 1);
		net.minecraftforge.client.MinecraftForgeClient.onRebuildChunk(world, position, cache);
		chunkCache = cache;
	}

	public void finishCompileTask() {
		lockCompileTask.lock();
		try {
			if (wrapper != null && wrapper.getStatus() != ChunkCompileTaskGenerator.Status.DONE) {
				wrapper.finish();
				wrapper = null;
			}
		} finally {
			lockCompileTask.unlock();
		}
	}

	public ReentrantLock getLockCompileTask() {
		return lockCompileTask;
	}

	public BlockPos getBlockPosOffset16(EnumFacing facing) {
		return mapEnumFacing[facing.ordinal()];
	}

	public World getWorld() {
		return world;
	}
}
