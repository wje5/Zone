package com.pinball3d.zone.block;

import com.pinball3d.zone.TabZone;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockReinforcedStone extends Block {
	public BlockReinforcedStone() {
		super(Material.ROCK);
		setHardness(35F);
		setResistance(100.0F);
		setRegistryName("zone:reinforced_stone");
		setUnlocalizedName("reinforced_stone");
		setCreativeTab(TabZone.tab);
	}
}
