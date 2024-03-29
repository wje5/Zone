package com.pinball3d.zone.gui.component;

import com.pinball3d.zone.gui.IHasComponents;
import com.pinball3d.zone.util.Util;

public class Text extends Component {
	protected String text;

	public Text(IHasComponents parent, int x, int y, String text) {
		super(parent, x, y, Util.getFontRenderer().getStringWidth(text), Util.getFontRenderer().FONT_HEIGHT);
		this.text = text;
	}

	@Override
	public void doRender(int mouseX, int mouseY) {
		super.doRender(mouseX, mouseY);
		Util.renderGlowString(text, 0, 0);
	}
}
