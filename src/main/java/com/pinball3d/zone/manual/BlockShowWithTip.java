package com.pinball3d.zone.manual;

import com.pinball3d.zone.sphinx.Component;
import com.pinball3d.zone.sphinx.IParent;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.ItemStack;

public class BlockShowWithTip extends Component {
	private float scale;
	private ItemStack stack;

	public BlockShowWithTip(IParent parent, int x, int y, float scale, ItemStack stack) {
		super(parent, x, y, (int) (scale * 16), (int) (scale * 16));
		this.scale = scale;
		this.stack = stack;
	}

	@Override
	public void doRender(int mouseX, int mouseY) {
		GlStateManager.pushMatrix();
		RenderItem ir = mc.getRenderItem();
		GlStateManager.enableLighting();
		GlStateManager.enableDepth();
		GlStateManager.enableBlend();
		RenderHelper.enableGUIStandardItemLighting();
		GlStateManager.disableLighting();
		GlStateManager.enableRescaleNormal();
		GlStateManager.enableColorMaterial();
		GlStateManager.enableLighting();
		GlStateManager.scale(scale, scale, scale);
		ir.renderItemAndEffectIntoGUI(stack, x, y);
		GlStateManager.popMatrix();
	}

	public void renderToolTip(int mouseX, int mouseY) {
		if (mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height) {
			GlStateManager.disableLighting();
			GlStateManager.disableDepth();
			GlStateManager.colorMask(true, true, true, false);
			Gui.drawRect(x, y, x + width, y + height, 0x80FFFFFF);
			GlStateManager.colorMask(true, true, true, true);
			((ScreenManualBase) parent).renderToolTip(stack, mouseX, mouseY);
		}
	}
}
