package com.pinball3d.zone.sphinx;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

import org.lwjgl.input.Keyboard;

import com.pinball3d.zone.tileentity.TEProcessingCenter;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ScreenSphinxController extends GuiScreen implements IParent {
	private int lastMouseX, lastMouseY;
	private int clickX, clickY;
	public String password;
	private static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/gui/sphinx/icons.png");
	private Set<Component> components = new HashSet<Component>();
	public Stack<Subscreen> subscreens = new Stack<Subscreen>();
	public TEProcessingCenter tileentity;

	public ScreenSphinxController(TEProcessingCenter te, String password) {
		this.password = password;
		tileentity = te;
	}

	public boolean checkTileentity() {
		if (tileentity == null) {
			mc.displayGuiScreen(null);
			return false;
		}
		return true;
	}

	@Override
	public void renderToolTip(ItemStack stack, int x, int y) {
		super.renderToolTip(stack, x, y);
	}

	@Override
	public void initGui() {
		if (tileentity == null) {
			mc.displayGuiScreen(null);
			return;
		}
		applyComponents();
		if (tileentity.needInit() && subscreens.empty()) {
			subscreens.add(new SubscreenSphinxInitWizard(this));
		}
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
				while (flag && it.hasNext()) {
					Component c = it.next();
					flag = c.onKeyTyped(typedChar, keyCode);
				}
			} else {
				subscreens.peek().keyTyped(typedChar, keyCode);
			}
		}
	}

	private void applyComponents() {
		components.clear();
		components.add(new TexturedButton(this, width - 30, 3, TEXTURE, 94, 68, 22, 30, 0.25F, new Runnable() {
			@Override
			public void run() {
				subscreens.push(new SubscreenViewStorage((ScreenSphinxController) mc.currentScreen));
			}
		}));
		components.add(new TexturedButton(this, width - 20, 2, TEXTURE, 0, 68, 32, 32, 0.25F, new Runnable() {
			@Override
			public void run() {
				subscreens.push(new SubscreenSphinxConfig((ScreenSphinxController) mc.currentScreen));
			}
		}));
		components.add(new TexturedButton(this, width - 8, 3, TEXTURE, 0, 42, 24, 26, 0.25F, new Runnable() {
			@Override
			public void run() {
				subscreens.push(new SubscreenShutdownSphinx((ScreenSphinxController) mc.currentScreen));
			}
		}));
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		if (!checkTileentity()) {
			return;
		}
		if (!tileentity.isOn()) {
			mc.displayGuiScreen(null);
			return;
		}
		MapHandler.draw(new WorldPos(tileentity.getPos(), tileentity.getWorld()), width, height);
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
				boolean flag = false;
				while (it.hasNext()) {
					Component c = it.next();
					int x = mouseX - c.x;
					int y = mouseY - c.y;
					if (x >= 0 && x <= c.width && y >= 0 && y <= c.height) {
						if (c.onClickScreen(x, y, state != 1)) {
							flag = true;
							break;
						}
					}
				}
				if (!flag) {
					MapHandler.onClick(width, height, mouseX, mouseY);
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
