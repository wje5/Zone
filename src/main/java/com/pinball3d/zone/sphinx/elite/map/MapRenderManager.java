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
import org.lwjgl.util.glu.Project;
import org.lwjgl.util.vector.Quaternion;

import com.pinball3d.zone.network.MessageUpdateCameraPos;
import com.pinball3d.zone.network.NetworkHandler;
import com.pinball3d.zone.sphinx.elite.EliteMainwindow;
import com.pinball3d.zone.sphinx.elite.MouseHandler;
import com.pinball3d.zone.sphinx.elite.panels.PanelMap;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
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
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
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
	public float cameraX, cameraY = 260, cameraZ, cameraPrevX, cameraPrevY, cameraPrevZ, cameraPitch = 45F,
			cameraYaw = 45F, scale = 0F;
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
	public RayTraceResult rayTraceResult, selectedRayTraceResult;

	public MapRenderManager() {
		lightmapTexture = new DynamicTexture(16, 16);
		locationLightMap = mc.getTextureManager().getDynamicTextureLocation("lightMap", lightmapTexture);
		NetworkHandler.instance
				.sendToServer(new MessageUpdateCameraPos(mc.player, new BlockPos(cameraX, cameraY, cameraZ)));
	}

	public void doRender(int width, int height, int mouseX, int mouseY, float partialTicks) {
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
		getMouseOver(mouseX, mouseY, width, height, partialTicks);
		GlStateManager.enableDepth();
		GlStateManager.enableAlpha();
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.5F);

		GlStateManager.enableCull();

		if (frameBuffer == null) {
			frameBuffer = new Framebuffer(width, height, true);
			frameBuffer.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
		} else if (frameBuffer.framebufferWidth != width || frameBuffer.framebufferHeight != height) {
			frameBuffer.deleteFramebuffer();
			frameBuffer.createFramebuffer(width, height);
		}
		frameBuffer.bindFramebuffer(false);
		GlStateManager.viewport(0, 0, width, height);
		GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

//		GlStateManager.loadIdentity();
//		GlStateManager.ortho(-width / 2F, width / 2F, height / 2F, -height / 2F, 0, 5000);
//		GL11.glFrustum(-width / 2F, width / 2F, height / 2F, -height / 2F, 0.05F, 512 * Math.sqrt(2));
//		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
//		GlStateManager.loadIdentity();
////		GlStateManager.translate(0.0F, 0.0F, 0.05F);
//		GlStateManager.rotate(cameraPitch, 1, 0, 0);
//		GlStateManager.rotate(cameraYaw, 0, 1, 0);
////		GlStateManager.rotate(makeQuaternion(-cameraPitch, cameraYaw + 180F, 0));
//		GlStateManager.scale(scale, -scale, scale);
		GlStateManager.matrixMode(GL11.GL_PROJECTION);
		GlStateManager.loadIdentity();
		Project.gluPerspective(70, width * 1.0F / height, 0.05F, 512 * MathHelper.SQRT_2);
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);

		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		GlStateManager.loadIdentity();
//		GlStateManager.translate(0.0F, 0.0F, 0.05F);
		GlStateManager.translate(0, 0, scale);
		GlStateManager.rotate(cameraPitch, 1, 0, 0);
		GlStateManager.rotate(cameraYaw, 0, 1, 0);

//		GlStateManager.rotate(makeQuaternion(-cameraPitch, cameraYaw + 180F, 0));
//		GlStateManager.scale(scale, -scale, scale);

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

		if (selectedRayTraceResult != null) {
			drawSelectBoundingBox(selectedRayTraceResult, true);
		}
		if (MouseHandler.isCursorEnable() && rayTraceResult != null) {
			drawSelectBoundingBox(rayTraceResult, false);
		}

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

	public void drawSelectBoundingBox(RayTraceResult ray, boolean isSelected) {
		if (ray.typeOfHit == RayTraceResult.Type.BLOCK) {
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
					GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
					GlStateManager.DestFactor.ZERO);
			GlStateManager.glLineWidth(2.0F);
			GlStateManager.disableTexture2D();
			GlStateManager.depthMask(false);
			BlockPos blockpos = ray.getBlockPos();
			IBlockState iblockstate = world.getBlockState(blockpos);

			if (iblockstate.getMaterial() != Material.AIR && world.getWorldBorder().contains(blockpos)) {
				RenderGlobal.drawSelectionBoundingBox(iblockstate.getSelectedBoundingBox(world, blockpos)
						.grow(0.0020000000949949026D).offset(-cameraX, -cameraY, -cameraZ), 1.0F,
						isSelected ? 0.5F : 0.0F, 0.0F, 1.0F);
			}

			GlStateManager.depthMask(true);
			GlStateManager.enableTexture2D();
			GlStateManager.disableBlend();
		}
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

//		BlockPos cameraPos = new BlockPos(cameraX, cameraY, cameraZ);
		BlockPos cameraPos = new BlockPos(cameraX, 8, cameraZ);
		ChunkWrapper renderchunk = chunkManager.getRenderChunk(cameraPos);
		BlockPos blockpos = new BlockPos(MathHelper.floor(d3 / 16D) * 16, MathHelper.floor(d4 / 16D) * 16,
				MathHelper.floor(d5 / 16D) * 16);
		displayListDirty = displayListDirty || !chunksToUpdate.isEmpty() || cameraX != lastViewCameraX
				|| cameraY != lastViewCameraY || cameraZ != lastViewCameraZ || cameraPitch != lastViewCameraRotX
				|| cameraYaw != lastViewCameraRotY;
		lastViewCameraX = cameraX;
		lastViewCameraY = cameraY;
		lastViewCameraZ = cameraZ;
		lastViewCameraRotX = cameraPitch;
		lastViewCameraRotY = cameraYaw;
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

	public Vec3d getViewVector(double partialTicks) {
		return getVectorForRotation((180 - cameraPitch) / 180 * Math.PI, cameraYaw / 180 * Math.PI).normalize();
	}

	public Vec3d getVectorForRotation(double pitch, double yaw) {
		double f = Math.cos(-yaw - Math.PI);
		double f1 = Math.sin(-yaw - Math.PI);
		double f2 = -Math.cos(-pitch);
		double f3 = Math.sin(-pitch);
		return new Vec3d(f1 * f2, f3, f * f2).normalize();
	}

	public Vec3d rotate(Vec3d vec1, Vec3d vec2, double rad) {
		double s = Math.sin(rad);
		double c = Math.cos(rad);
		double omc = 1 - c;
		double xx = vec2.x * vec2.x;
		double yy = vec2.y * vec2.y;
		double zz = vec2.z * vec2.z;
		double xy = vec2.x * vec2.y;
		double yz = vec2.y * vec2.z;
		double zx = vec2.z * vec2.x;
		double xs = vec2.x * s;
		double ys = vec2.y * s;
		double zs = vec2.z * s;
		double x = (omc * xx + c) * vec1.x + (omc * xy - zs) * vec1.y + (omc * zx + ys) * vec1.z;
		double y = (omc * xy + zs) * vec1.x + (omc * yy + c) * vec1.y + (omc * yz - xs) * vec1.z;
		double z = (omc * zx - ys) * vec1.x + (omc * yz + xs) * vec1.y + (omc * zz + c) * vec1.z;
		return new Vec3d(x, y, z);
	}

	private void getMouseOver(int mouseX, int mouseY, int width, int height, double partialTicks) {
		double d1 = (mouseX - width * 0.5);
		double d2 = (mouseY - height * 0.5);
		double xOffset = 0, yOffset = 0, zOffset = 0;
		yOffset += d2 * Math.cos(cameraPitch / 180F * Math.PI);
		double d = d2 * Math.sin(cameraPitch / 180F * Math.PI);
		zOffset += d * Math.cos(cameraYaw / 180F * Math.PI);
		xOffset -= d * Math.sin(cameraYaw / 180F * Math.PI);
		xOffset += d1 * Math.cos(cameraYaw / 180F * Math.PI);
		zOffset += d1 * Math.sin(cameraYaw / 180F * Math.PI);
		xOffset = 0;
		yOffset = 0;
		zOffset = 0;
		double fovy = 70 * Math.PI / 180;
		double fovx = 2 * Math.atan(Math.tan(fovy / 2) / height * width);
		double pitchOffset = Math.atan((mouseY - height * 0.5) * 2 / height * Math.tan(fovy / 2));
		double yawOffset = Math.atan((mouseX - width * 0.5) * 2 / width * Math.tan(fovx / 2) * Math.cos(pitchOffset));
		Vec3d vec31 = new Vec3d(cameraX - xOffset, cameraY - yOffset, cameraZ - zOffset)
				.add(getViewVector(partialTicks).scale(scale));
		Vec3d v = getVectorForRotation(-pitchOffset, 0);
		v = rotate(v, getVectorForRotation(-pitchOffset - Math.PI / 2, 0), yawOffset);
		v = rotate(v, new Vec3d(1, 0, 0), (180 - cameraPitch) / 180 * Math.PI).normalize();
		v = rotate(v, new Vec3d(0, 1, 0), -cameraYaw / 180 * Math.PI);
		Vec3d vec32 = vec31.add(v.scale(400));
		rayTraceResult = rayTraceBlocks(world, vec31, vec32, true, false, false);
	}

	public RayTraceResult rayTraceBlocks(World world, Vec3d vec31, Vec3d vec32, boolean stopOnLiquid,
			boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock) {
		if (Double.isNaN(vec31.x) || Double.isNaN(vec31.y) || Double.isNaN(vec31.z) || Double.isNaN(vec32.x)
				|| Double.isNaN(vec32.y) || Double.isNaN(vec32.z)) {
			return null;
		}
		int i = MathHelper.floor(vec32.x);
		int j = MathHelper.floor(vec32.y);
		int k = MathHelper.floor(vec32.z);
		int l = MathHelper.floor(vec31.x);
		int i1 = MathHelper.floor(vec31.y);
		int j1 = MathHelper.floor(vec31.z);
		BlockPos blockpos = new BlockPos(l, i1, j1);
		IBlockState iblockstate = world.getBlockState(blockpos);
		Block block = iblockstate.getBlock();
		if ((!ignoreBlockWithoutBoundingBox || iblockstate.getCollisionBoundingBox(world, blockpos) != Block.NULL_AABB)
				&& block.canCollideCheck(iblockstate, stopOnLiquid)) {
			RayTraceResult raytraceresult = iblockstate.collisionRayTrace(world, blockpos, vec31, vec32);
			if (raytraceresult != null) {
				return raytraceresult;
			}
		}
		RayTraceResult raytraceresult2 = null;
		int k1 = 2000;
		while (k1-- >= 0) {
			if (Double.isNaN(vec31.x) || Double.isNaN(vec31.y) || Double.isNaN(vec31.z)) {
				return null;
			}
			if (l == i && i1 == j && j1 == k) {
				return returnLastUncollidableBlock ? raytraceresult2 : null;
			}
			boolean flag2 = true;
			boolean flag = true;
			boolean flag1 = true;
			double d0 = 999.0D;
			double d1 = 999.0D;
			double d2 = 999.0D;

			if (i > l) {
				d0 = l + 1.0D;
			} else if (i < l) {
				d0 = l + 0.0D;
			} else {
				flag2 = false;
			}

			if (j > i1) {
				d1 = i1 + 1.0D;
			} else if (j < i1) {
				d1 = i1 + 0.0D;
			} else {
				flag = false;
			}

			if (k > j1) {
				d2 = j1 + 1.0D;
			} else if (k < j1) {
				d2 = j1 + 0.0D;
			} else {
				flag1 = false;
			}

			double d3 = 999.0D;
			double d4 = 999.0D;
			double d5 = 999.0D;
			double d6 = vec32.x - vec31.x;
			double d7 = vec32.y - vec31.y;
			double d8 = vec32.z - vec31.z;

			if (flag2) {
				d3 = (d0 - vec31.x) / d6;
			}

			if (flag) {
				d4 = (d1 - vec31.y) / d7;
			}

			if (flag1) {
				d5 = (d2 - vec31.z) / d8;
			}

			if (d3 == -0.0D) {
				d3 = -1.0E-4D;
			}

			if (d4 == -0.0D) {
				d4 = -1.0E-4D;
			}

			if (d5 == -0.0D) {
				d5 = -1.0E-4D;
			}

			EnumFacing enumfacing;

			if (d3 < d4 && d3 < d5) {
				enumfacing = i > l ? EnumFacing.WEST : EnumFacing.EAST;
				vec31 = new Vec3d(d0, vec31.y + d7 * d3, vec31.z + d8 * d3);
			} else if (d4 < d5) {
				enumfacing = j > i1 ? EnumFacing.DOWN : EnumFacing.UP;
				vec31 = new Vec3d(vec31.x + d6 * d4, d1, vec31.z + d8 * d4);
			} else {
				enumfacing = k > j1 ? EnumFacing.NORTH : EnumFacing.SOUTH;
				vec31 = new Vec3d(vec31.x + d6 * d5, vec31.y + d7 * d5, d2);
			}

			l = MathHelper.floor(vec31.x) - (enumfacing == EnumFacing.EAST ? 1 : 0);
			i1 = MathHelper.floor(vec31.y) - (enumfacing == EnumFacing.UP ? 1 : 0);
			j1 = MathHelper.floor(vec31.z) - (enumfacing == EnumFacing.SOUTH ? 1 : 0);
			blockpos = new BlockPos(l, i1, j1);
			IBlockState iblockstate1 = world.getBlockState(blockpos);
			Block block1 = iblockstate1.getBlock();

			if (!ignoreBlockWithoutBoundingBox || iblockstate1.getMaterial() == Material.PORTAL
					|| iblockstate1.getCollisionBoundingBox(world, blockpos) != Block.NULL_AABB) {
				if (block1.canCollideCheck(iblockstate1, stopOnLiquid)) {
					RayTraceResult raytraceresult1 = iblockstate1.collisionRayTrace(world, blockpos, vec31, vec32);

					if (raytraceresult1 != null) {
						return raytraceresult1;
					}
				} else {
					raytraceresult2 = new RayTraceResult(RayTraceResult.Type.MISS, vec31, enumfacing, blockpos);
				}
			}
		}
		return returnLastUncollidableBlock ? raytraceresult2 : null;
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

	public static Quaternion makeQuaternion(float p_188035_0_, float p_188035_1_, float p_188035_2_) {
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
