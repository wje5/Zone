package com.pinball3d.zone.sphinx;

import com.pinball3d.zone.network.MessageOpenIOPanelGui;
import com.pinball3d.zone.network.NetworkHandler;
import com.pinball3d.zone.sphinx.component.TexturedButton;
import com.pinball3d.zone.util.WorldPos;

import net.minecraft.util.math.BlockPos;

public class GuiContainerNeedNetworkIOPanel extends GuiContainerNeedNetwork {
	public GuiContainerNeedNetworkIOPanel(ContainerSphinxNeedNetwork container, WorldPos pos) {
		super(container, pos);
	}

	@Override
	public void addComponents() {
		super.addComponents();
		addComponent(new TexturedButton(this, width - 20, 2, TEXTURE, 32, 68, 32, 32, 0.25F, () -> {
			BlockPos pos = this.pos.getPos();
			NetworkHandler.instance
					.sendToServer(new MessageOpenIOPanelGui(mc.player, pos.getX(), pos.getY(), pos.getZ(), true));
		}));
	}
}
