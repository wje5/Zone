package com.pinball3d.zone.sphinx.subscreen;

import java.util.Set;

import com.pinball3d.zone.gui.Subscreen;
import com.pinball3d.zone.network.ConnectHelperClient;
import com.pinball3d.zone.network.ConnectionHelper.Type;
import com.pinball3d.zone.sphinx.IHasSubscreen;
import com.pinball3d.zone.sphinx.component.TextButton;
import com.pinball3d.zone.sphinx.component.TexturedButton;
import com.pinball3d.zone.sphinx.map.MapHandler;
import com.pinball3d.zone.util.Util;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class SubscreenNetworkInfo extends Subscreen {
	private static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/gui/sphinx/ui_border.png");
	private static final ResourceLocation TEXTURE_4 = new ResourceLocation("zone:textures/gui/sphinx/icons_4.png");

	public SubscreenNetworkInfo(IHasSubscreen parent) {
		this(parent, getDisplayWidth() / 2 - 150, getDisplayHeight() / 2 - 100);
	}

	public SubscreenNetworkInfo(IHasSubscreen parent, int x, int y) {
		super(parent, x, y, 300, 200, true);
		addComponent(new TexturedButton(this, x + 27, y + 100, TEXTURE_4, 60, 120, 60, 60, 0.25F, () -> {
			if (ConnectHelperClient.getInstance().hasData()) {
				BlockPos pos = ConnectHelperClient.getInstance().getNetworkPos().getPos();
				MapHandler.focus(pos.getX(), pos.getZ());
				while (!parent.getSubscreens().empty()) {
					parent.removeScreen(parent.getSubscreens().peek());
				}
			}
		}));
		addComponent(new TextButton(this, this.x + 235, this.y + 175, I18n.format("sphinx.confirm"), () -> {
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
		Util.drawTexture(TEXTURE, x - 5, y - 5, 0, 0, 99, 99, 0.5F);
		Util.drawTexture(TEXTURE, x + 255, y - 5, 99, 0, 99, 99, 0.5F);
		Util.drawTexture(TEXTURE, x - 5, y + 155, 0, 99, 99, 99, 0.5F);
		Util.drawTexture(TEXTURE, x + 255, y + 155, 99, 99, 99, 99, 0.5F);
		Gui.drawRect(x + 44, y, x + 255, y + 44, 0x2F000000);
		Gui.drawRect(x, y + 44, x + 300, y + 155, 0x2F000000);
		Gui.drawRect(x + 44, y + 155, x + 255, y + 200, 0x2F000000);
		Util.renderGlowHorizonLine(x + 10, y + 20, 280);
		Gui.drawRect(x + 16, y + 24, x + 284, y + 194, 0x651CC3B5);
		Util.renderGlowString(I18n.format("sphinx.network_info"), x + 15, y + 8);
		if (ConnectHelperClient.getInstance().hasData()) {
			Util.renderGlowString(I18n.format("sphinx.sphinx_name") + ":", x + 27, y + 35);
			Util.renderGlowString(ConnectHelperClient.getInstance().getName(), x + 180, y + 35);
			Util.renderGlowString(I18n.format("sphinx.working_state") + ":", x + 27, y + 55);
			Util.renderGlowString(ConnectHelperClient.getInstance().getWorkingState().name(), x + 180, y + 55);
			Util.renderGlowString(I18n.format("sphinx.energy") + ":", x + 27, y + 65);
			String text = "FULL";
			int energy = ConnectHelperClient.getInstance().getEnergy();
			if (energy < 512) {
				text = ((int) (energy * 100.0F / 576)) + "%";
			}
			Util.renderGlowString(text, x + 180, y + 65);
			Util.renderGlowString(I18n.format("sphinx.location") + ":", x + 27, y + 85);
			Util.renderGlowString(ConnectHelperClient.getInstance().getNetworkPos().toString(), x + 180, y + 85);
		}
		Util.renderGlowBorder(x + 15, y + 23, 270, 172);
	}

	@Override
	public boolean isBlockOtherSubscreen() {
		return true;
	}
}
