package com.pinball3d.zone.manual;

import com.pinball3d.zone.sphinx.Component;
import com.pinball3d.zone.sphinx.IParent;
import com.pinball3d.zone.sphinx.Util;

import net.minecraft.item.ItemStack;

public class BlockShow extends Component {
	private float scale;
	private ItemStack stack;

	public BlockShow(IParent parent, int x, int y, float scale, ItemStack stack) {
		super(parent, x, y, (int) (scale * 16), (int) (scale * 16));
		this.scale = scale;
		this.stack = stack;
	}

	@Override
	public void doRender(int mouseX, int mouseY) {
		Util.renderItem(stack, x, y, scale);
	}
}
