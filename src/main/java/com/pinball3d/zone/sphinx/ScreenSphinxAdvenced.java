package com.pinball3d.zone.sphinx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.input.Keyboard;

public abstract class ScreenSphinxAdvenced extends ScreenSphinxBase {
	protected List<Pointer> chosen = new ArrayList<Pointer>();
	protected int chosenIndex = 0;
	protected String password;

	public String getPassword() {
		return password;
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (keyCode == Keyboard.KEY_ESCAPE) {
			if (subscreens.empty()) {
				super.keyTyped(typedChar, keyCode);
			} else if (subscreens.peek().onQuit()) {
				subscreens.pop();
			}
		} else {
			if (subscreens.empty()) {
				Iterator<Component> it = components.iterator();
				boolean flag = false;
				while (!flag && it.hasNext()) {
					Component c = it.next();
					flag = c.onKeyTyped(typedChar, keyCode);
				}
				if (keyCode == Keyboard.KEY_TAB) {
					setChosenIndex(chosenIndex + 1);
					updateChosenUnitButton();
				}
			} else {
				subscreens.peek().keyTyped(typedChar, keyCode);
			}
		}
	}

	@Override
	public void draw(int mouseX, int mouseY, float partialTicks) {
		boolean flag = false;
		for (int i = 0; i < chosen.size(); i++) {
			if (!MapHandler.isValidPointer(chosen.get(i))) {
				chosen.remove(i);
				if (i < chosenIndex) {
					chosenIndex--;
				} else if (i == chosenIndex) {
					chosenIndex = 0;
				}
				flag = true;
			}
		}
		if (flag) {
			updateChosenUnitButton();
		}
		if (!chosen.isEmpty()) {
			Util.drawTexture(TEXTURE_3, width - 128, height - 58, 256, 115, 0.5F);
			Util.drawBorder(width - 106, height - 47, 28, 28, 1, 0xFF20E6E6);
			chosen.get(chosenIndex).renderThumbHuge(width - 105, height - 46);
			if (chosen.size() > 1) {
				for (int i = 0; i < chosen.size(); i++) {
					Util.drawBorder(width - 74 + (i % 5) * 15, height - (i < 5 ? 47 : 32), 13, 13, 1,
							i == chosenIndex ? 0xFFE0E0E0 : 0xFF20E6E6);
					chosen.get(i).renderThumb(width - 73 + (i % 5) * 15, height - (i < 5 ? 46 : 31));
				}
			}
		}
	}

	public void setChosen(List<Pointer> l) {
		chosen = l.size() > 10 ? l.subList(0, 10) : l;
		chosenIndex = 0;
		updateChosenUnitButton();
	}

	public List<Pointer> getChosen() {
		return chosen;
	}

	public void setChosenIndex(int index) {
		if (index < chosen.size()) {
			chosenIndex = index;
		} else {
			chosenIndex = 0;
		}
	}

	public int getChosenIndex() {
		return chosenIndex;
	}

	public void updateChosenUnitButton() {
		Iterator<Component> it = components.iterator();
		while (it.hasNext()) {
			Component c = it.next();
			if (c instanceof IUnitButton) {
				it.remove();
			}
		}
		if (!chosen.isEmpty()) {
			List<Component> l = chosen.get(chosenIndex).getUnitButtons(this);
			it = l.iterator();
			int index = 0;
			while (it.hasNext()) {
				Component c = it.next();
				c.x = width - 100 + index * 13;
				c.y = height - 12;
				index++;
			}
			components.addAll(l);
		}
	}
}
