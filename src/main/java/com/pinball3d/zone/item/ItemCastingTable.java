package com.pinball3d.zone.item;

import com.pinball3d.zone.TabZone;
import com.pinball3d.zone.block.BlockLoader;
import com.pinball3d.zone.render.TEISRCastingTable;

import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCastingTable extends ItemBlock {
	public ItemCastingTable() {
		super(BlockLoader.casting_table);
		setRegistryName("zone:casting_table");
		setUnlocalizedName("casting_table");
		setCreativeTab(TabZone.tab);
		if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			setTEISR();
		}
	}

	@SideOnly(Side.CLIENT)
	public void setTEISR() {
		setTileEntityItemStackRenderer(new TEISRCastingTable());
	}
}
