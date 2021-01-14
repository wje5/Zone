package com.pinball3d.zone.sphinx.component;

import com.pinball3d.zone.sphinx.IHasComponents;
import com.pinball3d.zone.util.Util;

import net.minecraft.client.gui.Gui;

public class TextButton extends Component {
	protected String text;
	protected Runnable event;

	public TextButton(IHasComponents parent, int x, int y, String text, Runnable onClick) {
		super(parent, x, y, Util.getFontRenderer().getStringWidth(text) + 8, 11);
		this.x = x;
		this.y = y;
		this.text = text;
		event = onClick;
	}

	@Override
	public boolean onLeftClick(int x, int y) {
		if (super.onLeftClick(x, y)) {
			return true;
		}
		event.run();
		return true;
	}

	@Override
	public void doRender(int mouseX, int mouseY) {
		super.doRender(mouseX, mouseY);
		Util.drawTexture(ICONS, x, y, 117, 155, 20, 30, 0.375F);
		Util.drawTexture(ICONS, x + width - 4, y, 137, 155, 20, 30, 0.375F);
		if (width > 15) {
			Gui.drawRect(x + 7, y, x + width - 4, y + 11, 0x32000000);
		}
		Util.getFontRenderer().drawString(text, x + 6, y + 1, 0xFF1ECCDE);
	}
}