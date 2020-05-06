package com.pinball3d.zone.sphinx;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.google.common.base.Predicate;

import net.minecraft.client.gui.FontRenderer;
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

public class ScreenTerminal extends GuiScreen implements IParent {
	protected Map<Long, ChunkRenderCache> mapCache = new HashMap<Long, ChunkRenderCache>();
	private static BufferBuilder bufferbuilder;
	private int lastMouseX, lastMouseY;
	private int clickX, clickY;
	private int xOffset, yOffset;
	private PointerPlayer pointerPlayer;
	private Map<Integer, PointerLiving> livings = new HashMap<Integer, PointerLiving>();
	public static final int size = 1;
	private static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/gui/sphinx/icons.png");
	private Set<Component> components = new HashSet<Component>();
	public Stack<Subscreen> subscreens = new Stack<Subscreen>();

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

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (keyCode == Keyboard.KEY_ESCAPE) {
			if (subscreens.empty()) {
				super.keyTyped(typedChar, keyCode);
			} else if (subscreens.peek().onQuit()) {
				subscreens.pop();
			}
		} else {
			if (subscreens.empty()) {
				components.forEach(e -> {
					e.onKeyTyped(typedChar, keyCode);
				});
			} else {
				subscreens.peek().keyTyped(typedChar, keyCode);
			}
		}
	}

	private void applyComponents() {
		components.add(new TexturedButton(this, width - 10, 2, TEXTURE, 0, 16, 32, 26, 0.25F, new Runnable() {
			@Override
			public void run() {
				subscreens.push(new SubscreenNetworkConfig((ScreenTerminal) mc.currentScreen));
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
		components.forEach(e -> {
			e.doRender(mouseX, mouseY);
		});
		Iterator<Subscreen> it = subscreens.iterator();
		while (it.hasNext()) {
			Subscreen screen = it.next();
			if (screen.dead) {
				it.remove();
			} else {
				screen.doRender(mouseX, mouseY);
			}
		}
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
		for (int i = xChunk; i <= xEnd + 1; i++) {
			for (int j = yChunk; j <= yEnd + 1; j++) {
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
		int color = 0x48C0C0C0;
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
		int moveX = lastMouseX > 0 ? mouseX - lastMouseX : 0;
		int moveY = lastMouseY > 0 ? mouseY - lastMouseY : 0;
		lastMouseX = mouseX;
		lastMouseY = mouseY;
		if (!subscreens.empty()) {
			Subscreen screen = subscreens.peek();
			if (mouseX >= screen.x && mouseX <= screen.x + width && mouseY >= screen.y && mouseY <= screen.y + height) {
				screen.onDrag(mouseX - screen.x, mouseY - screen.y, moveX, moveY, clickedMouseButton != 1);
			}
			return;
		}
		if (clickedMouseButton != 1) {
			if (lastMouseX > 0 && lastMouseY > 0) {
				xOffset = xOffset - moveX;
				yOffset = yOffset - moveY;
			}
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		clickX = mouseX;
		clickY = mouseY;
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		if ((clickX == -1 || Math.abs(mouseX - clickX) < 5) && (clickY == -1 || Math.abs(mouseY - clickY) < 5)) {
			if (subscreens.empty()) {
				components.forEach(e -> {
					int x = mouseX - e.x;
					int y = mouseY - e.y;
					if (x >= 0 && x <= e.width && y >= 0 && y <= e.height) {
						e.onClickScreen(x, y, state != 1);
					}
				});
			} else {
				Subscreen screen = subscreens.peek();
				if (mouseX >= screen.x && mouseX <= screen.x + width && mouseY >= screen.y
						&& mouseY <= screen.y + height) {
					screen.onClick(mouseX - screen.x, mouseY - screen.y, state != 1);
				}
			}
		}
		lastMouseX = -1;
		lastMouseY = -1;
		clickX = -1;
		clickY = -1;
		super.mouseReleased(mouseX, mouseY, state);
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public int getXOffset() {
		return 0;
	}

	@Override
	public int getYOffset() {
		return 0;
	}

	@Override
	public FontRenderer getFontRenderer() {
		return fontRenderer;
	}

	@Override
	public void putScreen(Subscreen screen) {
		subscreens.push(screen);
	}

	@Override
	public void quitScreen(Subscreen screen) {
		subscreens.remove(screen);
	}
}
