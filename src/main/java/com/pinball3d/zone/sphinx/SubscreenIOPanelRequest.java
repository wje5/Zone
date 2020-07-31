package com.pinball3d.zone.sphinx;

import com.pinball3d.zone.inventory.GuiContainerIOPanel;
import com.pinball3d.zone.network.MessageIOPanelRequest;
import com.pinball3d.zone.network.NetworkHandler;
import com.pinball3d.zone.tileentity.TEIOPanel;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class SubscreenIOPanelRequest extends Subscreen {
	private static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/gui/sphinx/icons.png");
	private TextInputBox box;
	private Slider slider;
	public ItemStack stack;
	public int amount;

	public SubscreenIOPanelRequest(IParent parent, ItemStack stack, int amount) {
		this(parent, parent.getWidth() / 2 - 70, parent.getHeight() / 2 - 28, stack, amount);
	}

	public SubscreenIOPanelRequest(IParent parent, int x, int y, ItemStack stack, int amount) {
		super(parent, x + 140 + parent.getXOffset() > displayWidth ? displayWidth - 140 - parent.getXOffset() : x,
				y + 56 + parent.getYOffset() > displayHeight ? displayHeight - 56 - parent.getYOffset() : y, 140, 56,
				false);
		this.stack = stack;
		this.amount = amount;
		components.add(box = new TextInputBox(parent, this.x + 70, this.y + 4, 60, 13, 9, null, 4));
		box.isFocus = true;
		box.text = "1";
		components.add(slider = new Slider(this, this.x + 33, this.y + 18, 96));
		slider.setOnChange(new Runnable() {
			@Override
			public void run() {
				box.text = String.valueOf((int) ((SubscreenIOPanelRequest.this.amount - 1) * slider.get() + 1));
				System.out.println(box.text);
			}
		});
		components.add(new TextButton(this, this.x + 15, this.y + 41, I18n.format("sphinx.confirm"), new Runnable() {
			@Override
			public void run() {
				if (box.text.isEmpty()) {
					return;
				}
				int amount = Integer.valueOf(box.text);
				if (amount <= 0) {
					return;
				}
				System.out.println(amount);
				TEIOPanel te = ((GuiContainerIOPanel) parent).container.tileEntity;
				StorageWrapper wrapper = stack.getMaxStackSize() <= 1 ? new StorageWrapper(stack)
						: new StorageWrapper(new HugeItemStack(stack, amount));
				NetworkHandler.instance.sendToServer(MessageIOPanelRequest.newMessage("aaaaaaaa", te.getNetworkPos(),
						wrapper, new WorldPos(te.getPos(), te.getWorld())));
				parent.quitScreen(SubscreenIOPanelRequest.this);
			}
		}));
		components.add(new TextButton(this, this.x + 83, this.y + 41, I18n.format("sphinx.cancel"), new Runnable() {
			@Override
			public void run() {
				parent.quitScreen(SubscreenIOPanelRequest.this);
			}
		}));
	}

	@Override
	public void doRenderBackground(int mouseX, int mouseY) {
		super.doRenderBackground(mouseX, mouseY);
		int amount = -1;
		if (!box.text.isEmpty()) {
			amount = Integer.valueOf(box.text);
		}
		if (amount > this.amount) {
			amount = this.amount;
		}
		if (amount != -1) {
			box.text = "" + amount;
		}
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
		getFontRenderer().drawString(I18n.format("sphinx.time") + ":", x + 30, y + 30, 0xFF1ECCDE);
		String text = "103s";
		getFontRenderer().drawString(text, x + 128 - getFontRenderer().getStringWidth(text), y + 30, 0xFF1ECCDE);
		GlStateManager.enableLighting();
		GlStateManager.enableDepth();
		GlStateManager.enableBlend();
		GlStateManager.popMatrix();
		super.doRenderForeground(mouseX, mouseY);
	}

	@Override
	public void keyTyped(char typedChar, int keyCode) {
		if (subscreens.empty()) {
			components.forEach(e -> {
				e.onKeyTyped(typedChar, keyCode);
			});
		} else {
			subscreens.peek().keyTyped(typedChar, keyCode);
		}
	}
}
