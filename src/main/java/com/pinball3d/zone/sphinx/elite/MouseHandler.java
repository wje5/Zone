package com.pinball3d.zone.sphinx.elite;

import java.nio.IntBuffer;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;

public class MouseHandler {
	private static MouseType type;

	public static void changeMouse(MouseType type) {
		if (type != MouseHandler.type) {
			try {
				if (type != null) {
					Mouse.setNativeCursor(new Cursor(1, 1, 0, 0, 1,
							IntBuffer.wrap(new int[] { 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF,
									0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF,
									0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF }),
							null));
				} else {
					Mouse.setNativeCursor(null);
				}
			} catch (LWJGLException e) {
				e.printStackTrace();
			}
			MouseHandler.type = type;
		}
	}

	public static void renderMouse() {
		if (type != null) {
			int x = getX();
			int y = getY();
			EliteRenderHelper.drawTexture(EliteMainwindow.ELITE, x - type.xHotspot, y - type.yHotspot, type.u, type.v,
					type.width, type.height, 1.0F);
		}
	}

	public static int getX() {
		return Mouse.getX();
	}

	public static int getY() {
		return Minecraft.getMinecraft().displayHeight - Mouse.getY();
	}

	public static boolean isMouseInsideWindow() {
		return Mouse.isInsideWindow();
	}

	public static enum MouseType {
		RESIZE_W(23, 9, 11, 4, 9, 51), RESIZE_S(9, 23, 4, 11, 0, 51), MOVE(23, 23, 11, 11, 0, 77),
		TEXT(7, 16, 3, 7, 23, 77), TEXT_LIGHT(7, 16, 3, 7, 30, 77);

		public final int width, height, xHotspot, yHotspot, u, v;

		private MouseType(int width, int height, int xHotspot, int yHotspot, int u, int v) {
			this.width = width;
			this.height = height;
			this.xHotspot = xHotspot;
			this.yHotspot = yHotspot;
			this.u = u;
			this.v = v;
		}
	}
}
