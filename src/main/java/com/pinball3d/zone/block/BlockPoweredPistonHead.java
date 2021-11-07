package com.pinball3d.zone.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockPoweredPistonHead extends BlockPistonExtension {
	public BlockPoweredPistonHead() {
		setHardness(200.0F);
		setResistance(5000.0F);
		setRegistryName("zone:powered_piston_head");
		setUnlocalizedName("powered_piston");
	}

	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
		if (player.capabilities.isCreativeMode) {
			BlockPos blockpos = pos.offset(state.getValue(FACING).getOpposite());
			Block block = worldIn.getBlockState(blockpos).getBlock();

			if (block == BlockLoader.powered_piston || block == BlockLoader.powered_piston_sticky) {
				worldIn.setBlockToAir(blockpos);
			}
		}
	}

	/**
	 * Called serverside after this block is replaced with another in Chunk, but
	 * before the Tile Entity is updated
	 */
	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		if (hasTileEntity(state)) {
			worldIn.removeTileEntity(pos);
		}
		EnumFacing enumfacing = state.getValue(FACING).getOpposite();
		pos = pos.offset(enumfacing);
		IBlockState iblockstate = worldIn.getBlockState(pos);

		if ((iblockstate.getBlock() == BlockLoader.powered_piston
				|| iblockstate.getBlock() == BlockLoader.powered_piston_sticky)
				&& iblockstate.getValue(BlockPistonBase.EXTENDED).booleanValue()) {
			iblockstate.getBlock().dropBlockAsItem(worldIn, pos, iblockstate, 0);
			worldIn.setBlockToAir(pos);
		}
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		EnumFacing enumfacing = state.getValue(FACING);
		BlockPos blockpos = pos.offset(enumfacing.getOpposite());
		IBlockState iblockstate = worldIn.getBlockState(blockpos);

		if (iblockstate.getBlock() != BlockLoader.powered_piston
				&& iblockstate.getBlock() != BlockLoader.powered_piston_sticky) {
			worldIn.setBlockToAir(pos);
		} else {
			iblockstate.neighborChanged(worldIn, blockpos, blockIn, fromPos);
		}
	}
}
