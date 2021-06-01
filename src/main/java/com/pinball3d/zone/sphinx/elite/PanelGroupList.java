package com.pinball3d.zone.sphinx.elite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.lwjgl.input.Keyboard;

import com.pinball3d.zone.sphinx.elite.panels.Panel;

public class PanelGroupList implements IDropDownList {
	private int x, y;
	private EliteMainwindow parent;
	private PanelGroup parentGroup;
	private int chosenIndex = -1;
	private boolean isText = true;

	public PanelGroupList(EliteMainwindow parent, PanelGroup parentGroup, int x, int y) {
		this.parent = parent;
		this.parentGroup = parentGroup;
		this.x = x;
		this.y = y;
	}

	@Override
	public void doRender(int mouseX, int mouseY) {
		int width = getWidth();
		int height = getHeight();

		int yOffset = 44;
		List<Panel> list = new ArrayList<Panel>(parentGroup.getPanels());
		Collections.sort(list, (a, b) -> a.getName().compareTo(b.getName()));
		EliteRenderHelper.drawBorder(x, y, width, height, 1, 0xFF383838);
		EliteRenderHelper.drawRect(x + 1, y + 1, width - 2, height - 2, 0xFF535353);
		EliteRenderHelper.drawRect(x + 6, y + 38, width - 12, 1, 0xFF9F9F9F);
		EliteRenderHelper.drawRect(x + 6, y + 39, width - 12, 1, 0xFFFFFFFF);
		for (int i = 0; i < list.size(); i++) {
			Panel p = list.get(i);
			if (chosenIndex == i) {
				EliteRenderHelper.drawRect(x + 6, y + yOffset, width - 12, 22, 0xFF383838);
				if (!isText) {
					EliteRenderHelper.drawDottedBorder(x + 6, y + yOffset, width - 12, 22, 0xFFD9BEA7);
				}
			}
			FontHandler.renderText(x + 11, y + yOffset + 5, p.getName(), 0xFFFFFFFF);
			yOffset += 22;
		}
		if (isText) {
			EliteRenderHelper.drawRect(x + 7, y + 9, 1, 20, 0xFF383838);
		}
	}

	@Override
	public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if (mouseButton != 0) {
			return true;
		}
		return isMouseInList(mouseX, mouseY);
	}

	@Override
	public boolean isMouseInList(int mouseX, int mouseY) {
		computeChosenIndex(mouseX, mouseY);
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
		if (chosenIndex >= 0) {
//			list.get(chosenIndex).onClick();
			// TODO
			return true;
		}
		return false;
	}

	@Override
	public void onMouseMoved(int mouseX, int mouseY, int moveX, int moveY) {
		if (mouseX >= x + 7 && mouseX <= x + getWidth() - 7 && mouseY >= y + 9 && mouseY <= y + 29) {
			// TODO
		}
		computeChosenIndex(mouseX, mouseY);
	}

	@Override
	public void keyTyped(char typedChar, int keyCode) {
		switch (keyCode) {
		case Keyboard.KEY_UP:
			isText = false;
			if (chosenIndex == 0) {
				chosenIndex = -1;
				isText = true;
			} else if (chosenIndex < 0) {
				chosenIndex = parentGroup.getPanels().size() - 1;
			} else {
				chosenIndex = chosenIndex - 1;
			}
			break;
		case Keyboard.KEY_DOWN:
			isText = false;
			if (chosenIndex >= parentGroup.getPanels().size() - 1) {
				chosenIndex = -1;
				isText = true;
			} else if (chosenIndex < 0) {
				chosenIndex = 0;
			} else {
				chosenIndex = chosenIndex + 1;
			}
			break;
		case Keyboard.KEY_LEFT:
			// TODO
			break;
		case Keyboard.KEY_RIGHT:
			// TODO
			break;
		case Keyboard.KEY_RETURN:
			// TODO if focus box: open index0 else open index
			break;
		}
	}

	private boolean computeChosenIndex(int mouseX, int mouseY) {
		int old = chosenIndex;
		int w = getWidth();
		int h = getHeight();
		chosenIndex = -1;
		if (mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h) {
			if (mouseX >= x + 6 && mouseX <= x + w - 6 && mouseY >= y + 44 && mouseY <= y + h - 5) {
				int yOffset = y + 44;
				for (int i = 0; i < parentGroup.getPanels().size(); i++) {
					if (mouseY >= yOffset && mouseY <= yOffset + 22) {
						chosenIndex = i;
						return old != chosenIndex;
					}
					yOffset += 22;
				}
			}
		}
		return old != chosenIndex;
	}

	public void setChosenIndex(int chosenIndex) {
		this.chosenIndex = chosenIndex;
	}

	public int getWidth() {
		return parentGroup.getPanels().stream().mapToInt(e -> FontHandler.getStringWidth(e.getName())).max().getAsInt()
				+ 31;
	}

	public int getHeight() {
		return parentGroup.getPanels().size() * 22 + 49;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public PanelGroupList setX(int x) {
		this.x = x;
		return this;
	}

	public PanelGroupList setY(int y) {
		this.y = y;
		return this;
	}

	@Override
	public boolean onQuit() {
		return true;
	}
}
