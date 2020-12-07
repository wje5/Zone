package com.pinball3d.zone.sphinx;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.pinball3d.zone.network.ConnectHelperClient;
import com.pinball3d.zone.network.ConnectionHelper.Type;
import com.pinball3d.zone.network.MessageConnectionControllerRequest;
import com.pinball3d.zone.network.MessageOpenSphinx;
import com.pinball3d.zone.network.NetworkHandler;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class ScreenSphinxOpenPassword extends GuiScreen implements IParent {
	private static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/gui/sphinx/icons.png");
	private static final ResourceLocation SPHINX = new ResourceLocation("zone:textures/gui/sphinx/sphinx.png");
	private String input = "";
	private boolean incorrect;
	private Set<Component> components = new HashSet<Component>();
	public Stack<Subscreen> subscreens = new Stack<Subscreen>();
	private int lastMouseX, lastMouseY;
	private int clickX, clickY;
	private boolean flag, inited;
	private WorldPos center;

	public ScreenSphinxOpenPassword(WorldPos center, boolean flag) {
		this.flag = flag;
		this.center = center;
	}

	@Override
	public void initGui() {
		if (!inited) {
			NetworkHandler.instance.sendToServer(new MessageConnectionControllerRequest(mc.player, center,
					Type.NETWORKPOS, Type.ADMINPASSWORD, Type.INITED));
			inited = true;
		}
		applyComponents();
		Keyboard.enableRepeatEvents(true);
		super.initGui();
	}

	@Override
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
		Iterator<Subscreen> it = subscreens.iterator();
		while (it.hasNext()) {
			it.next().close();
			it.remove();
		}
		ConnectHelperClient.getInstance().disconnect();
		super.onGuiClosed();
	}

	private void applyComponents() {
		components = new HashSet<Component>();
		components.add(new TexturedButton(this, width / 2 + 52, height / 2 + 22, TEXTURE, 32, 36, 60, 32, 0.5F, () -> {
			onConfirm();
		}));
	}

	private void onConfirm() {
		boolean inited = ConnectHelperClient.getInstance().isInited();
		String adminPassword = ConnectHelperClient.getInstance().getAdminPassword();
		if (!adminPassword.isEmpty() || !inited) {
			if ((input.length() == 8 || input.length() == 0)
					&& input.equals(ConnectHelperClient.getInstance().getAdminPassword())) {
				if (flag) {
					NetworkHandler.instance.sendToServer(new MessageOpenSphinx(input,
							ConnectHelperClient.getInstance().getNetworkPos(), new NBTTagCompound()));
					mc.displayGuiScreen(new ScreenLoadSphinx(center));
				} else {
					mc.displayGuiScreen(new ScreenSphinxController(center, input));
				}
			} else {
				input = "";
				incorrect = true;
			}
		}
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
				if (keyCode == Keyboard.KEY_RETURN) {
					onConfirm();
				} else {
					if (keyCode == Keyboard.KEY_BACK) {
						if (input.length() >= 1) {
							input = input.substring(0, input.length() - 1);
						}
						incorrect = false;
					}
					if (input.length() < 8 && Util.isValidChar(typedChar, 7)) {
						input += typedChar;
						incorrect = false;
					}
				}
			} else {
				subscreens.peek().keyTyped(typedChar, keyCode);
			}
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
//		System.out.println(ConnectHelperClient.getInstance().getAdminPassword());
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
		int xOffset = -82;
		int yOffset = 30;
		Gui.drawRect(0, 0, mc.displayWidth, mc.displayHeight, 0xFF003434);
		Gui.drawRect(width / 2 + xOffset, height / 2 - 8 + yOffset, width / 2 + xOffset + 128, height / 2 - 7 + yOffset,
				0xFF20E6EF);
		Gui.drawRect(width / 2 + xOffset, height / 2 + 7 + yOffset, width / 2 + xOffset + 128, height / 2 + 8 + yOffset,
				0xFF20E6EF);
		Gui.drawRect(width / 2 + xOffset, height / 2 - 7 + yOffset, width / 2 + xOffset + 1, height / 2 + 7 + yOffset,
				0xFF20E6EF);
		Gui.drawRect(width / 2 + xOffset + 127, height / 2 - 7 + yOffset, width / 2 + xOffset + 128,
				height / 2 + 7 + yOffset, 0xFF20E6EF);
		Util.drawTexture(SPHINX, width / 2 - 32, height / 2 - 45, 0, 0, 255, 202, 0.25F);
		String text = I18n.format("sphinx.input_admin_password");
		FontRenderer renderer = getFontRenderer();
		renderer.drawString(text, width / 2 - renderer.getStringWidth(text) / 2, height / 2 - 20 + yOffset, 0xFF1ECCDE);
		if (incorrect && input.length() == 0) {
			text = I18n.format("sphinx.password_incorrect");
			renderer.drawString(text, width / 2 - renderer.getStringWidth(text) / 2 + xOffset + 64,
					height / 2 - 4 + yOffset, 0xFF1ECCDE);
		} else {
			for (int i = 0; i < input.length(); i++) {
				Util.drawTexture(TEXTURE, width / 2 + xOffset + 3 + i * 16, height / 2 - 5 + yOffset, 0, 118, 21, 21,
						0.5F);
			}
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
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		clickX = mouseX;
		clickY = mouseY;
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		if ((clickX == -1 || Math.abs(mouseX - clickX) < 5) && (clickY == -1 || Math.abs(mouseY - clickY) < 5)) {
			if (subscreens.empty()) {
				Iterator<Component> it = components.iterator();
				while (it.hasNext()) {
					Component c = it.next();
					int x = mouseX - c.x;
					int y = mouseY - c.y;
					if (x >= 0 && x <= c.width && y >= 0 && y <= c.height) {
						if (c.onClickScreen(x, y, state != 1)) {
							break;
						}
					}
				}
			} else {
				Subscreen screen = subscreens.peek();
				screen.onClickScreen(mouseX, mouseY, state != 1);
			}
		}
		lastMouseX = -1;
		lastMouseY = -1;
		clickX = -1;
		clickY = -1;
		super.mouseReleased(mouseX, mouseY, state);
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
}
