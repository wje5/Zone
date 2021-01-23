package com.pinball3d.zone.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.lwjgl.opengl.GL11;

import com.pinball3d.zone.pdf.PDFHelper;
import com.pinball3d.zone.pdf.PDFImage;
import com.pinball3d.zone.sphinx.IHasSubscreen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Util {
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

	public static StorageWrapper search(StorageWrapper wrapper, String search) {
		if (search.isEmpty()) {
			return wrapper.copy();
		}
		search = search.toLowerCase();
		List<String[]> l = new ArrayList<String[]>();
		for (String s : search.split("\\s+")) {
			l.add(s.split("\\|+"));
		}
		Set<HugeItemStack> storges = new TreeSet<HugeItemStack>(StorageWrapper.hugeStackComparator);
		Iterator<HugeItemStack> it = wrapper.storges.iterator();
		tag2: while (it.hasNext()) {
			HugeItemStack hugestack = it.next();
			String name = hugestack.stack.getDisplayName().toLowerCase();
			tag: for (String[] e : l) {
				for (String s : e) {
					if (s.startsWith("@")) {
						if (hugestack.stack.getItem().getRegistryName().getResourceDomain().contains(s.substring(1))) {
							continue tag;
						}
					}
					if (name.contains(s)) {
						continue tag;
					}
				}
				continue tag2;
			}
			storges.add(hugestack);
		}
		Set<ItemStack> other = new TreeSet<ItemStack>(StorageWrapper.stackComparator);
		Iterator<ItemStack> it2 = wrapper.other.iterator();
		tag4: while (it2.hasNext()) {
			ItemStack stack = it2.next();
			String name = stack.getDisplayName().toLowerCase();
			tag3: for (String[] e : l) {
				for (String s : e) {
					if (s.startsWith("@")) {
						if (stack.getItem().getRegistryName().getResourceDomain().contains(s.substring(1))) {
							continue tag3;
						}
					}
					if (name.contains(s)) {
						continue tag3;
					}
				}
				continue tag4;
			}
			other.add(stack);
		}
		StorageWrapper r = new StorageWrapper();
		storges.forEach(e -> {
			r.merge(e.copy());
		});
		other.forEach(e -> {
			r.merge(e.copy());
		});
		return r;
	}

	public static List<ItemType> getSampleFromWrapper(StorageWrapper wrapper) {
		Set<ItemType> set = new TreeSet<ItemType>(ItemType.comparator);
		wrapper.storges.forEach(e -> {
			set.add(new ItemType(e.stack.getItem(), e.stack.getItemDamage()));
		});
		wrapper.other.forEach(e -> {
			set.add(new ItemType(e.getItem(), e.getItemDamage()));
		});
		return new ArrayList<ItemType>(set);
	}

	public static String genGravatarUrl(String email) {
		if (email != null && !email.isEmpty()) {
			try {
				MessageDigest md = MessageDigest.getInstance("MD5");
				byte[] array = md.digest(email.getBytes("CP1252"));
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < array.length; ++i) {
					sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
				}
				return "https://secure.gravatar.com/avatar/" + sb.toString() + "?s=64";
			} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return "https://secure.gravatar.com/avatar?s=64";
	}
}
