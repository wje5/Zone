package com.pinball3d.zone;

import com.pinball3d.zone.item.ItemLoader;

import net.minecraftforge.oredict.OreDictionary;

public class OreDictionaryLoader {
	public static void init() {
		OreDictionary.registerOre("plateIron", ItemLoader.iron_plate);
	}
}
