package com.pinball3d.zone.manual;

import com.pinball3d.zone.block.BlockLoader;
import com.pinball3d.zone.item.ItemLoader;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

public class ScreenManualBoilerAndDrill extends ScreenManualBase {
	@Override
	protected void applyComponents() {
		super.applyComponents();
		components
				.add(new BlockShow(this, getXOffset() + 40, getYOffset() + 30, 4F, new ItemStack(BlockLoader.boiler)));
		components.add(
				new BlockShow(this, getXOffset() + 186, getYOffset() + 30, 4F, new ItemStack(ItemLoader.drill_empty)));
		components
				.add(new ItemFrame(this, getXOffset() + 164, getYOffset() + 85, new ItemStack(ItemLoader.drill_head)));
		components.add(
				new ItemFrame(this, getXOffset() + 182, getYOffset() + 85, new ItemStack(ItemLoader.drill_heavy_head)));
	}

	@Override
	public void onFlip(boolean flag) {
		if (flag) {
			Minecraft.getMinecraft().displayGuiScreen(new ScreenManualElecFurnaceAndAlloySmelter());
		} else {
			Minecraft.getMinecraft().displayGuiScreen(new ScreenManualCentrifugeAndCrystallizer());
		}
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
