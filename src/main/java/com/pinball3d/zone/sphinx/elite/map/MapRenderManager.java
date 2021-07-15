package com.pinball3d.zone.sphinx.elite.map;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class MapRenderManager {
	private int currentRenderRange = -1, renderRange = 8;
	private MapRenderThreadManager renderManager;
	private Set<ChunkWrapper> chunksToUpdate = new LinkedHashSet<ChunkWrapper>();
	private boolean displayListDirty = true;
	private Set<TileEntity> setTileEntities = new HashSet<TileEntity>();
	private WorldChunkManager chunkManager;
	private List<ContainerLocalRenderInformation> renderInfos = new ArrayList<ContainerLocalRenderInformation>(69696);
	private ChunkWrapperList chunkWrapperList = new ChunkWrapperList();
	private float cameraX, cameraY, cameraZ, cameraPrevX, cameraPrevY, cameraPrevZ;
	private final DynamicTexture lightmapTexture;
	private final ResourceLocation locationLightMap;
	private Minecraft mc = Minecraft.getMinecraft();
	private World world;
	private double chunkManagerUpdateX = Double.MIN_VALUE;
	private double chunkManagerUpdateY = Double.MIN_VALUE;
	private double chunkManagerUpdateZ = Double.MIN_VALUE;
	private int chunkManagerUpdateChunkX = Integer.MIN_VALUE;
	private int chunkManagerUpdateChunkY = Integer.MIN_VALUE;
	private int chunkManagerUpdateChunkZ = Integer.MIN_VALUE;
	private double prevRenderSortX, prevRenderSortY, prevRenderSortZ;

	private int frameCount;

	public MapRenderManager() {
		lightmapTexture = new DynamicTexture(16, 16);
		locationLightMap = mc.getTextureManager().getDynamicTextureLocation("lightMap", lightmapTexture);
	}

	public void doRender(int mouseX, int mouseY, float partialTicks) {
		int j = Math.min(Minecraft.getDebugFPS(), mc.gameSettings.limitFramerate);
		j = Math.max(j, 60);
		long l = Math.max(1000000000 / j / 4, 0L);
		long finishTime = System.nanoTime() + l;

		// TODO EntityRenderer.updateLightMap
		GlStateManager.enableDepth();
		GlStateManager.enableAlpha();
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.5F);
		// RENDER START
//		GlStateManager.enableCull(); TODO
		GlStateManager.disableCull();
		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		RenderHelper.disableStandardItemLighting();
		applyMap(partialTicks);
		updateChunks(finishTime);
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		GlStateManager.pushMatrix();
		GlStateManager.disableAlpha();

		renderBlockLayer(BlockRenderLayer.SOLID, partialTicks);
		GlStateManager.enableAlpha();
		mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false,
				this.mc.gameSettings.mipmapLevels > 0);
		renderBlockLayer(BlockRenderLayer.CUTOUT_MIPPED, partialTicks);
		mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
		mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
		renderBlockLayer(BlockRenderLayer.CUTOUT, partialTicks);
		mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();

		GlStateManager.shadeModel(GL11.GL_FLAT);
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		GlStateManager.popMatrix();
		GlStateManager.pushMatrix();
		RenderHelper.enableStandardItemLighting();
		net.minecraftforge.client.ForgeHooksClient.setRenderPass(0);
//		renderEntities(entity, icamera, partialTicks); TODO
		net.minecraftforge.client.ForgeHooksClient.setRenderPass(0);
		RenderHelper.disableStandardItemLighting();
		disableLightmap();
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		GlStateManager.popMatrix();

		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE,
				GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
//        renderglobal.drawBlockDamageTexture(Tessellator.getInstance(), Tessellator.getInstance().getBuffer(), entity, partialTicks); TODO
		mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
		GlStateManager.disableBlend();

		enableLightmap();
//		mc.effectRenderer.renderLitParticles(entity, partialTicks); TODO
		RenderHelper.disableStandardItemLighting();
//		mc.effectRenderer.renderParticles(entity, partialTicks);
		disableLightmap();

		GlStateManager.depthMask(false);
		GlStateManager.enableCull();
//        renderRainSnow(partialTicks); TODO
		GlStateManager.depthMask(true);
//		renderglobal.renderWorldBorder(entity, partialTicks);//TODO
		GlStateManager.disableBlend();
		GlStateManager.enableCull();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
				GlStateManager.DestFactor.ZERO);
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
		GlStateManager.enableBlend();
		GlStateManager.depthMask(false);
		mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		renderBlockLayer(BlockRenderLayer.TRANSLUCENT, partialTicks);

		RenderHelper.enableStandardItemLighting();
		net.minecraftforge.client.ForgeHooksClient.setRenderPass(1);
//		renderglobal.renderEntities(entity, icamera, partialTicks);  TODO
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
				GlStateManager.DestFactor.ZERO);
		net.minecraftforge.client.ForgeHooksClient.setRenderPass(-1);
		RenderHelper.disableStandardItemLighting();

		GlStateManager.shadeModel(7424);
		GlStateManager.depthMask(true);
		GlStateManager.enableCull();
		GlStateManager.disableBlend();
		GlStateManager.disableFog();
		frameCount++;

	}

	public void applyMap(float partialTicks) {
		if (currentRenderRange != renderRange) {
			initRenderers();
		}
		double d0 = cameraX - chunkManagerUpdateX;
		double d1 = cameraY - chunkManagerUpdateY;
		int chunkX = MathHelper.floor(cameraX / 16D);
		int chunkY = MathHelper.floor(cameraY / 16D);
		int chunkZ = MathHelper.floor(cameraZ / 16D);
		double d2 = cameraZ - chunkManagerUpdateZ;
		if (chunkManagerUpdateChunkX != chunkX || chunkManagerUpdateChunkY != chunkY
				|| chunkManagerUpdateChunkZ != chunkZ || d0 * d0 + d1 * d1 + d2 * d2 > 16.0D) {
			chunkManagerUpdateX = cameraX;
			chunkManagerUpdateY = cameraY;
			chunkManagerUpdateZ = cameraZ;
			chunkManagerUpdateChunkX = chunkX;
			chunkManagerUpdateChunkY = chunkY;
			chunkManagerUpdateChunkZ = chunkZ;
			chunkManager.updateChunkPositions(cameraX, cameraZ);
		}
		double d3 = cameraPrevX + (cameraX - cameraPrevX) * partialTicks;
		double d4 = cameraPrevY + (cameraY - cameraPrevY) * partialTicks;
		double d5 = cameraPrevZ + (cameraZ - cameraPrevZ) * partialTicks;
		chunkWrapperList.init(d3, d4, d5);
		BlockPos blockpos = new BlockPos(MathHelper.floor(d3 / 16D) * 16, MathHelper.floor(d4 / 16D) * 16,
				MathHelper.floor(d5 / 16D) * 16);
		BlockPos cameraPos = new BlockPos(cameraX, cameraY, cameraZ);
		if (displayListDirty) {
			displayListDirty = false;
			renderInfos = new ArrayList<ContainerLocalRenderInformation>();
			Queue<ContainerLocalRenderInformation> queue = new ArrayDeque<ContainerLocalRenderInformation>();
			int i = cameraPos.getY() > 0 ? 248 : 8;
			for (int j = -currentRenderRange; j <= currentRenderRange; j++) {
				for (int k = -currentRenderRange; k <= currentRenderRange; k++) {
					ChunkWrapper chunk = chunkManager.getRenderChunk(new BlockPos((j << 4) + 8, i, (k << 4) + 8));
					if (chunk != null
//							&&camera.isBoundingBoxInFrustum(renderchunk1.boundingBox.expand(0.0,
//							cameraPos.getY() > 0 ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY, 0.0)) TODO
					) {
						chunk.setFrameIndex(frameCount);
						queue.add(new ContainerLocalRenderInformation(chunk, (EnumFacing) null, 0));
					}
				}
			}
			while (!queue.isEmpty()) {
				ContainerLocalRenderInformation info = queue.poll();
				ChunkWrapper chunk3 = info.chunk;
				EnumFacing facing = info.facing;
				this.renderInfos.add(info);
				boolean flag1 = false;// XXX
				for (EnumFacing e : EnumFacing.values()) {
					ChunkWrapper chunk = getRenderChunkOffset(blockpos, chunk3, e);
					if ((!flag1 || !info.hasDirection(e.getOpposite()))
							&& (!flag1 || facing == null
									|| chunk3.getCompiledChunk().isVisible(facing.getOpposite(), e))
							&& chunk != null && chunk.setFrameIndex(frameCount)
//							&& camera.isBoundingBoxInFrustum(renderchunk2.boundingBox) TODO
					) {
						ContainerLocalRenderInformation info2 = new ContainerLocalRenderInformation(chunk, e,
								info.counter + 1);
						info2.setDirection(info.setFacing, e);

						queue.add(info2);
					}
				}
			}
		}

		Set<ChunkWrapper> set = chunksToUpdate;
		chunksToUpdate = new LinkedHashSet<ChunkWrapper>();
		for (ContainerLocalRenderInformation info : renderInfos) {
			ChunkWrapper chunk = info.chunk;
			if (chunk.needsUpdate() || set.contains(chunk)) {
				displayListDirty = true;
				BlockPos blockpos2 = chunk.getPosition().add(8, 8, 8);
				boolean flag3 = blockpos2.distanceSq(cameraPos) < 768.0D;
				if (net.minecraftforge.common.ForgeModContainer.alwaysSetupTerrainOffThread
						|| (!chunk.needsImmediateUpdate() && !flag3)) {
					chunksToUpdate.add(chunk);
				} else {
					renderManager.updateChunkNow(chunk);
					chunk.clearNeedsUpdate();
				}
			}
		}
		chunksToUpdate.addAll(set);
	}

	public void setWorldAndLoadRenderers(World world) {
		if (this.world != null) {
//			this.world.removeEventListener(this); TODO
		}
		chunkManagerUpdateX = Double.MIN_VALUE;
		chunkManagerUpdateY = Double.MIN_VALUE;
		chunkManagerUpdateZ = Double.MIN_VALUE;
		chunkManagerUpdateChunkX = Integer.MIN_VALUE;
		chunkManagerUpdateChunkY = Integer.MIN_VALUE;
		chunkManagerUpdateChunkZ = Integer.MIN_VALUE;
		this.world = world;
		if (world != null) {
			// world.addEventListener(this); TODO
			initRenderers();
		} else {
			chunksToUpdate.clear();
			renderInfos.clear();
			if (chunkManager != null) {
				chunkManager.deleteGlResources();
				chunkManager = null;
			}
			if (renderManager != null) {
				renderManager.stopThreads();
			}
			renderManager = null;
		}
	}

	public void initRenderers() {
		if (world != null) {
			if (renderManager == null) {
				renderManager = new MapRenderThreadManager(this);
			}
			displayListDirty = true;
			currentRenderRange = renderRange;
			if (chunkManager != null) {
				chunkManager.deleteGlResources();
			}
			stopChunkUpdates();
			synchronized (setTileEntities) {
				setTileEntities.clear();
			}
			chunkManager = new WorldChunkManager(world, renderRange, this);
			if (world != null) {
				chunkManager.updateChunkPositions(cameraX, cameraZ);
			}
		}

	}

	public void updateChunks(long finishTime) {
		displayListDirty |= renderManager.runChunkUploads(finishTime);
		if (!chunksToUpdate.isEmpty()) {
			Iterator<ChunkWrapper> it = chunksToUpdate.iterator();
			while (it.hasNext()) {
				ChunkWrapper chunk = it.next();
				boolean flag;
				if (chunk.needsImmediateUpdate()) {
					flag = renderManager.updateChunkNow(chunk);
				} else {
					flag = renderManager.updateChunkLater(chunk);
				}
				if (!flag) {
					break;
				}
				chunk.clearNeedsUpdate();
				it.remove();
				long time = finishTime - System.nanoTime();
				if (time < 0L) {
					break;
				}
			}
		}
	}

	public int renderBlockLayer(BlockRenderLayer layer, double partialTicks) {
		RenderHelper.disableStandardItemLighting();
		if (layer == BlockRenderLayer.TRANSLUCENT) {
			double d0 = cameraX - prevRenderSortX;
			double d1 = cameraY - prevRenderSortY;
			double d2 = cameraZ - prevRenderSortZ;
			if (d0 * d0 + d1 * d1 + d2 * d2 > 1.0D) {
				prevRenderSortX = cameraX;
				prevRenderSortY = cameraY;
				prevRenderSortZ = cameraZ;
				int k = 0;
				for (ContainerLocalRenderInformation info : renderInfos) {
					if (info.chunk.compiledChunk.isLayerStarted(layer) && k++ < 15) {
						renderManager.updateTransparencyLater(info.chunk);
					}
				}
			}
		}
		int l = 0;
		boolean flag = layer == BlockRenderLayer.TRANSLUCENT;
		int i1 = flag ? renderInfos.size() - 1 : 0;
		int i = flag ? -1 : renderInfos.size();
		int j1 = flag ? -1 : 1;
		for (int j = i1; j != i; j += j1) {
			ChunkWrapper chunk = (renderInfos.get(j)).chunk;
			if (!chunk.getCompiledChunk().isLayerEmpty(layer)) {
				++l;
				chunkWrapperList.addChunk(chunk, layer);
			}
		}
		renderBlockLayer(layer);
		return l;
	}

	private void renderBlockLayer(BlockRenderLayer layer) {
		mc.entityRenderer.enableLightmap();
		chunkWrapperList.renderChunkLayer(layer);
		mc.entityRenderer.disableLightmap();
	}

	public void stopChunkUpdates() {
		this.chunksToUpdate.clear();
		renderManager.stopChunkUpdates();
	}

	private ChunkWrapper getRenderChunkOffset(BlockPos cameraPos, ChunkWrapper chunk, EnumFacing facing) {
		BlockPos pos = chunk.getBlockPosOffset16(facing);
		if (MathHelper.abs(cameraPos.getX() - pos.getX()) > currentRenderRange * 16) {
			return null;
		} else if (pos.getY() >= 0 && pos.getY() < 256) {
			return MathHelper.abs(cameraPos.getZ() - pos.getZ()) > currentRenderRange * 16 ? null
					: chunkManager.getRenderChunk(pos);
		} else {
			return null;
		}
	}

	public void disableLightmap() {
		GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GlStateManager.disableTexture2D();
		GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
	}

	public void enableLightmap() {
		GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GlStateManager.matrixMode(5890);
		GlStateManager.loadIdentity();
		float f = 0.00390625F;
		GlStateManager.scale(0.00390625F, 0.00390625F, 0.00390625F);
		GlStateManager.translate(8.0F, 8.0F, 8.0F);
		GlStateManager.matrixMode(5888);
		mc.getTextureManager().bindTexture(this.locationLightMap);
		GlStateManager.glTexParameteri(3553, 10241, 9729);
		GlStateManager.glTexParameteri(3553, 10240, 9729);
		GlStateManager.glTexParameteri(3553, 10242, 10496);
		GlStateManager.glTexParameteri(3553, 10243, 10496);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.enableTexture2D();
		GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
	}

	public float getCameraX() {
		return cameraX;
	}

	public float getCameraY() {
		return cameraY;
	}

	public float getCameraZ() {
		return cameraZ;
	}

	public class ContainerLocalRenderInformation {
		public final ChunkWrapper chunk;
		public final EnumFacing facing;
		public byte setFacing;
		public final int counter;

		private ContainerLocalRenderInformation(ChunkWrapper chunk, EnumFacing facingIn, @Nullable int counterIn) {
			this.chunk = chunk;
			this.facing = facingIn;
			this.counter = counterIn;
		}

		public void setDirection(byte p_189561_1_, EnumFacing p_189561_2_) {
			this.setFacing = (byte) (this.setFacing | p_189561_1_ | 1 << p_189561_2_.ordinal());
		}

		public boolean hasDirection(EnumFacing p_189560_1_) {
			return (this.setFacing & 1 << p_189560_1_.ordinal()) > 0;
		}
	}
}
