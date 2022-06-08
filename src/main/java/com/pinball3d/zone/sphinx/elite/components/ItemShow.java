package com.pinball3d.zone.sphinx.elite.components;

import java.util.function.Supplier;

import com.pinball3d.zone.sphinx.elite.Component;
import com.pinball3d.zone.sphinx.elite.EliteMainwindow;
import com.pinball3d.zone.sphinx.elite.Subpanel;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.ItemStack;

public class ItemShow extends Component {
	private Supplier<ItemStack> stack;

	public ItemShow(EliteMainwindow parent, Subpanel parentPanel, Supplier<ItemStack> stack) {
		super(parent, parentPanel, 64, 64);
		this.stack = stack;
	}

	public ItemShow(EliteMainwindow parent, Subpanel parentPanel, ItemStack stack) {
		this(parent, parentPanel, () -> stack);
	}

	public ItemStack getStack() {
		return stack.get();
	}

	public void setStack(Supplier<ItemStack> stack) {
		this.stack = stack;
	}

	@Override
	public void doRender(int mouseX, int mouseY, float partialTicks) {
		super.doRender(mouseX, mouseY, partialTicks);
		RenderItem ir = Minecraft.getMinecraft().getRenderItem();
		RenderHelper.enableGUIStandardItemLighting();
		GlStateManager.pushMatrix();
		GlStateManager.scale(4, 4, 1);
		ir.renderItemAndEffectIntoGUI(stack.get(), 0, 0);
		GlStateManager.popMatrix();
	}
}
