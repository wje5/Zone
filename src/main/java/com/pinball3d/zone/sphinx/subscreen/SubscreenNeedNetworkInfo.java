package com.pinball3d.zone.sphinx.subscreen;

import com.pinball3d.zone.gui.Subscreen;
import com.pinball3d.zone.network.ConnectHelperClient;
import com.pinball3d.zone.network.MessageRequestNeedNetworkInfo;
import com.pinball3d.zone.network.NetworkHandler;
import com.pinball3d.zone.sphinx.IHasSubscreen;
import com.pinball3d.zone.sphinx.SerialNumber;
import com.pinball3d.zone.sphinx.component.TextButton;
import com.pinball3d.zone.tileentity.TEProcessingCenter.WorkingState;
import com.pinball3d.zone.util.Util;
import com.pinball3d.zone.util.WorldPos;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class SubscreenNeedNetworkInfo extends Subscreen {
	private static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/gui/sphinx/ui_border.png");
	private WorldPos pos;
	private String name;
	private WorkingState state;
	private SerialNumber serial;

	public SubscreenNeedNetworkInfo(IHasSubscreen parent, SerialNumber serial) {
		this(parent, serial, getDisplayWidth() / 2 - 150, getDisplayHeight() / 2 - 100);
	}

	public SubscreenNeedNetworkInfo(IHasSubscreen parent, SerialNumber serial, int x, int y) {
		super(parent, x, y, 300, 200, true);
		NetworkHandler.instance.sendToServer(
				MessageRequestNeedNetworkInfo.newMessage(ConnectHelperClient.getInstance().getNetworkPos(), serial));
		addComponent(new TextButton(this, this.x + 235, this.y + 175, I18n.format("sphinx.confirm"), () -> {
			parent.removeScreen(SubscreenNeedNetworkInfo.this);
		}));
		this.serial = serial;
	}

	public void setData(SerialNumber serial, NBTTagCompound tag) {
		if (this.serial.equals(serial)) {
			name = tag.getString("name");
			state = WorkingState.values()[tag.getInteger("state")];
			pos = new WorldPos(tag.getCompoundTag("pos"));
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
		Util.renderGlowString(I18n.format("sphinx.unit_info"), x + 15, y + 8);
		if (name != null) {
			Util.renderGlowString(I18n.format("sphinx.unit_name") + ":", x + 27, y + 35);
			Util.renderGlowString(name, x + 180, y + 35);
			Util.renderGlowString(I18n.format("sphinx.serial_number") + ":", x + 27, y + 45);
			Util.renderGlowString(serial.toString(), x + 180, y + 45);
			Util.renderGlowString(I18n.format("sphinx.working_state") + ":", x + 27, y + 65);
			Util.renderGlowString(state.toString(), x + 180, y + 65);
			Util.renderGlowString(I18n.format("sphinx.location") + ":", x + 27, y + 75);
			Util.renderGlowString(pos.toString(), x + 180, y + 75);
		}
		Util.renderGlowBorder(x + 15, y + 23, 270, 172);
	}
}
