package com.pinball3d.zone.sphinx.component;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.IntBuffer;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.pinball3d.zone.Zone;
import com.pinball3d.zone.block.BlockLoader;
import com.pinball3d.zone.gui.IHasComponents;
import com.pinball3d.zone.gui.component.Component;
import com.pinball3d.zone.item.ItemLoader;
import com.pinball3d.zone.sphinx.container.GuiContainerSphinxAdvanced;
import com.pinball3d.zone.sphinx.subscreen.SubscreenMessageBox;
import com.pinball3d.zone.util.ItemType;
import com.pinball3d.zone.util.Util;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class QueueChart extends Component {
	public static final ResourceLocation CURVE = new ResourceLocation("zone:textures/gui/sphinx/curve.png");
	public static final ResourceLocation CURVE_2 = new ResourceLocation("zone:textures/gui/sphinx/curve_2.png");
	public static final ResourceLocation LINE = new ResourceLocation("zone:textures/gui/sphinx/line.png");
	private ChartNode root;
	private int xOffset, yOffset;
	private float scale = 1.0F;

	public QueueChart(IHasComponents parent, int x, int y, int width, int height, ItemType targetItem) {
		super(parent, x, y, width, height);
		root = new ChartNode(targetItem);
		Set<ChartNode> set = new HashSet<ChartNode>();
		Set<ChartNode> set2 = new HashSet<ChartNode>();
		Set<ChartNode> set3 = new HashSet<ChartNode>();
		Set<ChartNode> set4 = new HashSet<ChartNode>();
		Set<ChartNode> set5 = new HashSet<ChartNode>();
		Set<ChartNode> set6 = new HashSet<ChartNode>();
		Set<ChartNode> set7 = new HashSet<ChartNode>();
		Set<ChartNode> set8 = new HashSet<ChartNode>();
		Set<ChartNode> set9 = new HashSet<ChartNode>();
		Set<ChartNode> set10 = new HashSet<ChartNode>();
		Set<ChartNode> set11 = new HashSet<ChartNode>();
		Set<ChartNode> set12 = new HashSet<ChartNode>();
		Set<ChartNode> set13 = new HashSet<ChartNode>();
		Set<ChartNode> set14 = new HashSet<ChartNode>();
		Set<ChartNode> set15 = new HashSet<ChartNode>();
		Set<ChartNode> set16 = new HashSet<ChartNode>();
		Set<ChartNode> set17 = new HashSet<ChartNode>();
		Set<ChartNode> set18 = new HashSet<ChartNode>();

		set18.add(new ChartNode(new ItemType(ItemLoader.iron_dust), 8));
		set18.add(new ChartNode(new ItemType(ItemLoader.gold_dust), 8));
		set18.add(new ChartNode(new ItemType(ItemLoader.quartz_dust), 8));

		set17.add(new ChartNode(new ItemType(ItemLoader.iron_dust), 8));
		set17.add(new ChartNode(new ItemType(ItemLoader.gold_dust), 8));
		set17.add(new ChartNode(new ItemType(ItemLoader.quartz_dust), 8));

		set16.add(new ChartNode(new ItemType(Items.IRON_NUGGET), 4));

		set15.add(new ChartNode(new ItemType(Items.IRON_NUGGET), 4));

		set14.add(new ChartNode(new ItemType(ItemLoader.etherium), 8).setChilds(set18));

		set13.add(new ChartNode(new ItemType(ItemLoader.etherium), 8).setChilds(set17));

		set12.add(new ChartNode(new ItemType(ItemLoader.etherium_plate), 4).setChilds(set14));
		set12.add(new ChartNode(new ItemType(ItemLoader.rivet), 4).setChilds(set16));

		set11.add(new ChartNode(new ItemType(ItemLoader.etherium_plate), 4).setChilds(set13));
		set11.add(new ChartNode(new ItemType(ItemLoader.rivet), 4).setChilds(set15));

		set10.add(new ChartNode(new ItemType(Blocks.PLANKS), 2));

		set9.add(new ChartNode(new ItemType(Items.GOLD_NUGGET), 8));
		set9.add(new ChartNode(new ItemType(ItemLoader.etherium_plate_riveted), 4).setChilds(set12));
		set9.add(new ChartNode(new ItemType(Items.REDSTONE), 4));
		set9.add(new ChartNode(new ItemType(Items.PRISMARINE_SHARD), 2));

		set8.add(new ChartNode(new ItemType(Items.IRON_INGOT), 6));

		set7.add(new ChartNode(new ItemType(Items.STICK), 2).setChilds(set10));
		set7.add(new ChartNode(new ItemType(ItemLoader.iron_plate), 3).setChilds(set8));

		set6.add(new ChartNode(new ItemType(Items.QUARTZ), 4));

		set5.add(new ChartNode(new ItemType(ItemLoader.saw)).setChilds(set7));
		set5.add(new ChartNode(new ItemType(Blocks.QUARTZ_BLOCK)).setChilds(set6));

		set2.add(new ChartNode(new ItemType(Items.GOLD_NUGGET), 6));
		set2.add(new ChartNode(new ItemType(Items.DYE, 4), 2));
		set2.add(new ChartNode(new ItemType(ItemLoader.quartz_circuit_board), 2).setChilds(set5));

		set4.add(new ChartNode(new ItemType(BlockLoader.clarity_glass), 6));

		set3.add(new ChartNode(new ItemType(BlockLoader.clarity_glass_pane), 5).setChilds(set4));
		set3.add(new ChartNode(new ItemType(Items.QUARTZ), 2));

		set.add(new ChartNode(new ItemType(ItemLoader.etherium_plate_riveted), 4).setChilds(set11));
		set.add(new ChartNode(new ItemType(ItemLoader.transistor), 2).setChilds(set3));
		set.add(new ChartNode(new ItemType(ItemLoader.processing_unit), 2).setChilds(set9));
		set.add(new ChartNode(new ItemType(ItemLoader.chip)).setChilds(set2));
		root.setChilds(set);

		xOffset = 5;
		yOffset = height / 2 - 8;
	}

	@Override
	public void doRender(int mouseX, int mouseY) {
		super.doRender(mouseX, mouseY);
		renderChart(mouseX, mouseY, false);
	}

	public void renderChart(int mouseX, int mouseY, boolean flag) {
		GlStateManager.pushMatrix();
		if (!flag) {
			GlStateManager.translate(0, 0, -800F);
			GlStateManager.enableDepth();
			GlStateManager.depthFunc(GL11.GL_GEQUAL);
			GlStateManager.disableAlpha();
			Gui.drawRect(0, 0, width, height, 0x651CC3B5);
			GlStateManager.depthFunc(GL11.GL_LEQUAL);
			GlStateManager.translate(xOffset, yOffset, 0);
			if (mouseX < 0 || mouseX > width || mouseY < 0 || mouseY > height) {
				mouseX = Integer.MIN_VALUE;
			}
			GlStateManager.scale(scale, scale, 1.0F);
		}
		root.doRender(0, 0, 0, 0, (int) ((mouseX - xOffset) / scale), (int) ((mouseY - yOffset) / scale));
		GlStateManager.popMatrix();
		GlStateManager.depthFunc(GL11.GL_LEQUAL);
		GlStateManager.disableDepth();
	}

	public void exportCurve() {
		try {
			int width = ((root.getDepth() - 1) * 48 + 16) * 4 + 64;
			int height = (root.getHeight()) * 4 + 64;
			boolean flag = OpenGlHelper.isFramebufferEnabled();
			Framebuffer buffer = new Framebuffer(width, height, false);
			buffer.bindFramebuffer(true);

			GlStateManager.pushMatrix();
			GlStateManager.clear(0xFF);
			GlStateManager.matrixMode(GL11.GL_PROJECTION);
			GlStateManager.enableColorMaterial();
			GlStateManager.loadIdentity();
			GlStateManager.ortho(0, width, height, 0, 1000.0D, 3000.0D);
			GlStateManager.matrixMode(GL11.GL_MODELVIEW);
			GlStateManager.loadIdentity();
			GlStateManager.translate(0.0F, 0.0F, -2000.0F);
			GlStateManager.glLineWidth(1.0F);
			GlStateManager.disableTexture2D();

			Gui.drawRect(0, 0, width, height, 0xFF064F4F);
			Gui.drawRect(0, 0, width, height, 0x651CC3B5);
			GlStateManager.translate(32, height / 2 - 32, 0.0F);
			GlStateManager.scale(4.0F, 4.0F, 1.0F);
			renderChart(0, 0, true);
			GlStateManager.popMatrix();

			int i = width * height;
			IntBuffer pixelBuffer = BufferUtils.createIntBuffer(i);
			int[] pixelValues = new int[i];
			GlStateManager.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
			GlStateManager.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
			pixelBuffer.clear();
			if (flag) {
				GlStateManager.bindTexture(buffer.framebufferTexture);
				GlStateManager.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV,
						pixelBuffer);
			} else {
				GlStateManager.glReadPixels(0, 0, width, height, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV,
						pixelBuffer);
			}
			pixelBuffer.get(pixelValues);
			TextureUtil.processPixelValues(pixelValues, width, height);
			BufferedImage bufferedimage = new BufferedImage(width, height, 1);
			bufferedimage.setRGB(0, 0, width, height, pixelValues, 0, width);
			File file1 = new File(mc.mcDataDir, "screenshots");
			file1.mkdir();
			File file2 = new File(file1, root.type.createStack().getDisplayName() + ".png");
			file2 = file2.getCanonicalFile();
			ImageIO.write(bufferedimage, "png", file2);
			Util.getRoot().putScreen(new SubscreenMessageBox(Util.getRoot(), I18n.format("sphinx.export_success"),
					Util.formatAndAntiEscape("screenshot.success", file2.getName())));
		} catch (Exception exception) {
			Zone.logger.warn("Couldn't save screenshot", exception);
		}
	}

	public static void renderItem(ItemType type, int x, int y, int amount) {
		Util.resetOpenGl();
		RenderItem ir = mc.getRenderItem();
		RenderHelper.enableGUIStandardItemLighting();
		ItemStack stack = type.createStack();
		ir.renderItemAndEffectIntoGUI(stack, x, y);
		String text = amount <= 1 ? null : Util.transferString(amount);
		if (text != null) {
			FontRenderer fr = Util.getFontRenderer();
			GlStateManager.pushMatrix();
			GlStateManager.translate(0, 0, 400F);
			fr.drawStringWithShadow(text, x + 17 - fr.getStringWidth(text), y + 9, 0xFFFFFF);
			GlStateManager.popMatrix();
		}
	}

	// width MUST great than 24
	public static void drawCurve(int x, int y, int width, int height) {
		Util.resetOpenGl();
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
		int w = (width - 24) / 2;
		for (double i = -Math.PI / 2; i < Math.PI / 2; i += Math.PI / 60) {
			bufferbuilder.pos(x + w + (i / Math.PI + 0.5F) * 24, y + (Math.sin(i) + 1.0F) / 2 * height, 0.0F)
					.endVertex();
		}
		bufferbuilder.pos(x + width, y + height, 0.0F).endVertex();
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

	@Override
	public boolean onMouseScroll(int mouseX, int mouseY, boolean isUp) {
		if (super.onMouseScroll(mouseX, mouseY, isUp)) {
			return true;
		}
		if (isUp) {
			scale = scale - 0.1F <= 0.0F ? 0.1F : scale - 0.1F;
		} else {
			scale = scale - 0.1F >= 1.0F ? 1.0F : scale + 0.1F;
		}
		return true;
	}

	@Override
	public boolean getRenderLast(int mouseX, int mouseY) {
		return true;
	}

	public ChartNode getRoot() {
		return root;
	}

	public static class ChartNode {
		private ItemType type;
		private Set<ChartNode> childs = new TreeSet<ChartNode>((a, b) -> ItemType.comparator.compare(a.type, b.type));
		private Set<ChartNode> outgrowth = new TreeSet<ChartNode>(
				(a, b) -> ItemType.comparator.compare(a.type, b.type));
		private int amount;

		public ChartNode(ItemType type) {
			this(type, 1);
		}

		public ChartNode(ItemType type, int amount) {
			this.type = type;
			this.amount = amount;
		}

		public static int getCenter(int[] a) {
			if (a.length == 0) {
				return 0;
			}
			int index = -1;
			int l = 0;
			int r = 0;
			do {
				index++;
				l = 0;
				r = 0;
				for (int i = 0; i < a.length; i++) {
					if (i <= index) {
						l += a[i];
					} else {
						r += a[i];
					}
				}
			} while (r > l);
			return index;
		}

		public void doRender(int x, int y, int parentX, int parentY, int mouseX, int mouseY) {
			boolean isHovered = mouseX >= x && mouseX <= x + 16 && mouseY >= y && mouseY <= y + 16;
			renderItem(type, x, y, amount);
			if (x != 0 || y != 0) {
				drawCurve(parentX + 16, parentY + 8, x - parentX - 16, y - parentY);
			}
			if (!childs.isEmpty()) {
				int[] heights = new int[childs.size()];
				int totalHeight = (heights.length - 1) * 24;
				Iterator<ChartNode> it = childs.iterator();
				for (int i = 0; i < heights.length; i++) {
					int h = it.next().getHeight();
					totalHeight += h;
					heights[i] = h;
				}
				int renderY = -totalHeight / 2;
				it = childs.iterator();
				for (int i = 0; i < heights.length; i++) {
					it.next().doRender(x + 48, y + renderY + heights[i] / 2, x, y, mouseX, mouseY);
					renderY = renderY + heights[i] + 24;
				}
			}
			if (isHovered) {
				GlStateManager.disableLighting();
				GlStateManager.pushMatrix();
				GlStateManager.translate(0, 0, 400F);
				Gui.drawRect(x, y, x + 16, y + 16, 0x80FFFFFF);
				GlStateManager.popMatrix();
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

		public int getHeight() {
			if (!childs.isEmpty()) {
				int totalHeight = (childs.size() - 1) * 24;
				for (ChartNode c : childs) {
					totalHeight += c.getHeight();
				}
				return totalHeight;
			}
			return 16;
		}

		public int getDepth() {
			if (childs.isEmpty()) {
				return 1;
			}
			int depth = 0;
			for (ChartNode n : childs) {
				int d = n.getDepth();
				if (d > depth) {
					depth = d;
				}
			}
			return depth + 1;
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

		public ChartNode setChilds(Set<ChartNode> childs) {
			this.childs.clear();
			this.childs.addAll(childs);
			return this;
		}

		public ChartNode setOutgrowth(Set<ChartNode> outgrowth) {
			this.outgrowth.clear();
			this.outgrowth.addAll(outgrowth);
			return this;
		}

		public int getAmount() {
			return amount;
		}

		public void setAmount(int amount) {
			this.amount = amount;
		}
	}
}
