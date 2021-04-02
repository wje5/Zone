package com.pinball3d.zone.sphinx.subscreen;

import java.util.Iterator;
import java.util.Set;

import org.lwjgl.input.Keyboard;

import com.pinball3d.zone.gui.IHasSubscreen;
import com.pinball3d.zone.gui.Subscreen;
import com.pinball3d.zone.gui.component.ScrollingEdgeList;
import com.pinball3d.zone.gui.component.ScrollingEdgeList.ListBar;
import com.pinball3d.zone.gui.component.TextInputBox;
import com.pinball3d.zone.gui.component.TexturedButton;
import com.pinball3d.zone.network.ConnectHelperClient;
import com.pinball3d.zone.network.ConnectionHelper.Type;
import com.pinball3d.zone.sphinx.ClassifyGroup;
import com.pinball3d.zone.sphinx.container.GuiContainerSphinxAdvanced;
import com.pinball3d.zone.util.HugeItemStack;
import com.pinball3d.zone.util.StorageWrapper;
import com.pinball3d.zone.util.Util;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;

public class SubscreenViewStorage extends Subscreen {
	public int page = 1, maxPage = 1;
	private TextInputBox box;
	private ScrollingEdgeList list;

	public SubscreenViewStorage(IHasSubscreen parent) {
		this(parent, getDisplayWidth() / 2 - 210, getDisplayHeight() / 2 - 100);
	}

	public SubscreenViewStorage(IHasSubscreen parent, int x, int y) {
		super(parent, x, y, 300, 200, true);
		addComponent(box = new TextInputBox(this, 87, 27, 61, 15, 55, () -> {
			box.isFocus = true;
		}).setIsPixel(true));
		addComponent(new TexturedButton(this, 147, 27, ICONS, 92, 41, 15, 15, 1.0F, () -> {
			box.isFocus = false;
		}));
		addComponent(new TexturedButton(this, 180, 180, ICONS, 92, 32, 5, 9, 1.0F, () -> {
			page = page - 1 < 1 ? maxPage : page - 1;
		}));
		addComponent(new TexturedButton(this, 235, 180, ICONS, 97, 32, 5, 9, 1.0F, () -> {
			page = page + 1 > maxPage ? 1 : page + 1;
		}));
		addComponent(list = new ScrollingEdgeList(this, 0, 9, 195).setNullable(true));
	}

	@Override
	public void onMouseScroll(int x, int y, boolean isUp) {
		super.onMouseScroll(x, y, isUp);
		if (isUp) {
			page = page - 1 < 1 ? maxPage : page - 1;
		} else {
			page = page + 1 > maxPage ? 1 : page + 1;
		}
	}

	public void renderCover(int slot) {
		if (!(parent.getSubscreens().peek() == this)) {
			return;
		}
		GlStateManager.disableLighting();
		GlStateManager.disableDepth();
		int slotX = slot % 13;
		int slotY = slot / 13;
		int j1 = slotX * 19 + 1 + 87;
		int k1 = slotY * 19 + 1 + 45;
		Gui.drawRect(j1, k1, j1 + 16, k1 + 16, 0x80FFFFFF);
		GlStateManager.enableLighting();
		GlStateManager.enableDepth();
	}

	@Override
	public Set<Type> getDataTypes() {
		Set<Type> s = super.getDataTypes();
		s.add(Type.ITEMS);
		s.add(Type.USEDSTORAGE);
		s.add(Type.MAXSTORAGE);
		s.add(Type.CLASSIFY);
		return s;
	}

	@Override
	public void doRenderForeground(int mouseX, int mouseY) {
		super.doRenderForeground(mouseX, mouseY);
		if (!(parent.getSubscreens().peek() == this)) {
			return;
		}
		int slot = getHoveredSlot(mouseX, mouseY);
		if (slot != -1) {
			slot += (page - 1) * 91;
			StorageWrapper w = getItems();
			Iterator<HugeItemStack> i = w.storges.iterator();
			while (i.hasNext()) {
				HugeItemStack s = i.next();
				if (slot == 0) {
					((GuiContainerSphinxAdvanced) parent).renderToolTip(s.stack, mouseX, mouseY);
					return;
				}
				slot--;
			}
			Iterator<ItemStack> j = w.other.iterator();
			while (j.hasNext()) {
				ItemStack s = j.next();
				if (slot == 0) {
					((GuiContainerSphinxAdvanced) parent).renderToolTip(s, mouseX, mouseY);
					return;
				}
				slot--;
			}
		}
	}

	public int getHoveredSlot(int mouseX, int mouseY) {
		int mX = mouseX - 87;
		int mY = mouseY - 45;
		if (mX >= 0 && mX <= 246 && mY >= 0 && mY <= 132 && mX % 19 < 18 && mY % 19 < 18) {
			int slotX = mX / 19;
			int slotY = mY / 19;
			return slotY * 13 + slotX;
		}
		return -1;
	}

	public StorageWrapper getItems() {
		StorageWrapper s;
		if (ConnectHelperClient.getInstance().hasData()) {
			s = ConnectHelperClient.getInstance().getItems().copy().search(box.text);
			ListBar bar = list.get();
			if (bar != null) {
				s.search(((ClassifyGroup) bar.getData()).getItems());
			}
		} else {
			s = new StorageWrapper();
		}
		maxPage = (s.getSize() - 1) / 91 + 1;
		if (page > maxPage) {
			page = maxPage;
		}
		if (page < 1) {
			page = 1;
		}
		return s;
	}

	public void updateList() {
		list.clear();
		if (ConnectHelperClient.getInstance().hasData()) {
			ConnectHelperClient.getInstance().getClassify().values().forEach(e -> {
				list.addBar(new ListBar(e.getName(), () -> {
				}).setData(e));
			});
		}
	}

	@Override
	public void doRenderBackground(int mouseX, int mouseY) {
		super.doRenderBackground(mouseX, mouseY);
		updateList();
		Util.drawTexture(UI_BORDER, 55, -5, 0, 0, 99, 99, 0.5F);
		Util.drawTexture(UI_BORDER, 315, -5, 99, 0, 99, 99, 0.5F);
		Util.drawTexture(UI_BORDER, 55, 155, 0, 99, 99, 99, 0.5F);
		Util.drawTexture(UI_BORDER, 315, 155, 99, 99, 99, 99, 0.5F);
		Gui.drawRect(104, 0, 315, 44, 0x2F000000);
		Gui.drawRect(60, 44, 360, 155, 0x2F000000);
		Gui.drawRect(104, 155, 315, 200, 0x2F000000);
		Util.renderGlowHorizonLine(70, 20, 280);
		Gui.drawRect(76, 24, 344, 194, 0x651CC3B5);
		RenderItem ir = mc.getRenderItem();
		StorageWrapper w = getItems();
		Iterator<HugeItemStack> it = w.storges.iterator();
		Iterator<ItemStack> it2 = w.other.iterator();
		for (int offset = (page - 1) * 91; offset > 0; offset--) {
			if (it.hasNext()) {
				it.next();
			} else if (it2.hasNext()) {
				it2.next();
			}
		}
		for (int j = 0; j < 7; j++) {
			for (int i = 0; i < 13; i++) {
				Util.drawBorder(87 + i * 19, 45 + j * 19, 18, 18, 1, 0xFF1ECCDE);
			}
		}
		FontRenderer fr = Util.getFontRenderer();
		Util.renderGlowString(I18n.format("sphinx.view_storages"), 75, 8);
		Util.renderGlowBorder(75, 23, 270, 172);
		String text = page + "/" + maxPage;
		Util.renderGlowString(text, 210 - fr.getStringWidth(text) / 2, 181);
		int usedStorage = ConnectHelperClient.getInstance().getUsedStorage();
		int maxStorage = ConnectHelperClient.getInstance().getMaxStorage();
		text = I18n.format("sphinx.storage_space") + ": " + usedStorage + "/" + maxStorage;
		Util.renderGlowString(text, 235, 30);
		GlStateManager.enableLighting();
		GlStateManager.enableDepth();
		GlStateManager.enableBlend();
		RenderHelper.enableGUIStandardItemLighting();
		GlStateManager.disableLighting();
		GlStateManager.enableRescaleNormal();
		GlStateManager.enableColorMaterial();
		GlStateManager.enableLighting();
		for (int j = 0; j < 7; j++) {
			for (int i = 0; i < 13; i++) {
				ItemStack stack = ItemStack.EMPTY;
				int amount = 0;
				if (it.hasNext()) {
					HugeItemStack hugestack = it.next();
					stack = hugestack.stack;
					amount = hugestack.count;
				} else if (it2.hasNext()) {
					stack = it2.next();
					amount = stack.getCount();
				}
				stack = stack.copy();
				stack.setCount(1);
				ir.renderItemAndEffectIntoGUI(stack, 88 + i * 19, 46 + j * 19);
				text = amount <= 1 ? null : Util.transferString(amount);
				ir.renderItemOverlayIntoGUI(fr, stack, 88 + i * 19, 46 + j * 19, text);
			}
		}
		int slot = getHoveredSlot(mouseX, mouseY);
		if (slot != -1) {
			renderCover(slot);
		}
	}

	@Override
	public void keyTyped(char typedChar, int keyCode) {
		super.keyTyped(typedChar, keyCode);
		if (keyCode == Keyboard.KEY_RETURN && box.isFocus) {
			box.isFocus = false;
		}
	}

	@Override
	public boolean isBlockOtherSubscreen() {
		return true;
	}
}
