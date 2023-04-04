package com.pinball3d.zone.sphinx.elite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.pinball3d.zone.math.Pos2i;
import com.pinball3d.zone.sphinx.elite.MouseHandler.MouseType;
import com.pinball3d.zone.sphinx.elite.components.ScrollingBar;
import com.pinball3d.zone.sphinx.elite.history.EventTyping;
import com.pinball3d.zone.sphinx.elite.history.History;
import com.pinball3d.zone.sphinx.elite.history.HistoryEvent;
import com.pinball3d.zone.sphinx.elite.layout.PosLayout;
import com.pinball3d.zone.sphinx.elite.panels.Panel;
import com.pinball3d.zone.util.Util;

import net.minecraft.client.gui.GuiScreen;

public class PanelGroupList implements IDropDownList {
	private int x, y, maxHeight = 596;
	private EliteMainwindow parent;
	private PanelGroup parentGroup;
	private int chosenIndex = -1, cursorIndex = -1, cursorIndex2 = -2, textOffset = 0, dragX, dragY;
	private boolean isText = true, hoverText, init, dragLeft, dragRight, dragText;
	private List<Panel> list = new ArrayList<Panel>(), list2 = new ArrayList<Panel>();
	private String text = "";
	private History history = new History();

	private ScrollingBar scrollingBar;
	private Subpanel panel;

	public PanelGroupList(EliteMainwindow parent, PanelGroup parentGroup, int x, int y) {
		this.parent = parent;
		this.parentGroup = parentGroup;
		this.x = x;
		this.y = y;
	}

	@Override
	public void doRender(int mouseX, int mouseY, float partialTicks) {
		if (!init) {
			init = true;
			computeChosenIndex(mouseX, mouseY);
			panel = new Subpanel(parent, null, 17, getHeight() - 39, new PosLayout()) {
				@Override
				public Pos2i getPos() {
					return new Pos2i(x + PanelGroupList.this.getWidth() - 20, y + 35);
				}

				@Override
				public int getRenderWidth() {
					return 17;
				}
			};
			panel.addComponent(scrollingBar = new ScrollingBar(parent, panel, false, getHeight() - 39));
		}
		updateList();

		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		GL11.glScissor(panel.getPos().x, parent.getHeight() - panel.getPos().y - panel.getHeight(), panel.getWidth(),
				panel.getHeight());
		GL11.glPushMatrix();
		GL11.glTranslatef(panel.getPos().x, panel.getPos().y, 0);
		panel.doRenderPre(mouseX, mouseY, partialTicks);
		GL11.glPopMatrix();
		GL11.glDisable(GL11.GL_SCISSOR_TEST);

		int width = getWidth();
		int height = getHeight();
		int yOffset = 35;
		EliteRenderHelper.drawBorder(x, y, width, height, 1, Color.FF646464);
		EliteRenderHelper.drawRect(x + 1, y + 1, width - 2, height - 2, Color.WINDOW_BG);
		EliteRenderHelper.drawRect(x + 6, y + 28, width - 12, 1, Color.FF9F9F9F);
		EliteRenderHelper.drawRect(x + 6, y + 29, width - 12, 1, Color.WHITE);
		EliteRenderHelper.drawRect(x + 6, y + 35, width - 10, height - 39, Color.BACKGROUND);
		int inRange = (height - 39) / 21;
		if (list.size() + list2.size() > inRange) {
//			EliteRenderHelper.drawRect(x + width - 20, y + 35, 17, height - 39, Color.FF171717);
			if (mouseX >= x + width - 20 && mouseX <= x + width - 4) {
				if (mouseY >= y + 35 && mouseY <= y + 51) {
					EliteRenderHelper.drawRect(x + width - 19, y + 35, 15, 17, Color.FF373737);
				}
				if (mouseY >= y + height - 21 && mouseY <= y + height - 4) {
					EliteRenderHelper.drawRect(x + width - 19, y + height - 21, 15, 17, Color.FF373737);
				}
			}
			EliteRenderHelper.drawTexture(EliteMainwindow.ELITE, x + width - 15, y + 40, 116, 77, 7, 6);
			EliteRenderHelper.drawTexture(EliteMainwindow.ELITE, x + width - 15, y + height - 15, 123, 77, 7, 6);
			int barHeight = height - 73;
//			int tabHeight = 
			EliteRenderHelper.drawRect(x + width - 20, y + 52, 17, barHeight, Color.WHITE);
		}
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		GL11.glScissor(x + 6, parent.getHeight() - (y + height - 4), width - 12, height - 39);
		for (int i = 0; i < list.size(); i++) {
			Panel p = list.get(i);
			if (chosenIndex == i) {
				EliteRenderHelper.drawBorder(x + 10, y + yOffset, width - 33, 21, 1, Color.HOVER_BLUE);
				EliteRenderHelper.drawRect(x + 11, y + yOffset + 1, width - 35, 19, Color.HOVER_BLUE_COVER);
				if (!isText) {
					EliteRenderHelper.drawDottedBorder(x + 10, y + yOffset, width - 33, 21, Color.DOTTED_LINE);
				}
			}
			EliteRenderHelper.drawTexture(EliteMainwindow.ICONS, x + 10, y + yOffset + 2, 0, 0, 16, 16);
			FontHandler.renderText(x + 29, y + yOffset + 2, new FormattedString("§l" + p.getName()), Color.WHITE);
			yOffset += 21;
		}
		for (int i = 0; i < list2.size(); i++) {
			Panel p = list2.get(i);
			if (chosenIndex == i + list.size()) {
				EliteRenderHelper.drawBorder(x + 10, y + yOffset, width - 33, 21, 1, Color.HOVER_BLUE);
				EliteRenderHelper.drawRect(x + 11, y + yOffset + 1, width - 35, 19, Color.HOVER_BLUE_COVER);
				if (!isText) {
					EliteRenderHelper.drawDottedBorder(x + 10, y + yOffset, width - 33, 21, Color.DOTTED_LINE);
				}
			}
			EliteRenderHelper.drawTexture(EliteMainwindow.ICONS, x + 10, y + yOffset + 2, 0, 0, 16, 16);
			FontHandler.renderText(x + 29, y + yOffset + 2, p.getName(), Color.WHITE);
			yOffset += 21;
		}
		GL11.glScissor(x + 7, parent.getHeight() - (y + 23), width - 13, 17);
		int cursorOffset = FontHandler.getStringWidth(new FormattedString(text.substring(0, cursorIndex + 1), false));
		if (isText) {
			if (cursorIndex2 > -2) {
				int cursorOffset2 = FontHandler
						.getStringWidth(new FormattedString(text.substring(0, cursorIndex2 + 1), false));
				if (cursorIndex2 > cursorIndex) {
					EliteRenderHelper.drawRect(x + 8 + cursorOffset - textOffset, y + 6,
							cursorOffset2 - cursorOffset - 1, 17, Color.CHOSEN_TEXT_BG);
				} else {
					EliteRenderHelper.drawRect(x + 7 + cursorOffset2 - textOffset, y + 6,
							cursorOffset - cursorOffset2 - 1, 17, Color.CHOSEN_TEXT_BG);
				}
				FontHandler.renderText(x + 7 - textOffset, y + 6, new FormattedString(text, false), Color.WHITE);
				if (parent.mc.world.getTotalWorldTime() % 20 < 10) {
					EliteRenderHelper.drawRect(x + 7 + cursorOffset - textOffset, y + 6, 1, 20,
							Color.CHOSEN_TEXT_CURSOR_INVERSE);
				}
			} else {
				FontHandler.renderText(x + 7 - textOffset, y + 6, new FormattedString(text, false), Color.FFD0D0D0);
				if (parent.mc.world.getTotalWorldTime() % 20 < 10) {
					EliteRenderHelper.drawRect(x + 7 + cursorOffset - textOffset, y + 6, 1, 17, Color.TEXT_CURSOR);
				}
			}
		}
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
		GL11.glScissor(panel.getPos().x, parent.getHeight() - panel.getPos().y - panel.getHeight(), panel.getWidth(),
				panel.getHeight());
		GL11.glPushMatrix();
		GL11.glTranslatef(panel.getPos().x, panel.getPos().y, 0);
		panel.doRender(mouseX, mouseY, partialTicks);
		GL11.glPopMatrix();

		GL11.glPushMatrix();
		GL11.glTranslatef(panel.getPos().x, panel.getPos().y, 0);
		panel.doRenderPost(mouseX, mouseY, partialTicks);
		GL11.glPopMatrix();

		GL11.glDisable(GL11.GL_SCISSOR_TEST);
	}

	public void updateList() {
		list = new ArrayList<Panel>(parentGroup.getPanels()
				.subList(parentGroup.getPanels().size() - parentGroup.getRemain(), parentGroup.getPanels().size()));
		list2 = new ArrayList<Panel>(
				parentGroup.getPanels().subList(0, parentGroup.getPanels().size() - parentGroup.getRemain()));
		Iterator<Panel> it = list.iterator();
		while (it.hasNext()) {
			Panel p = it.next();
			if (!p.getName().toString().startsWith(text)) {
				it.remove();
			}
		}
		it = list2.iterator();
		while (it.hasNext()) {
			Panel p = it.next();
			if (!p.getName().toString().startsWith(text)) {
				it.remove();
			}
		}
		Collections.sort(list2, (a, b) -> a.getName().toString().compareTo(b.getName().toString()));
		Collections.sort(list, (a, b) -> a.getName().toString().compareTo(b.getName().toString()));
	}

	@Override
	public Drag mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if (!isMouseInList(mouseX, mouseY)) {
			return null;
		}
		if (mouseButton == 0) {
			dragLeft = true;
		} else {
			dragRight = true;
		}
		if (mouseButton == 0 && hoverText) {
			isText = true;
			cursorIndex2 = -2;
			int xOffset = x + 7;
			boolean flag = false;
			for (int i = 0; i < text.length(); i++) {
				char c = text.charAt(i);
				int w = FontHandler.getCharWidth(c, FontHandler.NORMAL);
				if (mouseX >= xOffset - textOffset && mouseX < xOffset + w - textOffset) {
					cursorIndex = i;
					if (mouseX <= xOffset + w / 2 - textOffset) {
						cursorIndex--;
					}
					flag = true;
					break;
				}
				xOffset += w;
			}
			if (!flag) {
				cursorIndex = text.length() - 1;
				computeChosenIndex(mouseX, mouseY);
			}
			dragText = true;
			return new Drag(0, (x, y, moveX, moveY) -> {
				if (cursorIndex2 == -2) {
					cursorIndex2 = cursorIndex;
				}
				int xOffset2 = this.x + 7;
				boolean flag2 = false;
				for (int i = 0; i < text.length(); i++) {
					char c = text.charAt(i);
					int w = FontHandler.getCharWidth(c, FontHandler.NORMAL);
					if (x >= xOffset2 - textOffset && x < xOffset2 + w - textOffset) {
						cursorIndex = i;
						if (x <= xOffset2 + w / 2 - textOffset) {
							cursorIndex--;
						}
						flag2 = true;
						break;
					}
					xOffset2 += w;
				}
				if (!flag2) {
					if (x < this.x + 7) {
						cursorIndex = -1;
					} else {
						cursorIndex = text.length() - 1;
					}
					computeChosenIndex(x, y);
				}
				if (cursorIndex2 == cursorIndex) {
					cursorIndex2 = -2;
				}
				int cursorOffset = FontHandler
						.getStringWidth(new FormattedString(text.substring(0, cursorIndex + 1), false));
				while (cursorOffset < textOffset) {
					textOffset -= 1;
					if (textOffset < 0) {
						textOffset = 0;
						break;
					}
				}
				int w = FontHandler.getStringWidth(new FormattedString(text, false));
				int width = getWidth();
				while (cursorOffset > textOffset + width - 14) {
					textOffset += 1;
					if (textOffset > w - width + 14) {
						textOffset = w - width + 14;
					}
				}
			}, (x, y, cancel) -> {
				dragText = false;
			});
		}
		if (mouseButton >= 2) {
			return new Drag(mouseButton);
		}
		if (chosenIndex >= 0) {
			isText = false;
		}
		dragX = mouseX;
		dragY = mouseY;
		return new Drag(mouseButton, (x, y, moveX, moveY) -> {
			dragX = x;
			dragY = y;
		}, (x, y, cancel) -> {
			if (mouseButton == 0) {
				dragLeft = false;
			} else {
				dragRight = false;
			}
			updateList();
			isText = true;
			int old = chosenIndex;
			computeChosenIndex(dragX, dragY);
			isText = false;
			if (chosenIndex >= 0) {
				if (chosenIndex < list.size()) {
					Panel p = list.get(chosenIndex);
					parentGroup.getPanels().remove(p);
					parentGroup.getPanels().add(0, p);
					parentGroup.setChosenIndex(0);
					PanelGroup.calcPanelGroup(parent, dragX, dragY);
				} else {
					parentGroup.setChosenIndex(parentGroup.getPanels().indexOf(list2.get(chosenIndex - list.size())));
					PanelGroup.calcPanelGroup(parent, dragX, dragY);
				}
				parent.setDropDownList(null);
			} else {
				chosenIndex = old;
			}
		});
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
	public MouseType getMouseType(int mouseX, int mouseY) {
		if (hoverText || dragText) {
			return MouseType.TEXT_LIGHT;
		}
		return IDropDownList.super.getMouseType(mouseX, mouseY);
	}

	@Override
	public boolean mouseReleased(int mouseX, int mouseY, int mouseButton) {
		if (mouseButton != 0) {
			return true;
		}
		return dragLeft || dragRight;
	}

	@Override
	public void mouseMoved(int mouseX, int mouseY, int moveX, int moveY) {
		computeChosenIndex(mouseX, mouseY);
	}

	public String deleteChosen() {
		if (cursorIndex2 != -2) {
			int min = 0, max = 0;
			String removed = null;
			if (cursorIndex > cursorIndex2) {
				min = cursorIndex2;
				max = cursorIndex;
				removed = text.substring(min + 1, max + 1);
				textOffset -= FontHandler.getStringWidth(new FormattedString(removed, false));
			} else {
				min = cursorIndex;
				max = cursorIndex2;
				removed = text.substring(min + 1, max + 1);
			}
			text = (min < 0 ? "" : text.substring(0, min + 1))
					+ ((max >= text.length() - 1 ? "" : text.substring(max + 1)));
			cursorIndex = min;
			cursorIndex2 = -2;
			int cursorOffset = FontHandler
					.getStringWidth(new FormattedString(text.substring(0, cursorIndex + 1), false));
			while (cursorOffset < textOffset) {
				textOffset -= 50;
				if (textOffset < 0) {
					textOffset = 0;
					break;
				}
			}
			int w = FontHandler.getStringWidth(new FormattedString(text, false));
			int width = getWidth();
			if (textOffset > w - width + 14) {
				textOffset = w - width + 14;
			} else {
				while (cursorOffset > textOffset + width - 14) {
					textOffset += 50;
					if (textOffset > w - width + 14) {
						textOffset = w - width + 14;
						break;
					}
				}
			}
			if (textOffset < 0) {
				textOffset = 0;
			}
			return removed;
		}
		return "";
	}

	public void undo() {
		cursorIndex2 = -2;
		HistoryEvent event = history.undo();
		if (event != null) {
			switch (event.type) {
			case TYPING:
				EventTyping e = (EventTyping) event;
				text = text.substring(0, e.index) + e.old + text.substring(e.index + e.text.length(), text.length());
				cursorIndex = e.index + e.old.length() - 1;
				int width = getWidth();
				int cursorOffset = FontHandler
						.getStringWidth(new FormattedString(text.substring(0, cursorIndex + 1), false));
				int w = FontHandler.getStringWidth(new FormattedString(text, false));
				while (cursorOffset > textOffset + width - 14) {
					textOffset += 50;
					if (textOffset > w - width + 14) {
						textOffset = w - width + 14;
						break;
					}
				}
				while (cursorOffset < textOffset) {
					textOffset -= 50;
					if (textOffset < 0) {
						textOffset = 0;
						break;
					}
				}
				break;
			}
		}
	}

	public void redo() {
		cursorIndex2 = -2;
		HistoryEvent event = history.redo();
		if (event != null) {
			switch (event.type) {
			case TYPING:
				EventTyping e = (EventTyping) event;
				text = text.substring(0, e.index) + e.text + text.substring(e.index + e.old.length(), text.length());
				cursorIndex = e.index + e.text.length() - 1;
				int width = getWidth();
				int cursorOffset = FontHandler
						.getStringWidth(new FormattedString(text.substring(0, cursorIndex + 1), false));
				int w = FontHandler.getStringWidth(new FormattedString(text, false));
				while (cursorOffset > textOffset + width - 14) {
					textOffset += 50;
					if (textOffset > w - width + 14) {
						textOffset = w - width + 14;
						break;
					}
				}
				while (cursorOffset < textOffset) {
					textOffset -= 50;
					if (textOffset < 0) {
						textOffset = 0;
						break;
					}
				}
				break;
			}
		}
	}

	@Override
	public void keyTyped(char typedChar, int keyCode) {
		if (GuiScreen.isKeyComboCtrlV(keyCode)) {
			String s = GuiScreen.getClipboardString();
			if (cursorIndex2 != -2) {
				int min = 0, max = 0;
				if (cursorIndex > cursorIndex2) {
					min = cursorIndex2;
					max = cursorIndex;
				} else {
					min = cursorIndex;
					max = cursorIndex2;
				}
				String removed = text.substring(min + 1, max + 1);
				text = (min < 0 ? "" : text.substring(0, min + 1)) + s
						+ (max >= text.length() - 1 ? "" : text.substring(max + 1));
				cursorIndex = min + s.length();
				cursorIndex2 = -2;
				history.push(new EventTyping(min + 1, removed, s));
			} else {
				text = text.substring(0, cursorIndex + 1) + s + text.substring(cursorIndex + 1);
				history.push(new EventTyping(cursorIndex + 1, "", s));
				cursorIndex += s.length();

			}
			int w = FontHandler.getStringWidth(new FormattedString(text, false));
			int width = getWidth();
			int cursorOffset = FontHandler
					.getStringWidth(new FormattedString(text.substring(0, cursorIndex + 1), false));
			while (cursorOffset > textOffset + width - 14) {
				textOffset += 50;
				if (textOffset > w - width + 14) {
					textOffset = w - width + 14;
					break;
				}
			}
			return;
		}
		if (GuiScreen.isKeyComboCtrlX(keyCode)) {
			if (cursorIndex2 != -2) {
				if (cursorIndex > cursorIndex2) {
					history.push(
							new EventTyping(cursorIndex2 + 1, text.substring(cursorIndex2 + 1, cursorIndex + 1), ""));
				} else {
					history.push(
							new EventTyping(cursorIndex + 1, text.substring(cursorIndex + 1, cursorIndex2 + 1), ""));
				}
				String s = deleteChosen();
				GuiScreen.setClipboardString(s);
			}
			return;
		}
		if (GuiScreen.isKeyComboCtrlC(keyCode)) {
			if (cursorIndex2 != -2) {
				if (cursorIndex > cursorIndex2) {
					GuiScreen.setClipboardString(text.substring(cursorIndex2 + 1, cursorIndex + 1));
				} else {
					GuiScreen.setClipboardString(text.substring(cursorIndex + 1, cursorIndex2 + 1));
				}
			}
			return;
		}
		if (GuiScreen.isKeyComboCtrlA(keyCode)) {
			cursorIndex = text.length() - 1;
			cursorIndex2 = -1;
			int w = FontHandler.getStringWidth(new FormattedString(text, false));
			int width = getWidth();
			if (w > width - 14) {
				textOffset = w - width + 14;
			}
			return;
		}
		if (keyCode == Keyboard.KEY_Z && GuiScreen.isCtrlKeyDown() && !GuiScreen.isShiftKeyDown()
				&& !GuiScreen.isAltKeyDown()) {
			undo();
		}
		if (keyCode == Keyboard.KEY_Y && GuiScreen.isCtrlKeyDown() && !GuiScreen.isShiftKeyDown()
				&& !GuiScreen.isAltKeyDown()) {
			redo();
		}
		switch (keyCode) {
		case Keyboard.KEY_ESCAPE:
			parent.setDropDownList(null);
			break;
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
			if (GuiScreen.isShiftKeyDown()) {
				if (cursorIndex2 == -2) {
					cursorIndex2 = cursorIndex;
				}
			} else if (!GuiScreen.isCtrlKeyDown() && cursorIndex2 > -2) {
				cursorIndex = Math.min(cursorIndex, cursorIndex2);
				cursorIndex2 = -2;
				int cursorOffset = FontHandler
						.getStringWidth(new FormattedString(text.substring(0, cursorIndex + 1), false));
				while (cursorOffset < textOffset) {
					textOffset -= 50;
					if (textOffset < 0) {
						textOffset = 0;
						break;
					}
				}
				break;
			}
			if (cursorIndex >= 0) {
				if (GuiScreen.isCtrlKeyDown()) {
					cursorIndex = -1;
					textOffset = 0;
				} else {
					cursorIndex--;
					int cursorOffset = FontHandler
							.getStringWidth(new FormattedString(text.substring(0, cursorIndex + 1), false));
					while (cursorOffset < textOffset) {
						textOffset -= 50;
						if (textOffset < 0) {
							textOffset = 0;
							break;
						}
					}
				}
			}
			if (cursorIndex2 == cursorIndex) {
				cursorIndex2 = -2;
			}
			break;
		case Keyboard.KEY_RIGHT:
			if (GuiScreen.isShiftKeyDown()) {
				if (cursorIndex2 == -2) {
					cursorIndex2 = cursorIndex;
				}
			} else if (!GuiScreen.isCtrlKeyDown() && cursorIndex2 > -2) {
				cursorIndex = Math.max(cursorIndex, cursorIndex2);
				cursorIndex2 = -2;
				int cursorOffset = FontHandler
						.getStringWidth(new FormattedString(text.substring(0, cursorIndex + 1), false));
				int w = FontHandler.getStringWidth(new FormattedString(text, false));
				int width = getWidth();
				while (cursorOffset > textOffset + width - 14) {
					textOffset += 50;
					if (textOffset > w - width + 14) {
						textOffset = w - width + 14;
						break;
					}
				}
				break;
			}
			if (cursorIndex < text.length() - 1) {
				if (GuiScreen.isCtrlKeyDown()) {
					cursorIndex = text.length() - 1;
					int w = FontHandler.getStringWidth(new FormattedString(text, false));
					int width = getWidth();
					if (w > width - 14) {
						textOffset = w - width + 14;
					}
				} else {
					cursorIndex++;
					int cursorOffset = FontHandler
							.getStringWidth(new FormattedString(text.substring(0, cursorIndex + 1), false));
					int w = FontHandler.getStringWidth(new FormattedString(text, false));
					int width = getWidth();
					while (cursorOffset > textOffset + width - 14) {
						textOffset += 50;
						if (textOffset > w - width + 14) {
							textOffset = w - width + 14;
							break;
						}
					}
				}
			}
			if (cursorIndex2 == cursorIndex) {
				cursorIndex2 = -2;
			}
			break;
		case Keyboard.KEY_RETURN:
			if (chosenIndex < 0) {
				chosenIndex = 0;
			}
			if (chosenIndex < list.size()) {
				Panel p = list.get(chosenIndex);
				parentGroup.getPanels().remove(p);
				parentGroup.getPanels().add(0, p);
				parentGroup.setChosenIndex(0);
			} else {
				parentGroup.setChosenIndex(parentGroup.getPanels().indexOf(list2.get(chosenIndex - list.size())));
			}
			parent.setDropDownList(null);
			break;
		case Keyboard.KEY_BACK:
			if (cursorIndex2 != -2) {
				if (cursorIndex > cursorIndex2) {
					history.push(
							new EventTyping(cursorIndex2 + 1, text.substring(cursorIndex2 + 1, cursorIndex + 1), ""));
				} else {
					history.push(
							new EventTyping(cursorIndex + 1, text.substring(cursorIndex + 1, cursorIndex2 + 1), ""));
				}
				deleteChosen();
				break;
			}
			if (cursorIndex >= 0) {
				int charWidth = FontHandler.getCharWidth(text.charAt(cursorIndex), FontHandler.NORMAL);
				history.push(new EventTyping(cursorIndex, text.substring(cursorIndex, cursorIndex + 1), ""));
				text = text.substring(0, cursorIndex)
						+ (cursorIndex >= text.length() - 1 ? "" : text.substring(cursorIndex + 1));
				cursorIndex--;
				int w = FontHandler.getStringWidth(new FormattedString(text, false));
				int width = getWidth();
				if (w <= width - 14) {
					textOffset = 0;
				} else {
					textOffset -= charWidth;
					int cursorOffset = FontHandler
							.getStringWidth(new FormattedString(text.substring(0, cursorIndex + 1), false));
					if (textOffset < 0) {
						textOffset = 0;
						break;
					}
					while (cursorOffset < textOffset) {
						textOffset -= 50;
						if (textOffset < 0) {
							textOffset = 0;
							break;
						}
					}
				}
			}
			break;
		case Keyboard.KEY_DELETE:
			if (cursorIndex2 != -2) {
				if (cursorIndex > cursorIndex2) {
					history.push(
							new EventTyping(cursorIndex2 + 1, text.substring(cursorIndex2 + 1, cursorIndex + 1), ""));
				} else {
					history.push(
							new EventTyping(cursorIndex + 1, text.substring(cursorIndex + 1, cursorIndex2 + 1), ""));
				}
				deleteChosen();
				break;
			}
			if (cursorIndex < text.length() - 1) {
				history.push(new EventTyping(cursorIndex + 1, text.substring(cursorIndex + 1, cursorIndex + 2), ""));
				text = text.substring(0, cursorIndex + 1)
						+ (cursorIndex >= text.length() - 2 ? "" : text.substring(cursorIndex + 2));
				int w = FontHandler.getStringWidth(new FormattedString(text, false));
				int width = getWidth();
				if (w <= width - 14) {
					textOffset = 0;
				} else if (textOffset + width - 14 > w) {
					textOffset = w - (width - 14);
				}
			}
			break;
		default:
			if (isText) {
				if (!Util.isValidChar(typedChar, 8)) {
					break;
				}
				if (cursorIndex2 != -2) {
					int min = 0, max = 0;
					if (cursorIndex > cursorIndex2) {
						min = cursorIndex2;
						max = cursorIndex;
					} else {
						min = cursorIndex;
						max = cursorIndex2;
					}
					if (cursorIndex > cursorIndex2) {
						history.push(new EventTyping(cursorIndex2 + 1,
								text.substring(cursorIndex2 + 1, cursorIndex + 1), "" + typedChar));
					} else {
						history.push(new EventTyping(cursorIndex + 1, text.substring(cursorIndex + 1, cursorIndex2 + 1),
								"" + typedChar));
					}
					text = (min < 0 ? "" : text.substring(0, min + 1)) + typedChar
							+ (max >= text.length() - 1 ? "" : text.substring(max + 1));
					cursorIndex = min + 1;
					cursorIndex2 = -2;
				} else {
					history.push(new EventTyping(cursorIndex + 1, "", "" + typedChar));
					text = text.substring(0, cursorIndex + 1) + typedChar + text.substring(cursorIndex + 1);
					cursorIndex++;
				}
				int w = FontHandler.getStringWidth(new FormattedString(text, false));
				int width = getWidth();
				int cursorOffset = FontHandler
						.getStringWidth(new FormattedString(text.substring(0, cursorIndex + 1), false));
				while (cursorOffset > textOffset + width - 14) {
					textOffset += 50;
					if (textOffset > w - width + 14) {
						textOffset = w - width + 14;
						break;
					}
				}
			}
			break;
		}
	}

	private boolean computeChosenIndex(int mouseX, int mouseY) {
		int old = chosenIndex;
		int w = getWidth();
		int h = getHeight();
		hoverText = false;
		if (isText) {
			chosenIndex = -1;
		}
		if (mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h) {
			if (mouseX >= x + 7 && mouseX <= x + w - 7 && mouseY >= y + 7 && mouseY <= y + 23) {
				hoverText = true;
			}
			if (mouseX >= x + 6 && mouseX <= x + w - 23 && mouseY >= y + 36 && mouseY <= y + h - 4) {
				int yOffset = y + 36;
				for (int i = 0; i < list.size() + list2.size(); i++) {
					if (mouseY >= yOffset && mouseY <= yOffset + 21 && (i != old || isText)) {
						chosenIndex = i;
						return true;
					}
					yOffset += 21;
				}
			}
		}
		return false;
	}

	public void setChosenIndex(int chosenIndex) {
		this.chosenIndex = chosenIndex;
	}

	public int getWidth() {
		return parentGroup.getPanels().isEmpty() ? 0
				: parentGroup.getPanels().stream()
						.mapToInt(e -> FontHandler.getStringWidth(parentGroup.getPanels()
								.indexOf(e) < parentGroup.getPanels().size() - parentGroup.getRemain() ? e.getName()
										: new FormattedString("§l" + e.getName())))
						.max().getAsInt() + 54;
	}

	public int getHeight() {
		return Math.min(maxHeight, parentGroup.getPanels().size() * 21 + 42);
	}

	public void setMaxHeight(int maxHeight) {
		this.maxHeight = maxHeight;
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
