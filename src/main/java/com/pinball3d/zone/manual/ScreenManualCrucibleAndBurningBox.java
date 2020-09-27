package com.pinball3d.zone.manual;

import com.pinball3d.zone.block.BlockLoader;
import com.pinball3d.zone.item.ItemLoader;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

public class ScreenManualCrucibleAndBurningBox extends ScreenManualBase {
	@Override
	protected void applyComponents() {
		super.applyComponents();
		components
				.add(new BlockShow(this, getXOffset() + 40, getYOffset() + 30, 4F, new ItemStack(ItemLoader.crucible)));
		components.add(
				new BlockShow(this, getXOffset() + 186, getYOffset() + 30, 4F, new ItemStack(BlockLoader.burning_box)));
	}

	@Override
	public void onFlip(boolean flag) {
		if (flag) {
			Minecraft.getMinecraft().displayGuiScreen(new ScreenManualToolAndMaterial());
		} else {
			Minecraft.getMinecraft().displayGuiScreen(new ScreenManualCastingTableAndCrucibleSpoon());
		}
	}

	@Override
	public void drawContents(int mouseX, int mouseY) {
		int x = width / 2 - 146;
		int x2 = width / 2;
		int y = height / 2 - 90;
		drawTextBlock("manual.crucible", x + 18, y + 16);
		drawTextBlock("manual.burning_box", x2 + 18, y + 16);
	}
}
