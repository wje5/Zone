package com.pinball3d.zone.manual;

import com.pinball3d.zone.block.BlockLoader;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

public class ScreenManualDrainerAndGrinder extends ScreenManualBase {
	@Override
	protected void applyComponents() {
		super.applyComponents();
		components
				.add(new BlockShow(this, getXOffset() + 40, getYOffset() + 30, 4F, new ItemStack(BlockLoader.drainer)));
		components.add(
				new BlockShow(this, getXOffset() + 186, getYOffset() + 30, 4F, new ItemStack(BlockLoader.grinder)));
	}

	@Override
	public void onFlip(boolean flag) {
		if (flag) {
			Minecraft.getMinecraft().displayGuiScreen(new ScreenManualCastingTableAndCrucibleSpoon());
		} else {
			Minecraft.getMinecraft().displayGuiScreen(new ScreenManualElecFurnaceAndAlloySmelter());
		}
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
