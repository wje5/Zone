package com.pinball3d.zone.tileentity;

import com.pinball3d.zone.block.BlockStoragePanel;
import com.pinball3d.zone.sphinx.IProduction;
import com.pinball3d.zone.util.StorageWrapper;
import com.pinball3d.zone.util.WorldPos;

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
		EnumFacing facing = world.getBlockState(pos).getValue(BlockStoragePanel.FACING);
		BlockPos newpos = pos.add(facing.getOpposite().getFrontOffsetX(), facing.getOpposite().getFrontOffsetY(),
				facing.getOpposite().getFrontOffsetZ());
		TileEntity te = world.getTileEntity(newpos);
		if (te != null) {
			IItemHandler handler = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing);
			if (handler != null) {
				StorageWrapper wrapper = new StorageWrapper(handler, false);
				if (!wrapper.isEmpty()) {
					TEProcessingCenter pc = (TEProcessingCenter) worldpos.getTileEntity();
					pc.insertToItemHandler(pc.dispenceItems(wrapper, new WorldPos(this)), handler);
					markDirty();
				}
			}
		}
		super.work();
	}
}
