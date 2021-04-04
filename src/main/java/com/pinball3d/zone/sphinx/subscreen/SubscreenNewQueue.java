package com.pinball3d.zone.sphinx.subscreen;

import java.util.Set;

import com.pinball3d.zone.gui.IHasSubscreen;
import com.pinball3d.zone.gui.Subscreen;
import com.pinball3d.zone.gui.component.TextButton;
import com.pinball3d.zone.network.ConnectionHelper.Type;
import com.pinball3d.zone.sphinx.component.QueueChart;
import com.pinball3d.zone.util.ItemType;
import com.pinball3d.zone.util.Util;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;

public class SubscreenNewQueue extends Subscreen {
	private ItemType target;

	public SubscreenNewQueue(IHasSubscreen parent, ItemType target) {
		this(parent, getDisplayWidth() / 2 - 150, getDisplayHeight() / 2 - 100, target);
	}

	public SubscreenNewQueue(IHasSubscreen parent, int x, int y, ItemType target) {
		super(parent, x, y, 300, 200, true);
		addComponent(new TextButton(this, 190, 175, I18n.format("sphinx.confirm"), () -> {
			parent.removeScreen(SubscreenNewQueue.this);
		}));
		addComponent(new TextButton(this, 235, 175, I18n.format("sphinx.cancel"), () -> {
			parent.removeScreen(SubscreenNewQueue.this);
		}));
		addComponent(new QueueChart(this, 21, 29, 183, 108));
		this.target = target;
	}

	@Override
	public Set<Type> getDataTypes() {
		Set<Type> s = super.getDataTypes();
		s.add(Type.NAME);
		return s;
	}

	@Override
	public void doRenderBackground(int mouseX, int mouseY) {
		super.doRenderBackground(mouseX, mouseY);
		Util.drawTexture(UI_BORDER, -5, -5, 0, 0, 99, 99, 0.5F);
		Util.drawTexture(UI_BORDER, 255, -5, 99, 0, 99, 99, 0.5F);
		Util.drawTexture(UI_BORDER, -5, 155, 0, 99, 99, 99, 0.5F);
		Util.drawTexture(UI_BORDER, 255, 155, 99, 99, 99, 99, 0.5F);
		Gui.drawRect(44, 0, 255, 44, 0x2F000000);
		Gui.drawRect(0, 44, 300, 155, 0x2F000000);
		Gui.drawRect(44, 155, 255, 200, 0x2F000000);
		Util.renderGlowHorizonLine(10, 20, 280);
		Gui.drawRect(16, 24, 284, 194, 0x651CC3B5);
		Util.renderGlowBorder(15, 23, 270, 172);
		Util.renderGlowString(I18n.format("sphinx.new_queue"), 15, 8);
		Util.drawBorder(20, 28, 185, 110, 1, 0xFF1ECCDE);
		Gui.drawRect(16, 24, 284, 28, 0x30000000);
		Gui.drawRect(16, 28, 20, 194, 0x30000000);
		Gui.drawRect(205, 28, 284, 194, 0x30000000);
		Gui.drawRect(20, 138, 205, 194, 0x30000000);
//		Util.renderGlowString(I18n.format("sphinx.total_product"), 210, 28);
		Util.renderGlowString("Aluminium Ingot * 32", 210, 28);
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 3; j++) {
				Util.drawBorder(207 + i * 19, 39 + j * 19, 18, 18, 1, 0xFF1ECCDE);
			}
		}
	}

	@Override
	public boolean isBlockOtherSubscreen() {
		return true;
	}
}
