package com.pinball3d.zone.manual;

import com.pinball3d.zone.item.ItemLoader;
import com.pinball3d.zone.manual.component.SlotItemFrame;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ContainerManualToolAndMaterial extends ContainerManual {
	public ContainerManualToolAndMaterial(EntityPlayer player) {
		super(player);
		addSlotToContainer(new SlotItemFrame(19, 48, new ItemStack(ItemLoader.hammer)));
		addSlotToContainer(new SlotItemFrame(37, 48, new ItemStack(ItemLoader.spanner)));
		addSlotToContainer(new SlotItemFrame(55, 48, new ItemStack(ItemLoader.file)));
		addSlotToContainer(new SlotItemFrame(73, 48, new ItemStack(ItemLoader.saw)));
		addSlotToContainer(new SlotItemFrame(165, 39, new ItemStack(ItemLoader.rivet)));
		addSlotToContainer(new SlotItemFrame(183, 39, new ItemStack(ItemLoader.vacuum_tube)));
		addSlotToContainer(new SlotItemFrame(201, 39, new ItemStack(ItemLoader.chip)));
		addSlotToContainer(new SlotItemFrame(219, 39, new ItemStack(ItemLoader.circuit_board)));
		addSlotToContainer(new SlotItemFrame(237, 39, new ItemStack(ItemLoader.grind_head)));
	}
}
