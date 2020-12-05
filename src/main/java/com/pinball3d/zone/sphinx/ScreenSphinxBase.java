package com.pinball3d.zone.sphinx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.UUID;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.pinball3d.zone.network.ConnectHelperClient;
import com.pinball3d.zone.network.ConnectionHelper.Type;
import com.pinball3d.zone.network.MessageConnectionRequest;
import com.pinball3d.zone.network.NetworkHandler;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public abstract class ScreenSphinxBase extends GuiScreen implements IParent {
	protected Map<Long, ChunkRenderCache> mapCache = new HashMap<Long, ChunkRenderCache>();
	private int lastMouseX, lastMouseY;
	private int clickX, clickY;
	protected float partialMoveX;
	protected float partialMoveY;
	public static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/gui/sphinx/icons.png");
	public static final ResourceLocation TEXTURE_3 = new ResourceLocation("zone:textures/gui/sphinx/icons_3.png");
	public static final ResourceLocation TEXTURE_NO_NETWORK = new ResourceLocation(
			"zone:textures/gui/sphinx/no_network.png");
	protected Set<Component> components = new HashSet<Component>();
	public WorldPos worldpos = WorldPos.ORIGIN;
	public boolean flag;
	public Stack<Subscreen> subscreens = new Stack<Subscreen>();
	public boolean inited;

	public abstract boolean canOpen();

	public abstract boolean isConnected();

	public abstract UUID getNetworkUUID();

	public abstract void resetNetwork();

	public void draw(int mouseX, int mouseY, float partialTicks) {
	};

	public void preDraw(boolean online, int mouseX, int mouseY, float partialTicks) {
	};

	@Override
	public void initGui() {
		if (!canOpen()) {
			mc.displayGuiScreen(null);
			return;
		}
		applyComponents();
		super.initGui();
		if (!inited) {
			init();
			inited = true;
		}
	}

	public void init() {
		NetworkHandler.instance.sendToServer(
				new MessageConnectionRequest(mc.player, getNetworkUUID(), getDataTypes().toArray(new Type[] {})));
	}

	public List<Type> getDataTypes() {
		List<Type> list = new ArrayList<Type>();
		list.add(Type.MAP);
		list.add(Type.PACK);
		return list;
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (keyCode == Keyboard.KEY_ESCAPE) {
			if (subscreens.empty()) {
				super.keyTyped(typedChar, keyCode);
			} else if (subscreens.peek().onQuit()) {
				subscreens.peek().close();
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
			} else {
				subscreens.peek().keyTyped(typedChar, keyCode);
			}
		}
	}

	@Override
	public void onGuiClosed() {
		Iterator<Subscreen> it = subscreens.iterator();
		while (it.hasNext()) {
			it.next().close();
			it.remove();
		}
		ConnectHelperClient.getInstance().disconnect(getNetworkUUID());
		super.onGuiClosed();
	}

	@Override
	public void renderToolTip(ItemStack stack, int x, int y) {
		super.renderToolTip(stack, x, y);
	}

	protected void applyComponents() {
		components = new HashSet<Component>();
	}

	public void dragMap(int mouseX, int mouseY, float partialTicks) {
		float sensitive = 5.0F;
		if (mouseX <= 1) {
			partialMoveX -= partialTicks * sensitive;
		}
		if (mouseX >= width - 1) {
			partialMoveX += partialTicks * sensitive;
		}
		if (mouseY <= 1) {
			partialMoveY -= partialTicks * sensitive;
		}
		if (mouseY >= height - 1) {
			partialMoveY += partialTicks * sensitive;
		}
		MapHandler.dragMap((int) partialMoveX, (int) partialMoveY);
		partialMoveX -= (int) partialMoveX;
		partialMoveY -= (int) partialMoveY;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		if (!canOpen()) {
			mc.displayGuiScreen(null);
			return;
		}
		int d = Mouse.getDWheel();
		if (d != 0) {
			if (subscreens.empty()) {
				Iterator<Component> it = components.iterator();
				boolean flag = true;
				while (it.hasNext()) {
					Component c = it.next();
					int x = mouseX - c.x;
					int y = mouseY - c.y;
					if (x >= 0 && x <= c.width && y >= 0 && y <= c.height) {
						if (c.onMouseScroll(x, y, d < 0)) {
							flag = false;
							break;
						}
					}
				}
				if (flag) {

				}
			} else {
				Subscreen screen = subscreens.peek();
				screen.onMouseScrollScreen(mouseX, mouseY, d < 0);
			}
		}
		preDraw(ConnectHelperClient.getInstance().isConnected(), mouseX, mouseY, partialTicks);
		if (ConnectHelperClient.getInstance().isConnected()) {
			if (subscreens.isEmpty()) {
				dragMap(mouseX, mouseY, partialTicks);
			}
			MapHandler.draw(ConnectHelperClient.getInstance().getNetworkPos(), width, height);
			draw(mouseX, mouseY, partialTicks);
		} else {
			Gui.drawRect(0, 0, mc.displayWidth, mc.displayHeight, 0xFF003434);
			Util.drawTexture(TEXTURE_NO_NETWORK, width / 2 - 32, height / 2 - 32, 256, 256, 0.25F);
			String text = I18n.format("sphinx.no_network");
			fontRenderer.drawString(text, width / 2 - fontRenderer.getStringWidth(text) / 2, height / 2 + 45,
					0xFFE0E0E0);
		}
		components.forEach(e -> {
			e.doRender(mouseX, mouseY);
		});
		Iterator<Subscreen> it = subscreens.iterator();
		while (it.hasNext()) {
			Subscreen screen = it.next();
			if (screen.dead) {
				it.remove();
			} else {
				screen.doRender(mouseX, mouseY);
			}
		}
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		int moveX = lastMouseX > 0 ? mouseX - lastMouseX : 0;
		int moveY = lastMouseY > 0 ? mouseY - lastMouseY : 0;
		lastMouseX = mouseX;
		lastMouseY = mouseY;
		if (!subscreens.empty()) {
			Subscreen screen = subscreens.peek();
			screen.onDragScreen(mouseX, mouseY, moveX, moveY, clickedMouseButton);
			return;
		}
		onDragScreen(mouseX, mouseY, moveX, moveY, clickedMouseButton);
	}

	protected void onDragScreen(int mouseX, int mouseY, int moveX, int moveY, int clickedMouseButton) {

	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		clickX = mouseX;
		clickY = mouseY;
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int button) {
		boolean flag = false;
		if ((clickX == -1 || Math.abs(mouseX - clickX) < 5) && (clickY == -1 || Math.abs(mouseY - clickY) < 5)) {
			if (subscreens.empty()) {
				Iterator<Component> it = components.iterator();
				while (it.hasNext()) {
					Component c = it.next();
					int x = mouseX - c.x;
					int y = mouseY - c.y;
					if (x >= 0 && x <= c.width && y >= 0 && y <= c.height) {
						if (c.onClickScreen(x, y, button != 1)) {
							flag = true;
							break;
						}
					}
				}
			} else {
				Subscreen screen = subscreens.peek();
				screen.onClickScreen(mouseX, mouseY, button != 1);
			}
		}
		onMouseReleaseScreen(mouseX, mouseY, button, flag);
		lastMouseX = -1;
		lastMouseY = -1;
		clickX = -1;
		clickY = -1;
		super.mouseReleased(mouseX, mouseY, button);
	}

	protected void onMouseReleaseScreen(int mouseX, int mouseY, int button, boolean flag) {

	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public int getXOffset() {
		return 0;
	}

	@Override
	public int getYOffset() {
		return 0;
	}

	@Override
	public FontRenderer getFontRenderer() {
		return fontRenderer;
	}

	@Override
	public void putScreen(Subscreen screen) {
		subscreens.push(screen);
	}

	@Override
	public void quitScreen(Subscreen screen) {
		subscreens.remove(screen);
	}

	@Override
	public ItemStack getTerminal() {
		return null;
	}
}
