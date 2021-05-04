package com.pinball3d.zone.sphinx.elite;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;

public class DropDownList {
	private float x, y;
	private DropDownList parentList, childList;
	private EliteMainwindow parent;
	private List<ListBar> list = new ArrayList<ListBar>();
	private int chosenIndex = -1;
	private boolean isKeyBoard;

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
		for (int i = 0; i < list.size(); i++) {
			ListBar bar = list.get(i);
			bar.doRender(x + 0.75F, y + yOffset, width - 1.5F, i == chosenIndex);
			yOffset += bar.getHeight();
		}
		if (childList != null) {
			childList.doRender(mouseX, mouseY);
		}
	}

	public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if (chosenIndex >= 0) {
			list.get(chosenIndex).onClick();
			return true;
		}
		return false;
	}

	public void onMouseMoved(int mouseX, int mouseY, int moveX, int moveY) {
		computeChosenIndex(mouseX, mouseY);
	}

	public void keyTyped(char typedChar, int keyCode) {
		switch (keyCode) {
		case Keyboard.KEY_UP:
			chosenIndex = chosenIndex <= 0 ? list.size() - 1 : chosenIndex - 1;
			break;
		case Keyboard.KEY_DOWN:
			chosenIndex = chosenIndex >= list.size() - 1 ? 0 : chosenIndex + 1;
			break;
		case Keyboard.KEY_LEFT:
			parent.getMenuBar().move(true);
			break;
		case Keyboard.KEY_RIGHT:
			parent.getMenuBar().move(false);
			break;
		case Keyboard.KEY_RETURN:
			if (chosenIndex >= 0) {
				list.get(chosenIndex).onClick();
			}
		}
	}

	private boolean computeChosenIndex(int mouseX, int mouseY) {
		int old = chosenIndex;
		float w = getWidth();
		float h = getHeight();
		if (mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h) {
			if (mouseX >= x + 0.75F && mouseX <= x + w - 1.5F && mouseY >= y + 0.75F && mouseY <= y + h - 1.5F) {
				float yOffset = y + 0.75F;
				for (int i = 0; i < list.size(); i++) {
					ListBar bar = list.get(i);
					float bh = bar.getHeight();
					if (mouseY >= yOffset && mouseY <= yOffset + bh) {
						chosenIndex = i;
						if (bar instanceof FolderBar) {
							childList = new DropDownList(parent, this, x + w - 1.5F, yOffset - 0.75F);
							childList.addBar(new ButtonBar(childList, "ABC", "DEF"));
						} else {
							childList = null;
						}
						return old != chosenIndex;
					}
					yOffset += bh;
				}
			}
		}
		if (!isKeyBoard) {
			chosenIndex = -1;
		}
		return old != chosenIndex;
	}

	public void setChosenIndex(int chosenIndex) {
		this.chosenIndex = chosenIndex;
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

		public void doRender(float x, float y, float width, boolean isHovered) {

		}

		public void onClick() {

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
			super(parent, FontHelper.getStringWidth(textL) + FontHelper.getStringWidth(textR) + 18.75F, 5.25F);
			this.textL = textL;
			this.textR = textR;
		}

		@Override
		public void doRender(float x, float y, float width, boolean isHovered) {
			super.doRender(x, y, width, isHovered);
			if (isHovered) {
				EliteRenderHelper.drawRect(x, y, width, height, 0xFF0078D7);
			}
			if (textL != null) {
				FontHelper.renderText(x + 4.75F, y + 1.25F, textL, isHovered ? 0xFFFFFFFF : 0xFF000000);
				FontHelper.renderText(x + width - FontHelper.getStringWidth(textR) - 4, y + 1.25F, textR,
						isHovered ? 0xFFFFFFFF : 0xFF000000);
			}
		}

		@Override
		public void onClick() {
			super.onClick();
			System.out.println(textL);
			parent.parent.quitMenuBar();
		}
	}

	public static class DividerBar extends ListBar {
		public DividerBar(DropDownList parent) {
			super(parent, 2, 2.25F);
		}

		@Override
		public void doRender(float x, float y, float width, boolean isHovered) {
			super.doRender(x, y, width, isHovered);
			EliteRenderHelper.drawRect(x + 0.25F, y + 0.75F, width - 0.5F, 0.25F, 0xFFA0A0A0);
			EliteRenderHelper.drawRect(x + 0.25F, y + 1, width - 0.5F, 0.25F, 0xFFFFFFFF);
		}
	}

	public static class FolderBar extends ListBar {
		private String text;

		public FolderBar(DropDownList parent, String text) {
			super(parent, FontHelper.getStringWidth(text) + 12.5F, 5.25F);
			this.text = text;
		}

		@Override
		public void doRender(float x, float y, float width, boolean isHovered) {
			super.doRender(x, y, width, isHovered);
			if (isHovered) {
				EliteRenderHelper.drawRect(x, y, width, height, 0xFF0078D7);
				EliteRenderHelper.drawTexture(EliteMainwindow.ELITE, x + width - 2.5F, y + 1.25F, 4, 40, 4, 7, 0.25F);
			} else {
				EliteRenderHelper.drawTexture(EliteMainwindow.ELITE, x + width - 2.5F, y + 1.25F, 0, 40, 4, 7, 0.25F);
			}
			if (text != null) {
				FontHelper.renderText(x + 4.75F, y + 1.25F, text, isHovered ? 0xFFFFFFFF : 0xFF000000);
			}
		}
	}
}
