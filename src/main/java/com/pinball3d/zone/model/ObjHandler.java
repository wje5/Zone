package com.pinball3d.zone.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.obj.OBJLoader;

public class ObjHandler {
	private static Map<ResourceLocation, IBakedModel> models = new HashMap<ResourceLocation, IBakedModel>();

	public static IBakedModel getBakedModel(ResourceLocation location) {
		IBakedModel bakedModel = models.get(location);
		if (bakedModel == null) {
			long time = System.nanoTime();
			try {
				IModel model = OBJLoader.INSTANCE.loadModel(location);
				bakedModel = model.bake(model.getDefaultState(), DefaultVertexFormats.ITEM,
						l -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(l.toString()));
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("############## BAKE:" + (System.nanoTime() - time) + " ##############");
		}
		models.put(location, bakedModel);
		return bakedModel;
	}

	public static void render(IBakedModel model) {
		for (EnumFacing enumfacing : EnumFacing.values()) {
			renderQuads(model.getQuads(null, enumfacing, 0));
		}
		renderQuads(model.getQuads(null, null, 0));
	}

	public static void renderQuads(List<BakedQuad> listQuads) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();

		int i = 0;
		for (int j = listQuads.size(); i < j; ++i) {
			BakedQuad bakedquad = listQuads.get(i);
			bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);
			bufferbuilder.addVertexData(bakedquad.getVertexData());

//			if (bakedquad.hasTintIndex()) {
//				bufferbuilder.putColorRGB_F4(red * brightness, green * brightness, blue * brightness);
//			} else {
//				bufferbuilder.putColorRGB_F4(brightness, brightness, brightness);
//			}

			Vec3i vec3i = bakedquad.getFace().getDirectionVec();
			bufferbuilder.putNormal(vec3i.getX(), vec3i.getY(), vec3i.getZ());
			tessellator.draw();
		}
	}
}
