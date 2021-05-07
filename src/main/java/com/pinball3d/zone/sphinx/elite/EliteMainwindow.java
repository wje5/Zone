package com.pinball3d.zone.sphinx.elite;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;

import com.pinball3d.zone.sphinx.elite.DropDownList.ButtonBar;
import com.pinball3d.zone.sphinx.elite.DropDownList.DividerBar;
import com.pinball3d.zone.sphinx.elite.DropDownList.FolderBar;
import com.pinball3d.zone.sphinx.elite.MenuBar.Menu;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;

public class EliteMainwindow extends GuiScreen {
	public static final ResourceLocation ELITE = new ResourceLocation("zone:textures/gui/elite/elite.png");

	private int mouseX, mouseY;
	private MenuBar menuBar;
	private DropDownList dropDownList;
	private IFocus focus;
	private List<PanelGroup> panels = new ArrayList<PanelGroup>();
	private Drag drag;

	public EliteMainwindow() {
		applyMenu();
	}

	private void applyMenu() {
		menuBar = new MenuBar(this);
		menuBar.addMenu(new Menu(this, I18n.format("elite.menu.view"), 'v')
				.addBar(new ButtonBar("甲乙丙丁戊己庚AbCdEf", "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")));
		menuBar.addMenu(new Menu(this, I18n.format("elite.menu.window"), 'w').addBar(new FolderBar("KKK")
				.addBar(new ButtonBar("aB", "Shift+Z")).addBar(new DividerBar()).addBar(new ButtonBar("AA", "AA"))));
		menuBar.addMenu(new Menu(this, I18n.format("elite.menu.help"), 'h')
				.addBar(new ButtonBar("甲乙丙丁戊己庚AbCdEf", "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")));
		PanelGroup g = new PanelGroup(this, 100, 100, 100, 100);
		g.addPanel(new Panel(this, g, "EliteMainWindow"));
		g.addPanel(new Panel(this, g, "PanelGroup"));
		g.addPanel(new Panel(this, g, "DropDownList"));
		panels.add(g);
		panels.add(new PanelGroup(this, 300, 100, 100, 100));
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		if (mouseX != this.mouseX || mouseY != this.mouseY) {
			int moveX = mouseX - this.mouseX;
			int moveY = mouseY - this.mouseY;
			onMouseMoved(this.mouseX = mouseX, this.mouseY = mouseY, moveX, moveY);
		}
		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 0, 50F);
		EliteRenderHelper.drawRect(0, 0, width, height, 0xFF282828);
		menuBar.doRender(mouseX, mouseY);
		panels.forEach(e -> e.doRenderPre(mouseX, mouseY));
		panels.forEach(e -> e.doRender(mouseX, mouseY));
		panels.forEach(e -> e.doRenderPost(mouseX, mouseY));
		if (dropDownList != null) {
			dropDownList.doRender(mouseX, mouseY);
		}
		GlStateManager.popMatrix();
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (keyCode == Keyboard.KEY_ESCAPE) {
			if (dropDownList != null) {
				if (dropDownList.onQuit()) {
					dropDownList = null;
				}
			} else if (menuBar.onQuit()) {
				super.keyTyped(typedChar, keyCode);
			}
		} else if (keyCode == Keyboard.KEY_LMENU) {
			menuBar.onPressAlt();
			if (dropDownList != null) {
				dropDownList = null;
			}
		} else if (dropDownList != null) {
			dropDownList.keyTyped(typedChar, keyCode);
		} else {
			if (Loader.isModLoaded("jei")) {
				if (typedChar == 'a') {
//					JEIHandler.showJEI(new ItemStack(ItemLoader.advenced_circuit_board), true);
				}
			}
			if (focus != null) {
				focus.keyTyped(typedChar, keyCode);
			}

		}
	}

	public void setFocus(IFocus focus) {
		this.focus = focus;
	}

	public void setDropDownList(DropDownList dropDownList) {
		this.dropDownList = dropDownList;
	}

	public void quitMenuBar() {
		this.dropDownList = null;
		menuBar.onQuit();
	}

	public MenuBar getMenuBar() {
		return menuBar;
	}

	public List<PanelGroup> getPanels() {
		return panels;
	}

	private void onMouseMoved(int mouseX, int mouseY, int moveX, int moveY) {
		menuBar.onMouseMoved(mouseX, mouseY, moveX, moveY);
		if (dropDownList != null) {
			dropDownList.onMouseMoved(mouseX, mouseY, moveX, moveY);
		}
		panels.forEach(e -> e.onMouseMoved(mouseX, mouseY, moveX, moveY));
		if (drag != null) {
			drag.drag(mouseX, mouseY, moveX, moveY);
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		if (mouseButton != 0 && mouseButton != 1) {
			return;
		}
		if (dropDownList != null) {
			if (!dropDownList.mouseClicked(mouseX, mouseY, mouseButton)) {
				dropDownList = null;
				menuBar.onListClosed();
			} else {
				return;
			}
		} else {
			menuBar.mouseClicked(mouseX, mouseY, mouseButton);
			for (PanelGroup g : panels) {
				Drag d = g.onMouseClicked(mouseX, mouseY, mouseButton);
				if (mouseButton == 0 && d != null) {
					drag = d;
					break;
				}
			}
		}
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int mouseButton) {
		if (!menuBar.mouseReleased(mouseX, mouseY, mouseButton)) {
			if (dropDownList != null) {
				if (!dropDownList.mouseReleased(mouseX, mouseY, mouseButton)) {
					dropDownList = null;
					menuBar.onListClosed();
				} else {
					return;
				}
			} else {
				for (PanelGroup g : panels) {
					g.onMouseReleased(mouseX, mouseY, mouseButton);
				}
				if (mouseButton == 0) {
					if (drag != null) {
						drag.stop();
					}
					drag = null;
				}
			}
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
}
