package com.pinball3d.zone.gui.component;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.pinball3d.zone.gui.IHasComponents;
import com.pinball3d.zone.util.Util;

import net.minecraft.client.renderer.GlStateManager;

public class Container extends Component implements IHasComponents {
	protected Set<Component> components = new HashSet<Component>();
	protected Component draggingComponent;

	public Container(IHasComponents parent, int x, int y, int width, int height) {
		super(parent, x, y, width, height);
	}

	@Override
	public boolean onClickScreen(int mouseX, int mouseY, boolean isLeft) {
		if (super.onClickScreen(mouseX, mouseY, isLeft)) {
			return true;
		}
		Iterator<Component> it = components.iterator();
		while (it.hasNext()) {
			Component c = it.next();
			int cX = mouseX - c.getX() + getX();
			int cY = mouseY - c.getY() + getY();
			if (cX >= 0 && cX <= c.width && cY >= 0 && cY <= c.height) {
				if (c.onClickScreen(cX, cY, isLeft)) {
					return true;
				}
			}
		}
		System.out.println(this);
		return false;
	}

	@Override
	public boolean onDrag(int mouseX, int mouseY, int moveX, int moveY) {
		if (super.onDrag(mouseX, mouseY, moveX, moveY)) {
			return true;
		}
		if (draggingComponent == null) {
			Iterator<Component> it = components.iterator();
			while (it.hasNext()) {
				Component c = it.next();
				int cX = mouseX - c.getX() + getX();
				int cY = mouseY - c.getY() + getY();
				if (cX >= 0 && cX <= c.width && cY >= 0 && cY <= c.height) {
					if (c.onDrag(mouseX - c.getX(), mouseY - c.getY(), moveX, moveY)) {
						draggingComponent = c;
						return true;
					}
				}
			}
		} else if (!draggingComponent.onDrag(mouseX - draggingComponent.getX(), mouseY - draggingComponent.getY(),
				moveX, moveY)) {
			draggingComponent = null;
		} else {
			return true;
		}
		return false;
	}

	@Override
	public void onStopDrag() {
		super.onStopDrag();
		if (draggingComponent != null) {
			draggingComponent.onStopDrag();
			draggingComponent = null;
		}
	}

	@Override
	public boolean onMouseScroll(int mouseX, int mouseY, boolean isUp) {
		if (super.onMouseScroll(mouseX, mouseY, isUp)) {
			return true;
		}
		Iterator<Component> it = components.iterator();
		while (it.hasNext()) {
			Component c = it.next();
			int cX = mouseX - c.getX() + getX();
			int cY = mouseY - c.getY() + getY();
			if (cX >= 0 && cX <= c.width && cY >= 0 && cY <= c.height) {
				if (c.onMouseScroll(mouseX, mouseY, isUp)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void doRender(int mouseX, int mouseY, int upCut, int downCut) {
		super.doRender(mouseX, mouseY, upCut, downCut);
		components.forEach(e -> {
			if (!e.getRenderLast()) {
				Util.resetOpenGl();
				GlStateManager.pushMatrix();
				e.doRenderScreen(mouseX - e.getX(), mouseY - e.getY(), upCut - e.getY() < 0 ? 0 : upCut,
						downCut - e.getY() + e.height < 0 ? 0 : downCut);
				GlStateManager.popMatrix();
			}
		});
		components.forEach(e -> {
			if (e.getRenderLast()) {
				Util.resetOpenGl();
				GlStateManager.pushMatrix();
				e.doRenderScreen(mouseX - e.getX(), mouseY - e.getY(), upCut - e.getY() < 0 ? 0 : upCut,
						downCut - e.getY() + e.height < 0 ? 0 : downCut);
				GlStateManager.popMatrix();
			}
		});
	}

	@Override
	public boolean onKeyTyped(char typedChar, int keyCode) {
		if (super.onKeyTyped(typedChar, keyCode)) {
			return true;
		}
		Iterator<Component> it = components.iterator();
		while (it.hasNext()) {
			Component c = it.next();
			if (c.onKeyTyped(typedChar, keyCode)) {
				return true;
			}
		}
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
