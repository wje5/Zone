package com.pinball3d.zone.manual;

import com.pinball3d.zone.sphinx.Component;
import com.pinball3d.zone.sphinx.IParent;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.ItemStack;

public class BlockShow extends Component {
	private float scale;
	private ItemStack stack;

	public BlockShow(IParent parent, int x, int y, float scale, ItemStack stack) {
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
		GlStateManager.translate(x, y, 0);
		GlStateManager.scale(scale, scale, scale);
		ir.renderItemAndEffectIntoGUI(stack, 0, 0);
		GlStateManager.popMatrix();
	}
}
