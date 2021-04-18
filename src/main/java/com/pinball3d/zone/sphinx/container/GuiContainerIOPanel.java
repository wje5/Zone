package com.pinball3d.zone.sphinx.container;

import java.util.Iterator;
import java.util.Set;

import org.lwjgl.input.Keyboard;

import com.pinball3d.zone.gui.component.TextInputBox;
import com.pinball3d.zone.gui.component.TexturedButton;
import com.pinball3d.zone.network.ConnectHelperClient;
import com.pinball3d.zone.network.ConnectionHelper.Type;
import com.pinball3d.zone.network.MessageIOPanelSendItemToStorage;
import com.pinball3d.zone.network.MessageIOPanelTransferPlayerInventory;
import com.pinball3d.zone.network.MessageOpenIOPanelGui;
import com.pinball3d.zone.network.NetworkHandler;
import com.pinball3d.zone.sphinx.subscreen.SubscreenIOPanelRequest;
import com.pinball3d.zone.util.HugeItemStack;
import com.pinball3d.zone.util.StorageWrapper;
import com.pinball3d.zone.util.Util;
import com.pinball3d.zone.util.WorldPos;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiContainerIOPanel extends GuiContainerNetworkBase {
	public static final ResourceLocation IO_PANEL = new ResourceLocation("zone:textures/gui/sphinx/io_panel.png");
	public static final ResourceLocation IO_PANEL2 = new ResourceLocation("zone:textures/gui/sphinx/io_panel_2.png");
	private TextInputBox box;
	private int panelX, panelY, page = 1, maxPage = 1;
	private WorldPos pos;

	public GuiContainerIOPanel(ContainerIOPanel container, WorldPos pos) {
		super(container);
		this.pos = pos;
	}

	@Override
	protected void setSize() {
		super.setSize();
		panelX = width / 2 - 184;
		panelY = height / 2 - 107;
		xSize = 306;
		ySize = 214;
	}

	@Override
	public void initGui() {
		super.initGui();
		guiLeft -= 31;
	}

	@Override
	public Set<Type> getDataTypes() {
		Set<Type> set = super.getDataTypes();
		set.add(Type.NETWORKPOS);
		set.add(Type.ITEMS);
		return set;
	}

	@Override
	public void addComponents() {
		super.addComponents();
		components.add(box = new TextInputBox(this, panelX + 7, panelY + 7, 61, 15, 55, () -> box.isFocus = true)
				.setIsPixel(true));
		components.add(new TexturedButton(this, panelX + 15, panelY + 201, SPHINX_ICONS, 92, 32, 5, 9, 1.0F, () -> {
			page = page - 1 < 1 ? maxPage : page - 1;
		}));
		components.add(new TexturedButton(this, panelX + 70, panelY + 201, SPHINX_ICONS, 97, 32, 5, 9, 1.0F, () -> {
			page = page + 1 > maxPage ? 1 : page + 1;
		}));
		components.add(new TexturedButton(this, panelX + 67, panelY + 7, SPHINX_ICONS, 92, 41, 15, 15, 1.0F, () -> {
			box.isFocus = false;
		}));
		components.add(new TexturedButton(this, panelX + 285, panelY + 5, SPHINX_ICONS, 64, 68, 30, 28, 0.5F, () -> {
			NetworkHandler.instance.sendToServer(MessageIOPanelSendItemToStorage.newMessage(mc.player, pos));
		}));
		components.add(new TexturedButton(this, panelX + 285, panelY + 24, SPHINX_ICONS, 0, 68, 32, 32, 0.5F, () -> {
			System.out.println("config");
		}));
		components.add(new TexturedButton(this, panelX + 285, panelY + 43, SPHINX_ICONS, 180, 68, 31, 32, 0.5F, () -> {
			NetworkHandler.instance.sendToServer(new MessageIOPanelTransferPlayerInventory(mc.player, true));
		}));
		components.add(new TexturedButton(this, panelX + 285, panelY + 62, SPHINX_ICONS, 211, 68, 31, 32, 0.5F, () -> {
			NetworkHandler.instance.sendToServer(new MessageIOPanelTransferPlayerInventory(mc.player, false));
		}));
	}

	public RenderItem getItemRenderer() {
		return itemRender;
	}

	public StorageWrapper getItems() {
		StorageWrapper s;
		if (ConnectHelperClient.getInstance().hasData()) {
			s = ConnectHelperClient.getInstance().getItems().copy().search(box.text);
		} else {
			s = new StorageWrapper();
		}
		maxPage = (s.getSize() - 1) / 36 + 1;
		if (page > maxPage) {
			page = maxPage;
		}
		if (page < 1) {
			page = 1;
		}
		return s;
	}

	public int getHoveredSlot(int mouseX, int mouseY) {
		int mX = mouseX - panelX - 8;
		int mY = mouseY - panelY - 29;
		if (mX >= 0 && mX <= 75 && mY >= 0 && mY <= 170 && mX % 19 < 18 && mY % 19 < 18) {
			int slotX = mX / 19;
			int slotY = mY / 19;
			return slotY * 4 + slotX;
		}
		return -1;
	}

	@Override
	protected void draw(int mouseX, int mouseY, float partialTicks) {
		super.draw(mouseX, mouseY, partialTicks);
		GlStateManager.color(1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(IO_PANEL);
		drawTexturedModalRect(panelX + 92, panelY, 0, 0, 214, 213);
		mc.getTextureManager().bindTexture(IO_PANEL2);
		drawTexturedModalRect(panelX, panelY, 0, 0, 89, 213);
		String text = page + "/" + maxPage;
		fontRenderer.drawString(text, panelX + 45 - fontRenderer.getStringWidth(text) / 2, panelY + 202, 0xFF1ECCDE);
		GlStateManager.enableLighting();
		GlStateManager.enableDepth();
		GlStateManager.enableBlend();
		RenderHelper.enableGUIStandardItemLighting();
		GlStateManager.disableLighting();
		GlStateManager.enableRescaleNormal();
		GlStateManager.enableColorMaterial();
		GlStateManager.enableLighting();
		RenderItem ir = mc.getRenderItem();
		StorageWrapper w = getItems();
		Iterator<HugeItemStack> it = w.storges.iterator();
		Iterator<ItemStack> it2 = w.other.iterator();
		for (int offset = (page - 1) * 36; offset > 0; offset--) {
			if (it.hasNext()) {
				it.next();
			} else if (it2.hasNext()) {
				it2.next();
			}
		}
		for (int j = 0; j < 9; j++) {
			for (int i = 0; i < 4; i++) {
				ItemStack stack = ItemStack.EMPTY;
				int amount = 0;
				if (it.hasNext()) {
					HugeItemStack hugestack = it.next();
					stack = hugestack.stack;
					amount = hugestack.count;
				} else if (it2.hasNext()) {
					stack = it2.next();
					amount = stack.getCount();
				}
				stack = stack.copy();
				stack.setCount(1);
				ir.renderItemAndEffectIntoGUI(stack, panelX + 8 + i * 19, panelY + 29 + j * 19);
				text = amount <= 1 ? null : Util.transferString(amount);
				ir.renderItemOverlayIntoGUI(fontRenderer, stack, panelX + 8 + i * 19, panelY + 29 + j * 19, text);
			}
		}
		int slot = getHoveredSlot(mouseX, mouseY);
		if (slot != -1) {
			renderCover(slot);
		}
	}

	@Override
	protected void drawForeground(int mouseX, int mouseY) {
		super.drawForeground(mouseX, mouseY);
		if (!subscreens.isEmpty()) {
			return;
		}
		int slot = getHoveredSlot(mouseX, mouseY);
		if (slot != -1) {
			slot += (page - 1) * 36;
			StorageWrapper w = getItems();
			Iterator<HugeItemStack> i = w.storges.iterator();
			while (i.hasNext()) {
				HugeItemStack s = i.next();
				if (slot == 0) {
					renderToolTip(s.stack, mouseX, mouseY);
					return;
				}
				slot--;
			}
			Iterator<ItemStack> j = w.other.iterator();
			while (j.hasNext()) {
				ItemStack s = j.next();
				if (slot == 0) {
					renderToolTip(s, mouseX, mouseY);
					return;
				}
				slot--;
			}
		}
	}

	public void renderCover(int slot) {
		GlStateManager.disableLighting();
		GlStateManager.disableDepth();
		int slotX = slot % 4;
		int slotY = slot / 4;
		int j1 = slotX * 19 + panelX + 8;
		int k1 = slotY * 19 + panelY + 29;
		Gui.drawRect(j1, k1, j1 + 16, k1 + 16, 0x80FFFFFF);
		GlStateManager.enableLighting();
		GlStateManager.enableDepth();
	}

	@Override
	public void onMouseScrolling(int mouseX, int mouseY, boolean isUp, boolean flag) {
		super.onMouseScrolling(mouseX, mouseY, isUp, flag);
		if (!flag) {
			int x = mouseX - guiLeft;
			int y = mouseY - guiTop;
			if (x >= 0 && y >= 0 && x <= 89 && y <= 213) {
				if (isUp) {
					page = page - 1 < 1 ? maxPage : page - 1;
				} else {
					page = page + 1 > maxPage ? 1 : page + 1;
				}
			}
		}
	}

	@Override
	protected void onKetInput(char typedChar, int keyCode) {
		if (keyCode == Keyboard.KEY_RETURN && box.isFocus) {
			box.isFocus = false;
		}
		super.onKetInput(typedChar, keyCode);
	}

	@Override
	protected void onMouseReleaseScreen(int mouseX, int mouseY, int button, boolean flag) {
		super.onMouseReleaseScreen(mouseX, mouseY, button, flag);
		if (!flag && subscreens.isEmpty()) {
			int slot = getHoveredSlot(mouseX, mouseY);
			if (slot >= 0) {
				ItemStack stack = ItemStack.EMPTY;
				int amount = 1;
				slot += (page - 1) * 36;
				StorageWrapper w = getItems();
				Iterator<HugeItemStack> i = w.storges.iterator();
				while (i.hasNext()) {
					HugeItemStack s = i.next();
					if (slot == 0) {
						stack = s.stack;
						amount = s.count;
						break;
					}
					slot--;
				}
				if (stack.isEmpty()) {
					Iterator<ItemStack> j = w.other.iterator();
					while (j.hasNext()) {
						ItemStack s = j.next();
						if (slot == 0) {
							stack = s;
							break;
						}
						slot--;
					}
				}
				if (!stack.isEmpty()) {
					SubscreenIOPanelRequest s = new SubscreenIOPanelRequest(this, mouseX, mouseY, stack, amount);
					if (Keyboard.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSneak.getKeyCode())) {
						s.max();
					}
					putScreen(s);
				}

			}
		}
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
