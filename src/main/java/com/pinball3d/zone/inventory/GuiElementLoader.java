package com.pinball3d.zone.inventory;

import com.pinball3d.zone.Zone;
import com.pinball3d.zone.sphinx.ContainerSphinxSystem;
import com.pinball3d.zone.sphinx.GuiContainerSphinxSystem;

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
	public static final int SPHINX_SYSTEM = 7;

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
		case SPHINX_SYSTEM:
			return new ContainerSphinxSystem(player, world.getTileEntity(new BlockPos(x, y, z)));
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
		case SPHINX_SYSTEM:
			return new GuiContainerSphinxSystem(
					new ContainerSphinxSystem(player, world.getTileEntity(new BlockPos(x, y, z))));
		}
		return null;
	}
}
