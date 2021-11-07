package com.pinball3d.zone.tileentity;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class TileEntityLoader {
	public static void init() {
		registerTileEntity(TEDrainer.class, "Drainer");
		registerTileEntity(TEGrinder.class, "Grinder");
		registerTileEntity(TEElecFurnace.class, "ElecFurnace");
		registerTileEntity(TEAlloySmelter.class, "AlloySmelter");
		registerTileEntity(TECentrifuge.class, "Centrifuge");
		registerTileEntity(TENode.class, "Node");
		registerTileEntity(TECrystallizer.class, "Crystallizer");
		registerTileEntity(TEProcessingCenter.class, "ProcessingCenter");
		registerTileEntity(TETransmissionModule.class, "TransmissionModule");
		registerTileEntity(TEStorageChest.class, "TEStorageChest");
		registerTileEntity(TEStoragePanel.class, "TEStoragePanel");
		registerTileEntity(TEIOPanel.class, "TEIOPanel");
		registerTileEntity(TEProductionPanel.class, "TEProductionPanel");
		registerTileEntity(TECrucible.class, "TECrucible");
		registerTileEntity(TEBurningBox.class, "TEBurningBox");
		registerTileEntity(TECastingTable.class, "TECastingTable");
		registerTileEntity(TEBoiler.class, "TEBoiler");
		registerTileEntity(TELathe.class, "TELathe");
		registerTileEntity(TEFormingPress.class, "TEFormingPress");
		registerTileEntity(TEPump.class, "TEPump");
		registerTileEntity(TEBeaconCore.class, "TEBeaconCore");
		registerTileEntity(TEPoweredPiston.class, "TEPoweredPiston");
	}

	private static void registerTileEntity(Class<? extends TileEntity> tileEntityClass, String id) {
		GameRegistry.registerTileEntity(tileEntityClass, new ResourceLocation("zone", id));
	}
}
