package com.pinball3d.zone.sphinx;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

import com.pinball3d.zone.network.ConnectionHelper.Type;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;

public class Subscreen implements IParent {
	public static int displayWidth = Minecraft.getMinecraft().displayWidth / 2;
	public static int displayHeight = Minecraft.getMinecraft().displayHeight / 2;
	public static Minecraft mc = Minecraft.getMinecraft();
	public boolean renderCover;
	public int width, height;
	public int x, y;
	protected IParent parent;
	protected Set<Component> components = new HashSet<Component>();
	protected Component draggingComponent;
	public Stack<Subscreen> subscreens = new Stack<Subscreen>();
	public boolean dead, inited;

	public Subscreen(IParent parent, int x, int y, int width, int height, boolean rendercover) {
		this.parent = parent;
		this.x = x + parent.getXOffset();
		this.y = y + parent.getYOffset();
		this.width = width;
		this.height = height;
		this.x = this.x + width > displayWidth / 2 ? displayWidth / 2 - width : this.x;
		this.y = this.y + height > displayHeight / 2 ? displayHeight / 2 - height : this.y;
		renderCover = rendercover;
	}

	public void doRender(int mouseX, int mouseY) {
		update();
		GlStateManager.pushMatrix();
		GlStateManager.disableLighting();
		GlStateManager.disableDepth();
		GlStateManager.disableBlend();
		GlStateManager.translate(0, 0, 300F);
		if (renderCover) {
			Minecraft mc = Minecraft.getMinecraft();
			Gui.drawRect(0, 0, mc.displayWidth, mc.displayHeight, 0x8F000000);
		}
		doRenderBackground(mouseX, mouseY);
		GlStateManager.disableLighting();
		GlStateManager.disableDepth();
		GlStateManager.disableBlend();
		components.forEach(e -> {
			e.doRender(mouseX, mouseY);
		});
		doRenderForeground(mouseX, mouseY);
		GlStateManager.disableLighting();
		GlStateManager.disableDepth();
		GlStateManager.disableBlend();
		Iterator<Subscreen> it = subscreens.iterator();
		while (it.hasNext()) {
			Subscreen screen = it.next();
			if (screen.dead) {
				it.remove();
			} else {
				screen.doRender(mouseX, mouseY);
			}
		}
		GlStateManager.enableLighting();
		GlStateManager.enableDepth();
		GlStateManager.enableBlend();
		GlStateManager.popMatrix();
	}

	public void doRenderForeground(int mouseX, int mouseY) {

	}

	public void doRenderBackground(int mouseX, int mouseY) {

	}

	public void update() {
		if (!inited) {
			init();
			inited = true;
		}
	}

	public void init() {
		IParent p = parent;
		while (true) {
			if (p instanceof Subscreen) {
				p = ((Subscreen) p).parent;
				continue;
			} else if (p instanceof ScreenSphinxBase) {
				((ScreenSphinxBase) p).sendReq();
			}
			return;
		}
	}

	public Set<Type> getDataTypes() {
		return new HashSet<Type>();
	}

	public boolean onClickScreen(int x, int y, boolean isLeft) {
		draggingComponent = null;
		if (x >= this.x && x <= this.x + width && y >= this.y && y <= this.y + height) {
			if (subscreens.empty()) {
				Iterator<Component> it = components.iterator();
				while (it.hasNext()) {
					Component c = it.next();
					int cX = x - c.x;
					int cY = y - c.y;
					if (cX >= 0 && cX <= c.width && cY >= 0 && cY <= c.height) {
						if (c.onClickScreen(cX, cY, isLeft)) {
							return true;
						}
					}
				}
				onClick(x - this.x, y - this.y, isLeft);
			} else {
				Subscreen screen = subscreens.peek();
				screen.onClickScreen(x, y, isLeft);
			}
			return true;
		}
		return false;
	}

	public void onClick(int x, int y, boolean isLeft) {

	}

	public boolean onDragScreen(int x, int y, int moveX, int moveY, int button) {
		if (x >= this.x && x <= this.x + width && y >= this.y && y <= this.y + height) {
			if (subscreens.empty()) {
				boolean flag = true;
				if (draggingComponent == null) {
					Iterator<Component> it = components.iterator();
					while (it.hasNext()) {
						Component c = it.next();
						int cX = x - c.x;
						int cY = y - c.y;
						if (cX >= 0 && cX <= c.width && cY >= 0 && cY <= c.height) {
							if (c.onDrag(x - c.x, y - c.y, moveX, moveY)) {
								draggingComponent = c;
								break;
							}
						}
					}
				} else if (!draggingComponent.onDrag(x - draggingComponent.x, y - draggingComponent.y, moveX, moveY)) {
					draggingComponent = null;
					flag = false;
				}
				if (flag && draggingComponent == null) {
					onDrag(x, y, moveX, moveY, button);
				}
			} else {
				Subscreen screen = subscreens.peek();
				if (x >= screen.x && x <= screen.x + width && y >= screen.y && y <= screen.y + height) {
					screen.onDragScreen(x - screen.x, y - screen.y, moveX, moveY, button);
				}
			}
			return true;
		}
		draggingComponent = null;
		return false;
	}

	public void onDrag(int x, int y, int moveX, int moveY, int button) {

	}

	public boolean onMouseScrollScreen(int x, int y, boolean isUp) {
		if (x >= this.x && x <= this.x + width && y >= this.y && y <= this.y + height) {
			if (subscreens.empty()) {
				Iterator<Component> it = components.iterator();
				while (it.hasNext()) {
					Component c = it.next();
					int cX = x - c.x;
					int cY = y - c.y;
					if (cX >= 0 && cX <= c.width && cY >= 0 && cY <= c.height) {
						if (c.onMouseScroll(cX, cY, isUp)) {
							return true;
						}
					}
				}
				onMouseScroll(x - this.x, y - this.y, isUp);
			} else {
				Subscreen screen = subscreens.peek();
				screen.onMouseScrollScreen(x, y, isUp);
			}
			return true;
		}
		return false;
	}

	public void onMouseScroll(int x, int y, boolean isUp) {

	}

	public boolean onQuit() {
		if (subscreens.empty()) {
			return true;
		}
		if (subscreens.peek().onQuit()) {
			subscreens.pop();
		}
		return false;
	}

	public void close() {

	}

	public void keyTyped(char typedChar, int keyCode) {
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
		return x;
	}

	@Override
	public int getYOffset() {
		return y;
	}

	@Override
	public FontRenderer getFontRenderer() {
		return parent.getFontRenderer();
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
		return parent.getTerminal();
	}
}
