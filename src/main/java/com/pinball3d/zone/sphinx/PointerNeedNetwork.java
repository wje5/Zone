package com.pinball3d.zone.sphinx;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public abstract class PointerNeedNetwork extends Pointer {
	public static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/gui/sphinx/icons.png");
	public WorldPos pos;
	public int width, height;

	public PointerNeedNetwork(WorldPos pos, int width, int height) {
		super(pos.getPos().getX(), pos.getPos().getZ());
		this.pos = pos;
		this.width = width;
		this.height = height;
	}

	@Override
	public List<Component> getUnitButtons(IParent parent) {
		List<Component> l = super.getUnitButtons(parent);
		l.add(0, new ButtonUnitInfo(parent, this, 0, 0));
		return l;
	}

	@Override
	public abstract void doRender(int offsetX, int offsetZ);

	@Override
	public void renderThumb(int x, int z) {
		super.renderThumb(x, z);
		Util.renderItem(new ItemStack(pos.getTileEntity().getBlockType()), x, z, 0.6875F);
	}

	@Override
	public void renderThumbHuge(int x, int z) {
		super.renderThumb(x, z);
		Util.renderItem(new ItemStack(pos.getTileEntity().getBlockType()), x, z, 1.625F);
	}

	public abstract boolean isClick(int x, int z);
}
