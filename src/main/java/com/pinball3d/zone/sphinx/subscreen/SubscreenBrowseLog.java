package com.pinball3d.zone.sphinx.subscreen;

import java.util.Set;

import com.pinball3d.zone.gui.Subscreen;
import com.pinball3d.zone.network.ConnectHelperClient;
import com.pinball3d.zone.network.ConnectionHelper.Type;
import com.pinball3d.zone.sphinx.IHasSubscreen;
import com.pinball3d.zone.sphinx.component.RadioButton;
import com.pinball3d.zone.sphinx.component.ScrollingLog;
import com.pinball3d.zone.util.Util;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class SubscreenBrowseLog extends Subscreen {
	private static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/gui/sphinx/ui_border.png");
	public RadioButton button1, button2, button3, button4;
	public ScrollingLog list;

	public SubscreenBrowseLog(IHasSubscreen parent) {
		this(parent, getDisplayWidth() / 2 - 150, getDisplayHeight() / 2 - 100);
	}

	public SubscreenBrowseLog(IHasSubscreen parent, int x, int y) {
		super(parent, x, y, 300, 200, true);
		addComponent(button1 = new RadioButton(this, x + 40, y + 26, () -> {
			button1.setState(!button1.getState());
			System.out.println(1);
		}));
		addComponent(button2 = new RadioButton(this, x + 100, y + 26, () -> {
			button2.setState(!button2.getState());
			System.out.println(2);
		}));
		addComponent(button3 = new RadioButton(this, x + 160, y + 26, () -> {
			button3.setState(!button3.getState());
			System.out.println(3);
		}));
		addComponent(button4 = new RadioButton(this, x + 220, y + 26, () -> {
			button4.setState(!button4.getState());
			System.out.println(4);
		}));
		addComponent(list = new ScrollingLog(this, this.x + 16, this.y + 35, 268, 159));
	}

	@Override
	public Set<Type> getDataTypes() {
		Set<Type> s = super.getDataTypes();
		s.add(Type.LOGS);
		return s;
	}

	@Override
	public void update() {
		super.update();
		if (ConnectHelperClient.getInstance().hasData()) {
			list.setLogs(ConnectHelperClient.getInstance().getLogs());
		}
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
		Util.renderGlowString(I18n.format("sphinx.browse_log"), x + 15, y + 8);
		Util.renderGlowString(I18n.format("sphinx.important"), x + 50, y + 26);
		Util.renderGlowString(I18n.format("sphinx.info"), x + 110, y + 26);
		Util.renderGlowString(I18n.format("sphinx.debug"), x + 170, y + 26);
		Util.renderGlowString(I18n.format("sphinx.chat"), x + 230, y + 26);
	}
}
