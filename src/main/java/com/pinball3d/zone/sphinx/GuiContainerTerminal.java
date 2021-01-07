package com.pinball3d.zone.sphinx;

import java.util.Set;

import com.pinball3d.zone.network.ConnectHelperClient;
import com.pinball3d.zone.network.ConnectionHelper.Type;
import com.pinball3d.zone.sphinx.component.ButtonNetworkConfig;
import com.pinball3d.zone.sphinx.component.TexturedButton;
import com.pinball3d.zone.sphinx.subscreen.SubscreenManageClassify;
import com.pinball3d.zone.sphinx.subscreen.SubscreenNetworkConfig;
import com.pinball3d.zone.sphinx.subscreen.SubscreenSynodLibrary;
import com.pinball3d.zone.sphinx.subscreen.SubscreenViewStorage;

public class GuiContainerTerminal extends GuiContainerSphinxAdvanced {
	public GuiContainerTerminal(ContainerSphinxTerminal container) {
		super(container);
	}

	@Override
	public Set<Type> getDataTypes() {
		Set<Type> set = super.getDataTypes();
		set.add(Type.PLAYERVALIDNETWORK);
		return set;
	}

	@Override
	public void addComponents() {
		super.addComponents();
		addComponent(new ButtonNetworkConfig(this, width - 10, 2, () -> {
			subscreens.push(new SubscreenNetworkConfig(GuiContainerTerminal.this));
		}, () -> ConnectHelperClient.getInstance().isConnected()));
		addComponent(new TexturedButton(this, width - 20, 2, TEXTURE, 94, 68, 22, 30, 0.25F, () -> {
			subscreens.push(new SubscreenViewStorage(GuiContainerTerminal.this));
		}).setEnable(() -> ConnectHelperClient.getInstance().isConnected()));
		addComponent(new TexturedButton(this, width - 30, 2, TEXTURE, 24, 100, 23, 30, 0.25F, () -> {
			subscreens.push(new SubscreenSynodLibrary(GuiContainerTerminal.this));
		}).setEnable(() -> ConnectHelperClient.getInstance().isConnected()));
		addComponent(new TexturedButton(this, width - 40, 2, TEXTURE, 47, 100, 28, 25, 0.25F, () -> {
			subscreens.push(new SubscreenManageClassify(GuiContainerTerminal.this));
		}).setEnable(() -> ConnectHelperClient.getInstance().isConnected()));
	}

	@Override
	public void sendReq() {
		ConnectHelperClient.getInstance().requestTerminal(getDataTypes().toArray(new Type[] {}));
	}
}
