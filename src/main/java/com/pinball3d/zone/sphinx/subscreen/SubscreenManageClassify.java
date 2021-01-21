package com.pinball3d.zone.sphinx.subscreen;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.lwjgl.input.Keyboard;

import com.pinball3d.zone.gui.Subscreen;
import com.pinball3d.zone.network.ConnectHelperClient;
import com.pinball3d.zone.network.ConnectionHelper.Type;
import com.pinball3d.zone.sphinx.ClassifyGroup;
import com.pinball3d.zone.sphinx.GuiContainerSphinxAdvanced;
import com.pinball3d.zone.sphinx.IHasSubscreen;
import com.pinball3d.zone.sphinx.component.DropDownList;
import com.pinball3d.zone.sphinx.component.ScrollingEdgeList;
import com.pinball3d.zone.sphinx.component.TextInputBox;
import com.pinball3d.zone.sphinx.component.TexturedButton;
import com.pinball3d.zone.util.ItemType;
import com.pinball3d.zone.util.StorageWrapper;
import com.pinball3d.zone.util.Util;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class SubscreenManageClassify extends Subscreen {
	private static final ResourceLocation ICONS = new ResourceLocation("zone:textures/gui/sphinx/icons.png");
	private static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/gui/sphinx/ui_border.png");
	public StorageWrapper data;
	public int page = 1, maxPage = 1;
	private TextInputBox box;
	private ScrollingEdgeList list;
	private DropDownList list2, list3;

	public SubscreenManageClassify(IHasSubscreen parent) {
		this(parent, getDisplayWidth() / 2 - 210, getDisplayHeight() / 2 - 100);
	}

	public SubscreenManageClassify(IHasSubscreen parent, int x, int y) {
		super(parent, x, y, 360, 200, true);
		components.add(box = new TextInputBox(this, this.x + 82, this.y + 27, 61, 15, 55, () -> {
			box.isFocus = true;
		}).setIsPixel(true).setOnInput(() -> {
			refreshData();
		}));
		components.add(new TexturedButton(this, this.x + 142, this.y + 27, ICONS, 92, 41, 15, 15, 1.0F, () -> {
			box.isFocus = false;
		}));
		components.add(new TexturedButton(this, this.x + 175, this.y + 180, ICONS, 92, 32, 5, 9, 1.0F, () -> {
			page = page - 1 < 1 ? maxPage : page - 1;
		}));
		components.add(new TexturedButton(this, this.x + 230, this.y + 180, ICONS, 97, 32, 5, 9, 1.0F, () -> {
			page = page + 1 > maxPage ? 1 : page + 1;
		}));
		components.add(new TexturedButton(this, this.x + 82, this.y + 48, ICONS, 137, 41, 15, 15, 1.0F, () -> {
			System.out.println("button1");
		}));
		components.add(new TexturedButton(this, this.x + 102, this.y + 48, ICONS, 152, 41, 15, 15, 1.0F, () -> {
			System.out.println("button2");
		}));
		components.add(new TexturedButton(this, this.x + 122, this.y + 49, ICONS, 167, 41, 15, 13, 1.0F, () -> {
			System.out.println("button3");
		}));
		components.add(new TexturedButton(this, this.x + 142, this.y + 51, ICONS, 182, 41, 14, 12, 1.0F, () -> {
			System.out.println("button4");
		}));
		components.add(list = new ScrollingEdgeList(this, this.x, this.y + 9, 195));
		components.add(list2 = new DropDownList(this, this.x + 180, this.y + 27, 61).setOnChange(i -> {
			refreshData();
		}));
		list2.addBar(I18n.format("sphinx.only_show_unchosen"), () -> {

		});
		list2.addBar(I18n.format("sphinx.only_show_chosen"), () -> {

		});
		list2.addBar(I18n.format("sphinx.show_all"), () -> {

		});
		components.add(list3 = new DropDownList(this, this.x + 262, this.y + 27, 61).setOnChange(i -> {
			refreshData();
		}));
		list3.addBar(I18n.format("sphinx.network_only"), () -> {

		});
		list3.addBar(I18n.format("sphinx.show_all"), () -> {

		});
	}

	@Override
	public void onClick(int x, int y, boolean isLeft) {
		super.onClick(x, y, isLeft);
	}

	@Override
	public Set<Type> getDataTypes() {
		Set<Type> s = super.getDataTypes();
		s.add(Type.ITEMS);
		s.add(Type.CLASSIFY);
		return s;
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
		GlStateManager.disableLighting();
		GlStateManager.disableDepth();
		int slotX = slot % 9;
		int slotY = slot / 9;
		int j1 = slotX * 19 + 1 + x + 163;
		int k1 = slotY * 19 + 1 + y + 45;
		Gui.drawRect(j1, k1, j1 + 16, k1 + 16, 0x80FFFFFF);
		GlStateManager.enableLighting();
		GlStateManager.enableDepth();
	}

	@Override
	public void doRenderForeground(int mouseX, int mouseY) {
		super.doRenderForeground(mouseX, mouseY);
		int slot = getHoveredSlot(mouseX, mouseY);
		if (slot != -1) {
			slot += (page - 1) * 63;
			List<ItemType> l = getItems();
			Iterator<ItemType> it = l.iterator();
			while (it.hasNext()) {
				ItemType s = it.next();
				if (slot == 0) {
					((GuiContainerSphinxAdvanced) parent).renderToolTip(s.createStack(), mouseX, mouseY);
					return;
				}
				slot--;
			}
		}
	}

	public int getHoveredSlot(int mouseX, int mouseY) {
		int mX = mouseX - x - 163;
		int mY = mouseY - y - 45;
		if (mX >= 0 && mX <= 170 && mY >= 0 && mY <= 132 && mX % 19 < 18 && mY % 19 < 18) {
			int slotX = mX / 19;
			int slotY = mY / 19;
			return slotY * 9 + slotX;
		}
		return -1;
	}

	public StorageWrapper getData() {
		if (data == null) {
			if (ConnectHelperClient.getInstance().hasData()) {
				data = ConnectHelperClient.getInstance().getItems();
				return data == null ? new StorageWrapper() : data;
			} else {
				return new StorageWrapper();
			}
		}
		return data;
	}

	public void refreshData() {
		if (ConnectHelperClient.getInstance().hasData()) {
			data = ConnectHelperClient.getInstance().getItems();
		}
	}

	public List<ItemType> getItems() {
		StorageWrapper s = Util.search(list3.index == 0 ? getData() : Util.getItemList(), box.text);
		List<ItemType> l = Util.getSampleFromWrapper(s);
		maxPage = (l.size() - 1) / 63 + 1;
		if (page > maxPage) {
			page = maxPage;
		}
		if (page < 1) {
			page = 1;
		}
		return l;
	}

	public void updateList() {
		list.clear();
		if (ConnectHelperClient.getInstance().hasData()) {
			List<ClassifyGroup> l = ConnectHelperClient.getInstance().getClassify();
			l.forEach(e -> {
				list.addBar(e.getName(), () -> {

				});
			});
		}
	}

	@Override
	public void doRenderBackground(int mouseX, int mouseY) {
		super.doRenderBackground(mouseX, mouseY);
		updateList();
		Util.drawTexture(TEXTURE, x + 60, y, 0, 0, 80, 80, 0.5F);
		Util.drawTexture(TEXTURE, x + 320, y, 80, 0, 80, 80, 0.5F);
		Util.drawTexture(TEXTURE, x + 60, y + 160, 0, 80, 80, 80, 0.5F);
		Util.drawTexture(TEXTURE, x + 320, y + 160, 80, 80, 80, 80, 0.5F);
		Gui.drawRect(x + 100, y, x + 320, y + 40, 0x2F000000);
		Gui.drawRect(x + 60, y + 40, x + 360, y + 160, 0x2F000000);
		Gui.drawRect(x + 100, y + 160, x + 320, y + 200, 0x2F000000);
		Gui.drawRect(x + 70, y + 20, x + 350, y + 22, 0xFF20E6EF);
		Gui.drawRect(x + 76, y + 24, x + 344, y + 194, 0x651CC3B5);
		RenderItem ir = mc.getRenderItem();
		List<ItemType> l = getItems();
		Iterator<ItemType> it = l.iterator();
		for (int offset = (page - 1) * 63; offset > 0; offset--) {
			if (it.hasNext()) {
				it.next();
			}
		}
		for (int j = 0; j < 7; j++) {
			for (int i = 0; i < 9; i++) {
				Util.drawBorder(x + 163 + i * 19, y + 45 + j * 19, 18, 18, 1, 0xFF1ECCDE);
			}
		}
		FontRenderer fr = Util.getFontRenderer();
		fr.drawString(I18n.format("sphinx.manage_classify"), x + 75, y + 8, 0xFF1ECCDE);
		Util.drawBorder(x + 75, y + 23, 270, 172, 1, 0xFF1ECCDE);
		String text = page + "/" + maxPage;
		fr.drawString(text, x + 205 - fr.getStringWidth(text) / 2, y + 181, 0xFF1ECCDE);
		GlStateManager.enableLighting();
		GlStateManager.enableDepth();
		GlStateManager.enableBlend();
		RenderHelper.enableGUIStandardItemLighting();
		GlStateManager.disableLighting();
		GlStateManager.enableRescaleNormal();
		GlStateManager.enableColorMaterial();
		GlStateManager.enableLighting();
		if (!ConnectHelperClient.getInstance().hasData()) {
			return;
		}
		ClassifyGroup group = list.isEmpty() ? null : ConnectHelperClient.getInstance().getClassify().get(list.index);
		for (int j = 0; j < 7; j++) {
			for (int i = 0; i < 9; i++) {
				int amount = 0;
				if (it.hasNext()) {
					ItemType type = it.next();
					ItemStack stack = type.createStack();
					ir.renderItemAndEffectIntoGUI(stack, x + 164 + i * 19, y + 46 + j * 19);
					text = amount <= 1 ? null : Util.transferString(amount);
					ir.renderItemOverlayIntoGUI(fr, stack, x + 164 + i * 19, y + 46 + j * 19, text);
					if (group != null && group.contains(type)) {
						Util.drawTexture(x + 174 + i * 19, y + 56 + j * 19, 92, 56, 7, 7, 1.0F);
					}
				}
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
}
