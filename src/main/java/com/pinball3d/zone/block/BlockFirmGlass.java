package com.pinball3d.zone.block;

import com.pinball3d.zone.TabZone;

import net.minecraft.block.BlockGlass;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.BlockRenderLayer;

public class BlockFirmGlass extends BlockGlass {
	public BlockFirmGlass() {
		super(Material.GLASS, false);
		setHardness(20.0F);
		setResistance(500.0F);
		setSoundType(SoundType.GLASS);
		setRegistryName("zone:firm_glass");
		setUnlocalizedName("firm_glass");
		setCreativeTab(TabZone.tab);
	}

	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}
}
