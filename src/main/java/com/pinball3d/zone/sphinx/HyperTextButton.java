package com.pinball3d.zone.sphinx;

public class HyperTextButton extends Component {
	protected String text;
	protected Runnable event;

	public HyperTextButton(IParent parent, int x, int y, String text, Runnable onClick) {
		super(parent, x, y, parent.getFontRenderer().getStringWidth(text), parent.getFontRenderer().FONT_HEIGHT);
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
		parent.getFontRenderer().drawString(text, x, y, 0xFF1ECCDE);
	}
}
