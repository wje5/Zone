package com.pinball3d.zone.util;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.resources.I18n;

public class VanillaTranslateHandler {
	private static Map<String, String> map = new HashMap<String, String>();
	private static boolean init;

	public static void init() {
		if (init) {
			return;
		}
		map.put("true", "elite.util.true");
		map.put("false", "elite.util.false");
		init = true;
	}

	public static String getTranslated(String text) {
		if (!init) {
			init();
		}
		String s = map.get(text);
		return s == null ? text : I18n.format(s);
	}
}
