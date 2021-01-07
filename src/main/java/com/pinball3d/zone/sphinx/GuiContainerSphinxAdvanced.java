package com.pinball3d.zone.sphinx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.input.Keyboard;

import com.pinball3d.zone.sphinx.component.Component;
import com.pinball3d.zone.sphinx.component.IUnitButton;
import com.pinball3d.zone.sphinx.map.MapHandler;
import com.pinball3d.zone.sphinx.map.Pointer;
import com.pinball3d.zone.util.Util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.settings.GameSettings;

public abstract class GuiContainerSphinxAdvanced extends GuiContainerSphinxBase {
	protected List<Pointer> chosen = new ArrayList<Pointer>();
	protected int chosenIndex = 0;
	private int dragBoxX, dragBoxY, dragBoxX2, dragBoxY2;

	public GuiContainerSphinxAdvanced(ContainerSphinxAdvanced container) {
		super(container);
	}

	@Override
	public void initGui() {
		super.initGui();
		updateChosenUnitButton();
	}

	@Override
	protected void onKetInput(char typedChar, int keyCode) {
		if (subscreens.empty() && keyCode == Keyboard.KEY_TAB) {
			setChosenIndex(chosenIndex + 1);
			updateChosenUnitButton();
		}
		super.onKetInput(typedChar, keyCode);
	}

	@Override
	protected void onDragScreen(int mouseX, int mouseY, int moveX, int moveY, int clickedMouseButton) {
		super.onDragScreen(mouseX, mouseY, moveX, moveY, clickedMouseButton);
		if (clickedMouseButton == 0) {
			dragBoxX2 = mouseX;
			dragBoxY2 = mouseY;
		}
	}

	@Override
	public void onDragMap(int mouseX, int mouseY, int moveX, int moveY) {
		super.onDragMap(mouseX, mouseY, moveX, moveY);
		if (dragBoxX != dragBoxX2 || dragBoxY != dragBoxY2) {
			dragBoxX -= moveX;
			dragBoxY -= moveY;
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		if (subscreens.empty()) {
			if (mouseButton == 0) {
				dragBoxX = mouseX;
				dragBoxY = mouseY;
				dragBoxX2 = mouseX;
				dragBoxY2 = mouseY;
			}
		}
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int mouseButton) {
		super.mouseReleased(mouseX, mouseY, mouseButton);
		if (subscreens.empty() && mouseButton == 0) {
			if (dragBoxX != dragBoxX2 || dragBoxY != dragBoxY2) {
				MapHandler.onReleaseDragBox(width, height, dragBoxX, dragBoxY, dragBoxX2, dragBoxY2);
			} else {
				MapHandler.onClick(width, height, mouseX, mouseY);
			}
			dragBoxX = 0;
			dragBoxY = 0;
			dragBoxX2 = 0;
			dragBoxY2 = 0;
		}
	}

	@Override
	public void draw(int mouseX, int mouseY, float partialTicks) {
		super.draw(mouseX, mouseY, partialTicks);
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
		if (dragBoxX != dragBoxX2 || dragBoxY != dragBoxY2) {
			Gui.drawRect(dragBoxX, dragBoxY, dragBoxX2, dragBoxY2, 0x28E0E0E0);
			int x = dragBoxX < dragBoxX2 ? dragBoxX : dragBoxX2;
			int y = dragBoxY < dragBoxY2 ? dragBoxY : dragBoxY2;
			Util.drawBorder(x, y, Math.abs(dragBoxX2 - dragBoxX), Math.abs(dragBoxY2 - dragBoxY), 1, 0xFFD2D2D2);
		}
	}

	public void setChosen(List<Pointer> l) {
		boolean ctrl = GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSprint);
		boolean shift = GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSneak);
		if (ctrl && shift) {
			l.forEach(e -> {
				if (!chosen.remove(e)) {
					chosen.add(e);
				}
			});
		} else if (ctrl) {
			l.forEach(e -> {
				chosen.remove(e);
			});
		} else if (shift) {
			l.forEach(e -> {
				if (!chosen.contains(e)) {
					chosen.add(e);
				}
			});
		} else {
			chosen = l;
		}
		chosen = chosen.size() > 10 ? chosen.subList(0, 10) : chosen;
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
