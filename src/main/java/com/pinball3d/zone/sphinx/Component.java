package com.pinball3d.zone.sphinx;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class Component {
	public static Minecraft mc = Minecraft.getMinecraft();
	public static final ResourceLocation ICONS = new ResourceLocation("zone:textures/gui/sphinx/icons.png");
	public int x, y;
	public int width, height;
	public IParent parent;

	public Component(IParent parent, int x, int y, int width, int height) {
		this.parent = parent;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public boolean onClickScreen(int x, int y, boolean isLeft) {
		if (isLeft) {
			return onLeftClick(x, y);
		} else {
			return onRightClick(x, y);
		}
	}

	public boolean onLeftClick(int x, int y) {
		return false;
	}

	public boolean onRightClick(int x, int y) {
		return false;
	}

	public boolean onDrag(int mouseX, int mouseY, int moveX, int moveY) {
		return false;
	}

	public boolean onMouseScroll(int mouseX, int mouseY, boolean isUp) {
		return false;
	}

	public void doRender(int mouseX, int mouseY) {

	}

	public boolean onKeyTyped(char typedChar, int keyCode) {
		return false;
	}
}
