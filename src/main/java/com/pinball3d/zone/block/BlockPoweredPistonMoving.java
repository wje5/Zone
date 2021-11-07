package com.pinball3d.zone.block;

import com.pinball3d.zone.tileentity.TEPoweredPiston;

import net.minecraft.block.BlockPistonMoving;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public class BlockPoweredPistonMoving extends BlockPistonMoving {
	public BlockPoweredPistonMoving() {
		setHardness(200.0F);
		setResistance(5000.0F);
		setRegistryName("zone:powered_piston_moving");
		setUnlocalizedName("powered_piston");
	}

	public static TileEntity createTilePiston(IBlockState blockStateIn, EnumFacing facingIn, boolean extendingIn,
			boolean shouldHeadBeRenderedIn) {
		return new TEPoweredPiston(blockStateIn, facingIn, extendingIn, shouldHeadBeRenderedIn);
	}
}
