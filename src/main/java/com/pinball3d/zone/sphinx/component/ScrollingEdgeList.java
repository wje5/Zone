package com.pinball3d.zone.sphinx.component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

import com.pinball3d.zone.sphinx.IHasComponents;
import com.pinball3d.zone.util.Util;

import net.minecraft.client.gui.FontRenderer;

public class ScrollingEdgeList extends Component {
	public int scrollingDistance;
	public List<ListBar> list = new ArrayList<ListBar>();
	private int stretch;
	public int index;
	private Predicate<Integer> onChange;

	public ScrollingEdgeList(IHasComponents parent, int x, int y, int height) {
		super(parent, x, y, 56, height);
	}

	public void addBar(String title, Runnable onClick) {
		list.add(new ListBar(title, onClick));
	}

	public void addBar(ListBar bar) {
		list.add(bar);
	}

	public void clear() {
		list.clear();
	}

	public boolean isEmpty() {
		return list.isEmpty();
	}

	public ListBar get() {
		return list.isEmpty() ? null : list.get(index);
	}

	public void setOnChange(Predicate<Integer> onChange) {
		this.onChange = onChange;
	}

	@Override
	public void doRender(int mouseX, int mouseY) {
		super.doRender(mouseX, mouseY);
		Iterator<ListBar> it = list.iterator();
		FontRenderer fr = Util.getFontRenderer();
		int index = 0;
		while (it.hasNext()) {
			ListBar e = it.next();
			int posY = index * 15 - scrollingDistance;
			int cutUp = posY < 0 ? -posY : 0;
			int cutDown = posY + 13 - height > 0 ? posY + 13 - height : 0;
			if (cutUp < 13 && cutDown < 13) {
				Util.drawTexture(ICONS, this.index == index ? x : x + 43 - stretch, y + posY + cutUp, 0,
						187 + cutUp * 4, stretch >= 43 || this.index == index ? 225 : stretch * 4 + 52,
						50 - cutUp * 4 - cutDown * 4, 0.25F);
				if (cutUp <= 3 && cutDown <= 3 && (this.index == index || stretch > 7)) {
					String text = Util.formatString(e.title);
					text = Util.formatStringToWidth(fr, text, this.index == index ? 36 : stretch - 7);
					fr.drawString(text, this.index == index ? x + 20 : x + 63 - stretch, y + 3 + posY, 0xFF1ECCDE);
				}
			}
			index++;
		}
		if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
			stretch = stretch + 5 >= 43 ? 43 : stretch + 5;
		} else {
			stretch = 0;
		}
	}

	@Override
	public boolean onLeftClick(int x, int y) {
		if (super.onLeftClick(x, y)) {
			return true;
		}
		int index = 0;
		Iterator<ListBar> it = list.iterator();
		y += scrollingDistance;
		while (it.hasNext()) {
			ListBar e = it.next();
			if (y >= index * 15 && y <= index * 15 + 13) {
				if (index != this.index) {
					if (onChange == null || onChange.test(index)) {
						e.event.run();
						this.index = index;
					}
				}
				return true;
			}
			index++;
		}
		return false;
	}

	@Override
	public boolean onMouseScroll(int mouseX, int mouseY, boolean isUp) {
		super.onMouseScroll(mouseX, mouseY, isUp);
		int maxScrollingDistance = list.size() * 15 - height;
		if (maxScrollingDistance < 0) {
			scrollingDistance = 0;
			return true;
		}
		scrollingDistance += isUp ? 15 : -15;
		scrollingDistance = scrollingDistance < 0 ? 0 : scrollingDistance;
		scrollingDistance = scrollingDistance > maxScrollingDistance ? maxScrollingDistance : scrollingDistance;
		return true;
	}

	@Override
	public boolean onDrag(int mouseX, int mouseY, int moveX, int moveY) {
		super.onDrag(mouseX, mouseY, moveX, moveY);
		int maxScrollingDistance = list.size() * 15 - height;
		if (maxScrollingDistance < 0) {
			scrollingDistance = 0;
			return true;
		}
		scrollingDistance -= moveY;
		scrollingDistance = scrollingDistance < 0 ? 0 : scrollingDistance;
		scrollingDistance = scrollingDistance > maxScrollingDistance ? maxScrollingDistance : scrollingDistance;
		return true;
	}

	public static class ListBar {
		public String title;
		private Runnable event;
		private Object data;

		public ListBar(String name, Runnable onClick) {
			title = name;
			event = onClick;
		}

		public void setData(Object data) {
			this.data = data;
		}

		public Object getData() {
			return data;
		}
	}
}
