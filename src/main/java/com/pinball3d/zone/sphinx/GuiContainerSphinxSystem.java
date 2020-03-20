package com.pinball3d.zone.sphinx;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiContainerSphinxSystem extends GuiContainer {
	private static final ResourceLocation TEXTURE = new ResourceLocation(
			"zone:textures/gui/container/sphinx_system.png");
	protected ContainerSphinxSystem container;
	protected Map<Long, ChunkRenderCache> mapCache = new HashMap<Long, ChunkRenderCache>();
	private static BufferBuilder bufferbuilder;

	public GuiContainerSphinxSystem(ContainerSphinxSystem container) {
		super(container);
		mc = Minecraft.getMinecraft();
		this.container = container;
		xSize = mc.displayWidth / 2;
		ySize = mc.displayHeight / 2;
		ChunkRenderCache.init();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F);
//		mc.getTextureManager().bindTexture(TEXTURE);
//		int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
//
//		drawTexturedModalRect(offsetX, offsetY, 0, 0, this.xSize, this.ySize);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		drawMap();
	}

	private void drawMap() {
		GlStateManager.pushMatrix();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
				GlStateManager.DestFactor.ZERO);
		BlockPos pos = mc.player.getPosition();
		int x = pos.getX() / 16;
		int z = pos.getZ() / 16;
		int size = 1;
		int xRenderRange = xSize / 2 / (size * 16) + 1;
		int yRenderRange = ySize / 2 / (size * 16) + 1;
		for (int i = -xRenderRange; i <= xRenderRange; i++) {
			for (int j = -yRenderRange; j <= yRenderRange; j++) {
				drawChunk(getCache(x + i, z + j), xSize / 2 + i * 16 * size, ySize / 2 + j * 16 * size);
			}
		}
		GlStateManager.popMatrix();
	}

	private void drawChunk(ChunkRenderCache cache, int xOffset, int zOffset) {
		int size = 1;
		GlStateManager.pushMatrix();
		Tessellator tessellator = Tessellator.getInstance();
		bufferbuilder = tessellator.getBuffer();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
				GlStateManager.DestFactor.ZERO);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				applyQuad(cache.getColor(x, z), xOffset + x * size, zOffset + z * size, size);
			}
		}
		tessellator.draw();
		GlStateManager.popMatrix();
		drawGrid(xOffset, zOffset, 16 * size);
	}

	private void applyQuad(int color, int x, int y, int size) {
//		drawRect(x, y, x + size, y + size, color | 0xFF000000);
		float r = (color >> 16) / 255F;
		float g = ((color >> 8) & 0x0000FF) / 255F;
		float b = (color & 0x0000FF) / 255F;
		bufferbuilder.pos(x, y + size, 0).color(r, g, b, 1.0F).endVertex();
		bufferbuilder.pos(x + size, y + size, 0).color(r, g, b, 1.0F).endVertex();
		bufferbuilder.pos(x + size, y, 0).color(r, g, b, 1.0F).endVertex();
		bufferbuilder.pos(x, y, 0).color(r, g, b, 1.0F).endVertex();
	}

	private void drawGrid(int x, int y, int size) {
		int color = 0xC8C0C0C0;
		color = 0x48C0C0C0;
		drawRect(x + size - 1, y, x + size, y + size, color);
		drawRect(x, y + size - 1, x + size - 1, y + size, color);
	}

	public ChunkRenderCache getCache(int x, int z) {
		ChunkRenderCache cache = mapCache.get(x * 30000000L + z);
		if (cache == null) {
			cache = new ChunkRenderCache(mc.player.world.getChunkFromChunkCoords(x, z));
			mapCache.put(x * 30000000L + z, cache);
		}
		return cache;
	}
}
