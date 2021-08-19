package com.pinball3d.zone.sphinx.elite;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;

public class ScreenChooseNetwork extends GuiScreen {
	private int boxState;
	private boolean isChosen;

	public ScreenChooseNetwork() {

	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
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
		FontHandler.renderText(x + 12, y + 118, new FormattedString(I18n.format("elite.choose_network.text3")),
				Color.BLACK);
		FontHandler.renderText(x + 30, y + 238, new FormattedString(I18n.format("elite.choose_network.text4")),
				Color.BLACK);
		EliteRenderHelper.drawBorder(x + 89, y + 113, 424, 25, 1, new Color(0xFF7A7A7A));
		if (mouseX >= x + 592 && mouseX <= x + width && mouseY >= y && mouseY <= y + 26) {
			EliteRenderHelper.drawTexture(EliteMainwindow.ELITE, x + 594, y + 1, 185, 57, 31, 24);
		} else {
			EliteRenderHelper.drawTexture(EliteMainwindow.ELITE, x + 605, y + 8, 132, 57, 10, 10);
		}
		if (mouseX >= x + 496 && mouseX <= x + 513 && mouseY >= y + 113 && mouseY <= y + 138) {
			EliteRenderHelper.drawTexture(EliteMainwindow.ELITE, x + 496, y + 113, 168, 57, 17, 25);
		} else {
			EliteRenderHelper.drawTexture(EliteMainwindow.ELITE, x + 499, y + 123, 116, 71, 10, 6);
		}
		if (mouseY >= y + 238 && mouseY <= y + 254) {
			EliteRenderHelper.drawBorder(x + 12, y + 239, 13, 13, 1, new Color(0xFF0571C7));
		} else {
			EliteRenderHelper.drawBorder(x + 12, y + 239, 13, 13, 1, new Color(0xFF333333));
		}
		if (mouseX >= x + 425 && mouseX <= x + 515 && mouseY >= y + 275 && mouseY <= y + 300) {
			EliteRenderHelper.drawRect(x + 425, y + 275, 90, 25, new Color(0xFFE5F1FB));
			EliteRenderHelper.drawBorder(x + 425, y + 275, 90, 25, 1, new Color(0xFF0078D7));
		} else {
			EliteRenderHelper.drawRect(x + 425, y + 275, 90, 25, new Color(0xFFE1E1E1));
			EliteRenderHelper.drawBorder(x + 425, y + 275, 90, 25, 2, new Color(0xFF0078D7));
		}
		FontHandler.renderTextCenter(x + 470, y + 279, new FormattedString(I18n.format("sphinx.launch")), Color.BLACK);
		if (mouseX >= x + 523 && mouseX <= x + 613 && mouseY >= y + 275 && mouseY <= y + 300) {
			EliteRenderHelper.drawRect(x + 523, y + 275, 90, 25, new Color(0xFFE5F1FB));
			EliteRenderHelper.drawBorder(x + 523, y + 275, 90, 25, 1, new Color(0xFF0078D7));
		} else {
			EliteRenderHelper.drawRect(x + 523, y + 275, 90, 25, new Color(0xFFE1E1E1));
			EliteRenderHelper.drawBorder(x + 523, y + 275, 90, 25, 1, new Color(0xFFADADAD));
		}
		FontHandler.renderTextCenter(x + 568, y + 279, new FormattedString(I18n.format("sphinx.cancel")), Color.BLACK);
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
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
}
