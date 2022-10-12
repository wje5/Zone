package com.pinball3d.zone.sphinx.elite.panels;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;
import com.pinball3d.zone.sphinx.elite.EliteMainwindow;
import com.pinball3d.zone.sphinx.elite.FormattedString;
import com.pinball3d.zone.sphinx.elite.PanelGroup;

public class PanelRegistry {
	private static Map<String, Factory> map = new HashMap<String, Factory>();

	public static void registerAll() {
		register("empty",
				(parent, parentGroup, data) -> new Panel(parent, parentGroup, "empty", new FormattedString("empty")));
		register("map", (parent, parentGroup, data) -> new PanelMap(parent, parentGroup));
		register("info", (parent, parentGroup, data) -> new PanelInfo(parent, parentGroup));
	}

	public static void register(String id, Factory factory) {
		map.put(id, factory);
	}

	public static Panel createPanel(EliteMainwindow parent, PanelGroup parentGroup, String id, JsonObject meta) {
		return map.get(id).apply(parent, parentGroup, meta);
	}

	@FunctionalInterface
	public static interface Factory {
		public Panel apply(EliteMainwindow parent, PanelGroup parentGroup, JsonObject data);
	}
}
