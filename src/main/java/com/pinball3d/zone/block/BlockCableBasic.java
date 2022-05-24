package com.pinball3d.zone.block;

import java.util.List;

import com.google.common.collect.Lists;
import com.pinball3d.zone.TabZone;
import com.pinball3d.zone.tileentity.TECableBasic;
import com.pinball3d.zone.tileentity.ZoneTieredMachine.Tier;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.Properties.PropertyAdapter;

public class BlockCableBasic extends BlockContainer {
	public static final PropertyBool DOWN = PropertyBool.create("down");
	public static final PropertyBool UP = PropertyBool.create("up");
	public static final PropertyBool NORTH = PropertyBool.create("north");
	public static final PropertyBool SOUTH = PropertyBool.create("south");
	public static final PropertyBool WEST = PropertyBool.create("west");
	public static final PropertyBool EAST = PropertyBool.create("east");

	public BlockCableBasic(Tier tier) {
		super(Material.IRON);
		setHardness(tier.getHardness());
		setResistance(tier.getHardness());
		setRegistryName("zone:cable_" + tier.getTier());
		setUnlocalizedName("cable_" + tier.getTier());
		setDefaultState(blockState.getBaseState().withProperty(DOWN, Boolean.valueOf(false))
				.withProperty(UP, Boolean.valueOf(false)).withProperty(NORTH, Boolean.valueOf(false))
				.withProperty(EAST, Boolean.valueOf(false)).withProperty(SOUTH, Boolean.valueOf(false))
				.withProperty(WEST, Boolean.valueOf(false)));
		setCreativeTab(TabZone.tab);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { DOWN, UP, NORTH, SOUTH, WEST, EAST });
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		TECableBasic te = (TECableBasic) world.getTileEntity(pos);
		return state.withProperty(DOWN, te.isConnect(EnumFacing.DOWN)).withProperty(UP, te.isConnect(EnumFacing.UP))
				.withProperty(NORTH, te.isConnect(EnumFacing.NORTH)).withProperty(SOUTH, te.isConnect(EnumFacing.SOUTH))
				.withProperty(WEST, te.isConnect(EnumFacing.WEST)).withProperty(EAST, te.isConnect(EnumFacing.EAST));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return 0;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState();
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullBlock(IBlockState state) {
		return false;
	}

	@Override
	public boolean isTopSolid(IBlockState state) {
		return false;
	}

	protected List<AxisAlignedBB> getCollisionBoxList(IBlockState state) {
		List<AxisAlignedBB> list = Lists.<AxisAlignedBB>newArrayList();
		list.add(new AxisAlignedBB(0.375D, 0.375D, 0.375D, 0.625D, 0.625D, 0.625D));
		if (state.getValue(DOWN)) {
			list.add(new AxisAlignedBB(0.375D, 0.0D, 0.375D, 0.625D, 0.375D, 0.625D));
		}
		if (state.getValue(UP)) {
			list.add(new AxisAlignedBB(0.375D, 0.625D, 0.375D, 0.625D, 1.0D, 0.625D));
		}
		if (state.getValue(NORTH)) {
			list.add(new AxisAlignedBB(0.375D, 0.375D, 0.0D, 0.625D, 0.625D, 0.375D));
		}
		if (state.getValue(SOUTH)) {
			list.add(new AxisAlignedBB(0.375D, 0.375D, 0.625D, 0.625D, 0.625D, 1.0D));
		}
		if (state.getValue(WEST)) {
			list.add(new AxisAlignedBB(0.0D, 0.375D, 0.375D, 0.375D, 0.625D, 0.625D));
		}
		if (state.getValue(EAST)) {
			list.add(new AxisAlignedBB(0.625D, 0.375D, 0.375D, 1.0D, 0.625D, 0.625D));
		}
		return list;
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox,
			List<AxisAlignedBB> collidingBoxes, Entity entityIn, boolean isActualState) {
		if (!isActualState) {
			state = getActualState(state, worldIn, pos);
		}
		for (AxisAlignedBB axisalignedbb : getCollisionBoxList(state)) {
			addCollisionBoxToList(pos, entityBox, collidingBoxes, axisalignedbb);
		}
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TECableBasic();
	}

	public static class UnlistedPropertyBool extends PropertyAdapter<Boolean> {
		public UnlistedPropertyBool(String name) {
			super(PropertyBool.create(name));
		}
	}
}
