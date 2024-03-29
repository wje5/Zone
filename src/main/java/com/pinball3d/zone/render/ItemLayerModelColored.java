package com.pinball3d.zone.render;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Optional;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.BakedItemModel;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ItemLayerModel;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.client.model.pipeline.IVertexConsumer;
import net.minecraftforge.client.model.pipeline.TRSRTransformer;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

public final class ItemLayerModelColored implements IModel {
	public static final ItemLayerModelColored INSTANCE = new ItemLayerModelColored(ImmutableList.of(), 0xFFFFFFFF);

	private static final EnumFacing[] HORIZONTALS = { EnumFacing.UP, EnumFacing.DOWN };
	private static final EnumFacing[] VERTICALS = { EnumFacing.WEST, EnumFacing.EAST };

	private final ImmutableList<ResourceLocation> textures;
	private final ItemOverrideList overrides;

	private final int color;

	public ItemLayerModelColored(ImmutableList<ResourceLocation> textures, int color) {
		this(textures, ItemOverrideList.NONE, color);
	}

	public ItemLayerModelColored(ImmutableList<ResourceLocation> textures, ItemOverrideList overrides, int color) {
		this.textures = textures;
		this.overrides = overrides;
		this.color = color;
	}

	public ItemLayerModelColored(ModelBlock model, int color) {
		this(getTextures(model), model.createOverrides(), color);
	}

	private static ImmutableList<ResourceLocation> getTextures(ModelBlock model) {
		ImmutableList.Builder<ResourceLocation> builder = ImmutableList.builder();
		for (int i = 0; model.isTexturePresent("layer" + i); i++) {
			builder.add(new ResourceLocation(model.resolveTextureName("layer" + i)));
		}
		return builder.build();
	}

	@Override
	public Collection<ResourceLocation> getTextures() {
		return textures;
	}

	@Override
	public ItemLayerModel retexture(ImmutableMap<String, String> textures) {
		ImmutableList.Builder<ResourceLocation> builder = ImmutableList.builder();
		for (int i = 0; i < textures.size() + this.textures.size(); i++) {
			if (textures.containsKey("layer" + i)) {
				builder.add(new ResourceLocation(textures.get("layer" + i)));
			} else if (i < this.textures.size()) {
				builder.add(this.textures.get(i));
			}
		}
		return new ItemLayerModel(builder.build(), overrides);
	}

	@Override
	public IBakedModel bake(IModelState state, final VertexFormat format,
			Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
		Optional<TRSRTransformation> transform = state.apply(Optional.empty());
		boolean identity = !transform.isPresent() || transform.get().isIdentity();
		for (int i = 0; i < textures.size(); i++) {
			TextureAtlasSprite sprite = bakedTextureGetter.apply(textures.get(i));
			builder.addAll(getQuadsForSprite(i, color, sprite, format, transform));
		}
		TextureAtlasSprite particle = bakedTextureGetter
				.apply(textures.isEmpty() ? new ResourceLocation("missingno") : textures.get(0));
		ImmutableMap<TransformType, TRSRTransformation> map = PerspectiveMapWrapper.getTransforms(state);
		return new BakedItemModel(builder.build(), particle, map, overrides, identity);
	}

	public static ImmutableList<BakedQuad> getQuadsForSprite(int tint, int color, TextureAtlasSprite sprite,
			VertexFormat format, Optional<TRSRTransformation> transform) {
		ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();

		int uMax = sprite.getIconWidth();
		int vMax = sprite.getIconHeight();

		FaceData faceData = new FaceData(uMax, vMax);
		boolean translucent = false;

		for (int f = 0; f < sprite.getFrameCount(); f++) {
			int[] pixels = sprite.getFrameTextureData(f)[0];
			boolean ptu;
			boolean[] ptv = new boolean[uMax];
			Arrays.fill(ptv, true);
			for (int v = 0; v < vMax; v++) {
				ptu = true;
				for (int u = 0; u < uMax; u++) {
					int alpha = getAlpha(pixels, uMax, vMax, u, v);
					boolean t = alpha / 255f <= 0.1f;

					if (!t && alpha < 255) {
						translucent = true;
					}

					if (ptu && !t) // left - transparent, right - opaque
					{
						faceData.set(EnumFacing.WEST, u, v);
					}
					if (!ptu && t) // left - opaque, right - transparent
					{
						faceData.set(EnumFacing.EAST, u - 1, v);
					}
					if (ptv[u] && !t) // up - transparent, down - opaque
					{
						faceData.set(EnumFacing.UP, u, v);
					}
					if (!ptv[u] && t) // up - opaque, down - transparent
					{
						faceData.set(EnumFacing.DOWN, u, v - 1);
					}

					ptu = t;
					ptv[u] = t;
				}
				if (!ptu) // last - opaque
				{
					faceData.set(EnumFacing.EAST, uMax - 1, v);
				}
			}
			// last line
			for (int u = 0; u < uMax; u++) {
				if (!ptv[u]) {
					faceData.set(EnumFacing.DOWN, u, vMax - 1);
				}
			}
		}

		// horizontal quads
		for (EnumFacing facing : HORIZONTALS) {
			for (int v = 0; v < vMax; v++) {
				int uStart = 0, uEnd = uMax;
				boolean building = false;
				for (int u = 0; u < uMax; u++) {
					boolean face = faceData.get(facing, u, v);
					if (!translucent) {
						if (face) {
							if (!building) {
								building = true;
								uStart = u;
							}
							uEnd = u + 1;
						}
					} else {
						if (building && !face) // finish current quad
						{
							// make quad [uStart, u]
							int off = facing == EnumFacing.DOWN ? 1 : 0;
							builder.add(buildSideQuad(format, transform, facing, tint, color, sprite, uStart, v + off,
									u - uStart));
							building = false;
						} else if (!building && face) // start new quad
						{
							building = true;
							uStart = u;
						}
					}
				}
				if (building) // build remaining quad
				{
					// make quad [uStart, uEnd]
					int off = facing == EnumFacing.DOWN ? 1 : 0;
					builder.add(buildSideQuad(format, transform, facing, tint, color, sprite, uStart, v + off,
							uEnd - uStart));
				}
			}
		}

		// vertical quads
		for (EnumFacing facing : VERTICALS) {
			for (int u = 0; u < uMax; u++) {
				int vStart = 0, vEnd = vMax;
				boolean building = false;
				for (int v = 0; v < vMax; v++) {
					boolean face = faceData.get(facing, u, v);
					if (!translucent) {
						if (face) {
							if (!building) {
								building = true;
								vStart = v;
							}
							vEnd = v + 1;
						}
					} else {
						if (building && !face) // finish current quad
						{
							// make quad [vStart, v]
							int off = facing == EnumFacing.EAST ? 1 : 0;
							builder.add(buildSideQuad(format, transform, facing, tint, color, sprite, u + off, vStart,
									v - vStart));
							building = false;
						} else if (!building && face) // start new quad
						{
							building = true;
							vStart = v;
						}
					}
				}
				if (building) // build remaining quad
				{
					// make quad [vStart, vEnd]
					int off = facing == EnumFacing.EAST ? 1 : 0;
					builder.add(buildSideQuad(format, transform, facing, tint, color, sprite, u + off, vStart,
							vEnd - vStart));
				}
			}
		}

		// front
		builder.add(buildQuad(format, transform, EnumFacing.NORTH, sprite, tint, color, 0, 0, 7.5f / 16f,
				sprite.getMinU(), sprite.getMaxV(), 0, 1, 7.5f / 16f, sprite.getMinU(), sprite.getMinV(), 1, 1,
				7.5f / 16f, sprite.getMaxU(), sprite.getMinV(), 1, 0, 7.5f / 16f, sprite.getMaxU(), sprite.getMaxV()));
		// back
		builder.add(buildQuad(format, transform, EnumFacing.SOUTH, sprite, tint, color, 0, 0, 8.5f / 16f,
				sprite.getMinU(), sprite.getMaxV(), 1, 0, 8.5f / 16f, sprite.getMaxU(), sprite.getMaxV(), 1, 1,
				8.5f / 16f, sprite.getMaxU(), sprite.getMinV(), 0, 1, 8.5f / 16f, sprite.getMinU(), sprite.getMinV()));

		return builder.build();
	}

	private static class FaceData {
		private final EnumMap<EnumFacing, BitSet> data = new EnumMap<>(EnumFacing.class);

		private final int vMax;

		FaceData(int uMax, int vMax) {
			this.vMax = vMax;

			data.put(EnumFacing.WEST, new BitSet(uMax * vMax));
			data.put(EnumFacing.EAST, new BitSet(uMax * vMax));
			data.put(EnumFacing.UP, new BitSet(uMax * vMax));
			data.put(EnumFacing.DOWN, new BitSet(uMax * vMax));
		}

		public void set(EnumFacing facing, int u, int v) {
			data.get(facing).set(getIndex(u, v));
		}

		public boolean get(EnumFacing facing, int u, int v) {
			return data.get(facing).get(getIndex(u, v));
		}

		private int getIndex(int u, int v) {
			return v * vMax + u;
		}
	}

	private static int getAlpha(int[] pixels, int uMax, int vMax, int u, int v) {
		return pixels[u + (vMax - 1 - v) * uMax] >> 24 & 0xFF;
	}

	private static BakedQuad buildSideQuad(VertexFormat format, Optional<TRSRTransformation> transform, EnumFacing side,
			int tint, int color, TextureAtlasSprite sprite, int u, int v, int size) {
		final float eps = 1e-2f;

		int width = sprite.getIconWidth();
		int height = sprite.getIconHeight();

		float x0 = (float) u / width;
		float y0 = (float) v / height;
		float x1 = x0, y1 = y0;
		float z0 = 7.5f / 16f, z1 = 8.5f / 16f;

		switch (side) {
		case WEST:
			z0 = 8.5f / 16f;
			z1 = 7.5f / 16f;
		case EAST:
			y1 = (float) (v + size) / height;
			break;
		case DOWN:
			z0 = 8.5f / 16f;
			z1 = 7.5f / 16f;
		case UP:
			x1 = (float) (u + size) / width;
			break;
		default:
			throw new IllegalArgumentException("can't handle z-oriented side");
		}

		float dx = side.getDirectionVec().getX() * eps / width;
		float dy = side.getDirectionVec().getY() * eps / height;

		float u0 = 16f * (x0 - dx);
		float u1 = 16f * (x1 - dx);
		float v0 = 16f * (1f - y0 - dy);
		float v1 = 16f * (1f - y1 - dy);

		return buildQuad(format, transform, remap(side), sprite, tint, color, x0, y0, z0, sprite.getInterpolatedU(u0),
				sprite.getInterpolatedV(v0), x1, y1, z0, sprite.getInterpolatedU(u1), sprite.getInterpolatedV(v1), x1,
				y1, z1, sprite.getInterpolatedU(u1), sprite.getInterpolatedV(v1), x0, y0, z1,
				sprite.getInterpolatedU(u0), sprite.getInterpolatedV(v0));
	}

	private static EnumFacing remap(EnumFacing side) {
		// getOpposite is related to the swapping of V direction
		return side.getAxis() == EnumFacing.Axis.Y ? side.getOpposite() : side;
	}

	private static BakedQuad buildQuad(VertexFormat format, Optional<TRSRTransformation> transform, EnumFacing side,
			TextureAtlasSprite sprite, int tint, int color, float x0, float y0, float z0, float u0, float v0, float x1,
			float y1, float z1, float u1, float v1, float x2, float y2, float z2, float u2, float v2, float x3,
			float y3, float z3, float u3, float v3) {
		UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(format);

		builder.setQuadTint(tint);
		builder.setQuadOrientation(side);
		builder.setTexture(sprite);

		boolean hasTransform = transform.isPresent() && !transform.get().isIdentity();
		IVertexConsumer consumer = hasTransform ? new TRSRTransformer(builder, transform.get()) : builder;

		putVertex(consumer, format, side, x0, y0, z0, u0, v0, color);
		putVertex(consumer, format, side, x1, y1, z1, u1, v1, color);
		putVertex(consumer, format, side, x2, y2, z2, u2, v2, color);
		putVertex(consumer, format, side, x3, y3, z3, u3, v3, color);

		return builder.build();
	}

	private static void putVertex(IVertexConsumer consumer, VertexFormat format, EnumFacing side, float x, float y,
			float z, float u, float v, int color) {
		for (int e = 0; e < format.getElementCount(); e++) {
			switch (format.getElement(e).getUsage()) {
			case POSITION:
				consumer.put(e, x, y, z, 1f);
				break;
			case COLOR:
				float r = ((color >> 16) & 0xFF) / 255f; // red
				float g = ((color >> 8) & 0xFF) / 255f; // green
				float b = ((color >> 0) & 0xFF) / 255f; // blue
				float a = ((color >> 24) & 0xFF) / 255f; // alpha
				consumer.put(e, r, g, b, a);
				break;
			case NORMAL:
				float offX = side.getFrontOffsetX();
				float offY = side.getFrontOffsetY();
				float offZ = side.getFrontOffsetZ();
				consumer.put(e, offX, offY, offZ, 0f);
				break;
			case UV:
				if (format.getElement(e).getIndex() == 0) {
					consumer.put(e, u, v, 0f, 1f);
					break;
				}
				// else fallthrough to default
			default:
				consumer.put(e);
				break;
			}
		}
	}

	public static enum Loader implements ICustomModelLoader {
		INSTANCE;

		@Override
		public void onResourceManagerReload(IResourceManager resourceManager) {
		}

		@Override
		public boolean accepts(ResourceLocation modelLocation) {
			return modelLocation.getResourceDomain().equals(ForgeVersion.MOD_ID)
					&& (modelLocation.getResourcePath().equals("item-layer")
							|| modelLocation.getResourcePath().equals("models/block/item-layer")
							|| modelLocation.getResourcePath().equals("models/item/item-layer"));
		}

		@Override
		public IModel loadModel(ResourceLocation modelLocation) {
			return ItemLayerModel.INSTANCE;
		}
	}
}
