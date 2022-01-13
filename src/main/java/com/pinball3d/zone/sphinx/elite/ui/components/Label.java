package com.pinball3d.zone.sphinx.elite.ui.components;

import java.util.function.Supplier;

import com.pinball3d.zone.sphinx.elite.Color;
import com.pinball3d.zone.sphinx.elite.EliteMainwindow;
import com.pinball3d.zone.sphinx.elite.FontHandler;
import com.pinball3d.zone.sphinx.elite.FormattedString;
import com.pinball3d.zone.sphinx.elite.FormattedString.StringComponent;
import com.pinball3d.zone.sphinx.elite.ui.core.Component;
import com.pinball3d.zone.sphinx.elite.ui.core.Subpanel;

public class Label extends Component {
	private Supplier<FormattedString> text;
	private Color color;

	public Label(EliteMainwindow parent, Subpanel parentPanel, FormattedString text, Color color) {
		this(parent, parentPanel, () -> text, color);
	}

	public Label(EliteMainwindow parent, Subpanel parentPanel, Supplier<FormattedString> text, Color color) {
		super(parent, parentPanel, FontHandler.getStringWidth(text.get()), FontHandler.HEIGHT);
		this.text = text;
		this.color = color;
	}

	@Override
	public void doRender(int mouseX, int mouseY, float partialTicks) {
		super.doRender(mouseX, mouseY, partialTicks);
		FontHandler.renderText(0, 0, getText(), color, getRenderWidth());
	}

	public FormattedString getText() {
		return text.get();
	}

	public void setText(Supplier<FormattedString> text) {
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
		return FontHandler.getStringWidth(getText());
	}

	@Override
	public int getMinWidth() {
		FormattedString f = text.get();
		if (f == null) {
			return 0;
		}
		StringComponent s = f.get(0);
		StringComponent s2 = new StringComponent(s.text.substring(0, 1) + "…", s.color, s.bold, s.italic, s.underline);
		return Math.min(getWidth(), FontHandler.getStringWidth(new FormattedString(s2)));
	}
}
