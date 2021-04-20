package com.pinball3d.zone.sphinx.container;

import java.util.Set;

import com.pinball3d.zone.gui.component.TexturedButton;
import com.pinball3d.zone.network.ConnectHelperClient;
import com.pinball3d.zone.network.ConnectionHelper.Type;
import com.pinball3d.zone.sphinx.subscreen.SubscreenBrowseLog;
import com.pinball3d.zone.sphinx.subscreen.SubscreenManageClassify;
import com.pinball3d.zone.sphinx.subscreen.SubscreenManageOreDictionary;
import com.pinball3d.zone.sphinx.subscreen.SubscreenManageQueue;
import com.pinball3d.zone.sphinx.subscreen.SubscreenManageUser;
import com.pinball3d.zone.sphinx.subscreen.SubscreenShutdownSphinx;
import com.pinball3d.zone.sphinx.subscreen.SubscreenSphinxConfig;
import com.pinball3d.zone.sphinx.subscreen.SubscreenSynodLibrary;
import com.pinball3d.zone.sphinx.subscreen.SubscreenViewStorage;

public class GuiContainerSphinxController extends GuiContainerSphinxAdvanced {
	public GuiContainerSphinxController(ContainerSphinxController container) {
		super(container);
	}

	@Override
	public void addComponents() {
		super.addComponents();
		addComponent(new TexturedButton(this, width - 17, 2, ICONS_4, 60, 0, 60, 60, 0.25F, () -> {
			subscreens.push(new SubscreenShutdownSphinx(GuiContainerSphinxController.this));
		}));
		addComponent(new TexturedButton(this, width - 34, 2, ICONS_4, 0, 0, 60, 60, 0.25F, () -> {
			subscreens.push(new SubscreenSphinxConfig(GuiContainerSphinxController.this));
		}));
		addComponent(new TexturedButton(this, width - 51, 2, ICONS_4, 0, 120, 60, 60, 0.25F, () -> {
			subscreens.push(new SubscreenViewStorage(GuiContainerSphinxController.this));
		}));
		addComponent(new TexturedButton(this, width - 68, 2, ICONS_4, 60, 60, 60, 60, 0.25F, () -> {
			subscreens.push(new SubscreenSynodLibrary(GuiContainerSphinxController.this));
		}));
		addComponent(new TexturedButton(this, width - 85, 2, ICONS_4, 0, 60, 60, 60, 0.25F, () -> {
			subscreens.push(new SubscreenManageClassify(GuiContainerSphinxController.this));
		}));
		addComponent(new TexturedButton(this, width - 102, 2, ICONS_4, 180, 0, 60, 60, 0.25F, () -> {
			subscreens.push(new SubscreenManageUser(GuiContainerSphinxController.this));
		}));
		addComponent(new TexturedButton(this, width - 119, 2, ICONS_4, 120, 0, 60, 60, 0.25F, () -> {
			subscreens.push(new SubscreenBrowseLog(GuiContainerSphinxController.this));
		}));
		addComponent(new TexturedButton(this, width - 136, 2, ICONS_5, 120, 0, 60, 60, 0.25F, () -> {
			subscreens.push(new SubscreenManageQueue(GuiContainerSphinxController.this));
		}));
		addComponent(new TexturedButton(this, width - 153, 2, ICONS_5, 120, 120, 60, 60, 0.25F, () -> {
			subscreens.push(new SubscreenManageOreDictionary(GuiContainerSphinxController.this));
		}));
	}

	@Override
	public Set<Type> getDataTypes() {
		Set<Type> set = super.getDataTypes();
		set.add(Type.PLAYERVALIDNETWORK);
		set.add(Type.WORKINGSTATE);
		return set;
	}

	@Override
	public void sendReq() {
		ConnectHelperClient.getInstance().requestController(
				((ContainerSphinxController) inventorySlots).getControllerPos(), getDataTypes().toArray(new Type[] {}));
	}
}
