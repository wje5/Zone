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
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import com.pinball3d.zone.network.MessageUpdateCameraPos;
import com.pinball3d.zone.network.NetworkHandler;
import com.pinball3d.zone.sphinx.elite.EliteMainwindow;
import com.pinball3d.zone.sphinx.elite.panels.PanelMap;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.World;

public class MapRenderManager implements IWorldEventListener {
	private int currentRenderRange = -1;
	private MapRenderThreadManager renderManager;
	private Set<ChunkWrapper> chunksToUpdate = new LinkedHashSet<ChunkWrapper>();
	private boolean displayListDirty = true;
	private Set<TileEntity> setTileEntities = new HashSet<TileEntity>();
	private WorldChunkManager chunkManager;
	private List<ContainerLocalRenderInformation> renderInfos = new ArrayList<ContainerLocalRenderInformation>(69696);
	private ChunkWrapperList chunkWrapperList = new ChunkWrapperList();
	public float cameraX, cameraY = 0, cameraZ, cameraPrevX, cameraPrevY, cameraPrevZ,
//			
			cameraRotX = -45F, cameraRotY = 45F,
//			cameraRotX = 0, cameraRotY = 0,
			cameraRotZ, scale = 4F;
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
	private float lastViewCameraX = Float.MIN_VALUE;
	private float lastViewCameraY = Float.MIN_VALUE;
	private float lastViewCameraZ = Float.MIN_VALUE;
	private float lastViewCameraRotX = Float.MIN_VALUE;
	private float lastViewCameraRotY = Float.MIN_VALUE;
	private double prevRenderSortX, prevRenderSortY, prevRenderSortZ;

	private int frameCount;

	private Framebuffer frameBuffer;

	public MapRenderManager() {
		lightmapTexture = new DynamicTexture(16, 16);
		locationLightMap = mc.getTextureManager().getDynamicTextureLocation("lightMap", lightmapTexture);
	}

	public void doRender(int x, int y, int width, int height, int mouseX, int mouseY, float partialTicks) {
		int j = Math.min(Minecraft.getDebugFPS(), mc.gameSettings.limitFramerate);
		j = Math.max(j, 60);
		long l = Math.max(1000000000 / j / 4, 0L);
		long finishTime = System.nanoTime() + l;
		GlStateManager.matrixMode(GL11.GL_PROJECTION);
		GlStateManager.pushMatrix();
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		GlStateManager.pushMatrix();
		GlStateManager.matrixMode(GL11.GL_PROJECTION);
		// TODO EntityRenderer.updateLightMap
		GlStateManager.enableDepth();
		GlStateManager.enableAlpha();
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.5F);

		GlStateManager.enableCull();

		if (frameBuffer == null) {
			frameBuffer = new Framebuffer(width, height, true);
			frameBuffer.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
		} else if (frameBuffer.framebufferWidth != width || frameBuffer.framebufferHeight != height) {
			System.out.println("update" + width + "|" + height);
			frameBuffer.deleteFramebuffer();
			frameBuffer.createFramebuffer(width, height);
		}
		frameBuffer.bindFramebuffer(false);
//		GlStateManager.viewport(0, 0, mc.displayWidth, mc.displayHeight);
//		GlStateManager.viewport(0, 0, width, height);
		GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
//		GlStateManager.viewport(x, mc.displayHeight - (y + height), width, height);
		GlStateManager.viewport(0, 0, width, height);
		GlStateManager.loadIdentity();
//		GlStateManager.ortho(0, mc.displayWidth, mc.displayHeight, 0, 0, 5000);
		GlStateManager.ortho(-width / 2F, width / 2F, height / 2F, -height / 2F, -50000, 50000);
//		GL11.glFrustum(0, width, height, 0, 1000, 5000);
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		GlStateManager.loadIdentity();
//		EliteRenderHelper.drawRect(0, 0, width, height, Color.WHITE);
//		EliteRenderHelper.drawBorder(0, 0, width, height, 3, Color.WHITE);
		if (x != 121212) {
//			GlStateManager.translate(width / 2 - x / 2, height / 2 - y / 2, 0);
//			GlStateManager.translate(0, 0, 256);
//		GlStateManager.translate(cameraX, cameraY, cameraZ);
			GlStateManager.rotate(makeQuaternion(cameraRotX, cameraRotY, cameraRotZ));
			GlStateManager.scale(scale, -scale, scale);
//		GlStateManager.translate(0, 0, 0.05F);
			GlStateManager.shadeModel(GL11.GL_SMOOTH);
			mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			RenderHelper.disableStandardItemLighting();
			applyMap(partialTicks);
			updateChunks(finishTime);
			GlStateManager.matrixMode(GL11.GL_MODELVIEW);

			GlStateManager.enableDepth();

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
			GlStateManager.shadeModel(GL11.GL_FLAT);
			GlStateManager.depthMask(true);
			GlStateManager.enableCull();
			GlStateManager.disableBlend();
			GlStateManager.disableFog();
		}
		frameBuffer.unbindFramebuffer();
		GlStateManager.pushMatrix();
		frameBuffer.framebufferRender(width, height);
		GlStateManager.popMatrix();

		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		GlStateManager.popMatrix();
		GlStateManager.matrixMode(GL11.GL_PROJECTION);
		GlStateManager.popMatrix();
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);

		GlStateManager.pushMatrix();
		mc.getFramebuffer().bindFramebuffer(true);
//		GlStateManager.loadIdentity();
//		GlStateManager.translate(0, 0, -1000);
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO,
				GlStateManager.DestFactor.ONE);
//		frameBuffer.framebufferRenderExt(width, height, false);
		renderFrameBuffer(width, height);
		GlStateManager.popMatrix();
		mc.getFramebuffer().bindFramebuffer(true);

		GlStateManager.viewport(0, 0, mc.displayWidth, mc.displayHeight);
		frameCount++;
	}

	public void renderFrameBuffer(int width, int height) {
		GlStateManager.colorMask(true, true, true, false);
		GlStateManager.disableDepth();
		GlStateManager.depthMask(false);
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		GlStateManager.enableTexture2D();
		GlStateManager.disableLighting();
		GlStateManager.disableAlpha();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		frameBuffer.bindFramebufferTexture();
		float f2 = (float) frameBuffer.framebufferWidth / (float) frameBuffer.framebufferTextureWidth;
		float f3 = (float) frameBuffer.framebufferHeight / (float) frameBuffer.framebufferTextureHeight;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		bufferbuilder.pos(0.0D, height, 0.0D).tex(0.0D, 0.0D).color(255, 255, 255, 255).endVertex();
		bufferbuilder.pos(width, height, 0.0D).tex(f2, 0.0D).color(255, 255, 255, 255).endVertex();
		bufferbuilder.pos(width, 0.0D, 0.0D).tex(f2, f3).color(255, 255, 255, 255).endVertex();
		bufferbuilder.pos(0.0D, 0.0D, 0.0D).tex(0.0D, f3).color(255, 255, 255, 255).endVertex();
		tessellator.draw();
		frameBuffer.unbindFramebufferTexture();
		GlStateManager.depthMask(true);
		GlStateManager.colorMask(true, true, true, true);
	}

	public void applyMap(float partialTicks) {
		if (currentRenderRange != mc.gameSettings.renderDistanceChunks) {
			initRenderers();
		}
		double d0 = cameraX - chunkManagerUpdateX;
		double d1 = cameraY - chunkManagerUpdateY;
		double d2 = cameraZ - chunkManagerUpdateZ;
		int chunkX = MathHelper.floor(cameraX / 16D);
		int chunkY = MathHelper.floor(cameraY / 16D);
		int chunkZ = MathHelper.floor(cameraZ / 16D);

		if (chunkManagerUpdateChunkX != chunkX || chunkManagerUpdateChunkY != chunkY
				|| chunkManagerUpdateChunkZ != chunkZ || d0 * d0 + d1 * d1 + d2 * d2 > 16.0D) {
			chunkManagerUpdateX = cameraX;
			chunkManagerUpdateY = cameraY;
			chunkManagerUpdateZ = cameraZ;
			chunkManagerUpdateChunkX = chunkX;
			chunkManagerUpdateChunkY = chunkY;
			chunkManagerUpdateChunkZ = chunkZ;
			NetworkHandler.instance
					.sendToServer(new MessageUpdateCameraPos(mc.player, new BlockPos(cameraX, cameraY, cameraZ)));
			chunkManager.updateChunkPositions(cameraX, cameraZ);
		}

		double d3 = cameraPrevX + (cameraX - cameraPrevX) * partialTicks;
		double d4 = cameraPrevY + (cameraY - cameraPrevY) * partialTicks;
		double d5 = cameraPrevZ + (cameraZ - cameraPrevZ) * partialTicks;
		chunkWrapperList.init(d3, d4, d5);

		BlockPos cameraPos = new BlockPos(cameraX, cameraY, cameraZ);
		ChunkWrapper renderchunk = chunkManager.getRenderChunk(cameraPos);
		BlockPos blockpos = new BlockPos(MathHelper.floor(d3 / 16D) * 16, MathHelper.floor(d4 / 16D) * 16,
				MathHelper.floor(d5 / 16D) * 16);
		displayListDirty = displayListDirty || !chunksToUpdate.isEmpty() || cameraX != lastViewCameraX
				|| cameraY != lastViewCameraY || cameraZ != lastViewCameraZ || cameraRotX != lastViewCameraRotX
				|| cameraRotY != lastViewCameraRotY;
		lastViewCameraX = cameraX;
		lastViewCameraY = cameraY;
		lastViewCameraZ = cameraZ;
		lastViewCameraRotX = cameraRotX;
		lastViewCameraRotY = cameraRotY;
		cameraPrevX = cameraX;
		cameraPrevY = cameraY;
		cameraPrevZ = cameraZ;
		if (displayListDirty) {
			displayListDirty = false;
			renderInfos = new ArrayList<ContainerLocalRenderInformation>();
			Queue<ContainerLocalRenderInformation> queue = new ArrayDeque<ContainerLocalRenderInformation>();

			if (renderchunk != null) {
				ContainerLocalRenderInformation info = new ContainerLocalRenderInformation(renderchunk,
						(EnumFacing) null, 0);
				renderchunk.setFrameIndex(frameCount);
				queue.add(info);
			} else {
				int i = cameraPos.getY() > 0 ? 248 : 8;
				for (int j = -currentRenderRange; j <= currentRenderRange; j++) {
					for (int k = -currentRenderRange; k <= currentRenderRange; k++) {
						ChunkWrapper chunk = chunkManager.getRenderChunk(new BlockPos((j << 4) + 8, i, (k << 4) + 8));
						if (chunk != null) {
							chunk.setFrameIndex(frameCount);
							queue.add(new ContainerLocalRenderInformation(chunk, (EnumFacing) null, 0));
						}
					}
				}
			}

			while (!queue.isEmpty()) {
				ContainerLocalRenderInformation info = queue.poll();
				ChunkWrapper chunk3 = info.chunk;
				renderInfos.add(info);
				for (EnumFacing e : EnumFacing.values()) {
					ChunkWrapper chunk = getRenderChunkOffset(blockpos, chunk3, e);
					if (chunk != null && chunk.setFrameIndex(frameCount)) {
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
//		System.out.println("renderInfo" + renderInfos.size());
		for (ContainerLocalRenderInformation info : renderInfos) {
			ChunkWrapper chunk = info.chunk;
			if (chunk.needsUpdate() || set.contains(chunk)) {
				displayListDirty = true;
				chunksToUpdate.add(chunk);
			}
		}
		chunksToUpdate.addAll(set);
	}

	public void setWorldAndLoadRenderers(World world) {
		if (this.world != null) {
			this.world.removeEventListener(this);
		}
		chunkManagerUpdateX = Double.MIN_VALUE;
		chunkManagerUpdateY = Double.MIN_VALUE;
		chunkManagerUpdateZ = Double.MIN_VALUE;
		chunkManagerUpdateChunkX = Integer.MIN_VALUE;
		chunkManagerUpdateChunkY = Integer.MIN_VALUE;
		chunkManagerUpdateChunkZ = Integer.MIN_VALUE;
		this.world = world;
		if (world != null) {
			world.addEventListener(this);
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
			currentRenderRange = mc.gameSettings.renderDistanceChunks;

			if (chunkManager != null) {
				chunkManager.deleteGlResources();
			}
			stopChunkUpdates();
			synchronized (setTileEntities) {
				setTileEntities.clear();
			}
			chunkManager = new WorldChunkManager(world, mc.gameSettings.renderDistanceChunks, this);
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

	protected Vector3f getViewVector(double partialTicks) {
//		float f = cameraRotX;
//		float f1 = cameraRotY;
//		if (Minecraft.getMinecraft().gameSettings.thirdPersonView == 2) {
//			f += 180.0F;
//		}
//		float f2 = MathHelper.cos(-f1 * 0.017453292F - (float) Math.PI);
//		float f3 = MathHelper.sin(-f1 * 0.017453292F - (float) Math.PI);
//		float f4 = -MathHelper.cos(-f * 0.017453292F);
//		float f5 = MathHelper.sin(-f * 0.017453292F);
//		return new Vector3f(f3 * f4, f5, f2 * f4);
		return new Vector3f(cameraRotX, cameraRotY, cameraRotZ);
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
		chunksToUpdate.clear();
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
		GlStateManager.scale(0.00390625F, 0.00390625F, 0.00390625F);
		GlStateManager.translate(8.0F, 8.0F, 8.0F);
		GlStateManager.matrixMode(5888);
		mc.getTextureManager().bindTexture(locationLightMap);
		GlStateManager.glTexParameteri(3553, 10241, 9729);
		GlStateManager.glTexParameteri(3553, 10240, 9729);
		GlStateManager.glTexParameteri(3553, 10242, 10496);
		GlStateManager.glTexParameteri(3553, 10243, 10496);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.enableTexture2D();
		GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
	}

	public boolean isBorder(BlockPos pos, EnumFacing facing) {
//		int chunkX = MathHelper.floor(cameraX / 16D);
//		int chunkZ = MathHelper.floor(cameraZ / 16D);
//		int minX = (chunkX - currentRenderRange + 1) * 16;
//		int maxX = (chunkX + currentRenderRange - 1) * 16 + 15;
//		int minZ = (chunkZ - currentRenderRange + 1) * 16;
//		int maxZ = (chunkZ + currentRenderRange - 1) * 16 + 15;
		switch (facing) {
		case WEST:
//			if (pos.getX() == minX) {
//				return true;
//			}
			if (pos.getX() % 16 == 0) {
				return true;
			}
			return false;
		case EAST:
//			if (pos.getX() == maxX) {
//				return true;
//			}
			if (pos.getX() % 15 == 0) {
				return true;
			}
			return false;
		case NORTH:
//			if (pos.getZ() == minZ) {
//				return true;
//			}
			if (pos.getZ() % 16 == 0) {
				return true;
			}
			return false;
		case SOUTH:
//			if (pos.getZ() == maxZ) {
//				return true;
//			}
			if (pos.getZ() % 16 == 15) {
				return true;
			}
			return false;
		default:
			return false;
		}
	}

	private static Quaternion makeQuaternion(float p_188035_0_, float p_188035_1_, float p_188035_2_) {
		float f = p_188035_0_ * 0.017453292F;
		float f1 = p_188035_1_ * 0.017453292F;
		float f2 = p_188035_2_ * 0.017453292F;
		float f3 = MathHelper.sin(0.5F * f);
		float f4 = MathHelper.cos(0.5F * f);
		float f5 = MathHelper.sin(0.5F * f1);
		float f6 = MathHelper.cos(0.5F * f1);
		float f7 = MathHelper.sin(0.5F * f2);
		float f8 = MathHelper.cos(0.5F * f2);
		return new Quaternion(f3 * f6 * f8 + f4 * f5 * f7, f4 * f5 * f8 - f3 * f6 * f7, f3 * f5 * f8 + f4 * f6 * f7,
				f4 * f6 * f8 - f3 * f5 * f7);
	}

	public static boolean shouldSideBeRendered(IBlockState state, IBlockAccess blockAccess, BlockPos pos,
			EnumFacing facing) {
		Minecraft mc = Minecraft.getMinecraft();
		if (mc.currentScreen instanceof EliteMainwindow) {
			PanelMap map = ((EliteMainwindow) mc.currentScreen).getMapPanel();
			if (map != null) {
				MapRenderManager manager = map.getRenderManager();
				return manager.isBorder(pos, facing) ? true : state.shouldSideBeRendered(blockAccess, pos, facing);
			}
		}
		return state.shouldSideBeRendered(blockAccess, pos, facing);

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

	private void markBlocksForUpdate(int minX, int minY, int minZ, int maxX, int maxY, int maxZ,
			boolean updateImmediately) {
		chunkManager.markBlocksForUpdate(minX, minY, minZ, maxX, maxY, maxZ, updateImmediately);
	}

	@Override
	public void notifyBlockUpdate(World worldIn, BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		markBlocksForUpdate(x - 1, y - 1, z - 1, x + 1, y + 1, z + 1, (flags & 8) != 0);
	}

	@Override
	public void notifyLightSet(BlockPos pos) {
		// TODO Auto-generated method stub

	}

	@Override
	public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {
		markBlocksForUpdate(x1 - 1, y1 - 1, z1 - 1, x2 + 1, y2 + 1, z2 + 1, false);

	}

	@Override
	public void playSoundToAllNearExcept(EntityPlayer player, SoundEvent soundIn, SoundCategory category, double x,
			double y, double z, float volume, float pitch) {
		// TODO Auto-generated method stub

	}

	@Override
	public void playRecord(SoundEvent soundIn, BlockPos pos) {
		// TODO Auto-generated method stub

	}

	@Override
	public void spawnParticle(int particleID, boolean ignoreRange, double xCoord, double yCoord, double zCoord,
			double xSpeed, double ySpeed, double zSpeed, int... parameters) {
		// TODO Auto-generated method stub

	}

	@Override
	public void spawnParticle(int id, boolean ignoreRange, boolean p_190570_3_, double x, double y, double z,
			double xSpeed, double ySpeed, double zSpeed, int... parameters) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEntityAdded(Entity entityIn) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEntityRemoved(Entity entityIn) {
		// TODO Auto-generated method stub

	}

	@Override
	public void broadcastSound(int soundID, BlockPos pos, int data) {
		// TODO Auto-generated method stub

	}

	@Override
	public void playEvent(EntityPlayer player, int type, BlockPos blockPosIn, int data) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress) {
		// TODO Auto-generated method stub

	}
}
