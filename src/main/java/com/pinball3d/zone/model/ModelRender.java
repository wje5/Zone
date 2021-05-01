package com.pinball3d.zone.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.client.model.pipeline.LightUtil;

public class ModelRender {
	public static void renderModel(Model model, int color) {
		float a = (color >> 24 & 255) / 255.0F;
		float r = (color >> 16 & 255) / 255.0F;
		float g = (color >> 8 & 255) / 255.0F;
		float b = (color & 255) / 255.0F;
		renderModel(model, r, g, b, a);
	}

	public static void renderModel(Model model, float r, float g, float b, float a) {
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
				GlStateManager.DestFactor.ZERO);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		if (!model.triangles.isEmpty()) {
			bufferbuilder.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_COLOR);
			Iterator<Integer> it = model.triangles.iterator();
			while (it.hasNext()) {
				int v1 = it.next(), v2 = it.next(), v3 = it.next();
				float x1 = model.vertices.get(v1 * 3), y1 = model.vertices.get(v1 * 3 + 1),
						z1 = model.vertices.get(v1 * 3 + 2);
				float x2 = model.vertices.get(v2 * 3), y2 = model.vertices.get(v2 * 3 + 1),
						z2 = model.vertices.get(v2 * 3 + 2);
				float x3 = model.vertices.get(v3 * 3), y3 = model.vertices.get(v3 * 3 + 1),
						z3 = model.vertices.get(v3 * 3 + 2);
				float xCenter = (Math.min(Math.min(x1, x2), x3) + Math.max(Math.max(x1, x2), x3)) / 2;
				float yCenter = (Math.min(Math.min(y1, y2), y3) + Math.max(Math.max(y1, y2), y3)) / 2;
				float zCenter = (Math.min(Math.min(z1, z2), z3) + Math.max(Math.max(z1, z2), z3)) / 2;
				float light = 1 - LightUtil.diffuseLight(xCenter, yCenter, zCenter);
				float lr = r * light, lg = g * light, lb = b * light;
				bufferbuilder.pos(x1, y1, z1).color(lr, lg, lb, a).endVertex();
				bufferbuilder.pos(x2, y2, z2).color(lr, lg, lb, a).endVertex();
				bufferbuilder.pos(x3, y3, z3).color(lr, lg, lb, a).endVertex();
			}
			tessellator.draw();
		}
		if (!model.quads.isEmpty()) {
			bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
			Iterator<Integer> it = model.quads.iterator();
			while (it.hasNext()) {
				int v1 = it.next(), v2 = it.next(), v3 = it.next(), v4 = it.next();
				float x1 = model.vertices.get(v1 * 3), y1 = model.vertices.get(v1 * 3 + 1),
						z1 = model.vertices.get(v1 * 3 + 2);
				float x2 = model.vertices.get(v2 * 3), y2 = model.vertices.get(v2 * 3 + 1),
						z2 = model.vertices.get(v2 * 3 + 2);
				float x3 = model.vertices.get(v3 * 3), y3 = model.vertices.get(v3 * 3 + 1),
						z3 = model.vertices.get(v3 * 3 + 2);
				float x4 = model.vertices.get(v4 * 3), y4 = model.vertices.get(v4 * 3 + 1),
						z4 = model.vertices.get(v4 * 3 + 2);
				float xCenter = (Math.min(Math.min(x1, x2), Math.min(x3, x4))
						+ Math.max(Math.max(x1, x2), Math.max(x3, x4))) / 2;
				float yCenter = (Math.min(Math.min(y1, y2), Math.min(y3, y4))
						+ Math.max(Math.max(y1, y2), Math.max(y3, y4))) / 2;
				float zCenter = (Math.min(Math.min(z1, z2), Math.min(z3, z4))
						+ Math.max(Math.max(z1, z2), Math.max(z3, z4))) / 2;
				float light = 1 - LightUtil.diffuseLight(xCenter, yCenter, zCenter);
				float lr = r * light, lg = g * light, lb = b * light;
				bufferbuilder.pos(x1, y1, z1).color(lr, lg, lb, a).endVertex();
				bufferbuilder.pos(x2, y2, z2).color(lr, lg, lb, a).endVertex();
				bufferbuilder.pos(x3, y3, z3).color(lr, lg, lb, a).endVertex();
				bufferbuilder.pos(x4, y4, z4).color(lr, lg, lb, a).endVertex();
			}
			tessellator.draw();
		}
		if (!model.polygons.isEmpty()) {
			Iterator<List<Integer>> it = model.polygons.iterator();
			while (it.hasNext()) {
				bufferbuilder.begin(GL11.GL_POLYGON, DefaultVertexFormats.POSITION_TEX);
				List<Integer> l = it.next();
				float[] x = new float[l.size()];
				float[] y = new float[l.size()];
				float[] z = new float[l.size()];
				float xMin = Float.MAX_VALUE, yMin = Float.MAX_VALUE, zMin = Float.MAX_VALUE, xMax = -Float.MAX_VALUE,
						yMax = -Float.MAX_VALUE, zMax = -Float.MAX_VALUE;
				for (int i = 0; i < l.size(); i++) {
					int j = l.get(i);
					x[i] = model.vertices.get(j * 3);
					if (x[i] < xMin) {
						xMin = x[i];
					}
					if (x[i] > xMax) {
						xMax = x[i];
					}
					y[i] = model.vertices.get(j * 3 + 1);
					if (y[i] < yMin) {
						yMin = y[i];
					}
					if (y[i] > yMax) {
						yMax = y[i];
					}
					z[i] = model.vertices.get(j * 3 + 2);
					if (z[i] < zMin) {
						zMin = z[i];
					}
					if (z[i] > zMax) {
						zMax = z[i];
					}
				}
				float light = 1 - LightUtil.diffuseLight((xMin + xMax) / 2, (yMin + yMax) / 2, (zMin + zMax) / 2);
				float lr = r * light, lg = g * light, lb = b * light;
				for (int i = 0; i < x.length; i++) {
					bufferbuilder.pos(x[i], y[i], z[i]).color(lr, lg, lb, a).endVertex();
				}
				tessellator.draw();
			}
		}
		GlStateManager.enableTexture2D();
	}

	public static class Model {
		private List<Float> vertices = new ArrayList<Float>();
		private List<Integer> triangles = new ArrayList<Integer>();
		private List<Integer> quads = new ArrayList<Integer>();
		private List<List<Integer>> polygons = new ArrayList<List<Integer>>();

		public Model() {

		}

		private int getPoint(float x1, float y1, float z1) {
			int index = 0;
			Iterator<Float> it = vertices.iterator();
			while (it.hasNext()) {
				if (x1 == it.next() & y1 == it.next() & z1 == it.next()) {
					return index;
				}
				index++;
			}
			vertices.add(x1);
			vertices.add(y1);
			vertices.add(z1);
			return index;
		}

		public void addTriangle(float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3,
				float z3) {
			int point1 = getPoint(x1, y1, z1);
			int point2 = getPoint(x2, y2, z2);
			int point3 = getPoint(x3, y3, z3);
			triangles.add(point1);
			triangles.add(point2);
			triangles.add(point3);
		}

		public void addQuad(float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3,
				float x4, float y4, float z4) {
			int point1 = getPoint(x1, y1, z1);
			int point2 = getPoint(x2, y2, z2);
			int point3 = getPoint(x3, y3, z3);
			int point4 = getPoint(x4, y4, z4);
			quads.add(point1);
			quads.add(point2);
			quads.add(point3);
			quads.add(point4);
		}

		public void addPolygon(float... fs) {
			List<Integer> list = new ArrayList<Integer>(fs.length / 3);
			for (int i = 0; i < fs.length / 3; i++) {
				list.add(getPoint(fs[i * 3], fs[i * 3 + 1], fs[i * 3 + 2]));
			}
			polygons.add(list);
		}
	}
}
