package com.pinball3d.zone.sphinx;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

import org.lwjgl.input.Keyboard;

import com.pinball3d.zone.network.MessageOpenSphinx;
import com.pinball3d.zone.network.NetworkHandler;
import com.pinball3d.zone.tileentity.TEProcessingCenter;

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
	private int xOffset, yOffset;
	private boolean flag, flag2;
	public TEProcessingCenter tileentity;

	public ScreenSphinxOpenPassword(TEProcessingCenter te, boolean flag) {
		tileentity = te;
		this.flag = flag;
	}

	public boolean checkTileentity() {
		if (tileentity == null) {
			mc.displayGuiScreen(null);
			return false;
		}
		return true;
	}

	@Override
	public void initGui() {
		if (!checkTileentity()) {
			return;
		}
		applyComponents();
		super.initGui();
	}

	private void applyComponents() {
		components = new HashSet<Component>();
		components.add(new TexturedButton(this, width / 2 + 52, height / 2 + 22, TEXTURE, 32, 36, 60, 32, 0.5F,
				new Runnable() {
					@Override
					public void run() {
						if ((input.length() == 8 || input.length() == 0) && tileentity.isCorrectAdminPassword(input)) {
							if (flag) {
								NetworkHandler.instance.sendToServer(new MessageOpenSphinx(input,
										new WorldPos(tileentity.getPos(), tileentity.getWorld()),
										new NBTTagCompound()));
								tileentity.open();
								mc.displayGuiScreen(new ScreenLoadSphinx(tileentity));
							} else {
								mc.displayGuiScreen(new ScreenSphinxController(tileentity, input));
							}
						} else {
							input = "";
							incorrect = true;
						}
					}
				}));
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
				components.forEach(e -> {
					e.onKeyTyped(typedChar, keyCode);
				});
				if (keyCode == Keyboard.KEY_RETURN) {
					if ((input.length() == 8 || input.length() == 0) && tileentity.isCorrectAdminPassword(input)) {
						if (flag) {
							NetworkHandler.instance.sendToServer(new MessageOpenSphinx(input,
									new WorldPos(tileentity.getPos(), tileentity.getWorld()), new NBTTagCompound()));
							tileentity.open();
							mc.displayGuiScreen(new ScreenLoadSphinx(tileentity));
						} else {
							mc.displayGuiScreen(new ScreenSphinxController(tileentity, input));
						}
					} else {
						input = "";
						incorrect = true;
					}
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
		if (!checkTileentity()) {
			return;
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
			if (mouseX >= screen.x && mouseX <= screen.x + width && mouseY >= screen.y && mouseY <= screen.y + height) {
				screen.onDrag(mouseX - screen.x, mouseY - screen.y, moveX, moveY, clickedMouseButton != 1);
			}
			return;
		}
		if (clickedMouseButton != 1) {
			if (lastMouseX > 0 && lastMouseY > 0) {
				xOffset = xOffset - moveX;
				yOffset = yOffset - moveY;
			}
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
				components.forEach(e -> {
					int x = mouseX - e.x;
					int y = mouseY - e.y;
					if (x >= 0 && x <= e.width && y >= 0 && y <= e.height) {
						e.onClickScreen(x, y, state != 1);
					}
				});
			} else {
				Subscreen screen = subscreens.peek();
				if (mouseX >= screen.x && mouseX <= screen.x + width && mouseY >= screen.y
						&& mouseY <= screen.y + height) {
					screen.onClick(mouseX - screen.x, mouseY - screen.y, state != 1);
				}
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
