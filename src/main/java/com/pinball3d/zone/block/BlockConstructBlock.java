package com.pinball3d.zone.block;

import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.Rotation;

public class BlockConstructBlock extends BlockRotatedPillar {
	public static final PropertyEnum<BlockLog.EnumAxis> LOG_AXIS = PropertyEnum.<BlockLog.EnumAxis>create("axis",
			BlockLog.EnumAxis.class);

	public BlockConstructBlock() {
		super(Material.IRON);
		setHardness(100.0F);
		setResistance(2500.0F);
		setRegistryName("zone:construct_block");
		setUnlocalizedName("construct_block");
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
}
