package com.pinball3d.zone.sphinx;

import java.util.UUID;

import com.pinball3d.zone.tileentity.TEProcessingCenter;

public class ScreenSphinxController extends ScreenSphinxAdvenced {
	public TEProcessingCenter tileentity;

	public ScreenSphinxController(TEProcessingCenter te, String password) {
		this.password = password;
		tileentity = te;
	}

	public boolean checkTileentity() {
		if (tileentity == null) {
			mc.displayGuiScreen(null);
			return false;
		}
		return true;
	}

	@Override
	public void initGui() {
		super.initGui();
		if (tileentity.needInit() && subscreens.empty()) {
			subscreens.add(new SubscreenSphinxInitWizard(this));
		}
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
		return tileentity != null;
	}

	@Override
	public boolean isConnected() {
		return true;
	}

	@Override
	public UUID getNetworkUUID() {
		return tileentity.getUUID();
	}

	@Override
	public void resetNetwork() {

	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public WorldPos getNetwork() {
		return new WorldPos(tileentity);
	}
}
