package com.pinball3d.zone.sphinx.elite;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
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

	public Font(String name, int fileSize) {
		InputStream stream = null;
		try {
			stream = Minecraft.getMinecraft().getResourceManager()
					.getResource(new ResourceLocation("zone:fonts/" + name + ".bin")).getInputStream();
			ByteBuffer buffer = Util.readToBuffer(stream, fileSize);
			buffer.clear();
			boolean[] flags = Util.byteToBool8(buffer.get());
			size = buffer.getInt();
			lineHeight = buffer.getInt();
			base = buffer.getInt();
			scaleW = buffer.getInt();
			scaleH = buffer.getInt();
			pages = buffer.getInt();
			charsCount = buffer.getInt();
			kerningsCount = buffer.getInt();
			padding = new int[] { buffer.getInt(), buffer.getInt(), buffer.getInt(), buffer.getInt() };
			spacing = new int[] { buffer.getInt(), buffer.getInt() };
			for (int i = 0; i < charsCount; i++) {
				int id = buffer.getInt();
				Integer[] a = new Integer[] { buffer.getInt(), buffer.getInt(), buffer.getInt(), buffer.getInt(),
						buffer.getInt(), buffer.getInt(), buffer.getInt(), buffer.getInt() };
				chars.put(id, a);
			}
			for (int i = 0; i < kerningsCount; i++) {
				long l = buffer.getInt();
				l <<= 32;
				l |= buffer.getInt();
				kernings.put(l, buffer.getInt());
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
