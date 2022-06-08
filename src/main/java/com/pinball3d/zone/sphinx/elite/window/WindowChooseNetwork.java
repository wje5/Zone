package com.pinball3d.zone.sphinx.elite.window;

import com.pinball3d.zone.sphinx.elite.Color;
import com.pinball3d.zone.sphinx.elite.EliteMainwindow;
import com.pinball3d.zone.sphinx.elite.FormattedString;
import com.pinball3d.zone.sphinx.elite.components.Label;

import net.minecraft.client.resources.I18n;

public class WindowChooseNetwork extends FloatingWindow {

	public WindowChooseNetwork(EliteMainwindow parent) {
		super(parent, 626, 317, new FormattedString(I18n.format("elite.window.keyboard_shortcuts_and_menu.title")));
		getRoot().addComponent(
				new Label(parent, getRoot(), new FormattedString(I18n.format("")), new Color(0xFFEEEEEE)));
	}

}
