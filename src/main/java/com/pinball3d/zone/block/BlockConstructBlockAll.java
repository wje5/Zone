package com.pinball3d.zone.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockConstructBlockAll extends Block {
	public BlockConstructBlockAll() {
		super(Material.IRON);
		setHardness(100.0F);
		setResistance(2500.0F);
		setRegistryName("zone:construct_block_all");
		setUnlocalizedName("construct_block");
	}
}
