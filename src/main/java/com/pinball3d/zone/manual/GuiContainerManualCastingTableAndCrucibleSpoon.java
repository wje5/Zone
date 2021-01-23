package com.pinball3d.zone.manual;

import com.pinball3d.zone.Zone;
import com.pinball3d.zone.inventory.GuiElementLoader;
import com.pinball3d.zone.item.ItemLoader;
import com.pinball3d.zone.manual.component.BlockShow;

import net.minecraft.item.ItemStack;

public class GuiContainerManualCastingTableAndCrucibleSpoon extends GuiContainerManualBase {
	public GuiContainerManualCastingTableAndCrucibleSpoon(ContainerManualCastingTableAndCrucibleSpoon container) {
		super(container);
	}

	@Override
	public void addComponents() {
		super.addComponents();
		int x = width / 2 - 146;
		int y = height / 2 - 90;
		components.add(new BlockShow(this, x + 40, y + 30, 4F, new ItemStack(ItemLoader.casting_table)));
	}

	@Override
	public void onFlip(boolean flag) {
		mc.player.openGui(Zone.instance, GuiElementLoader.MANUAL, mc.world, 0, 0, flag ? 2 : 4);
	}

	@Override
	public void drawContents(int mouseX, int mouseY) {
		int x = width / 2 - 146;
		int x2 = width / 2;
		int y = height / 2 - 90;
		drawTextBlock("manual.casting_table", x + 18, y + 16);
		drawTextBlock("manual.crucible_spoon", x2 + 18, y + 16);
		drawFrame(x + 164, y + 29);
		drawFrame(x + 182, y + 29);
	}
}
