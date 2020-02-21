package com.pinball3d.zone.block;

import com.pinball3d.zone.TabZone;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockEtheriumHull extends Block {
	public BlockEtheriumHull() {
		super(Material.IRON);
		setHardness(100.0F);
		setResistance(2500.0F);
		setRegistryName("zone:etherium_hull");
		setUnlocalizedName("etherium_hull");
		setCreativeTab(TabZone.tab);
	}
}
