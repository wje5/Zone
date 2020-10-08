package com.pinball3d.zone.manual;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

import org.lwjgl.input.Keyboard;

import com.pinball3d.zone.sphinx.Component;
import com.pinball3d.zone.sphinx.IParent;
import com.pinball3d.zone.sphinx.MapHandler;
import com.pinball3d.zone.sphinx.Subscreen;
import com.pinball3d.zone.sphinx.Util;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public abstract class ScreenManualBase extends GuiScreen implements IParent {
	public static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/gui/manual.png");
	public static final ResourceLocation TEXTURE2 = new ResourceLocation("zone:textures/gui/manual_2.png");
	private int lastMouseX, lastMouseY;
	private int clickX, clickY;
	protected Set<Component> components = new HashSet<Component>();
	public Stack<Subscreen> subscreens = new Stack<Subscreen>();

	@Override
	public void initGui() {
		applyComponents();
		super.initGui();
	}

	protected void applyComponents() {
		components.clear();
		components.add(new ButtonPage(this, getXOffset() + 12, getYOffset() + 158, true, new Runnable() {
			@Override
			public void run() {
				onFlip(true);
			}
		}));
		components.add(new ButtonPage(this, getXOffset() + 270, getYOffset() + 158, false, new Runnable() {
			@Override
			public void run() {
				onFlip(false);
			}
		}));
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

	public abstract void onFlip(boolean flag);

	public abstract void drawContents(int mouseX, int mouseY);

	@Override
	public void renderToolTip(ItemStack stack, int x, int y) {
		super.renderToolTip(stack, x, y);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		int x = width / 2 - 146;
		int x2 = width / 2;
		int y = height / 2 - 90;
		Util.drawTexture(TEXTURE, x, y, 146, 180);
		Util.drawTexture(TEXTURE2, x2, y, 146, 180);
		drawContents(mouseX, mouseY);
		components.forEach(e -> {
			e.doRender(mouseX, mouseY);
		});
		components.forEach(e -> {
			if (e instanceof ItemFrame) {
				((ItemFrame) e).renderToolTip(mouseX, mouseY);
			} else if (e instanceof BlockShowWithTip) {
				((BlockShowWithTip) e).renderToolTip(mouseX, mouseY);
			}
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

	public void drawTextBlock(String key, int x, int y) {
		String s = I18n.format(key);
		String[] texts = s.split("\\\\n");
		for (int i = 0; i < texts.length; i++) {
			fontRenderer.drawString(texts[i], x, i * fontRenderer.FONT_HEIGHT + y, 0xFF000000);
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
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
				MapHandler.dragMap(-moveX, -moveY);
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
				Iterator<Component> it = components.iterator();
				while (it.hasNext()) {
					Component c = it.next();
					int x = mouseX - c.x;
					int y = mouseY - c.y;
					if (x >= 0 && x <= c.width && y >= 0 && y <= c.height) {
						if (c.onClickScreen(x, y, state != 1)) {
							break;
						}
					}
				}
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
		return 292;
	}

	@Override
	public int getHeight() {
		return 180;
	}

	@Override
	public int getXOffset() {
		return width / 2 - 146;
	}

	@Override
	public int getYOffset() {
		return height / 2 - 90;
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
