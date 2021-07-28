package com.pinball3d.zone.core;

import java.util.Map;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

public class LoadingPluginZone implements IFMLLoadingPlugin {
	public static boolean runtimeDeobf;

	@Override
	public String[] getASMTransformerClass() {
		return new String[] { "com.pinball3d.zone.core.ClassTransformerZone" };
	}

	@Override
	public String getModContainerClass() {
		return "com.pinball3d.zone.core.ModContainerZone";
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
		runtimeDeobf = ((Boolean) data.get("runtimeDeobfuscationEnabled")).booleanValue();
	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}
}
