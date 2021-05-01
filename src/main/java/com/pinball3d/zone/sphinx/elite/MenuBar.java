package com.pinball3d.zone.sphinx.elite;

import java.util.ArrayList;
import java.util.List;

public class MenuBar {
	private EliteMainwindow parent;
	private List<Menu> list = new ArrayList<Menu>();
	private int chosenIndex = -1;

	public MenuBar(EliteMainwindow parent) {
		this.parent = parent;
	}

	public MenuBar addMenu(Menu menu) {
		list.add(menu);
		return this;
	}

	public void doRender(int mouseX, int mouseY) {
		float x = 1;
		EliteRenderHelper.drawRect(0, 0, parent.width, 7, 0xFF535353);
		for (Menu m : list) {
			x += m.doRender(x, mouseX, mouseY);
		}
	}

	public EliteMainwindow getParent() {
		return parent;
	}

	public void setChosenIndex(int chosenIndex) {
		this.chosenIndex = chosenIndex < list.size() ? chosenIndex : list.size();
	}

	public static class Menu {
		private String name;

		public Menu(String name) {
			this.name = name;
		}

		public float doRender(float x, int mouseX, int mouseY) {
			float w = FontHelper.getStringWidth(name) + 3.25F;
			if (mouseY <= 7 && mouseX >= x && mouseX <= x + w) {
				EliteRenderHelper.drawTexture(EliteMainwindow.ELITE, x, 1, 0, 20, (int) (w * 4 - 3), 20, 0.25F);
				EliteRenderHelper.drawTexture(EliteMainwindow.ELITE, x + w - 0.75F, 1, 253, 20, 3, 20, 0.25F);
			}
			FontHelper.renderText(x + 2, 2, name, 0xFFC7C7C7);
			return w;
		}

		public String getName() {
			return name;
		}
	}
}
