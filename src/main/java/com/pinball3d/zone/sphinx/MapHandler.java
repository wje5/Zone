package com.pinball3d.zone.sphinx;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class MapHandler {
	private static BufferBuilder bufferbuilder;
	private int xOffset, yOffset;
	private PointerPlayer pointerPlayer;
	private Map<Integer, PointerLiving> livings = new HashMap<Integer, PointerLiving>();
	private static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/gui/sphinx/icons.png");
	public static MapHandler instance = new MapHandler();

	public void draw(int width, int height, int xOffset, int yOffset) {
		drawMap(width, height, xOffset, yOffset);
	}

	private void drawMap(int width, int height, int xOffset, int yOffset) {
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		GlStateManager.pushMatrix();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
				GlStateManager.DestFactor.ZERO);
		EntityPlayer player = Minecraft.getMinecraft().player;
		BlockPos pos = player.getPosition();
		int xChunk = getRenderOffsetX(width) < 0 ? getRenderOffsetX(width) / 16 - 1 : getRenderOffsetX(width) / 16;
		int yChunk = getRenderOffsetY(height) < 0 ? getRenderOffsetY(height) / 16 - 1 : getRenderOffsetY(height) / 16;
		int xEnd = getRenderOffsetX(width) + width - 1 < 0 ? (getRenderOffsetX(width) + width - 1) / 16 - 1
				: (getRenderOffsetX(width) + width - 1) / 16;
		int yEnd = getRenderOffsetY(height) + height - 1 < 0 ? (getRenderOffsetY(height) + height - 1) / 16 - 1
				: (getRenderOffsetY(height) + height - 1) / 16;
		for (int i = xChunk; i <= xEnd + 1; i++) {
			for (int j = yChunk; j <= yEnd + 1; j++) {
				ChunkRenderCache data = ClientMapDataHandler.getData(player.world.provider.getDimension(), i, j);
				drawChunk(data, i * 16 - getRenderOffsetX(width), j * 16 - getRenderOffsetY(height));
			}
		}
		GlStateManager.popMatrix();
	}

	private int getRenderOffsetX(int width) {
		int xRenderRange = width / 2;
		return xOffset - xRenderRange + 1;
	}

	private int getRenderOffsetY(int height) {
		int yRenderRange = height / 2;
		return yOffset - yRenderRange + 1;
	}

	private void drawGrid(int x, int y) {
		int color = 0x48C0C0C0;
		Gui.drawRect(x + 15, y, x + 16, y + 16, color);
		Gui.drawRect(x, y + 15, x + 15, y + 16, color);
	}

	private void drawChunk(ChunkRenderCache cache, int xOffset, int zOffset) {
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
				if (cache == null) {
					applyQuad(0x000000, xOffset + x, zOffset + z);

				} else {
					int color = cache.getColor(x, z);
					applyQuad(color, xOffset + x, zOffset + z);
					if (color > 0xBF0000) {
						System.out.println(color);
					}

				}
			}
		}
		tessellator.draw();
		GlStateManager.popMatrix();
		drawGrid(xOffset, zOffset);
	}

	private void applyQuad(int color, int x, int y) {
		float r = (color >> 16) / 255F;
		float g = ((color >> 8) & 0x0000FF) / 255F;
		float b = (color & 0x0000FF) / 255F;
		bufferbuilder.pos(x, y + 1, 0).color(r, g, b, 1.0F).endVertex();
		bufferbuilder.pos(x + 1, y + 1, 0).color(r, g, b, 1.0F).endVertex();
		bufferbuilder.pos(x + 1, y, 0).color(r, g, b, 1.0F).endVertex();
		bufferbuilder.pos(x, y, 0).color(r, g, b, 1.0F).endVertex();
	}
}
