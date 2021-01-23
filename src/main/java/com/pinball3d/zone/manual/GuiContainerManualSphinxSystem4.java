package com.pinball3d.zone.manual;

import com.pinball3d.zone.Zone;
import com.pinball3d.zone.inventory.GuiElementLoader;

import net.minecraft.client.resources.I18n;

public class GuiContainerManualSphinxSystem4 extends GuiContainerManualBase {
	public GuiContainerManualSphinxSystem4(ContainerManualSphinxSystem4 container) {
		super(container);
	}

	@Override
	public void addComponents() {
		super.addComponents();
		button2.setEnable(() -> !I18n.format("manual.sphinx_system_9").equals("null"));
	}

	@Override
	public void onFlip(boolean flag) {
		mc.player.openGui(Zone.instance, GuiElementLoader.MANUAL, mc.world, 0, 0, flag ? 11 : 13);
	}

	@Override
	public void drawContents(int mouseX, int mouseY) {
		int x = width / 2 - 146;
		int x2 = width / 2;
		int y = height / 2 - 90;
		drawTextBlock("manual.sphinx_system_7", x + 18, y + 16);
		drawTextBlock("manual.sphinx_system_8", x2 + 18, y + 16);
	}
}
