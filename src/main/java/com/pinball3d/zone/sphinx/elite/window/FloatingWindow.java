package com.pinball3d.zone.sphinx.elite.window;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.pinball3d.zone.math.Pos2i;
import com.pinball3d.zone.sphinx.elite.Color;
import com.pinball3d.zone.sphinx.elite.Drag;
import com.pinball3d.zone.sphinx.elite.EliteMainwindow;
import com.pinball3d.zone.sphinx.elite.EliteRenderHelper;
import com.pinball3d.zone.sphinx.elite.FontHandler;
import com.pinball3d.zone.sphinx.elite.FormattedString;
import com.pinball3d.zone.sphinx.elite.Subpanel;
import com.pinball3d.zone.sphinx.elite.layout.BoxLayout;

public class FloatingWindow {
	private EliteMainwindow parent;
	private FormattedString title;
	private int x, y, width, height, drag;
	private Subpanel root;

	public FloatingWindow(EliteMainwindow parent, int width, int height, FormattedString title) {
		this.parent = parent;
		this.width = width;
		this.height = height;
		this.title = title;
		root = new Subpanel(parent, null, 0, 0, new BoxLayout(true)) {
			@Override
			public Pos2i getPos() {
				return new Pos2i(getPanelX(), getPanelX());
			}

			@Override
			public int getWidth() {
				return FloatingWindow.this.getPanelWidth();
			}

			@Override
			public int getHeight() {
				return FloatingWindow.this.getPanelHeight();
			}

			@Override
			public int getRenderWidth() {
				return FloatingWindow.this.getWidth();
			}

			@Override
			public int getMinWidth() {
				return FloatingWindow.this.getWidth();
			}
		};
	}

	public void doRenderPre(int mouseX, int mouseY, float partialTicks) {
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		GL11.glScissor(root.getPos().x, parent.getHeight() - root.getPos().y + root.getHeight(), root.getWidth(),
				root.getHeight());
		GL11.glPushMatrix();
		GL11.glTranslatef(root.getPos().x - getX(), root.getPos().y - getY(), 0);
		root.doRenderPre(mouseX, mouseY, partialTicks);
		GL11.glPopMatrix();
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
	}

	public void doRender(int mouseX, int mouseY, float partialTicks) {
		boolean top = this == parent.getFloatingWindows().peek();
		EliteRenderHelper.drawBorder(x, y, width, height, 1, top ? Color.WINDOW_BORDER : Color.WINDOW_BORDER_DARK);
		EliteRenderHelper.drawRect(x + 1, y + 1, width - 2, 25, Color.FF0078D7);
		EliteRenderHelper.drawRect(x + 1, y + 26, width - 2, height - 27, Color.WINDOW_BG);
		EliteRenderHelper.drawTexture(EliteMainwindow.ELITE, x + 5, y + 6, 116, 57, 16, 14);
		FontHandler.renderText(x + 26, y + 5, title, top ? Color.WHITE : Color.FF66AEE7);
		if (!top) {
			EliteRenderHelper.drawTexture(EliteMainwindow.ELITE, x + 605, y + 8, 132, 67, 10, 10);
		} else if (mouseX >= x + 592 && mouseX <= x + width && mouseY >= y && mouseY <= y + 26
				&& (drag == 0 || drag == 1)) {
			EliteRenderHelper.drawTexture(EliteMainwindow.ELITE, x + 594, y + 1, drag == 1 ? 216 : 185, 57, 31, 24);
		} else {
			EliteRenderHelper.drawTexture(EliteMainwindow.ELITE, x + 605, y + 8, 132, 57, 10, 10);
		}
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		GL11.glScissor(root.getPos().x, parent.getHeight() - root.getPos().y + root.getHeight(), root.getWidth(),
				root.getHeight());
		GL11.glPushMatrix();
		GL11.glTranslatef(root.getPos().x - getX(), root.getPos().y - getY(), 0);
		root.doRender(mouseX, mouseY, partialTicks);
		GL11.glPopMatrix();
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
	}

	public void doRenderPost(int mouseX, int mouseY, float partialTicks) {
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		GL11.glScissor(root.getPos().x, parent.getHeight() - root.getPos().y + root.getHeight(), root.getWidth(),
				root.getHeight());
		GL11.glPushMatrix();
		GL11.glTranslatef(root.getPos().x - getX(), root.getPos().y - getY(), 0);
		root.doRenderPost(mouseX, mouseY, partialTicks);
		GL11.glPopMatrix();
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
	}

	public Drag mouseClicked(int mouseX, int mouseY, int mouseButton) {
		System.out.println(mouseX + "|" + mouseY + "|" + mouseButton);
		if (mouseX >= getPanelX() && mouseX <= getPanelX() + getPanelWidth() && mouseY >= getPanelY()
				&& mouseY <= getPanelY() + getPanelHeight()) {
			return root.mouseClicked(mouseX, mouseY, mouseButton);
		}
		if (mouseX >= x + 592 && mouseX <= x + width && mouseY >= y && mouseY <= y + 26) {
			drag = 1;
		}
		return new Drag(mouseButton, (x, y, moveX, moveY) -> {
		}, (x, y, cancel) -> {
			System.out.println(drag);
			if (!cancel) {
				if (drag == 1 && x >= this.x + 592 && x <= this.x + width && y >= this.y && y <= this.y + 26) {
					if (canQuit()) {
						parent.tryCloseFloatingWindow();
					}
				}
			}
			drag = 0;
		});
	}

	public void mouseScrolled(int mouseX, int mouseY, int distance) {

	}

	public void mouseMoved(int mouseX, int mouseY, int moveX, int moveY) {

	}

	public void keyTyped(char typedChar, int keyCode) {
		if (keyCode == Keyboard.KEY_ESCAPE) {
			if (canQuit()) {
				parent.tryCloseFloatingWindow();
			}
		}
	}

	public boolean canQuit() {
		return true;
	}

	public void close() {

	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void setPos(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getPanelX() {
		return x + 1;
	}

	public int getPanelY() {
		return y + 30;
	}

	public int getPanelWidth() {
		return width - 2;
	}

	public int getPanelHeight() {
		return height - 31;
	}

	public Subpanel getRoot() {
		return root;
	}
}
