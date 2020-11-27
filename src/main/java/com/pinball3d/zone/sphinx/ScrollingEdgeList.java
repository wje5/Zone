package com.pinball3d.zone.sphinx;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.gui.FontRenderer;

public class ScrollingEdgeList extends Component {
	public int scrollingDistance;
	public List<ListBar> list = new ArrayList<ListBar>();
	private int stretch;

	public ScrollingEdgeList(IParent parent, int x, int y, int height) {
		super(parent, x, y, 56, height);
	}

	public void addBar(String title, Runnable onClick) {
		list.add(new ListBar(title, onClick));
	}

	@Override
	public void doRender(int mouseX, int mouseY) {
		super.doRender(mouseX, mouseY);
		Iterator<ListBar> it = list.iterator();
		FontRenderer fr = parent.getFontRenderer();
		int index = 0;
		while (it.hasNext()) {
			ListBar e = it.next();
			int posY = index * 15 - scrollingDistance;
			int cutUp = posY < 0 ? -posY : 0;
			int cutDown = posY + 13 - height > 0 ? posY + 13 - height : 0;
			if (cutUp < 13 && cutDown < 13) {
//				if (mouseX >= x && mouseX <= x + width && mouseY >= y + posY + cutUp
//						&& mouseY <= y + posY + 13 - cutDown) {
				Util.drawTexture(ICONS, x + 43 - stretch, y + posY + cutUp, 0, 187 + cutUp * 4,
						stretch >= 43 ? 225 : stretch * 4 + 52, 50 - cutUp * 4 - cutDown * 4, 0.25F);
				if (cutUp <= 3 && cutDown <= 3 && stretch > 7) {
					String text = Util.formatString(e.title);
					text = Util.formatStringToWidth(fr, text, stretch - 7);
					fr.drawString(text, x + 63 - stretch, y + 3 + posY, 0xFF1ECCDE);
				}
//				} else {
//					Util.drawTexture(ICONS, x + 43, y + posY + cutUp, 0, 187 + cutUp * 4, 52,
//							50 - cutUp * 4 - cutDown * 4, 0.25F);
//				}
			}
			if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
				stretch = stretch + 3 >= 43 ? 43 : stretch + 3;
			} else {
				stretch = 0;
			}
			index++;
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
				e.event.run();
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
		scrollingDistance += isUp ? 15 : -15;
		scrollingDistance = scrollingDistance < 0 ? 0 : scrollingDistance;
		scrollingDistance = scrollingDistance > maxScrollingDistance ? maxScrollingDistance : scrollingDistance;
		return true;
	}

	@Override
	public boolean onDrag(int mouseX, int mouseY, int moveX, int moveY) {
		super.onDrag(mouseX, mouseY, moveX, moveY);
		int maxScrollingDistance = list.size() * 15 - height;
		scrollingDistance -= moveY;
		scrollingDistance = scrollingDistance < 0 ? 0 : scrollingDistance;
		scrollingDistance = scrollingDistance > maxScrollingDistance ? maxScrollingDistance : scrollingDistance;
		return true;
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
