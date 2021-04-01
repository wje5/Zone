package com.pinball3d.zone.sphinx.subscreen;

import com.pinball3d.zone.gui.IHasSubscreen;
import com.pinball3d.zone.gui.Subscreen;
import com.pinball3d.zone.gui.component.TextButton;
import com.pinball3d.zone.gui.component.TexturedButton;
import com.pinball3d.zone.network.ConnectHelperClient;
import com.pinball3d.zone.network.MessageRequestNeedNetworkInfo;
import com.pinball3d.zone.network.NetworkHandler;
import com.pinball3d.zone.sphinx.INeedNetwork.WorkingState;
import com.pinball3d.zone.sphinx.SerialNumber;
import com.pinball3d.zone.sphinx.map.MapHandler;
import com.pinball3d.zone.util.StorageWrapper;
import com.pinball3d.zone.util.Util;
import com.pinball3d.zone.util.WorldPos;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;

public class SubscreenNeedNetworkInfo extends Subscreen {
	private WorldPos pos;
	private String name;
	private WorkingState state;
	private SerialNumber serial;
	private int usedStorage, maxStorage;
	private StorageWrapper storage;

	public SubscreenNeedNetworkInfo(IHasSubscreen parent, SerialNumber serial) {
		this(parent, serial, getDisplayWidth() / 2 - 150, getDisplayHeight() / 2 - 100);
	}

	public SubscreenNeedNetworkInfo(IHasSubscreen parent, SerialNumber serial, int x, int y) {
		super(parent, x, y, 300, 200, true);
		NetworkHandler.instance.sendToServer(
				MessageRequestNeedNetworkInfo.newMessage(ConnectHelperClient.getInstance().getNetworkPos(), serial));
		addComponent(new TexturedButton(this, 27, 90, ICONS_4, 60, 120, 60, 60, 0.25F, () -> {
			if (name != null) {
				MapHandler.focus(pos.getPos().getX(), pos.getPos().getZ());
				while (!parent.getSubscreens().empty()) {
					parent.removeScreen(parent.getSubscreens().peek());
				}
			}
		}).setYSupplier(() -> 90 + getYOffset()));
		addComponent(new TexturedButton(this, 44, 90, ICONS_5, 60, 0, 60, 60, 0.25F, () -> {
			parent.putScreen(new SubscreenViewItems(parent, storage));
		}).setYSupplier(() -> 90 + getYOffset()).setEnable(() -> usedStorage != 0 || maxStorage != 0));
		addComponent(new TextButton(this, 235, 175, I18n.format("sphinx.confirm"), () -> {
			parent.removeScreen(SubscreenNeedNetworkInfo.this);
		}));
		this.serial = serial;
	}

	public int getYOffset() {
		int yOffset = 0;
		if (usedStorage != 0 || maxStorage != 0) {
			yOffset += 20;
		}
		return yOffset;
	}

	public void setData(SerialNumber serial, NBTTagCompound tag) {
		if (this.serial.equals(serial)) {
			name = tag.getString("name");
			state = WorkingState.values()[tag.getInteger("state")];
			pos = new WorldPos(tag.getCompoundTag("pos"));
			usedStorage = tag.getInteger("usedStorage");
			maxStorage = tag.getInteger("maxStorage");
			if (tag.hasKey("storage")) {
				storage = new StorageWrapper((NBTTagCompound) tag.getTag("storage"));
			}
		}
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
		Util.renderGlowString(I18n.format("sphinx.unit_info"), 15, 8);
		if (name != null) {
			Util.renderGlowString(I18n.format("sphinx.unit_name") + ":", 27, 35);
			Util.renderGlowString(name, 180, 35);
			Util.renderGlowString(I18n.format("sphinx.serial_number") + ":", 27, 45);
			Util.renderGlowString(serial.toString(), 180, 45);
			Util.renderGlowString(I18n.format("sphinx.working_state") + ":", 27, 65);
			Util.renderGlowString(state.toString(), 180, 65);
			Util.renderGlowString(I18n.format("sphinx.location") + ":", 27, 75);
			Util.renderGlowString(pos.toString(), 180, 75);
			int yOffset = 0;
			if (usedStorage != 0 || maxStorage != 0) {
				yOffset += 20;
				Util.renderGlowString(I18n.format("sphinx.storage_space") + ":", 27, 75 + yOffset);
				Util.renderGlowString(usedStorage + "/" + maxStorage, 180, 75 + yOffset);
			}
		}
		Util.renderGlowBorder(15, 23, 270, 172);
	}

	@Override
	public boolean isBlockOtherSubscreen() {
		return true;
	}
}
