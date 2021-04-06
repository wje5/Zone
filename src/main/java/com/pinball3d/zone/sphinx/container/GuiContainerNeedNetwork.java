package com.pinball3d.zone.sphinx.container;

import java.util.Set;

import com.pinball3d.zone.gui.component.Button;
import com.pinball3d.zone.network.ConnectHelperClient;
import com.pinball3d.zone.network.ConnectionHelper.Type;
import com.pinball3d.zone.sphinx.subscreen.SubscreenNetworkConfig;
import com.pinball3d.zone.util.Util;
import com.pinball3d.zone.util.WorldPos;

import net.minecraft.nbt.NBTTagCompound;

public class GuiContainerNeedNetwork extends GuiContainerSphinxBase {
	public WorldPos pos;
	public NBTTagCompound data;

	public GuiContainerNeedNetwork(ContainerSphinxNeedNetwork container, WorldPos pos) {
		super(container);
		this.pos = pos;
	}

	@Override
	public void sendReq() {
		ConnectHelperClient.getInstance().requestNeedNetwork(pos, getDataTypes().toArray(new Type[] {}));
	}

	@Override
	public Set<Type> getDataTypes() {
		Set<Type> set = super.getDataTypes();
		set.add(Type.NETWORKUUID);
		set.add(Type.NEEDNETWORKVALIDNETWORK);
		set.add(Type.NEEDNETWORKSERIAL);
		return set;
	}

	@Override
	public void addComponents() {
		super.addComponents();
		addComponent(new Button(this, width - 17, 2, 15, 15, () -> {
			subscreens.push(new SubscreenNetworkConfig(GuiContainerNeedNetwork.this, pos));
		}) {
			@Override
			public void doRender(int mouseX, int mouseY) {
				super.doRender(mouseX, mouseY);
				Util.drawTexture(TEXTURE_4, 0, 0, ConnectHelperClient.getInstance().isConnected() ? 120 : 180, 60, 60,
						60, 0.25F);
			}
		});
	}
}
