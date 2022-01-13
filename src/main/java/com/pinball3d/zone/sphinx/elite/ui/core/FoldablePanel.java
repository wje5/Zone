package com.pinball3d.zone.sphinx.elite.ui.core;

import java.util.function.Supplier;

import com.pinball3d.zone.sphinx.elite.Color;
import com.pinball3d.zone.sphinx.elite.EliteMainwindow;
import com.pinball3d.zone.sphinx.elite.FormattedString;
import com.pinball3d.zone.sphinx.elite.TextureLocation;
import com.pinball3d.zone.sphinx.elite.ui.component.ImageLabel;
import com.pinball3d.zone.sphinx.elite.ui.component.Label;
import com.pinball3d.zone.sphinx.elite.ui.core.layout.BoxLayout;
import com.pinball3d.zone.util.Pair;

public class FoldablePanel extends Subpanel {
	private boolean fold = true;

	public final Subpanel panel;

	public FoldablePanel(EliteMainwindow parent, Subpanel parentPanel, FormattedString text, ILayout layout) {
		this(parent, parentPanel, () -> text, layout);
	}

	public FoldablePanel(EliteMainwindow parent, Subpanel parentPanel, Supplier<FormattedString> text, ILayout layout) {
		super(parent, parentPanel, new BoxLayout(true));
		Subpanel p1 = new Subpanel(parent, this, new BoxLayout(false));
		p1.setOnClick(() -> setFold(!isFold())).setExpand(true);
		p1.addComponent(new ImageLabel(parent, p1,
				() -> new Pair<TextureLocation, Float>(
						new TextureLocation(EliteMainwindow.ELITE, isFold() ? 8 : 0, 145, 8, 8), 1F)).setMarginRight(3),
				BoxLayout.Type.CENTER);
		p1.addComponent(new Label(parent, p1, text, Color.TEXT_LIGHT));
		addComponent(p1);

		panel = new Subpanel(parent, this, layout) {
			@Override
			public boolean isHide() {
				return super.isHide() || fold;
			}
		};
		panel.setMarginLeft(5);
		addComponent(panel, BoxLayout.Type.WEST);
	}

	public boolean isFold() {
		return fold;
	}

	public FoldablePanel setFold(boolean fold) {
		this.fold = fold;
		return this;
	}
}
