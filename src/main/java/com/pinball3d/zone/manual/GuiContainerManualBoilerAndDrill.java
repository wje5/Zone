package com.pinball3d.zone.manual;

import com.pinball3d.zone.Zone;
import com.pinball3d.zone.block.BlockLoader;
import com.pinball3d.zone.inventory.GuiElementLoader;
import com.pinball3d.zone.item.ItemLoader;
import com.pinball3d.zone.manual.component.BlockShow;
import com.pinball3d.zone.manual.component.ItemFrame;

import net.minecraft.item.ItemStack;

public class GuiContainerManualBoilerAndDrill extends GuiContainerManualBase {
	public GuiContainerManualBoilerAndDrill(ContainerManual container) {
		super(container);
	}

	@Override
	public void addComponents() {
		super.addComponents();
		int x = width / 2 - 146;
		int y = height / 2 - 90;
		components.add(new BlockShow(this, x + 40, y + 30, 4F, new ItemStack(BlockLoader.boiler)));
		components.add(new BlockShow(this, x + 186, y + 30, 4F, new ItemStack(ItemLoader.drill_empty)));
		components.add(new ItemFrame(this, x + 164, y + 85, new ItemStack(ItemLoader.drill_head)));
		components.add(new ItemFrame(this, x + 182, y + 85, new ItemStack(ItemLoader.drill_heavy_head)));
	}

	@Override
	public void onFlip(boolean flag) {
		mc.player.openGui(Zone.instance, GuiElementLoader.MANUAL, mc.world, 0, 0, flag ? 5 : 7);
	}

	@Override
	public void drawContents(int mouseX, int mouseY) {
		int x = width / 2 - 146;
		int x2 = width / 2;
		int y = height / 2 - 90;
		drawTextBlock("manual.boiler", x + 18, y + 16);
		drawTextBlock("manual.drill", x2 + 18, y + 16);
	}
}