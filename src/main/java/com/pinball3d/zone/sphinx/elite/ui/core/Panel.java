package com.pinball3d.zone.sphinx.elite.ui.core;

import org.lwjgl.opengl.GL11;

import com.pinball3d.zone.math.Pos2i;
import com.pinball3d.zone.sphinx.elite.Drag;
import com.pinball3d.zone.sphinx.elite.EliteMainwindow;
import com.pinball3d.zone.sphinx.elite.FormattedString;
import com.pinball3d.zone.sphinx.elite.MouseHandler.MouseType;
import com.pinball3d.zone.sphinx.elite.PanelGroup;
import com.pinball3d.zone.sphinx.elite.ui.core.layout.BoxLayout;

public class Panel {
	private EliteMainwindow parent;
	private PanelGroup parentGroup;
	private FormattedString name;
	private Subpanel root;

	public Panel(EliteMainwindow parent, PanelGroup parentGroup, FormattedString name) {
		this.parent = parent;
		this.parentGroup = parentGroup;
		this.name = name;
		root = new Subpanel(parent, null, 0, 0, new BoxLayout()) {
			@Override
			public Pos2i getPos() {
				return new Pos2i(getX(), getY());
			}

			@Override
			public int getWidth() {
				return Panel.this.getWidth();
			}

			@Override
			public int getHeight() {
				return Panel.this.getHeight();
			}

			@Override
			public int getRenderWidth() {
				return Panel.this.getWidth();
			}

			@Override
			public int getMinWidth() {
				return Panel.this.getWidth();
			}

			@Override
			public void doRenderPre(int mouseX, int mouseY, float partialTicks) {
				refresh();
				super.doRenderPre(mouseX, mouseY, partialTicks);
			}
		};
	}

	public void doRenderPre(int mouseX, int mouseY, float partialTicks) {
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		GL11.glScissor(getX(), parent.getHeight() - (getY() + root.getHeight()), root.getWidth(), root.getHeight());
		root.doRenderPre(mouseX, mouseY, partialTicks);
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
	}

	public void doRender(int mouseX, int mouseY, float partialTicks) {
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		GL11.glScissor(getX(), parent.getHeight() - (getY() + root.getHeight()), root.getWidth(), root.getHeight());
		root.doRender(mouseX, mouseY, partialTicks);
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
	}

	public void doRenderPost(int mouseX, int mouseY, float partialTicks) {
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		GL11.glScissor(getX(), parent.getHeight() - (getY() + root.getHeight()), root.getWidth(), root.getHeight());
		root.doRenderPost(mouseX, mouseY, partialTicks);
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
	}

	public boolean canQuit() {
		return true;
	}

	public void close() {

	}

	public boolean keyTyped(char typedChar, int keyCode) {
		return false;
	}

	public void onMouseMoved(int mouseX, int mouseY, int moveX, int moveY) {

	}

	public Drag mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if (isMouseInPanel(mouseX, mouseY)) {
			Drag drag = root.mouseClicked(mouseX, mouseY, mouseButton);
			if (drag != null) {
				return drag;
			}
			return new Drag(mouseButton);
		}
		return null;
	}

	public void onMouseScrolled(int mouseX, int mouseY, int distance) {
		if (isMouseInPanel(mouseX, mouseY)) {
			root.onMouseScrolled(mouseX, mouseY, distance);
		}
	}

	public MouseType getMouseType(int mouseX, int mouseY) {
		return root.getMouseType(mouseX, mouseY);
	}

	public boolean isMouseInPanel(int mouseX, int mouseY) {
		return mouseX >= 0 && mouseX <= getWidth() && mouseY >= 0 && mouseY <= getHeight();
	}

	public void setParentGroup(PanelGroup parentGroup) {
		this.parentGroup = parentGroup;
	}

	public PanelGroup getParentGroup() {
		return parentGroup;
	}

	public FormattedString getName() {
		return name;
	}

	public EliteMainwindow getParent() {
		return parent;
	}

	public Subpanel getRoot() {
		return root;
	}

	public final int getX() {
		return parentGroup.getPanelX();
	}

	public final int getY() {
		return parentGroup.getPanelY();
	}

	public final int getWidth() {
		return parentGroup.getPanelWidth();
	}

	public final int getHeight() {
		return parentGroup.getPanelHeight();
	}

	@Override
	public String toString() {
		return name.toString();
	}
}
