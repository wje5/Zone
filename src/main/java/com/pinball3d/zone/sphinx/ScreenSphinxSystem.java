package com.pinball3d.zone.sphinx;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.lwjgl.opengl.GL11;

import com.google.common.base.Predicate;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.IMob;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class ScreenSphinxSystem extends GuiScreen {
	protected Map<Long, ChunkRenderCache> mapCache = new HashMap<Long, ChunkRenderCache>();
	private static BufferBuilder bufferbuilder;
	private int lastMouseX, lastMouseY;
	private int xOffset, yOffset;
	private PointerPlayer pointerPlayer;
	private Map<Integer, PointerLiving> livings = new HashMap<Integer, PointerLiving>();
	public static final int size = 1;
	private static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/gui/sphinx/icons.png");
	private Set<TexturedButton> components = new HashSet<TexturedButton>();

	@Override
	public void initGui() {
		applyComponents();
		ChunkRenderCache.init();
		BlockPos pos = mc.player.getPosition();
		xOffset = pos.getX();
		yOffset = pos.getZ();
		pointerPlayer = new PointerPlayer(xOffset, yOffset);
		updateLiving();
		super.initGui();
	}

	private void applyComponents() {
		components.add(new TexturedButton(width - 10, 2, TEXTURE, 0, 16, 32, 26, 0.25F, new Runnable() {
			@Override
			public void run() {
				System.out.println(1);
			}
		}));
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		updatePlayer();
		if (mc.player.ticksExisted % 20 == 0) {
			updateLiving();
		}
		drawMap();
		drawPointer();
		Util.drawTexture(TEXTURE, width - 10, 2, 0, 16, 32, 26, 0.25F);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	private void drawMap() {
		GlStateManager.pushMatrix();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
				GlStateManager.DestFactor.ZERO);
		BlockPos pos = mc.player.getPosition();
		int xChunk = getRenderOffsetX() < 0 ? getRenderOffsetX() / 16 * size - 1 : getRenderOffsetX() / 16 * size;
		int yChunk = getRenderOffsetY() < 0 ? getRenderOffsetY() / 16 * size - 1 : getRenderOffsetY() / 16 * size;
		int xEnd = getRenderOffsetX() + width - 1 < 0 ? (getRenderOffsetX() + width - 1) / 16 * size - 1
				: (getRenderOffsetX() + width - 1) / 16 * size;
		int yEnd = getRenderOffsetY() + height - 1 < 0 ? (getRenderOffsetY() + height - 1) / 16 * size - 1
				: (getRenderOffsetY() + height - 1) / 16 * size;
		for (int i = xChunk; i <= xEnd; i++) {
			for (int j = yChunk; j <= yEnd; j++) {
				drawChunk(getCache(i, j), i * 16 * size - getRenderOffsetX(), j * 16 * size - getRenderOffsetY());
			}
		}
		GlStateManager.popMatrix();
	}

	private void drawPointer() {
		GlStateManager.pushMatrix();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		Iterator<PointerLiving> it = livings.values().iterator();
		while (it.hasNext()) {
			PointerLiving pointer = it.next();
			pointer.doRender(getRenderOffsetX(), getRenderOffsetY());
		}
		pointerPlayer.doRender(getRenderOffsetX(), getRenderOffsetY());
		GlStateManager.popMatrix();
	}

	private void updatePlayer() {
		BlockPos pos = mc.player.getPosition();
		pointerPlayer.x = pos.getX();
		pointerPlayer.z = pos.getZ();
	}

	private void updateLiving() {
		Predicate selector = new Predicate<Entity>() {
			@Override
			public boolean apply(Entity input) {
				return input instanceof EntityLiving;
			};
		};
		int x = getRenderOffsetX();
		int y = getRenderOffsetY();
		List<Entity> entitys = mc.player.world.getEntitiesInAABBexcluding(mc.player,
				new AxisAlignedBB(x, -100, y, x + width, 1000, y + height), selector);
		Iterator<Entity> it = entitys.iterator();
		Map<Integer, PointerLiving> temp = new HashMap<Integer, PointerLiving>();
		while (it.hasNext()) {
			EntityLiving entity = (EntityLiving) it.next();
			PointerLiving pointer = livings.get(entity.getEntityId());
			if (pointer == null) {
				BlockPos pos = entity.getPosition();
				pointer = new PointerLiving(pos.getX(), pos.getZ(), entity instanceof IMob);
			} else {
				BlockPos pos = entity.getPosition();
				pointer.x = pos.getX();
				pointer.z = pos.getZ();
			}
			temp.put(entity.getEntityId(), pointer);
		}
		livings = temp;
	}

	private int getRenderOffsetX() {
		int xRenderRange = width / 2 / size;
		return xOffset - xRenderRange + 1;
	}

	private int getRenderOffsetY() {
		int yRenderRange = height / 2 / size;
		return yOffset - yRenderRange + 1;
	}

	private void drawChunk(ChunkRenderCache cache, int xOffset, int zOffset) {
		GlStateManager.pushMatrix();
		Tessellator tessellator = Tessellator.getInstance();
		bufferbuilder = tessellator.getBuffer();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
				GlStateManager.DestFactor.ZERO);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				applyQuad(cache.getColor(x, z), xOffset + x * size, zOffset + z * size);
			}
		}
		tessellator.draw();
		GlStateManager.popMatrix();
		drawGrid(xOffset, zOffset);
	}

	private void applyQuad(int color, int x, int y) {
		float r = (color >> 16) / 255F;
		float g = ((color >> 8) & 0x0000FF) / 255F;
		float b = (color & 0x0000FF) / 255F;
		bufferbuilder.pos(x, y + size, 0).color(r, g, b, 1.0F).endVertex();
		bufferbuilder.pos(x + size, y + size, 0).color(r, g, b, 1.0F).endVertex();
		bufferbuilder.pos(x + size, y, 0).color(r, g, b, 1.0F).endVertex();
		bufferbuilder.pos(x, y, 0).color(r, g, b, 1.0F).endVertex();
	}

	private void drawGrid(int x, int y) {
		int color = 0xC8C0C0C0;
		color = 0x48C0C0C0;
		drawRect(x + size * 16 - 1, y, x + size * 16, y + size * 16, color);
		drawRect(x, y + size * 16 - 1, x + size * 16 - 1, y + size * 16, color);
	}

	public ChunkRenderCache getCache(int x, int z) {
		ChunkRenderCache cache = mapCache.get(x * 30000000L + z);
		if (cache == null) {
			cache = ChunkRenderCache.create(x, z);
			mapCache.put(x * 30000000L + z, cache);
		}
		return cache;
	}

	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		if (clickedMouseButton == 0) {
			if (lastMouseX > 0 && lastMouseY > 0) {
				xOffset = xOffset - (mouseX - lastMouseX);
				yOffset = yOffset - (mouseY - lastMouseY);
			}
			lastMouseX = mouseX;
			lastMouseY = mouseY;
		}
		super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		lastMouseX = -1;
		lastMouseY = -1;
		components.forEach(e -> e.onClickScreen(mouseX, mouseY, state == 0));
		super.mouseReleased(mouseX, mouseY, state);
	}
}
