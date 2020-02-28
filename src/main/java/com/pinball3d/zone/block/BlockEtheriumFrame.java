package com.pinball3d.zone.block;

import com.pinball3d.zone.TabZone;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockEtheriumFrame extends Block {
	public BlockEtheriumFrame() {
		super(Material.IRON);
		setHardness(100.0F);
		setResistance(2500.0F);
		setRegistryName("zone:etherium_frame");
		setUnlocalizedName("etherium_frame");
		setCreativeTab(TabZone.tab);
	}
}
