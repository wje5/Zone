package com.pinball3d.zone.sphinx.subscreen;

import java.util.Set;

import com.pinball3d.zone.gui.IHasSubscreen;
import com.pinball3d.zone.gui.Subscreen;
import com.pinball3d.zone.gui.component.TextButton;
import com.pinball3d.zone.gui.component.TexturedButton;
import com.pinball3d.zone.network.ConnectHelperClient;
import com.pinball3d.zone.network.ConnectionHelper.Type;
import com.pinball3d.zone.sphinx.map.MapHandler;
import com.pinball3d.zone.util.Util;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.BlockPos;

public class SubscreenNetworkInfo extends Subscreen {

	public SubscreenNetworkInfo(IHasSubscreen parent) {
		this(parent, getDisplayWidth() / 2 - 150, getDisplayHeight() / 2 - 100);
	}

	public SubscreenNetworkInfo(IHasSubscreen parent, int x, int y) {
		super(parent, x, y, 300, 200, true);
		addComponent(new TexturedButton(this, 27, 100, ICONS_4, 60, 120, 60, 60, 0.25F, () -> {
			if (ConnectHelperClient.getInstance().hasData()) {
				BlockPos pos = ConnectHelperClient.getInstance().getNetworkPos().getPos();
				MapHandler.focus(pos.getX(), pos.getZ());
				while (!parent.getSubscreens().empty()) {
					parent.removeScreen(parent.getSubscreens().peek());
				}
			}
		}));
		addComponent(new TextButton(this, 235, 175, I18n.format("sphinx.confirm"), () -> {
			parent.removeScreen(SubscreenNetworkInfo.this);
		}));
	}

	@Override
	public Set<Type> getDataTypes() {
		Set<Type> s = super.getDataTypes();
		s.add(Type.NAME);
		s.add(Type.WORKINGSTATE);
		s.add(Type.ENERGY);
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
		Util.renderGlowString(I18n.format("sphinx.network_info"), 15, 8);
		if (ConnectHelperClient.getInstance().hasData()) {
			Util.renderGlowString(I18n.format("sphinx.sphinx_name") + ":", 27, 35);
			Util.renderGlowString(ConnectHelperClient.getInstance().getName(), 180, 35);
			Util.renderGlowString(I18n.format("sphinx.working_state") + ":", 27, 55);
			Util.renderGlowString(ConnectHelperClient.getInstance().getWorkingState().name(), 180, 55);
			Util.renderGlowString(I18n.format("sphinx.energy") + ":", 27, 65);
			String text = "FULL";
			int energy = ConnectHelperClient.getInstance().getEnergy();
			if (energy < 512) {
				text = ((int) (energy * 100.0F / 576)) + "%";
			}
			Util.renderGlowString(text, 180, 65);
			Util.renderGlowString(I18n.format("sphinx.location") + ":", 27, 85);
			Util.renderGlowString(ConnectHelperClient.getInstance().getNetworkPos().toString(), 180, 85);
		}
		Util.renderGlowBorder(15, 23, 270, 172);
	}

	@Override
	public boolean isBlockOtherSubscreen() {
		return true;
	}
}
