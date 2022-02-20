package com.pinball3d.zone.tileentity;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class TileEntityLoader {
	public static void init() {

		registerTileEntity(TEProcessingCenter.class, "ProcessingCenter");
		registerTileEntity(TENode.class, "Node");
		registerTileEntity(TETransmissionModule.class, "TransmissionModule");
		registerTileEntity(TEStorageChest.class, "StorageChest");
		registerTileEntity(TEBeaconCore.class, "BeaconCore");
		registerTileEntity(TEPoweredPiston.class, "PoweredPiston");
		registerTileEntity(TETerminal.class, "Terminal");
		registerTileEntity(TECableBasic.class, "CableBasic");
		registerTileEntity(TECableGeneral.class, "CableGeneral");

		registerTileEntity(TEDrainer.class, "Drainer");
		registerTileEntity(TEBoiler.class, "Boiler");
		registerTileEntity(TECapacitor.class, "Capacitor");
		registerTileEntity(TEAlloySmelter.class, "AlloySmelter");
		registerTileEntity(TECentrifuge.class, "Centrifuge");
		registerTileEntity(TECharger.class, "Charger");
		registerTileEntity(TECrystallizer.class, "Crystallizer");
		registerTileEntity(TEElecFurnace.class, "ElecFurnace");
		registerTileEntity(TEExtruder.class, "Extruder");
		registerTileEntity(TEFormingPress.class, "FormingPress");
		registerTileEntity(TEGrinder.class, "Grinder");
		registerTileEntity(TEPump.class, "TEPump");
		registerTileEntity(TEMiner.class, "TEMiner");
	}

	private static void registerTileEntity(Class<? extends TileEntity> tileEntityClass, String id) {
		GameRegistry.registerTileEntity(tileEntityClass, new ResourceLocation("zone", id));
	}
}
