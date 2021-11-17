package com.pinball3d.zone.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnegative;

import org.lwjgl.opengl.GL11;

import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import com.pinball3d.zone.gui.GuiContainerZone;
import com.pinball3d.zone.gui.IHasSubscreen;
import com.pinball3d.zone.gui.Subscreen;
import com.pinball3d.zone.gui.component.Component;
import com.pinball3d.zone.pdf.PDFHelper;
import com.pinball3d.zone.pdf.PDFImage;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public class Util {
	private static final ResourceLocation BORDER_2 = new ResourceLocation("zone:textures/gui/sphinx/ui_border_2.png");
	private static Map<String, ModContainer> mods = null;
	public static Comparator<ItemStack> itemStackComparator = new Comparator<ItemStack>() {
		@Override
		public int compare(ItemStack o1, ItemStack o2) {
			int i = Item.getIdFromItem(o1.getItem());
			int i2 = Item.getIdFromItem(o2.getItem());
			if (i < i2) {
				return -1;
			}
			if (i > i2) {
				return 1;
			}
			return o1.getHasSubtypes() && o2.getHasSubtypes() ? o1.getItemDamage() - o2.getItemDamage() : 0;
		}
	};

	public static String DATA_CORRUPTION = "[Data Corruption]";

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
		drawBorder(x, y, width, height, lineWidth, color, 0, 0);
	}

	@SideOnly(Side.CLIENT)
	public static void drawBorder(int x, int y, int width, int height, int lineWidth, int color, @Nonnegative int upCut,
			@Nonnegative int downCut) {
		if (upCut < lineWidth) {
			Gui.drawRect(x, y + upCut, x + width, y + lineWidth, color);
		}
		if (downCut < lineWidth) {
			Gui.drawRect(x, y + height - lineWidth, x + width, y + height - downCut, color);
		}
		Gui.drawRect(x, y + lineWidth + upCut, x + lineWidth, y + height - lineWidth - downCut, color);
		Gui.drawRect(x + width - lineWidth, y + lineWidth + upCut, x + width, y + height - lineWidth - downCut, color);
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

	@SideOnly(Side.CLIENT)
	public static void renderItem(ItemStack stack, int x, int y, float scale) {
		GlStateManager.pushMatrix();
		RenderItem ir = Minecraft.getMinecraft().getRenderItem();
		GlStateManager.enableLighting();
		GlStateManager.enableDepth();
		GlStateManager.enableBlend();
		RenderHelper.enableGUIStandardItemLighting();
		GlStateManager.disableLighting();
		GlStateManager.enableRescaleNormal();
		GlStateManager.enableColorMaterial();
		GlStateManager.enableLighting();
		GlStateManager.translate(x, y, 0);
		GlStateManager.scale(scale, scale, scale);
		ir.renderItemAndEffectIntoGUI(stack, 0, 0);
		GlStateManager.popMatrix();
	}

	@SideOnly(Side.CLIENT)
	public static void drawTexture(int x, int y, int width, int height) {
		drawTexture(x, y, 0, 0, width, height, 1.0F);
	}

	@SideOnly(Side.CLIENT)
	public static void drawTexture(int x, int y, int uWidth, int vHeight, float scale) {
		drawTexture(x, y, 0, 0, uWidth, vHeight, scale);
	}

	@SideOnly(Side.CLIENT)
	public static void drawTexture(int x, int y, int u, int v, int uWidth, int vHeight, float scale) {
		drawTexture(x, y, (int) (scale * uWidth), (int) (scale * vHeight), u, v, uWidth, vHeight);
	}

	@SideOnly(Side.CLIENT)
	public static void drawTexture(int x, int y, int width, int height, int u, int v, int uWidth, int vHeight) {
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
	public static void drawPDF(PDFImage image, int x, int y, int width, int yOffset, int maxHeight) {
		if (!image.isLoaded()) {
			PDFHelper.instance.loadPdfImage(image);
		}
		GlStateManager.bindTexture(image.getGlTextureId());
		int height = (int) (width / image.width * image.height);
		int renderHeight = (int) (256F / height * maxHeight);
		drawTexture(x, y, width, maxHeight, 0, (int) (yOffset * 256F / height), 256,
				renderHeight > 256 ? 256 : renderHeight);
	}

	@SideOnly(Side.CLIENT)
	public static FontRenderer getFontRenderer() {
		return Minecraft.getMinecraft().fontRenderer;
	}

	@SideOnly(Side.CLIENT)
	public static IHasSubscreen getRoot() {
		return (IHasSubscreen) Minecraft.getMinecraft().currentScreen;
	}

	@SideOnly(Side.CLIENT)
	public static void resetOpenGl() {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.colorMask(true, true, true, false);
		GlStateManager.disableLighting();
	}

	@SideOnly(Side.CLIENT)
	public static void renderGlowString(String text, float x, float y) {
		Util.renderGlowString(text, x, y, 0xFF3AFAFD, 0xFF138E93);
	}

	@SideOnly(Side.CLIENT)
	public static void renderGlowString(String text, float x, float y, int color, int glowColor) {
		FontRenderer fr = Util.getFontRenderer();
		fr.drawString(text, x - 0.2F, y, glowColor, false);
		fr.drawString(text, x, y - 0.2F, glowColor, false);
		fr.drawString(text, x, y, color, false);
	}

	@SideOnly(Side.CLIENT)
	public static void renderSplitGlowString(String text, float x, float y, int wrapWidth) {
		Util.renderSplitGlowString(text, x, y, wrapWidth, 0xFF3AFAFD, 0xFF138E93);
	}

	@SideOnly(Side.CLIENT)
	public static void renderSplitGlowString(String text, float x, float y, int wrapWidth, int color, int glowColor) {
		FontRenderer fr = Util.getFontRenderer();
		fr.drawString(" ", 0, 0, color);
		text = trimStringNewline(text);
		for (String s : fr.listFormattedStringToWidth(text, wrapWidth)) {
			if (fr.getBidiFlag()) {
				String bidiString = null;
				try {
					Bidi bidi = new Bidi((new ArabicShaping(8)).shape(text), 127);
					bidi.setReorderingMode(0);
					bidiString = bidi.writeReordered(2);
				} catch (ArabicShapingException var3) {
					bidiString = text;
				}
				int i = fr.getStringWidth(bidiString);
				x = x + wrapWidth - i;
			}
			renderGlowString(s, x, y, color, glowColor);
			y += fr.FONT_HEIGHT;
		}
	}

	@SideOnly(Side.CLIENT)
	public static String cutStringToWidth(String text, int width) {
		FontRenderer fr = Util.getFontRenderer();
		fr.drawString(" ", 0, 0, 0x00000000);
		while (fr.getStringWidth(text) > width && !text.isEmpty()) {
			if (text.length() <= 1) {
				return "";
			}
			text = text.substring(0, text.length() - 1);
		}
		return text;
	}

	@SideOnly(Side.CLIENT)
	public static void renderGlowHorizonLine(int x, int y, int length) {
		length *= 2;
		Util.drawTexture(BORDER_2, x - 3, y - 3, 0, 0, 14, 17, 0.5F);
		x += 4;
		length -= 7;
		while (length > 243) {
			Util.drawTexture(BORDER_2, x, y - 3, 14, 0, 228, 17, 0.5F);
			x += 114;
			length -= 228;
		}
		Util.drawTexture(BORDER_2, x, y - 3, 250 - length, 0, length + 6, 17, 0.5F);
	}

	@SideOnly(Side.CLIENT)
	public static void renderGlowHorizonLineThin(int x, int y, int length) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, 0.0F);
		GlStateManager.scale(0.5F, 0.5F, 1.0F);
		Util.renderGlowHorizonLine(0, 0, length * 2);
		GlStateManager.popMatrix();
	}

	@SideOnly(Side.CLIENT)
	public static void renderGlowBorder(int x, int y, int width, int height) {
		int originX = x;
		int originY = y;
		int x2 = x + width - 1;
		int y2 = y + height - 1;
		width *= 2;
		height *= 2;
		Util.drawTexture(BORDER_2, x - 1, y - 1, 0, 17, 6, 6, 0.5F);
		x += 2;
		y += 2;
		width -= 4;
		height -= 4;
		while (width > 250) {
			Util.drawTexture(BORDER_2, x, originY - 1, 6, 17, 244, 6, 0.5F);
			Util.drawTexture(BORDER_2, x, y2 - 1, 6, 251, 244, 6, 0.5F);
			x += 122;
			width -= 244;
		}
		int u = 256 - width - 1;
		Util.drawTexture(BORDER_2, x, originY - 1, u, 17, width, 6, 0.5F);
		Util.drawTexture(BORDER_2, x, y2 - 1, u, 251, width, 6, 0.5F);
		while (height > 233) {
			Util.drawTexture(BORDER_2, originX - 1, y, 0, 23, 6, 228, 0.5F);
			Util.drawTexture(BORDER_2, x2 - 1, y, 251, 23, 6, 228, 0.5F);
			y += 114;
			height -= 228;
		}
		int v = 256 - height - 1;
		Util.drawTexture(BORDER_2, originX - 1, y, 0, v, 6, height, 0.5F);
		Util.drawTexture(BORDER_2, x2 - 1, y, 251, v, 6, height - 3, 0.5F);
	}

	public static boolean isCovered(Component c) {
		if (c.getParent() instanceof GuiContainerZone) {
			return !((GuiContainerZone) c.getParent()).getSubscreens().empty();
		}
		if (c.getParent() instanceof Subscreen) {
			return !((Subscreen) c.getParent()).getParent().getSubscreens().peek().equals(c.getParent());
		}
		if (c.getParent() instanceof Component) {
			return isCovered((Component) c.getParent());
		}
		return false;
	}

	public static String trimStringNewline(String text) {
		while (text != null && text.endsWith("\n")) {
			text = text.substring(0, text.length() - 1);
		}
		return text;
	}

	public static String formatAndAntiEscape(String text, Object... parms) {
		return I18n.format(text, parms).replaceAll("\\\\n", "\n");
	}

	/**
	 * 1:is lower case valid 2:is upper valid 4:is number valid
	 * 
	 */
	public static boolean isValidChar(char input, int flag) {
		if (flag > 7) {
			return ChatAllowedCharacters.isAllowedCharacter(input);
		}
		String lowerCase = "abcdefghijklmnopqrstuvwxyz";
		String upperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String number = "1234567890";
		boolean flag2 = false;
		if (lowerCase.contains(String.valueOf(input))) {
			flag2 = true;
			if (flag % 2 != 1) {
				return false;
			}
		}
		if (upperCase.contains(String.valueOf(input))) {
			flag2 = true;
			if (flag % 4 < 2) {
				return false;
			}
		}
		if (number.contains(String.valueOf(input))) {
			flag2 = true;
			if (flag < 4) {
				return false;
			}
		}
		return flag2;
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

	public static String formatString(String string) {
		string = string.replaceAll("" + (char) 0x3000, "  ");
		return string;
	}

	public static String formatStringToWidth(FontRenderer fr, String string, int width) {
		int total = 0;
		String s = "";
		for (int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			total += fr.getCharWidth(c);
			if (total > width) {
				return s;
			}
			s += c;
		}
		return s;
	}

	public static StorageWrapper getItemList() {
		StorageWrapper wrapper = new StorageWrapper();
		ForgeRegistries.ITEMS.forEach(e -> {
			for (CreativeTabs tab : e.getCreativeTabs()) {
				if (tab != null) {
					NonNullList<ItemStack> list = NonNullList.create();
					e.getSubItems(tab, list);
					list.forEach(i -> {
						ItemStack s = i.copy();
						s.setCount(1);
						wrapper.merge(s);
					});
				} else {
					wrapper.merge(new ItemStack(e));
				}
			}
		});
		return wrapper;
	}

	public static String genGravatarUrl(String email) {
		String md5 = genEmailMd5(email);
		if (md5 == null) {
			return "https://secure.gravatar.com/avatar?s=64";
		}
		return "https://secure.gravatar.com/avatar/" + md5 + "?s=64";
	}

	public static String genEmailMd5(String email) {
		if (email != null && !email.isEmpty()) {
			try {
				MessageDigest md = MessageDigest.getInstance("MD5");
				byte[] array = md.digest(email.getBytes("CP1252"));
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < array.length; ++i) {
					sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
				}
				return sb.toString();
			} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static String transferTickToString(int tick) {
		int h = tick / 72000;
		int m = (tick % 72000) / 1200;
		int s = (tick % 1200) / 20;
		NumberFormat format = NumberFormat.getInstance();
		format.setMinimumIntegerDigits(2);
		return "[" + format.format(h) + ":" + format.format(m) + ":" + format.format(s) + "]";
	}

	public static String transferModIdToName(String modId) {
		if (mods == null) {
			mods = Loader.instance().getIndexedModList();
		}
		ModContainer modContainer = mods.get(modId);
		if (modContainer == null) {
			return modId;
		}
		return modContainer.getName();
	}

	public static boolean search(String search, ItemStack stack) {
		search = search.toLowerCase();
		List<String[]> l = new ArrayList<String[]>();
		for (String s : search.split("\\s+")) {
			l.add(s.split("\\|+"));
		}
		String name = stack.getDisplayName().toLowerCase();
		tag: for (String[] e : l) {
			for (String s : e) {
				if (s.startsWith("@")) {
					if (stack.getItem().getRegistryName().getResourceDomain().toLowerCase().contains(s.substring(1))) {
						continue tag;
					}
				}
				if (s.startsWith("$")) {
					for (int i : OreDictionary.getOreIDs(stack)) {
						if (OreDictionary.getOreName(i).toLowerCase().contains(s.substring(1))) {
							continue tag;
						}
					}
				}
				if (name.contains(s)) {
					continue tag;
				}
			}
			return false;
		}
		return true;
	}

	public static ByteBuffer readToBuffer(InputStream stream, int size) throws IOException {
		ByteBuffer bytebuffer = ByteBuffer.allocateDirect(size + 1);
		ReadableByteChannel channel = Channels.newChannel(stream);
		while (channel.read(bytebuffer) != -1) {
		}
		return bytebuffer;
	}

	public static int readInt(InputStream stream) throws IOException {
		byte[] bytes = new byte[4];
		stream.read(bytes);
		int t = 0;
		for (int i = 0; i < 3; i++) {
			t |= bytes[i] & 0xFF;
			t <<= 8;
		}
		t |= bytes[3] & 0xFF;
		return t;
	}

	public static boolean[] readBooleans(InputStream stream) throws IOException {
		return byteToBool8((byte) stream.read());
	}

	public static boolean[] byteToBool8(byte b) {
		boolean[] bools = new boolean[8];
		for (int i = 0; i < 7; i++) {
			bools[i] = (b & 0x80) == 0x80;
			b <<= 1;
		}
		bools[7] = (b & 1) == 1;
		return bools;
	}
}
