package com.pinball3d.zone.sphinx.elite;

public class Color {
	public static final Color WHITE = new Color(0xFFFFFFFF);
	public static final Color BLACK = new Color(0xFF000000);
	public static final Color RED = new Color(0xFFFF0000);
	public static final Color GREEN = new Color(0xFF00FF00);
	public static final Color BLUE = new Color(0xFF0000FF);

	public static final Color COMP_BG_LIGHT = new Color(0xFF535353);
	public static final Color COMP_BG_CHOSEN = new Color(0xFF4F4F4F);
	public static final Color COMP_BG_MEDIUM_DARK = new Color(0xFF4A4A4A);
	public static final Color COMP_BG_DARK = new Color(0xFF424242);
	public static final Color COMP_ORNAMENT = new Color(0xFFA8A8A8);

	public static final Color BACKGROUND = new Color(0xFF282828);

	public static final Color CHECKBOX_BORDER = new Color(0xFF858585);
	public static final Color CHECKBOX_FILL = new Color(0xFFD4D4D4);

	public static final Color DIVIDER_BG = new Color(0xFF383838);
	public static final Color DIVIDER_COMPONENT = new Color(0xFF3E3E3E);

	public static final Color SUBCOMP_BG_TEXT = new Color(0xFF454545);
	public static final Color SUBCOMP_BG_CHECKBOX = new Color(0xFF454545);

	public static final Color TEXT_LIGHT = new Color(0xFFF0F0F0);
	public static final Color TEXT_MIDIUM = new Color(0xFFD6D6D6);
	public static final Color TEXT_DARK = new Color(0xFFA0A0A0);
	public static final Color TEXT_GRAY = new Color(0xFF878787);

	public static final Color DOTTED_LINE = new Color(0xFFD9BEA7);
	public static final Color WINDOW_BORDER = new Color(0xFF1883D7);
	public static final Color WINDOW_BG = new Color(0xFF515658);

	public static final Color CHOSEN_TEXT_BG = new Color(0xFF0078D7);
	public static final Color CHOSEN_TEXT_CURSOR = new Color(0xFFFF8728);

	public static final Color FF9F9F9F = new Color(0xFF9F9F9F);
	public static final Color FFEDEDED = new Color(0xFFEDEDED);
	public static final Color FF0078D7 = new Color(0xFF0078D7);
	public static final Color FFC7C7C7 = new Color(0xFFC7C7C7);
	public static final Color FF6F6F6F = new Color(0xFF6F6F6F);

	public static final Color ICON = new Color(0xFFDDDDDD);

	public final int a, r, g, b;

	public Color(int a, int r, int g, int b) {
		this.a = a;
		this.r = r;
		this.g = g;
		this.b = b;
	}

	public Color(int color) {
		this(color >> 24 & 255, color >> 16 & 255, color >> 8 & 255, color & 255);
	}

	public int toInt() {
		return (a << 24 & 0xFF000000) | (r << 16 & 0xFF0000) | (g << 8 & 0xFF00) | (b & 0xFF);
	}
}
