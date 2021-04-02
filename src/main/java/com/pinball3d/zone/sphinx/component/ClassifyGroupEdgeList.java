package com.pinball3d.zone.sphinx.component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

import com.pinball3d.zone.gui.IHasComponents;
import com.pinball3d.zone.gui.component.Component;
import com.pinball3d.zone.network.ConnectHelperClient;
import com.pinball3d.zone.network.MessageNewClass;
import com.pinball3d.zone.network.NetworkHandler;
import com.pinball3d.zone.util.Util;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;

public class ClassifyGroupEdgeList extends Component {
	public int scrollingDistance;
	public List<ListBar> list = new ArrayList<ListBar>();
	private int stretch;
	public int index;
	private Predicate<Integer> onChange;

	public ClassifyGroupEdgeList(IHasComponents parent, int x, int y, int height) {
		super(parent, x, y, 56, height);
	}

	public void addBar(String title, int id, Runnable onClick) {
		list.add(new ListBar(title, id, onClick));
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

	public ClassifyGroupEdgeList setOnChange(Predicate<Integer> onChange) {
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
	public void doRender(int mouseX, int mouseY, int upCut, int downCut) {
		super.doRender(mouseX, mouseY, upCut, downCut);// TODO
		Iterator<ListBar> it = list.iterator();
		FontRenderer fr = Util.getFontRenderer();
		int index = 0;
		boolean flag = true;
		while (it.hasNext() || flag) {
			ListBar e = null;
			if (it.hasNext()) {
				e = it.next();
			} else {
				flag = false;
			}
			int posY = index * 15 - scrollingDistance;
			int cutUp = posY < 0 ? -posY : 0;
			int cutDown = posY + 13 - height > 0 ? posY + 13 - height : 0;
			if (cutUp < 13 && cutDown < 13) {
				Util.drawTexture(ICONS, (this.index == index ? 0 : 43 - stretch) - 3, posY + cutUp - 3, 0,
						187 + cutUp * 4, (stretch >= 43 || this.index == index ? 225 : stretch * 4 + 52) + 19,
						(50 - cutUp * 4 - cutDown * 4) + 19, 0.25F);
				if (cutUp <= 3 && cutDown <= 3 && (this.index == index || stretch > 7)) {
					String text = flag ? Util.formatString(e.title) : I18n.format("sphinx.new_class");
					text = Util.formatStringToWidth(fr, text, this.index == index ? 36 : stretch - 7);
					Util.renderGlowString(text, this.index == index ? 20 : 63 - stretch, 3 + posY);
				}
				if (!flag) {
					int xOffset = this.index == index ? 10 : 53 - stretch;
					Util.drawTexture(ICONS, xOffset, posY + cutUp + 3, 193, 155 + cutUp * 4, 32,
							32 - cutUp * 4 - cutDown * 4, 0.25F);
				}
			}
			index++;
		}
		if (mouseX >= 0 && mouseX <= width && mouseY >= 0 && mouseY <= height) {
			stretch = stretch + 5 >= 28 ? 28 : stretch + 5;
		} else {
			stretch = 0;
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
		boolean flag = true;
		while (it.hasNext() || flag) {
			if (it.hasNext()) {
				it.next();
			} else {
				flag = false;
			}
			if (y >= index * 15 && y <= index * 15 + 13) {
				if (flag) {
					change(index);
				} else {
					NetworkHandler.instance.sendToServer(
							MessageNewClass.newMessage(ConnectHelperClient.getInstance().getNetworkPos()));
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
		int maxScrollingDistance = list.size() * 15 + 15 - height;
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
		int maxScrollingDistance = list.size() * 15 + 15 - height;
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
		public int id;
		private Runnable event;
		private Object data;

		public ListBar(String name, int id, Runnable onClick) {
			title = name;
			this.id = id;
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
