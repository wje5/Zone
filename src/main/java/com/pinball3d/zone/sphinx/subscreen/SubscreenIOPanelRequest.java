package com.pinball3d.zone.sphinx.subscreen;

import java.util.Iterator;

import org.lwjgl.input.Keyboard;

import com.pinball3d.zone.gui.IHasSubscreen;
import com.pinball3d.zone.gui.Subscreen;
import com.pinball3d.zone.gui.component.Component;
import com.pinball3d.zone.gui.component.Slider;
import com.pinball3d.zone.gui.component.TextButton;
import com.pinball3d.zone.gui.component.TextInputBox;
import com.pinball3d.zone.network.ConnectHelperClient;
import com.pinball3d.zone.network.MessageComputeLogisticTime;
import com.pinball3d.zone.network.MessageIOPanelRequest;
import com.pinball3d.zone.network.NetworkHandler;
import com.pinball3d.zone.sphinx.container.ContainerIOPanel;
import com.pinball3d.zone.sphinx.container.GuiContainerIOPanel;
import com.pinball3d.zone.tileentity.TEIOPanel;
import com.pinball3d.zone.util.HugeItemStack;
import com.pinball3d.zone.util.StorageWrapper;
import com.pinball3d.zone.util.Util;
import com.pinball3d.zone.util.WorldPos;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;

public class SubscreenIOPanelRequest extends Subscreen {
	private TextInputBox box;
	private Slider slider;
	public ItemStack stack;
	public int amount;
	public int time = -1;

	public SubscreenIOPanelRequest(IHasSubscreen parent, ItemStack stack, int amount) {
		this(parent, getDisplayWidth() / 2 - 70, getDisplayHeight() / 2 - 28, stack, amount);
	}

	public SubscreenIOPanelRequest(IHasSubscreen parent, int x, int y, ItemStack stack, int amount) {
		super(parent, x, y, 140, 56, false);
		this.stack = stack;
		this.amount = amount;
		addComponent(box = new TextInputBox(this, 70, 4, 60, 13, 9, null, 4).setOnInput(() -> {
			int count = -1;
			if (!box.text.isEmpty()) {
				count = Integer.valueOf(box.text);
			}
			if (count > SubscreenIOPanelRequest.this.amount) {
				count = SubscreenIOPanelRequest.this.amount;
			}
			if (count != -1) {
				box.text = "" + count;
				slider.set(1.0F * count / SubscreenIOPanelRequest.this.amount);
			}
			refreshTime();
		}));
		box.isFocus = true;
		box.text = "1";

		refreshTime();
		addComponent(slider = new Slider(this, 33, 18, 96));
		slider.setOnChange(() -> {
			box.text = String.valueOf((int) ((SubscreenIOPanelRequest.this.amount - 1) * slider.get() + 1));
			refreshTime();
		});
		addComponent(new TextButton(this, 15, 41, I18n.format("sphinx.confirm"), () -> {
			onConfirm();
		}));
		addComponent(new TextButton(this, 83, 41, I18n.format("sphinx.cancel"), () -> {
			parent.removeScreen(SubscreenIOPanelRequest.this);
		}));
	}

	public void max() {
		box.text = amount + "";
		slider.set(1.0F);
		refreshTime();
	}

	public void onConfirm() {
		if (box.text.isEmpty()) {
			return;
		}
		int amount = Integer.valueOf(box.text);
		if (amount <= 0) {
			return;
		}
		TEIOPanel te = ((ContainerIOPanel) ((GuiContainerIOPanel) parent).inventorySlots).tileEntity;
		StorageWrapper wrapper = stack.getMaxStackSize() <= 1 ? new StorageWrapper(stack)
				: new StorageWrapper(new HugeItemStack(stack, amount));
		NetworkHandler.instance.sendToServer(MessageIOPanelRequest.newMessage(mc.player, new WorldPos(te), wrapper));
		parent.removeScreen(this);
	}

	public void refreshTime() {
		time = -1;
		if (box.text.isEmpty()) {
			return;
		}
		int amount = Integer.valueOf(box.text);
		if (amount <= 0) {
			return;
		}
		StorageWrapper wrapper = stack.getMaxStackSize() <= 1 ? new StorageWrapper(stack.copy())
				: new StorageWrapper(new HugeItemStack(stack.copy(), amount));
		TEIOPanel te = ((ContainerIOPanel) ((GuiContainerIOPanel) parent).inventorySlots).tileEntity;
		NetworkHandler.instance.sendToServer(MessageComputeLogisticTime
				.newMessage(ConnectHelperClient.getInstance().getNetworkPos(), new WorldPos(te), wrapper));
	}

	public void updateTime(int time, StorageWrapper wrapper) {
		int amount = Integer.valueOf(box.text);
		if (amount <= 0) {
			return;
		}
		StorageWrapper w = stack.getMaxStackSize() <= 1 ? new StorageWrapper(stack.copy())
				: new StorageWrapper(new HugeItemStack(stack.copy(), amount));
		if (w.isEquals(wrapper)) {
			this.time = time;
		}
	}

	@Override
	public void doRenderBackground(int mouseX, int mouseY) {
		super.doRenderBackground(mouseX, mouseY);
		Gui.drawRect(0, 0, width, height, 0xFF0B5953);
		GlStateManager.enableLighting();
		GlStateManager.enableDepth();
		GlStateManager.enableBlend();
		RenderItem ir = ((GuiContainerIOPanel) parent).getItemRenderer();
		ir.zLevel = 400.0F;
		RenderHelper.enableGUIStandardItemLighting();
		GlStateManager.disableLighting();
		GlStateManager.enableRescaleNormal();
		GlStateManager.enableColorMaterial();
		GlStateManager.enableLighting();
		ir.renderItemAndEffectIntoGUI(stack, 9, 9);
		ir.renderItemOverlayIntoGUI(Util.getFontRenderer(), stack, 9, 9, "");
	}

	@Override
	public void doRenderForeground(int mouseX, int mouseY) {
		GlStateManager.pushMatrix();
		GlStateManager.disableLighting();
		GlStateManager.disableDepth();
		GlStateManager.disableBlend();
		Util.renderGlowBorder(0, 0, width, height);
		Util.drawBorder(8, 8, 18, 18, 1, 0xFF1ECCDE);
		FontRenderer fr = Util.getFontRenderer();
		Util.renderGlowString(I18n.format("sphinx.output") + ":", 30, 6);
		if (time != -1) {
			Util.renderGlowString(I18n.format("sphinx.time") + ":", 30, 30);
			String text = (time / 20) + "s" + (time % 20) + "tick";
			Util.renderGlowString(text, 128 - fr.getStringWidth(text), 30);
		}
		GlStateManager.enableLighting();
		GlStateManager.enableDepth();
		GlStateManager.enableBlend();
		GlStateManager.popMatrix();
		super.doRenderForeground(mouseX, mouseY);
	}

	@Override
	public void keyTyped(char typedChar, int keyCode) {
		Iterator<Component> it = components.iterator();
		boolean flag = false;
		while (!flag && it.hasNext()) {
			Component c = it.next();
			flag = c.onKeyTyped(typedChar, keyCode);
		}
		if (!flag && keyCode == Keyboard.KEY_RETURN) {
			onConfirm();
		}
	}
}
