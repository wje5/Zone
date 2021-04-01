package com.pinball3d.zone.sphinx.container;

import com.pinball3d.zone.gui.component.TexturedButton;
import com.pinball3d.zone.network.MessageOpenIOPanelGui;
import com.pinball3d.zone.network.NetworkHandler;
import com.pinball3d.zone.util.WorldPos;

import net.minecraft.util.math.BlockPos;

public class GuiContainerNeedNetworkIOPanel extends GuiContainerNeedNetwork {
	public GuiContainerNeedNetworkIOPanel(ContainerSphinxNeedNetwork container, WorldPos pos) {
		super(container, pos);
	}

	@Override
	public void addComponents() {
		super.addComponents();
		addComponent(new TexturedButton(this, width - 34, 2, ICONS_4, 180, 180, 60, 60, 0.25F, () -> {
			BlockPos pos = this.pos.getPos();
			NetworkHandler.instance
					.sendToServer(new MessageOpenIOPanelGui(mc.player, pos.getX(), pos.getY(), pos.getZ(), true));
		}));
	}
}
