package com.pinball3d.zone.sphinx.component;

import java.util.Set;
import java.util.TreeSet;

import org.lwjgl.opengl.GL11;

import com.pinball3d.zone.gui.IHasComponents;
import com.pinball3d.zone.gui.component.Component;
import com.pinball3d.zone.sphinx.container.GuiContainerSphinxAdvanced;
import com.pinball3d.zone.util.ItemType;
import com.pinball3d.zone.util.Util;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class QueueChart extends Component {
	public static final ResourceLocation CURVE = new ResourceLocation("zone:textures/gui/sphinx/curve.png");
	public static final ResourceLocation CURVE_2 = new ResourceLocation("zone:textures/gui/sphinx/curve_2.png");
	public static final ResourceLocation LINE = new ResourceLocation("zone:textures/gui/sphinx/line.png");
	private ChartNode root;
	private int xOffset, yOffset;

	public QueueChart(IHasComponents parent, int x, int y, int width, int height, ItemType targetItem) {
		super(parent, x, y, width, height);
		root = new ChartNode(targetItem);
		xOffset = 5;
		yOffset = height / 2 - 8;
	}

	@Override
	public void doRender(int mouseX, int mouseY) {
		super.doRender(mouseX, mouseY);
		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 0, -800F);
		GlStateManager.enableDepth();
		GlStateManager.depthFunc(GL11.GL_GEQUAL);
		GlStateManager.disableAlpha();
		Gui.drawRect(0, 0, width, height, 0x651CC3B5);
		GlStateManager.depthFunc(GL11.GL_LEQUAL);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		RenderHelper.enableGUIStandardItemLighting();
		GlStateManager.translate(xOffset, yOffset, 0);
		root.doRender(0, 0, mouseX - xOffset, mouseY - yOffset);
		GlStateManager.popMatrix();
		GlStateManager.depthFunc(GL11.GL_LEQUAL);
		GlStateManager.disableDepth();
	}

	public static void renderItem(ItemType type, int x, int y, int amount) {
		RenderItem ir = mc.getRenderItem();
		ItemStack stack = type.createStack();
		ir.renderItemAndEffectIntoGUI(stack, x, y);
		String text = amount <= 1 ? null : Util.transferString(amount);
		ir.renderItemOverlayIntoGUI(Util.getFontRenderer(), stack, x, y, text);
		drawCurve(x + 16, y + 8, 24, 32);
		drawLine(x + 16, y + 8, 24);
	}

	public static void drawCurve(int x, int y, int width, int height) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
				GlStateManager.DestFactor.ZERO);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		bufferbuilder.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);
		for (float i = -2.0F; i < 2.0F; i += 0.05F) {
			bufferbuilder.pos(x + (i + 2.0F) / 4.0F * width, y + (Math.tanh(i) + 1.0F) / 2 * height, 0.0F).endVertex();
		}
		tessellator.draw();
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
	}

	public static void drawLine(int x, int y, int width) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
				GlStateManager.DestFactor.ZERO);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		bufferbuilder.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);
		bufferbuilder.pos(x, y, 0.0F).endVertex();
		bufferbuilder.pos(x + width, y, 0.0F).endVertex();
		tessellator.draw();
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
	}

	@Override
	public boolean onDrag(int mouseX, int mouseY, int moveX, int moveY) {
		if (super.onDrag(mouseX, mouseY, moveX, moveY)) {
			return true;
		}
		xOffset += moveX;
		yOffset += moveY;
		return true;
	}

	public static class ChartNode {
		private ItemType type;
		private Set<ChartNode> childs = new TreeSet<ChartNode>((a, b) -> ItemType.comparator.compare(a.type, b.type));
		private Set<ChartNode> outgrowth = new TreeSet<ChartNode>(
				(a, b) -> ItemType.comparator.compare(a.type, b.type));

		public ChartNode(ItemType type) {
			this.type = type;

		}

		public void doRender(int x, int y, int mouseX, int mouseY) {
			boolean isHovered = mouseX >= x && mouseX <= x + 16 && mouseY >= y && mouseY <= y + 16;
			renderItem(type, x, y, 1);
			if (isHovered) {
				GlStateManager.disableLighting();
				GlStateManager.pushMatrix();
				GlStateManager.translate(0, 0, 400F);
				Gui.drawRect(x, y, x + 16, y + 16, 0x80FFFFFF);
				GlStateManager.popMatrix();
				GlStateManager.enableLighting();
				GlStateManager.enableDepth();
				GlStateManager.pushMatrix();
				if (mouseY < 16) {
					int yOffset = 16 - mouseY;
					mouseY = 16;
					GlStateManager.translate(0.0F, -yOffset, 0.0F);
				}
				((GuiContainerSphinxAdvanced) Util.getRoot()).renderToolTip(type.createStack(), mouseX, mouseY);
				GlStateManager.popMatrix();
			}
		}

		public ItemType getType() {
			return type;
		}

		public Set<ChartNode> getChilds() {
			return childs;
		}

		public Set<ChartNode> getOutgrowth() {
			return outgrowth;
		}
	}
}
