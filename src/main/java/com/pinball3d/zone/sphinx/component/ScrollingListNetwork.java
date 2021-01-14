package com.pinball3d.zone.sphinx.component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.pinball3d.zone.network.ConnectHelperClient;
import com.pinball3d.zone.network.MessageTryConnectToNetwork;
import com.pinball3d.zone.network.NetworkHandler;
import com.pinball3d.zone.sphinx.IHasComponents;
import com.pinball3d.zone.sphinx.IHasSubscreen;
import com.pinball3d.zone.sphinx.subscreen.SubscreenCheckConnectedNetwork;
import com.pinball3d.zone.sphinx.subscreen.SubscreenConnectToNetworkBox;
import com.pinball3d.zone.util.Util;
import com.pinball3d.zone.util.WorldPos;

import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

public class ScrollingListNetwork extends Component {
	protected int length, lineHeight, scrollingDistance;
	protected List<ListBar> list = new ArrayList<ListBar>();
	protected WorldPos pos;

	public ScrollingListNetwork(IHasComponents parent, int x, int y, int width, int height, WorldPos pos) {
		super(parent, x, y, width, height);
		this.parent = parent;
		this.lineHeight = 25;
		this.pos = pos;
	}

	public void refresh() {
		Set<WorldPos> s = new TreeSet<WorldPos>(new Comparator<WorldPos>() {
			@Override
			public int compare(WorldPos o1, WorldPos o2) {
				return o1.compare(o2);
			}
		});
		Map<WorldPos, String> m = pos.isOrigin() ? ConnectHelperClient.getInstance().getPlayerValidNetworks()
				: ConnectHelperClient.getInstance().getNeedNetworkValidNetworks();
		s.addAll(m.keySet());
		WorldPos connected = ConnectHelperClient.getInstance().getNetworkPos();
		List<WorldPos> l = new ArrayList<WorldPos>();
		s.forEach(e -> {
			if (!e.isOrigin() && e.equals(connected)) {
				l.add(e);
			}
		});
		s.forEach(e -> {
			if (!e.isOrigin() && !e.equals(connected)) {
				l.add(e);
			}
		});
		list = new ArrayList<ListBar>();
		length = 0;
		scrollingDistance = 0;
		l.forEach(e -> {
			String name = m.get(e);
			list.add(new ListBar(e, name, e.equals(connected), width, lineHeight));
			length += lineHeight;
		});

	}

	@Override
	public void doRender(int mouseX, int mouseY) {
		super.doRender(mouseX, mouseY);
		refresh();
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
				IHasSubscreen root = Util.getRoot();
				if (bar.selected) {
					root.putScreen(new SubscreenCheckConnectedNetwork(root, bar.pos, bar.name, x + this.x, y + this.y));
				} else {
					NetworkHandler.instance.sendToServer(new MessageTryConnectToNetwork(mc.player, pos.isOrigin(),
							pos.isOrigin() ? new WorldPos(mc.player.getPosition(), mc.world.provider.getDimension())
									: pos,
							bar.pos));
					root.putScreen(new SubscreenConnectToNetworkBox(root, bar.name, this.x + x, this.y + y));
				}
				return true;
			}
			yOffset += bar.height;
		}
		return false;
	}

	public class ListBar {
		protected WorldPos pos;
		protected String name;
		protected boolean selected;
		protected int width;
		protected int height;

		public ListBar(WorldPos pos, String name, boolean selected, int width, int height) {
			this.pos = pos;
			this.name = name;
			this.selected = selected;
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
				Util.getFontRenderer().drawString(name, x + 30, y + 3, 0xFF1ECCDE);
			}
			if (selected) {
				y += 2;
				upCut = upCut - 2 > 0 ? upCut - 2 : 0;
				downCut = downCut - 2 > 0 ? downCut - 2 : 0;
				Util.drawTexture(new ResourceLocation("zone:textures/gui/sphinx/icons.png"), x + 240, y + upCut, 0,
						100 + upCut * 2, 24, 18 - (upCut + downCut) * 2, 0.5F);
			}
		}
	}
}