package com.pinball3d.zone.sphinx.elite;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.lwjgl.input.Keyboard;

import com.pinball3d.zone.sphinx.elite.DropDownList.ButtonBar;
import com.pinball3d.zone.sphinx.elite.DropDownList.DividerBar;
import com.pinball3d.zone.sphinx.elite.DropDownList.FolderBar;
import com.pinball3d.zone.sphinx.elite.MenuBar.Menu;
import com.pinball3d.zone.sphinx.elite.MouseHandler.MouseType;
import com.pinball3d.zone.sphinx.elite.PanelGroup.Edge;
import com.pinball3d.zone.sphinx.elite.PanelGroup.Rect;
import com.pinball3d.zone.sphinx.elite.PanelGroup.Side;
import com.pinball3d.zone.sphinx.elite.panels.Panel;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;

public class EliteMainwindow extends GuiScreen {
	public static final ResourceLocation ELITE = new ResourceLocation("zone:textures/gui/elite/elite.png");

	private int lastX, lastY;
	private MenuBar menuBar;
	private ButtomBar buttomBar;
	private DropDownList dropDownList;
	private IFocus focus;
	private List<PanelGroup> panels = new ArrayList<PanelGroup>();
	private Drag drag;
	private boolean inited;
	private List<Set<PanelGroup>> draggingPanels;

	public static EliteMainwindow getWindow() {
		GuiScreen s = Minecraft.getMinecraft().currentScreen;
		if (s instanceof EliteMainwindow) {
			return (EliteMainwindow) s;
		}
		return null;
	}

	private void applyMenu() {
		menuBar = new MenuBar(this);
		menuBar.addMenu(new Menu(this, I18n.format("elite.menu.view"), 'v')
				.addBar(new ButtonBar("甲乙丙丁戊己庚AbCdEf", "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))
				.addBar(new ButtonBar("甲乙丙丁戊己庚AbCdEf", "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))
				.addBar(new ButtonBar("甲乙丙丁戊己庚AbCdEf", "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))
				.addBar(new ButtonBar("甲乙丙丁戊己庚AbCdEf", "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))
				.addBar(new ButtonBar("甲乙丙丁戊己庚AbCdEf", "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))
				.addBar(new ButtonBar("甲乙丙丁戊己庚AbCdEf", "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")));
		menuBar.addMenu(new Menu(this, I18n.format("elite.menu.window"), 'w').addBar(new FolderBar("KKK")
				.addBar(new ButtonBar("aB", "Shift+Z")).addBar(new DividerBar()).addBar(new ButtonBar("AA", "AA"))));
		menuBar.addMenu(new Menu(this, I18n.format("elite.menu.help"), 'h')
				.addBar(new ButtonBar("甲乙丙丁戊己庚AbCdEf", "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")));
	}

	private void applyPanels() {
		PanelGroup g = new PanelGroup(this, 0, 28, getWidth(), getHeight() - 49);
//		g.addPanel(new PanelMap(this, g));
//		g.addPanel(new PanelMap(this, g));
//		g.addPanel(new PanelMap(this, g));
//		g.addPanel(new PanelMap(this, g));
//		g.addPanel(new PanelMap(this, g));
//		g.addPanel(new PanelMap(this, g));
		g.addPanel(new Panel(this, g, "1"));
		g.addPanel(new Panel(this, g, "2"));
		g.addPanel(new Panel(this, g, "3"));
		g.addPanel(new Panel(this, g, "4"));
		g.addPanel(new Panel(this, g, "5"));
		panels.add(g);
	}

	@Override
	public void initGui() {
		if (!inited) {
			applyMenu();
			applyPanels();
			buttomBar = new ButtomBar(this);
			inited = true;
		}
	}

	@Override
	public void drawScreen(int mX, int mY, float partialTicks) {
		float xScale = width * 1.0F / getWidth();
		float yScale = height * 1.0F / getHeight();
		int mouseX = MouseHandler.getX();
		int mouseY = MouseHandler.getY();
		if (mouseX != this.lastX || mouseY != this.lastY) {
			int moveX = mouseX - this.lastX;
			int moveY = mouseY - this.lastY;
			onMouseMoved(this.lastX = mouseX, this.lastY = mouseY, moveX, moveY);
		}
		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 0, 50F);
		GlStateManager.scale(xScale, yScale, 1.0F);
		EliteRenderHelper.drawRect(0, 0, getWidth(), getHeight(), 0xFF282828);
		menuBar.doRender(mouseX, mouseY);
		buttomBar.doRender(mouseX, mouseY);
		panels.forEach(e -> e.doRenderPre(mouseX, mouseY));
		panels.forEach(e -> e.doRender(mouseX, mouseY));
		panels.forEach(e -> e.doRenderPost(mouseX, mouseY));
		if (dropDownList != null) {
			dropDownList.doRender(mouseX, mouseY);
		}
		updateMouse(mouseX, mouseY);
		MouseHandler.renderMouse();
		GlStateManager.popMatrix();
	}

	public void updateMouse(int mouseX, int mouseY) {
		MouseType type = null;
		if (draggingPanels != null) {
			if (!draggingPanels.get(Side.UP.ordinal()).isEmpty()
					|| !draggingPanels.get(Side.DOWN.ordinal()).isEmpty()) {
				type = MouseType.RESIZE_S;
			}
			if (!draggingPanels.get(Side.LEFT.ordinal()).isEmpty()
					|| !draggingPanels.get(Side.RIGHT.ordinal()).isEmpty()) {
				type = type == MouseType.RESIZE_S ? MouseType.MOVE : MouseType.RESIZE_W;
			}
		} else {
			for (PanelGroup p : panels) {
				Rect rect = p.getRect();
				for (Side s : Side.values()) {
					Edge edge = rect.getEdge(s);
					if (s.isRow()) {
						if (edge.getY1() == 28 || Math.abs(edge.getY1() - (getHeight() - 21)) < 1) {
							continue;
						}
						if (mouseX >= edge.getX1() && mouseX <= edge.getX2() && mouseY >= edge.getY1() - 5
								&& mouseY <= edge.getY1() + 5) {
							if (type == null) {
								type = MouseType.RESIZE_S;
							} else if (type == MouseType.RESIZE_W) {
								type = MouseType.MOVE;
							}
						}
					} else {
						if (edge.getX1() == 0 || Math.abs(edge.getX1() - getWidth()) < 1) {
							continue;
						}
						if (mouseX >= edge.getX1() - 5 && mouseX <= edge.getX1() + 5 && mouseY >= edge.getY1()
								&& mouseY <= edge.getY2()) {
							if (type == null) {
								type = MouseType.RESIZE_W;
							} else if (type == MouseType.RESIZE_S) {
								type = MouseType.MOVE;
							}
						}
					}
				}
			}
		}
		MouseHandler.changeMouse(type);
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
			if (drag != null) {
				drag.stop(true);
				drag = null;
			} else {
				menuBar.onPressAlt();
			}
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

	public DropDownList getDropDownList() {
		return dropDownList;
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

	public int getWidth() {
		return mc.displayWidth;
	}

	public int getHeight() {
		return mc.displayHeight;
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
	protected void mouseClicked(int mX, int mY, int mouseButton) throws IOException {
		if (mouseButton != 0 && mouseButton != 1) {
			return;
		}
		int mouseX = MouseHandler.getX();
		int mouseY = MouseHandler.getY();
		if (dropDownList != null) {
			if (!dropDownList.mouseClicked(mouseX, mouseY, mouseButton)) {
				dropDownList = null;
				menuBar.onListClosed();
			} else {
				return;
			}
		} else {
			menuBar.mouseClicked(mouseX, mouseY, mouseButton);
		}
		if (mouseButton == 0) {
			for (PanelGroup p : panels) {
				Rect rect = p.getRect();
				for (Side s : Side.values()) {
					Edge edge = rect.getEdge(s);
					if (s.isRow()) {
						if (edge.getY1() == 28 || Math.abs(edge.getY1() - (getHeight() - 21)) < 1) {
							continue;
						}
						if (mouseX >= edge.getX1() && mouseX <= edge.getX2() && mouseY >= edge.getY1() - 5
								&& mouseY <= edge.getY1() + 5) {
							onStartDragPanelSide(mouseX, mouseY);
							return;
						}
					} else {
						if (edge.getX1() == 0 || Math.abs(edge.getX1() - getWidth()) < 1) {
							continue;
						}
						if (mouseX >= edge.getX1() - 5 && mouseX <= edge.getX1() + 5 && mouseY >= edge.getY1()
								&& mouseY <= edge.getY2()) {
							onStartDragPanelSide(mouseX, mouseY);
							return;
						}
					}
				}
			}
		}
		for (PanelGroup g : panels) {
			Drag d = g.onMouseClicked(mouseX, mouseY, mouseButton);
			if (mouseButton == 0 && d != null && drag == null) {
				drag = d;
				break;
			}
		}
	}

	private void onStartDragPanelSide(int mouseX, int mouseY) {
		if (drag == null) {
			drag = new Drag((x, y, mX, mY) -> {
				if (draggingPanels == null) {
					calcResizePanel(mouseX, mouseY);
				}
				onResizePanel(mX, mY);
			}, (cancel) -> {
				draggingPanels = null;
			});
		}
	}

	private void calcResizePanel(int mouseX, int mouseY) {
		draggingPanels = Arrays.asList(new HashSet<PanelGroup>(), new HashSet<PanelGroup>(), new HashSet<PanelGroup>(),
				new HashSet<PanelGroup>());
		Edge r = null, c = null;
		for (PanelGroup p : panels) {
			Rect rect = p.getRect();
			for (Side s : Side.values()) {
				Edge edge = rect.getEdge(s);
				if (s.isRow()) {
					if (edge.getY1() == 28 || Math.abs(edge.getY1() - (getHeight() - 21)) < 1) {
						continue;
					}
					if (mouseX >= edge.getX1() - 5 && mouseX <= edge.getX2() + 5 && mouseY >= edge.getY1() - 5
							&& mouseY <= edge.getY1() + 5) {
						Set<PanelGroup> set = draggingPanels.get(s.ordinal());
						if (set.isEmpty() && r == null && (c == null || c.isCross(edge))) {
							r = edge;
							set.add(p);
						}
					}
				} else {
					if (edge.getX1() == 0 || Math.abs(edge.getX1() - getWidth()) < 1) {
						continue;
					}
					if (mouseX >= edge.getX1() - 5 && mouseX <= edge.getX1() + 5 && mouseY >= edge.getY1() - 5
							&& mouseY <= edge.getY2() + 5) {
						Set<PanelGroup> set = draggingPanels.get(s.ordinal());
						if (set.isEmpty() && c == null && (r == null || r.isCross(edge))) {
							c = edge;
							set.add(p);
						}
					}
				}
			}
		}
		for (int i = 0; i < panels.size(); i++) {
			PanelGroup p = panels.get(i);
			Rect rect = p.getRect();
			for (Side s : Side.values()) {
				Edge edge = rect.getEdge(s);
				if (edge.isRow()) {
					if (r != null) {
						Edge e = edge.connect(r);
						if (e != null) {
							draggingPanels.get(s.ordinal()).add(p);
							if (e.getLength() > r.getLength()) {
								r = e;
								i = 0;
								break;
							}
						}
					}
				} else {
					if (c != null) {
						Edge e = edge.connect(c);
						if (e != null) {
							draggingPanels.get(s.ordinal()).add(p);
							if (e.getLength() > c.getLength()) {
								c = e;
								i = 0;
								break;
							}
						}
					}
				}
			}
		}
	}

	private void onResizePanel(int moveX, int moveY) {
		for (Side s : Side.values()) {
			draggingPanels.get(s.ordinal()).forEach(e -> {
				switch (s) {
				case UP:
					e.setYF(e.getYF() + moveY);
					e.setHeightF(e.getHeightF() - moveY);
					break;
				case DOWN:
					e.setHeightF(e.getHeightF() + moveY);
					break;
				case LEFT:
					e.setXF(e.getXF() + moveX);
					e.setWidthF(e.getWidthF() - moveX);
					break;
				case RIGHT:
					e.setWidthF(e.getWidthF() + moveX);
					break;
				}
			});
		}
	}

	@Override
	protected void mouseReleased(int mX, int mY, int mouseButton) {
		if (mouseButton != 0 && mouseButton != 1) {
			return;
		}
		int mouseX = MouseHandler.getX();
		int mouseY = MouseHandler.getY();
		if (mouseButton == 0) {
			if (drag != null) {
				drag.stop(false);
				drag = null;
			} else if (!menuBar.mouseReleased(mouseX, mouseY, mouseButton)) {
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
				}
			}
		}
	}

	public void refreshPanelSize() {
		float maxX = 0, maxY = 0;
		for (PanelGroup p : panels) {
			float x2 = p.getXF() + p.getWidthF();
			float y2 = p.getYF() + p.getHeightF();
			if (x2 > maxX) {
				maxX = x2;
			}
			if (y2 > maxY) {
				maxY = y2;
			}
		}
		float xScale = getWidth() / maxX;
		float yScale = (getHeight() - 49) / (maxY - 28);
		for (PanelGroup p : panels) {
			p.setXF(p.getXF() * xScale);
			p.setYF((p.getYF() - 28) * yScale + 28);
			p.setWidthF(p.getWidthF() * xScale);
			p.setHeightF(p.getHeightF() * yScale);
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	public void onGuiClosed() {
		MouseHandler.changeMouse(null);
		super.onGuiClosed();
	}

	@Override
	public void onResize(Minecraft mc, int w, int h) {
		super.onResize(mc, w, h);
		refreshPanelSize();
	}
}
