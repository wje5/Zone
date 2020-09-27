package com.pinball3d.zone.manual;

import com.pinball3d.zone.block.BlockLoader;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

public class ScreenManualElecFurnaceAndAlloySmelter extends ScreenManualBase {
	@Override
	protected void applyComponents() {
		super.applyComponents();
		components.add(
				new BlockShow(this, getXOffset() + 40, getYOffset() + 30, 4F, new ItemStack(BlockLoader.elec_furnace)));
		components.add(new BlockShow(this, getXOffset() + 186, getYOffset() + 30, 4F,
				new ItemStack(BlockLoader.alloy_smelter)));
	}

	@Override
	public void onFlip(boolean flag) {
		if (flag) {
			Minecraft.getMinecraft().displayGuiScreen(new ScreenManualDrainerAndGrinder());
		} else {
			Minecraft.getMinecraft().displayGuiScreen(new ScreenManualBoilerAndDrill());
		}
	}

	@Override
	public void drawContents(int mouseX, int mouseY) {
		int x = width / 2 - 146;
		int x2 = width / 2;
		int y = height / 2 - 90;
		drawTextBlock("manual.elec_furnace", x + 18, y + 16);
		drawTextBlock("manual.alloy_smelter", x2 + 18, y + 16);
	}
}
