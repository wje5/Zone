package com.pinball3d.zone.block;

import com.pinball3d.zone.TabZone;

import net.minecraft.block.BlockPane;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.BlockRenderLayer;

public class BlockFirmGlassPane extends BlockPane {
	public BlockFirmGlassPane() {
		super(Material.GLASS, false);
		setHardness(50.0F);
		setResistance(1000.0F);
		setSoundType(SoundType.GLASS);
		setRegistryName("zone:firm_glass_pane");
		setUnlocalizedName("firm_glass_pane");
		setCreativeTab(TabZone.tab);
	}

	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}
}
