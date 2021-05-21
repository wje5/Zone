package com.pinball3d.zone.sphinx.elite;

import java.nio.IntBuffer;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;

public class MouseHandler {
	private static final int W = 0xFFFFFFFF, U = 0xFF0000FF, B = 0xFF000000, R = 0xFFFF0000, G = 0xFF00FF00,
			A = 0x00FF0000;
	private static MouseType type;

	public static void changeMouse(MouseType type) {
//		try {
//			Mouse.setNativeCursor(type == null ? null : type.createCursor());
//		} catch (LWJGLException e) {
//			e.printStackTrace();
//		}

		try {
			if (type != null) {
				Mouse.setNativeCursor(new Cursor(1, 1, 0, 0, 1, IntBuffer.wrap(new int[] { 0x00FFFFFF }), null));
			} else {
				Mouse.setNativeCursor(null);
			}
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		MouseHandler.type = type;
	}

	public static void renderMouse() {
		if (type != null) {
			float x = getX();
			float y = getY();
		}
	}

	public static int getX() {
		return Mouse.getX();
	}

	public static int getY() {
		return Minecraft.getMinecraft().displayHeight - Mouse.getY();
	}

	public static enum MouseType {
		RESIZE_S(24, 12, 11, 8, new int[] { A, A, A, A, B, B, A, A, A, A, A, A, A, A, A, A, A, B, B, A, A, A, A, A, A,
				A, A, B, W, B, A, A, A, A, A, A, A, A, A, A, A, B, W, B, A, A, A, A, A, A, B, W, W, B, A, A, A, A, A, A,
				A, A, A, A, A, B, W, W, B, A, A, A, A, B, W, W, W, B, B, B, B, B, B, B, B, B, B, B, B, B, W, W, W, B, A,
				A, B, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, B, A, A, B, W, W, W, B, B, B, B, B,
				B, B, B, B, B, B, B, B, W, W, W, B, A, A, A, A, B, W, W, B, A, A, A, A, A, A, A, A, A, A, A, B, W, W, B,
				A, A, A, A, A, A, B, W, B, A, A, A, A, A, A, A, A, A, A, A, B, W, B, A, A, A, A, A, A, A, A, B, B, A, A,
				A, A, A, A, A, A, A, A, A, B, B, A, A, A, A, A, A, A, A, A, A, A, A, A, A, A, A, A, A, A, A, A, A, A, A,
				A, A, A, A, A, A, A, A, A, A, A, A, A, A, A, A, A, A, A, A, A, A, A, A, A, A, A, A, A, A, A, A, A, A, A,
				A, A, A, A, A, A, A, A, A, A, A, A, A, A, A, A, A, A }, null);

		private final int width, height, xHotspot, yHotspot;
		private IntBuffer image, delays;
		private static Cursor c;

		private MouseType(int width, int height, int xHotspot, int yHotspot, int[] image, int[] delays) {
			this.width = width;
			this.height = height;
			this.xHotspot = xHotspot;
			this.yHotspot = yHotspot;
			this.image = IntBuffer.wrap(image);
			this.delays = delays == null ? null : IntBuffer.wrap(delays);
		}

		public Cursor createCursor() throws LWJGLException {
			if (c == null) {
				c = new Cursor(width, height, xHotspot, yHotspot, delays == null ? 1 : delays.limit(), image, delays);
			}
			return c;
		}
	}
}
