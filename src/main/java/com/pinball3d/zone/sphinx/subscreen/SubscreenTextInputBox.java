package com.pinball3d.zone.sphinx.subscreen;

import java.util.function.Consumer;
import java.util.function.Function;

import com.pinball3d.zone.gui.Subscreen;
import com.pinball3d.zone.sphinx.IHasSubscreen;
import com.pinball3d.zone.sphinx.component.TextButton;
import com.pinball3d.zone.sphinx.component.TextInputBox;
import com.pinball3d.zone.util.Util;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class SubscreenTextInputBox extends Subscreen {
	public static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/gui/sphinx/ui_border.png");
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
		addComponent(new TextButton(this, this.x + 95, this.y + 80, I18n.format("sphinx.confirm"), () -> {
			String s = isValid == null ? "" : isValid.apply(box.text);
			if (s.isEmpty()) {
				parent.removeScreen(SubscreenTextInputBox.this);
				event.accept(box.text);
			}
		}));
		addComponent(box = new TextInputBox(this, this.x + 12, this.y + 30, 100, 13, maxLength,
				() -> box.isFocus = true, flag));
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
		Util.drawTexture(TEXTURE, x - 2, y - 2, 0, 0, 99, 99, 0.25F);
		Util.drawTexture(TEXTURE, x + 128, y - 2, 99, 0, 99, 99, 0.25F);
		Util.drawTexture(TEXTURE, x - 2, y + 79, 0, 99, 99, 99, 0.25F);
		Util.drawTexture(TEXTURE, x + 128, y + 79, 99, 99, 99, 99, 0.25F);
		Gui.drawRect(x + 22, y, x + 128, y + 22, 0x2F000000);
		Gui.drawRect(x, y + 22, x + 150, y + 79, 0x2F000000);
		Gui.drawRect(x + 22, y + 79, x + 128, y + 101, 0x2F000000);
		Util.renderGlowHorizonLineThin(x + 5, y + 10, 140);
		Gui.drawRect(x + 8, y + 12, x + 142, y + 97, 0x651CC3B5);
		Util.renderGlowString(title, x + 7, y + 2);
		Util.renderSplitGlowString(text, x + 14, y + 15, 120);
		String s = isValid == null ? "" : isValid.apply(box.text);
		if (!s.isEmpty()) {
			Util.renderGlowString(s, x + 14, y + 45, 0xFFFC3D3D, 0xFFEF2020);
		}
		Util.renderGlowBorder(x + 7, y + 12, 135, 86);
	}
}
