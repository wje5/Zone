package com.pinball3d.zone.gui;

import com.pinball3d.zone.Zone;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class GuiElementLoader implements IGuiHandler {
	public static final int DRAINER = 1;
	public static final int GRINDER = 2;

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
		}
		return null;
	}
}
