package com.pinball3d.zone.sphinx;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class PointerLiving extends Gui {
	public int x, z;
	public boolean isMob;
	private static final ResourceLocation TEXTURE_MOB = new ResourceLocation("zone:textures/gui/sphinx/icon_mob.png");

	public PointerLiving(int x, int z, boolean isMob) {
		this.x = x;
		this.z = z;
		this.isMob = isMob;
	}

	public void doRender(int offsetX, int offsetZ) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE_MOB);
//		drawTexturedModalRect(x - offsetX - 11, z - offsetZ - 34, 0, 0, 23, 35);
		GlStateManager.pushMatrix();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
				GlStateManager.DestFactor.ZERO);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		int posX = x - offsetX - 11;
		int posY = z - offsetZ - 34;
		int sizeX = 16 / 2;
		int sizeY = 24 / 2;
		bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos(posX, posY + sizeY, 0).tex(0, 24 * 0.00390625F).endVertex();
		bufferbuilder.pos(posX + sizeX, posY + sizeY, 0).tex(16 * 0.00390625F, 24 * 0.00390625F).endVertex();
		bufferbuilder.pos(posX + sizeX, posY, 0).tex(16 * 0.00390625F, 0).endVertex();
		bufferbuilder.pos(posX, posY, 0).tex(0, 0).endVertex();
		tessellator.draw();
		GlStateManager.popMatrix();
	}
}
