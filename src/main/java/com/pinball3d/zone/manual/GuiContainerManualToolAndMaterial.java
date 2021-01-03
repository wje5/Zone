package com.pinball3d.zone.manual;

import com.pinball3d.zone.Zone;
import com.pinball3d.zone.inventory.GuiElementLoader;
import com.pinball3d.zone.item.ItemLoader;
import com.pinball3d.zone.manual.component.ItemFrame;

import net.minecraft.item.ItemStack;

public class GuiContainerManualToolAndMaterial extends GuiContainerManualBase {
	public GuiContainerManualToolAndMaterial(ContainerManual container) {
		super(container);
	}

	@Override
	public void addComponents() {
		super.addComponents();
		int x = width / 2 - 146;
		int y = height / 2 - 90;
		components.add(new ItemFrame(this, x + 18, y + 47, new ItemStack(ItemLoader.hammer)));
		components.add(new ItemFrame(this, x + 36, y + 47, new ItemStack(ItemLoader.spanner)));
		components.add(new ItemFrame(this, x + 54, y + 47, new ItemStack(ItemLoader.file)));
		components.add(new ItemFrame(this, x + 72, y + 47, new ItemStack(ItemLoader.saw)));
		components.add(new ItemFrame(this, x + 164, y + 38, new ItemStack(ItemLoader.rivet)));
		components.add(new ItemFrame(this, x + 182, y + 38, new ItemStack(ItemLoader.vacuum_tube)));
		components.add(new ItemFrame(this, x + 200, y + 38, new ItemStack(ItemLoader.chip)));
		components.add(new ItemFrame(this, x + 218, y + 38, new ItemStack(ItemLoader.circuit_board)));
		components.add(new ItemFrame(this, x + 236, y + 38, new ItemStack(ItemLoader.grind_head)));
	}

	@Override
	public void onFlip(boolean flag) {
		mc.player.openGui(Zone.instance, GuiElementLoader.MANUAL, mc.world, 0, 0, flag ? 0 : 2);
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
