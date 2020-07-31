package com.pinball3d.zone.sphinx;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import com.pinball3d.zone.network.MessageRequestStorage;
import com.pinball3d.zone.network.NetworkHandler;
import com.pinball3d.zone.tileentity.TEProcessingCenter;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class SubscreenViewStorage extends Subscreen {
	private static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/gui/sphinx/ui_border.png");
	public StorageWrapper data = new StorageWrapper();
	public String search = "";
	public int page = 1;

	public SubscreenViewStorage(IParent parent) {
		this(parent, parent.getWidth() / 2 - 150, parent.getHeight() / 2 - 100);
	}

	public SubscreenViewStorage(IParent parent, int x, int y) {
		super(parent, x, y, 300, 200, true);
		if (parent instanceof ScreenTerminal) {
			TEProcessingCenter te = (TEProcessingCenter) ((ScreenTerminal) parent).getNetwork().getTileEntity();
		} else {
			TEProcessingCenter te = ((ScreenSphinxController) parent).tileentity;
		}
	}

	public void renderCoverAndToolTip(ItemStack stack, int slot, int mouseX, int mouseY) {
		GlStateManager.disableLighting();
		GlStateManager.disableDepth();
		int slotX = slot % 13;
		int slotY = slot / 13;
		int j1 = slotX * 19 + 1 + x + 27;
		int k1 = slotY * 19 + 1 + y + 45;
		GlStateManager.colorMask(true, true, true, false);
		Gui.drawRect(j1, k1, j1 + 16, k1 + 16, 0x80FFFFFF);
		GlStateManager.colorMask(true, true, true, true);
		GlStateManager.enableLighting();
		GlStateManager.enableDepth();
		if (parent instanceof ScreenTerminal) {
			((ScreenTerminal) parent).renderToolTip(stack, mouseX, mouseY);
		} else {
			((ScreenSphinxController) parent).renderToolTip(stack, mouseX, mouseY);
		}
	}

	public int getHoveredSlot(int mouseX, int mouseY) {
		int mX = mouseX - x - 27;
		int mY = mouseY - y - 45;
		if (mX >= 0 && mX <= 246 && mY >= 0 && mY <= 132 && mX % 19 < 18 && mY % 19 < 18) {
			int slotX = mX / 19;
			int slotY = mY / 19;
			return slotY * 13 + slotX;
		}
		return -1;
	}

	@Override
	public void doRenderBackground(int mouseX, int mouseY) {
		super.doRenderBackground(mouseX, mouseY);
		WorldPos network;
		if (parent instanceof ScreenTerminal) {
			network = ((ScreenTerminal) parent).getNetwork();
		} else {
			network = new WorldPos(((ScreenSphinxController) parent).tileentity);
		}
		NetworkHandler.instance.sendToServer(new MessageRequestStorage(Minecraft.getMinecraft().player, network));
		Util.drawTexture(TEXTURE, x, y, 0, 0, 80, 80, 0.5F);
		Util.drawTexture(TEXTURE, x + 260, y, 80, 0, 80, 80, 0.5F);
		Util.drawTexture(TEXTURE, x, y + 160, 0, 80, 80, 80, 0.5F);
		Util.drawTexture(TEXTURE, x + 260, y + 160, 80, 80, 80, 80, 0.5F);
		Gui.drawRect(x + 40, y, x + 260, y + 40, 0x2F000000);
		Gui.drawRect(x, y + 40, x + 300, y + 160, 0x2F000000);
		Gui.drawRect(x + 40, y + 160, x + 260, y + 200, 0x2F000000);
		Gui.drawRect(x + 10, y + 20, x + 290, y + 22, 0xFF20E6EF);
		Gui.drawRect(x + 16, y + 24, x + 284, y + 194, 0x651CC3B5);
		RenderItem ir = mc.getRenderItem();
		Set<HugeItemStack> storges = data.storges;
		if (!search.isEmpty()) {
			storges = new TreeSet<HugeItemStack>(StorageWrapper.hugeStackComparator);
			Iterator<HugeItemStack> it = data.storges.iterator();
			while (it.hasNext()) {
				HugeItemStack hugestack = it.next();
				if (hugestack.stack.getItem().getRegistryName().getResourcePath().contains(search)
						|| hugestack.stack.getDisplayName().contains(search)) {
					storges.add(hugestack);
				}
			}
		}
		Set<ItemStack> other = data.other;
		if (!search.isEmpty()) {
			other = new TreeSet<ItemStack>(StorageWrapper.stackComparator);
			Iterator<ItemStack> it2 = data.other.iterator();
			while (it2.hasNext()) {
				ItemStack stack = it2.next();
				if (stack.getItem().getRegistryName().getResourcePath().contains(search)
						|| stack.getDisplayName().contains(search)) {
					other.add(stack);
				}
			}
		}
		Iterator<HugeItemStack> it = storges.iterator();
		Iterator<ItemStack> it2 = other.iterator();
		int index = 0;
		for (int offset = (page - 1) * 36; offset > 0; offset--) {
			if (it.hasNext()) {
				it.next();
			} else if (it2.hasNext()) {
				it2.next();
			}
		}
		for (int j = 0; j < 7; j++) {
			for (int i = 0; i < 13; i++) {
				Util.drawBorder(x + 27 + i * 19, y + 45 + j * 19, 18, 18, 1, 0xFF1ECCDE);
			}
		}
		parent.getFontRenderer().drawString(I18n.format("sphinx.view_storages"), x + 15, y + 8, 0xFF1ECCDE);
		Util.drawBorder(x + 15, y + 23, 270, 172, 1, 0xFF1ECCDE);
		RenderHelper.enableGUIStandardItemLighting();
		GlStateManager.disableLighting();
		GlStateManager.disableDepth();
		GlStateManager.disableBlend();
		ItemStack temp = ItemStack.EMPTY;
		int slot = -1;
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
				ir.renderItemAndEffectIntoGUI(stack, x + 28 + i * 19, y + 46 + j * 19);
				if (amount > 1) {
					String text = Util.transferString(amount);
					getFontRenderer().drawStringWithShadow(text,
							x + 45 + i * 19 - getFontRenderer().getStringWidth(text), y + 55 + j * 19, 0xFFFFFFFF);
				}
				ir.renderItemOverlays(getFontRenderer(), stack, x + 28 + i * 19, y + 46 + j * 19);
				if (getHoveredSlot(mouseX, mouseY) == j * 13 + i) {
					temp = stack;
					slot = j * 13 + i;
				}
			}
		}
		if (!temp.isEmpty()) {
			renderCoverAndToolTip(temp, slot, mouseX, mouseY);
		}
		RenderHelper.disableStandardItemLighting();
		GlStateManager.enableLighting();
		GlStateManager.enableDepth();
		GlStateManager.enableBlend();
	}
}
