package com.pinball3d.zone.sphinx.elite;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.pinball3d.zone.network.NetworkHandler;
import com.pinball3d.zone.network.elite.MessageCloseElite;
import com.pinball3d.zone.tileentity.TETerminal;
import com.pinball3d.zone.util.Pair;
import com.pinball3d.zone.util.WorldPos;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;

public class ScreenChooseNetwork extends GuiScreen {
	private int dropBoxState = 1, chooseBoxState, pressedButton, mousePrevX, mousePrevY, chosenIndex, hoverIndex,
			pressIndex;
	private boolean isPressInDropList, isAlt, startElite;
	private WorldPos terminalPos;

	private List<Pair<UUID, String>> data;

	public ScreenChooseNetwork(WorldPos terminalPos, List<Pair<UUID, String>> data) {
		this.terminalPos = terminalPos;
		this.data = data;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		if (terminalPos.getDim() != mc.player.dimension
				|| mc.player.getDistance(terminalPos.getPos().getX() + 0.5F, terminalPos.getPos().getY() + 0.5F,
						terminalPos.getPos().getZ() + 0.5F) > 16F
				|| !(terminalPos.getTileEntity() instanceof TETerminal)) {
			mc.displayGuiScreen(null);
		}
		GlStateManager.matrixMode(GL11.GL_PROJECTION);
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();
		GlStateManager.ortho(0, mc.displayWidth, mc.displayHeight, 0, 1000.0D, 3000.0D);
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		GlStateManager.pushMatrix();
		drawGradientRect(0, 0, mc.displayWidth, mc.displayHeight, -1072689136, -804253680);
		int width = 626;
		int height = 317;
		int x = (mc.displayWidth - width) / 2;
		int y = (mc.displayHeight - height) / 2 - 65;
		mouseX = MouseHandler.getX();
		mouseY = MouseHandler.getY();
		if (mouseX != mousePrevX || mouseY != mousePrevY) {
			int length = data.size() * 17;
			if (dropBoxState == 2 && (isPressInDropList || !Mouse.isButtonDown(0)) && mouseX >= x + 90
					&& mouseX <= x + 512 && mouseY >= y + 138 && mouseY < y + 138 + length) {
				hoverIndex = (mouseY - y - 138) / 17;
			}
		}
		mousePrevX = mouseX;
		mousePrevY = mouseY;
		EliteRenderHelper.drawBorder(x, y, width, height, 1, new Color(0xFF1883D7));
		EliteRenderHelper.drawRect(x + 1, y + 1, width - 2, 25, new Color(0xFF0078D7));
		EliteRenderHelper.drawRect(x + 1, y + 26, width - 2, 71, Color.WHITE);
		EliteRenderHelper.drawRect(x + 1, y + 95, width - 2, 1, new Color(0xFFA0A0A0));
		EliteRenderHelper.drawRect(x + 1, y + 97, width - 2, 219, new Color(0xFFF0F0F0));
		EliteRenderHelper.drawTexture(EliteMainwindow.ELITE, x + 5, y + 6, 116, 57, 16, 14);
		FontHandler.renderText(x + 26, y + 5, new FormattedString(I18n.format("elite.choose_network.title")),
				Color.WHITE);
		FontHandler.renderText(x + 7, y + 37, new FormattedString(I18n.format("elite.choose_network.text1")),
				Color.BLACK);
		FontHandler.renderText(x + 12, y + 62, new FormattedString(I18n.format("elite.choose_network.text2")),
				Color.BLACK);
		if (mouseX >= x + 592 && mouseX <= x + width && mouseY >= y && mouseY <= y + 26
				&& (pressedButton == 0 || pressedButton == 1)) {
			EliteRenderHelper.drawTexture(EliteMainwindow.ELITE, x + 594, y + 1, pressedButton == 1 ? 216 : 185, 57, 31,
					24);
		} else {
			EliteRenderHelper.drawTexture(EliteMainwindow.ELITE, x + 605, y + 8, 132, 57, 10, 10);
		}
		FontHandler.renderText(x + 12, y + 118, new FormattedString(I18n.format("elite.choose_network.text3")),
				Color.BLACK);
		if (mouseX <= x + 35
				+ FontHandler.renderText(x + 30, y + 238,
						new FormattedString(I18n.format("elite.choose_network.text4")), Color.BLACK, isAlt)
				&& mouseX >= x + 12 && mouseY >= y + 238 && mouseY <= y + 254) {
			if (chooseBoxState == 1) {
				EliteRenderHelper.drawTexture(EliteMainwindow.ELITE, x + 12, y + 239, 155, pressedButton == 4 ? 70 : 57,
						13, 13);
			} else if (pressedButton == 4) {
				EliteRenderHelper.drawBorder(x + 12, y + 239, 13, 13, 1, new Color(0xFF005499));
				EliteRenderHelper.drawRect(x + 13, y + 240, 11, 11, new Color(0xFFCCE4F7));
			} else {
				EliteRenderHelper.drawBorder(x + 12, y + 239, 13, 13, 1, new Color(0xFF0571C7));
				EliteRenderHelper.drawRect(x + 13, y + 240, 11, 11, Color.WHITE);
			}
		} else if (chooseBoxState == 1) {
			EliteRenderHelper.drawTexture(EliteMainwindow.ELITE, x + 12, y + 239, 142, 57, 13, 13);
		} else {
			EliteRenderHelper.drawBorder(x + 12, y + 239, 13, 13, 1, new Color(0xFF333333));
			EliteRenderHelper.drawRect(x + 13, y + 240, 11, 11, Color.WHITE);
		}
		if (mouseX >= x + 425 && mouseX <= x + 515 && mouseY >= y + 275 && mouseY <= y + 300
				&& (pressedButton == 0 || pressedButton == 2)) {
			if (pressedButton == 2) {
				EliteRenderHelper.drawRect(x + 425, y + 275, 90, 25, new Color(0xFFCCE4F7));
				EliteRenderHelper.drawBorder(x + 425, y + 275, 90, 25, 1, new Color(0xFF005499));
			} else {
				EliteRenderHelper.drawRect(x + 425, y + 275, 90, 25, new Color(0xFFE5F1FB));
				EliteRenderHelper.drawBorder(x + 425, y + 275, 90, 25, 1, new Color(0xFF0078D7));
			}
		} else {
			EliteRenderHelper.drawRect(x + 425, y + 275, 90, 25, new Color(0xFFE1E1E1));
			EliteRenderHelper.drawBorder(x + 425, y + 275, 90, 25, 2, new Color(0xFF0078D7));
		}
		FontHandler.renderTextCenter(x + 470, y + 279, new FormattedString(I18n.format("sphinx.launch")), Color.BLACK);
		if (mouseX >= x + 523 && mouseX <= x + 613 && mouseY >= y + 275 && mouseY <= y + 300
				&& (pressedButton == 0 || pressedButton == 3)) {
			if (pressedButton == 3) {
				EliteRenderHelper.drawRect(x + 523, y + 275, 90, 25, new Color(0xFFCCE4F7));
				EliteRenderHelper.drawBorder(x + 523, y + 275, 90, 25, 1, new Color(0xFF005499));
			} else {
				EliteRenderHelper.drawRect(x + 523, y + 275, 90, 25, new Color(0xFFE5F1FB));
				EliteRenderHelper.drawBorder(x + 523, y + 275, 90, 25, 1, new Color(0xFF0078D7));
			}
		} else {
			EliteRenderHelper.drawRect(x + 523, y + 275, 90, 25, new Color(0xFFE1E1E1));
			EliteRenderHelper.drawBorder(x + 523, y + 275, 90, 25, 1, new Color(0xFFADADAD));
		}
		FontHandler.renderTextCenter(x + 568, y + 279, new FormattedString(I18n.format("sphinx.cancel")), Color.BLACK);
		if (dropBoxState == 2) {
			int length = data.size() * 17;
			EliteRenderHelper.drawBorder(x + 89, y + 137, 424, (length > 0 ? length : 17) + 2, 1,
					new Color(0xFF0078D7));
			EliteRenderHelper.drawRect(x + 90, y + 138, 422, length > 0 ? length : 17, Color.WHITE);
			for (int i = 0; i < data.size(); i++) {
				String s = data.get(i).value();
				if (hoverIndex == i) {
					EliteRenderHelper.drawRect(x + 90, y + 138 + i * 17, 422, 17, new Color(0xFF0078D7));
					FontHandler.renderText(x + 93, y + 138 + i * 17, new FormattedString(s), Color.WHITE);
				} else {
					FontHandler.renderText(x + 93, y + 138 + i * 17, new FormattedString(s), Color.BLACK);
				}
			}
		}
		EliteRenderHelper.drawBorder(x + 89, y + 113, 424, 25, 1,
				dropBoxState == 0 ? new Color(0xFF7A7A7A) : new Color(0xFF0078D7));
		EliteRenderHelper.drawRect(x + 90, y + 114, 422, 23, Color.WHITE);
		if (!data.isEmpty()) {
			FontHandler.renderText(x + 93, y + 117, new FormattedString(data.get(chosenIndex).value()), Color.BLACK);
		}
		if (mouseX >= x + 496 && mouseX <= x + 513 && mouseY >= y + 113 && mouseY <= y + 138) {
			EliteRenderHelper.drawTexture(EliteMainwindow.ELITE, x + 496, y + 113, 168, 57, 17, 25);
		} else {
			EliteRenderHelper.drawTexture(EliteMainwindow.ELITE, x + 499, y + 123, 116, 71, 10, 6);
		}
		GlStateManager.popMatrix();
		GlStateManager.matrixMode(GL11.GL_PROJECTION);
		GlStateManager.popMatrix();
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		int width = 626;
		int height = 317;
		int x = (mc.displayWidth - width) / 2;
		int y = (mc.displayHeight - height) / 2 - 65;
		mouseX = MouseHandler.getX();
		mouseY = MouseHandler.getY();
		if (mouseX <= x + 35
				+ FontHandler.getStringWidth(new FormattedString(I18n.format("elite.choose_network.text4")))
				&& mouseX >= x + 12 && mouseY >= y + 238 && mouseY <= y + 254) {
			pressedButton = 4;
			dropBoxState = 0;
		} else {
			if (mouseX >= x + 89 && mouseX <= x + 513 && mouseY >= y + 113 && mouseY <= y + 138) {
				dropBoxState = mouseX >= x + 496 ? dropBoxState == 2 ? 1 : 2 : 1;
			} else {
				int length = data.size() * 17;
				if (dropBoxState == 2 && mouseX >= x + 89 && mouseX <= x + 513 && mouseY >= y + 138
						&& mouseY <= y + 138 + length) {
					pressIndex = hoverIndex;
					isPressInDropList = true;
				} else {
					if (dropBoxState == 2) {
						dropBoxState = 1;
					}
					if (mouseX >= x + 592 && mouseX <= x + width && mouseY >= y && mouseY <= y + 26) {
						pressedButton = 1;
					} else if (mouseX >= x + 425 && mouseX <= x + 515 && mouseY >= y + 275 && mouseY <= y + 300) {
						pressedButton = 2;
					} else if (mouseX >= x + 523 && mouseX <= x + 613 && mouseY >= y + 275 && mouseY <= y + 300) {
						pressedButton = 3;
					}
				}
			}
		}
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int mouseButton) {
		int width = 626;
		int height = 317;
		int x = (mc.displayWidth - width) / 2;
		int y = (mc.displayHeight - height) / 2 - 65;
		mouseX = MouseHandler.getX();
		mouseY = MouseHandler.getY();
		if (dropBoxState == 2) {
			if (isPressInDropList) {
				int length = data.size() * 17;
				if (mouseX >= x + 90 && mouseX <= x + 512 && mouseY >= y + 138 && mouseY < y + 138 + length) {
					chosenIndex = hoverIndex;
				} else {
					chosenIndex = pressIndex;
					pressIndex = -1;
				}
				isPressInDropList = false;
				dropBoxState = 1;
			}
		}
		if (mouseX <= x + 35
				+ FontHandler.getStringWidth(new FormattedString(I18n.format("elite.choose_network.text4")))
				&& mouseX >= x + 12 && mouseY >= y + 238 && mouseY <= y + 254) {
			if (pressedButton == 4) {
				if (chooseBoxState == 1) {
					chooseBoxState = 0;
				} else {
					chooseBoxState = 1;
				}
			}
		} else if (mouseX >= x + 592 && mouseX <= x + width && mouseY >= y && mouseY <= y + 26) {
			if (pressedButton == 1) {
				mc.displayGuiScreen(null);
			}
		} else if (mouseX >= x + 425 && mouseX <= x + 515 && mouseY >= y + 275 && mouseY <= y + 300) {
			if (pressedButton == 2 && !data.isEmpty()) {
				startElite = true;
				mc.displayGuiScreen(new EliteMainwindow(terminalPos, data.get(chosenIndex).key()));
			}
		} else if (mouseX >= x + 523 && mouseX <= x + 613 && mouseY >= y + 275 && mouseY <= y + 300) {
			if (pressedButton == 3) {
				mc.displayGuiScreen(null);
			}
		}
		pressedButton = 0;
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		super.keyTyped(typedChar, keyCode);
		switch (keyCode) {
		case Keyboard.KEY_UP:
			if (dropBoxState >= 1 && !data.isEmpty()) {
				if (hoverIndex >= 0) {
					chosenIndex = hoverIndex <= 0 ? 0 : hoverIndex - 1;
					hoverIndex = chosenIndex;
				} else {
					chosenIndex = chosenIndex <= 0 ? 0 : chosenIndex - 1;
				}
			}
			break;
		case Keyboard.KEY_DOWN:
			if (dropBoxState >= 1 && !data.isEmpty()) {
				if (hoverIndex >= 0) {
					chosenIndex = hoverIndex < data.size() - 1 ? hoverIndex + 1 : data.size() - 1;
					hoverIndex = chosenIndex;
				} else {
					chosenIndex = chosenIndex < data.size() - 1 ? chosenIndex + 1 : data.size() - 1;
				}
			}
			break;
		case Keyboard.KEY_LMENU:
		case Keyboard.KEY_RMENU:
			isAlt = true;
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		if (!startElite) {
			NetworkHandler.instance.sendToServer(new MessageCloseElite(terminalPos));
		}
	}
}
