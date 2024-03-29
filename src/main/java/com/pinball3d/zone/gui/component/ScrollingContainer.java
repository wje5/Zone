package com.pinball3d.zone.gui.component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.pinball3d.zone.gui.IHasComponents;
import com.pinball3d.zone.util.Util;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

public class ScrollingContainer extends Component implements IHasComponents {
	protected int length, scrollingDistance;
	protected List<Container> list = new ArrayList<Container>();

	public ScrollingContainer(IHasComponents parent, int x, int y, int width, int height) {
		super(parent, x, y, width, height);
		this.parent = parent;
	}

	@Override
	public boolean onClickScreen(int mouseX, int mouseY, boolean isLeft) {
		if (super.onClickScreen(mouseX, mouseY, isLeft)) {
			return true;
		}
		Iterator<Container> it = list.iterator();
		while (it.hasNext()) {
			Container c = it.next();
			int cX = mouseX - c.getX();
			int cY = mouseY - c.getY();
			if (cX >= 0 && cX <= c.width && cY >= 0 && cY <= c.height) {
				if (c.onClickScreen(cX, cY, isLeft)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void doRender(int mouseX, int mouseY) {
		super.doRender(mouseX, mouseY);
		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 0, -800F);
		GlStateManager.enableDepth();
		GlStateManager.depthFunc(GL11.GL_GEQUAL);
		Gui.drawRect(0, 0, width, height, 0x651CC3B5);
		GlStateManager.depthFunc(GL11.GL_LEQUAL);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		for (int i = 0; i < 2; i++) {
			int yOffset = 0;
			Iterator<Container> it = list.iterator();
			while (it.hasNext()) {
				Container c = it.next();
				int renderY = yOffset - scrollingDistance;
				if (renderY <= height && renderY + c.height >= 0) {
					int renderUpCut = renderY < 0 ? -renderY : 0;
					int renderDownCut = renderY + c.height - height > 0 ? renderY + c.height - height : 0;
					boolean flag = mouseX >= 0 && mouseX <= c.width && mouseY > renderY
							&& mouseY <= renderY + c.height - renderDownCut;
					if ((i == 1) == c.getRenderLast(mouseX - c.getX(), mouseY - c.getY())
							&& renderUpCut + renderDownCut < c.height) {
						Util.resetOpenGl();
						GlStateManager.pushMatrix();
						c.doRenderScreen(mouseX - c.getX(), mouseY - c.getY());
						GlStateManager.popMatrix();
					}
					if (i == 0 && flag && !Util.isCovered(this)) {
						Util.resetOpenGl();
						Gui.drawRect(c.getX(), c.getY() + renderUpCut, c.getX() + c.width,
								c.getY() + c.height - renderDownCut, 0x4FFFFFFF);
					}
				}
				yOffset += c.height;
			}
		}
		GlStateManager.popMatrix();
		GlStateManager.depthFunc(515);
		GlStateManager.disableDepth();
	}

	@Override
	public boolean onDrag(int mouseX, int mouseY, int moveX, int moveY) {
		if (super.onDrag(mouseX, mouseY, moveX, moveY)) {
			return true;
		}
		if (enable != null && !enable.getAsBoolean()) {
			return false;
		}
		scrollingDistance -= moveY;
		checkScrollingDistance();
		return true;
	}

	@Override
	public boolean onMouseScroll(int mouseX, int mouseY, boolean isUp) {
		if (super.onMouseScroll(mouseX, mouseY, isUp)) {
			return true;
		}
		if (enable != null && !enable.getAsBoolean()) {
			return false;
		}
		scrollingDistance += isUp ? 15 : -15;
		checkScrollingDistance();
		return true;
	}

	@Override
	public boolean onKeyTyped(char typedChar, int keyCode) {
		if (super.onKeyTyped(typedChar, keyCode)) {
			return true;
		}
		Iterator<Container> it = list.iterator();
		while (it.hasNext()) {
			Component c = it.next();
			if (c.onKeyTyped(typedChar, keyCode)) {
				return true;
			}
		}
		return false;
	}

	public void setScrollingDistance(int scrollingDistance) {
		this.scrollingDistance = scrollingDistance;
		checkScrollingDistance();
	}

	public void checkScrollingDistance() {
		scrollingDistance = scrollingDistance > length - height ? length - height : scrollingDistance;
		scrollingDistance = scrollingDistance < 0 ? 0 : scrollingDistance;
	}

	@Deprecated
	@Override
	public List<Component> getComponents() {
		return new ArrayList<Component>(list);
	}

	@Override
	public void addComponent(Component c) {
		if (c instanceof Container) {
			list.add((Container) c);
			c.setX(0);
			int a = length;
			c.setYSupplier(() -> a - scrollingDistance);
			length += c.height;
		} else {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public void removeComponents() {
		list.clear();
		length = 0;
	}
}
