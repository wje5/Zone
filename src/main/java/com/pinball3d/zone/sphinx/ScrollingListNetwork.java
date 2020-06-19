package com.pinball3d.zone.sphinx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.pinball3d.zone.block.BlockLoader;
import com.pinball3d.zone.block.BlockProcessingCenter;
import com.pinball3d.zone.network.MessageTerminalRequestValidNetworks;
import com.pinball3d.zone.network.NetworkHandler;
import com.pinball3d.zone.tileentity.TEProcessingCenter;

import net.minecraft.block.Block;
import net.minecraft.client.gui.Gui;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class ScrollingListNetwork extends Component {
	protected int length, lineHeight, scrollingDistance;
	protected List<ListBar> list = new ArrayList<ListBar>();

	public ScrollingListNetwork(IParent parent, int x, int y, int width, int height) {
		super(parent, x, y, width, height);
		this.parent = parent;
		this.lineHeight = 25;
		NetworkHandler.instance.sendToServer(new MessageTerminalRequestValidNetworks(mc.player));
	}

	public void setData(List<WorldPos> data) {
		list = new ArrayList<ListBar>();
		ItemStack stack = parent.getTerminal();
		WorldPos worldpos;
		if (stack != ItemStack.EMPTY) {
			NBTTagCompound tag = stack.getTagCompound();
			if (tag == null) {
				tag = new NBTTagCompound();
				stack.setTagCompound(tag);
			}
			worldpos = WorldPos.load(tag);
		} else {
			worldpos = parent.getNeedNetworkTileEntity().getNetworkPos();
		}
		System.out.println(worldpos);
		data.forEach(e -> {
			list.add(new ListBar((TEProcessingCenter) e.getTileEntity(),
					worldpos != null && e.getWorld().provider.getDimension() == worldpos.getDim()
							&& e.getPos().equals(worldpos.getPos()),
					width, lineHeight));
			length += lineHeight;
		});
	}

	public void refresh() {
		list = new ArrayList<ListBar>();
		length = 0;
		scrollingDistance = 0;
		List<TEProcessingCenter> l = new ArrayList<TEProcessingCenter>();
		BlockPos pos = mc.player.getPosition();

		for (int i = pos.getX() - 12; i <= pos.getX() + 12; i++) {
			for (int j = pos.getY() - 12; j <= pos.getY() + 12; j++) {
				for (int k = pos.getZ() - 12; k <= pos.getZ() + 12; k++) {
					BlockPos blockpos = new BlockPos(i, j, k);
					Block block = mc.player.world.getBlockState(blockpos).getBlock();
					if (block == BlockLoader.processing_center_light) {
						BlockProcessingCenter center = (BlockProcessingCenter) block;
						if (center.isFullStructure(mc.world, blockpos)) {
							TEProcessingCenter te = (TEProcessingCenter) mc.world.getTileEntity(blockpos);
							if (!te.needInit() && !te.isLoading()) {
								l.add(te);
							}
						}
					}
				}
			}
		}
		ItemStack stack = parent.getTerminal();
		WorldPos worldpos;
		if (stack != ItemStack.EMPTY) {
			NBTTagCompound tag = stack.getTagCompound();
			if (tag == null) {
				tag = new NBTTagCompound();
				stack.setTagCompound(tag);
			}
			worldpos = WorldPos.load(tag);
		} else {
			worldpos = parent.getNeedNetworkTileEntity().getNetworkPos();
		}
		if (worldpos != null) {
			Collections.sort(l, new Comparator<TEProcessingCenter>() {
				@Override
				public int compare(TEProcessingCenter o1, TEProcessingCenter o2) {
					if (o1.getWorld().provider.getDimension() == worldpos.getDim()
							&& o1.getPos().equals(worldpos.getPos())) {
						return -1;
					}
					if (o2.getWorld().provider.getDimension() == worldpos.getDim()
							&& o2.getPos().equals(worldpos.getPos())) {
						return 1;
					}
					return mc.player.getDistanceSqToCenter(o1.getPos()) > mc.player.getDistanceSqToCenter(o2.getPos())
							? 1
							: -1;
				}
			});
		}
		l.forEach(e -> {
			list.add(new ListBar(e, worldpos != null && e.getWorld().provider.getDimension() == worldpos.getDim()
					&& e.getPos().equals(worldpos.getPos()), width, lineHeight));
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
	public void onDrag(int moveX, int moveY) {
		super.onDrag(moveX, moveY);
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
				parent.putScreen(new SubscreenConnectToNetwork(parent, bar.tileentity, x + this.x - parent.getXOffset(),
						y + this.y - parent.getYOffset()));
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
