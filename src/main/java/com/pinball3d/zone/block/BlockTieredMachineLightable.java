package com.pinball3d.zone.block;

import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

import com.pinball3d.zone.tileentity.ZoneTieredMachine.Tier;

import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

public class BlockTieredMachineLightable extends BlockTieredMachine {
	private Supplier<Block> blockSupplier, lightBlockSupplier;
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

	public BlockTieredMachineLightable(String name, int gui, Function<Tier, TileEntity> createTE, Tier tier,
			boolean isLight, Supplier<Block> blockSupplier, Supplier<Block> lightBlockSupplier) {
		super(name, gui, createTE, tier, true);
		this.blockSupplier = blockSupplier;
		this.lightBlockSupplier = lightBlockSupplier;
		setLightLevel(isLight ? 1 : 0);
		setRegistryName("zone:" + name + "_" + tier.getTier() + (isLight ? "_on" : ""));
	}

	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
		super.onBlockAdded(worldIn, pos, state);
		setDefaultFacing(worldIn, pos, state);
	}

	private void setDefaultFacing(World worldIn, BlockPos pos, IBlockState state) {
		if (!worldIn.isRemote) {
			IBlockState iblockstate = worldIn.getBlockState(pos.north());
			IBlockState iblockstate1 = worldIn.getBlockState(pos.south());
			IBlockState iblockstate2 = worldIn.getBlockState(pos.west());
			IBlockState iblockstate3 = worldIn.getBlockState(pos.east());
			EnumFacing enumfacing = state.getValue(FACING);
			if (enumfacing == EnumFacing.NORTH && iblockstate.isFullBlock() && !iblockstate1.isFullBlock()) {
				enumfacing = EnumFacing.SOUTH;
			} else if (enumfacing == EnumFacing.SOUTH && iblockstate1.isFullBlock() && !iblockstate.isFullBlock()) {
				enumfacing = EnumFacing.NORTH;
			} else if (enumfacing == EnumFacing.WEST && iblockstate2.isFullBlock() && !iblockstate3.isFullBlock()) {
				enumfacing = EnumFacing.EAST;
			} else if (enumfacing == EnumFacing.EAST && iblockstate3.isFullBlock() && !iblockstate2.isFullBlock()) {
				enumfacing = EnumFacing.WEST;
			}
			worldIn.setBlockState(pos, state.withProperty(FACING, enumfacing), 2);
		}
	}

	public static void setState(boolean active, World worldIn, BlockPos pos) {
		IBlockState iblockstate = worldIn.getBlockState(pos);
		BlockTieredMachineLightable block = (BlockTieredMachineLightable) iblockstate.getBlock();
		TileEntity tileentity = worldIn.getTileEntity(pos);
		keepInventory = true;
		if (active) {
			worldIn.setBlockState(pos,
					block.lightBlockSupplier.get().getDefaultState().withProperty(FACING, iblockstate.getValue(FACING)),
					Constants.BlockFlags.DEFAULT_AND_RERENDER);
			worldIn.setBlockState(pos,
					block.lightBlockSupplier.get().getDefaultState().withProperty(FACING, iblockstate.getValue(FACING)),
					Constants.BlockFlags.DEFAULT_AND_RERENDER);
		} else {
			worldIn.setBlockState(pos,
					block.blockSupplier.get().getDefaultState().withProperty(FACING, iblockstate.getValue(FACING)),
					Constants.BlockFlags.DEFAULT_AND_RERENDER);
			worldIn.setBlockState(pos,
					block.blockSupplier.get().getDefaultState().withProperty(FACING, iblockstate.getValue(FACING)),
					Constants.BlockFlags.DEFAULT_AND_RERENDER);
		}
		keepInventory = false;
		if (tileentity != null) {
			tileentity.validate();
			worldIn.setTileEntity(pos, tileentity);
		}
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		EnumFacing facing = EnumFacing.getHorizontal(meta % 4);
		return this.getDefaultState().withProperty(FACING, facing);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getHorizontalIndex();
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY,
			float hitZ, int meta, EntityLivingBase placer) {
		return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
	}

	@Override
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
		return new ItemStack(blockSupplier.get());
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Item.getItemFromBlock(blockSupplier.get());
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING);
	}
}
