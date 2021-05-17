package com.pinball3d.zone.sphinx.elite;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.pinball3d.zone.util.Util;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class FontHandler {
	public static ResourceLocation FONTDATA = new ResourceLocation("zone:fonts/font.bin");
	private static int size, lineHeight, base, scaleW, scaleH, pages, charsCount, kerningsCount;
	private static int[] padding, spacing;
	private static Map<Integer, Integer[]> chars = new HashMap<Integer, Integer[]>();
	private static Map<Long, Integer> kernings = new HashMap<Long, Integer>();

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
	}
}
