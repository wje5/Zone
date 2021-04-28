package com.pinball3d.zone.sphinx.elite;

import java.io.IOException;

import com.pinball3d.zone.item.ItemLoader;
import com.pinball3d.zone.jei.JEIHandler;
import com.pinball3d.zone.sphinx.elite.MenuBar.Menu;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;

public class EliteMainwindow extends GuiScreen {
	public static final ResourceLocation ELITE = new ResourceLocation("zone:textures/gui/elite/elite.png");

	private int mouseX, mouseY;
	private MenuBar menuBar;

	public EliteMainwindow() {
		menuBar = new MenuBar(this);
		menuBar.addMenu(new Menu("帮助"));
		menuBar.addMenu(new Menu("设置"));
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		if (mouseX != this.mouseX || mouseY != this.mouseY) {
			int moveX = mouseX - this.mouseX;
			int moveY = mouseY - this.mouseY;
			onMouseMoved(this.mouseX = mouseX, this.mouseY = mouseY, moveX, moveY);
		}
		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 0, 50F);
		EliteRenderHelper.drawRect(0, 0, width, height, 0xFF282828);
		menuBar.doRender(mouseX, mouseY);
		GlStateManager.popMatrix();
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		super.keyTyped(typedChar, keyCode);
		if (Loader.isModLoaded("jei")) {
			if (typedChar == 'a') {
				JEIHandler.showJEI(new ItemStack(ItemLoader.advenced_circuit_board), true);
			}
		}
	}

	private void onMouseMoved(int mouseX, int mouseY, int moveX, int moveY) {

	}
}
