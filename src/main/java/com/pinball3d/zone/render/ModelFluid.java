package com.pinball3d.zone.render;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import javax.annotation.Nullable;
import javax.vecmath.Quat4f;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.BakedItemModel;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelStateComposition;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.client.model.SimpleModelState;
import net.minecraftforge.common.model.IModelPart;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public final class ModelFluid implements IModel {
	public static final ModelResourceLocation LOCATION = new ModelResourceLocation(
			new ResourceLocation("zone:dynfluid"), "inventory");

	public static final IModel MODEL = new ModelFluid();

	@Nullable
	private final Fluid fluid;

	private final boolean flipGas;
	private final boolean tint;

	public ModelFluid() {
		this(null, false, true);
	}

	public ModelFluid(Fluid fluid, boolean flipGas, boolean tint) {
		this.fluid = fluid;
		this.flipGas = flipGas;
		this.tint = tint;
	}

	@Override
	public Collection<ResourceLocation> getTextures() {
		ImmutableSet.Builder<ResourceLocation> builder = ImmutableSet.builder();
		if (fluid != null) {
			builder.add(fluid.getStill());
		}
		return builder.build();
	}

	@Override
	public IBakedModel bake(IModelState state, VertexFormat format,
			Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {

		ImmutableMap<TransformType, TRSRTransformation> transformMap = PerspectiveMapWrapper.getTransforms(state);
		if (flipGas && fluid != null && fluid.isLighterThanAir()) {
			state = new ModelStateComposition(state, TRSRTransformation
					.blockCenterToCorner(new TRSRTransformation(null, new Quat4f(0, 0, 1, 0), null, null)));
		}

		TRSRTransformation transform = state.apply(Optional.empty()).orElse(TRSRTransformation.identity());
		TextureAtlasSprite particleSprite = null;
		ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();

		if (fluid != null) {
			IBakedModel model = new ItemLayerModelColored(ImmutableList.of(fluid.getStill()),
					tint ? fluid.getColor() : 0xFFFFFFFF).bake(state, format, bakedTextureGetter);
			builder.addAll(model.getQuads(null, null, 0));
			particleSprite = model.getParticleTexture();
		}
		return new BakedDynBucket(this, builder.build(), particleSprite, format, Maps.immutableEnumMap(transformMap),
				Maps.newHashMap(), transform.isIdentity());
	}

	@Override
	public ModelFluid process(ImmutableMap<String, String> customData) {
		String fluidName = customData.get("fluid");
		Fluid fluid = FluidRegistry.getFluid(fluidName);

		if (fluid == null)
			fluid = this.fluid;

		boolean flip = flipGas;
		if (customData.containsKey("flipGas")) {
			String flipStr = customData.get("flipGas");
			if (flipStr.equals("true"))
				flip = true;
			else if (flipStr.equals("false"))
				flip = false;
			else
				throw new IllegalArgumentException(String.format(
						"DynBucket custom data \"flipGas\" must have value \'true\' or \'false\' (was \'%s\')",
						flipStr));
		}

		boolean tint = this.tint;
		if (customData.containsKey("applyTint")) {
			String string = customData.get("applyTint");
			switch (string) {
			case "true":
				tint = true;
				break;
			case "false":
				tint = false;
				break;
			default:
				throw new IllegalArgumentException(String.format(
						"DynBucket custom data \"applyTint\" must have value \'true\' or \'false\' (was \'%s\')",
						string));
			}
		}
		return new ModelFluid(fluid, flip, tint);
	}

	public enum LoaderDynBucket implements ICustomModelLoader {
		INSTANCE;

		@Override
		public boolean accepts(ResourceLocation modelLocation) {
			return modelLocation.getResourceDomain().equals("zone")
					&& modelLocation.getResourcePath().contains("modelfluid");
		}

		@Override
		public IModel loadModel(ResourceLocation modelLocation) {
			return MODEL;
		}

		@Override
		public void onResourceManagerReload(IResourceManager resourceManager) {

		}
	}

	private static final class BakedDynBucketOverrideHandler extends ItemOverrideList {
		public static final BakedDynBucketOverrideHandler INSTANCE = new BakedDynBucketOverrideHandler();

		private BakedDynBucketOverrideHandler() {
			super(ImmutableList.of());
		}

		@Override
		public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, @Nullable World world,
				@Nullable EntityLivingBase entity) {
			FluidStack fluidStack = FluidUtil.getFluidContained(stack);

			// not a fluid item apparently
			if (fluidStack == null) {
				// empty bucket
				return originalModel;
			}

			BakedDynBucket model = (BakedDynBucket) originalModel;

			Fluid fluid = fluidStack.getFluid();
			String name = fluid.getName();

			if (!model.cache.containsKey(name)) {
				IModel parent = model.parent.process(ImmutableMap.of("fluid", name));
				Function<ResourceLocation, TextureAtlasSprite> textureGetter;
				textureGetter = location -> Minecraft.getMinecraft().getTextureMapBlocks()
						.getAtlasSprite(location.toString());

				ImmutableMap<? extends IModelPart, TRSRTransformation> transforms = null;
				try {
					Field field = BakedItemModel.class.getDeclaredField("transforms");
					field.setAccessible(true);
					transforms = (ImmutableMap<? extends IModelPart, TRSRTransformation>) field.get(model);
				} catch (NoSuchFieldException | SecurityException | IllegalArgumentException
						| IllegalAccessException e) {
					e.printStackTrace();
				}
//				IBakedModel bakedModel = parent.bake(new SimpleModelState(model.transforms), model.format,
//						textureGetter);
				IBakedModel bakedModel = parent.bake(new SimpleModelState(transforms), model.format, textureGetter);
				model.cache.put(name, bakedModel);
				return bakedModel;
			}

			return model.cache.get(name);
		}
	}

	// the dynamic bucket is based on the empty bucket
	private static final class BakedDynBucket extends BakedItemModel {
		private final ModelFluid parent;
		private final Map<String, IBakedModel> cache; // contains all the baked models since they'll never change
		private final VertexFormat format;

		BakedDynBucket(ModelFluid parent, ImmutableList<BakedQuad> quads, TextureAtlasSprite particle,
				VertexFormat format, ImmutableMap<TransformType, TRSRTransformation> transforms,
				Map<String, IBakedModel> cache, boolean untransformed) {
			super(quads, particle, transforms, BakedDynBucketOverrideHandler.INSTANCE, untransformed);
			this.format = format;
			this.parent = parent;
			this.cache = cache;
		}
	}

}
