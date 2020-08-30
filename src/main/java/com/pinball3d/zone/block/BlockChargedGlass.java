package com.pinball3d.zone.block;

import com.pinball3d.zone.TabZone;

import net.minecraft.block.BlockGlass;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.BlockRenderLayer;

public class BlockChargedGlass extends BlockGlass {
	public BlockChargedGlass() {
		super(Material.GLASS, false);
		setHardness(35F);
		setResistance(750.0F);
		setSoundType(SoundType.GLASS);
		setRegistryName("zone:charged_glass");
		setUnlocalizedName("charged_glass");
		setCreativeTab(TabZone.tab);
	}

	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}
}
