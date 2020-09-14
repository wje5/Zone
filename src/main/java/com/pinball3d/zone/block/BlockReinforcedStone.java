package com.pinball3d.zone.block;

import com.pinball3d.zone.TabZone;

import net.minecraft.block.Block;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;

public class BlockReinforcedStone extends Block {
	public BlockReinforcedStone() {
		super(Material.ROCK);
		setHardness(1000F);
		setResistance(30000.0F);
		setRegistryName("zone:reinforced_stone");
		setUnlocalizedName("reinforced_stone");
		setCreativeTab(TabZone.tab);
	}

	@Override
	public EnumPushReaction getMobilityFlag(IBlockState state) {
		return EnumPushReaction.BLOCK;
	}
}
