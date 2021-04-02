package com.pinball3d.zone.gui.component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
		int yOffset = 0;
		mouseY += scrollingDistance;
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
	public void doRender(int mouseX, int mouseY, int upCut, int downCut) {
		super.doRender(mouseX, mouseY, upCut, downCut);
		Iterator<Container> it = list.iterator();
		int yOffset = 0;
		for (int i = 0; i < 2; i++) {
			while (it.hasNext()) {
				Container c = it.next();
				int renderY = yOffset - scrollingDistance;
				if (renderY <= height && renderY + c.height >= 0) {
					int renderUpCut = renderY < upCut ? -renderY + upCut : upCut;
					int renderDownCut = renderY + c.height - height + downCut > 0
							? renderY + c.height - height + downCut
							: 0;
					boolean flag = mouseX >= 0 && mouseX <= c.width && mouseY > renderY
							&& mouseY <= renderY + c.height - renderDownCut;
					if (i == 1 == c.getRenderLast() && upCut + downCut < c.height) {
						Util.resetOpenGl();
						GlStateManager.pushMatrix();
						c.doRenderScreen(mouseX - c.getX(), mouseY - c.getY(), renderUpCut, renderDownCut);
						if (flag) {
							Gui.drawRect(c.getX(), c.getY() + renderUpCut, c.getX() + c.width,
									c.getY() + c.height - renderDownCut, 0x4FFFFFFF);
						}
						GlStateManager.popMatrix();
					}
				}
				yOffset += c.height;
			}
		}
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
		scrollingDistance = scrollingDistance > length - height ? length - height : scrollingDistance;
		scrollingDistance = scrollingDistance < 0 ? 0 : scrollingDistance;
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

	@Deprecated
	@Override
	public Set<Component> getComponents() {
		return new HashSet<Component>(list);
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
	}
}
