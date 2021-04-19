package com.pinball3d.zone.gui.component;

import com.pinball3d.zone.gui.GuiContainerZone;
import com.pinball3d.zone.gui.IHasComponents;
import com.pinball3d.zone.util.Util;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.ItemStack;

public class ItemShow extends Component {
	protected ItemStack stack;
	protected int amount;

	public ItemShow(IHasComponents parent, int x, int y, ItemStack stack) {
		this(parent, x, y, stack, stack.getCount());
	}

	public ItemShow(IHasComponents parent, int x, int y, ItemStack stack, int amount) {
		super(parent, x, y, 16, 16);
		this.stack = stack;
		this.amount = amount;
	}

	@Override
	public void doRender(int mouseX, int mouseY) {
		super.doRender(mouseX, mouseY);
		Util.resetOpenGl();
		RenderItem ir = mc.getRenderItem();
		RenderHelper.enableGUIStandardItemLighting();
		ir.renderItemAndEffectIntoGUI(stack, 0, 0);
		String text = amount <= 1 ? null : Util.transferString(amount);
		if (text != null) {
			FontRenderer fr = Util.getFontRenderer();
			GlStateManager.pushMatrix();
			GlStateManager.translate(0, 0, 400F);
			fr.drawStringWithShadow(text, 17 - fr.getStringWidth(text), 9, 0xFFFFFF);
			GlStateManager.popMatrix();
		}
		if (mouseX >= 0 && mouseX <= width && mouseY >= 0 && mouseY <= width) {
			GlStateManager.pushMatrix();
			Util.resetOpenGl();
			if (mouseY < 16) {
				int yOffset = 16 - mouseY;
				mouseY = 16;
				GlStateManager.translate(0.0F, -yOffset, 0.0F);
			}
			((GuiContainerZone) Util.getRoot()).renderToolTip(stack, mouseX, mouseY);
			GlStateManager.popMatrix();
		}
	}

	@Override
	public boolean getRenderLast(int mouseX, int mouseY) {
		return mouseX >= 0 && mouseX <= width && mouseY >= 0 && mouseY <= width;
	}
}
