package com.pinball3d.zone.sphinx.elite.ui.component;

import com.pinball3d.zone.sphinx.elite.Color;
import com.pinball3d.zone.sphinx.elite.EliteMainwindow;
import com.pinball3d.zone.sphinx.elite.FontHandler;
import com.pinball3d.zone.sphinx.elite.FormattedString;
import com.pinball3d.zone.sphinx.elite.FormattedString.StringComponent;
import com.pinball3d.zone.sphinx.elite.ui.core.Component;
import com.pinball3d.zone.sphinx.elite.ui.core.Subpanel;

public class Label extends Component {
	private FormattedString text;
	private Color color;

	public Label(EliteMainwindow parent, Subpanel parentPanel, FormattedString text, Color color) {
		super(parent, parentPanel, FontHandler.getStringWidth(text), FontHandler.HEIGHT);
		this.text = text;
		this.color = color;

	}

	@Override
	public void doRender(int mouseX, int mouseY, float partialTicks) {
		super.doRender(mouseX, mouseY, partialTicks);
		FontHandler.renderText(0, 0, text, color, getRenderWidth());
	}

	public FormattedString getText() {
		return text;
	}

	public void setText(FormattedString text) {
		this.text = text;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	@Override
	public int getWidth() {
		return FontHandler.getStringWidth(text);
	}

	@Override
	public int getMinWidth() {
		StringComponent s = text.get(0);
		StringComponent s2 = new StringComponent(s.text.substring(0, 1) + "…", s.color, s.bold, s.italic, s.underline);
		return Math.min(getWidth(), FontHandler.getStringWidth(new FormattedString(s2)));
	}
}
