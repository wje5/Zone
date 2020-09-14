package com.pinball3d.zone.sphinx;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Util {
	@SideOnly(Side.CLIENT)
	public static void drawTexture(ResourceLocation texture, int x, int y, int width, int height) {
		drawTexture(texture, x, y, 0, 0, width, height, 1.0F);
	}

	@SideOnly(Side.CLIENT)
	public static void drawTexture(ResourceLocation texture, int x, int y, int uWidth, int vHeight, float scale) {
		drawTexture(texture, x, y, 0, 0, uWidth, vHeight, scale);
	}

	@SideOnly(Side.CLIENT)
	public static void drawTexture(ResourceLocation texture, int x, int y, int u, int v, int uWidth, int vHeight,
			float scale) {
		drawTexture(texture, x, y, (int) (scale * uWidth), (int) (scale * vHeight), u, v, uWidth, vHeight);
	}

	@SideOnly(Side.CLIENT)
	public static void drawTexture(ResourceLocation texture, int x, int y, int width, int height, int u, int v,
			int uWidth, int vHeight) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		GlStateManager.pushMatrix();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
				GlStateManager.DestFactor.ZERO);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		float f = 0.00390625F;
		bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos(x, y + height, 0).tex(u * f, (v + vHeight) * f).endVertex();
		bufferbuilder.pos(x + width, y + height, 0).tex((u + uWidth) * f, (v + vHeight) * f).endVertex();
		bufferbuilder.pos(x + width, y, 0).tex((u + uWidth) * f, v * f).endVertex();
		bufferbuilder.pos(x, y, 0).tex(u * f, v * f).endVertex();
		tessellator.draw();
		GlStateManager.popMatrix();
	}

	@SideOnly(Side.CLIENT)
	public static void drawBorder(int x, int y, int width, int height, int lineWidth, int color) {
		Gui.drawRect(x, y, x + width, y + lineWidth, color);
		Gui.drawRect(x, y + height - lineWidth, x + width, y + height, color);
		Gui.drawRect(x, y + lineWidth, x + lineWidth, y + height - lineWidth, color);
		Gui.drawRect(x + width - lineWidth, y + lineWidth, x + width, y + height - lineWidth, color);
	}

	@SideOnly(Side.CLIENT)
	public static void drawLine(double x1, double y1, double x2, double y2, int color) {
		float f3 = (color >> 24 & 255) / 255.0F;
		float f = (color >> 16 & 255) / 255.0F;
		float f1 = (color >> 8 & 255) / 255.0F;
		float f2 = (color & 255) / 255.0F;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
				GlStateManager.DestFactor.ZERO);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
		double width = 0.25D;
		double s = (y2 - y1) / (x2 - x1);
		double xoffset = Math.sqrt(width * width * s * s / (s * s + 1));
		double yoffset = Math.abs(xoffset / s);
		if (x2 - x1 == 0) {
			yoffset = 0;
			xoffset = width;
		} else if (y2 - y1 == 0) {
			xoffset = 0;
			yoffset = width;
		}
		if (y1 > y2) {
			if (x1 > x2) {
				bufferbuilder.pos(x1 - xoffset, y1 + yoffset, 0.0D).color(f, f1, f2, f3).endVertex();
				bufferbuilder.pos(x1 + xoffset, y1 - yoffset, 0.0D).color(f, f1, f2, f3).endVertex();
				bufferbuilder.pos(x2 + xoffset, y2 - yoffset, 0.0D).color(f, f1, f2, f3).endVertex();
				bufferbuilder.pos(x2 - xoffset, y2 + yoffset, 0.0D).color(f, f1, f2, f3).endVertex();
			} else {
				if (xoffset > yoffset) {
					bufferbuilder.pos(x1 - xoffset, y1 - yoffset, 0.0D).color(f, f1, f2, f3).endVertex();
					bufferbuilder.pos(x1 + xoffset, y1 + yoffset, 0.0D).color(f, f1, f2, f3).endVertex();
					bufferbuilder.pos(x2 + xoffset, y2 + yoffset, 0.0D).color(f, f1, f2, f3).endVertex();
					bufferbuilder.pos(x2 - xoffset, y2 - yoffset, 0.0D).color(f, f1, f2, f3).endVertex();
				} else {
					bufferbuilder.pos(x2 - xoffset, y2 - yoffset, 0.0D).color(f, f1, f2, f3).endVertex();
					bufferbuilder.pos(x1 - xoffset, y1 - yoffset, 0.0D).color(f, f1, f2, f3).endVertex();
					bufferbuilder.pos(x1 + xoffset, y1 + yoffset, 0.0D).color(f, f1, f2, f3).endVertex();
					bufferbuilder.pos(x2 + xoffset, y2 + yoffset, 0.0D).color(f, f1, f2, f3).endVertex();
				}
			}
		} else if (x1 > x2) {
			if (xoffset > yoffset) {
				bufferbuilder.pos(x2 - xoffset, y2 - yoffset, 0.0D).color(f, f1, f2, f3).endVertex();
				bufferbuilder.pos(x2 + xoffset, y2 + yoffset, 0.0D).color(f, f1, f2, f3).endVertex();
				bufferbuilder.pos(x1 + xoffset, y1 + yoffset, 0.0D).color(f, f1, f2, f3).endVertex();
				bufferbuilder.pos(x1 - xoffset, y1 - yoffset, 0.0D).color(f, f1, f2, f3).endVertex();
			} else {
				bufferbuilder.pos(x2 + xoffset, y2 + yoffset, 0.0D).color(f, f1, f2, f3).endVertex();
				bufferbuilder.pos(x1 + xoffset, y1 + yoffset, 0.0D).color(f, f1, f2, f3).endVertex();
				bufferbuilder.pos(x1 - xoffset, y1 - yoffset, 0.0D).color(f, f1, f2, f3).endVertex();
				bufferbuilder.pos(x2 - xoffset, y2 - yoffset, 0.0D).color(f, f1, f2, f3).endVertex();
			}
		} else {
			bufferbuilder.pos(x2 - xoffset, y2 + yoffset, 0.0D).color(f, f1, f2, f3).endVertex();
			bufferbuilder.pos(x2 + xoffset, y2 - yoffset, 0.0D).color(f, f1, f2, f3).endVertex();
			bufferbuilder.pos(x1 + xoffset, y1 - yoffset, 0.0D).color(f, f1, f2, f3).endVertex();
			bufferbuilder.pos(x1 - xoffset, y1 + yoffset, 0.0D).color(f, f1, f2, f3).endVertex();
		}
		tessellator.draw();
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
	}

	/**
	 * 1:is lower case valid 2:is upper valid 4:is number valid
	 * 
	 */
	public static boolean isValidChar(char input, int flag) {
		String lowerCase = "abcdefghijklmnopqrstuvwxyz";
		String upperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String number = "1234567890";
		if (lowerCase.contains(String.valueOf(input))) {
			return flag % 2 == 1;
		}
		if (upperCase.contains(String.valueOf(input))) {
			return flag % 4 >= 2;
		}
		if (number.contains(String.valueOf(input))) {
			return flag >= 4;
		}
		return false;
	}

	public static String transferString(int count) {
		if (count < 1000) {
			return String.valueOf(count);
		}
		if (count < 100000) {
			return (count / 1000) + "K";
		}
		if (count < 1000000) {
			return "<1M";
		}
		if (count < 100000000) {
			return (count / 1000000) + "M";
		}
		if (count < 1000000000) {
			return "<1G";
		}
		return (count / 1000000000) + "G";
	}

	public static boolean isItemStackEqualEgnoreCount(ItemStack stackA, ItemStack stackB) {
		if (stackA.isEmpty() && stackB.isEmpty()) {
			return true;
		} else {
			if (!stackA.isEmpty() && !stackB.isEmpty()) {
				if (stackA.getItem() != stackB.getItem()) {
					return false;
				} else if (stackA.getItemDamage() != 32767 && stackA.getItemDamage() != stackB.getItemDamage()) {
					return false;
				} else if (stackA.getTagCompound() == null && stackB.getTagCompound() != null) {
					return false;
				} else if (stackB.getTagCompound() == null && stackA.getTagCompound() != null) {
					return false;
				} else {
					return (stackA.getTagCompound() == null || stackA.getTagCompound().equals(stackB.getTagCompound()))
							&& stackA.areCapsCompatible(stackB);
				}
			}
			return false;
		}
	}
}
