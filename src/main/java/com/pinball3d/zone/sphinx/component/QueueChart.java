package com.pinball3d.zone.sphinx.component;

import java.util.Set;
import java.util.TreeSet;

import org.lwjgl.opengl.GL11;

import com.pinball3d.zone.gui.IHasComponents;
import com.pinball3d.zone.gui.component.Component;
import com.pinball3d.zone.util.ItemType;
import com.pinball3d.zone.util.Util;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.ItemStack;

public class QueueChart extends Component {
	private ChartNode root;

	public QueueChart(IHasComponents parent, int x, int y, int width, int height, ItemType targetItem) {
		super(parent, x, y, width, height);
		root = new ChartNode(targetItem);
	}

	@Override
	public void doRender(int mouseX, int mouseY) {
		super.doRender(mouseX, mouseY);
		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 0, -800F);
		GlStateManager.enableDepth();
		GlStateManager.depthFunc(GL11.GL_GEQUAL);
		GlStateManager.disableAlpha();
		Gui.drawRect(0, 0, width, height, 0xFF000000);
		GlStateManager.depthFunc(GL11.GL_LEQUAL);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		RenderHelper.enableGUIStandardItemLighting();
		renderItem(root.type, 70, 98, 1);
		GlStateManager.popMatrix();
		GlStateManager.depthFunc(GL11.GL_LEQUAL);
		GlStateManager.disableDepth();
	}

	public void renderItem(ItemType type, int x, int y, int amount) {
		RenderItem ir = mc.getRenderItem();
		ItemStack stack = type.createStack();
		ir.renderItemAndEffectIntoGUI(stack, x, y);
		String text = amount <= 1 ? null : Util.transferString(amount);
		ir.renderItemOverlayIntoGUI(Util.getFontRenderer(), stack, x, y, text);
	}

	public static class ChartNode {
		private ItemType type;
		private Set<ChartNode> childs = new TreeSet<ChartNode>((a, b) -> ItemType.comparator.compare(a.type, b.type));

		public ChartNode(ItemType type) {
			this.type = type;

		}

		public ItemType getType() {
			return type;
		}

		public Set<ChartNode> getChilds() {
			return childs;
		}
	}
}
