package com.pinball3d.zone.sphinx;

import java.util.Iterator;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.pinball3d.zone.ConfigLoader;
import com.pinball3d.zone.gui.Subscreen;
import com.pinball3d.zone.network.ConnectHelperClient;
import com.pinball3d.zone.network.ConnectionHelper.Type;
import com.pinball3d.zone.network.MessageIOPanelPageChange;
import com.pinball3d.zone.network.MessageIOPanelSearchChange;
import com.pinball3d.zone.network.MessageIOPanelSendItemToStorage;
import com.pinball3d.zone.network.MessageIOPanelTransferPlayerInventory;
import com.pinball3d.zone.network.MessageOpenIOPanelGui;
import com.pinball3d.zone.network.MessageUpdateIOPanelGui;
import com.pinball3d.zone.network.NetworkHandler;
import com.pinball3d.zone.sphinx.component.Component;
import com.pinball3d.zone.sphinx.component.TextInputBox;
import com.pinball3d.zone.sphinx.component.TexturedButton;
import com.pinball3d.zone.util.Util;
import com.pinball3d.zone.util.WorldPos;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiContainerIOPanel extends GuiContainerNetworkBase {
	public static final ResourceLocation IO_PANEL = new ResourceLocation("zone:textures/gui/sphinx/io_panel.png");
	public static final ResourceLocation IO_PANEL2 = new ResourceLocation("zone:textures/gui/sphinx/io_panel_2.png");
	public static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/gui/sphinx/icons.png");
	private TextInputBox box;
	private int panelX, panelY;
	private WorldPos pos;

	public GuiContainerIOPanel(ContainerIOPanel container, WorldPos pos) {
		super(container);
		this.pos = pos;
	}

	@Override
	protected void setSize() {
		super.setSize();
		panelX = width / 2 - 184;
		panelY = height / 2 - 106;
		xSize = 306;
		ySize = 213;
	}

	@Override
	public void initGui() {
		super.initGui();
		guiLeft -= 31;
	}

	@Override
	protected void onMousePressScreen(int mouseX, int mouseY, int button) {
		if (subscreens.empty()) {
			ContainerIOPanel container = (ContainerIOPanel) inventorySlots;
			container.x = mouseX;
			container.y = mouseY;
			container.offsetedX = mouseX + panelX;
			container.offsetedY = mouseY + panelY;
		}
		super.onMousePressScreen(mouseX, mouseY, button);
	}

	@Override
	public void addComponents() {
		super.addComponents();
		components.add(box = new TextInputBox(this, panelX + 7, panelY + 7, 61, 15, 55, () -> {
			box.isFocus = true;
		}).setIsPixel(true));
		components.add(new TexturedButton(this, panelX + 15, panelY + 201, TEXTURE, 92, 32, 5, 9, 1.0F, () -> {
			NetworkHandler.instance.sendToServer(new MessageIOPanelPageChange(Minecraft.getMinecraft().player, true));
		}));
		components.add(new TexturedButton(this, panelX + 70, panelY + 201, TEXTURE, 97, 32, 5, 9, 1.0F, () -> {
			NetworkHandler.instance.sendToServer(new MessageIOPanelPageChange(Minecraft.getMinecraft().player, false));
		}));
		components.add(new TexturedButton(this, panelX + 67, panelY + 7, TEXTURE, 92, 41, 15, 15, 1.0F, () -> {
			NetworkHandler.instance
					.sendToServer(new MessageIOPanelSearchChange(Minecraft.getMinecraft().player, box.text));
		}));
		components.add(new TexturedButton(this, panelX + 285, panelY + 5, TEXTURE, 64, 68, 30, 28, 0.5F, () -> {
			ContainerIOPanel container = (ContainerIOPanel) inventorySlots;
			NetworkHandler.instance
					.sendToServer(MessageIOPanelSendItemToStorage.newMessage(container.tileEntity.getPassword(),
							container.tileEntity.getNetworkPos(), new WorldPos(container.tileEntity), mc.player));
		}));
		components.add(new TexturedButton(this, panelX + 285, panelY + 24, TEXTURE, 0, 68, 32, 32, 0.5F, () -> {
			System.out.println("config");
		}));
		components.add(new TexturedButton(this, panelX + 285, panelY + 43, TEXTURE, 180, 68, 31, 32, 0.5F, () -> {
			NetworkHandler.instance.sendToServer(new MessageIOPanelTransferPlayerInventory(mc.player, true));
		}));
		components.add(new TexturedButton(this, panelX + 285, panelY + 62, TEXTURE, 211, 68, 31, 32, 0.5F, () -> {
			NetworkHandler.instance.sendToServer(new MessageIOPanelTransferPlayerInventory(mc.player, false));
		}));
	}

	public RenderItem getItemRenderer() {
		return itemRender;
	}

	@Override
	protected void draw(int mouseX, int mouseY, float partialTicks) {
		super.draw(mouseX, mouseY, partialTicks);
		GlStateManager.color(1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(IO_PANEL);
		drawTexturedModalRect(panelX + 92, panelY, 0, 0, 214, 213);
		mc.getTextureManager().bindTexture(IO_PANEL2);
		drawTexturedModalRect(panelX, panelY, 0, 0, 89, 213);
		ContainerIOPanel container = (ContainerIOPanel) inventorySlots;
		String text = container.page + "/" + container.maxPage;
		fontRenderer.drawString(text, panelX + 45 - fontRenderer.getStringWidth(text) / 2, panelY + 202, 0xFF1ECCDE);
	}

	@Override
	protected void renderHoveredToolTip(int mouseX, int mouseY) {
		GlStateManager.pushMatrix();
		GlStateManager.disableLighting();
		GlStateManager.disableDepth();
		GlStateManager.disableBlend();
		ContainerIOPanel container = (ContainerIOPanel) inventorySlots;
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
		drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	public void updateScreen() {
		if (Minecraft.getMinecraft().world.getTotalWorldTime() % ConfigLoader.itemUpdateRate == 0) {
			NetworkHandler.instance.sendToServer(new MessageUpdateIOPanelGui(Minecraft.getMinecraft().player));
		}
		super.updateScreen();
	}

	@Override
	protected void onKetInput(char typedChar, int keyCode) {
		if (keyCode == Keyboard.KEY_RETURN && box.isFocus) {
			NetworkHandler.instance
					.sendToServer(new MessageIOPanelSearchChange(Minecraft.getMinecraft().player, box.text));
			box.isFocus = false;
		}
		super.onKetInput(typedChar, keyCode);
	}

	@Override
	public boolean onQuit() {
		BlockPos pos = this.pos.getPos();
		NetworkHandler.instance
				.sendToServer(new MessageOpenIOPanelGui(mc.player, pos.getX(), pos.getY(), pos.getZ(), false));
		return false;
	}

	@Override
	public void sendReq() {
		ConnectHelperClient.getInstance().requestNeedNetwork(
				new WorldPos(((ContainerIOPanel) inventorySlots).tileEntity), getDataTypes().toArray(new Type[] {}));
	}
}
