package com.pinball3d.zone.block;

import com.pinball3d.zone.TabZone;

import net.minecraft.block.BlockPane;
import net.minecraft.block.material.Material;

public class BlockEtheriumBars extends BlockPane {
	public BlockEtheriumBars() {
		super(Material.IRON, true);
		setHardness(100.0F);
		setResistance(2500.0F);
		setRegistryName("zone:etherium_bars");
		setUnlocalizedName("etherium_bars");
		setCreativeTab(TabZone.tab);
	}
}
