package com.pinball3d.zone.sphinx.elite;

import org.lwjgl.opengl.GL11;

import com.pinball3d.zone.math.Pos2i;
import com.pinball3d.zone.sphinx.elite.ui.core.Subpanel;
import com.pinball3d.zone.sphinx.elite.ui.core.layout.BoxLayout;

public class FloatingWindow {
	private EliteMainwindow parent;
	private FormattedString title;
	private int x, y, width, height;
	private Subpanel root;

	public FloatingWindow(EliteMainwindow parent, FormattedString title) {
		this.parent = parent;
		this.title = title;
		root = new Subpanel(parent, null, 0, 0, new BoxLayout(true)) {
			@Override
			public Pos2i getPos() {
				return new Pos2i(getX(), getY());
			}

			@Override
			public int getWidth() {
				return FloatingWindow.this.getWidth();
			}

			@Override
			public int getHeight() {
				return FloatingWindow.this.getHeight();
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
		GL11.glScissor(root.getPos().x, parent.getHeight() - (root.getPos().y + root.getHeight()), root.getWidth(),
				root.getHeight());
		GL11.glPushMatrix();
		GL11.glTranslatef(root.getPos().x - getX(), root.getPos().y - getY(), 0);
		root.doRenderPre(mouseX, mouseY, partialTicks);
		GL11.glPopMatrix();
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
	}

	public void doRender(int mouseX, int mouseY, float partialTicks) {
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		GL11.glScissor(root.getPos().x, parent.getHeight() - (root.getPos().y + root.getHeight()), root.getWidth(),
				root.getHeight());
		GL11.glPushMatrix();
		GL11.glTranslatef(root.getPos().x - getX(), root.getPos().y - getY(), 0);
		root.doRender(mouseX, mouseY, partialTicks);
		GL11.glPopMatrix();
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
	}

	public void doRenderPost(int mouseX, int mouseY, float partialTicks) {
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		GL11.glScissor(root.getPos().x, parent.getHeight() - (root.getPos().y + root.getHeight()), root.getWidth(),
				root.getHeight());
		GL11.glPushMatrix();
		GL11.glTranslatef(root.getPos().x - getX(), root.getPos().y - getY(), 0);
		root.doRenderPost(mouseX, mouseY, partialTicks);
		GL11.glPopMatrix();
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
	}

	public Drag onMouseClicked(int mouseX, int mouseY, int mouseButton) {
		return new Drag(mouseButton);
	}

	public void onMouseScrolled(int mouseX, int mouseY, int distance) {

	}

	public void onMouseMoved(int mouseX, int mouseY, int moveX, int moveY) {

	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
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
}
