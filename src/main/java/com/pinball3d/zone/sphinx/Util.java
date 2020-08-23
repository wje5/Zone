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
