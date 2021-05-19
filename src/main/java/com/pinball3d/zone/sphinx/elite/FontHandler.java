package com.pinball3d.zone.sphinx.elite;

public class FontHandler {
	public static Font NORMAL, BOLD, ITALIC, BOLD_ITALIC;

	public static void init() {
		NORMAL = new Font("droidsans/normal/font");
	}

	private static float renderChar(float x, float y, char c, int color, Font font) {
		Integer[] a = font.getChars().get((int) c);
		if (a != null) {
			EliteRenderHelper.drawTexture(font.getTextures().get(a[7]), x + a[4] * 0.25F, y + a[5] * 0.25F,
					a[2] * 0.25F, a[3] * 0.25F, a[0], a[1], a[2], a[3], color);
			return a[6] * 0.25F;
		} else {
//			System.out.println((int) c);
			return 0;
		}
	}

	public static float renderText(float x, float y, String s, int color, Font font) {
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

			float w = renderChar(t + x, y, c, color, font);
			if (underLine) {
				EliteRenderHelper.drawRect(t + x - 0.25F, y + font.getLineHeight() * 0.25F - 0.25F, w, 0.25F, color);
			}
			t += w;
		}
		return t;
	}

	public static float getStringWidth(String s, Font font) {
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
			w += getCharWidth(c, font);
		}
		return w;
	}

	public static float getCharWidth(char c, Font font) {
		Integer[] a = font.getChars().get((int) c);
		if (a != null) {
			return a[6] * 0.25F;
		}
		return 0;
	}
}
