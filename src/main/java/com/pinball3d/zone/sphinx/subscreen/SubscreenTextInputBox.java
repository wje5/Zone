package com.pinball3d.zone.sphinx.subscreen;

import java.util.function.Consumer;
import java.util.function.Function;

import com.pinball3d.zone.gui.IHasSubscreen;
import com.pinball3d.zone.gui.Subscreen;
import com.pinball3d.zone.gui.component.TextButton;
import com.pinball3d.zone.gui.component.TextInputBox;
import com.pinball3d.zone.util.Util;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;

public class SubscreenTextInputBox extends Subscreen {
	public String title, text;
	public Consumer<String> event;
	public Function<String, String> isValid;
	public TextInputBox box;

	public SubscreenTextInputBox(IHasSubscreen parent, String title, String text, Consumer<String> event, int maxLength,
			int flag) {
		this(parent, getDisplayWidth() / 2 - 75, getDisplayHeight() / 2 - 50, title, text, event, maxLength, flag);
	}

	public SubscreenTextInputBox(IHasSubscreen parent, int x, int y, String title, String text, Consumer<String> event,
			int maxLength, int flag) {
		super(parent, x, y, 150, 100, true);
		addComponent(new TextButton(this, 95, 80, I18n.format("sphinx.confirm"), () -> {
			String s = isValid == null ? "" : isValid.apply(box.text);
			if (s.isEmpty()) {
				parent.removeScreen(SubscreenTextInputBox.this);
				event.accept(box.text);
			}
		}));
		addComponent(box = new TextInputBox(this, 12, 30, 100, 13, maxLength, () -> box.isFocus = true, flag));
		this.title = title;
		this.text = text;
		this.event = event;
	}

	public SubscreenTextInputBox setIsValid(Function<String, String> isValid) {
		this.isValid = isValid;
		return this;
	}

	public SubscreenTextInputBox setText(String text) {
		box.text = text;
		return this;
	}

	@Override
	public void doRenderBackground(int mouseX, int mouseY) {
		super.doRenderBackground(mouseX, mouseY);
		Util.drawTexture(UI_BORDER, -2, -2, 0, 0, 99, 99, 0.25F);
		Util.drawTexture(UI_BORDER, 128, -2, 99, 0, 99, 99, 0.25F);
		Util.drawTexture(UI_BORDER, -2, 79, 0, 99, 99, 99, 0.25F);
		Util.drawTexture(UI_BORDER, 128, 79, 99, 99, 99, 99, 0.25F);
		Gui.drawRect(22, 0, 128, 22, 0x2F000000);
		Gui.drawRect(0, 22, 150, 79, 0x2F000000);
		Gui.drawRect(22, 79, 128, 101, 0x2F000000);
		Util.renderGlowHorizonLineThin(5, 10, 140);
		Gui.drawRect(8, 12, 142, 97, 0x651CC3B5);
		Util.renderGlowString(title, 7, 2);
		Util.renderSplitGlowString(text, 14, 15, 120);
		String s = isValid == null ? "" : isValid.apply(box.text);
		if (!s.isEmpty()) {
			Util.renderGlowString(s, 14, 45, 0xFFFC3D3D, 0xFFEF2020);
		}
		Util.renderGlowBorder(7, 12, 135, 86);
	}
}
