package com.pinball3d.zone.manual;

import com.pinball3d.zone.Zone;
import com.pinball3d.zone.inventory.GuiElementLoader;

public class GuiContainerManualDrainerAndGrinder extends GuiContainerManualBase {
	public GuiContainerManualDrainerAndGrinder(ContainerManual container) {
		super(container);
	}

	@Override
	public void addComponents() {
		super.addComponents();
		int x = width / 2 - 146;
		int y = height / 2 - 90;
//		components.add(new BlockShow(this, x + 40, y + 30, 4F, new ItemStack(BlockLoader.drainer)));
//		components.add(new BlockShow(this, x + 186, y + 30, 4F, new ItemStack(BlockLoader.grinder)));
	}

	@Override
	public void onFlip(boolean flag) {
		mc.player.openGui(Zone.instance, GuiElementLoader.MANUAL, mc.world, 0, 0, flag ? 3 : 5);
	}

	@Override
	public void drawContents(int mouseX, int mouseY) {
		int x = width / 2 - 146;
		int x2 = width / 2;
		int y = height / 2 - 90;
		drawTextBlock("manual.drainer", x + 18, y + 16);
		drawTextBlock("manual.grinder", x2 + 18, y + 16);
	}
}
