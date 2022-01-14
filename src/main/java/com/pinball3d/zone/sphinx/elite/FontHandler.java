package com.pinball3d.zone.sphinx.elite;

import com.pinball3d.zone.sphinx.elite.FormattedString.StringComponent;

public class FontHandler {
	public static Font NORMAL, BOLD, ITALIC, BOLD_ITALIC;
	public static int HEIGHT = 17;

	public static void init() {
		NORMAL = new Font("founder/normal/font", 277473);
		BOLD = new Font("founder/bold/font", 277473);
		ITALIC = new Font("founder/italic/font", 277473);
		BOLD_ITALIC = new Font("founder/bold_italic/font", 277473);
	}

	public static int renderChar(int x, int y, char c, Color color, Font font) {
		Integer[] a = font.getChars().get((int) c);
		if (a != null) {
			EliteRenderHelper.drawTexture(font.getTextures().get(a[7]), x + a[4], y + a[5], a[2], a[3], a[0], a[1],
					a[2], a[3], color);
			return a[6];
		}
		return 0;
	}

	public static int renderText(int x, int y, FormattedString s, Color color, int width) {
		s = extrusion(s, width);
		return renderText(x, y, s, color);
	}

	public static int renderTextCenter(int x, int y, FormattedString s, Color color) {
		return renderText(x - getStringWidth(s) / 2, y, s, color);
	}

	public static int renderText(int x, int y, FormattedString s, Color color) {
		return renderText(x, y, s, color, false);
	}

	public static int renderText(int x, int y, FormattedString s, Color color, boolean isAlt) {
		if (s == null) {
			return 0;
		}
		int t = 0;
		for (int i = 0; i < s.getComponentsSize(); i++) {
			StringComponent c = s.get(i);
			char[] a = c.text.toCharArray();
			Font font = c.italic ? c.bold ? FontHandler.BOLD_ITALIC : FontHandler.ITALIC
					: c.bold ? FontHandler.BOLD : FontHandler.NORMAL;
			Color cColor = c.color == null ? color : c.color;
			int w = 0;
			for (char e : a) {
				w += renderChar(t + x + w, y, e, cColor, font);
			}
			if (c.underline == 1 || (c.underline == 2 && isAlt)) {
				EliteRenderHelper.drawRect(t + x - 1, y + font.getLineHeight() - 1, w, 1, color);
			}
			t += w;
		}
		return t;
	}

	public static int getStringComponentWidth(StringComponent c) {
		if (c == null) {
			return 0;
		}
		Font font = c.italic ? c.bold ? FontHandler.BOLD_ITALIC : FontHandler.ITALIC
				: c.bold ? FontHandler.BOLD : FontHandler.NORMAL;
		int w = 0;
		for (int i = 0; i < c.text.length(); i++) {
			w += getCharWidth(c.text.charAt(i), font);
		}
		return w;
	}

	public static int getStringWidth(FormattedString s) {
		if (s == null) {
			return 0;
		}
		int w = 0;
		for (int i = 0; i < s.getComponentsSize(); i++) {
			StringComponent c = s.get(i);
			if (c == null) {
				return 0;
			}
			w += getStringComponentWidth(c);
		}
		return w;
	}

	public static int getCharWidth(char c, Font font) {
		Integer[] a = font.getChars().get((int) c);
		if (a != null) {
			return a[6];
		}
		return 0;
	}

	public static FormattedString extrusion(FormattedString text, int width) {
		if (text == null) {
			return text;
		}
		while (getStringWidth(text) > width) {
			StringComponent c = text.get(text.getComponentsSize() - 1);
			String s = c.text;
			if (s.endsWith("…")) {
				s = s.substring(0, s.length() - 1);
			}
			if (!s.isEmpty()) {
				s = s.substring(0, s.length() - 1) + "…";
				if (s.length() == 1 && text.getComponentsSize() == 1) {
					return FormattedString.EMPTY;
				}
				StringComponent[] a = new StringComponent[text.getComponentsSize()];
				for (int i = 0; i < text.getComponentsSize() - 2; i++) {
					a[i] = text.get(i);
				}
				a[text.getComponentsSize() - 1] = new StringComponent(s, c.color, c.bold, c.italic, c.underline);
				text = new FormattedString(a);
			} else if (text.getComponentsSize() > 1) {
				c = text.get(text.getComponentsSize() - 2);
				s = c.text;
				if (!s.isEmpty()) {
					s = s.substring(0, s.length() - 1) + "…";
				}
				StringComponent[] a = new StringComponent[text.getComponentsSize() - 1];
				for (int i = 0; i < text.getComponentsSize() - 2; i++) {
					a[i] = text.get(i);
				}
				a[text.getComponentsSize() - 2] = new StringComponent(s, c.color, c.bold, c.italic, c.underline);
				text = new FormattedString(a);
			} else {
				return FormattedString.EMPTY;
			}
		}
		return text;
	}
}
