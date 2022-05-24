package com.pinball3d.zone.inventory;

import com.pinball3d.zone.Zone;
import com.pinball3d.zone.manual.ContainerManual;
import com.pinball3d.zone.manual.ContainerManualBoilerAndDrill;
import com.pinball3d.zone.manual.ContainerManualCastingTableAndCrucibleSpoon;
import com.pinball3d.zone.manual.ContainerManualSphinxSystem2;
import com.pinball3d.zone.manual.ContainerManualSphinxSystem3;
import com.pinball3d.zone.manual.ContainerManualSphinxSystem4;
import com.pinball3d.zone.manual.ContainerManualToolAndMaterial;
import com.pinball3d.zone.manual.GuiContainerManualBoilerAndDrill;
import com.pinball3d.zone.manual.GuiContainerManualCastingTableAndCrucibleSpoon;
import com.pinball3d.zone.manual.GuiContainerManualCentrifugeAndCrystallizer;
import com.pinball3d.zone.manual.GuiContainerManualCrucibleAndBurningBox;
import com.pinball3d.zone.manual.GuiContainerManualDrainerAndGrinder;
import com.pinball3d.zone.manual.GuiContainerManualElecFurnaceAndAlloySmelter;
import com.pinball3d.zone.manual.GuiContainerManualLatheAndFormingPress;
import com.pinball3d.zone.manual.GuiContainerManualPrefaceAndMenu;
import com.pinball3d.zone.manual.GuiContainerManualSphinxSystem;
import com.pinball3d.zone.manual.GuiContainerManualSphinxSystem2;
import com.pinball3d.zone.manual.GuiContainerManualSphinxSystem3;
import com.pinball3d.zone.manual.GuiContainerManualSphinxSystem4;
import com.pinball3d.zone.manual.GuiContainerManualSphinxSystem5;
import com.pinball3d.zone.manual.GuiContainerManualToolAndMaterial;
import com.pinball3d.zone.tileentity.TEAlloySmelter;
import com.pinball3d.zone.tileentity.TEBoiler;
import com.pinball3d.zone.tileentity.TECableGeneral;
import com.pinball3d.zone.tileentity.TECapacitor;
import com.pinball3d.zone.tileentity.TECentrifuge;
import com.pinball3d.zone.tileentity.TECharger;
import com.pinball3d.zone.tileentity.TECrystallizer;
import com.pinball3d.zone.tileentity.TEDrainer;
import com.pinball3d.zone.tileentity.TEDynavoltRestrainer;
import com.pinball3d.zone.tileentity.TEElecFurnace;
import com.pinball3d.zone.tileentity.TEExtruder;
import com.pinball3d.zone.tileentity.TEFormingPress;
import com.pinball3d.zone.tileentity.TEGrinder;
import com.pinball3d.zone.tileentity.TEMiner;
import com.pinball3d.zone.tileentity.TEPump;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class GuiElementLoader implements IGuiHandler {
	public static final int DRAINER = 1;
	public static final int BOILER = 2;
	public static final int CAPACITOR = 3;
	public static final int ALLOY_SMELTER = 4;
	public static final int CENTRIFUGE = 5;
	public static final int CHARGER = 6;
	public static final int CRYSTALLIZER = 7;
	public static final int ELEC_FURNACE = 8;
	public static final int EXTRUDER = 9;
	public static final int FORMING_PRESS = 10;
	public static final int GRINDER = 11;
	public static final int PUMP = 12;
	public static final int MINER = 13;
	public static final int MANUAL = 14;
	public static final int TRANSMISSION_MODULE = 15;
	public static final int CABLE_2_DOWN = 16;
	public static final int CABLE_2_UP = 17;
	public static final int CABLE_2_NORTH = 18;
	public static final int CABLE_2_SOUTH = 19;
	public static final int CABLE_2_WEST = 20;
	public static final int CABLE_2_EAST = 21;
	public static final int DYNAVOLT_RESTRAINER = 22;

	public GuiElementLoader() {
		NetworkRegistry.INSTANCE.registerGuiHandler(Zone.instance, this);
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		switch (ID) {
		case DRAINER:
			return new ContainerDrainer(player, (TEDrainer) world.getTileEntity(new BlockPos(x, y, z)));
		case BOILER:
			return new ContainerBoiler(player, (TEBoiler) world.getTileEntity(new BlockPos(x, y, z)));
		case CAPACITOR:
			return new ContainerCapacitor(player, (TECapacitor) world.getTileEntity(new BlockPos(x, y, z)));
		case ALLOY_SMELTER:
			return new ContainerAlloySmelter(player, (TEAlloySmelter) world.getTileEntity(new BlockPos(x, y, z)));
		case CENTRIFUGE:
			return new ContainerCentrifuge(player, (TECentrifuge) world.getTileEntity(new BlockPos(x, y, z)));
		case CHARGER:
			return new ContainerCharger(player, (TECharger) world.getTileEntity(new BlockPos(x, y, z)));
		case CRYSTALLIZER:
			return new ContainerCrystallizer(player, (TECrystallizer) world.getTileEntity(new BlockPos(x, y, z)));
		case ELEC_FURNACE:
			return new ContainerElecFurnace(player, (TEElecFurnace) world.getTileEntity(new BlockPos(x, y, z)));
		case EXTRUDER:
			return new ContainerExtruder(player, (TEExtruder) world.getTileEntity(new BlockPos(x, y, z)));
		case FORMING_PRESS:
			return new ContainerFormingPress(player, (TEFormingPress) world.getTileEntity(new BlockPos(x, y, z)));
		case GRINDER:
			return new ContainerGrinder(player, (TEGrinder) world.getTileEntity(new BlockPos(x, y, z)));
		case PUMP:
			return new ContainerPump(player, (TEPump) world.getTileEntity(new BlockPos(x, y, z)));
		case MINER:
			return new ContainerMiner(player, (TEMiner) world.getTileEntity(new BlockPos(x, y, z)));
		case MANUAL:
			switch (z) {
			case 1:
				return new ContainerManualToolAndMaterial(player);
			case 3:
				return new ContainerManualCastingTableAndCrucibleSpoon(player);
			case 6:
				return new ContainerManualBoilerAndDrill(player);
			case 10:
				return new ContainerManualSphinxSystem2(player);
			case 11:
				return new ContainerManualSphinxSystem3(player);
			case 12:
				return new ContainerManualSphinxSystem4(player);
			default:
				return new ContainerManual(player);
			}
		case TRANSMISSION_MODULE:
			return new ContainerTransmissionModule(player, world.getTileEntity(new BlockPos(x, y, z)));
		case CABLE_2_DOWN:
		case CABLE_2_UP:
		case CABLE_2_NORTH:
		case CABLE_2_SOUTH:
		case CABLE_2_WEST:
		case CABLE_2_EAST:
			return new ContainerCable2(player, (TECableGeneral) world.getTileEntity(new BlockPos(x, y, z)),
					EnumFacing.VALUES[ID - CABLE_2_DOWN]);
		case DYNAVOLT_RESTRAINER:
			return new ContainerDynavoltRestrainer(player,
					(TEDynavoltRestrainer) world.getTileEntity(new BlockPos(x, y, z)));
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		switch (ID) {
		case DRAINER:
			return new GuiContainerDrainer(
					new ContainerDrainer(player, (TEDrainer) world.getTileEntity(new BlockPos(x, y, z))));
		case BOILER:
			return new GuiContainerBoiler(
					new ContainerBoiler(player, (TEBoiler) world.getTileEntity(new BlockPos(x, y, z))));
		case CAPACITOR:
			return new GuiContainerCapacitor(
					new ContainerCapacitor(player, (TECapacitor) world.getTileEntity(new BlockPos(x, y, z))));
		case ALLOY_SMELTER:
			return new GuiContainerAlloySmelter(
					new ContainerAlloySmelter(player, (TEAlloySmelter) world.getTileEntity(new BlockPos(x, y, z))));
		case CENTRIFUGE:
			return new GuiContainerCentrifuge(
					new ContainerCentrifuge(player, (TECentrifuge) world.getTileEntity(new BlockPos(x, y, z))));
		case CHARGER:
			return new GuiContainerCharger(
					new ContainerCharger(player, (TECharger) world.getTileEntity(new BlockPos(x, y, z))));
		case CRYSTALLIZER:
			return new GuiContainerCrystallizer(
					new ContainerCrystallizer(player, (TECrystallizer) world.getTileEntity(new BlockPos(x, y, z))));
		case ELEC_FURNACE:
			return new GuiContainerElecFurnace(
					new ContainerElecFurnace(player, (TEElecFurnace) world.getTileEntity(new BlockPos(x, y, z))));

		case EXTRUDER:
			return new GuiContainerExtruder(
					new ContainerExtruder(player, (TEExtruder) world.getTileEntity(new BlockPos(x, y, z))));
		case FORMING_PRESS:
			return new GuiContainerFormingPress(
					new ContainerFormingPress(player, (TEFormingPress) world.getTileEntity(new BlockPos(x, y, z))));
		case GRINDER:
			return new GuiContainerGrinder(
					new ContainerGrinder(player, (TEGrinder) world.getTileEntity(new BlockPos(x, y, z))));
		case PUMP:
			return new GuiContainerPump(new ContainerPump(player, (TEPump) world.getTileEntity(new BlockPos(x, y, z))));
		case MINER:
			return new GuiContainerMiner(
					new ContainerMiner(player, (TEMiner) world.getTileEntity(new BlockPos(x, y, z))));
		case MANUAL:
			switch (z) {
			case 0:
				return new GuiContainerManualPrefaceAndMenu(new ContainerManual(player));
			case 1:
				return new GuiContainerManualToolAndMaterial(new ContainerManualToolAndMaterial(player));
			case 2:
				return new GuiContainerManualCrucibleAndBurningBox(new ContainerManual(player));
			case 3:
				return new GuiContainerManualCastingTableAndCrucibleSpoon(
						new ContainerManualCastingTableAndCrucibleSpoon(player));
			case 4:
				return new GuiContainerManualDrainerAndGrinder(new ContainerManual(player));
			case 5:
				return new GuiContainerManualElecFurnaceAndAlloySmelter(new ContainerManual(player));
			case 6:
				return new GuiContainerManualBoilerAndDrill(new ContainerManualBoilerAndDrill(player));
			case 7:
				return new GuiContainerManualCentrifugeAndCrystallizer(new ContainerManual(player));
			case 8:
				return new GuiContainerManualLatheAndFormingPress(new ContainerManual(player));
			case 9:
				return new GuiContainerManualSphinxSystem(new ContainerManual(player));
			case 10:
				return new GuiContainerManualSphinxSystem2(new ContainerManualSphinxSystem2(player));
			case 11:
				return new GuiContainerManualSphinxSystem3(new ContainerManualSphinxSystem3(player));
			case 12:
				return new GuiContainerManualSphinxSystem4(new ContainerManualSphinxSystem4(player));
			case 13:
				return new GuiContainerManualSphinxSystem5(new ContainerManual(player));
			}
		case TRANSMISSION_MODULE:
			return new GuiContainerTransmissionModule(
					new ContainerTransmissionModule(player, world.getTileEntity(new BlockPos(x, y, z))));
		case CABLE_2_DOWN:
		case CABLE_2_UP:
		case CABLE_2_NORTH:
		case CABLE_2_SOUTH:
		case CABLE_2_WEST:
		case CABLE_2_EAST:
			return new GuiContainerCable2(new ContainerCable2(player,
					(TECableGeneral) world.getTileEntity(new BlockPos(x, y, z)), EnumFacing.VALUES[ID - CABLE_2_DOWN]));
		case DYNAVOLT_RESTRAINER:
			return new GuiContainerDynavoltRestrainer(new ContainerDynavoltRestrainer(player,
					(TEDynavoltRestrainer) world.getTileEntity(new BlockPos(x, y, z))));
		}

		return null;
	}
}
