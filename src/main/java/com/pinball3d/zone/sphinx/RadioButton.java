package com.pinball3d.zone.sphinx;

public class RadioButton extends Component {
	protected Runnable event;
	protected boolean isChoosed;

	public RadioButton(IParent parent, int x, int y, Runnable onClick) {
		super(parent, x, y, 15, 15);
		this.x = x;
		this.y = y;
		event = onClick;
	}

	public void setState(boolean flag) {
		this.isChoosed = flag;
	}

	@Override
	public boolean onLeftClick(int x, int y) {
		if (super.onLeftClick(x, y)) {
			return true;
		}
		isChoosed = true;
		event.run();
		return true;
	}

	@Override
	public void doRender(int mouseX, int mouseY) {
		super.doRender(mouseX, mouseY);
		Util.drawTexture(ICONS, x, y, isChoosed ? 122 : 107, 41, 15, 15, 1.0F);
	}
}
