package com.pinball3d.zone.block;

import com.pinball3d.zone.TabZone;

import net.minecraft.block.BlockGlass;
import net.minecraft.block.material.Material;
import net.minecraft.util.BlockRenderLayer;

public class BlockReinforcedGlass extends BlockGlass {
	public BlockReinforcedGlass() {
		super(Material.GLASS, false);
		setHardness(20.0F);
		setResistance(500.0F);
		setRegistryName("zone:reinforced_glass");
		setUnlocalizedName("reinforced_glass");
		setCreativeTab(TabZone.tab);
	}

	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}
}
