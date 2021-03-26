package com.pinball3d.zone.sphinx.subscreen;

import com.pinball3d.zone.gui.IHasSubscreen;
import com.pinball3d.zone.gui.Subscreen;
import com.pinball3d.zone.network.ConnectHelperClient;
import com.pinball3d.zone.network.MessageRequestNeedNetworkInfo;
import com.pinball3d.zone.network.NetworkHandler;
import com.pinball3d.zone.sphinx.INeedNetwork.WorkingState;
import com.pinball3d.zone.sphinx.SerialNumber;
import com.pinball3d.zone.sphinx.component.TextButton;
import com.pinball3d.zone.sphinx.component.TexturedButton;
import com.pinball3d.zone.sphinx.map.MapHandler;
import com.pinball3d.zone.util.StorageWrapper;
import com.pinball3d.zone.util.Util;
import com.pinball3d.zone.util.WorldPos;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class SubscreenNeedNetworkInfo extends Subscreen {
	private static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/gui/sphinx/ui_border.png");
	private static final ResourceLocation TEXTURE_4 = new ResourceLocation("zone:textures/gui/sphinx/icons_4.png");
	private static final ResourceLocation TEXTURE_5 = new ResourceLocation("zone:textures/gui/sphinx/icons_5.png");
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
		addComponent(new TexturedButton(this, x + 27, y + 90, TEXTURE_4, 60, 120, 60, 60, 0.25F, () -> {
			if (name != null) {
				MapHandler.focus(pos.getPos().getX(), pos.getPos().getZ());
				while (!parent.getSubscreens().empty()) {
					parent.removeScreen(parent.getSubscreens().peek());
				}
			}
		}).setYSupplier(() -> this.y + 90 + getYOffset()));
		addComponent(new TexturedButton(this, x + 44, y + 90, TEXTURE_5, 60, 0, 60, 60, 0.25F, () -> {
			parent.putScreen(new SubscreenViewItems(parent, storage));
		}).setYSupplier(() -> this.y + 90 + getYOffset()).setEnable(() -> usedStorage != 0 || maxStorage != 0));
		addComponent(new TextButton(this, this.x + 235, this.y + 175, I18n.format("sphinx.confirm"), () -> {
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
			storage = new StorageWrapper((NBTTagCompound) tag.getTag("storage"));
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
			int yOffset = 0;
			if (usedStorage != 0 || maxStorage != 0) {
				yOffset += 20;
				Util.renderGlowString(I18n.format("sphinx.storage_space") + ":", x + 27, y + 75 + yOffset);
				Util.renderGlowString(usedStorage + "/" + maxStorage, x + 180, y + 75 + yOffset);
			}
		}
		Util.renderGlowBorder(x + 15, y + 23, 270, 172);
	}

	@Override
	public boolean isBlockOtherSubscreen() {
		return true;
	}
}
