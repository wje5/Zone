package com.pinball3d.zone.sphinx.elite.ui.component;

import java.util.function.Supplier;

import com.pinball3d.zone.sphinx.elite.EliteMainwindow;
import com.pinball3d.zone.sphinx.elite.ui.core.Component;
import com.pinball3d.zone.sphinx.elite.ui.core.Subpanel;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class BlockShow extends Component {
	private Supplier<BlockPos> pos;

	public BlockShow(EliteMainwindow parent, Subpanel parentPanel, Supplier<BlockPos> pos) {
		super(parent, parentPanel, 64, 64);
		this.pos = pos;
	}

	public BlockShow(EliteMainwindow parent, Subpanel parentPanel, BlockPos pos) {
		this(parent, parentPanel, () -> pos);
	}

	public BlockPos getBlockPos() {
		return pos.get();
	}

	public void setBlockPos(Supplier<BlockPos> pos) {
		this.pos = pos;
	}

	@Override
	public void doRender(int mouseX, int mouseY, float partialTicks) {
		super.doRender(mouseX, mouseY, partialTicks);
		World world = Minecraft.getMinecraft().world;
		BlockPos pos = getBlockPos();
		if (pos == null) {
			return;
		}
		IBlockState state = world.getBlockState(pos);
		if (state.getRenderType() == EnumBlockRenderType.MODEL) {
			if (state.getRenderType() != EnumBlockRenderType.INVISIBLE) {
				Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
				GlStateManager.pushMatrix();
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder bufferbuilder = tessellator.getBuffer();

				bufferbuilder.begin(7, DefaultVertexFormats.BLOCK);
//				GL11.glDisable(GL11.GL_SCISSOR_TEST);
//				BlockPos blockpos = new BlockPos(entity.posX, entity.getEntityBoundingBox().maxY, entity.posZ);
//				GlStateManager.disableDepth();
//				RenderHelper.enableGUIStandardItemLighting();
				GlStateManager.disableLighting();
				GlStateManager.enableAlpha();

				int scale = 40;

				GlStateManager.rotate(-30F, 1, 0, 0);
				GlStateManager.rotate(225F, 0, 1, 0);

				GlStateManager.translate((-pos.getX()) * scale - 85, (pos.getY()) * scale + 91,
						(0F - pos.getZ()) * scale);
				GlStateManager.scale(scale, -scale, scale);

				BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
				blockrendererdispatcher.getBlockModelRenderer().renderModel(world,
						blockrendererdispatcher.getModelForState(state), state, pos, bufferbuilder, false,
						MathHelper.getPositionRandom(pos));
				tessellator.draw();
				GlStateManager.popMatrix();
			}
		}
	}
}
