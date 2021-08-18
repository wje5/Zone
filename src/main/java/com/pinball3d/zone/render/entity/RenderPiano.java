package com.pinball3d.zone.render.entity;

import com.pinball3d.zone.entity.EntityPiano;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderPiano extends Render<EntityPiano> {
	public static ResourceLocation MODEL = new ResourceLocation("zone:models/entity/grand_piano.obj");

	public RenderPiano(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(EntityPiano entity, double x, double y, double z, float entityYaw, float partialTicks) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.scale(0.05F, 0.05F, 0.05F);
//		ObjHandler.render(ObjHandler.getBakedModel(MODEL));
		GlStateManager.popMatrix();
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityPiano entity) {
		return null;
	}
}
