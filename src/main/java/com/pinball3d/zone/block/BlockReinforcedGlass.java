package com.pinball3d.zone.block;

import com.pinball3d.zone.TabZone;

import net.minecraft.block.BlockGlass;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;

public class BlockReinforcedGlass extends BlockGlass {
	public BlockReinforcedGlass() {
		super(Material.GLASS, false);
		setHardness(50.0F);
		setResistance(1000.0F);
		setSoundType(SoundType.GLASS);
		setRegistryName("zone:reinforced_glass");
		setUnlocalizedName("reinforced_glass");
		setCreativeTab(TabZone.tab);
	}

	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public EnumPushReaction getMobilityFlag(IBlockState state) {
		return EnumPushReaction.BLOCK;
	}
}
