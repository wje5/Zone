package com.pinball3d.zone.gui;

import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class Component {
	public static Minecraft mc = Minecraft.getMinecraft();
	public static final ResourceLocation ICONS = new ResourceLocation("zone:textures/gui/sphinx/icons.png");
	public static final ResourceLocation TEXTURE_4 = new ResourceLocation("zone:textures/gui/sphinx/icons_4.png");
	private int x, y;
	public int width, height;
	public IHasComponents parent;
	protected BooleanSupplier enable;
	protected IntSupplier xSupplier, ySupplier;

	public Component(IHasComponents parent, int x, int y, int width, int height) {
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

	public Component setEnable(BooleanSupplier enable) {
		this.enable = enable;
		return this;
	}

	public Component setXSupplier(IntSupplier xSupplier) {
		this.xSupplier = xSupplier;
		return this;
	}

	public Component setYSupplier(IntSupplier ySupplier) {
		this.ySupplier = ySupplier;
		return this;
	}

	public int getX() {
		return xSupplier == null ? x : xSupplier.getAsInt();
	}

	public int getY() {
		return ySupplier == null ? y : ySupplier.getAsInt();
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

	public void onStopDrag() {

	}

	public boolean onMouseScroll(int mouseX, int mouseY, boolean isUp) {
		return false;
	}

	public void doRender(int mouseX, int mouseY) {

	}

	public boolean onKeyTyped(char typedChar, int keyCode) {
		return false;
	}

	public boolean getRenderLast() {
		return false;
	}
}
