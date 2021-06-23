package com.pinball3d.zone.core;

import java.util.Arrays;

import com.google.common.eventbus.EventBus;

import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;

public class ModContainerZone extends DummyModContainer {
	public ModContainerZone() {
		super(new ModMetadata());
		ModMetadata meta = getMetadata();
		meta.modId = "zonecore";
		meta.name = "Zone Core";
		meta.version = "1.0.0";
		meta.authorList = Arrays.asList("Zone Studio");
		meta.description = "A coremod for Zone.";
	}

	@Override
	public boolean registerBus(EventBus bus, LoadController controller) {
		bus.register(this);
		return true;
	}
}
