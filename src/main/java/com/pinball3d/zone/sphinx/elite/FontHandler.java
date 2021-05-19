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

public class FontHandler {
	public static ResourceLocation FONTDATA = new ResourceLocation("zone:fonts/font.bin");
	private static int size, lineHeight, base, scaleW, scaleH, pages, charsCount, kerningsCount;
	private static int[] padding, spacing;

	// x y width height xoffset yoffset xadvance page
	private static Map<Integer, Integer[]> chars = new HashMap<Integer, Integer[]>();

	// long:(first<<32 + second) amount
	private static Map<Long, Integer> kernings = new HashMap<Long, Integer>();
	private static List<ResourceLocation> textures = new ArrayList<ResourceLocation>();
	public static final int SIZE = 12;

	public static void init() {
		InputStream stream = null;
		try {
			stream = Minecraft.getMinecraft().getResourceManager().getResource(FONTDATA).getInputStream();
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
			textures.add(new ResourceLocation("zone:fonts/font" + (i + 1) + ".png"));
		}
		System.out.println(size);
	}

	private static float renderChar(float x, float y, char c, int color) {
		Integer[] a = chars.get((int) c);
		if (a != null) {
			EliteRenderHelper.drawTexture(textures.get(a[7]), x + a[4] * 0.25F, y + a[5] * 0.25F, a[0], a[1], a[2],
					a[3], 0.25F);
			return a[6] * 0.25F;
		} else {
//			System.out.println((int) c);
			return 0;
		}
	}

	public static float renderText(float x, float y, String s, int color) {
		float t = 0;
		boolean underLine = false;
		char[] a = s.toCharArray();
		for (int i = 0; i < a.length; i++) {
			char c = a[i];
			if (c == '§') {
				if (a.length > i + 1) {
					char d = a[i + 1];
					if (d == 'n') {
						underLine = true;
						i++;
						continue;
					} else if (d == 'r') {
						underLine = false;
						i++;
						continue;
					}
				}
			}

			float w = renderChar(t + x, y, c, color);
			if (underLine) {
				EliteRenderHelper.drawRect(t + x - 0.25F, y + SIZE * 0.25F + 0.25F, w, 0.25F, color);
			}
			t += w;
		}
		return t;
	}

	public static float getStringWidth(String s) {
		if (s == null || s.isEmpty()) {
			return 0;
		}
		float w = 0;
		char[] a = s.toCharArray();
		for (int i = 0; i < a.length; i++) {
			char c = a[i];
			if (c == '§') {
				if (a.length > i + 1) {
					char d = a[i + 1];
					if (d == 'n') {
						i++;
						continue;
					} else if (d == 'r') {
						i++;
						continue;
					}
				}
			}
			w += getCharWidth(c) + 0.25F;
		}
		return w;
	}

	public static float getCharWidth(char c) {
		Integer[] a = chars.get((int) c);
		if (a != null) {
			return a[6] * 0.25F;
		}
		return 0;
	}
}
