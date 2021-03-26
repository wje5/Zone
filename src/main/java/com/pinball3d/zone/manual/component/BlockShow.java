package com.pinball3d.zone.manual.component;

import com.pinball3d.zone.gui.Component;
import com.pinball3d.zone.gui.IHasComponents;
import com.pinball3d.zone.util.Util;

import net.minecraft.item.ItemStack;

public class BlockShow extends Component {
	private float scale;
	private ItemStack stack;

	public BlockShow(IHasComponents parent, int x, int y, float scale, ItemStack stack) {
		super(parent, x, y, (int) (scale * 16), (int) (scale * 16));
		this.scale = scale;
		this.stack = stack;
	}

	@Override
	public void doRender(int mouseX, int mouseY) {
		Util.renderItem(stack, getX(), getY(), scale);
	}
}
