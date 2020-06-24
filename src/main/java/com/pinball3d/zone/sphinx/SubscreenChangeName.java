package com.pinball3d.zone.sphinx;

import com.pinball3d.zone.network.MessageChangeName;
import com.pinball3d.zone.network.NetworkHandler;
import com.pinball3d.zone.tileentity.TEProcessingCenter;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class SubscreenChangeName extends Subscreen {
	private static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/gui/sphinx/ui_border.png");
	private TextInputBox box;

	public SubscreenChangeName(IParent parent) {
		this(parent, parent.getWidth() / 2 - 150, parent.getHeight() / 2 - 100);
	}

	public SubscreenChangeName(IParent parent, int x, int y) {
		super(parent, x, y, 300, 200, true);
		components.add(box = new TextInputBox(this, x + 30, y + 50, 100, 12, new Runnable() {
			@Override
			public void run() {
				box.isFocus = true;
			}
		}));
		TEProcessingCenter te = ((ScreenSphinxController) parent).tileentity;
		box.text = te.getName();
		components.add(new TextButton(this, this.x + 190, this.y + 175, I18n.format("sphinx.confirm"), new Runnable() {
			@Override
			public void run() {
				if (box.text.length() >= 4) {
					TEProcessingCenter te = ((ScreenSphinxController) parent).tileentity;
					NetworkHandler.instance
							.sendToServer(MessageChangeName.newMessage(((ScreenSphinxController) parent).password,
									new WorldPos(te.getPos(), te.getWorld()), box.text));
					te.setName(box.text);
					parent.quitScreen(SubscreenChangeName.this);
				}
			}
		}));
		components.add(new TextButton(this, this.x + 235, this.y + 175, I18n.format("sphinx.cancel"), new Runnable() {
			@Override
			public void run() {
				parent.quitScreen(SubscreenChangeName.this);
			}
		}));
	}

	@Override
	public boolean onQuit() {
		if (subscreens.empty()) {
			subscreens.push(new SubscreenQuitWizard(this));
			return false;
		}
		if (subscreens.peek().onQuit()) {
			subscreens.pop();
		}
		return false;
	}

	@Override
	public void doRenderBackground(int mouseX, int mouseY) {
		super.doRenderBackground(mouseX, mouseY);
		Util.drawTexture(TEXTURE, x, y, 0, 0, 80, 80, 0.5F);
		Util.drawTexture(TEXTURE, x + 260, y, 80, 0, 80, 80, 0.5F);
		Util.drawTexture(TEXTURE, x, y + 160, 0, 80, 80, 80, 0.5F);
		Util.drawTexture(TEXTURE, x + 260, y + 160, 80, 80, 80, 80, 0.5F);
		Gui.drawRect(x + 40, y, x + 260, y + 40, 0x2F000000);
		Gui.drawRect(x, y + 40, x + 300, y + 160, 0x2F000000);
		Gui.drawRect(x + 40, y + 160, x + 260, y + 200, 0x2F000000);
		Gui.drawRect(x + 10, y + 20, x + 290, y + 22, 0xFF20E6EF);
		Gui.drawRect(x + 16, y + 24, x + 284, y + 194, 0x651CC3B5);
		Util.drawBorder(x + 15, y + 23, 270, 172, 1, 0xFF1ECCDE);
		parent.getFontRenderer().drawString(I18n.format("sphinx.change_sphinx_name"), x + 15, y + 8, 0xFF1ECCDE);
		parent.getFontRenderer().drawString(I18n.format("sphinx.set_sphinx_name"), x + 30, y + 35, 0xFF1ECCDE);
		if (box.text.length() > 0 && box.text.length() < 4) {
			parent.getFontRenderer().drawString(I18n.format("sphinx.name_length_error"), x + 30, y + 155, 0xFFDA2D2D);
		}
	}
}
