package com.pinball3d.zone.manual;

import com.pinball3d.zone.item.ItemLoader;
import com.pinball3d.zone.manual.component.SlotItemFrame;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ContainerManualBoilerAndDrill extends ContainerManual {
	public ContainerManualBoilerAndDrill(EntityPlayer player) {
		super(player);
		addSlotToContainer(new SlotItemFrame(165, 86, new ItemStack(ItemLoader.drill_head)));
		addSlotToContainer(new SlotItemFrame(183, 86, new ItemStack(ItemLoader.drill_heavy_head)));
	}
}
