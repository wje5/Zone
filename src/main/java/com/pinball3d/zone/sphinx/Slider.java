package com.pinball3d.zone.sphinx;

import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

public class Slider extends Component {
	private static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/gui/sphinx/icons.png");
	public int scrollingDistance;
	public Runnable onChange;

	public Slider(IParent parent, int x, int y, int width) {
		super(parent, x, y + 1, width, 5);
	}

	@Override
	public void doRender(int mouseX, int mouseY) {
		super.doRender(mouseX, mouseY);
		Gui.drawRect(x + 2, y - 1, x + width - 2, y, 0xFF1ECCDE);
		Util.drawTexture(TEXTURE, x + scrollingDistance, y + 1, 102, 32, 5, 5, 1.0F);
	}

	@Override
	public boolean onDrag(int mouseX, int mouseY, int moveX, int moveY) {
		scrollingDistance = mouseX - 2;
		scrollingDistance = scrollingDistance < 0 ? 0 : scrollingDistance;
		scrollingDistance = scrollingDistance > width - 5 ? width - 5 : scrollingDistance;
		onChange.run();
		super.onDrag(mouseX, mouseY, moveX, moveY);
		return true;
	}

	public float get() {
		return 1.0F * scrollingDistance / (width - 5);
	}

	public void set(float f) {
		scrollingDistance = (int) (f * (width - 5));
	}

	public void setOnChange(Runnable onChange) {
		this.onChange = onChange;
	}
}
