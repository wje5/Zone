package com.pinball3d.zone.tileentity;

import com.pinball3d.zone.block.BlockStoragePanel;
import com.pinball3d.zone.sphinx.IStorable;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class TEStoragePanel extends TENeedNetwork implements IStorable {
	public TEStoragePanel() {

	}

	@Override
	public IItemHandler getStorage() {
		EnumFacing facing = world.getBlockState(pos).getValue(BlockStoragePanel.FACING);
		BlockPos newpos = pos.add(facing.getOpposite().getFrontOffsetX(), facing.getOpposite().getFrontOffsetY(),
				facing.getOpposite().getFrontOffsetZ());
		TileEntity te = world.getTileEntity(newpos);
		if (te != null) {
			IItemHandler handler = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing);
			if (handler != null) {
				return handler;
			}
		}
		return new ItemStackHandler(0);
	}
}
