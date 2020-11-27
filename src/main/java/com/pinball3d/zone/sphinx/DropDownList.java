package com.pinball3d.zone.sphinx;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.FontRenderer;

public class DropDownList extends Component {
	public String text = "";
	public List<ListBar> list = new ArrayList<ListBar>();

	public DropDownList(IParent parent, int x, int y, int width) {
		super(parent, x, y, width, 13);
		this.x = x;
		this.y = y;
	}

	public void addBar(String title, Runnable onClick) {
		list.add(new ListBar(title, onClick));
	}

	@Override
	public boolean onLeftClick(int x, int y) {
		if (super.onLeftClick(x, y)) {
			return true;
		}
		return true;
	}

	@Override
	public void doRender(int mouseX, int mouseY) {
		super.doRender(mouseX, mouseY);
		Util.drawBorder(x, y, width, height, 1, 0xFF1ECCDE);
		FontRenderer renderer = parent.getFontRenderer();
		renderer.drawString(text, x + 3, y + 3, 0xFF1ECCDE);
	}

	public class ListBar {
		public String title;
		public Runnable event;

		public ListBar(String name, Runnable onClick) {
			title = name;
			event = onClick;
		}
	}
}
