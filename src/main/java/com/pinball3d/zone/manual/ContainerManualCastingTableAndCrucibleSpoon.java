package com.pinball3d.zone.manual;

import com.pinball3d.zone.item.ItemLoader;
import com.pinball3d.zone.manual.component.SlotItemFrame;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ContainerManualCastingTableAndCrucibleSpoon extends ContainerManual {
	public ContainerManualCastingTableAndCrucibleSpoon(EntityPlayer player) {
		super(player);
		addSlotToContainer(new SlotItemFrame(165, 30, new ItemStack(ItemLoader.crucible_spoon)));
		addSlotToContainer(new SlotItemFrame(183, 30, new ItemStack(ItemLoader.crucible_spoon_filled)));
	}
}
