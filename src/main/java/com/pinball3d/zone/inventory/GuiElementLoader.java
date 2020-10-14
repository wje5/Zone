package com.pinball3d.zone.inventory;

import com.pinball3d.zone.Zone;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class GuiElementLoader implements IGuiHandler {
	public static final int DRAINER = 1;
	public static final int GRINDER = 2;
	public static final int ELEC_FURNACE = 3;
	public static final int ALLOY_SMELTER = 4;
	public static final int CENTRIFUGE = 5;
	public static final int CRYSTALLIZER = 6;
	public static final int TRANSMISSION_MODULE = 7;
	public static final int IO_PANEL = 8;
	public static final int BURNING_BOX = 9;
	public static final int BOILER = 10;
	public static final int LATHE = 11;
	public static final int FORMING_PRESS = 12;
	public static final int PUMP = 13;

	public GuiElementLoader() {
		NetworkRegistry.INSTANCE.registerGuiHandler(Zone.instance, this);
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		switch (ID) {
		case DRAINER:
			return new ContainerDrainer(player, world.getTileEntity(new BlockPos(x, y, z)));
		case GRINDER:
			return new ContainerGrinder(player, world.getTileEntity(new BlockPos(x, y, z)));
		case ELEC_FURNACE:
			return new ContainerElecFurnace(player, world.getTileEntity(new BlockPos(x, y, z)));
		case ALLOY_SMELTER:
			return new ContainerAlloySmelter(player, world.getTileEntity(new BlockPos(x, y, z)));
		case CENTRIFUGE:
			return new ContainerCentrifuge(player, world.getTileEntity(new BlockPos(x, y, z)));
		case CRYSTALLIZER:
			return new ContainerCrystallizer(player, world.getTileEntity(new BlockPos(x, y, z)));
		case TRANSMISSION_MODULE:
			return new ContainerTransmissionModule(player, world.getTileEntity(new BlockPos(x, y, z)));
		case IO_PANEL:
			return new ContainerIOPanel(player, world.getTileEntity(new BlockPos(x, y, z)));
		case BURNING_BOX:
			return new ContainerBurningBox(player, world.getTileEntity(new BlockPos(x, y, z)));
		case BOILER:
			return new ContainerBoiler(player, world.getTileEntity(new BlockPos(x, y, z)));
		case LATHE:
			return new ContainerLathe(player, world.getTileEntity(new BlockPos(x, y, z)));
		case FORMING_PRESS:
			return new ContainerFormingPress(player, world.getTileEntity(new BlockPos(x, y, z)));
		case PUMP:
			return new ContainerPump(player, world.getTileEntity(new BlockPos(x, y, z)));
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		switch (ID) {
		case DRAINER:
			return new GuiContainerDrainer(new ContainerDrainer(player, world.getTileEntity(new BlockPos(x, y, z))));
		case GRINDER:
			return new GuiContainerGrinder(new ContainerGrinder(player, world.getTileEntity(new BlockPos(x, y, z))));
		case ELEC_FURNACE:
			return new GuiContainerElecFurnace(
					new ContainerElecFurnace(player, world.getTileEntity(new BlockPos(x, y, z))));
		case ALLOY_SMELTER:
			return new GuiContainerAlloySmelter(
					new ContainerAlloySmelter(player, world.getTileEntity(new BlockPos(x, y, z))));
		case CENTRIFUGE:
			return new GuiContainerCentrifuge(
					new ContainerCentrifuge(player, world.getTileEntity(new BlockPos(x, y, z))));
		case CRYSTALLIZER:
			return new GuiContainerCrystallizer(
					new ContainerCrystallizer(player, world.getTileEntity(new BlockPos(x, y, z))));
		case TRANSMISSION_MODULE:
			return new GuiContainerTransmissionModule(
					new ContainerTransmissionModule(player, world.getTileEntity(new BlockPos(x, y, z))));
		case IO_PANEL:
			return new GuiContainerIOPanel(new ContainerIOPanel(player, world.getTileEntity(new BlockPos(x, y, z))));
		case BURNING_BOX:
			return new GuiContainerBurningBox(
					new ContainerBurningBox(player, world.getTileEntity(new BlockPos(x, y, z))));
		case BOILER:
			return new GuiContainerBoiler(new ContainerBoiler(player, world.getTileEntity(new BlockPos(x, y, z))));
		case LATHE:
			return new GuiContainerLathe(new ContainerLathe(player, world.getTileEntity(new BlockPos(x, y, z))));
		case FORMING_PRESS:
			return new GuiContainerFormingPress(
					new ContainerFormingPress(player, world.getTileEntity(new BlockPos(x, y, z))));
		case PUMP:
			return new GuiContainerPump(new ContainerPump(player, world.getTileEntity(new BlockPos(x, y, z))));
		}
		return null;
	}
}
