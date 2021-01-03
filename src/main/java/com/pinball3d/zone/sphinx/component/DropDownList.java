package com.pinball3d.zone.sphinx.component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;

import com.pinball3d.zone.sphinx.IHasComponents;
import com.pinball3d.zone.util.Util;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;

public class DropDownList extends Component {
	public int index = 0;
	public List<ListBar> list = new ArrayList<ListBar>();
	public boolean isDrop;
	public IntConsumer onChange;

	public DropDownList(IHasComponents parent, int x, int y, int width) {
		super(parent, x, y, width + 10, 15);
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
		if (isDrop && y >= 16) {
			int i = (y - 16) / 14;
			if (index != i) {
				if (onChange != null) {
					onChange.accept(index);
				}
			}
			index = i;
			list.get(i).event.run();
		}
		setState(!isDrop);
		return true;
	}

	public DropDownList setOnChange(IntConsumer onChange) {
		this.onChange = onChange;
		return this;
	}

	public void setState(boolean isDrop) {
		this.isDrop = isDrop;
		height = isDrop ? 15 + list.size() * 14 : 15;
	}

	@Override
	public void doRender(int mouseX, int mouseY) {
		super.doRender(mouseX, mouseY);
		FontRenderer renderer = Util.getFontRenderer();
		Util.drawBorder(x, y, width, 15, 1, 0xFF1ECCDE);
		if (!list.isEmpty()) {
			renderer.drawString(list.get(index).title, x + 3, y + 3, 0xFF1ECCDE);
		}
		if (isDrop) {
			for (int i = 0; i < list.size(); i++) {
				Gui.drawRect(x, y + i * 14 + 15, x + 1, y + i * 14 + 29, 0xFF00479D);
				Gui.drawRect(x + width - 1, y + i * 14 + 15, x + width, y + i * 14 + 29, 0xFF00479D);
				Gui.drawRect(x + 1, y + i * 14 + 28, x + width - 1, y + i * 14 + 29, 0xFF00479D);
				Gui.drawRect(x + 1, y + i * 14 + 15, x + width - 1, y + i * 14 + 28, 0xFFE0E0E0);
				renderer.drawString(list.get(i).title, x + 3, y + 17 + i * 14, 0xFF000000);
			}
		}
		Util.drawTexture(ICONS, x + width - 11, y, 196, 41, 11, 15, 1.0F);

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
