package com.pinball3d.zone.sphinx.subscreen;

import com.pinball3d.zone.gui.Subscreen;
import com.pinball3d.zone.sphinx.IHasSubscreen;
import com.pinball3d.zone.sphinx.component.ScrollingListNetwork;
import com.pinball3d.zone.util.Util;
import com.pinball3d.zone.util.WorldPos;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class SubscreenNetworkConfig extends Subscreen {
	private static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/gui/sphinx/ui_border.png");
	public ScrollingListNetwork list;

	public SubscreenNetworkConfig(IHasSubscreen parent) {
		this(parent, getDisplayWidth() / 2 - 150, getDisplayHeight() / 2 - 100);
	}

	public SubscreenNetworkConfig(IHasSubscreen parent, WorldPos pos) {
		this(parent, getDisplayWidth() / 2 - 150, getDisplayHeight() / 2 - 100, pos);
	}

	public SubscreenNetworkConfig(IHasSubscreen parent, int x, int y) {
		this(parent, x, y, WorldPos.ORIGIN);
	}

	public SubscreenNetworkConfig(IHasSubscreen parent, int x, int y, WorldPos pos) {
		super(parent, x, y, 300, 200, true);
		components.add(list = new ScrollingListNetwork(this, this.x + 16, this.y + 24, 268, 170, pos));
	}

	public void refresh() {
		list.refresh();
	}

	@Override
	public void doRenderBackground(int mouseX, int mouseY) {
		super.doRenderBackground(mouseX, mouseY);
		Util.drawTexture(TEXTURE, x, y, 0, 0, 80, 80, 0.5F);
		Util.drawTexture(TEXTURE, x + 260, y, 80, 0, 80, 80, 0.5F);
		Util.drawTexture(TEXTURE, x, y + 160, 0, 80, 80, 80, 0.5F);
		Util.drawTexture(TEXTURE, x + 260, y + 160, 80, 80, 80, 80, 0.5F);
		Gui.drawRect(x + 40, y, x + 260, y + 40, 0x2F000000);
		Gui.drawRect(x, y + 40, x + 300, y + 160, 0x2F000000);
		Gui.drawRect(x + 40, y + 160, x + 260, y + 200, 0x2F000000);
		Gui.drawRect(x + 10, y + 20, x + 290, y + 22, 0xFF20E6EF);
		Gui.drawRect(x + 16, y + 24, x + 284, y + 194, 0x651CC3B5);
		Util.getFontRenderer().drawString(I18n.format("sphinx.network_config"), x + 15, y + 8, 0xFF1ECCDE);
		Util.drawBorder(x + 15, y + 23, 270, 172, 1, 0xFF1ECCDE);
	}
}
