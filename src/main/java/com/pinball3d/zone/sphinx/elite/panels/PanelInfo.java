package com.pinball3d.zone.sphinx.elite.panels;

import com.pinball3d.zone.math.Pos2i;
import com.pinball3d.zone.sphinx.elite.Color;
import com.pinball3d.zone.sphinx.elite.EliteMainwindow;
import com.pinball3d.zone.sphinx.elite.FormattedString;
import com.pinball3d.zone.sphinx.elite.PanelGroup;
import com.pinball3d.zone.sphinx.elite.ui.component.Label;
import com.pinball3d.zone.sphinx.elite.ui.core.Panel;
import com.pinball3d.zone.sphinx.elite.ui.core.Subpanel;
import com.pinball3d.zone.sphinx.elite.ui.core.layout.PosLayout;

import net.minecraft.client.resources.I18n;

public class PanelInfo extends Panel {
	private InfoType type;

	public PanelInfo(EliteMainwindow parent, PanelGroup parentGroup) {
		super(parent, parentGroup, new FormattedString(I18n.format("elite.panel.info")));
		Subpanel root = getRoot();

		root.addComponent(new Label(parent, root, getName(), Color.TEXT_LIGHT));

		Subpanel panel1 = new Subpanel(parent, root, 200, 60, new PosLayout());
		panel1.addComponent(new Label(parent, panel1, new FormattedString("DR R R R RRRRR"), Color.TEXT_LIGHT),
				new Pos2i(5, 0));
		panel1.addComponent(new Label(parent, panel1, new FormattedString("III"), Color.TEXT_LIGHT), new Pos2i(15, 15));
		root.addComponent(panel1);
		for (int i = 0; i < 100; i++) {
			root.addComponent(new Label(parent, root, new FormattedString("DR R R R RRRRR" + i), Color.TEXT_LIGHT));
		}
	}

	@Override
	public void doRenderPre(int mouseX, int mouseY, float partialTicks) {
		refreshInfo();
		super.doRenderPre(mouseX, mouseY, partialTicks);
	}

	public void refreshInfo() {

	}

	public static enum InfoType {
		BLOCK;
	}
}
