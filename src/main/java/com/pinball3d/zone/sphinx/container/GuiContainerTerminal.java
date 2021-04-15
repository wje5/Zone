package com.pinball3d.zone.sphinx.container;

import java.util.Set;

import com.pinball3d.zone.gui.component.Button;
import com.pinball3d.zone.gui.component.TexturedButton;
import com.pinball3d.zone.network.ConnectHelperClient;
import com.pinball3d.zone.network.ConnectionHelper.Type;
import com.pinball3d.zone.sphinx.subscreen.SubscreenBrowseLog;
import com.pinball3d.zone.sphinx.subscreen.SubscreenManageClassify;
import com.pinball3d.zone.sphinx.subscreen.SubscreenManageQueue;
import com.pinball3d.zone.sphinx.subscreen.SubscreenManageUser;
import com.pinball3d.zone.sphinx.subscreen.SubscreenNetworkConfig;
import com.pinball3d.zone.sphinx.subscreen.SubscreenShutdownSphinx;
import com.pinball3d.zone.sphinx.subscreen.SubscreenSphinxConfig;
import com.pinball3d.zone.sphinx.subscreen.SubscreenSynodLibrary;
import com.pinball3d.zone.sphinx.subscreen.SubscreenViewStorage;
import com.pinball3d.zone.util.Util;

public class GuiContainerTerminal extends GuiContainerSphinxAdvanced {
	public GuiContainerTerminal(ContainerSphinxTerminal container) {
		super(container);
	}

	@Override
	public Set<Type> getDataTypes() {
		Set<Type> set = super.getDataTypes();
		set.add(Type.PLAYERVALIDNETWORK);
		set.add(Type.USERS);
		return set;
	}

	@Override
	public void addComponents() {
		super.addComponents();
		addComponent(new Button(this, width - 17, 2, 15, 15, () -> {
			subscreens.push(new SubscreenNetworkConfig(GuiContainerTerminal.this));
		}) {
			@Override
			public void doRender(int mouseX, int mouseY) {
				super.doRender(mouseX, mouseY);
				Util.drawTexture(ICONS_4, 0, 0, ConnectHelperClient.getInstance().isConnected() ? 120 : 180, 60, 60,
						60, 0.25F);
			}
		});
		addComponent(new TexturedButton(this, width - 34, 2, ICONS_4, 0, 120, 60, 60, 0.25F, () -> {
			subscreens.push(new SubscreenViewStorage(GuiContainerTerminal.this));
		}).setEnable(() -> ConnectHelperClient.getInstance().isConnected()));
		addComponent(new TexturedButton(this, width - 51, 2, ICONS_4, 60, 60, 60, 60, 0.25F, () -> {
			subscreens.push(new SubscreenSynodLibrary(GuiContainerTerminal.this));
		}).setEnable(() -> ConnectHelperClient.getInstance().isConnected()));
		addComponent(new TexturedButton(this, width - 68, 2, ICONS_4, 0, 60, 60, 60, 0.25F, () -> {
			subscreens.push(new SubscreenManageClassify(GuiContainerTerminal.this));
		}).setEnable(() -> ConnectHelperClient.getInstance().isConnected()));
		addComponent(new TexturedButton(this, width - 85, 2, ICONS_4, 180, 0, 60, 60, 0.25F, () -> {
			subscreens.push(new SubscreenManageUser(GuiContainerTerminal.this));
		}).setEnable(() -> ConnectHelperClient.getInstance().isConnected()));
		addComponent(new TexturedButton(this, width - 102, 2, ICONS_4, 120, 0, 60, 60, 0.25F, () -> {
			subscreens.push(new SubscreenBrowseLog(GuiContainerTerminal.this));
		}).setEnable(() -> ConnectHelperClient.getInstance().isConnected()));
		addComponent(new TexturedButton(this, width - 119, 2, ICONS_5, 120, 0, 60, 60, 0.25F, () -> {
			subscreens.push(new SubscreenManageQueue(GuiContainerTerminal.this));
		}).setEnable(() -> ConnectHelperClient.getInstance().isConnected()));
		addComponent(new TexturedButton(this, width - 136, 2, ICONS_4, 60, 0, 60, 60, 0.25F, () -> {
			subscreens.push(new SubscreenShutdownSphinx(GuiContainerTerminal.this));
		}).setEnable(
				() -> ConnectHelperClient.getInstance().isConnected() && ConnectHelperClient.getInstance().isAdmin()));
		addComponent(new TexturedButton(this, width - 153, 2, ICONS_4, 0, 0, 60, 60, 0.25F, () -> {
			subscreens.push(new SubscreenSphinxConfig(GuiContainerTerminal.this));
		}).setEnable(
				() -> ConnectHelperClient.getInstance().isConnected() && ConnectHelperClient.getInstance().isAdmin()));
	}

	@Override
	public void sendReq() {
		ConnectHelperClient.getInstance().requestTerminal(getDataTypes().toArray(new Type[] {}));
	}
}
