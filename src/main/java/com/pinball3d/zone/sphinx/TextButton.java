package com.pinball3d.zone.sphinx;

import net.minecraft.client.gui.Gui;

public class TextButton extends Component {
	protected String text;
	protected Runnable event;

	public TextButton(IParent parent, int x, int y, String text, Runnable onClick) {
		super(parent, x, y, parent.getFontRenderer().getStringWidth(text) + 8, 13);
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
//		Util.drawBorder(x, y, width, height, 1, 0xFF1ECCDE);
		Util.drawTexture(ICONS, x, y, 0, 237, 10, 15, 0.75F);
		Util.drawTexture(ICONS, x + width - 4, y, 10, 237, 10, 15, 0.75F);
		if (width > 15) {
			Gui.drawRect(x + 7, y, x + width - 4, y + 11, 0x32000000);
		}
		parent.getFontRenderer().drawString(text, x + 6, y + 1, 0xFF1ECCDE);
	}
}
