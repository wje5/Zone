package com.pinball3d.zone.sphinx.subscreen;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pinball3d.zone.gui.IHasSubscreen;
import com.pinball3d.zone.gui.Subscreen;
import com.pinball3d.zone.gui.component.Button;
import com.pinball3d.zone.gui.component.Container;
import com.pinball3d.zone.gui.component.ItemShow;
import com.pinball3d.zone.gui.component.ScrollingContainer;
import com.pinball3d.zone.gui.component.ScrollingEdgeTextureList;
import com.pinball3d.zone.gui.component.Text;
import com.pinball3d.zone.gui.component.TextInputBox;
import com.pinball3d.zone.gui.component.TexturedButton;
import com.pinball3d.zone.network.ConnectHelperClient;
import com.pinball3d.zone.network.ConnectionHelper.Type;
import com.pinball3d.zone.sphinx.crafting.CraftingIngredentItem;
import com.pinball3d.zone.sphinx.crafting.OreDictionaryData;
import com.pinball3d.zone.util.Util;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;

public class SubscreenManageOreDictionary extends Subscreen {
	private ScrollingEdgeTextureList edgeList;
	private ScrollingContainer list;
	private TextInputBox box;

	public SubscreenManageOreDictionary(IHasSubscreen parent) {
		this(parent, getDisplayWidth() / 2 - 150, getDisplayHeight() / 2 - 100);
	}

	public SubscreenManageOreDictionary(IHasSubscreen parent, int x, int y) {
		super(parent, x, y, 320, 200, true);
		addComponent(edgeList = new ScrollingEdgeTextureList(this, 0, 9, 195));
		edgeList.addBar(ICONS, 9, 8, 32, 130, 19, 15, null);
		edgeList.addBar(ICONS, 10, 8, 51, 130, 20, 15, null);
		edgeList.setOnChange(e -> {
			list.setScrollingDistance(0);
			return true;
		});
		addComponent(list = new ScrollingContainer(this, 36, 44, 268, 150));
		addComponent(box = new TextInputBox(this, 40, 27, 61, 15, 55, () -> {
			box.isFocus = true;
		}).setIsPixel(true));
		addComponent(new TexturedButton(this, 100, 27, ICONS, 92, 41, 15, 15, 1.0F, () -> {
			box.isFocus = false;
		}));
	}

	@Override
	public Set<Type> getDataTypes() {
		Set<Type> s = super.getDataTypes();
		s.add(Type.OREDICTIONARY);
		return s;
	}

	private Container genBar(int id, OreDictionaryData data) {
		Container c = new Container(list, 0, 0, 268, 25);
		List<CraftingIngredentItem> allItems = new ArrayList<CraftingIngredentItem>();
		for (CraftingIngredentItem i : data.getItems()) {
			allItems.add(i);
		}
		for (CraftingIngredentItem i : data.getDisableItems()) {
			allItems.add(i);
		}
		if (!box.text.isEmpty()) {
			boolean flag = true;
			for (CraftingIngredentItem i : allItems) {
				if (Util.search(box.text, i.createStack())) {
					flag = false;
					break;
				}
			}
			if (flag) {
				return null;
			}
		}
		for (int i = 0; i < 7 && i < allItems.size(); i++) {
			c.addComponent(new ItemShow(c, 4 + i * 20, 4, allItems.get(i).createStack(), allItems.get(i).getCount()));
		}
		c.addComponent(new Text(c, 146, 9, data.getName()));
		c.addComponent(new Button(c, 0, 0, 268, 25,
				() -> parent.putScreen(new SubscreenManageOreDictionaryPriority(parent, data, id))));
		list.addComponent(c);
		return c;
	}

	public void updateList() {
		list.removeComponents();
		if (ConnectHelperClient.getInstance().hasData()) {
			Map<Integer, OreDictionaryData> map = ConnectHelperClient.getInstance().getOreDictionarys();
			map.forEach((k, v) -> {
				if (v.getName() == null == (edgeList.index == 0)) {
					genBar(k, v);
				}
			});
		}
	}

	@Override
	public void doRenderBackground(int mouseX, int mouseY) {
		super.doRenderBackground(mouseX, mouseY);
		updateList();
		Util.drawTexture(UI_BORDER, 15, -5, 0, 0, 99, 99, 0.5F);
		Util.drawTexture(UI_BORDER, 275, -5, 99, 0, 99, 99, 0.5F);
		Util.drawTexture(UI_BORDER, 15, 155, 0, 99, 99, 99, 0.5F);
		Util.drawTexture(UI_BORDER, 275, 155, 99, 99, 99, 99, 0.5F);
		Gui.drawRect(64, 0, 275, 44, 0x2F000000);
		Gui.drawRect(20, 44, 320, 155, 0x2F000000);
		Gui.drawRect(64, 155, 275, 200, 0x2F000000);
		Gui.drawRect(36, 24, 304, 44, 0x651CC3B5);
		Util.renderGlowHorizonLine(30, 20, 280);
		Util.renderGlowString(I18n.format("sphinx.manage_ore_dictionary"), 35, 8);
		Util.renderGlowBorder(35, 23, 270, 172);
	}

	@Override
	public boolean isBlockOtherSubscreen() {
		return true;
	}
}
