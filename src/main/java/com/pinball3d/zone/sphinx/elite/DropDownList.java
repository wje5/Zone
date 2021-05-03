package com.pinball3d.zone.sphinx.elite;

import java.util.ArrayList;
import java.util.List;

public class DropDownList {
	private float x, y;
	private DropDownList parentList;
	private EliteMainwindow parent;
	private List<ListBar> list = new ArrayList<ListBar>();

	public DropDownList(EliteMainwindow parent, DropDownList parentList, float x, float y) {
		this.parent = parent;
		this.parentList = parentList;
		this.x = x;
		this.y = y;
	}

	public void doRender(int mouseX, int mouseY) {
		float width = getWidth();
		float height = getHeight();
		EliteRenderHelper.drawBorder(x, y, width, height, 0.25F, 0xFFA0A0A0);
		EliteRenderHelper.drawRect(x + 0.25F, y + 0.25F, width - 0.5F, height - 0.5F, 0xFFF0F0F0);
		float yOffset = 0.75F;
		for (ListBar bar : list) {
			bar.doRender(x + 0.75F, y + yOffset, mouseX, mouseY);
			yOffset += bar.getHeight();
		}
	}

	public float getWidth() {
		return (float) list.stream().mapToDouble(ListBar::getWidth).max().getAsDouble() + 1.5F;
	}

	public float getHeight() {
		return (float) (list.stream().mapToDouble(ListBar::getHeight).sum() + 1.5F);
	}

	public void addBar(ListBar bar) {
		list.add(bar);
	}

	public boolean onQuit() {
		if (parent.getMenuBar().isClicked()) {
			parent.getMenuBar().onQuitList();
		}
		return true;
	}

	public static class ListBar {
		protected float width, height;
		protected DropDownList parent;

		public ListBar(DropDownList parent, float width, float height) {
			this.parent = parent;
			this.width = width;
			this.height = height;
		}

		public void doRender(float x, float y, int mouseX, int mouseY) {

		}

		public float getWidth() {
			return width;
		}

		public float getHeight() {
			return height;
		}

		public DropDownList getParent() {
			return parent;
		}
	}

	public static class ButtonBar extends ListBar {
		private String textL, textR;

		public ButtonBar(DropDownList parent, String textL, String textR) {
			super(parent, 65F, 5.25F);
			this.textL = textL;
			this.textR = textR;
		}

		@Override
		public void doRender(float x, float y, int mouseX, int mouseY) {
			super.doRender(x, y, mouseX, mouseY);
			if (textL != null) {
				FontHelper.renderText(x + 4.75F, y + 1.25F, textL, 0xFF000000);
			}
		}
	}
}
