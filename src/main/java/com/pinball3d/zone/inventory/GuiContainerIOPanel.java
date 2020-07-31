package com.pinball3d.zone.inventory;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

import org.lwjgl.input.Keyboard;

import com.pinball3d.zone.network.MessageIOPanelPageChange;
import com.pinball3d.zone.network.MessageIOPanelSearchChange;
import com.pinball3d.zone.network.MessageIOPanelSendItemToStorage;
import com.pinball3d.zone.network.MessageUpdateIOPanelGui;
import com.pinball3d.zone.network.NetworkHandler;
import com.pinball3d.zone.sphinx.Component;
import com.pinball3d.zone.sphinx.IParent;
import com.pinball3d.zone.sphinx.MapHandler;
import com.pinball3d.zone.sphinx.ScreenIOPanel;
import com.pinball3d.zone.sphinx.Subscreen;
import com.pinball3d.zone.sphinx.TextInputBox;
import com.pinball3d.zone.sphinx.TexturedButton;
import com.pinball3d.zone.sphinx.Util;
import com.pinball3d.zone.sphinx.WorldPos;
import com.pinball3d.zone.tileentity.INeedNetwork;

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
		xSize = 276;
		ySize = 213;
	}

	@Override
	public void initGui() {
		applyComponents();
		super.initGui();
	}

	protected void applyComponents() {
		components = new HashSet<Component>();
		components.add(box = new TextInputBox(this, getXOffset() + 7, getYOffset() + 7, 61, 15, 8, new Runnable() {
			@Override
			public void run() {
				box.isFocus = true;
			}
		}));
		int offsetX = width / 2 - 184, offsetY = (height - ySize) / 2;
		components.add(new TexturedButton(this, offsetX + 15, offsetY + 201, ICONS, 92, 32, 5, 9, 1.0F, new Runnable() {
			@Override
			public void run() {
				NetworkHandler.instance
						.sendToServer(new MessageIOPanelPageChange(Minecraft.getMinecraft().player, true));
			}
		}));
		components.add(new TexturedButton(this, offsetX + 70, offsetY + 201, ICONS, 97, 32, 5, 9, 1.0F, new Runnable() {
			@Override
			public void run() {
				NetworkHandler.instance
						.sendToServer(new MessageIOPanelPageChange(Minecraft.getMinecraft().player, false));
			}
		}));
		components.add(new TexturedButton(this, offsetX + 67, offsetY + 7, ICONS, 92, 41, 15, 15, 1.0F, new Runnable() {
			@Override
			public void run() {
				NetworkHandler.instance
						.sendToServer(new MessageIOPanelSearchChange(Minecraft.getMinecraft().player, box.text));
			}
		}));
		components
				.add(new TexturedButton(this, offsetX + 285, offsetY + 5, ICONS, 64, 68, 30, 28, 0.5F, new Runnable() {
					@Override
					public void run() {
						NetworkHandler.instance.sendToServer(MessageIOPanelSendItemToStorage.newMessage("aaaaaaaa",
								container.tileEntity.getNetworkPos(), new WorldPos(container.tileEntity)));
					}
				}));
		components
				.add(new TexturedButton(this, offsetX + 285, offsetY + 24, ICONS, 0, 68, 32, 32, 0.5F, new Runnable() {
					@Override
					public void run() {
						System.out.println("config");
					}
				}));
	}

	public RenderItem getItemRenderer() {
		return itemRender;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(TEXTURE);
		int offsetX = (width - 184) / 2, offsetY = (height - ySize) / 2;
		drawTexturedModalRect(offsetX, offsetY, 0, 0, 214, 213);
		mc.getTextureManager().bindTexture(TEXTURE2);
		drawTexturedModalRect(offsetX - 92, offsetY, 0, 0, 89, 213);
		String text = container.page + "/" + container.maxPage;
		fontRenderer.drawString(text, offsetX - 47 - fontRenderer.getStringWidth(text) / 2, offsetY + 202, 0xFF1ECCDE);
	}

	@Override
	protected void renderHoveredToolTip(int mouseX, int mouseY) {
		int offsetX = (width - 184) / 2 - 46, offsetY = (height - ySize) / 2;
		GlStateManager.pushMatrix();
		GlStateManager.disableLighting();
		GlStateManager.disableDepth();
		GlStateManager.disableBlend();
		for (int i = 0; i < 36; i++) {
			if (container.list[i] > 1) {
				String text = Util.transferString(container.list[i]);
				int x = (i % 4) * 19 - 38 + offsetX;
				int y = (i / 4) * 19 + 29 + offsetY;
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
		super.renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
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
			if (mouseX >= screen.x && mouseX <= screen.x + width && mouseY >= screen.y && mouseY <= screen.y + height) {
				screen.onDrag(mouseX - screen.x, mouseY - screen.y, moveX, moveY, clickedMouseButton != 1);
			}
			return;
		}
		if (clickedMouseButton != 1) {
			if (lastMouseX > 0 && lastMouseY > 0) {
				MapHandler.dragMap(-moveX, -moveY);
			}
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
			super.mouseClicked(mouseX, mouseY, mouseButton);
		}
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		box.isFocus = false;
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
				mc.displayGuiScreen(new ScreenIOPanel(getNeedNetworkTileEntity()));
			} else if (subscreens.peek().onQuit()) {
				subscreens.pop();
			}
			return;
		} else {
			if (subscreens.empty()) {
				components.forEach(e -> {
					e.onKeyTyped(typedChar, keyCode);
				});
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
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public int getXOffset() {
		return (width - xSize) / 2 - 46;
	}

	@Override
	public int getYOffset() {
		return (height - ySize) / 2;
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
	public INeedNetwork getNeedNetworkTileEntity() {
		return container.tileEntity;
	};
}
