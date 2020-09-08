package com.pinball3d.zone.render;

import org.lwjgl.opengl.GL11;

import com.pinball3d.zone.tileentity.TECrucible;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class RenderCrucible extends TileEntitySpecialRenderer<TECrucible> {
	private ModelCrucible model = new ModelCrucible();
	private ModelCrucibleFilled model2 = new ModelCrucibleFilled();

	@Override
	public void render(TECrucible tileentity, double x, double y, double z, float tick, int destroyStage, float alpha) {
		GL11.glPushMatrix();
		GL11.glTranslated(x + 0.5D, y, z + 0.5D);
		GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
		TECrucible te = tileentity;
		int meta = te.getBlockMetadata();
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
			bindTexture(new ResourceLocation("zone:textures/blocks/crucible.png"));
			model.setRotationAngle(0, (float) rot, 0);
			model.render((Entity) null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
		} else {
			bindTexture(new ResourceLocation("zone:textures/blocks/crucible_filled.png"));
			model2.setRotationAngle(0, (float) rot, 0);
			model2.render((Entity) null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
		}
		if (meta % 8 > 3) {
//			RenderItem.renderInFrame = true;
			GL11.glPushMatrix();
			GL11.glTranslatef(0.0F, -0.5F, 0.0F);
			GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
			Minecraft.getMinecraft().getRenderItem().renderItem(new ItemStack(Blocks.IRON_ORE), TransformType.FIXED);
			GL11.glPopMatrix();
//			RenderItem.renderInFrame = false;
		}
		GL11.glPopMatrix();
	}
}
