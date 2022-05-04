package com.pinball3d.zone.render;

import com.pinball3d.zone.tileentity.TECableBasic;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TESRCableBasic extends TileEntitySpecialRenderer<TECableBasic> {
	private BlockRendererDispatcher blockRenderer;

	@Override
	@SuppressWarnings("deprecation")
	public void render(TECableBasic te, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha) {

		if (blockRenderer == null)
			blockRenderer = Minecraft.getMinecraft().getBlockRendererDispatcher(); // Forge: Delay this from constructor
																					// to allow us to change it later
		BlockPos blockpos = te.getPos();
		World world = getWorld();
		IBlockState iblockstate = world.getBlockState(blockpos);
		Block block = iblockstate.getBlock();

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		RenderHelper.disableStandardItemLighting();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.enableBlend();
		GlStateManager.disableCull();

		if (Minecraft.isAmbientOcclusionEnabled()) {
			GlStateManager.shadeModel(7425);
		} else {
			GlStateManager.shadeModel(7424);
		}

		bufferbuilder.begin(7, DefaultVertexFormats.BLOCK);
		bufferbuilder.setTranslation(x - blockpos.getX(), y - blockpos.getY(), z - blockpos.getZ());

		renderStateModel(blockpos, block.getActualState(iblockstate, world, blockpos), bufferbuilder, world, false);

		bufferbuilder.setTranslation(0.0D, 0.0D, 0.0D);
		tessellator.draw();
		RenderHelper.enableStandardItemLighting();
	}

	private boolean renderStateModel(BlockPos pos, IBlockState state, BufferBuilder buffer, World p_188186_4_,
			boolean checkSides) {
		return this.blockRenderer.getBlockModelRenderer().renderModel(p_188186_4_,
				this.blockRenderer.getModelForState(state), state, pos, buffer, checkSides);
	}
}
