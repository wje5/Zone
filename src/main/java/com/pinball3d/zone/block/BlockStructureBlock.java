package com.pinball3d.zone.block;

import com.pinball3d.zone.TabZone;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockStructureBlock extends Block {
	public BlockStructureBlock() {
		super(Material.IRON);
		setHardness(100.0F);
		setResistance(2500.0F);
		setRegistryName("zone:structure_block");
		setUnlocalizedName("structure_block");
		setCreativeTab(TabZone.tab);
	}
}
