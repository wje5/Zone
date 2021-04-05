package com.pinball3d.zone.gui.component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.pinball3d.zone.gui.IHasComponents;
import com.pinball3d.zone.util.Util;

import net.minecraft.client.gui.Gui;

public class ScrollingList extends Component {
	protected int length, lineHeight, scrollingDistance;
	protected List<ListBar> list = new ArrayList<ListBar>();

	public ScrollingList(IHasComponents parent, int x, int y, int width, int height) {
		super(parent, x, y, width, height);
		this.parent = parent;
		this.lineHeight = 25;
	}

	public void addListBar(String name, Runnable onClick, int u, int v, int uWidth, int vHeight, float scale) {
		list.add(new ListBar(width, lineHeight, name, onClick, u, v, uWidth, vHeight, scale));
		length += lineHeight;
	}

	@Override
	public void doRender(int mouseX, int mouseY) {
		super.doRender(mouseX, mouseY);
		Iterator<ListBar> it = list.iterator();
		int yOffset = 0;
		while (it.hasNext()) {
			ListBar bar = it.next();
			int renderY = yOffset - scrollingDistance;
			if (renderY <= height && renderY + bar.height >= 0) {
				int renderUpcut = renderY < 0 ? -renderY : 0;
				int renderDownCut = renderY + bar.height - height > 0 ? renderY + bar.height - height : 0;
				boolean flag = mouseX >= 0 && mouseX <= 0 + width && mouseY > renderY && mouseY <= renderY + bar.height;
				bar.doRender(0, renderY, renderUpcut, renderDownCut, flag);
			}
			yOffset += bar.height;
		}
	}

	@Override
	public boolean onDrag(int mouseX, int mouseY, int moveX, int moveY) {
		if (super.onDrag(mouseX, mouseY, moveX, moveY)) {
			return true;
		}
		if (enable != null && !enable.getAsBoolean()) {
			return false;
		}
		scrollingDistance -= moveY;
		scrollingDistance = scrollingDistance > length - height ? length - height : scrollingDistance;
		scrollingDistance = scrollingDistance < 0 ? 0 : scrollingDistance;
		return true;
	}

	@Override
	public boolean onLeftClick(int x, int y) {
		if (super.onLeftClick(x, y)) {
			return true;
		}
		if (enable != null && !enable.getAsBoolean()) {
			return false;
		}
		Iterator<ListBar> it = list.iterator();
		int yOffset = 0;
		while (it.hasNext()) {
			ListBar bar = it.next();
			if (y + scrollingDistance >= yOffset && y + scrollingDistance < yOffset + bar.height) {
				bar.event.run();
				return true;
			}
			yOffset += bar.height;
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
		scrollingDistance += isUp ? 15 : -15;
		scrollingDistance = scrollingDistance > length - height ? length - height : scrollingDistance;
		scrollingDistance = scrollingDistance < 0 ? 0 : scrollingDistance;
		return true;
	}

	public class ListBar {
		protected String name;
		protected int width;
		protected int height;
		protected Runnable event;
		protected int u, v, uWidth, vHeight;
		protected float scale;

		public ListBar(int width, int height, String name, Runnable onClick, int u, int v, int uWidth, int vHeight,
				float scale) {
			this.width = width;
			this.height = height;
			this.name = name;
			event = onClick;
			this.u = u;
			this.v = v;
			this.uWidth = uWidth;
			this.vHeight = vHeight;
			this.scale = scale;
		}

		public void doRender(int x, int y, int upCut, int downCut, boolean flag) {
			if (flag) {
				int a = y + upCut;
				int b = y + height - downCut;
				if (a < b) {
					Gui.drawRect(x, a, x + width, b, 0x4FFFFFFF);
				}
			}
			if (upCut < 10 && downCut < 10) {
				Util.renderGlowString(name, x + 30, y + 9);
			}
			int d = (int) ((height - (vHeight * scale)) / 2);
			y += d;
			upCut = upCut - d > 0 ? upCut - d : 0;
			downCut = downCut - d > 0 ? downCut - d : 0;
			Util.drawTexture(ICONS, x + 7, y + upCut, u, v + upCut * 2, uWidth, vHeight - (upCut + downCut) * 2, scale);

		}
	}
}
