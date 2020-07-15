package com.pinball3d.zone.sphinx;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

import com.pinball3d.zone.tileentity.INeedNetwork;

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
	public boolean dead;

	public Subscreen(IParent parent, int x, int y, int width, int height, boolean rendercover) {
		this.parent = parent;
		this.x = x + parent.getXOffset();
		this.y = y + parent.getYOffset();
		this.width = width;
		this.height = height;
		renderCover = rendercover;
	}

	public void doRender(int mouseX, int mouseY) {
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
		components.forEach(e -> {
			e.doRender(mouseX, mouseY);
		});
		doRenderForeground(mouseX, mouseY);
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

	public void onClickScreen(int x, int y, boolean isLeft) {
		draggingComponent = null;
		if (x >= this.x && x <= this.x + width && y >= this.y && y <= this.y + height) {
			onClick(x - this.x, y - this.y, isLeft);
		}
	}

	public void onClick(int x, int y, boolean isLeft) {
		if (subscreens.empty()) {
			components.forEach(e -> {
				int cX = x + this.x - e.x;
				int cY = y + this.y - e.y;
				if (cX >= 0 && cX <= e.width && cY >= 0 && cY <= e.height) {
					e.onClickScreen(cX, cY, isLeft);
				}
			});
		} else {
			Subscreen screen = subscreens.peek();
			int x1 = x + this.x;
			int y1 = y + this.y;
			if (x1 >= screen.x && x1 <= screen.x + width && y1 >= screen.y && y1 <= screen.y + height) {
				screen.onClick(x1 - screen.x, y1 - screen.y, isLeft);
			}
		}
	}

	public void onDrag(int x, int y, int moveX, int moveY, boolean isLeft) {
		if (subscreens.empty()) {
			if (draggingComponent == null) {
				Iterator<Component> it = components.iterator();
				while (it.hasNext()) {
					Component c = it.next();
					if (x >= c.x - this.x && x <= c.x - this.x + c.width && y >= c.y - this.y
							&& y <= c.y - this.y + c.height) {
						draggingComponent = c;
					}
				}
			}
			if (draggingComponent != null) {
				draggingComponent.onDrag(x - draggingComponent.x + this.x, y - draggingComponent.y + this.y, moveX,
						moveY);
			}
		} else {
			Subscreen screen = subscreens.peek();
			int x1 = x + this.x;
			int y1 = y + this.y;
			if (x1 >= screen.x && x1 <= screen.x + width && y1 >= screen.y && y1 <= screen.y + height) {
				screen.onDrag(x1 - screen.x, y1 - screen.y, moveX, moveY, isLeft);
			}
		}
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

	public void keyTyped(char typedChar, int keyCode) {
		if (subscreens.empty()) {
			components.forEach(e -> {
				e.onKeyTyped(typedChar, keyCode);
			});
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

	@Override
	public INeedNetwork getNeedNetworkTileEntity() {
		return parent.getNeedNetworkTileEntity();
	}
}
