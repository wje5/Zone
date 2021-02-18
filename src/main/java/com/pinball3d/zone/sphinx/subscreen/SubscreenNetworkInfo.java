package com.pinball3d.zone.sphinx.subscreen;

import com.pinball3d.zone.gui.Subscreen;
import com.pinball3d.zone.network.MessageRequestNetworkInfo;
import com.pinball3d.zone.network.NetworkHandler;
import com.pinball3d.zone.sphinx.IHasSubscreen;
import com.pinball3d.zone.sphinx.component.TextButton;
import com.pinball3d.zone.tileentity.TEProcessingCenter.WorkingState;
import com.pinball3d.zone.util.Util;
import com.pinball3d.zone.util.WorldPos;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class SubscreenNetworkInfo extends Subscreen {
	private static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/gui/sphinx/ui_border.png");
	private WorldPos pos;
	private String name;
	private WorkingState state;
	private int energy;

	public SubscreenNetworkInfo(IHasSubscreen parent, WorldPos pos) {
		this(parent, getDisplayWidth() / 2 - 150, getDisplayHeight() / 2 - 100, pos);
	}

	public SubscreenNetworkInfo(IHasSubscreen parent, int x, int y, WorldPos pos) {
		super(parent, x, y, 300, 200, true);
		this.pos = pos;
		NetworkHandler.instance.sendToServer(new MessageRequestNetworkInfo(mc.player, pos));
		addComponent(new TextButton(this, this.x + 235, this.y + 175, I18n.format("sphinx.confirm"), () -> {
			parent.removeScreen(SubscreenNetworkInfo.this);
		}));
	}

	public void setData(WorldPos pos, NBTTagCompound tag) {
		if (pos.equals(this.pos)) {
			name = tag.getString("name");
			state = WorkingState.values()[tag.getInteger("state")];
			energy = tag.getInteger("energy");
		}
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
		if (name != null) {
			Util.renderGlowString(I18n.format("sphinx.sphinx_name") + ":", x + 27, y + 35);
			Util.renderGlowString(name, x + 180, y + 35);
			Util.renderGlowString(I18n.format("sphinx.working_state") + ":", x + 27, y + 55);
			Util.renderGlowString(state.toString(), x + 180, y + 55);
			Util.renderGlowString(I18n.format("sphinx.energy") + ":", x + 27, y + 65);
			String text = "FULL";
			if (energy < 512) {
				text = ((int) (energy * 100.0F / 576)) + "%";
			}
			Util.renderGlowString(text, x + 180, y + 65);
			Util.renderGlowString(I18n.format("sphinx.location") + ":", x + 27, y + 85);
			Util.renderGlowString(pos.toString(), x + 180, y + 85);
		}
		Util.renderGlowBorder(x + 15, y + 23, 270, 172);
	}
}
