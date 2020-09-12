package com.pinball3d.zone.sphinx;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class PointerPlayer extends Pointer {
	public float angle;

	public PointerPlayer(int x, int z) {
		super(x, z);
	}

	@Override
	public void doRender(int offsetX, int offsetZ) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
		GlStateManager.pushMatrix();
		GlStateManager.translate(x - offsetX, z - offsetZ, 0);
		GlStateManager.rotate(angle + 180F, 0, 0, 1);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
				GlStateManager.DestFactor.ZERO);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		float f = 0.00390625F;
		bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		int X = -3, Y = -8;
		bufferbuilder.pos(X, Y + 16, 0).tex(162 * f, 16 * f).endVertex();
		bufferbuilder.pos(X + 7, Y + 16, 0).tex((162 + 7) * f, 16 * f).endVertex();
		bufferbuilder.pos(X + 7, Y, 0).tex((162 + 7) * f, 0).endVertex();
		bufferbuilder.pos(X, Y, 0).tex(162 * f, 0).endVertex();
		tessellator.draw();
		GlStateManager.popMatrix();
	}
}
