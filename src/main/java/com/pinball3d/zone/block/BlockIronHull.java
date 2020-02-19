package com.pinball3d.zone.block;

import com.pinball3d.zone.TabZone;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockIronHull extends Block {
	public BlockIronHull() {
		super(Material.IRON);
		setHardness(5.0F);
		setResistance(10.0F);
		setRegistryName("zone:iron_hull");
		setUnlocalizedName("iron_hull");
		setCreativeTab(TabZone.tab);
	}
}
