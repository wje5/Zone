package com.pinball3d.zone.inventory;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiContainerSphinxSystem extends GuiContainer {
	private static final ResourceLocation TEXTURE = new ResourceLocation(
			"zone:textures/gui/container/sphinx_system.png");

	protected ContainerSphinxSystem container;

	public GuiContainerSphinxSystem(ContainerSphinxSystem container) {
		super(container);
		mc = Minecraft.getMinecraft();
		this.container = container;
		xSize = mc.displayWidth;
		ySize = mc.displayHeight;

	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F);

//		mc.getTextureManager().bindTexture(TEXTURE);
		int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;

//		drawTexturedModalRect(offsetX, offsetY, 0, 0, this.xSize, this.ySize);

	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);

		drawMap();

		this.renderHoveredToolTip(mouseX, mouseY);
	}

	private void drawMap() {
		GlStateManager.pushMatrix();
		GlStateManager.disableTexture2D();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		drawChunk(mc.player.world.getChunkFromBlockCoords(mc.player.getPosition()));
		GlStateManager.popMatrix();
	}

	private void drawChunk(Chunk chunk) {
		int size = 40;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
		for (int x = chunk.x * 16; x < chunk.x * 16 + 16; x++) {
			for (int z = chunk.z * 16; z < chunk.z * 16 + 16; z++) {
				int y = chunk.getHeightValue(x - chunk.x * 16, z - chunk.z * 16) - 1;
				BlockPos pos = new BlockPos(x, y, z);
				IBlockState state = mc.world.getBlockState(pos);
				applyQuad(bufferbuilder, state.getMapColor(mc.world, pos), 100 + x - chunk.x * 16,
						100 + z - chunk.z * 16, size);
			}
		}
		tessellator.draw();
	}

	private void applyQuad(BufferBuilder builder, MapColor color, float x, float y, float size) {
		int r = color.colorValue >> 16 & 255;
		int g = color.colorValue >> 8 & 255;
		int b = color.colorValue & 255;
		builder.pos(x, y, 0.0D).color(r, g, b, 255).endVertex();
		builder.pos(x, y + size, 0.0D).color(r, g, b, 255).endVertex();
		builder.pos(x + size, y + size, 0.0D).color(r, g, b, 255).endVertex();
		builder.pos(x + size, y, 0.0D).color(r, g, b, 255).endVertex();
	}
}
