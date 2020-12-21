package com.pinball3d.zone.sphinx;

import java.util.Set;
import java.util.UUID;

import com.pinball3d.zone.network.ConnectHelperClient;
import com.pinball3d.zone.network.ConnectionHelper.Type;
import com.pinball3d.zone.tileentity.TEProcessingCenter.WorkingState;

public class ScreenSphinxController extends ScreenSphinxAdvenced {
	private WorldPos center;

	public ScreenSphinxController(WorldPos center, String password) {
		this.password = password;
		this.center = center;
	}

	@Override
	public void update(boolean online, int mouseX, int mouseY, float partialTicks) {
		super.update(online, mouseX, mouseY, partialTicks);
		if (ConnectHelperClient.getInstance().hasData()
				&& ConnectHelperClient.getInstance().getWorkingState() == WorkingState.UNINIT && subscreens.empty()) {
			subscreens.add(new SubscreenSphinxInitWizard(this));
		}
	}

	@Override
	public void sendReq() {
		ConnectHelperClient.getInstance().requestController(center, getDataTypes().toArray(new Type[] {}));
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
	protected void applyComponents() {
		super.applyComponents();
		components.add(new TexturedButton(this, width - 8, 2, TEXTURE, 0, 42, 24, 26, 0.25F, () -> {
			subscreens.push(new SubscreenShutdownSphinx(ScreenSphinxController.this));
		}));
		components.add(new TexturedButton(this, width - 20, 2, TEXTURE, 0, 68, 32, 32, 0.25F, () -> {
			subscreens.push(new SubscreenSphinxConfig(ScreenSphinxController.this));
		}));
		components.add(new TexturedButton(this, width - 30, 2, TEXTURE, 94, 68, 22, 30, 0.25F, () -> {
			subscreens.push(new SubscreenViewStorage(ScreenSphinxController.this));
		}));
		components.add(new TexturedButton(this, width - 39, 2, TEXTURE, 24, 100, 23, 30, 0.25F, () -> {
			subscreens.push(new SubscreenSynodLibrary(ScreenSphinxController.this));
		}));
		components.add(new TexturedButton(this, width - 49, 2, TEXTURE, 47, 100, 28, 25, 0.25F, () -> {
			subscreens.push(new SubscreenManageClassify(ScreenSphinxController.this));
		}));
	}

	@Override
	public boolean canOpen() {
		if (!ConnectHelperClient.getInstance().hasData()) {
			return true;
		}
		WorkingState state = ConnectHelperClient.getInstance().getWorkingState();
		return state == WorkingState.WORKING || state == WorkingState.UNINIT;
	}

	@Override
	public boolean isConnected() {
		return true;
	}

	@Override
	public UUID getNetworkUUID() {
		return ConnectHelperClient.getInstance().getNetworkFromController();
	}

	@Override
	public void resetNetwork() {

	}

	@Override
	public String getAdminPassword() {
		return password;
	}

	@Override
	public void draw(int mouseX, int mouseY, float partialTicks) {
		super.draw(mouseX, mouseY, partialTicks);
	}
}
