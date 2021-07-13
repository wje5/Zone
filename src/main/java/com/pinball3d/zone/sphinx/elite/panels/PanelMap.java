package com.pinball3d.zone.sphinx.elite.panels;

import java.lang.reflect.Field;
import java.util.BitSet;
import java.util.List;

import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Quaternion;

import com.pinball3d.zone.core.LoadingPluginZone;
import com.pinball3d.zone.sphinx.elite.Color;
import com.pinball3d.zone.sphinx.elite.Drag;
import com.pinball3d.zone.sphinx.elite.EliteMainwindow;
import com.pinball3d.zone.sphinx.elite.FontHandler;
import com.pinball3d.zone.sphinx.elite.FormattedString;
import com.pinball3d.zone.sphinx.elite.PanelGroup;
import com.pinball3d.zone.sphinx.elite.map.MapRenderManager;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockFluidRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;

public class PanelMap extends Panel {
	private static BlockFluidRenderer fluidRenderer;
	private float xOffset, yOffset, zOffset, rotX = -45, rotY = 45, rotZ = 0;
	private MapRenderManager renderManager = new MapRenderManager();
	private boolean inited;

	public PanelMap(EliteMainwindow parent, PanelGroup parentGroup) {
		super(parent, parentGroup, new FormattedString(I18n.format("elite.panel.map")));
		xOffset = (float) -parent.mc.player.posX + parentGroup.getPanelWidth() / 2;
		yOffset = (float) -parent.mc.player.posZ + parentGroup.getPanelHeight() / 2;
		if (fluidRenderer == null) {
			BlockRendererDispatcher dispatcher = parent.mc.getBlockRendererDispatcher();
			try {
				Field f = BlockRendererDispatcher.class
						.getDeclaredField(LoadingPluginZone.runtimeDeobf ? "field_175025_e" : "fluidRenderer");
				f.setAccessible(true);
				fluidRenderer = (BlockFluidRenderer) f.get(dispatcher);
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public Drag mouseClicked(int mouseX, int mouseY, int mouseButton) {
		return isMouseInPanel(mouseX, mouseY) ? new Drag((x, y, moveX, moveY) -> {
			if (mouseButton == 0) {
				xOffset += moveX;
				yOffset += moveY;
			} else {
				rotX += moveY / 10F;
				rotY += moveX / -10F;
			}
		}, cancel -> {

		}) : null;
	}

	@Override
	public void doRender(int mouseX, int mouseY, float partialTicks) {
		if (!inited) {
			renderManager.setWorldAndLoadRenderers(getParent().mc.world);
			inited = true;
		}
		renderManager.doRender(mouseX, mouseY, partialTicks);
		GlStateManager.pushMatrix();
		GlStateManager.matrixMode(GL11.GL_PROJECTION);
		GlStateManager.pushMatrix();
		PanelGroup group = getParentGroup();
//		GlStateManager.viewport(group.getPanelX(), group.getPanelY(), group.getPanelWidth(), group.getPanelHeight());

		GlStateManager.viewport(group.getPanelX(),
				getParent().getHeight() - (group.getPanelY() + group.getPanelHeight()), group.getPanelWidth(),
				group.getPanelHeight());
		GlStateManager.matrixMode(GL11.GL_PROJECTION);
		GlStateManager.loadIdentity();
		GlStateManager.ortho(0, group.getPanelWidth(), group.getPanelHeight(), 0, 0.0D, 5000.0D);
//		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
//		FontHandler.renderText(100, 20, getName(), Color.TEXT_LIGHT, getParentGroup().getWidth());
//		GlStateManager.loadIdentity();
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
//		GlStateManager.loadIdentity();
		GlStateManager.translate(0.0F, 0.0F, 500.0F);

//		renderWorld(partialTicks);
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		GlStateManager.popMatrix();
		GlStateManager.matrixMode(GL11.GL_PROJECTION);
		GlStateManager.popMatrix();
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);

//		GlStateManager.matrixMode(GL11.GL_PROJECTION);
//		GlStateManager.ortho(0, getParent().getWidth(), getParent().getHeight(), 0, 1000.0D, 3000.0D);
		GlStateManager.viewport(0, 0, getParent().getWidth(), getParent().getHeight());
//		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
//		EliteRenderHelper.drawRect(0, 0, group.getPanelWidth(), group.getPanelHeight(), Color.WHITE);
		FontHandler.renderText(10, 0, new FormattedString("(123中(文测试）AaBbCc"), Color.TEXT_LIGHT,
				getParentGroup().getWidth());
		FontHandler.renderText(10, 20, getName(), Color.TEXT_LIGHT, getParentGroup().getWidth());
		FontHandler.renderText(10, 40, new FormattedString("§o我§n能吞§l下玻璃而§r不伤身§l体("), Color.TEXT_LIGHT,
				getParentGroup().getWidth());
		FontHandler.renderText(10, 60, new FormattedString("FPS:" + Minecraft.getDebugFPS()), Color.TEXT_LIGHT,
				getParentGroup().getWidth());
		FontHandler.renderText(0, 80,
				new FormattedString("§o我§n能吞§l下玻璃而§r不伤身§l体(KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK"),
				Color.TEXT_LIGHT, getParentGroup().getWidth());

//		GlStateManager.enableDepth();
//		Minecraft mc = getParent().mc;
//		EliteRenderHelper.drawRect(0, 0, getParentGroup().getPanelWidth(), getParentGroup().getPanelHeight(),
//				new Color(0xFFFFFFFF));
//		GlStateManager.pushMatrix();
//
//		GlStateManager.translate(500, 500, 0);
//		GlStateManager.rotate(180, 0, 0, 1);
//		GlStateManager.rotate(45, 1, 0, 0);
//		GlStateManager.scale(3, 3, 3);
//		int pass = 2;
//		Entity entity = mc.player;
//		RenderGlobal renderglobal = mc.renderGlobal;
//		ICamera icamera = new Frustum() {
//			@Override
//			public boolean isBoxInFrustum(double p_78548_1_, double p_78548_3_, double p_78548_5_, double p_78548_7_,
//					double p_78548_9_, double p_78548_11_) {
//				return true;
//			}
//
//			@Override
//			public boolean isBoundingBoxInFrustum(AxisAlignedBB p_78546_1_) {
//				return true;
//			}
//		};
//		double d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
//		double d1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
//		double d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;
//		icamera.setPosition(d0, d1, d2);
//
//		mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
//
//		GlStateManager.disableAlpha();
//		renderglobal.renderBlockLayer(BlockRenderLayer.SOLID, partialTicks, pass, entity);
//		GlStateManager.enableAlpha();
//		mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false,
//				mc.gameSettings.mipmapLevels > 0);
//		renderglobal.renderBlockLayer(BlockRenderLayer.CUTOUT_MIPPED, partialTicks, pass, entity);
//		mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
//		mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
//		renderglobal.renderBlockLayer(BlockRenderLayer.CUTOUT, partialTicks, pass, entity);
//		mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
//		GlStateManager.shadeModel(GL11.GL_FLAT);
//		GlStateManager.alphaFunc(516, 0.1F);
//		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
//		GlStateManager.popMatrix();
//
//		GlStateManager.pushMatrix();
//		RenderHelper.enableStandardItemLighting();
//		mc.mcProfiler.endStartSection("entities");
//		net.minecraftforge.client.ForgeHooksClient.setRenderPass(0);
//		renderglobal.renderEntities(entity, icamera, partialTicks);
//		net.minecraftforge.client.ForgeHooksClient.setRenderPass(0);
//		RenderHelper.disableStandardItemLighting();
//		GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
//		GlStateManager.disableTexture2D();
//		GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
//
//		GlStateManager.popMatrix();

		super.doRender(mouseX, mouseY, partialTicks);
	}

//	private void renderWorld(float partialTicks) {
//		setupChunks();
//		block = 0;
//		Minecraft mc = getParent().mc;
//		GlStateManager.enableCull();
////		BlockRendererDispatcher dispatcher = mc.getBlockRendererDispatcher();
//		BlockPos pos = mc.player.getPosition().down();
//
//		RenderHelper.enableGUIStandardItemLighting();
//		GlStateManager.pushMatrix();
//		mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
//
////		GlStateManager.loadIdentity();
////		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
//
////		GlStateManager.disableAlpha();
//
////		bufferBuilders[0].begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
////		bufferBuilders[0].setTranslation(0, 0, 0);
//
////		Tessellator tessellator = Tessellator.getInstance();
////		BufferBuilder builder = tessellator.getBuffer();
////		BufferBuilder builder = bufferBuilders[0];
////		builder.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
////		builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
////		renderBlock(block, pos, mc.world, builder);
////		drawRect(10, 10, 40, 40, Color.WHITE, builder);
////		RenderItem ir = mc.getRenderItem();
////		ItemStack stack = new ItemStack(Blocks.STAINED_GLASS);
////		IBakedModel bakedmodel = ir.getItemModelMesher().getItemModel(stack);
//		BlockRendererDispatcher dispatcher = mc.getBlockRendererDispatcher();
////		IBakedModel bakedmodel = dispatcher.getModelForState(block);
////		bakedmodel = bakedmodel.getOverrides().handleItemState(bakedmodel, stack, null, mc.player);
////		TextureManager textureManager = mc.getTextureManager();
//		GlStateManager.pushMatrix();
////		textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
////		textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
////		GlStateManager.enableRescaleNormal();
////		GlStateManager.enableAlpha();
////		GlStateManager.alphaFunc(516, 0.1F);
////		GlStateManager.enableBlend();
////		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
////		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
////
////		GlStateManager.translate(10, 10, 50.0F);
////		GlStateManager.translate(8.0F, 8.0F, 0.0F);
////		GlStateManager.scale(1.0F, -1.0F, 1.0F);
////		GlStateManager.scale(16.0F, 16.0F, 16.0F);
//
////		if (bakedmodel.isGui3d()) {
////			GlStateManager.enableLighting();
////		} else {
////			GlStateManager.disableLighting();
////		}
//
////		bakedmodel = ForgeHooksClient.handleCameraTransforms(bakedmodel, ItemCameraTransforms.TransformType.GUI, false);
////		IBakedModel bakedmodel = dispatcher.getModelForState(block);
//		Tessellator tessellator = Tessellator.getInstance();
//		BufferBuilder bufferbuilder = tessellator.getBuffer();
//		GlStateManager.disableLighting();
//
//		GlStateManager.disableAlpha();
//		renderLayer(BlockRenderLayer.SOLID);
//		GlStateManager.enableAlpha();
//		mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false,
//				mc.gameSettings.mipmapLevels > 0);
//		renderLayer(BlockRenderLayer.CUTOUT_MIPPED);
//		mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
//		mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
//		renderLayer(BlockRenderLayer.CUTOUT);
//		mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
//
//		GlStateManager.shadeModel(GL11.GL_FLAT);
//		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
////		ir.renderItem(stack, bakedmodel);
////		GlStateManager.disableAlpha();
////		GlStateManager.disableRescaleNormal();
////		GlStateManager.disableLighting();
//		GlStateManager.popMatrix();
////		textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
////		textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
//
//		RenderHelper.disableStandardItemLighting();
////		tessellator.draw();
////		builder.finishDrawing();
////		new WorldVertexBufferUploader().draw(builder);
//
////		GlStateManager.enableAlpha();
//
//		GlStateManager.popMatrix();
//		GlStateManager.disableCull();
//	}

//	private void setupChunks() {
//		for (int x = -renderRange; x <= renderRange; x++) {
//			for (int y = -renderRange; y <= renderRange; y++) {
//				for (int z = -renderRange; z <= renderRange; z++) {
//
//				}
//			}
//		}
//	}

	private static Quaternion makeQuaternion(float p_188035_0_, float p_188035_1_, float p_188035_2_) {
		float f = p_188035_0_ * 0.017453292F;
		float f1 = p_188035_1_ * 0.017453292F;
		float f2 = p_188035_2_ * 0.017453292F;
		float f3 = MathHelper.sin(0.5F * f);
		float f4 = MathHelper.cos(0.5F * f);
		float f5 = MathHelper.sin(0.5F * f1);
		float f6 = MathHelper.cos(0.5F * f1);
		float f7 = MathHelper.sin(0.5F * f2);
		float f8 = MathHelper.cos(0.5F * f2);
		return new Quaternion(f3 * f6 * f8 + f4 * f5 * f7, f4 * f5 * f8 - f3 * f6 * f7, f3 * f5 * f8 + f4 * f6 * f7,
				f4 * f6 * f8 - f3 * f5 * f7);
	}

//	private void renderLayer(BlockRenderLayer layer) {
//		Minecraft mc = getParent().mc;
//		Tessellator tessellator = Tessellator.getInstance();
//		BufferBuilder builder = tessellator.getBuffer();
//		switch (layer) {
//		case SOLID:
//			mc.entityRenderer.enableLightmap();
//
////			for (EnumFacing enumfacing : EnumFacing.values()) {
////				ir.renderQuads(bufferbuilder, bakedmodel.getQuads((IBlockState) null, enumfacing, 0L), 0xFFFFFFFF, stack);
////				bakedmodel.getQuads(block, enumfacing, 0L).forEach(e -> {
////					bufferbuilder.addVertexData(e.getVertexData());
////				});
////			}
////			bakedmodel.getQuads(block, null, 0L).forEach(e -> {
////				bufferbuilder.addVertexData(e.getVertexData());
////			});
////			ir.renderQuads(bufferbuilder, bakedmodel.getQuads((IBlockState) null, (EnumFacing) null, 0L), 0xFFFFFFFF,
////					stack);
//
//			// RENDER CHUNK START
//			GlStateManager.pushMatrix();
//			GlStateManager.translate(xOffset, yOffset, zOffset);
//			GlStateManager.rotate(makeQuaternion(rotX, rotY, rotZ));
//			float scale = 1;
//			GlStateManager.scale(scale, -scale, scale);
//
//			// CHUNK START
//			GlStateManager.pushMatrix();
//			builder.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
//			for (int x = -renderRange; x <= renderRange; x++) {
//				for (int y = -renderRange; y <= renderRange; y++) {
//					for (int z = -renderRange; z <= renderRange; z++) {
//						renderChunk(new BlockPos((mc.player.getPosition().getX() & 0xFFFFFFF0) + x * 16,
//								(mc.player.getPosition().getY() & 0xFFFFFFF0) + y * 16,
//								(mc.player.getPosition().getZ() & 0xFFFFFFF0) + z * 16), builder);
//
//					}
//				}
//			}
//			tessellator.draw();
//			GlStateManager.popMatrix();
//			// CHUNK END
//			ModelRender.renderModel(Model.createCube(1), 0xFFFF0000);
//			GlStateManager.popMatrix();
//			// RENDER CHUNK END
//			mc.entityRenderer.disableLightmap();
//			break;
//		}
//	}

//	private void renderChunk(BlockPos pos, BufferBuilder builder) {
//		Minecraft mc = getParent().mc;
//		BlockPos pos2 = pos.add(15, 15, 15);
//		for (BlockPos.MutableBlockPos e : BlockPos.getAllInBoxMutable(pos, pos2)) {
////			builder.setTranslation((e.getX() - pos.getX()), (e.getY() - pos.getY()),
////					(e.getZ() - pos.getZ()));
////			GlStateManager.translate(e.getX() - pos.getX(), e.getY() - pos.getY(), e.getZ() - pos.getZ());
//			renderBlock(e, builder);
//		}
//	}

//	private void renderBlock(BlockPos pos, BufferBuilder builder) {
//		Minecraft mc = getParent().mc;
//		IBlockState state = mc.world.getBlockState(pos);
//		state = state.getActualState(mc.world, pos);
//		state = state.getBlock().getExtendedState(state, mc.world, pos);
//		EnumBlockRenderType type = state.getRenderType();
//		switch (type) {
//		case MODEL:
//			BlockRendererDispatcher dispatcher = getParent().mc.getBlockRendererDispatcher();
//			IBakedModel bakedmodel = dispatcher.getModelForState(state);
//			boolean checkSide = true;
//			renderModelFlat(mc.world, bakedmodel, state, pos, builder, checkSide, MathHelper.getPositionRandom(pos));
//			block++;
//			break;
//		case LIQUID:
//			fluidRenderer.renderFluid(mc.world, state, pos, builder);
//			block++;
//			break;
//		default:
//			break;
//		}
//
////		for (EnumFacing enumfacing : EnumFacing.values()) {
////			bakedmodel.getQuads(state, enumfacing, 0L).forEach(e -> {
////				builder.addVertexData(e.getVertexData());
////			});
////		}
////		bakedmodel.getQuads(state, null, 0L).forEach(e -> {
////			builder.addVertexData(e.getVertexData());
////		});
//	}

	public boolean renderModelFlat(IBlockAccess worldIn, IBakedModel modelIn, IBlockState stateIn, BlockPos posIn,
			BufferBuilder buffer, boolean checkSides, long rand) {
		boolean flag = false;
		BitSet bitset = new BitSet(3);
		for (EnumFacing enumfacing : EnumFacing.values()) {
			List<BakedQuad> list = modelIn.getQuads(stateIn, enumfacing, rand);
			if (!list.isEmpty() && (!checkSides || stateIn.shouldSideBeRendered(worldIn, posIn, enumfacing))) {
				int i = stateIn.getPackedLightmapCoords(worldIn, posIn.offset(enumfacing));
				renderQuadsFlat(worldIn, stateIn, posIn, i, false, buffer, list, bitset);
				flag = true;
			}
		}
		List<BakedQuad> list2 = modelIn.getQuads(stateIn, (EnumFacing) null, rand);
		if (!list2.isEmpty()) {
			renderQuadsFlat(worldIn, stateIn, posIn, -1, true, buffer, list2, bitset);
			flag = true;
		}
		return flag;
	}

	private void renderQuadsFlat(IBlockAccess blockAccessIn, IBlockState stateIn, BlockPos posIn, int brightnessIn,
			boolean ownBrightness, BufferBuilder buffer, List<BakedQuad> list, BitSet bitSet) {
		Vec3d vec3d = stateIn.getOffset(blockAccessIn, posIn);
		double d0 = posIn.getX() + vec3d.x;
		double d1 = posIn.getY() + vec3d.y;
		double d2 = posIn.getZ() + vec3d.z;
		int i = 0;

		for (int j = list.size(); i < j; ++i) {
			BakedQuad bakedquad = list.get(i);

			if (ownBrightness) {
				fillQuadBounds(stateIn, bakedquad.getVertexData(), bakedquad.getFace(), (float[]) null, bitSet);
				BlockPos blockpos = bitSet.get(0) ? posIn.offset(bakedquad.getFace()) : posIn;
				brightnessIn = stateIn.getPackedLightmapCoords(blockAccessIn, blockpos);
			}

			buffer.addVertexData(bakedquad.getVertexData());
			buffer.putBrightness4(brightnessIn, brightnessIn, brightnessIn, brightnessIn);

			if (bakedquad.hasTintIndex()) {
				int k = getParent().mc.getBlockColors().colorMultiplier(stateIn, blockAccessIn, posIn,
						bakedquad.getTintIndex());

				if (EntityRenderer.anaglyphEnable) {
					k = TextureUtil.anaglyphColor(k);
				}

				float f = (k >> 16 & 255) / 255.0F;
				float f1 = (k >> 8 & 255) / 255.0F;
				float f2 = (k & 255) / 255.0F;
				if (bakedquad.shouldApplyDiffuseLighting()) {
					float diffuse = net.minecraftforge.client.model.pipeline.LightUtil
							.diffuseLight(bakedquad.getFace());
					f *= diffuse;
					f1 *= diffuse;
					f2 *= diffuse;
				}
				buffer.putColorMultiplier(f, f1, f2, 4);
				buffer.putColorMultiplier(f, f1, f2, 3);
				buffer.putColorMultiplier(f, f1, f2, 2);
				buffer.putColorMultiplier(f, f1, f2, 1);
			} else if (bakedquad.shouldApplyDiffuseLighting()) {
				float diffuse = net.minecraftforge.client.model.pipeline.LightUtil.diffuseLight(bakedquad.getFace());
				buffer.putColorMultiplier(diffuse, diffuse, diffuse, 4);
				buffer.putColorMultiplier(diffuse, diffuse, diffuse, 3);
				buffer.putColorMultiplier(diffuse, diffuse, diffuse, 2);
				buffer.putColorMultiplier(diffuse, diffuse, diffuse, 1);
			}

			buffer.putPosition(d0, d1, d2);
//			buffer.putPosition(d0 % 16, d1 % 16, d2 % 16);
		}
	}

	private void fillQuadBounds(IBlockState stateIn, int[] vertexData, EnumFacing face, @Nullable float[] quadBounds,
			BitSet boundsFlags) {
		float f = 32.0F;
		float f1 = 32.0F;
		float f2 = 32.0F;
		float f3 = -32.0F;
		float f4 = -32.0F;
		float f5 = -32.0F;

		for (int i = 0; i < 4; ++i) {
			float f6 = Float.intBitsToFloat(vertexData[i * 7]);
			float f7 = Float.intBitsToFloat(vertexData[i * 7 + 1]);
			float f8 = Float.intBitsToFloat(vertexData[i * 7 + 2]);
			f = Math.min(f, f6);
			f1 = Math.min(f1, f7);
			f2 = Math.min(f2, f8);
			f3 = Math.max(f3, f6);
			f4 = Math.max(f4, f7);
			f5 = Math.max(f5, f8);
		}

		if (quadBounds != null) {
			quadBounds[EnumFacing.WEST.getIndex()] = f;
			quadBounds[EnumFacing.EAST.getIndex()] = f3;
			quadBounds[EnumFacing.DOWN.getIndex()] = f1;
			quadBounds[EnumFacing.UP.getIndex()] = f4;
			quadBounds[EnumFacing.NORTH.getIndex()] = f2;
			quadBounds[EnumFacing.SOUTH.getIndex()] = f5;
			int j = EnumFacing.values().length;
			quadBounds[EnumFacing.WEST.getIndex() + j] = 1.0F - f;
			quadBounds[EnumFacing.EAST.getIndex() + j] = 1.0F - f3;
			quadBounds[EnumFacing.DOWN.getIndex() + j] = 1.0F - f1;
			quadBounds[EnumFacing.UP.getIndex() + j] = 1.0F - f4;
			quadBounds[EnumFacing.NORTH.getIndex() + j] = 1.0F - f2;
			quadBounds[EnumFacing.SOUTH.getIndex() + j] = 1.0F - f5;
		}

		float f9 = 1.0E-4F;
		float f10 = 0.9999F;

		switch (face) {
		case DOWN:
			boundsFlags.set(1, f >= 1.0E-4F || f2 >= 1.0E-4F || f3 <= 0.9999F || f5 <= 0.9999F);
			boundsFlags.set(0, (f1 < 1.0E-4F || stateIn.isFullCube()) && f1 == f4);
			break;
		case UP:
			boundsFlags.set(1, f >= 1.0E-4F || f2 >= 1.0E-4F || f3 <= 0.9999F || f5 <= 0.9999F);
			boundsFlags.set(0, (f4 > 0.9999F || stateIn.isFullCube()) && f1 == f4);
			break;
		case NORTH:
			boundsFlags.set(1, f >= 1.0E-4F || f1 >= 1.0E-4F || f3 <= 0.9999F || f4 <= 0.9999F);
			boundsFlags.set(0, (f2 < 1.0E-4F || stateIn.isFullCube()) && f2 == f5);
			break;
		case SOUTH:
			boundsFlags.set(1, f >= 1.0E-4F || f1 >= 1.0E-4F || f3 <= 0.9999F || f4 <= 0.9999F);
			boundsFlags.set(0, (f5 > 0.9999F || stateIn.isFullCube()) && f2 == f5);
			break;
		case WEST:
			boundsFlags.set(1, f1 >= 1.0E-4F || f2 >= 1.0E-4F || f4 <= 0.9999F || f5 <= 0.9999F);
			boundsFlags.set(0, (f < 1.0E-4F || stateIn.isFullCube()) && f == f3);
			break;
		case EAST:
			boundsFlags.set(1, f1 >= 1.0E-4F || f2 >= 1.0E-4F || f4 <= 0.9999F || f5 <= 0.9999F);
			boundsFlags.set(0, (f3 > 0.9999F || stateIn.isFullCube()) && f == f3);
		}
	}

	private void drawRect(int x, int y, int width, int height, Color color, BufferBuilder builder) {
		float a = color.a / 255.0F;
		float r = color.r / 255.0F;
		float g = color.g / 255.0F;
		float b = color.b / 255.0F;
		GlStateManager.color(r, g, b, a);
		builder.pos(x, (double) y + height, 0.0D).endVertex();
		builder.pos((double) x + width, (double) y + height, 0.0D).endVertex();
		builder.pos((double) x + width, y, 0.0D).endVertex();
		builder.pos(x, y, 0.0D).endVertex();
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
	}
}
