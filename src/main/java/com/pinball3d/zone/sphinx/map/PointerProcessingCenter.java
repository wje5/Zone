package com.pinball3d.zone.sphinx.map;

import com.pinball3d.zone.item.ItemLoader;
import com.pinball3d.zone.tileentity.INeedNetwork.WorkingState;
import com.pinball3d.zone.util.Util;
import com.pinball3d.zone.util.WorldPos;

import net.minecraft.item.Item;

public class PointerProcessingCenter extends PointerNeedNetwork {
	public PointerProcessingCenter(WorldPos pos) {
		super(pos, Item.getIdFromItem(ItemLoader.processing_center_light), WorkingState.WORKING, new BoundingBox(
				pos.getPos().getX() - 5, pos.getPos().getZ() - 5, pos.getPos().getX() + 5, pos.getPos().getZ() + 6));
	}

	@Override
	public void render(int offsetX, int offsetZ) {
		Util.drawTexture(TEXTURE, pos.getPos().getX() - offsetX - 5, pos.getPos().getZ() - offsetZ - 5, 116, 0, 20, 22,
				0.5F);
	}
}
