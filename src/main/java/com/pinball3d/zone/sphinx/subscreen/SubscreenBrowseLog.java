package com.pinball3d.zone.sphinx.subscreen;

import java.util.Iterator;
import java.util.Queue;
import java.util.Set;

import com.pinball3d.zone.gui.IHasSubscreen;
import com.pinball3d.zone.gui.Subscreen;
import com.pinball3d.zone.gui.component.RadioButton;
import com.pinball3d.zone.network.ConnectHelperClient;
import com.pinball3d.zone.network.ConnectionHelper.Type;
import com.pinball3d.zone.sphinx.component.ScrollingLog;
import com.pinball3d.zone.sphinx.log.Log;
import com.pinball3d.zone.util.Util;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;

public class SubscreenBrowseLog extends Subscreen {
	public RadioButton button1, button2, button3, button4;
	public ScrollingLog list;

	public SubscreenBrowseLog(IHasSubscreen parent) {
		this(parent, getDisplayWidth() / 2 - 150, getDisplayHeight() / 2 - 100);
	}

	public SubscreenBrowseLog(IHasSubscreen parent, int x, int y) {
		super(parent, x, y, 300, 200, true);
		addComponent(button1 = new RadioButton(this, 40, 26, () -> {
			button1.setState(!button1.getState());
		}).setState(true));
		addComponent(button2 = new RadioButton(this, 100, 26, () -> {
			button2.setState(!button2.getState());
		}).setState(true));
		addComponent(button3 = new RadioButton(this, 160, 26, () -> {
			button3.setState(!button3.getState());
		}));
		addComponent(button4 = new RadioButton(this, 220, 26, () -> {
			button4.setState(!button4.getState());
		}).setState(true));
		addComponent(list = new ScrollingLog(this, 16, 34, 268, 160));
	}

	@Override
	public Set<Type> getDataTypes() {
		Set<Type> s = super.getDataTypes();
		s.add(Type.LOGS);
		s.add(Type.NAME);
		return s;
	}

	@Override
	public void update() {
		super.update();
		if (ConnectHelperClient.getInstance().hasData()) {
			Queue<Log> q = ConnectHelperClient.getInstance().getLogs();
			Iterator<Log> it = q.iterator();
			while (it.hasNext()) {
				switch (it.next().getLevel()) {
				case IMPORTANT:
					if (!button1.getState()) {
						it.remove();
					}
					break;
				case INFO:
					if (!button2.getState()) {
						it.remove();
					}
					break;
				case DEBUG:
					if (!button3.getState()) {
						it.remove();
					}
					break;
				case CHAT:
					if (!button4.getState()) {
						it.remove();
					}
					break;
				}
			}
			list.setLogs(q);
		}
	}

	@Override
	public void doRenderBackground(int mouseX, int mouseY) {
		super.doRenderBackground(mouseX, mouseY);
		Util.drawTexture(UI_BORDER, -5, -5, 0, 0, 99, 99, 0.5F);
		Util.drawTexture(UI_BORDER, 255, -5, 99, 0, 99, 99, 0.5F);
		Util.drawTexture(UI_BORDER, -5, 155, 0, 99, 99, 99, 0.5F);
		Util.drawTexture(UI_BORDER, 255, 155, 99, 99, 99, 99, 0.5F);
		Gui.drawRect(44, 0, 255, 44, 0x2F000000);
		Gui.drawRect(0, 44, 300, 155, 0x2F000000);
		Gui.drawRect(44, 155, 255, 200, 0x2F000000);
		Util.renderGlowHorizonLine(10, 20, 280);
		Gui.drawRect(16, 24, 284, 194, 0x651CC3B5);
		Util.renderGlowBorder(15, 23, 270, 172);
		Util.renderGlowString(I18n.format("sphinx.browse_log"), 15, 8);
		Util.renderGlowString(I18n.format("sphinx.important"), 50, 26);
		Util.renderGlowString(I18n.format("sphinx.info"), 110, 26);
		Util.renderGlowString(I18n.format("sphinx.debug"), 170, 26);
		Util.renderGlowString(I18n.format("sphinx.chat"), 230, 26);
	}

	@Override
	public boolean isBlockOtherSubscreen() {
		return true;
	}
}
