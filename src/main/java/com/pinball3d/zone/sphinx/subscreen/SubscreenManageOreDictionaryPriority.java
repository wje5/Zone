package com.pinball3d.zone.sphinx.subscreen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.pinball3d.zone.gui.IHasSubscreen;
import com.pinball3d.zone.gui.Subscreen;
import com.pinball3d.zone.gui.component.Container;
import com.pinball3d.zone.gui.component.ItemShow;
import com.pinball3d.zone.gui.component.ScrollingContainerPriority;
import com.pinball3d.zone.gui.component.Text;
import com.pinball3d.zone.gui.component.TexturedButton;
import com.pinball3d.zone.network.ConnectHelperClient;
import com.pinball3d.zone.network.ConnectionHelper.Type;
import com.pinball3d.zone.network.MessageManageOreDictionaryPriority;
import com.pinball3d.zone.network.NetworkHandler;
import com.pinball3d.zone.sphinx.crafting.CraftingIngredentItem;
import com.pinball3d.zone.sphinx.crafting.OreDictionaryData;
import com.pinball3d.zone.util.ItemType;
import com.pinball3d.zone.util.Util;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;

public class SubscreenManageOreDictionaryPriority extends Subscreen {
	private ScrollingContainerPriority list;
	private int id;
	private OreDictionaryData data;
	private List<CraftingIngredentItem> l1;
	private List<CraftingIngredentItem> l2;

	public SubscreenManageOreDictionaryPriority(IHasSubscreen parent, OreDictionaryData data, int id) {
		this(parent, data, id, getDisplayWidth() / 2 - 150, getDisplayHeight() / 2 - 100);
	}

	public SubscreenManageOreDictionaryPriority(IHasSubscreen parent, OreDictionaryData data, int id, int x, int y) {
		super(parent, x, y, 326, 200, true);
		this.data = data;
		this.id = id;
		addComponent(list = new ScrollingContainerPriority(this, 16, 24, 268, 170));
		if (data != null) {
			l1 = new ArrayList<CraftingIngredentItem>(Arrays.asList(data.getItems()));
			l2 = new ArrayList<CraftingIngredentItem>(Arrays.asList(data.getDisableItems()));
			if (data.getName() == null) {
				addComponent(new TexturedButton(this, 300, 6, ICONS_5, 60, 120, 51, 51, 0.5F,
						() -> parent.putScreen(new SubscreenChooseItem(parent, this::newItem))));
			}
		}
	}

	public void newItem(ItemType item) {
		l1.add(new CraftingIngredentItem(item.createStack()));
	}

	@Override
	public Set<Type> getDataTypes() {
		Set<Type> s = super.getDataTypes();
		if (data == null) {
			s.add(Type.OREDICTIONARY);
		}
		return s;
	}

	private Container genBar(CraftingIngredentItem item) {
		Container c = createBar(item);
		list.addComponent(c);
		return c;
	}

	private Container createBar(CraftingIngredentItem item) {
		Container c = new Container(list, 0, 0, 268, 25);
		ItemStack stack = item.createStack();
		c.addComponent(new ItemShow(c, 4, 4, stack, item.getCount()));
		c.addComponent(new Text(c, 46, 3, stack.getDisplayName()));
		c.addComponent(new Text(c, 46, 15, Util.transferModIdToName(stack.getItem().getCreatorModId(stack))));
		c.addComponent(new TexturedButton(c, 243, 17, ICONS, 16, 172, 11, 15, 0.5F,
				() -> parent.putScreen(new SubscreenConfirmBox(parent, I18n.format("sphinx.delete_item"),
						Util.formatAndAntiEscape("sphinx.confirm_delete_item"), () -> {
							l1.remove(item);
							l2.remove(item);
						}))));
		c.setData(item);
		return c;
	}

	public void updateList() {
		list.removeComponents();
		if (l1 != null) {
			for (CraftingIngredentItem item : l1) {
				genBar(item);
			}
			Container c = new Container(list, 0, 0, 268, 2) {
				@Override
				public void doRender(int mouseX, int mouseY) {
					super.doRender(mouseX, mouseY);
					Gui.drawRect(0, 0, width, height, 0xFFFC3D3D);
				}
			};
			list.addComponent(c);
			for (CraftingIngredentItem item : l2) {
				genBar(item);
			}
		}
		list.checkScrollingDistance();
	}

	@Override
	public void doRenderBackground(int mouseX, int mouseY) {
		super.doRenderBackground(mouseX, mouseY);
		if (data == null && ConnectHelperClient.getInstance().hasData()) {
			data = ConnectHelperClient.getInstance().getOreDictionarys().get(id);
			if (data != null) {
				l1 = new ArrayList<CraftingIngredentItem>(Arrays.asList(data.getItems()));
				l2 = new ArrayList<CraftingIngredentItem>(Arrays.asList(data.getDisableItems()));
				if (data.getName() == null) {
					addComponent(new TexturedButton(this, 300, 6, ICONS_5, 60, 120, 51, 51, 0.5F,
							() -> parent.putScreen(new SubscreenChooseItem(parent, this::newItem))));
				}
			}
		}
		updateList();
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
	public boolean onQuit() {
		if (super.onQuit()) {
			if (data != null) {
				OreDictionaryData data = new OreDictionaryData(this.data.getName(),
						l1.toArray(new CraftingIngredentItem[] {}), l2.toArray(new CraftingIngredentItem[] {}));
				if (!data.equals(this.data)) {
					NetworkHandler.instance.sendToServer(MessageManageOreDictionaryPriority
							.newMessage(ConnectHelperClient.getInstance().getNetworkPos(), id, data));
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean isBlockOtherSubscreen() {
		return true;
	}
}
