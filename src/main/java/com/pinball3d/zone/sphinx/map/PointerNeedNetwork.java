package com.pinball3d.zone.sphinx.map;

import java.util.List;

import com.pinball3d.zone.gui.IHasComponents;
import com.pinball3d.zone.gui.component.Component;
import com.pinball3d.zone.sphinx.INeedNetwork.WorkingState;
import com.pinball3d.zone.sphinx.SerialNumber;
import com.pinball3d.zone.sphinx.component.ButtonUnitInfo;
import com.pinball3d.zone.util.Util;
import com.pinball3d.zone.util.WorldPos;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public abstract class PointerNeedNetwork extends Pointer {
	public WorldPos pos;
	public SerialNumber serial;
	public int id;
	public WorkingState state;

	public PointerNeedNetwork(WorldPos pos, SerialNumber serial, int id, WorkingState state, BoundingBox box) {
		super(box);
		this.pos = pos;
		this.serial = serial;
		this.id = id;
		this.state = state;
	}

	@Override
	public List<Component> getUnitButtons(IHasComponents parent) {
		List<Component> l = super.getUnitButtons(parent);
		l.add(new ButtonUnitInfo(parent, this, 0, 0));
		return l;
	}

	@Override
	public void doRender(int offsetX, int offsetZ) {
		render(offsetX, offsetZ);
		renderCover(offsetX, offsetZ);
	}

	public abstract void render(int offsetX, int offsetZ);

	public void renderCover(int offsetX, int offsetZ) {
		if (state != WorkingState.WORKING) {
			int width = box.x2 - box.x;
			int height = box.y2 - box.y;
			Util.drawTexture(SPHINX_ICONS, box.x - offsetX + width - 4, box.y - offsetZ + height - 4, 116, 21, 9, 9, 0.5F);
		}
	}

	@Override
	public void renderThumb(int x, int z) {
		super.renderThumb(x, z);
		Util.renderItem(new ItemStack(Item.getItemById(id)), x, z, 0.6875F);
		if (state == WorkingState.DISCONNECTED) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(0, 0, 400);
			Util.drawTexture(SPHINX_ICONS, x + 6, z + 6, 116, 21, 9, 9, 0.5F);
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
			Util.drawTexture(SPHINX_ICONS, x + 14, z + 14, 116, 21, 9, 9, 1.0F);
			GlStateManager.popMatrix();
		}
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof PointerNeedNetwork && ((PointerNeedNetwork) obj).pos.equals(pos);
	}

	@Override
	public int hashCode() {
		return pos.hashCode() + id * 31;
	}
}
