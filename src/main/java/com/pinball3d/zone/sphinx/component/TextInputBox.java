package com.pinball3d.zone.sphinx.component;

import org.lwjgl.input.Keyboard;

import com.pinball3d.zone.gui.Component;
import com.pinball3d.zone.sphinx.IHasComponents;
import com.pinball3d.zone.util.Util;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;

public class TextInputBox extends Component {
	public String text = "";
	public int maxLength;
	protected Runnable event, onInput;
	public boolean isFocus;
	public int flag;
	public boolean isPixel;

	public TextInputBox(IHasComponents parent, int x, int y, int width, int maxLength, Runnable onClick) {
		this(parent, x, y, width, 13, maxLength, onClick);
	}

	public TextInputBox(IHasComponents parent, int x, int y, int width, int height, int maxLength, Runnable onClick) {
		this(parent, x, y, width, height, maxLength, onClick, 8);
	}

	public TextInputBox(IHasComponents parent, int x, int y, int width, int height, int maxLength, Runnable onClick,
			int flag) {
		super(parent, x, y, width, height);
		this.maxLength = maxLength;
		event = onClick;
		this.flag = flag;
	}

	public TextInputBox setOnInput(Runnable r) {
		onInput = r;
		return this;
	}

	public TextInputBox setIsPixel(boolean flag) {
		isPixel = flag;
		return this;
	}

	@Override
	public boolean onLeftClick(int x, int y) {
		if (super.onLeftClick(x, y)) {
			return true;
		}
		if (event != null) {
			event.run();
		}
		return true;
	}

	@Override
	public boolean onKeyTyped(char typedChar, int keyCode) {
		if (isFocus) {
			if (GuiScreen.isKeyComboCtrlV(keyCode) && text.length() < maxLength) {
				String s = GuiScreen.getClipboardString();
				for (int i = 0; i < s.length(); i++) {
					if (!Util.isValidChar(s.charAt(i), flag)) {
						return false;
					}
				}
				text += s;
				if (text.length() > maxLength) {
					text = text.substring(0, maxLength);
				}
				if (onInput != null) {
					onInput.run();
				}
				return true;
			} else if (keyCode == Keyboard.KEY_BACK && text.length() >= 1) {
				text = text.substring(0, text.length() - 1);
				if (onInput != null) {
					onInput.run();
				}
				return true;
			} else if (Util.isValidChar(typedChar, flag) && text.length() < maxLength) {
				if (isPixel) {
					if (Util.getFontRenderer().getStringWidth(text + typedChar) <= maxLength) {
						text += typedChar;
						if (onInput != null) {
							onInput.run();
						}
						return true;
					}
				} else if (text.length() < maxLength) {
					text += typedChar;
					if (onInput != null) {
						onInput.run();
					}
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void doRender(int mouseX, int mouseY) {
		super.doRender(mouseX, mouseY);
		Util.drawBorder(x, y, width, height, 1, 0xFF1ECCDE);
		FontRenderer renderer = Util.getFontRenderer();
		if (isFocus && mc.world.getTotalWorldTime() % 20 < 10) {
			Gui.drawRect(x + 3 + renderer.getStringWidth(text), y + 2, x + 4 + renderer.getStringWidth(text),
					y + height - 2, 0xFF1ECCDE);
		}
		Util.renderGlowString(text, x + 3, y + 3);
	}
}
