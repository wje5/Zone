package com.pinball3d.zone.util;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.material.Material;
import net.minecraft.client.resources.I18n;

public class VanillaTranslateHandler {
	private static Map<String, String> map = new HashMap<String, String>();
	private static Map<Material, String> materialNames = new HashMap<Material, String>();
	private static boolean init;

	public static void init() {
		if (init) {
			return;
		}
		map.put("true", "elite.util.true");
		map.put("false", "elite.util.false");

		materialNames.put(Material.AIR, "elite.translate.material.air");
		materialNames.put(Material.GRASS, "elite.translate.material.grass");
		materialNames.put(Material.GROUND, "elite.translate.material.ground");
		materialNames.put(Material.WOOD, "elite.translate.material.wood");
		materialNames.put(Material.ROCK, "elite.translate.material.rock");
		materialNames.put(Material.IRON, "elite.translate.material.iron");
		materialNames.put(Material.ANVIL, "elite.translate.material.anvil");
		materialNames.put(Material.WATER, "elite.translate.material.water");
		materialNames.put(Material.LAVA, "elite.translate.material.lava");
		materialNames.put(Material.LEAVES, "elite.translate.material.leaves");
		materialNames.put(Material.PLANTS, "elite.translate.material.plants");
		materialNames.put(Material.VINE, "elite.translate.material.vine");
		materialNames.put(Material.SPONGE, "elite.translate.material.sponge");
		materialNames.put(Material.CLOTH, "elite.translate.material.cloth");
		materialNames.put(Material.FIRE, "elite.translate.material.fire");
		materialNames.put(Material.SAND, "elite.translate.material.sand");
		materialNames.put(Material.CIRCUITS, "elite.translate.material.circuits");
		materialNames.put(Material.CARPET, "elite.translate.material.carpet");
		materialNames.put(Material.GLASS, "elite.translate.material.glass");
		materialNames.put(Material.REDSTONE_LIGHT, "elite.translate.material.redstone_light");
		materialNames.put(Material.TNT, "elite.translate.material.tnt");
		materialNames.put(Material.CORAL, "elite.translate.material.coral");
		materialNames.put(Material.ICE, "elite.translate.material.ice");
		materialNames.put(Material.PACKED_ICE, "elite.translate.material.packed_ice");
		materialNames.put(Material.SNOW, "elite.translate.material.snow");
		materialNames.put(Material.CRAFTED_SNOW, "elite.translate.material.crafted_snow");
		materialNames.put(Material.CACTUS, "elite.translate.material.cactus");
		materialNames.put(Material.CLAY, "elite.translate.material.clay");
		materialNames.put(Material.GOURD, "elite.translate.material.gourd");
		materialNames.put(Material.DRAGON_EGG, "elite.translate.material.dragon_egg");
		materialNames.put(Material.PORTAL, "elite.translate.material.portal");
		materialNames.put(Material.CAKE, "elite.translate.material.cake");
		materialNames.put(Material.WEB, "elite.translate.material.web");
		init = true;
	}

	public static String getTranslated(String text) {
		if (!init) {
			init();
		}
		String key = map.get(text);
		return key == null ? text : I18n.format(key);
	}

	public static String getMaterialName(Material material) {
		String key = materialNames.get(material);
		return I18n.format(key == null ? "elite.translate.material.unknown" : key);
	}
}
