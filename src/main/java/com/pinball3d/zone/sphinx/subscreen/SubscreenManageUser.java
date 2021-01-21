package com.pinball3d.zone.sphinx.subscreen;

import java.util.List;
import java.util.Set;

import com.pinball3d.zone.gui.Subscreen;
import com.pinball3d.zone.network.ConnectHelperClient;
import com.pinball3d.zone.network.ConnectionHelper.Type;
import com.pinball3d.zone.sphinx.IHasSubscreen;
import com.pinball3d.zone.sphinx.component.ScrollingEdgeList;
import com.pinball3d.zone.sphinx.component.ScrollingEdgeList.ListBar;
import com.pinball3d.zone.tileentity.TEProcessingCenter.UserData;
import com.pinball3d.zone.util.Util;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class SubscreenManageUser extends Subscreen {
	private static final ResourceLocation ICONS = new ResourceLocation("zone:textures/gui/sphinx/icons.png");
	private static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/gui/sphinx/ui_border.png");
	private ScrollingEdgeList list;

	public SubscreenManageUser(IHasSubscreen parent) {
		this(parent, getDisplayWidth() / 2 - 210, getDisplayHeight() / 2 - 100);
	}

	public SubscreenManageUser(IHasSubscreen parent, int x, int y) {
		super(parent, x, y, 360, 200, true);
		components.add(list = new ScrollingEdgeList(this, this.x, this.y + 9, 195));
	}

	@Override
	public void onClick(int x, int y, boolean isLeft) {
		super.onClick(x, y, isLeft);
	}

	@Override
	public Set<Type> getDataTypes() {
		Set<Type> s = super.getDataTypes();
		s.add(Type.USERS);
		return s;
	}

	public void updateList() {
		list.clear();
		if (ConnectHelperClient.getInstance().hasData()) {
			List<UserData> l = ConnectHelperClient.getInstance().getUsers();
			l.forEach(e -> {
				ListBar bar = new ListBar(e.uuid.toString(), () -> {
				});
				bar.setData(e);
				list.addBar(bar);
			});
		}
	}

	@Override
	public void doRenderBackground(int mouseX, int mouseY) {
		super.doRenderBackground(mouseX, mouseY);
		updateList();
		Util.drawTexture(TEXTURE, x + 60, y, 0, 0, 80, 80, 0.5F);
		Util.drawTexture(TEXTURE, x + 320, y, 80, 0, 80, 80, 0.5F);
		Util.drawTexture(TEXTURE, x + 60, y + 160, 0, 80, 80, 80, 0.5F);
		Util.drawTexture(TEXTURE, x + 320, y + 160, 80, 80, 80, 80, 0.5F);
		Gui.drawRect(x + 100, y, x + 320, y + 40, 0x2F000000);
		Gui.drawRect(x + 60, y + 40, x + 360, y + 160, 0x2F000000);
		Gui.drawRect(x + 100, y + 160, x + 320, y + 200, 0x2F000000);
		Gui.drawRect(x + 70, y + 20, x + 350, y + 22, 0xFF20E6EF);
		Gui.drawRect(x + 76, y + 24, x + 344, y + 194, 0x651CC3B5);
		FontRenderer fr = Util.getFontRenderer();
		fr.drawString(I18n.format("sphinx.manage_user"), x + 75, y + 8, 0xFF1ECCDE);
		Util.drawBorder(x + 75, y + 23, 270, 172, 1, 0xFF1ECCDE);
		ListBar bar = list.get();
		if (bar != null) {
			UserData user = (UserData) bar.getData();
			Util.drawBorder(x + 83, y + 28, 40, 40, 1, 0xFF1ECCDE);
			Util.drawBorder(x + 83, y + 73, 15, 15, 1, 0xFF1ECCDE);
			Util.drawBorder(x + 100, y + 73, 15, 15, 1, 0xFF1ECCDE);
			Util.drawBorder(x + 117, y + 73, 15, 15, 1, 0xFF1ECCDE);
			Util.drawBorder(x + 134, y + 73, 15, 15, 1, 0xFF1ECCDE);
			Util.drawBorder(x + 151, y + 73, 15, 15, 1, 0xFF1ECCDE);
			fr.drawString(user.uuid.toString(), x + 128, y + 30, 0xFF1ECCDE);
			fr.drawString("Admininstrator", x + 128, y + 37, 0xFF1ECCDE);
			fr.drawString("Offline", x + 128, y + 44, 0xFFBFBFBF);
		}
	}
}
