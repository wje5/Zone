package com.pinball3d.zone.sphinx;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.UUID;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public abstract class ScreenSphinxBase extends GuiScreen implements IParent {
	protected Map<Long, ChunkRenderCache> mapCache = new HashMap<Long, ChunkRenderCache>();
	private int lastMouseX, lastMouseY;
	private int clickX, clickY;
	protected float partialMoveX;
	protected float partialMoveY;
	public static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/gui/sphinx/icons.png");
	public static final ResourceLocation TEXTURE_3 = new ResourceLocation("zone:textures/gui/sphinx/icons_3.png");
	public static final ResourceLocation TEXTURE_NO_NETWORK = new ResourceLocation(
			"zone:textures/gui/sphinx/no_network.png");
	protected Set<Component> components = new HashSet<Component>();
	public WorldPos worldpos;
	public boolean flag;
	public Stack<Subscreen> subscreens = new Stack<Subscreen>();

	public abstract boolean canOpen();

	public abstract boolean isConnected();

	public abstract UUID getNetworkUUID();

	public abstract void resetNetwork();

	public void draw(int mouseX, int mouseY, float partialTicks) {
	};

	public void preDraw(boolean online, int mouseX, int mouseY, float partialTicks) {
	};

	public boolean needRequestNetworkPos() {
		return false;
	}

	@Override
	public void initGui() {
		if (!canOpen()) {
			mc.displayGuiScreen(null);
			return;
		}
		applyComponents();
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
				Iterator<Component> it = components.iterator();
				boolean flag = false;
				while (!flag && it.hasNext()) {
					Component c = it.next();
					flag = c.onKeyTyped(typedChar, keyCode);
				}
			} else {
				subscreens.peek().keyTyped(typedChar, keyCode);
			}
		}
	}

	@Override
	public void renderToolTip(ItemStack stack, int x, int y) {
		super.renderToolTip(stack, x, y);
	}

	protected void applyComponents() {
		components = new HashSet<Component>();
	}

	protected boolean isOnline() {
		if (!needRequestNetworkPos()) {
			return getNetwork() != null;
		}
		if (!flag) {
			requestNetworkPos();
		}
		return getNetwork() != null;
	}

	protected void requestNetworkPos() {
		if (!needRequestNetworkPos()) {
			throw new RuntimeException("DRRRRRRRRRRRR!");
		}
	};

	public void dragMap(int mouseX, int mouseY, float partialTicks) {
		float sensitive = 5.0F;
		if (mouseX <= 1) {
			partialMoveX -= partialTicks * sensitive;
		}
		if (mouseX >= width - 1) {
			partialMoveX += partialTicks * sensitive;
		}
		if (mouseY <= 1) {
			partialMoveY -= partialTicks * sensitive;
		}
		if (mouseY >= height - 1) {
			partialMoveY += partialTicks * sensitive;
		}
		MapHandler.dragMap((int) partialMoveX, (int) partialMoveY);
		partialMoveX -= (int) partialMoveX;
		partialMoveY -= (int) partialMoveY;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		if (!canOpen()) {
			mc.displayGuiScreen(null);
			return;
		}
		boolean flag = isOnline();
		preDraw(flag, mouseX, mouseY, partialTicks);
		if (flag) {
			if (subscreens.isEmpty()) {
				dragMap(mouseX, mouseY, partialTicks);
			}
			MapHandler.draw(getNetwork(), width, height);
			draw(mouseX, mouseY, partialTicks);
		} else {
			Gui.drawRect(0, 0, mc.displayWidth, mc.displayHeight, 0xFF003434);
			Util.drawTexture(TEXTURE_NO_NETWORK, width / 2 - 32, height / 2 - 32, 256, 256, 0.25F);
			String text = I18n.format("sphinx.no_network");
			fontRenderer.drawString(text, width / 2 - fontRenderer.getStringWidth(text) / 2, height / 2 + 45,
					0xFFE0E0E0);
		}
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

	public WorldPos getNetwork() {
		if (!needRequestNetworkPos()) {
			throw new RuntimeException("DRRRRRRRRRRRR!");
		}
		return worldpos;
	}

	public void setWorldPos(WorldPos pos, UUID uuid) {
		if (!needRequestNetworkPos()) {
			throw new RuntimeException("DRRRRRRRRRRRR!");
		}
		if (uuid.equals(getNetworkUUID())) {
			if (pos == null) {
				resetNetwork();
			}
			worldpos = pos;
			flag = true;
		}
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
				screen.onDrag(mouseX - screen.x, mouseY - screen.y, moveX, moveY, clickedMouseButton);
			}
			return;
		}
		onDragScreen(mouseX, mouseY, moveX, moveY, clickedMouseButton);
	}

	protected void onDragScreen(int mouseX, int mouseY, int moveX, int moveY, int clickedMouseButton) {

	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		clickX = mouseX;
		clickY = mouseY;
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int button) {
		boolean flag = false;
		if ((clickX == -1 || Math.abs(mouseX - clickX) < 5) && (clickY == -1 || Math.abs(mouseY - clickY) < 5)) {
			if (subscreens.empty()) {
				Iterator<Component> it = components.iterator();
				while (it.hasNext()) {
					Component c = it.next();
					int x = mouseX - c.x;
					int y = mouseY - c.y;
					if (x >= 0 && x <= c.width && y >= 0 && y <= c.height) {
						if (c.onClickScreen(x, y, button != 1)) {
							flag = true;
							break;
						}
					}
				}
			} else {
				Subscreen screen = subscreens.peek();
				if (mouseX >= screen.x && mouseX <= screen.x + width && mouseY >= screen.y
						&& mouseY <= screen.y + height) {
					screen.onClick(mouseX - screen.x, mouseY - screen.y, button != 1);
				}
			}
		}
		onMouseReleaseScreen(mouseX, mouseY, button, flag);
		lastMouseX = -1;
		lastMouseY = -1;
		clickX = -1;
		clickY = -1;
		super.mouseReleased(mouseX, mouseY, button);
	}

	protected void onMouseReleaseScreen(int mouseX, int mouseY, int button, boolean flag) {

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

	@Override
	public ItemStack getTerminal() {
		return null;
	}
}