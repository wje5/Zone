package com.pinball3d.zone.gui;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.pinball3d.zone.gui.component.Component;
import com.pinball3d.zone.network.ConnectionHelper.Type;
import com.pinball3d.zone.sphinx.container.GuiContainerNetworkBase;
import com.pinball3d.zone.util.Util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class Subscreen implements IHasComponents {
	public static final ResourceLocation UI_BORDER = new ResourceLocation("zone:textures/gui/sphinx/ui_border.png");
	public static final ResourceLocation ICONS = new ResourceLocation("zone:textures/gui/sphinx/icons.png");
	public static final ResourceLocation ICONS_4 = new ResourceLocation("zone:textures/gui/sphinx/icons_4.png");
	public static final ResourceLocation ICONS_5 = new ResourceLocation("zone:textures/gui/sphinx/icons_5.png");
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
		Util.resetOpenGl();
		GlStateManager.pushMatrix();
		doRenderBackground(mouseX, mouseY);
		components.forEach(e -> {
			if (!e.getRenderLast()) {
				Util.resetOpenGl();
				GlStateManager.pushMatrix();
				e.doRenderScreen(mouseX - e.getX(), mouseY - e.getY(), 0, 0);
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
				e.doRenderScreen(mouseX - e.getX(), mouseY - e.getY(), 0, 0);
				GlStateManager.popMatrix();
			}
		});
		GlStateManager.popMatrix();
	}

	public void doRenderScreen(int mouseX, int mouseY) {
		update();
		Util.resetOpenGl();
		if (renderCover) {
			Minecraft mc = Minecraft.getMinecraft();
			Gui.drawRect(0, 0, mc.displayWidth, mc.displayHeight, 0x8F000000);
		}
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, 300F);
		doRender(mouseX, mouseY);
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

	public boolean onClickScreen(int mouseX, int mouseY, boolean isLeft, boolean isClick) {
		if (draggingComponent != null) {
			draggingComponent.onStopDrag();
		}
		draggingComponent = null;
		if (isClick && mouseX >= this.x && mouseX <= this.x + width && mouseY >= this.y && mouseY <= this.y + height) {
			mouseX -= this.x;
			mouseY -= this.y;
			Iterator<Component> it = components.iterator();
			while (it.hasNext()) {
				Component c = it.next();
				if (mouseX >= c.getX() && mouseX <= c.getX() + c.width && mouseY >= c.getY()
						&& mouseY <= c.getY() + c.height) {
					if (c.onClickScreen(mouseX - c.getX(), mouseY - c.getY(), isLeft)) {
						return true;
					}
				}
			}
			onClick(mouseX, mouseY, isLeft);
			return true;
		}
		return false;
	}

	public void onClick(int x, int y, boolean isLeft) {

	}

	public void onDragScreen(int mouseX, int mouseY, int moveX, int moveY, int button) {
		boolean flag = true;
		mouseX -= x;
		mouseY -= y;
		if (draggingComponent == null) {
			if (mouseX >= 0 && mouseX <= width && mouseY >= 0 && mouseY <= height) {
				Iterator<Component> it = components.iterator();
				while (it.hasNext()) {
					Component c = it.next();
					int cX = mouseX - c.getX();
					int cY = mouseY - c.getY();
					if (cX >= 0 && cX <= c.width && cY >= 0 && cY <= c.height) {
						if (c.onDragScreen(cX, cY, moveX, moveY)) {
							draggingComponent = c;
							break;
						}
					}
				}
			}

		} else if (!draggingComponent.onDragScreen(mouseX - draggingComponent.getX(), mouseY - draggingComponent.getY(),
				moveX, moveY)) {
			draggingComponent.onStopDrag();
			draggingComponent = null;
			flag = false;
		}
		if (flag && draggingComponent == null) {
			onDrag(mouseX, mouseY, moveX, moveY, button);
		}
	}

	public void onDrag(int mouseX, int mouseY, int moveX, int moveY, int button) {

	}

	public void onMouseScrollScreen(int mouseX, int mouseY, boolean isUp) {
		Iterator<Component> it = components.iterator();
		while (it.hasNext()) {
			Component c = it.next();
			int cX = mouseX - c.getX();
			int cY = mouseY - c.getY();
			if (cX >= 0 && cX <= c.width && cY >= 0 && cY <= c.height) {
				if (c.onMouseScroll(cX, cY, isUp)) {
					return;
				}
			}
		}
		onMouseScroll(mouseX, mouseY, isUp);
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
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
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
