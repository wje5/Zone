package com.pinball3d.zone.sphinx;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

public class ScrollingListNetwork extends Component {
	protected int length, lineHeight, scrollingDistance;
	protected List<ListBar> list = new ArrayList<ListBar>();

	public ScrollingListNetwork(IParent parent, int x, int y, int width, int height) {
		super(parent, x, y, width, height);
		this.parent = parent;
		this.lineHeight = 25;
		String[] s = new String[] { "Phantomhive", "1234567890", "１２３４５６７８９０", "上大人孔乙己化三千可知礼也", "そ～ですか？", "バカ野郎！",
				"Phantomhive", "Phantomhive", "Phantomhive", "Phantomhive", "Phantomhive", "", "", "", "", "" };
		for (int i = 0; i < s.length; i++) {
			list.add(new ListBar(s[i], width, lineHeight));
			length += lineHeight;
		}
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
	public void onDrag(int moveX, int moveY) {
		super.onDrag(moveX, moveY);
		scrollingDistance -= moveY;
		scrollingDistance = scrollingDistance < 0 ? 0 : scrollingDistance;
		scrollingDistance = scrollingDistance > length - height ? length - height : scrollingDistance;
	}

	@Override
	public void onLeftClick(int x, int y) {
		super.onLeftClick(x, y);
		Iterator<ListBar> it = list.iterator();
		int yOffset = 0;
		while (it.hasNext()) {
			ListBar bar = it.next();
			yOffset += bar.height;
			if (yOffset >= y + scrollingDistance && yOffset < y + scrollingDistance + bar.height) {
				parent.putScreen(new SubscreenConnectToNetwork((SubscreenNetworkConfig) parent, bar.name,
						x + this.x - parent.getXOffset(), y + this.y - parent.getYOffset()));
				return;
			}
		}

	}

	public class ListBar {
		protected String name;
		protected int width;
		protected int height;

		public ListBar(String name, int width, int height) {
			this.name = name;
			this.width = width;
			this.height = height;
		}

		public void doRender(int x, int y, int upCut, int downCut, boolean flag) {
			if (flag) {
				int a = y + upCut;
				int b = y + height - downCut;
				if (a < b) {
					Gui.drawRect(x, a, x + width, b, 0x4FFFFFFF);
				}
			}
			y += 6;
			upCut = upCut - 6 > 0 ? upCut - 6 : 0;
			downCut = downCut - 6 > 0 ? downCut - 6 : 0;
			Util.drawTexture(new ResourceLocation("zone:textures/gui/sphinx/icons.png"), x + 7, y + upCut, 0,
					16 + upCut * 2, 32, 26 - (upCut + downCut) * 2, 0.5F);
			if (upCut < 4 && downCut < 4) {
				parent.getFontRenderer().drawString(name, x + 30, y + 3, 0xFF1ECCDE);
			}
		}
	}
}
