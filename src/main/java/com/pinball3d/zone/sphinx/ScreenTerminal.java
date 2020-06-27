package com.pinball3d.zone.sphinx;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.UUID;

import org.lwjgl.input.Keyboard;

import com.pinball3d.zone.block.BlockProcessingCenter;
import com.pinball3d.zone.item.ItemLoader;
import com.pinball3d.zone.network.MessageTerminalRequestNetworkData;
import com.pinball3d.zone.network.NetworkHandler;
import com.pinball3d.zone.tileentity.TEProcessingCenter;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;

public class ScreenTerminal extends GuiScreen implements IParent {
	protected Map<Long, ChunkRenderCache> mapCache = new HashMap<Long, ChunkRenderCache>();
	private static BufferBuilder bufferbuilder;
	private int lastMouseX, lastMouseY;
	private int clickX, clickY;
	private PointerPlayer pointerPlayer;
	private Map<Integer, PointerLiving> livings = new HashMap<Integer, PointerLiving>();
	private static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/gui/sphinx/icons.png");
	private static final ResourceLocation TEXTURE_NO_NETWORK = new ResourceLocation(
			"zone:textures/gui/sphinx/no_network.png");
	private Set<Component> components = new HashSet<Component>();
	public WorldPos worldpos;
	private boolean flag;
	public Stack<Subscreen> subscreens = new Stack<Subscreen>();
	public ItemStack stack;

	public ScreenTerminal(ItemStack stack) {
		this.stack = stack;
	}

	@Override
	public void initGui() {
		if (!checkItem()) {
			return;
		}
		applyComponents();
		super.initGui();
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
			} else {
				subscreens.peek().keyTyped(typedChar, keyCode);
			}
		}
	}

	private void applyComponents() {
		components = new HashSet<Component>();
		components.add(new ButtonNetworkConfig(this, width - 10, 2, new Runnable() {
			@Override
			public void run() {
				subscreens.push(new SubscreenNetworkConfig((ScreenTerminal) mc.currentScreen));
			}
		}, true));
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		if (!checkItem()) {
			return;
		}
		if (isConnected()) {
			MapHandler.draw(getNetwork(), width, height);
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

	private boolean checkItem() {
		if (mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() == ItemLoader.terminal
				|| mc.player.getHeldItem(EnumHand.OFF_HAND).getItem() == ItemLoader.terminal) {
			return true;
		} else {
			mc.displayGuiScreen(null);
			return false;
		}
	}

	public boolean isConnected() {
		if (getNetwork() != null) {
			if (getNetwork().getDim() == mc.world.provider.getDimension()
					&& getNetwork().getBlockState().getBlock() instanceof BlockProcessingCenter
					&& ((TEProcessingCenter) (getNetwork().getTileEntity())).isOn()) {
				NBTTagCompound tag = stack.getTagCompound();
				if (tag == null) {
					tag = new NBTTagCompound();
					stack.setTagCompound(tag);
				}
				String password = tag.getString("password");
				if (((TEProcessingCenter) (getNetwork().getTileEntity())).isCorrectLoginPassword(password)) {
					return true;
				}
			}
			resetNetwork();
		}
		return false;
	}

	public void resetNetwork() {
		NBTTagCompound tag = stack.getTagCompound();
		if (tag == null) {
			tag = new NBTTagCompound();
			stack.setTagCompound(tag);
		}
		tag.removeTag("networkMost");
		tag.removeTag("networkLeast");
	}

	public WorldPos getNetwork() {
		if (!flag) {
			NBTTagCompound tag = stack.getTagCompound();
			if (tag == null) {
				tag = new NBTTagCompound();
				stack.setTagCompound(tag);
			}
			if (tag.hasKey("networkMost")) {
				NetworkHandler.instance
						.sendToServer(new MessageTerminalRequestNetworkData(tag.getUniqueId("network"), mc.player));
			}
			return null;
		}
		return worldpos;
	}

	public void setWorldPos(WorldPos pos, UUID uuid) {
		NBTTagCompound tag = stack.getTagCompound();
		if (tag == null) {
			tag = new NBTTagCompound();
			stack.setTagCompound(tag);
		}
		if (tag.hasKey("networkMost")) {
			if (tag.getUniqueId("network").equals(uuid)) {
				if (pos == null) {
					resetNetwork();
				}
				worldpos = pos;
				flag = true;
			}
		}
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

	@Override
	public ItemStack getTerminal() {
		return stack;
	}
}
