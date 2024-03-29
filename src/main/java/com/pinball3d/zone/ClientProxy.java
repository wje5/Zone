package com.pinball3d.zone;

import com.pinball3d.zone.render.RenderLoader;
import com.pinball3d.zone.sphinx.elite.EliteConfigHandler;
import com.pinball3d.zone.sphinx.elite.EliteRenderHelper;
import com.pinball3d.zone.sphinx.elite.panels.PanelRegistry;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		new RenderLoader();
		EliteRenderHelper.init();
		EliteConfigHandler.onPreinit(event);
		PanelRegistry.registerAll();
	}

	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
	}
}
