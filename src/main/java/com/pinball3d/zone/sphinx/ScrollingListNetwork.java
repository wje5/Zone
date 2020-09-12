package com.pinball3d.zone.sphinx;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.pinball3d.zone.network.MessageRequestValidNetworks;
import com.pinball3d.zone.network.NetworkHandler;
import com.pinball3d.zone.tileentity.INeedNetwork;
import com.pinball3d.zone.tileentity.TEProcessingCenter;

import net.minecraft.client.gui.Gui;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ScrollingListNetwork extends Component {
	protected int length, lineHeight, scrollingDistance;
	protected List<ListBar> list = new ArrayList<ListBar>();
	protected List<WorldPos> data = new ArrayList<WorldPos>();

	public ScrollingListNetwork(IParent parent, int x, int y, int width, int height) {
		this(parent, x, y, width, height, null);
	}

	public ScrollingListNetwork(IParent parent, int x, int y, int width, int height, WorldPos pos) {
		super(parent, x, y, width, height);
		this.parent = parent;
		this.lineHeight = 25;
		NetworkHandler.instance.sendToServer(new MessageRequestValidNetworks(mc.player,
				pos == null
						? new WorldPos((int) mc.player.posX, (int) mc.player.posY, (int) mc.player.posZ,
								mc.player.world)
						: pos));
	}

	public void setData(List<WorldPos> data) {
		this.data = data;
		refresh();
	}

	public void sort() {
		WorldPos worldpos;
		if (((SubscreenNetworkConfig) parent).parent instanceof ScreenTerminal) {
			worldpos = ((ScreenTerminal) ((SubscreenNetworkConfig) parent).parent).worldpos;
		} else {
			INeedNetwork te = ((ScreenNeedNetwork) ((SubscreenNetworkConfig) parent).parent).tileentity;
			worldpos = te.isConnected() ? te.getNetworkPos() : null;
		}
		List<WorldPos> temp = new ArrayList<WorldPos>();
		data.forEach(e -> {
			if (worldpos != null && e.getWorld().provider.getDimension() == worldpos.getDim()
					&& e.getPos().equals(worldpos.getPos())) {
				temp.add(e);
			}
		});
		data.forEach(e -> {
			if (!(worldpos != null && e.getWorld().provider.getDimension() == worldpos.getDim()
					&& e.getPos().equals(worldpos.getPos()))) {
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
		ItemStack stack = parent.getTerminal();
		WorldPos worldpos;
		if (((SubscreenNetworkConfig) parent).parent instanceof ScreenTerminal) {
			worldpos = ((ScreenTerminal) ((SubscreenNetworkConfig) parent).parent).worldpos;
		} else {
			INeedNetwork te = ((ScreenNeedNetwork) ((SubscreenNetworkConfig) parent).parent).tileentity;
			worldpos = te.isConnected() ? te.getNetworkPos() : null;
		}
		data.forEach(e -> {
			list.add(new ListBar((TEProcessingCenter) e.getTileEntity(),
					worldpos != null && e.getWorld().provider.getDimension() == worldpos.getDim()
							&& e.getPos().equals(worldpos.getPos()),
					width, lineHeight));
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
	public void onDrag(int mouseX, int mouseY, int moveX, int moveY) {
		super.onDrag(mouseX, mouseY, moveX, moveY);
		scrollingDistance -= moveY;
		scrollingDistance = scrollingDistance > length - height ? length - height : scrollingDistance;
		scrollingDistance = scrollingDistance < 0 ? 0 : scrollingDistance;
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
				if (bar.selected) {
					parent.putScreen(new SubscreenCheckConnectedNetwork(parent, bar.tileentity,
							x + this.x - parent.getXOffset(), y + this.y - parent.getYOffset()));
				} else {
					parent.putScreen(new SubscreenConnectToNetwork(parent, bar.tileentity,
							x + this.x - parent.getXOffset(), y + this.y - parent.getYOffset()));
				}

				return;
			}
		}
	}

	public class ListBar {
		protected TEProcessingCenter tileentity;
		protected boolean selected;
		protected int width;
		protected int height;

		public ListBar(TEProcessingCenter tileentity, boolean selected, int width, int height) {
			this.tileentity = tileentity;
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
				parent.getFontRenderer().drawString(tileentity.getName(), x + 30, y + 3, 0xFF1ECCDE);
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
