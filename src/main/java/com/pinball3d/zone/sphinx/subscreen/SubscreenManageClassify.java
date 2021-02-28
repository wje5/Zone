package com.pinball3d.zone.sphinx.subscreen;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.lwjgl.input.Keyboard;

import com.pinball3d.zone.gui.Subscreen;
import com.pinball3d.zone.network.ConnectHelperClient;
import com.pinball3d.zone.network.ConnectionHelper.Type;
import com.pinball3d.zone.network.MessageChangeClassifyName;
import com.pinball3d.zone.network.MessageDeleteClassify;
import com.pinball3d.zone.network.MessageManageClassify;
import com.pinball3d.zone.network.NetworkHandler;
import com.pinball3d.zone.sphinx.ClassifyGroup;
import com.pinball3d.zone.sphinx.IHasSubscreen;
import com.pinball3d.zone.sphinx.component.ClassifyGroupEdgeList;
import com.pinball3d.zone.sphinx.component.ClassifyGroupEdgeList.ListBar;
import com.pinball3d.zone.sphinx.component.DropDownList;
import com.pinball3d.zone.sphinx.component.TextInputBox;
import com.pinball3d.zone.sphinx.component.TexturedButton;
import com.pinball3d.zone.sphinx.container.GuiContainerSphinxAdvanced;
import com.pinball3d.zone.util.ItemSample;
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
	private ClassifyGroupEdgeList list;
	private DropDownList list2, list3;
	private ClassifyGroup local;
	private boolean dirty;

	public SubscreenManageClassify(IHasSubscreen parent) {
		this(parent, getDisplayWidth() / 2 - 210, getDisplayHeight() / 2 - 100);
	}

	public SubscreenManageClassify(IHasSubscreen parent, int x, int y) {
		super(parent, x, y, 360, 200, true);
		addComponent(box = new TextInputBox(this, this.x + 82, this.y + 27, 61, 15, 55, () -> {
			box.isFocus = true;
		}).setIsPixel(true).setOnInput(() -> {
			refreshData();
		}));
		addComponent(new TexturedButton(this, this.x + 142, this.y + 27, ICONS, 92, 41, 15, 15, 1.0F, () -> {
			box.isFocus = false;
		}));
		addComponent(new TexturedButton(this, this.x + 175, this.y + 180, ICONS, 92, 32, 5, 9, 1.0F, () -> {
			page = page - 1 < 1 ? maxPage : page - 1;
		}));
		addComponent(new TexturedButton(this, this.x + 230, this.y + 180, ICONS, 97, 32, 5, 9, 1.0F, () -> {
			page = page + 1 > maxPage ? 1 : page + 1;
		}));
		addComponent(new TexturedButton(this, this.x + 82, this.y + 48, ICONS, 137, 41, 15, 15, 1.0F, () -> {
			System.out.println("button1");
		}));
		addComponent(new TexturedButton(this, this.x + 102, this.y + 48, ICONS, 152, 41, 15, 15, 1.0F, () -> {
			System.out.println("button2");
		}));
		addComponent(new TexturedButton(this, this.x + 122, this.y + 49, ICONS, 167, 41, 15, 13, 1.0F, () -> {
			System.out.println("button3");
		}));
		addComponent(new TexturedButton(this, this.x + 153, this.y + 71, ICONS, 0, 172, 16, 15, 0.5F, () -> {
			parent.putScreen(new SubscreenTextInputBox(parent, I18n.format("sphinx.change_name"),
					I18n.format("sphinx.set_classify_name"), s -> {
						local.setName(s);
						NetworkHandler.instance.sendToServer(MessageChangeClassifyName
								.newMessage(ConnectHelperClient.getInstance().getNetworkPos(), list.get().id, s));
					}, 8, 8).setIsValid(s -> s.length() >= 1 ? "" : I18n.format("sphinx.classify_name_length_error"))
							.setText(getGroup().getName()));
		}).setEnable(() -> getGroup() != null));
		addComponent(new TexturedButton(this, this.x + 153, this.y + 86, ICONS, 16, 172, 11, 15, 0.5F, () -> {
			parent.putScreen(new SubscreenConfirmBox(parent, I18n.format("sphinx.delete_classify"),
					Util.formatAndAntiEscape("sphinx.confirm_delete_classify", list.get().title), () -> {
						NetworkHandler.instance.sendToServer(MessageDeleteClassify
								.newMessage(ConnectHelperClient.getInstance().getNetworkPos(), list.get().id));
						list.list.remove(list.index);
						list.change(0);
					}));
		}).setEnable(() -> getGroup() != null));
		addComponent(new TexturedButton(this, this.x + 142, this.y + 48, ICONS, 207, 41, 15, 15, 1.0F, () -> {
			if (ConnectHelperClient.getInstance().hasData()) {
				NetworkHandler.instance.sendToServer(MessageManageClassify
						.newMessage(ConnectHelperClient.getInstance().getNetworkPos(), list.get().id, local));
				dirty = false;
			}
		}));
		addComponent(list = new ClassifyGroupEdgeList(this, this.x, this.y + 9, 195).setOnChange((index) -> {
			if (dirty) {
				parent.putScreen(new SubscreenYNCBox(parent, I18n.format("sphinx.save"),
						I18n.format("sphinx.save_change", local.getName()), () -> {
							NetworkHandler.instance.sendToServer(MessageManageClassify.newMessage(
									ConnectHelperClient.getInstance().getNetworkPos(), list.get().id, local));
							list.index = index;
							ListBar bar = list.get();
							local = ConnectHelperClient.getInstance().getClassify().get(bar.id);
							dirty = false;
						}, () -> {
							list.index = index;
							ListBar bar = list.get();
							local = ConnectHelperClient.getInstance().getClassify().get(bar.id);
							dirty = false;
						}));
				return false;
			} else {
				list.index = index;
				ListBar bar = list.get();
				local = ConnectHelperClient.getInstance().getClassify().get(bar.id);
				return true;
			}
		}));
		addComponent(list2 = new DropDownList(this, this.x + 180, this.y + 27, 61).setOnChange(i -> {
			refreshData();
		}));
		list2.addBar(I18n.format("sphinx.only_show_unchosen"), () -> {

		});
		list2.addBar(I18n.format("sphinx.only_show_chosen"), () -> {

		});
		list2.addBar(I18n.format("sphinx.show_all"), () -> {

		});
		addComponent(list3 = new DropDownList(this, this.x + 262, this.y + 27, 61).setOnChange(i -> {
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
		int slot = getHoveredSlot(x, y);
		if (slot != -1) {
			Set<ItemType> l = getItems();
			Iterator<ItemType> it = l.iterator();
			for (int offset = (page - 1) * 63 + slot; offset > 0; offset--) {
				if (it.hasNext()) {
					it.next();
				} else {
					return;
				}
			}
			if (it.hasNext()) {
				ItemType type = it.next();
				ClassifyGroup g = getGroup();
				if (g != null) {
					if (g.contains(type)) {
						g.removeItem(type);
					} else {
						g.addItem(type);
					}
					dirty = !g.equals(ConnectHelperClient.getInstance().getClassify().get(list.get().id));
				} else {
					parent.putScreen(new SubscreenMessageBox(parent, I18n.format("sphinx.error"),
							I18n.format("sphinx.unselected_classify")));
				}
			}
		}
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
		if (!(parent.getSubscreens().peek() == this)) {
			return;
		}
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
		if (!(parent.getSubscreens().peek() == this)) {
			return;
		}
		int slot = getHoveredSlot(mouseX, mouseY);
		if (slot != -1) {
			slot += (page - 1) * 63;
			Set<ItemType> l = getItems();
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
		if (data == null || data.isEmpty()) {
			if (ConnectHelperClient.getInstance().hasData()) {
				data = ConnectHelperClient.getInstance().getItems();
				return data == null ? new StorageWrapper() : data;
			} else {
				return new StorageWrapper();
			}
		}
		return data;
	}

	public ClassifyGroup getGroup() {
		if (list.isEmpty()) {
			local = null;
		} else if (local == null && ConnectHelperClient.getInstance().hasData()) {
			local = ConnectHelperClient.getInstance().getClassify().get(list.get().id);
		}
		return local;
	}

	public void refreshData() {
		if (ConnectHelperClient.getInstance().hasData()) {
			data = ConnectHelperClient.getInstance().getItems();
		}
	}

	public Set<ItemType> getItems() {
		ItemSample sample = new ItemSample(Util.search(list3.index == 0 ? getData() : Util.getItemList(), box.text));
		if (list2.index == 0) {
			ClassifyGroup group = list.isEmpty() ? null
					: ConnectHelperClient.getInstance().getClassify().get(list.get().id);
			if (group != null) {
				sample.remove(group.getItems());
			}
		} else if (list2.index == 1) {
			ClassifyGroup group = list.isEmpty() ? null
					: ConnectHelperClient.getInstance().getClassify().get(list.get().id);
			if (group != null) {
				sample.and(group.getItems());
			} else {
				sample.clear();
			}
		}
		Set<ItemType> s = sample.get();
		maxPage = (s.size() - 1) / 63 + 1;
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
			Map<Integer, ClassifyGroup> m = ConnectHelperClient.getInstance().getClassify();
			m.forEach((k, v) -> {
				list.addBar(v.getName(), k, () -> {

				});
			});
		}
	}

	@Override
	public void doRenderBackground(int mouseX, int mouseY) {
		super.doRenderBackground(mouseX, mouseY);
		updateList();
		Util.drawTexture(TEXTURE, x + 55, y - 5, 0, 0, 99, 99, 0.5F);
		Util.drawTexture(TEXTURE, x + 315, y - 5, 99, 0, 99, 99, 0.5F);
		Util.drawTexture(TEXTURE, x + 55, y + 155, 0, 99, 99, 99, 0.5F);
		Util.drawTexture(TEXTURE, x + 315, y + 155, 99, 99, 99, 99, 0.5F);
		Gui.drawRect(x + 104, y, x + 315, y + 44, 0x2F000000);
		Gui.drawRect(x + 60, y + 44, x + 360, y + 155, 0x2F000000);
		Gui.drawRect(x + 104, y + 155, x + 315, y + 200, 0x2F000000);
		Util.renderGlowHorizonLine(x + 70, y + 20, 280);
		Gui.drawRect(x + 76, y + 24, x + 344, y + 194, 0x651CC3B5);
		RenderItem ir = mc.getRenderItem();
		Set<ItemType> l = getItems();
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
		ClassifyGroup group = getGroup();
		Util.renderGlowString(I18n.format("sphinx.manage_classify") + (dirty ? "*" : ""), x + 75, y + 8);
		if (group != null) {
			Util.renderGlowString(group.getName(), x + 82, y + 70);
		}

		Util.renderGlowBorder(x + 75, y + 23, 270, 172);
		String text = page + "/" + maxPage;
		Util.renderGlowString(text, x + 205 - fr.getStringWidth(text) / 2, y + 181);
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
		int c = 0;
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
						GlStateManager.pushMatrix();
						GlStateManager.translate(0, 0, 400F);
						Util.drawTexture(ICONS, x + 174 + i * 19, y + 56 + j * 19, 92, 56, 7, 7, 1.0F);
						GlStateManager.popMatrix();
						c++;
					}
				}
			}
		}
		Util.renderGlowString(I18n.format("sphinx.object_amount", c), x + 82, y + 85);
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
	public boolean onQuit() {
		if (!super.onQuit()) {
			return false;
		}
		if (dirty) {
			parent.putScreen(new SubscreenYNCBox(parent, I18n.format("sphinx.save"),
					I18n.format("sphinx.save_change", local.getName()), () -> {
						NetworkHandler.instance.sendToServer(MessageManageClassify
								.newMessage(ConnectHelperClient.getInstance().getNetworkPos(), list.get().id, local));
						parent.removeScreen(SubscreenManageClassify.this);
					}, () -> parent.removeScreen(SubscreenManageClassify.this)));
			return false;
		}
		return true;
	}

	@Override
	public boolean isBlockOtherSubscreen() {
		return true;
	}
}
