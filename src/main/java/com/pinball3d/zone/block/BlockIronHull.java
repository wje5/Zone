package com.pinball3d.zone.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockIronHull extends Block {
	public BlockIronHull() {
		super(Material.IRON);
		setRegistryName("zone:iron_hull");
		setUnlocalizedName("iron_hull");
	}
}
