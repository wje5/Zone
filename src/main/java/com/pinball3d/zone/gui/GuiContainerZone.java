package com.pinball3d.zone.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.pinball3d.zone.gui.component.Component;
import com.pinball3d.zone.util.Util;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public abstract class GuiContainerZone extends GuiContainer implements IHasComponents, IHasSubscreen {
	public static final ResourceLocation SPHINX_ICONS = new ResourceLocation("zone:textures/gui/sphinx/icons.png");
	public static final ResourceLocation ICONS_3 = new ResourceLocation("zone:textures/gui/sphinx/icons_3.png");
	public static final ResourceLocation ICONS_4 = new ResourceLocation("zone:textures/gui/sphinx/icons_4.png");
	public static final ResourceLocation ICONS_5 = new ResourceLocation("zone:textures/gui/sphinx/icons_5.png");
	public static final ResourceLocation TEXTURE_NO_NETWORK = new ResourceLocation(
			"zone:textures/gui/sphinx/no_network.png");
	private boolean inited;
	private int lastMouseX, lastMouseY, clickX, clickY;
	protected float partialMoveX, partialMoveY;
	protected List<Component> components = new ArrayList<Component>();
	protected Stack<Subscreen> subscreens = new Stack<Subscreen>();

	public GuiContainerZone(ContainerZone container) {
		super(container);
	}

	protected void draw(int mouseX, int mouseY, float partialTicks) {

	}

	protected void drawForeground(int mouseX, int mouseY) {

	}

	protected void update() {

	}

	protected void init() {

	}

	public void onMouseScrolling(int mouseX, int mouseY, boolean isUp, boolean flag) {

	}

	protected void onKetInput(char typedChar, int keyCode) {

	}

	protected void onDragScreen(int mouseX, int mouseY, int moveX, int moveY, int clickedMouseButton) {

	}

	protected void onMouseReleaseScreen(int mouseX, int mouseY, int button, boolean flag) {

	}

	protected void onMousePressScreen(int mouseX, int mouseY, int button) {

	}

	protected void setSize() {
		xSize = width;
		ySize = height;
	}

	@Override
	public void initGui() {
		setSize();
		super.initGui();
		refreshComponents();
		if (!inited) {
			init();
			inited = true;
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		int d = Mouse.getDWheel();
		if (d != 0) {
			if (subscreens.empty()) {
				Iterator<Component> it = components.iterator();
				boolean flag = false;
				while (it.hasNext()) {
					Component c = it.next();
					int x = mouseX - c.getX();
					int y = mouseY - c.getY();
					if (x >= 0 && x <= c.width && y >= 0 && y <= c.height) {
						if (c.onMouseScroll(x, y, d < 0)) {
							flag = true;
							break;
						}
					}
				}
				onMouseScrolling(mouseX, mouseY, d < 0, flag);
			} else {
				Subscreen screen = subscreens.peek();
				int x = mouseX - screen.x;
				int y = mouseY - screen.y;
				if (x >= 0 && x <= screen.width && y >= 0 && y <= screen.height) {
					screen.onMouseScrollScreen(x, y, d < 0);
				}
			}
		}
		drawDefaultBackground();
		GlStateManager.pushMatrix();
		draw(mouseX, mouseY, partialTicks);
		GlStateManager.popMatrix();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		if (subscreens.isEmpty()) {
			renderHoveredToolTip(mouseX, mouseY);
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		Util.resetOpenGl();
		GlStateManager.pushMatrix();
		GlStateManager.translate(-guiLeft, -guiTop, 0);
		components.forEach(e -> {
			if (!e.getRenderLast(mouseX - e.getX(), mouseY - e.getY())) {
				Util.resetOpenGl();
				GlStateManager.pushMatrix();
				e.doRenderScreen(mouseX - e.getX(), mouseY - e.getY());
				GlStateManager.popMatrix();
			}
		});
		GlStateManager.popMatrix();
		Util.resetOpenGl();
		GlStateManager.pushMatrix();
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		GlStateManager.popMatrix();
		Util.resetOpenGl();
		GlStateManager.pushMatrix();
		GlStateManager.translate(-guiLeft, -guiTop, 0);
		components.forEach(e -> {
			if (e.getRenderLast(mouseX - e.getX(), mouseY - e.getY())) {
				Util.resetOpenGl();
				GlStateManager.pushMatrix();
				e.doRenderScreen(mouseX - e.getX(), mouseY - e.getY());
				GlStateManager.popMatrix();
			}
		});
		GlStateManager.popMatrix();
		Util.resetOpenGl();
		GlStateManager.pushMatrix();
		GlStateManager.translate(-guiLeft, -guiTop, 0);
		Iterator<Subscreen> it = subscreens.iterator();
		while (it.hasNext()) {
			Subscreen screen = it.next();
			if (screen.dead) {
				it.remove();
			} else {
				boolean flag = false;
				if (it.hasNext()) {
					List<Subscreen> l = subscreens.subList(subscreens.indexOf(screen) + 1, subscreens.size());
					Iterator<Subscreen> it2 = l.iterator();
					while (it2.hasNext()) {
						if (it2.next().isBlockOtherSubscreen()) {
							flag = true;
						}
					}
				}
				if (!flag) {
					GlStateManager.pushMatrix();
					screen.doRenderScreen(mouseX - screen.x, mouseY - screen.y);
					GlStateManager.popMatrix();
					Util.resetOpenGl();
				}
			}
		}
		GlStateManager.popMatrix();
		Util.resetOpenGl();
		GlStateManager.pushMatrix();
		GlStateManager.translate(-guiLeft, -guiTop, 0);
		drawForeground(mouseX, mouseY);
		GlStateManager.popMatrix();
		Util.resetOpenGl();
	}

	@Override
	public void onGuiClosed() {
		Iterator<Subscreen> it = subscreens.iterator();
		while (it.hasNext()) {
			Subscreen s = it.next();
			it.remove();
			s.close();

		}
		super.onGuiClosed();
	}

	public boolean onQuit() {
		return true;
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (keyCode == Keyboard.KEY_ESCAPE) {
			if (subscreens.empty()) {
				if (onQuit()) {
					super.keyTyped(typedChar, keyCode);
				}
				onKetInput(typedChar, keyCode);
			} else if (subscreens.peek().onQuit()) {
				subscreens.pop().close();
			}
		} else {
			if (subscreens.empty()) {
				Iterator<Component> it = components.iterator();
				boolean flag = false;
				while (!flag && it.hasNext()) {
					Component c = it.next();
					flag = c.onKeyTyped(typedChar, keyCode);
				}
				onKetInput(typedChar, keyCode);
			} else {
				subscreens.peek().keyTyped(typedChar, keyCode);
			}
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
			screen.onDragScreen(mouseX, mouseY, moveX, moveY, clickedMouseButton);
			return;
		}
		onDragScreen(mouseX, mouseY, moveX, moveY, clickedMouseButton);
	}

	protected boolean onMouseReleaseScreenPre(int mouseX, int mouseY, int button) {
		return false;
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int button) {
		if (!onMouseReleaseScreenPre(mouseX, mouseY, button)) {
			boolean flag = false;
			boolean isClick = (clickX == -1 || Math.abs(mouseX - clickX) < 5)
					&& (clickY == -1 || Math.abs(mouseY - clickY) < 5);
			if (subscreens.empty()) {
				Iterator<Component> it = components.iterator();
				while (it.hasNext()) {
					Component c = it.next();
					int x = mouseX - c.getX();
					int y = mouseY - c.getY();
					if (x >= 0 && x <= c.width && y >= 0 && y <= c.height) {
						if (c.onClickScreen(x, y, button != 1)) {
							flag = true;
							break;
						}
					}
				}
			} else {
				Subscreen screen = subscreens.peek();
				screen.onClickScreen(mouseX, mouseY, button != 1, isClick);
				flag = true;
			}
			onMouseReleaseScreen(mouseX, mouseY, button, flag);
		}
		lastMouseX = -1;
		lastMouseY = -1;
		clickX = -1;
		clickY = -1;
		super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		clickX = mouseX;
		clickY = mouseY;
		onMousePressScreen(mouseX, mouseY, mouseButton);
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public void renderToolTip(ItemStack stack, int x, int y) {
		super.renderToolTip(stack, x, y);
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		update();
	}

	@Override
	public int getX() {
		return 0;
	}

	@Override
	public int getY() {
		return 0;
	}

	@Override
	public void addComponent(Component c) {
		components.add(c);
	}

	@Override
	public List<Component> getComponents() {
		return components;
	}

	@Override
	public void removeComponents() {
		components.clear();
	}

	@Override
	public void putScreen(Subscreen screen) {
		subscreens.push(screen);
	}

	@Override
	public void removeScreen(Subscreen screen) {
		screen.close();
		subscreens.remove(screen);
	}

	@Override
	public Stack<Subscreen> getSubscreens() {
		return subscreens;
	}
}
