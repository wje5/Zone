package com.pinball3d.zone.sphinx;

public class TextButton extends Component {
	protected String text;
	protected Runnable event;

	public TextButton(IParent parent, int x, int y, String text, Runnable onClick) {
		super(parent, x, y, parent.getFontRenderer().getStringWidth(text) + 6, 13);
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
		Util.drawBorder(x, y, width, height, 1, 0xFF1ECCDE);
		parent.getFontRenderer().drawString(text, x + 3, y + 3, 0xFF1ECCDE);
	}
}
