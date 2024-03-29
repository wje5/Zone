package com.pinball3d.zone.sphinx.elite;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;

import com.pinball3d.zone.sphinx.elite.DropDownList.ListBar;;

public class MenuBar {
	private EliteMainwindow parent;
	private List<Menu> list = new ArrayList<Menu>();
	private int chosenIndex = -1;
	private boolean isClicked, released;

	public MenuBar(EliteMainwindow parent) {
		this.parent = parent;
	}

	public MenuBar addMenu(Menu menu) {
		list.add(menu);
		return this;
	}

	public void doRender(int mouseX, int mouseY) {
		int x = 4;
		EliteRenderHelper.drawRect(0, 0, parent.getWidth(), 28, Color.COMP_BG_LIGHT);
		for (int i = 0; i < list.size(); i++) {
			Menu m = list.get(i);
			x += m.doRender(x, chosenIndex == i ? isClicked ? 2 : parent.doMouseHover() ? 1 : 0 : 0);
		}
	}

	public void mouseMoved(int mouseX, int mouseY, int moveX, int moveY) {
		if (computeChosenIndex(mouseX, mouseY) && isClicked) {
			openDropDownList(false);
		}
	}

	private boolean computeChosenIndex(int mouseX, int mouseY) {
		if (parent.getDropDownList() != null && parent.getDropDownList().isMouseInList(mouseX, mouseY)) {
			return false;
		}
		int old = chosenIndex;
		if (mouseY <= 28) {
			int x = 4;
			for (int i = 0; i < list.size(); i++) {
				Menu m = list.get(i);
				int w = m.getWidth();
				if (mouseX >= x && mouseX <= x + w) {
					chosenIndex = i;
					return old != chosenIndex;
				}
				x += m.getWidth();
			}
		}
		if (!isClicked && !parent.isAlt()) {
			chosenIndex = -1;
		}
		return old != chosenIndex;
	}

	private int checkChosenIndex(int mouseX, int mouseY) {
		if (mouseY <= 28) {
			int x = 4;
			for (int i = 0; i < list.size(); i++) {
				Menu m = list.get(i);
				int w = m.getWidth();
				if (mouseX >= x && mouseX <= x + w) {
					return i;
				}
				x += m.getWidth();
			}
		}
		return -1;
	}

	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if (mouseButton != 0) {
			return;
		}
		parent.setAlt(false);
		computeChosenIndex(mouseX, mouseY);
		if (!isClicked) {
			if (chosenIndex >= 0 && chosenIndex < list.size()) {
				isClicked = true;
				parent.setAlt(true);
				openDropDownList(false);
			}
		} else if (!(chosenIndex >= 0 && chosenIndex < list.size())) {
			isClicked = false;
			computeChosenIndex(mouseX, mouseY);
			parent.setDropDownList(null);
			parent.setAlt(false);
		}
	}

	public boolean mouseReleased(int mouseX, int mouseY, int mouseButton) {
		int index = checkChosenIndex(mouseX, mouseY);
		boolean r = checkChosenIndex(mouseX, mouseY) >= 0 && !(released && isClicked && chosenIndex == index);
		released = r;
		return r;
	}

	public void onPressAlt() {
		if (chosenIndex < 0) {
			chosenIndex = 0;
			parent.setAlt(true);
		} else {
			chosenIndex = -1;
			parent.setAlt(false);
		}
	}

	private void openDropDownList(boolean flag) {
		int x = 4;
		for (int i = 0; i < chosenIndex; i++) {
			Menu m = list.get(i);
			x += m.getWidth();
		}
		DropDownList l = list.get(chosenIndex).openList(x, 23);
		if (flag) {
			l.setChosenIndex(0);
			l.setKeyBoard(true);
		}
		parent.setDropDownList(l);
	}

	public void keyTyped(char c, int keyCode) {
		if (parent.isAlt()) {
			switch (keyCode) {
			case Keyboard.KEY_RETURN:
				isClicked = true;
				openDropDownList(true);
				return;
			case Keyboard.KEY_LEFT:
				move(true);
				return;
			case Keyboard.KEY_RIGHT:
				move(false);
				return;
			}
			if (c >= 'a' && c <= 'z') {
				for (int i = 0; i < list.size(); i++) {
					char s = list.get(i).getShortCut();
					if (c == s) {
						chosenIndex = i;
						isClicked = true;
						openDropDownList(true);
						return;
					}
				}
				parent.setAlt(false);
				chosenIndex = -1;
			}
		}
	}

	public boolean onQuit() {
		if (parent.isAlt()) {
			parent.setAlt(false);
			chosenIndex = -1;
			isClicked = false;
			return false;
		}
		return true;
	}

	public void move(boolean isLeft) {
		int old = chosenIndex;
		if (isLeft) {
			chosenIndex = chosenIndex <= 0 ? list.size() - 1 : chosenIndex - 1;
		} else {
			chosenIndex = chosenIndex >= list.size() - 1 ? 0 : chosenIndex + 1;
		}
		if (old != chosenIndex && isClicked) {
			openDropDownList(true);
		}
	}

	public EliteMainwindow getParent() {
		return parent;
	}

	public void setChosenIndex(int chosenIndex) {
		this.chosenIndex = chosenIndex < list.size() ? chosenIndex : list.size();
	}

	public void onQuitList() {
		isClicked = false;
	}

	public boolean isClicked() {
		return isClicked;
	}

	public void onListClosed() {
		parent.setAlt(false);
		isClicked = false;
		chosenIndex = -1;
	}

	public static class Menu {
		private EliteMainwindow parent;
		private FormattedString name;
		private char shortCut;
		private List<ListBar> list = new ArrayList<ListBar>();

		public Menu(EliteMainwindow parent, FormattedString name, char shortCut) {
			this.parent = parent;
			this.name = name;
			this.shortCut = shortCut;
		}

		public int doRender(int x, int flag) {
			int w = getWidth();
			if (flag == 1) {
				EliteRenderHelper.drawTexture(EliteMainwindow.ELITE, x, 4, 0, 20, w - 3, 20);
				EliteRenderHelper.drawTexture(EliteMainwindow.ELITE, x + w - 3, 4, 253, 20, 3, 20);
			} else if (flag == 2) {
				EliteRenderHelper.drawTexture(EliteMainwindow.ELITE, x, 4, 0, 0, w - 3, 20);
				EliteRenderHelper.drawTexture(EliteMainwindow.ELITE, x + w - 3, 4, 253, 0, 3, 20);
			}
			FontHandler.renderText(x + 8, 6, name, Color.FFC7C7C7, parent.isAlt());
			return w;
		}

		public int getWidth() {
			return FontHandler.getStringWidth(name) + 13;
		}

		public FormattedString getName() {
			return name;
		}

		public char getShortCut() {
			return shortCut;
		}

		public DropDownList openList(int x, int y) {
			DropDownList l = new DropDownList(parent, null, x, y);
			list.forEach(e -> l.addBar(e.setParentList(l)));
			if (l.getX() + l.getWidth() > parent.getWidth()) {
				int lX = parent.getWidth() - l.getWidth();
				l.setX(lX < 0 ? 0 : lX);
			}
			if (l.getY() + l.getHeight() > parent.getHeight()) {
				int lY = parent.getHeight() - l.getHeight();
				l.setY(lY < 0 ? 0 : lY);
			}
			return l;
		}

		public Menu addBar(ListBar bar) {
			list.add(bar);
			return this;
		}
	}
}
