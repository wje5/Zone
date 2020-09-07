package com.pinball3d.zone;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public class SoundUtil {
	private static final SoundEvent[] sounds = new SoundEvent[] { new SoundEvent(new ResourceLocation("zone:grinder")),
			new SoundEvent(new ResourceLocation("zone:drainer")),
			new SoundEvent(new ResourceLocation("zone:alloy_smelter")),
			new SoundEvent(new ResourceLocation("zone:elec_furnace")),
			new SoundEvent(new ResourceLocation("zone:centrifuge")),
			new SoundEvent(new ResourceLocation("zone:crystallizer")) };

	public static SoundEvent getSoundEventFromId(int id) {
		return sounds[id];
	}
}
