package com.pinball3d.zone.sphinx.elite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.pinball3d.zone.sphinx.elite.MouseHandler.MouseType;
import com.pinball3d.zone.sphinx.elite.panels.Panel;
import com.pinball3d.zone.util.Util;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;

public class PanelGroupList implements IDropDownList {
	private int x, y;
	private EliteMainwindow parent;
	private PanelGroup parentGroup;
	private int chosenIndex = -1, cursorIndex = -1, cursorIndex2 = -2, textOffset = 0, dragX, dragY;
	private boolean isText = true, hoverText, init, dragLeft, dragRight, dragText;
	private List<Panel> list, list2;
	private String text = "";

	public PanelGroupList(EliteMainwindow parent, PanelGroup parentGroup, int x, int y) {
		this.parent = parent;
		this.parentGroup = parentGroup;
		this.x = x;
		this.y = y;
	}

	@Override
	public void doRender(int mouseX, int mouseY) {
		if (!init) {
			init = true;
			computeChosenIndex(mouseX, mouseY);
		}
		updateList();
		int width = getWidth();
		int height = getHeight();
		int yOffset = 44;
		EliteRenderHelper.drawBorder(x, y, width, height, 1, Color.DIVIDER_BG);
		EliteRenderHelper.drawRect(x + 1, y + 1, width - 2, height - 2, Color.COMP_BG_LIGHT);
		EliteRenderHelper.drawRect(x + 6, y + 38, width - 12, 1, Color.FF9F9F9F);
		EliteRenderHelper.drawRect(x + 6, y + 39, width - 12, 1, Color.WHITE);
		for (int i = 0; i < list.size(); i++) {
			Panel p = list.get(i);
			if (chosenIndex == i) {
				EliteRenderHelper.drawRect(x + 6, y + yOffset, width - 12, 22, Color.DIVIDER_BG);
				if (!isText) {
					EliteRenderHelper.drawDottedBorder(x + 6, y + yOffset, width - 12, 22, Color.DOTTED_LINE);
				}
			}
			FontHandler.renderText(x + 11, y + yOffset + 5, new FormattedString("§l" + p.getName()), Color.WHITE);
			yOffset += 22;
		}
		for (int i = 0; i < list2.size(); i++) {
			Panel p = list2.get(i);
			if (chosenIndex == i + list.size()) {
				EliteRenderHelper.drawRect(x + 6, y + yOffset, width - 12, 22, Color.DIVIDER_BG);
				if (!isText) {
					EliteRenderHelper.drawDottedBorder(x + 6, y + yOffset, width - 12, 22, Color.DOTTED_LINE);
				}
			}
			FontHandler.renderText(x + 11, y + yOffset + 5, p.getName(), Color.WHITE);
			yOffset += 22;
		}

		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 0, -800F);
		GlStateManager.enableDepth();
		GlStateManager.depthFunc(GL11.GL_GEQUAL);
		EliteRenderHelper.drawRect(x + 7, y + 9, width - 13, 20, Color.COMP_BG_LIGHT);
		GlStateManager.depthFunc(GL11.GL_LEQUAL);

		int cursorOffset = FontHandler.getStringWidth(new FormattedString(text.substring(0, cursorIndex + 1), false));
		if (isText) {
			if (cursorIndex2 > -2) {
				int cursorOffset2 = FontHandler
						.getStringWidth(new FormattedString(text.substring(0, cursorIndex2 + 1), false));
				if (parent.mc.world.getTotalWorldTime() % 20 < 10) {
					EliteRenderHelper.drawRect(x + 7 + cursorOffset - textOffset, y + 9, 1, 20,
							Color.CHOSEN_TEXT_CURSOR);
				}
				if (cursorIndex2 > cursorIndex) {
					EliteRenderHelper.drawRect(x + 8 + cursorOffset - textOffset, y + 9,
							cursorOffset2 - cursorOffset - 1, 20, Color.CHOSEN_TEXT_BG);
				} else {
					EliteRenderHelper.drawRect(x + 7 + cursorOffset2 - textOffset, y + 9,
							cursorOffset - cursorOffset2 - 1, 20, Color.CHOSEN_TEXT_BG);
				}
			} else if (parent.mc.world.getTotalWorldTime() % 20 < 10) {
				EliteRenderHelper.drawRect(x + 7 + cursorOffset - textOffset, y + 9, 1, 20, Color.FFEDEDED);
			}
		}

		FontHandler.renderText(x + 7 - textOffset, y + 9, new FormattedString(text, false), Color.WHITE);
		GlStateManager.popMatrix();
	}

	public void updateList() {
		list = new ArrayList<Panel>(parentGroup.getPanels()
				.subList(parentGroup.getPanels().size() - parentGroup.getRemain(), parentGroup.getPanels().size()));
		Collections.sort(list, (a, b) -> a.getName().toString().compareTo(b.getName().toString()));
		list2 = new ArrayList<Panel>(
				parentGroup.getPanels().subList(0, parentGroup.getPanels().size() - parentGroup.getRemain()));
		Collections.sort(list2, (a, b) -> a.getName().toString().compareTo(b.getName().toString()));
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
			return new Drag((x, y, moveX, moveY) -> {
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
			}, cancel -> {
				dragText = false;
			});
		}
		if (chosenIndex >= 0) {
			isText = false;
		}
		dragX = mouseX;
		dragY = mouseY;
		return new Drag((x, y, moveX, moveY) -> {
			dragX = x;
			dragY = y;
		}, cancel -> {
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
					parentGroup.calcPanelGroup(dragX, dragY);
				} else {
					parentGroup.setChosenIndex(parentGroup.getPanels().indexOf(list2.get(chosenIndex - list.size())));
					parentGroup.calcPanelGroup(dragX, dragY);
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
	public void onMouseMoved(int mouseX, int mouseY, int moveX, int moveY) {
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
				text = (min < 0 ? "" : text.substring(0, min + 1)) + s
						+ (max >= text.length() - 1 ? "" : text.substring(max + 1));
				cursorIndex = min + s.length();
				cursorIndex2 = -2;
			} else {
				text = text.substring(0, cursorIndex + 1) + s + text.substring(cursorIndex + 1);
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
				String s = deleteChosen();
				System.out.println(s);
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
		}
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
				deleteChosen();
				break;
			}
			if (cursorIndex >= 0) {
				int charWidth = FontHandler.getCharWidth(text.charAt(cursorIndex), FontHandler.NORMAL);
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
				deleteChosen();
				break;
			}
			if (cursorIndex < text.length() - 1) {
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
			if (isText && Util.isValidChar(typedChar, 8)) {
				if (cursorIndex2 != -2) {
					int min = 0, max = 0;
					if (cursorIndex > cursorIndex2) {
						min = cursorIndex2;
						max = cursorIndex;
					} else {
						min = cursorIndex;
						max = cursorIndex2;
					}
					text = (min < 0 ? "" : text.substring(0, min + 1)) + typedChar
							+ (max >= text.length() - 1 ? "" : text.substring(max + 1));
					cursorIndex = min + 1;
					cursorIndex2 = -2;
				} else {
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
			if (mouseX >= x + 7 && mouseX <= x + w - 7 && mouseY >= y + 9 && mouseY <= y + 29) {
				hoverText = true;
			}
			if (mouseX >= x + 6 && mouseX <= x + w - 6 && mouseY >= y + 44 && mouseY <= y + h - 5) {
				int yOffset = y + 44;
				for (int i = 0; i < parentGroup.getPanels().size(); i++) {
					if (mouseY >= yOffset && mouseY <= yOffset + 22 && (i != old || isText)) {
						chosenIndex = i;
						return true;
					}
					yOffset += 22;
				}
			}
		}
		return false;
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
