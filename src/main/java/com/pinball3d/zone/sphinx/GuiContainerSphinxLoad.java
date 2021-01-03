package com.pinball3d.zone.sphinx;

import java.util.Set;

import com.pinball3d.zone.network.ConnectHelperClient;
import com.pinball3d.zone.network.ConnectionHelper.Type;
import com.pinball3d.zone.tileentity.TEProcessingCenter.WorkingState;
import com.pinball3d.zone.util.Util;

import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

public class GuiContainerSphinxLoad extends GuiContainerNetworkBase {
	private int tick;

	public GuiContainerSphinxLoad(ContainerSphinxLoad container) {
		super(container);
	}

	@Override
	public void sendReq(Set<Type> types) {
		ConnectHelperClient.getInstance().requestController(((ContainerSphinxLoad) inventorySlots).getControllerPos(),
				types.toArray(new Type[] {}));
	}

	@Override
	public Set<Type> getDataTypes() {
		Set<Type> set = super.getDataTypes();
		set.add(Type.WORKINGSTATE);
		return set;
	}

	@Override
	public void updateScreen() {
		tick++;
	}

	@Override
	protected void draw(int mouseX, int mouseY, float partialTicks) {
		if (ConnectHelperClient.getInstance().hasData()
				&& ConnectHelperClient.getInstance().getWorkingState() != WorkingState.STARTING) {
			mc.player.closeScreen();
			return;
		}
		Gui.drawRect(0, 0, mc.displayWidth, mc.displayHeight, 0xFF003434);
		Util.drawTexture(new ResourceLocation("zone:textures/gui/sphinx/sphinx_load_" + tick % 12 + ".png"),
				width / 2 - 29, height / 2 - 32, 0, 0, 256, 256, 0.25F);
		super.draw(mouseX, mouseY, partialTicks);
	}
}
