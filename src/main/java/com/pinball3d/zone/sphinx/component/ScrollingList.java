package com.pinball3d.zone.sphinx.component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.pinball3d.zone.sphinx.IHasComponents;
import com.pinball3d.zone.util.Util;

import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

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
			int renderY = y + yOffset - scrollingDistance;
			if (renderY <= y + height && renderY + bar.height >= y) {
				int upCut = y - renderY > 0 ? y - renderY : 0;
				int downCut = renderY + bar.height - (y + height) > 0 ? renderY + bar.height - (y + height) : 0;
				boolean flag = mouseX >= x && mouseX <= x + width && mouseY > renderY && mouseY <= renderY + bar.height;
				bar.doRender(x, renderY, upCut, downCut, flag);
			}
			yOffset += bar.height;
		}
	}

	@Override
	public boolean onDrag(int mouseX, int mouseY, int moveX, int moveY) {
		super.onDrag(mouseX, mouseY, moveX, moveY);
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
			Util.drawTexture(new ResourceLocation("zone:textures/gui/sphinx/icons.png"), x + 7, y + upCut, u,
					v + upCut * 2, uWidth, vHeight - (upCut + downCut) * 2, scale);

		}
	}
}
