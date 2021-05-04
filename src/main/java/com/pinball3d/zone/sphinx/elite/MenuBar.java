package com.pinball3d.zone.sphinx.elite;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;

import com.pinball3d.zone.sphinx.elite.DropDownList.ButtonBar;
import com.pinball3d.zone.sphinx.elite.DropDownList.DividerBar;
import com.pinball3d.zone.sphinx.elite.DropDownList.FolderBar;;

public class MenuBar {
	private EliteMainwindow parent;
	private List<Menu> list = new ArrayList<Menu>();
	private int chosenIndex = -1;
	private boolean isClicked, isKeyBoard;

	public MenuBar(EliteMainwindow parent) {
		this.parent = parent;
	}

	public MenuBar addMenu(Menu menu) {
		list.add(menu);
		return this;
	}

	public void doRender(int mouseX, int mouseY) {
		float x = 1;
		EliteRenderHelper.drawRect(0, 0, parent.width, 7, 0xFF535353);
		for (int i = 0; i < list.size(); i++) {
			Menu m = list.get(i);
			x += m.doRender(x, chosenIndex == i ? isClicked ? 2 : 1 : 0);
		}
	}

	public void onMouseMoved(int mouseX, int mouseY, int moveX, int moveY) {
		if (computeChosenIndex(mouseX, mouseY) && isClicked) {
			openDropDownList(false);
		}
	}

	private boolean computeChosenIndex(int mouseX, int mouseY) {
		int old = chosenIndex;
		if (mouseY <= 7) {
			float x = 1;
			for (int i = 0; i < list.size(); i++) {
				Menu m = list.get(i);
				float w = m.getWidth();
				if (mouseX >= x && mouseX <= x + w) {
					chosenIndex = i;
					return old != chosenIndex;
				}
				x += m.getWidth();
			}
		}
		if (!isClicked && !isKeyBoard) {
			chosenIndex = -1;
		}
		return old != chosenIndex;
	}

	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if (!isClicked) {
			if (isKeyBoard) {
				isKeyBoard = false;
				computeChosenIndex(mouseX, mouseY);
			}
			if (mouseButton == 0 && chosenIndex >= 0 && chosenIndex < list.size()) {
				isClicked = true;
				parent.setFocus(this::onMenuShortCut);
				isKeyBoard = true;
				openDropDownList(false);
			}
		} else {
			if (!(mouseButton == 1 && chosenIndex >= 0 && chosenIndex < list.size())) {
				isClicked = false;
				isKeyBoard = false;
				computeChosenIndex(mouseX, mouseY);
			}
		}
	}

	public void onPressAlt() {
		if (chosenIndex < 0) {
			chosenIndex = 0;
			isKeyBoard = true;
			parent.setFocus(this::onMenuShortCut);
		} else {
			chosenIndex = -1;
			isKeyBoard = false;
		}
	}

	private void openDropDownList(boolean flag) {
		float x = 1;
		for (int i = 0; i < chosenIndex; i++) {
			Menu m = list.get(i);
			x += m.getWidth();
		}
		DropDownList l = new DropDownList(parent, null, x, 5.75F);
		l.addBar(new ButtonBar(l, "¼×ÒÒ±û¶¡Îì¼º¸ýAbCdEf", "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));
		l.addBar(new ButtonBar(l, "¼×ÒÒ±û¶¡Îì¼º¸ýAb", "Ctrl+Shift+R"));
		l.addBar(new ButtonBar(l, "¼×ÒÒ±û¶¡Îì¼º¸ýAbCdEfGhi", "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));
		l.addBar(new DividerBar(l));
		l.addBar(new ButtonBar(l, "¼×", ""));
		l.addBar(new FolderBar(l, "sf"));
		l.addBar(new ButtonBar(l, "¼×ÒÒ±û¶¡Îì¼º¸ýAbCdEfGhi", "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));
		if (flag) {
			l.setChosenIndex(0);
		}
		parent.setDropDownList(l);
	}

	public void onMenuShortCut(char c, int keyCode) {
		if (isKeyBoard) {
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
				isKeyBoard = false;
				chosenIndex = -1;
			}
		}
	}

	public boolean onQuit() {
		if (isKeyBoard) {
			isKeyBoard = false;
			chosenIndex = -1;
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
		isKeyBoard = false;
		isClicked = false;
		chosenIndex = -1;
	}

	public static class Menu {
		private String name;
		private char shortCut;

		public Menu(String name, char shortCut) {
			this.name = name;
			this.shortCut = shortCut;
		}

		public float doRender(float x, int flag) {
			float w = getWidth();
			if (flag == 1) {
				EliteRenderHelper.drawTexture(EliteMainwindow.ELITE, x, 1, 0, 20, (int) (w * 4 - 3), 20, 0.25F);
				EliteRenderHelper.drawTexture(EliteMainwindow.ELITE, x + w - 0.75F, 1, 253, 20, 3, 20, 0.25F);
			} else if (flag == 2) {
				EliteRenderHelper.drawTexture(EliteMainwindow.ELITE, x, 1, 0, 0, (int) (w * 4 - 3), 20, 0.25F);
				EliteRenderHelper.drawTexture(EliteMainwindow.ELITE, x + w - 0.75F, 1, 253, 0, 3, 20, 0.25F);
			}
			FontHelper.renderText(x + 2, 1.5F, name, 0xFFC7C7C7);
			return w;
		}

		public float getWidth() {
			return FontHelper.getStringWidth(name) + 3.25F;
		}

		public String getName() {
			return name;
		}

		public char getShortCut() {
			return shortCut;
		}
	}
}
