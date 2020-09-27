package com.pinball3d.zone.item;

import com.pinball3d.zone.TabZone;
import com.pinball3d.zone.block.BlockLoader;
import com.pinball3d.zone.render.TEISRCrucible;

import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCrucible extends ItemBlock {
	public ItemCrucible() {
		super(BlockLoader.crucible);
		setRegistryName("zone:crucible");
		setUnlocalizedName("crucible");
		setCreativeTab(TabZone.tab);
		if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			setTEISR();
		}
	}

	@SideOnly(Side.CLIENT)
	public void setTEISR() {
		setTileEntityItemStackRenderer(new TEISRCrucible());
	}
}
