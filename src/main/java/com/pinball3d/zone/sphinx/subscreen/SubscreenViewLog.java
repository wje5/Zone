package com.pinball3d.zone.sphinx.subscreen;

import com.pinball3d.zone.gui.Subscreen;
import com.pinball3d.zone.sphinx.IHasSubscreen;
import com.pinball3d.zone.sphinx.component.ScrollingViewLog;
import com.pinball3d.zone.sphinx.log.FormattedLog;
import com.pinball3d.zone.util.Util;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class SubscreenViewLog extends Subscreen {
	private static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/gui/sphinx/ui_border.png");
	private FormattedLog log;

	public SubscreenViewLog(IHasSubscreen parent, FormattedLog log) {
		this(parent, log, getDisplayWidth() / 2 - 150, getDisplayHeight() / 2 - 100);
	}

	public SubscreenViewLog(IHasSubscreen parent, FormattedLog log, int x, int y) {
		super(parent, x, y, 300, 200, true);
		this.log = log;
		addComponent(new ScrollingViewLog(this, log, this.x + 21, this.y + 27, 263, 167));
	}

	public FormattedLog getLog() {
		return log;
	}

	@Override
	public void doRenderBackground(int mouseX, int mouseY) {
		super.doRenderBackground(mouseX, mouseY);
		Util.drawTexture(TEXTURE, x - 5, y - 5, 0, 0, 99, 99, 0.5F);
		Util.drawTexture(TEXTURE, x + 255, y - 5, 99, 0, 99, 99, 0.5F);
		Util.drawTexture(TEXTURE, x - 5, y + 155, 0, 99, 99, 99, 0.5F);
		Util.drawTexture(TEXTURE, x + 255, y + 155, 99, 99, 99, 99, 0.5F);
		Gui.drawRect(x + 44, y, x + 255, y + 44, 0x2F000000);
		Gui.drawRect(x, y + 44, x + 300, y + 155, 0x2F000000);
		Gui.drawRect(x + 44, y + 155, x + 255, y + 200, 0x2F000000);
		Util.renderGlowHorizonLine(x + 10, y + 20, 280);
		Gui.drawRect(x + 16, y + 24, x + 284, y + 194, 0x651CC3B5);
		Util.renderGlowBorder(x + 15, y + 23, 270, 172);
		Util.renderGlowString(I18n.format("sphinx.view_log"), x + 15, y + 8);
	}

	@Override
	public boolean onQuit() {
		parent.removeScreen(this);
		parent.putScreen(new SubscreenBrowseLog(parent));
		return false;
	}
}
