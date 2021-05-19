package com.pinball3d.zone.sphinx.elite;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pinball3d.zone.util.Util;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class Font {
	private int size, lineHeight, base, scaleW, scaleH, pages, charsCount, kerningsCount;
	private int[] padding, spacing;

	// x y width height xoffset yoffset xadvance page
	private Map<Integer, Integer[]> chars = new HashMap<Integer, Integer[]>();

	// long:(first<<32 + second) amount
	private Map<Long, Integer> kernings = new HashMap<Long, Integer>();
	private List<ResourceLocation> textures = new ArrayList<ResourceLocation>();

	public Font(String name) {
		InputStream stream = null;
		try {
			stream = Minecraft.getMinecraft().getResourceManager()
					.getResource(new ResourceLocation("zone:fonts/" + name + ".bin")).getInputStream();
			boolean[] flags = Util.readBooleans(stream);
			size = Util.readInt(stream);
			lineHeight = Util.readInt(stream);
			base = Util.readInt(stream);
			scaleW = Util.readInt(stream);
			scaleH = Util.readInt(stream);
			pages = Util.readInt(stream);
			charsCount = Util.readInt(stream);
			kerningsCount = Util.readInt(stream);
			padding = new int[] { Util.readInt(stream), Util.readInt(stream), Util.readInt(stream),
					Util.readInt(stream) };
			spacing = new int[] { Util.readInt(stream), Util.readInt(stream) };
			for (int i = 0; i < charsCount; i++) {
				int id = Util.readInt(stream);
				Integer[] a = new Integer[] { Util.readInt(stream), Util.readInt(stream), Util.readInt(stream),
						Util.readInt(stream), Util.readInt(stream), Util.readInt(stream), Util.readInt(stream),
						Util.readInt(stream) };
				chars.put(id, a);
			}
			for (int i = 0; i < kerningsCount; i++) {
				long l = Util.readInt(stream);
				l <<= 32;
				l |= Util.readInt(stream);
				kernings.put(l, Util.readInt(stream));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		for (int i = 0; i < pages; i++) {
			textures.add(new ResourceLocation("zone:fonts/" + name + +(i + 1) + ".png"));
		}
	}

	public Map<Integer, Integer[]> getChars() {
		return chars;
	}

	public List<ResourceLocation> getTextures() {
		return textures;
	}

	public Map<Long, Integer> getKernings() {
		return kernings;
	}

	public int getLineHeight() {
		return lineHeight;
	}

	public int getBase() {
		return base;
	}

	public int getScaleW() {
		return scaleW;
	}

	public int getScaleH() {
		return scaleH;
	}

	public int getSize() {
		return size;
	}

	public int getPages() {
		return pages;
	}

	public int getCharsCount() {
		return charsCount;
	}

	public int getKerningsCount() {
		return kerningsCount;
	}

	public int[] getSpacing() {
		return spacing;
	}

	public int[] getPadding() {
		return padding;
	}
}
