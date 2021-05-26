package com.pinball3d.zone.sphinx.elite.panels;

import com.pinball3d.zone.sphinx.elite.EliteMainwindow;
import com.pinball3d.zone.sphinx.elite.FontHandler;
import com.pinball3d.zone.sphinx.elite.PanelGroup;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

public class PanelMap extends Panel {
	public PanelMap(EliteMainwindow parent, PanelGroup parentGroup) {
		super(parent, parentGroup, I18n.format("elite.panel.map"));
	}

	@Override
	public void doRender(int mouseX, int mouseY) {
		FontHandler.renderText(10, 20, getName(), 0xFFF0F0F0, getParentGroup().getWidth());
		FontHandler.renderText(10, 40, "§o我§n能吞§l下玻璃而§r不伤身§l体(", 0xFFF0F0F0, getParentGroup().getWidth());
		FontHandler.renderText(10, 0, "(123中(文测试）AaBbCc", 0xFFF0F0F0, getParentGroup().getWidth());
		FontHandler.renderText(10, 60, "FPS:" + Minecraft.getDebugFPS(), 0xFFF0F0F0, getParentGroup().getWidth());
		FontHandler.renderText(0, 80, "§o我§n能吞§l下玻璃而§r不伤身§l体(KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK",
				0xFFF0F0F0, getParentGroup().getWidth());
		super.doRender(mouseX, mouseY);
	}
}
