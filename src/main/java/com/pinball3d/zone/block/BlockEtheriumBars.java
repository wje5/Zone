package com.pinball3d.zone.block;

import com.pinball3d.zone.TabZone;

import net.minecraft.block.BlockPane;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;

public class BlockEtheriumBars extends BlockPane {
	public BlockEtheriumBars() {
		super(Material.IRON, true);
		setHardness(200.0F);
		setResistance(5000.0F);
		setRegistryName("zone:etherium_bars");
		setUnlocalizedName("etherium_bars");
		setCreativeTab(TabZone.tab);
	}

	@Override
	public EnumPushReaction getMobilityFlag(IBlockState state) {
		return EnumPushReaction.BLOCK;
	}
}
