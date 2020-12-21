package com.pinball3d.zone.sphinx;

import java.util.Iterator;

import org.lwjgl.input.Keyboard;

import com.pinball3d.zone.inventory.GuiContainerIOPanel;
import com.pinball3d.zone.network.MessageComputeLogisticTime;
import com.pinball3d.zone.network.MessageIOPanelRequest;
import com.pinball3d.zone.network.NetworkHandler;
import com.pinball3d.zone.tileentity.TEIOPanel;

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

	public SubscreenIOPanelRequest(IParent parent, ItemStack stack, int amount) {
		this(parent, parent.getWidth() / 2 - 70, parent.getHeight() / 2 - 28, stack, amount);
	}

	public SubscreenIOPanelRequest(IParent parent, int x, int y, ItemStack stack, int amount) {
		super(parent,
				x + 140 + parent.getXOffset() > getDisplayWidth() ? getDisplayWidth() - 140 - parent.getXOffset() : x,
				y + 56 + parent.getYOffset() > getDisplayHeight() ? getDisplayHeight() - 56 - parent.getYOffset() : y,
				140, 56, false);
		this.stack = stack;
		this.amount = amount;
		components.add(box = new TextInputBox(parent, this.x + 70, this.y + 4, 60, 13, 9, null, 4).setOnInput(() -> {
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
		components.add(slider = new Slider(this, this.x + 33, this.y + 18, 96));
		slider.setOnChange(() -> {
			box.text = String.valueOf((int) ((SubscreenIOPanelRequest.this.amount - 1) * slider.get() + 1));
			refreshTime();
		});
		components.add(new TextButton(this, this.x + 15, this.y + 41, I18n.format("sphinx.confirm"), () -> {
			onConfirm();
		}));
		components.add(new TextButton(this, this.x + 83, this.y + 41, I18n.format("sphinx.cancel"), () -> {
			parent.quitScreen(SubscreenIOPanelRequest.this);
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
		TEIOPanel te = ((GuiContainerIOPanel) parent).container.tileEntity;
		StorageWrapper wrapper = stack.getMaxStackSize() <= 1 ? new StorageWrapper(stack)
				: new StorageWrapper(new HugeItemStack(stack, amount));
		NetworkHandler.instance.sendToServer(
				MessageIOPanelRequest.newMessage(te.getPassword(), te.getNetworkPos(), wrapper, new WorldPos(te)));
		parent.quitScreen(this);
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
		NetworkHandler.instance.sendToServer(new MessageComputeLogisticTime(mc.player,
				new WorldPos(((GuiContainerIOPanel) parent).container.tileEntity), wrapper));
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
		Gui.drawRect(x, y, x + width, y + height, 0xFF0B5953);
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
		ir.renderItemAndEffectIntoGUI(stack, x + 9, y + 9);
		ir.renderItemOverlays(getFontRenderer(), stack, x + 9, y + 9);
	}

	@Override
	public void doRenderForeground(int mouseX, int mouseY) {
		GlStateManager.pushMatrix();
		GlStateManager.disableLighting();
		GlStateManager.disableDepth();
		GlStateManager.disableBlend();
		Util.drawBorder(x, y, 140, 56, 1, 0xFF1ECCDE);
		Util.drawBorder(x + 8, y + 8, 18, 18, 1, 0xFF1ECCDE);
		getFontRenderer().drawString(I18n.format("sphinx.output") + ":", x + 30, y + 6, 0xFF1ECCDE);
		if (time != -1) {
			getFontRenderer().drawString(I18n.format("sphinx.time") + ":", x + 30, y + 30, 0xFF1ECCDE);
			String text = (time / 20) + "s" + (time % 20) + "tick";
			getFontRenderer().drawString(text, x + 128 - getFontRenderer().getStringWidth(text), y + 30, 0xFF1ECCDE);
		}
		GlStateManager.enableLighting();
		GlStateManager.enableDepth();
		GlStateManager.enableBlend();
		GlStateManager.popMatrix();
		super.doRenderForeground(mouseX, mouseY);
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
			if (!flag && keyCode == Keyboard.KEY_RETURN) {
				onConfirm();
			}
		} else {
			subscreens.peek().keyTyped(typedChar, keyCode);
		}
	}
}
