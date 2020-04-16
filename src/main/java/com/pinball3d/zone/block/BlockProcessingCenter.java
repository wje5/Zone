package com.pinball3d.zone.block;

import com.pinball3d.zone.TabZone;
import com.pinball3d.zone.tileentity.TEProcessingCenter;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class BlockProcessingCenter extends BlockContainer {
	public BlockProcessingCenter() {
		super(Material.IRON);
		setHardness(100.0F);
		setResistance(2500.0F);
		setLightLevel(1.0F);
		setRegistryName("zone:processing_center");
		setUnlocalizedName("processing_center");
		setCreativeTab(TabZone.tab);
	}

	@Override
	public void onBlockDestroyedByExplosion(World world, BlockPos pos, Explosion explosionIn) {
		Explosion explosion = world.newExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 4.0F, true, true);
		super.onBlockDestroyedByExplosion(world, pos, explosionIn);
	}

	public boolean isFullStructure(World world, BlockPos pos) {
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				if (world.getBlockState(pos.add(i, -2, j)).getBlock() != BlockLoader.construct_block_all) {
					return false;
				}
			}
		}
		if (world.getBlockState(pos.add(-2, -2, -2)).getBlock() != BlockLoader.construct_block_all
				|| world.getBlockState(pos.add(2, -2, -2)).getBlock() != BlockLoader.construct_block_all
				|| world.getBlockState(pos.add(-2, -2, 2)).getBlock() != BlockLoader.construct_block_all
				|| world.getBlockState(pos.add(2, -2, 2)).getBlock() != BlockLoader.construct_block_all
				|| world.getBlockState(pos.add(-2, 2, -2)).getBlock() != BlockLoader.construct_block_all
				|| world.getBlockState(pos.add(2, 2, -2)).getBlock() != BlockLoader.construct_block_all
				|| world.getBlockState(pos.add(-2, 2, 2)).getBlock() != BlockLoader.construct_block_all
				|| world.getBlockState(pos.add(2, 2, 2)).getBlock() != BlockLoader.construct_block_all) {
			return false;
		}
		for (int i = -1; i <= 1; i++) {
			IBlockState state = world.getBlockState(pos.add(-2, -2, i));
			if (state.getBlock() != BlockLoader.construct_block
					|| state.getValue(BlockRotatedPillar.AXIS) != EnumFacing.Axis.Z) {
				return false;
			}
			state = world.getBlockState(pos.add(2, -2, i));
			if (state.getBlock() != BlockLoader.construct_block
					|| state.getValue(BlockRotatedPillar.AXIS) != EnumFacing.Axis.Z) {
				return false;
			}
			state = world.getBlockState(pos.add(i, -2, -2));
			if (state.getBlock() != BlockLoader.construct_block
					|| state.getValue(BlockRotatedPillar.AXIS) != EnumFacing.Axis.X) {
				return false;
			}
			state = world.getBlockState(pos.add(i, -2, 2));
			if (state.getBlock() != BlockLoader.construct_block
					|| state.getValue(BlockRotatedPillar.AXIS) != EnumFacing.Axis.X) {
				return false;
			}
			state = world.getBlockState(pos.add(-2, 2, i));
			if (state.getBlock() != BlockLoader.construct_block
					|| state.getValue(BlockRotatedPillar.AXIS) != EnumFacing.Axis.Z) {
				return false;
			}
			state = world.getBlockState(pos.add(2, 2, i));
			if (state.getBlock() != BlockLoader.construct_block
					|| state.getValue(BlockRotatedPillar.AXIS) != EnumFacing.Axis.Z) {
				return false;
			}
			state = world.getBlockState(pos.add(i, 2, -2));
			if (state.getBlock() != BlockLoader.construct_block
					|| state.getValue(BlockRotatedPillar.AXIS) != EnumFacing.Axis.X) {
				return false;
			}
			state = world.getBlockState(pos.add(i, 2, 2));
			if (state.getBlock() != BlockLoader.construct_block
					|| state.getValue(BlockRotatedPillar.AXIS) != EnumFacing.Axis.X) {
				return false;
			}
		}
		for (int i = -1; i <= 1; i++) {
			IBlockState state = world.getBlockState(pos.add(-2, i, -2));
			if (state.getBlock() != BlockLoader.construct_block
					|| state.getValue(BlockRotatedPillar.AXIS) != EnumFacing.Axis.Y) {
				return false;
			}
			state = world.getBlockState(pos.add(2, i, -2));
			if (state.getBlock() != BlockLoader.construct_block
					|| state.getValue(BlockRotatedPillar.AXIS) != EnumFacing.Axis.Y) {
				return false;
			}
			state = world.getBlockState(pos.add(-2, i, 2));
			if (state.getBlock() != BlockLoader.construct_block
					|| state.getValue(BlockRotatedPillar.AXIS) != EnumFacing.Axis.Y) {
				return false;
			}
			state = world.getBlockState(pos.add(2, i, 2));
			if (state.getBlock() != BlockLoader.construct_block
					|| state.getValue(BlockRotatedPillar.AXIS) != EnumFacing.Axis.Y) {
				return false;
			}
		}
		if (world.getBlockState(pos.add(-1, -1, -1)).getBlock() != BlockLoader.cluster_operation_module
				|| world.getBlockState(pos.add(1, -1, -1)).getBlock() != BlockLoader.cluster_operation_module
				|| world.getBlockState(pos.add(-1, -1, 1)).getBlock() != BlockLoader.cluster_operation_module
				|| world.getBlockState(pos.add(1, -1, 1)).getBlock() != BlockLoader.cluster_operation_module
				|| world.getBlockState(pos.add(-1, 1, -1)).getBlock() != BlockLoader.cluster_operation_module
				|| world.getBlockState(pos.add(1, 1, -1)).getBlock() != BlockLoader.cluster_operation_module
				|| world.getBlockState(pos.add(-1, 1, 1)).getBlock() != BlockLoader.cluster_operation_module
				|| world.getBlockState(pos.add(1, 1, 1)).getBlock() != BlockLoader.cluster_operation_module) {
			return false;
		}
		IBlockState state = world.getBlockState(pos.add(0, -1, -1));
		if (state.getBlock() != BlockLoader.truss || state.getValue(BlockRotatedPillar.AXIS) != EnumFacing.Axis.X) {
			return false;
		}
		state = world.getBlockState(pos.add(0, -1, 1));
		if (state.getBlock() != BlockLoader.truss || state.getValue(BlockRotatedPillar.AXIS) != EnumFacing.Axis.X) {
			return false;
		}
		state = world.getBlockState(pos.add(-1, -1, 0));
		if (state.getBlock() != BlockLoader.truss || state.getValue(BlockRotatedPillar.AXIS) != EnumFacing.Axis.Z) {
			return false;
		}
		state = world.getBlockState(pos.add(1, -1, 0));
		if (state.getBlock() != BlockLoader.truss || state.getValue(BlockRotatedPillar.AXIS) != EnumFacing.Axis.Z) {
			return false;
		}
		state = world.getBlockState(pos.add(0, 1, -1));
		if (state.getBlock() != BlockLoader.truss || state.getValue(BlockRotatedPillar.AXIS) != EnumFacing.Axis.X) {
			return false;
		}
		state = world.getBlockState(pos.add(0, 1, 1));
		if (state.getBlock() != BlockLoader.truss || state.getValue(BlockRotatedPillar.AXIS) != EnumFacing.Axis.X) {
			return false;
		}
		state = world.getBlockState(pos.add(-1, 1, 0));
		if (state.getBlock() != BlockLoader.truss || state.getValue(BlockRotatedPillar.AXIS) != EnumFacing.Axis.Z) {
			return false;
		}
		state = world.getBlockState(pos.add(1, 1, 0));
		if (state.getBlock() != BlockLoader.truss || state.getValue(BlockRotatedPillar.AXIS) != EnumFacing.Axis.Z) {
			return false;
		}
		state = world.getBlockState(pos.add(-1, 0, -1));
		if (state.getBlock() != BlockLoader.truss || state.getValue(BlockRotatedPillar.AXIS) != EnumFacing.Axis.Y) {
			return false;
		}
		state = world.getBlockState(pos.add(-1, 0, 1));
		if (state.getBlock() != BlockLoader.truss || state.getValue(BlockRotatedPillar.AXIS) != EnumFacing.Axis.Y) {
			return false;
		}
		state = world.getBlockState(pos.add(1, 0, -1));
		if (state.getBlock() != BlockLoader.truss || state.getValue(BlockRotatedPillar.AXIS) != EnumFacing.Axis.Y) {
			return false;
		}
		state = world.getBlockState(pos.add(1, 0, 1));
		if (state.getBlock() != BlockLoader.truss || state.getValue(BlockRotatedPillar.AXIS) != EnumFacing.Axis.Y) {
			return false;
		}
		if (world.getBlockState(pos.add(-1, 0, 0)).getBlock() != Blocks.AIR
				|| world.getBlockState(pos.add(1, 0, 0)).getBlock() != Blocks.AIR
				|| world.getBlockState(pos.add(0, 0, -1)).getBlock() != Blocks.AIR
				|| world.getBlockState(pos.add(0, 0, 1)).getBlock() != Blocks.AIR
				|| world.getBlockState(pos.add(0, -1, 0)).getBlock() != Blocks.AIR
				|| world.getBlockState(pos.add(0, 1, 0)).getBlock() != Blocks.AIR) {
			return false;
		}
		Block block = BlockLoader.charged_glass;
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				if (world.getBlockState(pos.add(-2, i, j)).getBlock() != block
						|| world.getBlockState(pos.add(2, i, j)).getBlock() != block
						|| world.getBlockState(pos.add(i, j, -2)).getBlock() != block
						|| world.getBlockState(pos.add(i, j, 2)).getBlock() != block
						|| world.getBlockState(pos.add(i, 2, j)).getBlock() != block) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TEProcessingCenter();
	}
}
