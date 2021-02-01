package com.pinball3d.zone.sphinx.subscreen;

import com.pinball3d.zone.gui.Subscreen;
import com.pinball3d.zone.network.MessageRequestNeedNetworkInfo;
import com.pinball3d.zone.network.NetworkHandler;
import com.pinball3d.zone.sphinx.IHasSubscreen;
import com.pinball3d.zone.sphinx.component.TextButton;
import com.pinball3d.zone.tileentity.TEProcessingCenter.WorkingState;
import com.pinball3d.zone.util.Util;
import com.pinball3d.zone.util.WorldPos;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class SubscreenNeedNetworkInfo extends Subscreen {
	private static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/gui/sphinx/ui_border.png");
	private WorldPos pos;
	private String name;
	private WorkingState state;

	public SubscreenNeedNetworkInfo(IHasSubscreen parent, WorldPos pos) {
		this(parent, pos, getDisplayWidth() / 2 - 150, getDisplayHeight() / 2 - 100);
	}

	public SubscreenNeedNetworkInfo(IHasSubscreen parent, WorldPos pos, int x, int y) {
		super(parent, x, y, 300, 200, true);
		NetworkHandler.instance.sendToServer(new MessageRequestNeedNetworkInfo(mc.player, pos));
		components.add(new TextButton(this, this.x + 235, this.y + 175, I18n.format("sphinx.confirm"), () -> {
			parent.removeScreen(SubscreenNeedNetworkInfo.this);
		}));
		this.pos = pos;
	}

	public void setData(WorldPos pos, NBTTagCompound tag) {
		if (pos.equals(this.pos)) {
			name = tag.getString("name");
			state = WorkingState.values()[tag.getInteger("state")];
		}
	}

	@Override
	public void doRenderBackground(int mouseX, int mouseY) {
		super.doRenderBackground(mouseX, mouseY);
		Util.drawTexture(TEXTURE, x - 5, y - 5, 0, 0, 99, 99, 0.5F);
		Util.drawTexture(TEXTURE, x + 255, y - 5, 99, 0, 99, 99, 0.5F);
		Util.drawTexture(TEXTURE, x - 5, y + 155, 0, 99, 99, 99, 0.5F);
		Util.drawTexture(TEXTURE, x + 255, y + 155, 99, 99, 99, 99, 0.5F);
		Gui.drawRect(x + 44, y, x + 255, y + 44, 0x2F000000);
		Gui.drawRect(x, y + 44, x + 300, y + 155, 0x2F000000);
		Gui.drawRect(x + 44, y + 155, x + 255, y + 200, 0x2F000000);
		Util.renderGlowHorizonLine(x + 10, y + 20, 280);
		Gui.drawRect(x + 16, y + 24, x + 284, y + 194, 0x651CC3B5);
		FontRenderer fr = Util.getFontRenderer();
		fr.drawString(I18n.format("sphinx.unit_info"), x + 15, y + 8, 0xFF1ECCDE);
		if (name != null) {
			fr.drawString(I18n.format("sphinx.unit_name") + ":", x + 27, y + 35, 0xFF1ECCDE);
			fr.drawString(name, x + 180, y + 35, 0xFF1ECCDE);
			fr.drawString(I18n.format("sphinx.working_state") + ":", x + 27, y + 55, 0xFF1ECCDE);
			fr.drawString(state.toString(), x + 180, y + 55, 0xFF1ECCDE);
			fr.drawString(I18n.format("sphinx.location") + ":", x + 27, y + 65, 0xFF1ECCDE);
			String text = pos.getPos().getX() + "," + pos.getPos().getY() + "," + pos.getPos().getZ();
			fr.drawString(text, x + 180, y + 65, 0xFF1ECCDE);
		}
		Util.drawBorder(x + 15, y + 23, 270, 172, 1, 0xFF1ECCDE);
	}
}
