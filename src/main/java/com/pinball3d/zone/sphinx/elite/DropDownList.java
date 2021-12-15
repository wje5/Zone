package com.pinball3d.zone.sphinx.elite;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;

public class DropDownList implements IDropDownList {
	private int x, y;
	private DropDownList parentList, childList;
	private EliteMainwindow parent;
	private List<ListBar> list = new ArrayList<ListBar>();
	private int chosenIndex = -1;
	private boolean isKeyBoard;

	public DropDownList(EliteMainwindow parent, DropDownList parentList, int x, int y) {
		this.parent = parent;
		this.parentList = parentList;
		this.x = x;
		this.y = y;
	}

	@Override
	public void doRender(int mouseX, int mouseY) {
		int width = getWidth();
		int height = getHeight();
		EliteRenderHelper.drawBorder(x, y, width, height, 1, Color.TEXT_DARK);
		EliteRenderHelper.drawRect(x + 1, y + 1, width - 2, height - 2, Color.TEXT_LIGHT);
		int yOffset = 3;
		for (int i = 0; i < list.size(); i++) {
			ListBar bar = list.get(i);
			bar.doRender(x + 3, y + yOffset, width - 6, i == chosenIndex);
			yOffset += bar.getHeight();
		}
		if (childList != null) {
			childList.doRender(mouseX, mouseY);
		}
	}

	@Override
	public Drag mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if (!isMouseInList(mouseX, mouseY)) {
			return null;
		}
		return new Drag(mouseButton);
	}

	@Override
	public boolean isMouseInList(int mouseX, int mouseY) {
		computeChosenIndex(mouseX, mouseY);
		if (childList != null) {
			if (childList.isMouseInList(mouseX, mouseY)) {
				return true;
			}
			return checkChosenIndex(mouseX, mouseY) >= 0;
		}
		if (mouseX >= x && mouseX <= x + getWidth() && mouseY >= y && mouseY <= y + getHeight()) {
			return true;
		}
		return false;
	}

	@Override
	public boolean mouseReleased(int mouseX, int mouseY, int mouseButton) {
		if (mouseButton != 0) {
			return true;
		}
		computeChosenIndex(mouseX, mouseY);
		if (childList != null) {
			if (childList.mouseReleased(mouseX, mouseY, mouseButton)) {
				return true;
			}
			return checkChosenIndex(mouseX, mouseY) >= 0;
		}
		if (chosenIndex >= 0) {
			list.get(chosenIndex).onClick();
			return true;
		}
		return false;
	}

	@Override
	public void onMouseMoved(int mouseX, int mouseY, int moveX, int moveY) {
		computeChosenIndex(mouseX, mouseY);
		if (childList != null) {
			childList.onMouseMoved(mouseX, mouseY, moveX, moveY);
		}
	}

	@Override
	public void keyTyped(char typedChar, int keyCode) {
		if (childList != null) {
			childList.keyTyped(typedChar, keyCode);
			return;
		}
		switch (keyCode) {
		case Keyboard.KEY_UP:
			int old = chosenIndex;
			do {
				chosenIndex = chosenIndex <= 0 ? list.size() - 1 : chosenIndex - 1;
			} while (list.get(chosenIndex) instanceof DividerBar && chosenIndex != old);
			isKeyBoard = true;
			break;
		case Keyboard.KEY_DOWN:
			old = chosenIndex;
			do {
				chosenIndex = chosenIndex >= list.size() - 1 ? 0 : chosenIndex + 1;
			} while (list.get(chosenIndex) instanceof DividerBar && chosenIndex != old);
			isKeyBoard = true;
			break;
		case Keyboard.KEY_LEFT:
			if (parentList != null) {
				parentList.childList = null;
				break;
			}
			parent.getMenuBar().move(true);
			break;
		case Keyboard.KEY_RIGHT:
			if (chosenIndex >= 0) {
				ListBar bar = list.get(chosenIndex);
				if (bar instanceof FolderBar) {
					int yOffset = y + 3;
					for (int i = 0; i < chosenIndex; i++) {
						ListBar b = list.get(i);
						yOffset += b.height;
					}
					childList = ((FolderBar) bar).openList(parent, x + getWidth() - 6, yOffset - 3);
					childList.setChosenIndex(0);
					childList.setKeyBoard(true);
					break;
				}
			}
			parent.getMenuBar().move(false);
			break;
		case Keyboard.KEY_RETURN:
			if (chosenIndex >= 0) {
				ListBar bar = list.get(chosenIndex);
				if (bar instanceof FolderBar) {
					int yOffset = y + 3;
					for (int i = 0; i < chosenIndex; i++) {
						ListBar b = list.get(i);
						yOffset += b.height;
					}
					childList = ((FolderBar) bar).openList(parent, x + getWidth() - 6, yOffset - 3);
					childList.setChosenIndex(0);
					childList.setKeyBoard(true);
					break;
				}
				bar.onClick();
				isKeyBoard = true;
			}
			break;
		}

	}

	private boolean computeChosenIndex(int mouseX, int mouseY) {
		if (childList != null && childList.isMouseInList(mouseX, mouseY)) {
			return false;
		}
		int old = chosenIndex;
		int w = getWidth();
		int h = getHeight();
		if (mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h) {
			if (mouseX >= x + 3 && mouseX <= x + w - 3 && mouseY >= y + 3 && mouseY <= y + h - 3) {
				int yOffset = y + 3;
				for (int i = 0; i < list.size(); i++) {
					ListBar bar = list.get(i);
					int bh = bar.getHeight();
					if (mouseY >= yOffset && mouseY <= yOffset + bh) {
						chosenIndex = i;
						if (bar instanceof FolderBar) {
							childList = ((FolderBar) bar).openList(parent, x + w - 6, yOffset - 3);
						} else {
							childList = null;
						}
						isKeyBoard = false;
						return old != chosenIndex;
					}
					yOffset += bh;
				}
			}
		}
		if (!isKeyBoard && childList == null) {
			chosenIndex = -1;
		}
		return old != chosenIndex;
	}

	private int checkChosenIndex(int mouseX, int mouseY) {
		int w = getWidth();
		int h = getHeight();
		if (mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h) {
			if (mouseX >= x + 3 && mouseX <= x + w - 3 && mouseY >= y + 3 && mouseY <= y + h - 3) {
				int yOffset = y + 3;
				for (int i = 0; i < list.size(); i++) {
					ListBar bar = list.get(i);
					int bh = bar.getHeight();
					if (mouseY >= yOffset && mouseY <= yOffset + bh) {
						return i;
					}
					yOffset += bh;
				}
			}
		}
		return -1;
	}

	public void setChosenIndex(int chosenIndex) {
		this.chosenIndex = chosenIndex;
	}

	public int getWidth() {
		return list.stream().mapToInt(ListBar::getWidth).max().getAsInt() + 6;
	}

	public int getHeight() {
		return list.stream().mapToInt(ListBar::getHeight).sum() + 6;
	}

	public void setKeyBoard(boolean isKeyBoard) {
		this.isKeyBoard = isKeyBoard;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public DropDownList setX(int x) {
		this.x = x;
		return this;
	}

	public DropDownList setY(int y) {
		this.y = y;
		return this;
	}

	public DropDownList addBar(ListBar bar) {
		list.add(bar);
		bar.setParentList(this);
		return this;
	}

	@Override
	public boolean onQuit() {
		if (childList != null) {
			if (childList.onQuit()) {
				childList = null;
			}
			return false;
		}
		if (parentList == null) {
			parent.getMenuBar().onQuitList();
		}
		return true;
	}

	public static class ListBar {
		protected int width, height;
		protected DropDownList parentList;

		public ListBar(int width, int height) {
			this.width = width;
			this.height = height;
		}

		public void doRender(int x, int y, int width, boolean isHovered) {

		}

		public void onClick() {

		}

		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}

		public ListBar setParentList(DropDownList parentList) {
			this.parentList = parentList;
			return this;
		}

	}

	public static class ButtonBar extends ListBar {
		private FormattedString textL, textR;

		public ButtonBar(FormattedString textL, FormattedString textR) {
			super(FontHandler.getStringWidth(textL) + FontHandler.getStringWidth(textR) + 75, 21);
			this.textL = textL;
			this.textR = textR;
		}

		@Override
		public void doRender(int x, int y, int width, boolean isHovered) {
			super.doRender(x, y, width, isHovered);
			if (isHovered) {
				EliteRenderHelper.drawRect(x, y, width, height, Color.FF0078D7);
			}
			if (textL != null) {
				FontHandler.renderText(x + 19, y + 5, textL, isHovered ? Color.WHITE : Color.BLACK);
				FontHandler.renderText(x + width - FontHandler.getStringWidth(textR) - 16, y + 5, textR,
						isHovered ? Color.WHITE : Color.BLACK);
			}
		}

		@Override
		public void onClick() {
			super.onClick();
			EliteMainwindow.getWindow().quitMenuBar();
		}
	}

	public static class DividerBar extends ListBar {
		public DividerBar() {
			super(8, 9);
		}

		@Override
		public void doRender(int x, int y, int width, boolean isHovered) {
			super.doRender(x, y, width, isHovered);
			EliteRenderHelper.drawRect(x + 1, y + 3, width - 2, 1, Color.TEXT_DARK);
			EliteRenderHelper.drawRect(x + 1, y + 4, width - 2, 1, Color.WHITE);
		}
	}

	public static class FolderBar extends ListBar {
		private FormattedString text;
		private List<ListBar> list = new ArrayList<ListBar>();

		public FolderBar(FormattedString text) {
			super(FontHandler.getStringWidth(text) + 50, 21);
			this.text = text;
		}

		@Override
		public void doRender(int x, int y, int width, boolean isHovered) {
			super.doRender(x, y, width, isHovered);
			if (isHovered) {
				EliteRenderHelper.drawRect(x, y, width, height, Color.FF0078D7);
				EliteRenderHelper.drawTexture(EliteMainwindow.ELITE, x + width - 10, y + 5, 4, 40, 4, 7);
			} else {
				EliteRenderHelper.drawTexture(EliteMainwindow.ELITE, x + width - 10, y + 5, 0, 40, 4, 7);
			}
			if (text != null) {
				FontHandler.renderText(x + 19, y + 5, text, isHovered ? Color.WHITE : Color.BLACK);
			}
		}

		public DropDownList openList(EliteMainwindow parent, int x, int y) {
			DropDownList l = new DropDownList(parent, parentList, x, y);
			list.forEach(e -> l.addBar(e));
			if (x + l.getWidth() > parent.getWidth()) {
				int lX = parentList.x - l.getWidth() + 6;
				l.setX(lX < 0 ? 0 : lX);
			}
			if (l.getY() + l.getHeight() > parent.getHeight()) {
				int lY = parent.getHeight() - l.getHeight();
				l.setY(lY < 0 ? 0 : lY);
			}
			return l;
		}

		public FolderBar addBar(ListBar bar) {
			list.add(bar);
			return this;
		}
	}
}
