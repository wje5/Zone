package com.pinball3d.zone.tileentity;

import com.pinball3d.zone.block.BlockStoragePanel;
import com.pinball3d.zone.sphinx.IProduction;
import com.pinball3d.zone.sphinx.StorageWrapper;
import com.pinball3d.zone.sphinx.WorldPos;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class TEProductionPanel extends TENeedNetwork implements IProduction {
	public TEProductionPanel() {

	}

	@Override
	public void work() {
		EnumFacing facing = world.getBlockState(pos).getValue(BlockStoragePanel.FACING).getOpposite();
		BlockPos newpos = pos.add(facing.getFrontOffsetX(), facing.getFrontOffsetY(), facing.getFrontOffsetZ());
		TileEntity te = world.getTileEntity(newpos);
		if (te != null) {
			IItemHandler handler = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN);
			((TEProcessingCenter) worldpos.getTileEntity()).dispenceItems(new StorageWrapper(handler, false),
					new WorldPos(this));
			markDirty();
		}
		super.work();
	}
}
