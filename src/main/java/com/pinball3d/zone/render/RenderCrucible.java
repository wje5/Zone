package com.pinball3d.zone.render;

import com.pinball3d.zone.tileentity.TECrucible;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class RenderCrucible extends TileEntitySpecialRenderer<TECrucible> {
	private static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/blocks/crucible.png");
	private static final ResourceLocation TEXTURE2 = new ResourceLocation("zone:textures/blocks/crucible_filled.png");
	private ModelCrucible model = new ModelCrucible();
	private ModelCrucibleFilled model2 = new ModelCrucibleFilled();

	public void render(int meta, double x, double y, double z, float tick, int destroyStage, float alpha) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(x + 0.5F, y, z + 0.5F);
		GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
		double rot = 0;
		switch (meta % 4) {
		case 0:
			break;
		case 1:
			rot = Math.PI / 2;
			break;
		case 2:
			rot = Math.PI;
			break;
		case 3:
			rot = -Math.PI / 2;
		}
		if (meta < 8) {
			bindTexture(TEXTURE);
			model.setRotationAngle(0, (float) rot, 0);
			model.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
		} else {
			bindTexture(TEXTURE2);
			model2.setRotationAngle(0, (float) rot, 0);
			model2.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
		}
		if (meta % 8 > 3) {
			GlStateManager.translate(0.0F, -0.5F, 0.0F);
			GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
			Minecraft.getMinecraft().getRenderItem().renderItem(new ItemStack(Blocks.IRON_ORE), TransformType.FIXED);
		}
		GlStateManager.popMatrix();
	}

	@Override
	public void render(TECrucible te, double x, double y, double z, float tick, int destroyStage, float alpha) {
		render(te.getBlockMetadata(), x, y, z, tick, destroyStage, alpha);
	}
}
