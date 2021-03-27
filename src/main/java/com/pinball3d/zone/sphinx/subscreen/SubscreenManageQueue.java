package com.pinball3d.zone.sphinx.subscreen;

import com.pinball3d.zone.gui.Container;
import com.pinball3d.zone.gui.IHasSubscreen;
import com.pinball3d.zone.gui.Subscreen;
import com.pinball3d.zone.sphinx.component.RadioButton;
import com.pinball3d.zone.sphinx.component.ScrollingListNetwork;
import com.pinball3d.zone.sphinx.component.TextInputBox;
import com.pinball3d.zone.sphinx.component.TexturedButton;
import com.pinball3d.zone.util.Util;
import com.pinball3d.zone.util.WorldPos;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class SubscreenManageQueue extends Subscreen {
	private static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/gui/sphinx/ui_border.png");
	private static final ResourceLocation ICONS = new ResourceLocation("zone:textures/gui/sphinx/icons.png");
	private TextInputBox box;
	public ScrollingListNetwork list;
	public RadioButton button1, button2, button3;

	public SubscreenManageQueue(IHasSubscreen parent) {
		this(parent, getDisplayWidth() / 2 - 150, getDisplayHeight() / 2 - 100);
	}

	public SubscreenManageQueue(IHasSubscreen parent, WorldPos pos) {
		this(parent, getDisplayWidth() / 2 - 150, getDisplayHeight() / 2 - 100, pos);
	}

	public SubscreenManageQueue(IHasSubscreen parent, int x, int y) {
		this(parent, x, y, WorldPos.ORIGIN);
	}

	public SubscreenManageQueue(IHasSubscreen parent, int x, int y, WorldPos pos) {
		super(parent, x, y, 300, 200, true);
		addComponent(box = new TextInputBox(this, x + 20, y + 27, 61, 15, 55, () -> {
			box.isFocus = true;
		}).setIsPixel(true));
		addComponent(new TexturedButton(this, x + 80, y + 27, ICONS, 92, 41, 15, 15, 1.0F, () -> {
			box.isFocus = false;
		}));
		addComponent(button1 = new RadioButton(this, x + 100, y + 31, () -> {
			button1.setState(!button1.getState());
		}).setState(true));
		addComponent(button2 = new RadioButton(this, x + 170, y + 31, () -> {
			button2.setState(!button2.getState());
		}).setState(true));
		addComponent(button3 = new RadioButton(this, x + 240, y + 31, () -> {
			button3.setState(!button3.getState());
		}).setState(true));
		addComponent(list = new ScrollingListNetwork(this, this.x + 16, this.y + 45, 268, 149, pos));
		Container c = null;
		addComponent(c = new Container(this, x + 5, y + 5, 100, 100));
		c.addComponent(new RadioButton(this, x + 10, y + 31, () -> {
			System.out.println(1);
		}).setState(true));
	}

	public void refresh() {
		list.refresh();
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
		Util.renderGlowString(I18n.format("sphinx.manage_queue"), x + 15, y + 8);
		Util.renderGlowBorder(x + 15, y + 23, 270, 172);
		Util.renderGlowString(I18n.format("sphinx.infinite_queue"), x + 110, y + 31);
		Util.renderGlowString(I18n.format("sphinx.pausing"), x + 180, y + 31);
		Util.renderGlowString(I18n.format("sphinx.running"), x + 250, y + 31);
	}

	@Override
	public boolean isBlockOtherSubscreen() {
		return true;
	}
}
