package com.pinball3d.zone.sphinx;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.pinball3d.zone.network.MessageRequestValidNetworks;
import com.pinball3d.zone.network.NetworkHandler;

import net.minecraft.client.gui.Gui;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;

public class ScrollingListNetwork extends Component {
	protected int length, lineHeight, scrollingDistance;
	protected List<ListBar> list = new ArrayList<ListBar>();
	protected List<NBTTagCompound> data = new ArrayList<NBTTagCompound>();
	protected WorldPos pos, connected;

	public ScrollingListNetwork(IParent parent, int x, int y, int width, int height) {
		this(parent, x, y, width, height, null);
	}

	public ScrollingListNetwork(IParent parent, int x, int y, int width, int height, WorldPos pos) {
		super(parent, x, y, width, height);
		this.parent = parent;
		this.lineHeight = 25;
		this.pos = pos;
		boolean flag = pos == null;
		NetworkHandler.instance.sendToServer(new MessageRequestValidNetworks(mc.player,
				flag ? new WorldPos((int) mc.player.posX, (int) mc.player.posY, (int) mc.player.posZ, mc.player.world)
						: pos,
				flag));
	}

	public void setData(NBTTagCompound tag) {
		data.clear();
		connected = WorldPos.load(tag.getCompoundTag("connected"));
		NBTTagList list = tag.getTagList("list", 10);
		list.forEach(e -> {
			data.add((NBTTagCompound) e);
		});
		refresh();
	}

	public void sort() {
		List<NBTTagCompound> temp = new ArrayList<NBTTagCompound>();
		data.forEach(e -> {
			WorldPos p = WorldPos.load(e);
			if (p != null && p.equals(connected)) {
				temp.add(e);
			}
		});
		data.forEach(e -> {
			WorldPos p = WorldPos.load(e);
			if (!(p != null && p.equals(connected))) {
				temp.add(e);
			}
		});
		data = temp;
	}

	public void refresh() {
		sort();
		list = new ArrayList<ListBar>();
		length = 0;
		scrollingDistance = 0;
		data.forEach(e -> {
			WorldPos pos = WorldPos.load(e);
			String name = e.getString("name");
			list.add(new ListBar(pos, name, pos.equals(connected), width, lineHeight));
			length += lineHeight;
		});

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
				if (bar.selected) {
					parent.putScreen(new SubscreenCheckConnectedNetwork(parent, bar.pos, bar.name,
							x + this.x - parent.getXOffset(), y + this.y - parent.getYOffset()));
				} else {
					parent.putScreen(new SubscreenConnectToNetwork(parent, pos, bar.pos, bar.name,
							x + this.x - parent.getXOffset(), y + this.y - parent.getYOffset()));
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
				parent.getFontRenderer().drawString(name, x + 30, y + 3, 0xFF1ECCDE);
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
