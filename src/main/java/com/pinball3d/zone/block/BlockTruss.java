package com.pinball3d.zone.block;

import com.pinball3d.zone.TabZone;

import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockTruss extends BlockRotatedPillar {
	public static final PropertyEnum<BlockLog.EnumAxis> LOG_AXIS = PropertyEnum.<BlockLog.EnumAxis>create("axis",
			BlockLog.EnumAxis.class);

	public BlockTruss() {
		super(Material.IRON);
		setHardness(300.0F);
		setResistance(7500.0F);
		setRegistryName("zone:truss");
		setUnlocalizedName("truss");
		setCreativeTab(TabZone.tab);
	}

	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot) {
		switch (rot) {
		case COUNTERCLOCKWISE_90:
		case CLOCKWISE_90:
			switch (state.getValue(LOG_AXIS)) {
			case X:
				return state.withProperty(LOG_AXIS, BlockLog.EnumAxis.Z);
			case Z:
				return state.withProperty(LOG_AXIS, BlockLog.EnumAxis.X);
			default:
				return state;
			}
		default:
			return state;
		}
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		switch (state.getValue(AXIS)) {
		case X:
			return new AxisAlignedBB(0F, 0.3125F, 0.3125F, 1F, 0.6875F, 0.6875F);
		case Y:
			return new AxisAlignedBB(0.3125F, 0F, 0.3125F, 0.6875F, 1F, 0.6875F);
		case Z:
		default:
			return new AxisAlignedBB(0.3125F, 0.3125F, 0F, 0.6875F, 0.6875F, 1F);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos,
			EnumFacing side) {
		return true;
	}
}
