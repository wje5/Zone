package com.pinball3d.zone.sphinx.subscreen;

import com.pinball3d.zone.gui.IHasSubscreen;
import com.pinball3d.zone.gui.Subscreen;
import com.pinball3d.zone.gui.component.Container;
import com.pinball3d.zone.gui.component.ItemShow;
import com.pinball3d.zone.gui.component.ScrollingContainer;
import com.pinball3d.zone.gui.component.Text;
import com.pinball3d.zone.sphinx.crafting.CraftingIngredentItem;
import com.pinball3d.zone.sphinx.crafting.OreDictionaryData;
import com.pinball3d.zone.util.Util;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;

public class SubscreenManageOreDictionaryPriority extends Subscreen {
	private ScrollingContainer list;
	private OreDictionaryData data;

	public SubscreenManageOreDictionaryPriority(IHasSubscreen parent, OreDictionaryData data) {
		this(parent, data, getDisplayWidth() / 2 - 150, getDisplayHeight() / 2 - 100);
	}

	public SubscreenManageOreDictionaryPriority(IHasSubscreen parent, OreDictionaryData data, int x, int y) {
		super(parent, x, y, 300, 200, true);
		this.data = data;
		addComponent(list = new ScrollingContainer(this, 16, 24, 268, 170));
		for (CraftingIngredentItem item : data.getItems()) {
			genBar(item);
		}
	}

	private Container genBar(CraftingIngredentItem item) {
		Container c = new Container(list, 0, 0, 268, 25);
		c.addComponent(new ItemShow(c, 4, 4, item.createStack(), item.getCount()));
		c.addComponent(new Text(c, 146, 9, data.getName()));
		list.addComponent(c);
		return c;
	}

	@Override
	public void doRenderBackground(int mouseX, int mouseY) {
		super.doRenderBackground(mouseX, mouseY);
		Util.drawTexture(UI_BORDER, -5, -5, 0, 0, 99, 99, 0.5F);
		Util.drawTexture(UI_BORDER, 255, -5, 99, 0, 99, 99, 0.5F);
		Util.drawTexture(UI_BORDER, -5, 155, 0, 99, 99, 99, 0.5F);
		Util.drawTexture(UI_BORDER, 255, 155, 99, 99, 99, 99, 0.5F);
		Gui.drawRect(44, 0, 255, 44, 0x2F000000);
		Gui.drawRect(0, 44, 300, 155, 0x2F000000);
		Gui.drawRect(44, 155, 255, 200, 0x2F000000);
		Util.renderGlowHorizonLine(10, 20, 280);
		Util.renderGlowString(I18n.format("sphinx.manage_ore_dictionary"), 15, 8);
		Util.renderGlowBorder(15, 23, 270, 172);
	}

	@Override
	public boolean isBlockOtherSubscreen() {
		return true;
	}
}
