package com.pinball3d.zone.sphinx.elite;

public class FontHandler {
	public static Font NORMAL, BOLD, ITALIC, BOLD_ITALIC;

	public static void init() {
		NORMAL = new Font("droidsans/normal/font");
		BOLD = new Font("droidsans/bold/font");
		ITALIC = new Font("droidsans/italic/font");
		BOLD_ITALIC = new Font("droidsans/bold_italic/font");
	}

	private static int renderChar(int x, int y, char c, int color, Font font) {
		Integer[] a = font.getChars().get((int) c);
		if (a != null) {
			EliteRenderHelper.drawTexture(font.getTextures().get(a[7]), x + a[4], y + a[5], a[2], a[3], a[0], a[1],
					a[2], a[3], color);
			return a[6];
		}
		return 0;
	}

	public static int renderText(int x, int y, String s, int color, int width) {
		s = extrusion(s, width);
		return renderText(x, y, s, color);
	}

	public static int renderText(int x, int y, String s, int color) {
		int t = 0;
		boolean underLine = false, italic = false, bold = false;
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
					} else if (d == 'o') {
						italic = true;
						i++;
						continue;
					} else if (d == 'l') {
						bold = true;
						i++;
						continue;
					} else if (d == 'r') {
						italic = false;
						bold = false;
						underLine = false;
						i++;
						continue;
					}
				}
			}
			Font font = italic ? bold ? FontHandler.BOLD_ITALIC : FontHandler.ITALIC
					: bold ? FontHandler.BOLD : FontHandler.NORMAL;
			int w = renderChar(t + x, y, c, color, font);
			if (underLine) {
				EliteRenderHelper.drawRect(t + x - 1, y + font.getLineHeight() - 1, w, 1, color);
			}
			t += w;
		}
		return t;
	}

	public static int getStringWidth(String s) {
		int t = 0;
		boolean italic = false, bold = false;
		char[] a = s.toCharArray();
		for (int i = 0; i < a.length; i++) {
			char c = a[i];
			if (c == '§') {
				if (a.length > i + 1) {
					char d = a[i + 1];
					if (d == 'n') {
						i++;
						continue;
					} else if (d == 'o') {
						italic = true;
						i++;
						continue;
					} else if (d == 'l') {
						bold = true;
						i++;
						continue;
					} else if (d == 'r') {
						italic = false;
						bold = false;
						i++;
						continue;
					}
				}
			}
			Font font = italic ? bold ? FontHandler.BOLD_ITALIC : FontHandler.ITALIC
					: bold ? FontHandler.BOLD : FontHandler.NORMAL;
			float w = getCharWidth(c, font);
			t += w;
		}
		return t;
	}

	public static int getCharWidth(char c, Font font) {
		Integer[] a = font.getChars().get((int) c);
		if (a != null) {
			return a[6];
		}
		return 0;
	}

	public static int getHeight(Font font) {
		return font.getLineHeight();
	}

	public static String extrusion(String text, int width) {
		while (getStringWidth(text) > width) {
			if (text.endsWith("…")) {
				text = text.substring(0, text.length() - 1);
			}
			if (text.length() >= 2) {
				if (text.charAt(text.length() - 2) == '§') {
					switch (text.charAt(text.length() - 1)) {
					case 'n':
					case 'o':
					case 'l':
					case 'r':
						text = text.substring(0, text.length() - 2);
					}
				}
			}
			if (text.length() < 2) {
				return "";
			}
			text = text.substring(0, text.length() - 1) + "…";
		}
		return text;
	}
}
