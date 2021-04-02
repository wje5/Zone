package com.pinball3d.zone.sphinx.subscreen;

import com.pinball3d.zone.gui.IHasSubscreen;
import com.pinball3d.zone.gui.Subscreen;
import com.pinball3d.zone.gui.component.Button;
import com.pinball3d.zone.gui.component.Container;
import com.pinball3d.zone.gui.component.RadioButton;
import com.pinball3d.zone.gui.component.ScrollingContainer;
import com.pinball3d.zone.gui.component.Text;
import com.pinball3d.zone.gui.component.TextInputBox;
import com.pinball3d.zone.gui.component.Texture;
import com.pinball3d.zone.gui.component.TexturedButton;
import com.pinball3d.zone.util.Util;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;

public class SubscreenManageQueue extends Subscreen {
	private TextInputBox box;
	public ScrollingContainer list;
	public RadioButton button1, button2, button3;

	public SubscreenManageQueue(IHasSubscreen parent) {
		this(parent, getDisplayWidth() / 2 - 150, getDisplayHeight() / 2 - 100);
	}

	public SubscreenManageQueue(IHasSubscreen parent, int x, int y) {
		super(parent, x, y, 300, 200, true);
		addComponent(box = new TextInputBox(this, 20, 27, 61, 15, 55, () -> {
			box.isFocus = true;
		}).setIsPixel(true));
		addComponent(new TexturedButton(this, 80, 27, ICONS, 92, 41, 15, 15, 1.0F, () -> {
			box.isFocus = false;
		}));
		addComponent(button1 = new RadioButton(this, 100, 31, () -> {
			button1.setState(!button1.getState());
		}).setState(true));
		addComponent(button2 = new RadioButton(this, 170, 31, () -> {
			button2.setState(!button2.getState());
		}).setState(true));
		addComponent(button3 = new RadioButton(this, 240, 31, () -> {
			button3.setState(!button3.getState());
		}).setState(true));
		addComponent(list = new ScrollingContainer(this, 16, 45, 268, 149));
		Container c = new Container(list, 0, 0, 268, 25);
		c.addComponent(new Texture(c, 5, 5, ICONS_5, 180, 0, 64, 64, 0.25F));
		c.addComponent(new Button(c, 0, 0, 268, 25, () -> System.out.println(11111)));
		c.addComponent(new Text(c, 23, 9, I18n.format("sphinx.add_new_queue")));
		list.addComponent(c);
	}

	private Container genQueueBar() {
		Container c = new Container(list, 0, 0, 268, 25);
		list.addComponent(c);
		return c;
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
		Util.renderGlowString(I18n.format("sphinx.manage_queue"), 15, 8);
		Util.renderGlowBorder(15, 23, 270, 172);
		Util.renderGlowString(I18n.format("sphinx.infinite_queue"), 110, 31);
		Util.renderGlowString(I18n.format("sphinx.pausing"), 180, 31);
		Util.renderGlowString(I18n.format("sphinx.running"), 250, 31);
	}

	@Override
	public boolean isBlockOtherSubscreen() {
		return true;
	}
}
