package com.pinball3d.zone.block;

import java.util.Random;

import com.pinball3d.zone.TabZone;
import com.pinball3d.zone.tileentity.TEProcessingCenter;
import com.pinball3d.zone.util.WorldPos;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

public class BlockProcessingCenter extends BlockContainer {
	private static boolean flag;

	public BlockProcessingCenter(boolean on) {
		super(Material.IRON);
		setHardness(10000.0F);
		setResistance(250000.0F);
		setLightLevel(on ? 1F : 0F);
		setRegistryName("zone:processing_center" + (on ? "_light" : ""));
		setUnlocalizedName("processing_center");
		setCreativeTab(TabZone.tab);
	}

	@Override
	public void onBlockDestroyedByExplosion(World world, BlockPos pos, Explosion explosionIn) {
		world.newExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 20.0F, true, true);
		super.onBlockDestroyedByExplosion(world, pos, explosionIn);
	}

	public Result isFullStructure(WorldPos worldPos) {
		World world = worldPos.getWorld();
		BlockPos pos = worldPos.getPos();
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				if (world.getBlockState(pos.add(i, -2, j)).getBlock() != BlockLoader.construct_block_all) {
					return new Result(pos.add(i, -2, j), BlockLoader.construct_block_all,
							world.getBlockState(pos.add(i, -2, j)).getBlock() == BlockLoader.construct_block);
				}
			}
		}
		for (int i = -2; i <= 2; i += 4) {
			for (int j = -2; j <= 2; j += 4) {
				for (int k = -2; k <= 2; k += 4) {
					if (world.getBlockState(pos.add(i, j, k)).getBlock() != BlockLoader.construct_block_all) {
						return new Result(pos.add(i, j, k), BlockLoader.construct_block_all,
								world.getBlockState(pos.add(i, j, k)).getBlock() == BlockLoader.construct_block);
					}
				}
			}
		}
		for (int i = -1; i <= 1; i++) {
			IBlockState state = world.getBlockState(pos.add(-2, -2, i));
			if (state.getBlock() != BlockLoader.construct_block
					|| state.getValue(BlockRotatedPillar.AXIS) != EnumFacing.Axis.Z) {
				return new Result(pos.add(-2, -2, i), BlockLoader.construct_block,
						state.getBlock() == BlockLoader.construct_block_all
								|| state.getBlock() == BlockLoader.construct_block);
			}
			state = world.getBlockState(pos.add(2, -2, i));
			if (state.getBlock() != BlockLoader.construct_block
					|| state.getValue(BlockRotatedPillar.AXIS) != EnumFacing.Axis.Z) {
				return new Result(pos.add(2, -2, i), BlockLoader.construct_block,
						state.getBlock() == BlockLoader.construct_block_all
								|| state.getBlock() == BlockLoader.construct_block);
			}
			state = world.getBlockState(pos.add(i, -2, -2));
			if (state.getBlock() != BlockLoader.construct_block
					|| state.getValue(BlockRotatedPillar.AXIS) != EnumFacing.Axis.X) {
				return new Result(pos.add(i, -2, -2), BlockLoader.construct_block,
						state.getBlock() == BlockLoader.construct_block_all
								|| state.getBlock() == BlockLoader.construct_block);
			}
			state = world.getBlockState(pos.add(i, -2, 2));
			if (state.getBlock() != BlockLoader.construct_block
					|| state.getValue(BlockRotatedPillar.AXIS) != EnumFacing.Axis.X) {
				return new Result(pos.add(i, -2, 2), BlockLoader.construct_block,
						state.getBlock() == BlockLoader.construct_block_all
								|| state.getBlock() == BlockLoader.construct_block);
			}
			state = world.getBlockState(pos.add(-2, 2, i));
			if (state.getBlock() != BlockLoader.construct_block
					|| state.getValue(BlockRotatedPillar.AXIS) != EnumFacing.Axis.Z) {
				return new Result(pos.add(-2, 2, i), BlockLoader.construct_block,
						state.getBlock() == BlockLoader.construct_block_all
								|| state.getBlock() == BlockLoader.construct_block);
			}
			state = world.getBlockState(pos.add(2, 2, i));
			if (state.getBlock() != BlockLoader.construct_block
					|| state.getValue(BlockRotatedPillar.AXIS) != EnumFacing.Axis.Z) {
				return new Result(pos.add(2, -2, i), BlockLoader.construct_block,
						state.getBlock() == BlockLoader.construct_block_all
								|| state.getBlock() == BlockLoader.construct_block);
			}
			state = world.getBlockState(pos.add(i, 2, -2));
			if (state.getBlock() != BlockLoader.construct_block
					|| state.getValue(BlockRotatedPillar.AXIS) != EnumFacing.Axis.X) {
				return new Result(pos.add(i, 2, -2), BlockLoader.construct_block,
						state.getBlock() == BlockLoader.construct_block_all
								|| state.getBlock() == BlockLoader.construct_block);
			}
			state = world.getBlockState(pos.add(i, 2, 2));
			if (state.getBlock() != BlockLoader.construct_block
					|| state.getValue(BlockRotatedPillar.AXIS) != EnumFacing.Axis.X) {
				return new Result(pos.add(i, 2, 2), BlockLoader.construct_block,
						state.getBlock() == BlockLoader.construct_block_all
								|| state.getBlock() == BlockLoader.construct_block);
			}
		}
		for (int i = -1; i <= 1; i++) {
			IBlockState state = world.getBlockState(pos.add(-2, i, -2));
			if (state.getBlock() != BlockLoader.construct_block
					|| state.getValue(BlockRotatedPillar.AXIS) != EnumFacing.Axis.Y) {
				return new Result(pos.add(-2, i, -2), BlockLoader.construct_block,
						state.getBlock() == BlockLoader.construct_block_all
								|| state.getBlock() == BlockLoader.construct_block);
			}
			state = world.getBlockState(pos.add(2, i, -2));
			if (state.getBlock() != BlockLoader.construct_block
					|| state.getValue(BlockRotatedPillar.AXIS) != EnumFacing.Axis.Y) {
				return new Result(pos.add(2, i, -2), BlockLoader.construct_block,
						state.getBlock() == BlockLoader.construct_block_all
								|| state.getBlock() == BlockLoader.construct_block);
			}
			state = world.getBlockState(pos.add(-2, i, 2));
			if (state.getBlock() != BlockLoader.construct_block
					|| state.getValue(BlockRotatedPillar.AXIS) != EnumFacing.Axis.Y) {
				return new Result(pos.add(-2, i, 2), BlockLoader.construct_block,
						state.getBlock() == BlockLoader.construct_block_all
								|| state.getBlock() == BlockLoader.construct_block);
			}
			state = world.getBlockState(pos.add(2, i, 2));
			if (state.getBlock() != BlockLoader.construct_block
					|| state.getValue(BlockRotatedPillar.AXIS) != EnumFacing.Axis.Y) {
				return new Result(pos.add(2, i, 2), BlockLoader.construct_block,
						state.getBlock() == BlockLoader.construct_block_all
								|| state.getBlock() == BlockLoader.construct_block);
			}
		}
		for (int i = -1; i <= 1; i += 2) {
			for (int j = -1; j <= 1; j += 2) {
				for (int k = -1; k <= 1; k += 2) {
					if (world.getBlockState(pos.add(i, j, k)).getBlock() != BlockLoader.cluster_operation_module) {
						return new Result(pos.add(i, j, k), BlockLoader.cluster_operation_module, false);
					}
				}
			}
		}
		IBlockState state = world.getBlockState(pos.add(0, -1, -1));
		if (state.getBlock() != BlockLoader.truss || state.getValue(BlockRotatedPillar.AXIS) != EnumFacing.Axis.X) {
			return new Result(pos.add(0, -1, -1), BlockLoader.truss, state.getBlock() == BlockLoader.truss);
		}
		state = world.getBlockState(pos.add(0, -1, 1));
		if (state.getBlock() != BlockLoader.truss || state.getValue(BlockRotatedPillar.AXIS) != EnumFacing.Axis.X) {
			return new Result(pos.add(0, -1, 1), BlockLoader.truss, state.getBlock() == BlockLoader.truss);
		}
		state = world.getBlockState(pos.add(-1, -1, 0));
		if (state.getBlock() != BlockLoader.truss || state.getValue(BlockRotatedPillar.AXIS) != EnumFacing.Axis.Z) {
			return new Result(pos.add(-1, -1, 0), BlockLoader.truss, state.getBlock() == BlockLoader.truss);
		}
		state = world.getBlockState(pos.add(1, -1, 0));
		if (state.getBlock() != BlockLoader.truss || state.getValue(BlockRotatedPillar.AXIS) != EnumFacing.Axis.Z) {
			return new Result(pos.add(1, -1, 0), BlockLoader.truss, state.getBlock() == BlockLoader.truss);
		}
		state = world.getBlockState(pos.add(0, 1, -1));
		if (state.getBlock() != BlockLoader.truss || state.getValue(BlockRotatedPillar.AXIS) != EnumFacing.Axis.X) {
			return new Result(pos.add(0, 1, -1), BlockLoader.truss, state.getBlock() == BlockLoader.truss);
		}
		state = world.getBlockState(pos.add(0, 1, 1));
		if (state.getBlock() != BlockLoader.truss || state.getValue(BlockRotatedPillar.AXIS) != EnumFacing.Axis.X) {
			return new Result(pos.add(0, 1, 1), BlockLoader.truss, state.getBlock() == BlockLoader.truss);
		}
		state = world.getBlockState(pos.add(-1, 1, 0));
		if (state.getBlock() != BlockLoader.truss || state.getValue(BlockRotatedPillar.AXIS) != EnumFacing.Axis.Z) {
			return new Result(pos.add(-1, 1, 0), BlockLoader.truss, state.getBlock() == BlockLoader.truss);
		}
		state = world.getBlockState(pos.add(1, 1, 0));
		if (state.getBlock() != BlockLoader.truss || state.getValue(BlockRotatedPillar.AXIS) != EnumFacing.Axis.Z) {
			return new Result(pos.add(1, 1, 0), BlockLoader.truss, state.getBlock() == BlockLoader.truss);
		}
		state = world.getBlockState(pos.add(-1, 0, -1));
		if (state.getBlock() != BlockLoader.truss || state.getValue(BlockRotatedPillar.AXIS) != EnumFacing.Axis.Y) {
			return new Result(pos.add(-1, 0, -1), BlockLoader.truss, state.getBlock() == BlockLoader.truss);
		}
		state = world.getBlockState(pos.add(-1, 0, 1));
		if (state.getBlock() != BlockLoader.truss || state.getValue(BlockRotatedPillar.AXIS) != EnumFacing.Axis.Y) {
			return new Result(pos.add(-1, 0, 1), BlockLoader.truss, state.getBlock() == BlockLoader.truss);
		}
		state = world.getBlockState(pos.add(1, 0, -1));
		if (state.getBlock() != BlockLoader.truss || state.getValue(BlockRotatedPillar.AXIS) != EnumFacing.Axis.Y) {
			return new Result(pos.add(1, 0, -1), BlockLoader.truss, state.getBlock() == BlockLoader.truss);
		}
		state = world.getBlockState(pos.add(1, 0, 1));
		if (state.getBlock() != BlockLoader.truss || state.getValue(BlockRotatedPillar.AXIS) != EnumFacing.Axis.Y) {
			return new Result(pos.add(1, 0, 1), BlockLoader.truss, state.getBlock() == BlockLoader.truss);
		}
		if (world.getBlockState(pos.add(-1, 0, 0)).getBlock() != Blocks.AIR) {
			return new Result(pos.add(-1, 0, 0), Blocks.AIR, false);
		}
		if (world.getBlockState(pos.add(1, 0, 0)).getBlock() != Blocks.AIR) {
			return new Result(pos.add(1, 0, 0), Blocks.AIR, false);
		}
		if (world.getBlockState(pos.add(0, 0, -1)).getBlock() != Blocks.AIR) {
			return new Result(pos.add(0, 0, -1), Blocks.AIR, false);
		}
		if (world.getBlockState(pos.add(0, 0, 1)).getBlock() != Blocks.AIR) {
			return new Result(pos.add(0, 0, 1), Blocks.AIR, false);
		}
		if (world.getBlockState(pos.add(0, -1, 0)).getBlock() != Blocks.AIR) {
			return new Result(pos.add(0, -1, 0), Blocks.AIR, false);
		}
		if (world.getBlockState(pos.add(0, 1, 0)).getBlock() != Blocks.AIR) {
			return new Result(pos.add(0, 1, 0), Blocks.AIR, false);
		}
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				if (world.getBlockState(pos.add(-2, i, j)).getBlock() != BlockLoader.charged_glass) {
					return new Result(pos.add(-2, i, j), BlockLoader.charged_glass, false);
				}
				if (world.getBlockState(pos.add(2, i, j)).getBlock() != BlockLoader.charged_glass) {
					return new Result(pos.add(2, i, j), BlockLoader.charged_glass, false);
				}
				if (world.getBlockState(pos.add(i, j, -2)).getBlock() != BlockLoader.charged_glass) {
					return new Result(pos.add(i, j, -2), BlockLoader.charged_glass, false);
				}
				if (world.getBlockState(pos.add(i, j, 2)).getBlock() != BlockLoader.charged_glass) {
					return new Result(pos.add(i, j, 2), BlockLoader.charged_glass, false);
				}
				if (world.getBlockState(pos.add(i, 2, j)).getBlock() != BlockLoader.charged_glass) {
					return new Result(pos.add(i, 2, j), BlockLoader.charged_glass, false);
				}
			}
		}
		return null;
	}

	public static class Result {
		public BlockPos pos;
		public Block block;
		public boolean checkFacing;

		public Result(BlockPos pos, Block block, boolean checkFacing) {
			this.pos = pos;
			this.block = block;
			this.checkFacing = checkFacing;
		}
	}

	public static void setState(boolean active, World worldIn, BlockPos pos) {
		flag = true;
		TileEntity tileentity = worldIn.getTileEntity(pos);
		if (active) {
			worldIn.setBlockState(pos, BlockLoader.processing_center_light.getDefaultState(),
					Constants.BlockFlags.DEFAULT_AND_RERENDER);
			worldIn.setBlockState(pos, BlockLoader.processing_center_light.getDefaultState(),
					Constants.BlockFlags.DEFAULT_AND_RERENDER);
		} else {
			worldIn.setBlockState(pos, BlockLoader.processing_center.getDefaultState(),
					Constants.BlockFlags.DEFAULT_AND_RERENDER);
			worldIn.setBlockState(pos, BlockLoader.processing_center.getDefaultState(),
					Constants.BlockFlags.DEFAULT_AND_RERENDER);
		}
		if (tileentity != null) {
			tileentity.validate();
			worldIn.setTileEntity(pos, tileentity);
		}
		flag = false;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		if (!flag) {
			((TEProcessingCenter) worldIn.getTileEntity(pos)).unload();
		}
		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
		return new ItemStack(BlockLoader.processing_center);
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Item.getItemFromBlock(BlockLoader.processing_center);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TEProcessingCenter();
	}
}
