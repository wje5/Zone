package com.pinball3d.zone.gui.component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

import com.pinball3d.zone.gui.IHasComponents;
import com.pinball3d.zone.util.Util;

import net.minecraft.util.ResourceLocation;

public class ScrollingEdgeTextureList extends Component {
	public int scrollingDistance;
	public List<ListBar> list = new ArrayList<ListBar>();
	public int index;
	private boolean nullable;
	private Predicate<Integer> onChange;

	public ScrollingEdgeTextureList(IHasComponents parent, int x, int y, int height) {
		super(parent, x, y, 20, height);
	}

	public void addBar(ResourceLocation texture, int width, int height, int u, int v, int uWidth, int vHeight,
			Runnable onClick) {
		list.add(new ListBar(texture, width, height, u, v, uWidth, vHeight, onClick));
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

	public ScrollingEdgeTextureList setNullable(boolean nullable) {
		this.nullable = nullable;
		index = -1;
		return this;
	}

	public ListBar get() {
		return list.size() > index && index >= 0 ? list.get(index) : null;
	}

	public ScrollingEdgeTextureList setOnChange(Predicate<Integer> onChange) {
		this.onChange = onChange;
		return this;
	}

	public void change(int index) {
		if (index < list.size()) {
			if (onChange == null || onChange.test(index)) {
				list.get(index).event.run();
				this.index = index;
			}
		}
	}

	@Override
	public void doRender(int mouseX, int mouseY) {
		super.doRender(mouseX, mouseY);
		Iterator<ListBar> it = list.iterator();
		int index = 0;
		while (it.hasNext()) {
			ListBar e = it.next();
			int posY = index * 25 - scrollingDistance;
			int cutUp = posY < 0 ? -posY : 0;
			int cutDown = posY + 13 - height > 0 ? posY + 13 - height : 0;
			if (cutUp < 13 && cutDown < 13) {
				Util.drawTexture(ICONS_5, (this.index == index ? 0 : 4) - 4, posY + cutUp - 3, 180, 60 + cutUp * 2,
						(this.index == index ? 52 : 39), (40 - cutUp * 4 - cutDown * 4) + 11, 0.5F);
				Util.drawTexture(e.texture, this.index == index ? 5 : 9, posY + 5, e.width, e.height, e.u, e.v,
						e.uWidth, e.vHeight);
			}
			index++;
		}
	}

	@Override
	public boolean onLeftClick(int x, int y) {
		if (super.onLeftClick(x, y)) {
			return true;
		}
		if (enable != null && !enable.getAsBoolean()) {
			return false;
		}
		int index = 0;
		Iterator<ListBar> it = list.iterator();
		y += scrollingDistance;
		while (it.hasNext()) {
			ListBar e = it.next();
			if (y >= index * 25 && y <= index * 25 + 20) {
				if (index != this.index) {
					if (onChange == null || onChange.test(index)) {
						if (e.event != null) {
							e.event.run();
						}

						this.index = index;
					}
				} else if (nullable) {
					if (onChange == null || onChange.test(index)) {
						if (e.event != null) {
							e.event.run();
						}
						this.index = -1;
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
		if (super.onMouseScroll(mouseX, mouseY, isUp)) {
			return true;
		}
		if (enable != null && !enable.getAsBoolean()) {
			return false;
		}
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
		if (super.onDrag(mouseX, mouseY, moveX, moveY)) {
			return true;
		}
		if (enable != null && !enable.getAsBoolean()) {
			return false;
		}
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
		protected ResourceLocation texture;
		protected int width, height, u, v, uWidth, vHeight;
		private Runnable event;
		private Object data;

		public ListBar(ResourceLocation texture, int width, int height, int u, int v, int uWidth, int vHeight,
				Runnable onClick) {
			this.texture = texture;
			this.width = width;
			this.height = height;
			this.u = u;
			this.v = v;
			this.uWidth = uWidth;
			this.vHeight = vHeight;
			event = onClick;
		}

		public ListBar setData(Object data) {
			this.data = data;
			return this;
		}

		public Object getData() {
			return data;
		}
	}
}
