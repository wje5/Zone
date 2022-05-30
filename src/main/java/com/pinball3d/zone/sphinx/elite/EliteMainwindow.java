package com.pinball3d.zone.sphinx.elite;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.google.gson.JsonObject;
import com.pinball3d.zone.network.ConnectHelperClient;
import com.pinball3d.zone.network.ConnectionHelper.Type;
import com.pinball3d.zone.network.NetworkHandler;
import com.pinball3d.zone.network.elite.MessageCloseElite;
import com.pinball3d.zone.sphinx.elite.DropDownList.ButtonBar;
import com.pinball3d.zone.sphinx.elite.EliteConfigHandler.WorkspaceConfig;
import com.pinball3d.zone.sphinx.elite.EliteConfigHandler.WorkspaceConfig.StateData;
import com.pinball3d.zone.sphinx.elite.MenuBar.Menu;
import com.pinball3d.zone.sphinx.elite.MouseHandler.MouseType;
import com.pinball3d.zone.sphinx.elite.PanelGroup.Edge;
import com.pinball3d.zone.sphinx.elite.PanelGroup.Rect;
import com.pinball3d.zone.sphinx.elite.PanelGroup.Side;
import com.pinball3d.zone.sphinx.elite.panels.PanelInfo;
import com.pinball3d.zone.sphinx.elite.panels.PanelMap;
import com.pinball3d.zone.sphinx.elite.panels.PanelRegistry;
import com.pinball3d.zone.sphinx.elite.ui.core.Panel;
import com.pinball3d.zone.tileentity.TETerminal;
import com.pinball3d.zone.util.Pair;
import com.pinball3d.zone.util.WorldPos;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.world.WorldEvent.Unload;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(value = net.minecraftforge.fml.relauncher.Side.CLIENT)
public class EliteMainwindow extends GuiScreen {
	public static final ResourceLocation ELITE = new ResourceLocation("zone:textures/gui/elite/elite.png");

	private int dragMinX, dragMaxX, dragMinY, dragMaxY;
	private MenuBar menuBar;
	private ButtomBar buttomBar;
	private IDropDownList dropDownList;
	private IFocus focus;
	private List<PanelGroup> panels = new ArrayList<PanelGroup>();
	private Drag drag;
	private boolean inited, isAlt;
	private List<Set<PanelGroup>> draggingPanels;
	private PanelGroup focusPanel;
	private WorldPos terminalPos;
	private UUID network;
	private WorkspaceConfig config;
	public boolean enableDebugMode;

	public EliteMainwindow(WorldPos terminalPos, UUID network) {
		this.terminalPos = terminalPos;
		this.network = network;
		this.config = EliteConfigHandler.getConfig(network);
		ConnectHelperClient.getInstance().requestTerminal(terminalPos, network, Type.NETWORKUUID, Type.NETWORKPOS);
	}

	public static EliteMainwindow getWindow() {
		GuiScreen s = Minecraft.getMinecraft().currentScreen;
		if (s instanceof EliteMainwindow) {
			return (EliteMainwindow) s;
		}
		return null;
	}

	public WorldPos getTerminalPos() {
		return terminalPos;
	}

	private void applyMenu() {
		menuBar = new MenuBar(this);
		menuBar.addMenu(new Menu(this, new FormattedString(I18n.format("elite.menu.view")), 'v')
				.addBar(new ButtonBar(new FormattedString(I18n.format("elite.menu.view.show_extra_contents")),
						new FormattedString("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))));
//		menuBar.addMenu(new Menu(this, new FormattedString(I18n.format("elite.menu.window")), 'w')
//				.addBar(new FolderBar(new FormattedString(("KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK")))
//						.addBar(new ButtonBar(new FormattedString("aBCdDDDDDDDD"), new FormattedString("Shift+Z")))
//						.addBar(new FolderBar(new FormattedString("KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK"))
//								.addBar(new ButtonBar(new FormattedString("aBCdDDDDDDDD"),
//										new FormattedString("Shift+Z")))
//								.addBar(new DividerBar())
//								.addBar(new ButtonBar(new FormattedString("AA"), new FormattedString("AA"))))));
//		menuBar.addMenu(new Menu(this, new FormattedString(I18n.format("elite.menu.tool")), 'h'));
//		menuBar.addMenu(new Menu(this, new FormattedString(I18n.format("elite.menu.option")), 'h'));
//		menuBar.addMenu(new Menu(this, new FormattedString(I18n.format("elite.menu.help")), 'h')
//				.addBar(new ButtonBar(new FormattedString("甲乙丙丁戊己庚AbCdEf"),
//						new FormattedString("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))));
	}

	@Override
	public void initGui() {
		if (!inited) {
			mc.renderGlobal.setWorldAndLoadRenderers(null);
			initState();
			inited = true;
		}
		Keyboard.enableRepeatEvents(true);
	}

	public void initState() {
		applyMenu();
		if (config.init) {
			config.stateData.panels.forEach(e -> {
				PanelGroup g = new PanelGroup(this, e.value().get(0) * getWidth(),
						e.value().get(1) * (getHeight() - 49) + 28, e.value().get(2) * getWidth(),
						e.value().get(3) * (getHeight() - 49));
				e.key().forEach(pair -> g.addPanel(PanelRegistry.createPanel(this, g, pair.key(), pair.value())));
				panels.add(g);
			});
		} else {
			PanelGroup g = new PanelGroup(this, 0, 28, getWidth() - 200, getHeight() - 49);
			g.addPanel(new PanelMap(this, g));
			panels.add(g);

			PanelGroup g2 = new PanelGroup(this, getWidth() - 200, 28, 200, getHeight() - 49);
			g2.addPanel(new PanelInfo(getWindow(), g2));
			panels.add(g2);
		}
		config.init = true;
		buttomBar = new ButtomBar(this);
	}

	@Override
	public void drawScreen(int mX, int mY, float partialTicks) {
		if (terminalPos.getDim() != mc.player.dimension
				|| mc.player.getDistance(terminalPos.getPos().getX() + 0.5F, terminalPos.getPos().getY() + 0.5F,
						terminalPos.getPos().getZ() + 0.5F) > 16F
				|| !(terminalPos.getTileEntity() instanceof TETerminal)) {
			mc.displayGuiScreen(null);
			return;
		}
		GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		updateMouse();
		int mouseX = MouseHandler.getX();
		int mouseY = MouseHandler.getY();
		int moveX = MouseHandler.getDX();
		int moveY = MouseHandler.getDY();
		if (moveX != 0 || moveY != 0) {
			onMouseMoved(mouseX, mouseY, moveX, moveY);
		}

		GlStateManager.matrixMode(GL11.GL_PROJECTION);
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();
		GlStateManager.ortho(0, getWidth(), getHeight(), 0, 1000.0D, 3000.0D);
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);

		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();
		GlStateManager.translate(0, 0, -2000F);
		EliteRenderHelper.drawRect(0, 0, getWidth(), getHeight(), Color.BACKGROUND);
		menuBar.doRender(mouseX, mouseY);
		buttomBar.doRender(mouseX, mouseY);
		panels.forEach(e -> e.doRenderPre(mouseX, mouseY, partialTicks));
		panels.forEach(e -> e.doRender(mouseX, mouseY, partialTicks));
		panels.forEach(e -> e.doRenderPost(mouseX, mouseY, partialTicks));
		if (dropDownList != null) {
			dropDownList.doRender(mouseX, mouseY);
		}
		MouseHandler.renderMouse();
		GlStateManager.popMatrix();
		GlStateManager.matrixMode(GL11.GL_PROJECTION);
		GlStateManager.popMatrix();
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
	}

	public void updateMouse() {
		MouseHandler.updateDelta();
		int mouseX = MouseHandler.getX();
		int mouseY = MouseHandler.getY();
		int scrollDistance = MouseHandler.getDWheel();
		if (scrollDistance != 0) {
			onMouseScrolled(MouseHandler.getX(), MouseHandler.getY(), scrollDistance);
		}
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
		} else if (dropDownList != null) {
			type = dropDownList.getMouseType(mouseX, mouseY);
		} else if (drag == null && MouseHandler.isMouseInsideWindow()) {
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
		Minecraft mc = Minecraft.getMinecraft();
		if (mouseX <= 0 || mouseX >= mc.displayWidth - 1 || mouseY <= 1 || mouseY >= mc.displayHeight) {
			type = null;
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
				Iterator<PanelGroup> it = panels.iterator();
				while (it.hasNext()) {
					Iterator<Panel> it2 = it.next().getPanels().iterator();
					while (it2.hasNext()) {
						if (!it2.next().canQuit()) {
							return;
						}
					}
				}
				super.keyTyped(typedChar, keyCode);
			}
		} else if (keyCode == Keyboard.KEY_LMENU || keyCode == Keyboard.KEY_RMENU) {
			isAlt = true;
			boolean flag = false;
			if (drag != null) {
				drag.stop(true);
				setDrag(null);
				flag = true;
			}
			if (!flag) {
				menuBar.onPressAlt();
			}
			if (dropDownList != null) {
				dropDownList = null;
			}
		} else if (dropDownList != null) {
			dropDownList.keyTyped(typedChar, keyCode);
		} else if (focus != null) {
			focus.keyTyped(typedChar, keyCode);
		} else {
			// process hotkey
			if (typedChar == 'g') {
				enableDebugMode = !enableDebugMode;
			}
			if (Loader.isModLoaded("jei")) {
				if (typedChar == 'a') {
//					JEIHandler.showJEI(new ItemStack(ItemLoader.advenced_circuit_board), true);
				}
			}
		}

	}

	public void setFocus(IFocus focus) {
		this.focus = focus;
	}

	public void setDropDownList(IDropDownList dropDownList) {
		this.dropDownList = dropDownList;
	}

	public IDropDownList getDropDownList() {
		return dropDownList;
	}

	public void quitMenuBar() {
		dropDownList = null;
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

	public boolean isAlt() {
		return isAlt;
	}

	public PanelGroup getFocusPanel() {
		if (!panels.contains(focusPanel)) {
			focusPanel = panels.isEmpty() ? null : panels.get(0);
		}
		return focusPanel;
	}

	public PanelMap getMapPanel() {
		for (PanelGroup i : panels) {
			for (Panel j : i.getPanels()) {
				if (j instanceof PanelMap) {
					return (PanelMap) j;
				}
			}
		}
		return null;
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
		if (drag != null) {
			drag.stop(true);
			setDrag(null);
		}
		int mouseX = MouseHandler.getX();
		int mouseY = MouseHandler.getY();
		if (dropDownList != null) {
			Drag drag = dropDownList.mouseClicked(mouseX, mouseY, mouseButton);
			if (drag == null) {
				dropDownList = null;
				menuBar.onListClosed();
			}
			return;
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
			if (d != null) {
				setDrag(d);
				focusPanel = g;
				break;
			}
		}
	}

	private void setDrag(Drag drag) {
		if (this.drag != null && this.drag.grab) {
			MouseHandler.ungrab();
		}
		this.drag = drag;
		if (drag != null && drag.grab) {
			MouseHandler.grab();
		}
	}

	public void onMouseScrolled(int mouseX, int mouseY, int distance) {
		for (PanelGroup g : panels) {
			g.onMouseScrolled(mouseX, mouseY, distance);
		}
	}

	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();

	}

	private void onStartDragPanelSide(int mouseX, int mouseY) {
		setDrag(new Drag(0, (x, y, mX, mY) -> {
			if (draggingPanels == null) {
				calcResizePanel(mouseX, mouseY);
			}
			onResizePanel(x, y, mX, mY);
		}, (cancel) -> {
			draggingPanels = null;
			dragMinX = 0;
			dragMaxX = 0;
			dragMinY = 0;
			dragMaxY = 0;
		}));
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

	private void onResizePanel(int mouseX, int mouseY, int moveX, int moveY) {
		int originX = mouseX - moveX, originY = mouseY - moveY;
		int originMoveX = moveX, originMoveY = moveY;
		if (dragMinX != 0) {
			moveX = Math.max(dragMinX, mouseX) - Math.max(dragMinX, originX);
		}
		if (dragMaxX != 0) {
			int t = Math.min(dragMaxX, mouseX) - Math.min(dragMaxX, originX);
			if (t != originMoveX) {
				moveX = t;
			}
		}
		if (dragMinY != 0) {
			moveY = Math.max(dragMinY, mouseY) - Math.max(dragMinY, originY);
		}
		if (dragMaxY != 0) {
			int t = Math.min(dragMaxY, mouseY) - Math.min(dragMaxY, originY);
			if (t != originMoveY) {
				moveY = t;
			}
		}
		int mX = moveX, mY = moveY;
		for (Side s : Side.values()) {
			for (PanelGroup e : draggingPanels.get(s.ordinal())) {
				switch (s) {
				case UP:
					int t = (int) (e.getHeightF() - mY);
					if (t < PanelGroup.MIN_PANEL_HEIGHT) {
						mY -= PanelGroup.MIN_PANEL_HEIGHT - t;
					}
					break;
				case DOWN:
					t = (int) (e.getHeightF() + mY);
					if (t < PanelGroup.MIN_PANEL_HEIGHT) {
						mY += PanelGroup.MIN_PANEL_HEIGHT - t;
					}
					break;
				case LEFT:
					t = (int) (e.getWidthF() - mX);
					if (t < PanelGroup.MIN_PANEL_WIDTH) {
						mX -= PanelGroup.MIN_PANEL_WIDTH - t;
					}
					break;
				case RIGHT:
					t = (int) (e.getWidthF() + mX);
					if (t < PanelGroup.MIN_PANEL_WIDTH) {
						mX += PanelGroup.MIN_PANEL_WIDTH - t;
					}
					break;
				}
			}
		}
		if (dragMaxX == 0 && mX < moveX) {
			dragMaxX = originX + mX;
		} else if (dragMinX == 0 && mX > moveX) {
			dragMinX = originX + mX;
		}
		if (dragMaxY == 0 && mY < moveY) {
			dragMaxY = originY + mY;
		} else if (dragMinY == 0 && mY > moveY) {
			dragMinY = originY + mY;
		}
		for (Side s : Side.values()) {
			for (PanelGroup e : draggingPanels.get(s.ordinal())) {
				switch (s) {
				case UP:
					e.setYF(e.getYF() + mY);
					e.setHeightF(e.getHeightF() - mY);
					break;
				case DOWN:
					e.setHeightF(e.getHeightF() + mY);
					break;
				case LEFT:
					e.setXF(e.getXF() + mX);
					e.setWidthF(e.getWidthF() - mX);
					break;
				case RIGHT:
					e.setWidthF(e.getWidthF() + mX);
					break;
				}
			}
		}
	}

	@Override
	protected void mouseReleased(int mX, int mY, int mouseButton) {
		if (drag != null) {
			drag.stop(false);
			setDrag(null);
		}
		int mouseX = MouseHandler.getX();
		int mouseY = MouseHandler.getY();

		if (!menuBar.mouseReleased(mouseX, mouseY, mouseButton)) {
			if (dropDownList != null) {
				if (!dropDownList.mouseReleased(mouseX, mouseY, mouseButton)) {
					dropDownList = null;
					menuBar.onListClosed();
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
		panels.forEach(e -> e.getPanels().forEach(p -> p.close()));
		mc.renderGlobal.setWorldAndLoadRenderers(mc.world);
		MouseHandler.changeMouse(null);
		Keyboard.enableRepeatEvents(false);
		NetworkHandler.instance.sendToServer(new MessageCloseElite(terminalPos));
		writeStateDataToConfig();
		EliteConfigHandler.saveConfig(network);
		super.onGuiClosed();
	}

	public void writeStateDataToConfig() {
		config.stateData = new StateData();
		for (PanelGroup g : panels) {
			config.stateData.panels.add(new Pair<List<Pair<String, JsonObject>>, List<Float>>(
					g.getPanels().stream().map(e -> new Pair<String, JsonObject>(e.getId(), e.writeDataToJson()))
							.collect(Collectors.toList()),
					Arrays.asList(g.getXF() / getWidth(), (g.getYF() - 28) / (getHeight() - 49),
							g.getWidthF() / getWidth(), g.getHeightF() / (getHeight() - 49))));
		}
	}

	@Override
	public void onResize(Minecraft mc, int w, int h) {
		super.onResize(mc, w, h);
		if (dropDownList != null) {
			dropDownList = null;
			menuBar.onListClosed();
		}
		refreshPanelSize();
	}

	@SubscribeEvent
	public static void onUnload(Unload event) {
		if (Minecraft.getMinecraft().currentScreen instanceof EliteMainwindow) {
			((EliteMainwindow) Minecraft.getMinecraft().currentScreen).onGuiClosed();
		}
	}
}
