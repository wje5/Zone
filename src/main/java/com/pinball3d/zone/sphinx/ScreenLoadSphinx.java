package com.pinball3d.zone.sphinx;

import com.pinball3d.zone.network.ConnectHelperClient;
import com.pinball3d.zone.network.ConnectionHelper.Type;
import com.pinball3d.zone.network.MessageConnectionControllerRequest;
import com.pinball3d.zone.network.NetworkHandler;
import com.pinball3d.zone.tileentity.TEProcessingCenter.WorkingState;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

public class ScreenLoadSphinx extends GuiScreen {
	private WorldPos center;
	private boolean inited;
	private int tick;

	public ScreenLoadSphinx(WorldPos center) {
		this.center = center;
	}

	@Override
	public void initGui() {
		super.initGui();
		if (!inited) {
			NetworkHandler.instance.sendToServer(new MessageConnectionControllerRequest(mc.player, center,
					Type.NETWORKPOS, Type.WORKINGSTATE, Type.ON));
			inited = true;
		}
	}

	@Override
	public void onGuiClosed() {
		ConnectHelperClient.getInstance().disconnect();
		super.onGuiClosed();
	}

	@Override
	public void updateScreen() {
		tick++;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		WorkingState state = ConnectHelperClient.getInstance().getWorkingState();
		if (state != null && state != WorkingState.STARTING) {
			if (ConnectHelperClient.getInstance().isOn()) {
				if (ConnectHelperClient.getInstance().isInited()) {
					mc.displayGuiScreen(new ScreenSphinxOpenPassword(center, false));
				} else {
					mc.displayGuiScreen(new ScreenSphinxController(center, ""));
				}
			} else {
				mc.displayGuiScreen(null);
			}
			return;
		}
		Gui.drawRect(0, 0, mc.displayWidth, mc.displayHeight, 0xFF003434);
		Util.drawTexture(new ResourceLocation("zone:textures/gui/sphinx/sphinx_load_" + tick % 12 + ".png"),
				width / 2 - 29, height / 2 - 32, 0, 0, 256, 256, 0.25F);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
}
