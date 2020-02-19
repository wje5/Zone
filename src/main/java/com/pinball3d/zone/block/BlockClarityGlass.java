package com.pinball3d.zone.block;

import com.pinball3d.zone.TabZone;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockClarityGlass extends Block {
	public BlockClarityGlass() {
		super(Material.GLASS);
		setHardness(0.3F);
		setRegistryName("zone:clarity_glass");
		setUnlocalizedName("clarity_glass");
		setCreativeTab(TabZone.tab);
	}
}
