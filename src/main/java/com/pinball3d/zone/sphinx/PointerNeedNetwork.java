package com.pinball3d.zone.sphinx;

import java.util.List;

import com.pinball3d.zone.tileentity.INeedNetwork.WorkingState;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public abstract class PointerNeedNetwork extends Pointer {
	public static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/gui/sphinx/icons.png");
	public WorldPos pos;
	public int id;
	public int width, height;
	public WorkingState state;

	public PointerNeedNetwork(WorldPos pos, int id, WorkingState state, int width, int height) {
		super(pos.getPos().getX(), pos.getPos().getZ());
		this.pos = pos;
		this.width = width;
		this.height = height;
		this.id = id;
		this.state = state;
	}

	@Override
	public List<Component> getUnitButtons(IParent parent) {
		List<Component> l = super.getUnitButtons(parent);
		l.add(new ButtonUnitInfo(parent, this, 0, 0));
		return l;
	}

	@Override
	public abstract void doRender(int offsetX, int offsetZ);

	@Override
	public void renderThumb(int x, int z) {
		super.renderThumb(x, z);
		Util.renderItem(new ItemStack(Item.getItemById(id)), x, z, 0.6875F);
		if (state == WorkingState.DISCONNECTED) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(0, 0, 400);
			Util.drawTexture(TEXTURE, x + 6, z + 6, 116, 21, 9, 9, 0.5F);
			GlStateManager.popMatrix();
		}
	}

	@Override
	public void renderThumbHuge(int x, int z) {
		super.renderThumb(x, z);
		Util.renderItem(new ItemStack(Item.getItemById(id)), x, z, 1.625F);
		if (state == WorkingState.DISCONNECTED) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(0, 0, 400);
			Util.drawTexture(TEXTURE, x + 14, z + 14, 116, 21, 9, 9, 1.0F);
			GlStateManager.popMatrix();
		}
	}

	public abstract boolean isClick(int x, int z);

	@Override
	public boolean equals(Object obj) {
		return obj instanceof PointerNeedNetwork && ((PointerNeedNetwork) obj).pos.equals(pos);
	}

	@Override
	public int hashCode() {
		return pos.hashCode() + id * 31;
	}
}
