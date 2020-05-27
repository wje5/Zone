package com.pinball3d.zone.sphinx;

import com.pinball3d.zone.network.MessageShutdownSphinx;
import com.pinball3d.zone.network.NetworkHandler;
import com.pinball3d.zone.tileentity.TEProcessingCenter;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class SubscreenShutdownSphinx extends Subscreen {
	private static final ResourceLocation TEXTURE = new ResourceLocation(
			"zone:textures/gui/sphinx/connect_to_network.png");

	public SubscreenShutdownSphinx(IParent parent) {
		this(parent, parent.getWidth() / 2 - 80, parent.getHeight() / 2 - 45);
	}

	public SubscreenShutdownSphinx(IParent parent, int x, int y) {
		super(parent, x, y, 160, 90, false);
		components.add(new MultilineText(this, this.x + 5, this.y + 5, 150, I18n.format("sphinx.shutdown_sphinx")));
		components.add(new TextButton(this, this.x + 25, this.y + 75, I18n.format("sphinx.yes"), new Runnable() {
			@Override
			public void run() {
				TEProcessingCenter tileentity = ((ScreenSphinxController) parent).tileentity;
				NetworkHandler.instance
						.sendToServer(new MessageShutdownSphinx(((ScreenSphinxController) parent).password,
								new WorldPos(tileentity.getPos(), tileentity.getWorld()), new NBTTagCompound()));
				tileentity.shutdown();
				mc.displayGuiScreen(null);

			}
		}));
		components.add(new TextButton(this, this.x + 120, this.y + 75, I18n.format("sphinx.no"), new Runnable() {
			@Override
			public void run() {
				parent.quitScreen(getScreen());
			}
		}));
	}

	@Override
	public void doRenderBackground(int mouseX, int mouseY) {
		super.doRenderBackground(mouseX, mouseY);
		Gui.drawRect(x, y, x + width, y + height, 0xAF282828);
		Util.drawBorder(x, y, width, height, 1, 0xFF1ECCDE);
	}
}
