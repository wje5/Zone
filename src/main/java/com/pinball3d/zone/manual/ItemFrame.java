package com.pinball3d.zone.manual;

import com.pinball3d.zone.sphinx.Component;
import com.pinball3d.zone.sphinx.IParent;
import com.pinball3d.zone.sphinx.Util;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ItemFrame extends Component {
	public static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/gui/manual.png");
	private ItemStack stack;

	public ItemFrame(IParent parent, int x, int y, ItemStack stack) {
		super(parent, x, y, 18, 18);
		this.stack = stack;
	}

	@Override
	public void doRender(int mouseX, int mouseY) {
		Util.drawTexture(TEXTURE, x, y, 146, 13, 18, 18, 1.0F);
		RenderItem ir = mc.getRenderItem();
		GlStateManager.enableLighting();
		GlStateManager.enableDepth();
		GlStateManager.enableBlend();
		RenderHelper.enableGUIStandardItemLighting();
		GlStateManager.disableLighting();
		GlStateManager.enableRescaleNormal();
		GlStateManager.enableColorMaterial();
		GlStateManager.enableLighting();
		ir.renderItemAndEffectIntoGUI(stack, x + 1, y + 1);
		ir.renderItemOverlayIntoGUI(parent.getFontRenderer(), stack, x + 1, y + 1, null);
	}

	public void renderToolTip(int mouseX, int mouseY) {
		if (mouseX >= x && mouseX <= x + 18 && mouseY >= y && mouseY <= y + 18) {
			GlStateManager.disableLighting();
			GlStateManager.disableDepth();
			GlStateManager.colorMask(true, true, true, false);
			Gui.drawRect(x + 1, y + 1, x + 17, y + 17, 0x80FFFFFF);
			GlStateManager.colorMask(true, true, true, true);
			((ScreenManualBase) parent).renderToolTip(stack, mouseX, mouseY);
		}
	}
}