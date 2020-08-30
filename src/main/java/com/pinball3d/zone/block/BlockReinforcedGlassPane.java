package com.pinball3d.zone.block;

import com.pinball3d.zone.TabZone;

import net.minecraft.block.BlockPane;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.BlockRenderLayer;

public class BlockReinforcedGlassPane extends BlockPane {
	public BlockReinforcedGlassPane() {
		super(Material.GLASS, false);
		setHardness(50.0F);
		setResistance(1000.0F);
		setSoundType(SoundType.GLASS);
		setRegistryName("zone:reinforced_glass_pane");
		setUnlocalizedName("reinforced_glass_pane");
		setCreativeTab(TabZone.tab);
	}

	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}
}
