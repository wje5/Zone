package com.pinball3d.zone.gui;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.pinball3d.zone.network.ConnectionHelper.Type;
import com.pinball3d.zone.sphinx.IHasComponents;
import com.pinball3d.zone.sphinx.IHasSubscreen;
import com.pinball3d.zone.sphinx.container.GuiContainerNetworkBase;
import com.pinball3d.zone.util.Util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

public class Subscreen implements IHasComponents {
	public static Minecraft mc = Minecraft.getMinecraft();
	public boolean renderCover;
	public int width, height;
	public int x, y;
	protected IHasSubscreen parent;
	protected Set<Component> components = new HashSet<Component>();
	protected Component draggingComponent;
	public boolean dead, inited;

	public Subscreen(IHasSubscreen parent, int x, int y, int width, int height, boolean rendercover) {
		this.parent = parent;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		renderCover = rendercover;
	}

	public static int getDisplayWidth() {
		if (Minecraft.getMinecraft().currentScreen != null) {
			return Minecraft.getMinecraft().currentScreen.width;
		}
		return 0;
	}

	public static int getDisplayHeight() {
		if (Minecraft.getMinecraft().currentScreen != null) {
			return Minecraft.getMinecraft().currentScreen.height;
		}
		return 0;
	}

	public void doRender(int mouseX, int mouseY) {
		update();
		Util.resetOpenGl();
		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 0, 300F);
		if (renderCover) {
			Minecraft mc = Minecraft.getMinecraft();
			Gui.drawRect(0, 0, mc.displayWidth, mc.displayHeight, 0x8F000000);
		}
		Util.resetOpenGl();
		GlStateManager.pushMatrix();
		doRenderBackground(mouseX, mouseY);
		GlStateManager.popMatrix();
		components.forEach(e -> {
			if (!e.getRenderLast()) {
				Util.resetOpenGl();
				GlStateManager.pushMatrix();
				e.doRender(mouseX, mouseY);
				GlStateManager.popMatrix();
			}
		});
		Util.resetOpenGl();
		GlStateManager.pushMatrix();
		doRenderForeground(mouseX, mouseY);
		GlStateManager.popMatrix();
		components.forEach(e -> {
			if (e.getRenderLast()) {
				Util.resetOpenGl();
				GlStateManager.pushMatrix();
				e.doRender(mouseX, mouseY);
				GlStateManager.popMatrix();
			}
		});
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
		if (!getDataTypes().isEmpty() && parent instanceof GuiContainerNetworkBase) {
			((GuiContainerNetworkBase) parent).sendReq();
		}
	}

	public Set<Type> getDataTypes() {
		return new HashSet<Type>();
	}

	public boolean onClickScreen(int x, int y, boolean isLeft) {
		draggingComponent = null;
		if (x >= this.x && x <= this.x + width && y >= this.y && y <= this.y + height) {
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
			onClick(x, y, isLeft);
			return true;
		}
		return false;
	}

	public void onClick(int x, int y, boolean isLeft) {

	}

	public boolean onDragScreen(int x, int y, int moveX, int moveY, int button) {
		if (x >= this.x && x <= this.x + width && y >= this.y && y <= this.y + height) {
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
			return true;
		}
		draggingComponent = null;
		return false;
	}

	public void onDrag(int x, int y, int moveX, int moveY, int button) {

	}

	public boolean onMouseScrollScreen(int x, int y, boolean isUp) {
		if (x >= this.x && x <= this.x + width && y >= this.y && y <= this.y + height) {
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
			return true;
		}
		return false;
	}

	public void onMouseScroll(int x, int y, boolean isUp) {

	}

	public boolean onQuit() {
		return true;
	}

	public void close() {
		if (!getDataTypes().isEmpty() && parent instanceof GuiContainerNetworkBase) {
			((GuiContainerNetworkBase) parent).sendReq();
		}
	}

	public void keyTyped(char typedChar, int keyCode) {
		Iterator<Component> it = components.iterator();
		boolean flag = false;
		while (!flag && it.hasNext()) {
			Component c = it.next();
			flag = c.onKeyTyped(typedChar, keyCode);
		}
	}

	public boolean isBlockOtherSubscreen() {
		return false;
	}

	@Override
	public Set<Component> getComponents() {
		return components;
	}

	@Override
	public void addComponent(Component c) {
		components.add(c);
	}

	@Override
	public void removeComponents() {
		components.clear();
	}
}
