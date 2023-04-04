package com.pinball3d.zone.sphinx.elite.components;

import com.pinball3d.zone.sphinx.elite.Color;
import com.pinball3d.zone.sphinx.elite.Component;
import com.pinball3d.zone.sphinx.elite.Drag;
import com.pinball3d.zone.sphinx.elite.EliteMainwindow;
import com.pinball3d.zone.sphinx.elite.EliteRenderHelper;
import com.pinball3d.zone.sphinx.elite.Subpanel;

public class ScrollingBar extends Component {
	private boolean horizontal;

	public ScrollingBar(EliteMainwindow parent, Subpanel parentPanel, boolean isHorizontal, int length) {
		super(parent, parentPanel, isHorizontal ? length : 17, isHorizontal ? 17 : length);
		horizontal = isHorizontal;
	}

	@Override
	public Drag mouseClicked(int mouseX, int mouseY, int mouseButton) {
		return new Drag(mouseButton);
	}

	public boolean isHorizontal() {
		return horizontal;
	}

	@Override
	public void doRender(int mouseX, int mouseY, float partialTicks) {
		super.doRender(mouseX, mouseY, partialTicks);
		EliteRenderHelper.drawRect(0, 0, getWidth(), getHeight(), Color.FF171717);
		if (horizontal) {

		} else {
			System.out.println(mouseX + "|" + mouseY);
			if (mouseX >= 0 && mouseX <= 17) {
				if (mouseY >= 0 && mouseY <= 17) {
					EliteRenderHelper.drawRect(1, 0, 15, 17, Color.FF373737);
				}
				if (mouseY >= getHeight() - 17 && mouseY <= getHeight()) {
					EliteRenderHelper.drawRect(1, getHeight() - 17, 15, 17, Color.FF373737);
				}
			}
			EliteRenderHelper.drawTexture(EliteMainwindow.ELITE, 5, 5, 116, 77, 7, 6);
			EliteRenderHelper.drawTexture(EliteMainwindow.ELITE, 5, getHeight() - 11, 123, 77, 7, 6);
		}

	}

}
