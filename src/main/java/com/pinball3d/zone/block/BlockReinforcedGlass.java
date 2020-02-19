package com.pinball3d.zone.block;

import com.pinball3d.zone.TabZone;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockReinforcedGlass extends Block {
	public BlockReinforcedGlass() {
		super(Material.GLASS);
		setHardness(20.0F);
		setResistance(500.0F);
		setRegistryName("zone:reinforced_glass");
		setUnlocalizedName("reinforced_glass");
		setCreativeTab(TabZone.tab);
	}
}
