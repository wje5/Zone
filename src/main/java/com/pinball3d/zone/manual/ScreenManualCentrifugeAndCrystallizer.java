package com.pinball3d.zone.manual;

import com.pinball3d.zone.block.BlockLoader;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

public class ScreenManualCentrifugeAndCrystallizer extends ScreenManualBase {
	@Override
	protected void applyComponents() {
		super.applyComponents();
		components.add(
				new BlockShow(this, getXOffset() + 40, getYOffset() + 30, 4F, new ItemStack(BlockLoader.centrifuge)));
		components.add(new BlockShow(this, getXOffset() + 186, getYOffset() + 30, 4F,
				new ItemStack(BlockLoader.crystallizer)));
	}

	@Override
	public void onFlip(boolean flag) {
		if (flag) {
			Minecraft.getMinecraft().displayGuiScreen(new ScreenManualBoilerAndDrill());
		} else {
			Minecraft.getMinecraft().displayGuiScreen(new ScreenManualLatheAndFormingPress());
		}
	}

	@Override
	public void drawContents(int mouseX, int mouseY) {
		int x = width / 2 - 146;
		int x2 = width / 2;
		int y = height / 2 - 90;
		drawTextBlock("manual.centrifuge", x + 18, y + 16);
		drawTextBlock("manual.crystallizer", x2 + 18, y + 16);
	}
}
