package com.pinball3d.zone.sphinx;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;

public class TextInputBox extends Component {
	public String text = "";
	public int maxLength;
	protected Runnable event;
	public boolean isFocus;
	public int flag;

	public TextInputBox(IParent parent, int x, int y, int width, int maxLength, Runnable onClick) {
		this(parent, x, y, width, 13, maxLength, onClick);
	}

	public TextInputBox(IParent parent, int x, int y, int width, int height, int maxLength, Runnable onClick) {
		this(parent, x, y, width, height, maxLength, onClick, 7);
	}

	public TextInputBox(IParent parent, int x, int y, int width, int height, int maxLength, Runnable onClick,
			int flag) {
		super(parent, x, y, width, height);
		this.x = x;
		this.y = y;
		this.maxLength = maxLength;
		event = onClick;
		this.flag = flag;
	}

	@Override
	public void onLeftClick(int x, int y) {
		super.onLeftClick(x, y);
		if (event != null) {
			event.run();
		}
	}

	@Override
	public void onKeyTyped(char typedChar, int keyCode) {
		if (isFocus) {
			if (keyCode == Keyboard.KEY_BACK && text.length() >= 1) {
				text = text.substring(0, text.length() - 1);
			}
			if (Util.isValidChar(typedChar, flag) && text.length() < maxLength) {
				text += typedChar;
			}
		}
	}

	@Override
	public void doRender(int mouseX, int mouseY) {
		super.doRender(mouseX, mouseY);
		Util.drawBorder(x, y, width, height, 1, 0xFF1ECCDE);
		FontRenderer renderer = parent.getFontRenderer();
		if (isFocus && mc.world.getTotalWorldTime() % 20 < 10) {
			Gui.drawRect(x + 3 + renderer.getStringWidth(text), y + 2, x + 4 + renderer.getStringWidth(text),
					y + height - 2, 0xFF1ECCDE);
		}
		renderer.drawString(text, x + 3, y + 3, 0xFF1ECCDE);
	}
}
