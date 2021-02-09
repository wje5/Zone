package com.pinball3d.zone.manual;

import com.pinball3d.zone.gui.GuiContainerZone;
import com.pinball3d.zone.manual.component.ButtonPage;
import com.pinball3d.zone.util.Util;

import net.minecraft.util.ResourceLocation;

public abstract class GuiContainerManualBase extends GuiContainerZone {
	protected ButtonPage button, button2;

	public GuiContainerManualBase(ContainerManual container) {
		super(container);
	}

	public static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/gui/manual.png");
	public static final ResourceLocation TEXTURE2 = new ResourceLocation("zone:textures/gui/manual_2.png");

	@Override
	public void addComponents() {
		super.addComponents();
		int x = width / 2 - 146;
		int y = height / 2 - 90;
		addComponent(button = new ButtonPage(this, x + 12, y + 158, true, () -> {
			onFlip(true);
		}));
		addComponent(button2 = new ButtonPage(this, x + 270, y + 158, false, () -> {
			onFlip(false);
		}));
	}

	@Override
	protected void setSize() {
		super.setSize();
		xSize = 292;
		ySize = 180;
	}

	public abstract void onFlip(boolean flag);

	public abstract void drawContents(int mouseX, int mouseY);

	@Override
	protected void draw(int mouseX, int mouseY, float partialTicks) {
		int x = width / 2 - 146;
		int x2 = width / 2;
		int y = height / 2 - 90;
		Util.drawTexture(TEXTURE, x, y, 146, 180);
		Util.drawTexture(TEXTURE2, x2, y, 146, 180);
		drawContents(mouseX, mouseY);
		super.draw(mouseX, mouseY, partialTicks);
	}

	public void drawTextBlock(String key, int x, int y) {
		String s = Util.formatAndAntiEscape(key);
		fontRenderer.drawSplitString(s, x, y, 292, 0xFF000000);
	}

	public void drawFrame(int x, int y) {
		Util.drawTexture(TEXTURE, x, y, 146, 13, 18, 18, 1.0F);
	}
}
