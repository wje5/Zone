package com.pinball3d.zone.manual;

import com.pinball3d.zone.item.ItemLoader;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

public class ScreenManualToolAndMaterial extends ScreenManualBase {
	@Override
	protected void applyComponents() {
		super.applyComponents();
		components.add(new ItemFrame(this, getXOffset() + 18, getYOffset() + 47, new ItemStack(ItemLoader.hammer)));
		components.add(new ItemFrame(this, getXOffset() + 36, getYOffset() + 47, new ItemStack(ItemLoader.spanner)));
		components.add(new ItemFrame(this, getXOffset() + 54, getYOffset() + 47, new ItemStack(ItemLoader.file)));
		components.add(new ItemFrame(this, getXOffset() + 72, getYOffset() + 47, new ItemStack(ItemLoader.saw)));
		components.add(new ItemFrame(this, getXOffset() + 164, getYOffset() + 38, new ItemStack(ItemLoader.rivet)));
		components
				.add(new ItemFrame(this, getXOffset() + 182, getYOffset() + 38, new ItemStack(ItemLoader.vacuum_tube)));
		components.add(new ItemFrame(this, getXOffset() + 200, getYOffset() + 38, new ItemStack(ItemLoader.chip)));
		components.add(
				new ItemFrame(this, getXOffset() + 218, getYOffset() + 38, new ItemStack(ItemLoader.circuit_board)));
		components
				.add(new ItemFrame(this, getXOffset() + 236, getYOffset() + 38, new ItemStack(ItemLoader.grind_head)));
	}

	@Override
	public void onFlip(boolean flag) {
		if (flag) {
			Minecraft.getMinecraft().displayGuiScreen(new ScreenManualPrefaceAndMenu());
		} else {
			Minecraft.getMinecraft().displayGuiScreen(new ScreenManualCrucibleAndBurningBox());
		}
	}

	@Override
	public void drawContents(int mouseX, int mouseY) {
		int x = width / 2 - 146;
		int x2 = width / 2;
		int y = height / 2 - 90;
		drawTextBlock("manual.tool", x + 18, y + 16);
		drawTextBlock("manual.material", x2 + 18, y + 16);
	}
}
