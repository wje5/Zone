package com.pinball3d.zone.manual;

import com.pinball3d.zone.item.ItemLoader;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

public class ScreenManualCastingTableAndCrucibleSpoon extends ScreenManualBase {
	@Override
	protected void applyComponents() {
		super.applyComponents();
		components.add(
				new BlockShow(this, getXOffset() + 40, getYOffset() + 30, 4F, new ItemStack(ItemLoader.casting_table)));
		components.add(
				new ItemFrame(this, getXOffset() + 164, getYOffset() + 29, new ItemStack(ItemLoader.crucible_spoon)));
		components.add(new ItemFrame(this, getXOffset() + 182, getYOffset() + 29,
				new ItemStack(ItemLoader.crucible_spoon_filled)));
	}

	@Override
	public void onFlip(boolean flag) {
		if (flag) {
			Minecraft.getMinecraft().displayGuiScreen(new ScreenManualCrucibleAndBurningBox());
		} else {
			Minecraft.getMinecraft().displayGuiScreen(new ScreenManualDrainerAndGrinder());
		}
	}

	@Override
	public void drawContents(int mouseX, int mouseY) {
		int x = width / 2 - 146;
		int x2 = width / 2;
		int y = height / 2 - 90;
		drawTextBlock("manual.casting_table", x + 18, y + 16);
		drawTextBlock("manual.crucible_spoon", x2 + 18, y + 16);
	}
}
