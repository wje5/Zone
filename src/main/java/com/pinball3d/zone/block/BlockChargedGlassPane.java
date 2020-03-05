package com.pinball3d.zone.block;

import com.pinball3d.zone.TabZone;

import net.minecraft.block.BlockPane;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.BlockRenderLayer;

public class BlockChargedGlassPane extends BlockPane {
	public BlockChargedGlassPane() {
		super(Material.GLASS, false);
		setHardness(0.3F);
		setSoundType(SoundType.GLASS);
		setRegistryName("zone:charged_glass_pane");
		setUnlocalizedName("charged_glass_pane");
		setCreativeTab(TabZone.tab);
	}

	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}
}
