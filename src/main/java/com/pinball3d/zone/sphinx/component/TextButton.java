package com.pinball3d.zone.sphinx.component;

import com.pinball3d.zone.gui.Component;
import com.pinball3d.zone.sphinx.IHasComponents;
import com.pinball3d.zone.util.Util;

import net.minecraft.client.gui.Gui;

public class TextButton extends Component {
	protected String text;
	protected Runnable event;

	public TextButton(IHasComponents parent, int x, int y, String text, Runnable onClick) {
		super(parent, x, y, Util.getFontRenderer().getStringWidth(text) + 8, 11);
		this.text = text;
		event = onClick;
	}

	@Override
	public boolean onLeftClick(int x, int y) {
		if (super.onLeftClick(x, y)) {
			return true;
		}
		if (enable != null && !enable.getAsBoolean()) {
			return false;
		}
		event.run();
		return true;
	}

	@Override
	public void doRender(int mouseX, int mouseY) {
		super.doRender(mouseX, mouseY);
		if (enable != null && !enable.getAsBoolean()) {
			return;
		}
		Util.drawTexture(ICONS, getX() - 4, getY() - 4, 117, 138, 38, 49, 0.375F);
		Util.drawTexture(ICONS, getX() + width - 7, getY() - 4, 155, 138, 38, 49, 0.375F);
		if (width > 15) {
			Gui.drawRect(getX() + 10, getY(), getX() + width - 7, getY() + 11, 0x32000000);
		}
		Util.renderGlowString(text, getX() + 6, getY() + 1);
	}
}
