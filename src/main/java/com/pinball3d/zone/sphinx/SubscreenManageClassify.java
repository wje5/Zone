package com.pinball3d.zone.sphinx;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.lwjgl.input.Keyboard;

import com.pinball3d.zone.network.ConnectionHelper.Type;

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
	public StorageWrapper data = new StorageWrapper();
	public int page = 1, maxPage = 1;
	private TextInputBox box;
	private ScrollingEdgeList list;
	private DropDownList list2, list3;
	public List<String> classes = new ArrayList<String>();

	public SubscreenManageClassify(IParent parent) {
		this(parent, parent.getWidth() / 2 - 210, parent.getHeight() / 2 - 100);
	}

	public SubscreenManageClassify(IParent parent, int x, int y) {
		super(parent, x, y, 360, 200, true);
		components.add(box = new TextInputBox(this, this.x + 82, this.y + 27, 61, 15, 55, () -> {
			box.isFocus = true;
		}).setIsPixel(true));
		components.add(new TexturedButton(this, this.x + 142, this.y + 27, ICONS, 92, 41, 15, 15, 1.0F, () -> {
			box.isFocus = false;// TODO
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
		components.add(list2 = new DropDownList(this, this.x + 180, this.y + 27, 61));
		list2.addBar(I18n.format("sphinx.only_show_unchosen"), () -> {

		});
		list2.addBar(I18n.format("sphinx.only_show_chosen"), () -> {

		});
		list2.addBar(I18n.format("sphinx.show_all"), () -> {

		});
		components.add(list3 = new DropDownList(this, this.x + 262, this.y + 27, 61));
		list3.addBar(I18n.format("sphinx.network_only"), () -> {

		});
		list3.addBar(I18n.format("sphinx.show_all"), () -> {

		});
		classes.add("1234");
		classes.add("5rdx");
		classes.add("^&YT");
		classes.add("#########");
		classes.add("####");
		classes.add("####");
		classes.add("####");
		classes.add("####");
		classes.add("####");
		classes.add("####");
		classes.add("####");
		classes.add("####");
		classes.add("####");
		classes.add("####");
		classes.add("####");
		classes.add("####");
		classes.add("####");
		classes.add("####");
		classes.add("####");
		classes.add("/u0072");
		classes.forEach(e -> {
			list.addBar(e, () -> {

			});
		});
	}

	@Override
	public Set<Type> getDataTypes() {
		Set<Type> s = super.getDataTypes();
		s.add(Type.ITEMS);
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

	public void renderCoverAndToolTip(ItemStack stack, int slot, int mouseX, int mouseY) {
		GlStateManager.disableLighting();
		GlStateManager.disableDepth();
		int slotX = slot % 9;
		int slotY = slot / 9;
		int j1 = slotX * 19 + 1 + x + 163;
		int k1 = slotY * 19 + 1 + y + 45;
		GlStateManager.colorMask(true, true, true, false);
		Gui.drawRect(j1, k1, j1 + 16, k1 + 16, 0x80FFFFFF);
		GlStateManager.colorMask(true, true, true, true);
		GlStateManager.enableLighting();
		GlStateManager.enableDepth();
	}

	@Override
	public void doRenderForeground(int mouseX, int mouseY) {
		super.doRenderForeground(mouseX, mouseY);
		if (parent instanceof ScreenSphinxBase) {
			int slot = getHoveredSlot(mouseX, mouseY);
			if (slot != -1) {
				slot += (page - 1) * 63;
				StorageWrapper w = getItems();
				Iterator<HugeItemStack> i = w.storges.iterator();
				while (i.hasNext()) {
					HugeItemStack s = i.next();
					if (slot == 0) {
						((ScreenSphinxBase) parent).renderToolTip(s.stack, mouseX, mouseY);
						return;
					}
					slot--;
				}
				Iterator<ItemStack> j = w.other.iterator();
				while (j.hasNext()) {
					ItemStack s = j.next();
					if (slot == 0) {
						((ScreenSphinxBase) parent).renderToolTip(s, mouseX, mouseY);
						return;
					}
					slot--;
				}
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

	public StorageWrapper getItems() {
		StorageWrapper s = Util.search(Util.getItemList(), box.text);
		maxPage = (s.getSize() - 1) / 63 + 1;
		if (page > maxPage) {
			page = maxPage;
		}
		if (page < 1) {
			page = 1;
		}
		return s;
	}

	@Override
	public void doRenderBackground(int mouseX, int mouseY) {
		super.doRenderBackground(mouseX, mouseY);
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
		StorageWrapper w = getItems();
		Iterator<HugeItemStack> it = w.storges.iterator();
		Iterator<ItemStack> it2 = w.other.iterator();
		for (int offset = (page - 1) * 63; offset > 0; offset--) {
			if (it.hasNext()) {
				it.next();
			} else if (it2.hasNext()) {
				it2.next();
			}
		}
		for (int j = 0; j < 7; j++) {
			for (int i = 0; i < 9; i++) {
				Util.drawBorder(x + 163 + i * 19, y + 45 + j * 19, 18, 18, 1, 0xFF1ECCDE);
			}
		}
		FontRenderer fr = getFontRenderer();
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
		ItemStack temp = ItemStack.EMPTY;
		int slot = -1;
		for (int j = 0; j < 7; j++) {
			for (int i = 0; i < 9; i++) {
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
				ir.renderItemAndEffectIntoGUI(stack, x + 164 + i * 19, y + 46 + j * 19);
				GlStateManager.pushMatrix();
				GlStateManager.translate(0, 0, 300F);
				text = amount <= 1 ? null : Util.transferString(amount);
				GlStateManager.popMatrix();
				ir.renderItemOverlayIntoGUI(getFontRenderer(), stack, x + 164 + i * 19, y + 46 + j * 19, text);
				if (getHoveredSlot(mouseX, mouseY) == j * 9 + i) {
					temp = stack;
					slot = j * 9 + i;
				}
			}
		}
		if (!temp.isEmpty()) {
			renderCoverAndToolTip(temp, slot, mouseX, mouseY);
		}
	}

	@Override
	public void keyTyped(char typedChar, int keyCode) {
		if (subscreens.empty()) {
			Iterator<Component> it = components.iterator();
			boolean flag = false;
			while (!flag && it.hasNext()) {
				Component c = it.next();
				flag = c.onKeyTyped(typedChar, keyCode);
			}
			if (keyCode == Keyboard.KEY_RETURN && box.isFocus) {
				box.isFocus = false;
			}
		} else {
			subscreens.peek().keyTyped(typedChar, keyCode);
		}
	}
}
