package com.pinball3d.zone.sphinx;

import java.util.Set;

import com.pinball3d.zone.network.ConnectHelperClient;
import com.pinball3d.zone.network.ConnectionHelper.Type;
import com.pinball3d.zone.sphinx.component.TexturedButton;
import com.pinball3d.zone.sphinx.subscreen.SubscreenManageClassify;
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
		addComponent(new TexturedButton(this, width - 8, 2, TEXTURE, 0, 42, 24, 26, 0.25F, () -> {
			subscreens.push(new SubscreenShutdownSphinx(GuiContainerSphinxController.this));
		}));
		addComponent(new TexturedButton(this, width - 20, 2, TEXTURE, 0, 68, 32, 32, 0.25F, () -> {
			subscreens.push(new SubscreenSphinxConfig(GuiContainerSphinxController.this));
		}));
		addComponent(new TexturedButton(this, width - 30, 2, TEXTURE, 94, 68, 22, 30, 0.25F, () -> {
			subscreens.push(new SubscreenViewStorage(GuiContainerSphinxController.this));
		}));
		addComponent(new TexturedButton(this, width - 39, 2, TEXTURE, 24, 100, 23, 30, 0.25F, () -> {
			subscreens.push(new SubscreenSynodLibrary(GuiContainerSphinxController.this));
		}));
		addComponent(new TexturedButton(this, width - 49, 2, TEXTURE, 47, 100, 28, 25, 0.25F, () -> {
			subscreens.push(new SubscreenManageClassify(GuiContainerSphinxController.this));
		}));
		addComponent(new TexturedButton(this, width - 58, 2, TEXTURE, 75, 100, 22, 27, 0.25F, () -> {
			subscreens.push(new SubscreenManageUser(GuiContainerSphinxController.this));
		}));
	}

	@Override
	public Set<Type> getDataTypes() {
		Set<Type> set = super.getDataTypes();
		set.add(Type.PLAYERVALIDNETWORK);
		set.add(Type.NETWORKPOS);
		set.add(Type.WORKINGSTATE);
		return set;
	}

	@Override
	public void sendReq() {
		ConnectHelperClient.getInstance().requestController(
				((ContainerSphinxController) inventorySlots).getControllerPos(), getDataTypes().toArray(new Type[] {}));
	}
}
