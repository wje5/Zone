package com.pinball3d.zone.sphinx;

import com.pinball3d.zone.tileentity.INeedNetwork;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class SubscreenNeedNetworkInfo extends Subscreen {
	private static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/gui/sphinx/ui_border.png");
	private INeedNetwork tileentity;

	public SubscreenNeedNetworkInfo(IParent parent, INeedNetwork te) {
		this(parent, te, parent.getWidth() / 2 - 150, parent.getHeight() / 2 - 100);
	}

	public SubscreenNeedNetworkInfo(IParent parent, INeedNetwork te, int x, int y) {
		super(parent, x, y, 300, 200, true);
		components.add(new TextButton(this, this.x + 235, this.y + 175, I18n.format("sphinx.confirm"), new Runnable() {
			@Override
			public void run() {
				parent.quitScreen(SubscreenNeedNetworkInfo.this);
			}
		}));
		tileentity = te;
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
		parent.getFontRenderer().drawString(I18n.format("sphinx.unit_info"), x + 15, y + 8, 0xFF1ECCDE);
		parent.getFontRenderer().drawString(I18n.format("sphinx.unit_name") + ":", x + 27, y + 35, 0xFF1ECCDE);
		parent.getFontRenderer().drawString(tileentity.getName(), x + 180, y + 35, 0xFF1ECCDE);
		parent.getFontRenderer().drawString(I18n.format("sphinx.working_state") + ":", x + 27, y + 55, 0xFF1ECCDE);
		parent.getFontRenderer().drawString(tileentity.getWorkingState().toString(), x + 180, y + 55, 0xFF1ECCDE);
		parent.getFontRenderer().drawString(I18n.format("sphinx.location") + ":", x + 27, y + 65, 0xFF1ECCDE);
		BlockPos pos = ((TileEntity) tileentity).getPos();
		String text = pos.getX() + "," + pos.getY() + "," + pos.getZ();
		parent.getFontRenderer().drawString(text, x + 180, y + 65, 0xFF1ECCDE);
		Util.drawBorder(x + 15, y + 23, 270, 172, 1, 0xFF1ECCDE);
	}
}
