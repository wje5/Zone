package com.pinball3d.zone.inventory;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.pinball3d.zone.network.MessageIOPanelPageChange;
import com.pinball3d.zone.network.MessageIOPanelSearchChange;
import com.pinball3d.zone.network.MessageIOPanelSendItemToStorage;
import com.pinball3d.zone.network.MessageIOPanelTransferPlayerInventory;
import com.pinball3d.zone.network.MessageUpdateIOPanelGui;
import com.pinball3d.zone.network.NetworkHandler;
import com.pinball3d.zone.sphinx.Component;
import com.pinball3d.zone.sphinx.IParent;
import com.pinball3d.zone.sphinx.ScreenIOPanel;
import com.pinball3d.zone.sphinx.Subscreen;
import com.pinball3d.zone.sphinx.TextInputBox;
import com.pinball3d.zone.sphinx.TexturedButton;
import com.pinball3d.zone.sphinx.Util;
import com.pinball3d.zone.sphinx.WorldPos;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiContainerIOPanel extends GuiContainer implements IParent {
	public static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/gui/sphinx/io_panel.png");
	public static final ResourceLocation TEXTURE2 = new ResourceLocation("zone:textures/gui/sphinx/io_panel_2.png");
	public static final ResourceLocation ICONS = new ResourceLocation("zone:textures/gui/sphinx/icons.png");
	public ContainerIOPanel container;
	private int lastMouseX, lastMouseY;
	private int clickX, clickY;
	protected Set<Component> components = new HashSet<Component>();
	public Stack<Subscreen> subscreens = new Stack<Subscreen>();
	private TextInputBox box;

	public GuiContainerIOPanel(ContainerIOPanel container) {
		super(container);
		this.container = container;
		xSize = 306;
		ySize = 213;
	}

	@Override
	public void initGui() {
		super.initGui();
		guiLeft -= 31;
		applyComponents();
	}

	protected void applyComponents() {
		components = new HashSet<Component>();
		components.add(box = new TextInputBox(this, getXOffset() + 7, getYOffset() + 7, 61, 15, 55, () -> {
			box.isFocus = true;
		}).setIsPixel(true));
		int offsetX = width / 2 - 184, offsetY = (height - ySize) / 2;
		components.add(new TexturedButton(this, offsetX + 15, offsetY + 201, ICONS, 92, 32, 5, 9, 1.0F, () -> {
			NetworkHandler.instance.sendToServer(new MessageIOPanelPageChange(Minecraft.getMinecraft().player, true));
		}));
		components.add(new TexturedButton(this, offsetX + 70, offsetY + 201, ICONS, 97, 32, 5, 9, 1.0F, () -> {
			NetworkHandler.instance.sendToServer(new MessageIOPanelPageChange(Minecraft.getMinecraft().player, false));
		}));
		components.add(new TexturedButton(this, offsetX + 67, offsetY + 7, ICONS, 92, 41, 15, 15, 1.0F, () -> {
			NetworkHandler.instance
					.sendToServer(new MessageIOPanelSearchChange(Minecraft.getMinecraft().player, box.text));
		}));
		components.add(new TexturedButton(this, offsetX + 285, offsetY + 5, ICONS, 64, 68, 30, 28, 0.5F, () -> {
			NetworkHandler.instance
					.sendToServer(MessageIOPanelSendItemToStorage.newMessage(container.tileEntity.getPassword(),
							container.tileEntity.getNetworkPos(), new WorldPos(container.tileEntity), mc.player));
		}));
		components.add(new TexturedButton(this, offsetX + 285, offsetY + 24, ICONS, 0, 68, 32, 32, 0.5F, () -> {
			System.out.println("config");
		}));
		components.add(new TexturedButton(this, offsetX + 285, offsetY + 43, ICONS, 180, 68, 31, 32, 0.5F, () -> {
			NetworkHandler.instance.sendToServer(new MessageIOPanelTransferPlayerInventory(mc.player, true));
		}));
		components.add(new TexturedButton(this, offsetX + 285, offsetY + 62, ICONS, 211, 68, 31, 32, 0.5F, () -> {
			NetworkHandler.instance.sendToServer(new MessageIOPanelTransferPlayerInventory(mc.player, false));
		}));
	}

	public RenderItem getItemRenderer() {
		return itemRender;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(TEXTURE);
		drawTexturedModalRect(guiLeft + 92, guiTop, 0, 0, 214, 213);
		mc.getTextureManager().bindTexture(TEXTURE2);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, 89, 213);
		String text = container.page + "/" + container.maxPage;
		fontRenderer.drawString(text, guiLeft + 45 - fontRenderer.getStringWidth(text) / 2, guiTop + 202, 0xFF1ECCDE);
	}

	@Override
	protected void renderHoveredToolTip(int mouseX, int mouseY) {
		GlStateManager.pushMatrix();
		GlStateManager.disableLighting();
		GlStateManager.disableDepth();
		GlStateManager.disableBlend();
		for (int i = 0; i < 36; i++) {
			if (container.list[i] > 1) {
				String text = Util.transferString(container.list[i]);
				int x = (i % 4) * 19 + 8 + guiLeft;
				int y = (i / 4) * 19 + 29 + guiTop;
				fontRenderer.drawStringWithShadow(text, x + 17 - fontRenderer.getStringWidth(text), y + 9, 0xFFFFFFFF);
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

		GlStateManager.enableLighting();
		GlStateManager.enableDepth();
		GlStateManager.enableBlend();
		GlStateManager.popMatrix();
		if (subscreens.empty()) {
			super.renderHoveredToolTip(mouseX, mouseY);
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
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
				int x = mouseX - guiLeft;
				int y = mouseY - guiTop;
				if (flag && x >= 0 && y >= 0 && x <= 89 && y <= 213) {
					NetworkHandler.instance
							.sendToServer(new MessageIOPanelPageChange(Minecraft.getMinecraft().player, d > 0));
				}
			} else {
				Subscreen screen = subscreens.peek();
				screen.onMouseScrollScreen(mouseX, mouseY, d < 0);
			}
		}
		NetworkHandler.instance.sendToServer(new MessageUpdateIOPanelGui(Minecraft.getMinecraft().player));
		drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		renderHoveredToolTip(mouseX, mouseY);
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
		super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		clickX = mouseX;
		clickY = mouseY;
		if (subscreens.empty()) {
			container.x = clickX;
			container.y = clickY;
		}
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		box.isFocus = false;
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
				return;
			}
		}
		lastMouseX = -1;
		lastMouseY = -1;
		clickX = -1;
		clickY = -1;
		super.mouseReleased(mouseX, mouseY, state);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (keyCode == Keyboard.KEY_ESCAPE) {
			if (subscreens.empty()) {
				WorldPos p = container.tileEntity.getNetworkPos();
				System.out.println(p);
				mc.displayGuiScreen(new ScreenIOPanel(container.tileEntity.getNetworkPos()));
			} else if (subscreens.peek().onQuit()) {
				subscreens.peek().close();
				subscreens.pop();
			}
			return;
		} else {
			if (subscreens.empty()) {
				Iterator<Component> it = components.iterator();
				boolean flag = false;
				while (!flag && it.hasNext()) {
					Component c = it.next();
					flag = c.onKeyTyped(typedChar, keyCode);
				}
				if (keyCode == Keyboard.KEY_RETURN && box.isFocus) {
					NetworkHandler.instance
							.sendToServer(new MessageIOPanelSearchChange(Minecraft.getMinecraft().player, box.text));
					box.isFocus = false;
				}
			} else {
				subscreens.peek().keyTyped(typedChar, keyCode);
				return;
			}
		}
		if (!box.isFocus) {
			super.keyTyped(typedChar, keyCode);
		}
	}

	@Override
	public void onGuiClosed() {
		Iterator<Subscreen> it = subscreens.iterator();
		while (it.hasNext()) {
			it.next().close();
			it.remove();
		}
		super.onGuiClosed();
	}

	@Override
	public int getWidth() {
		return xSize;
	}

	@Override
	public int getHeight() {
		return ySize;
	}

	@Override
	public int getXOffset() {
		return guiLeft;
	}

	@Override
	public int getYOffset() {
		return guiTop;
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
