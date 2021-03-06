package com.pinball3d.zone.sphinx.subscreen;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.pinball3d.zone.gui.Subscreen;
import com.pinball3d.zone.network.ConnectHelperClient;
import com.pinball3d.zone.network.ConnectionHelper.Type;
import com.pinball3d.zone.network.MessageChangeGravatar;
import com.pinball3d.zone.network.MessageDeleteUser;
import com.pinball3d.zone.network.MessageReviewUser;
import com.pinball3d.zone.network.MessageTransferAdmin;
import com.pinball3d.zone.network.NetworkHandler;
import com.pinball3d.zone.sphinx.IHasSubscreen;
import com.pinball3d.zone.sphinx.component.ScrollingEdgeList;
import com.pinball3d.zone.sphinx.component.ScrollingEdgeList.ListBar;
import com.pinball3d.zone.sphinx.component.TexturedButton;
import com.pinball3d.zone.sphinx.map.MapHandler;
import com.pinball3d.zone.tileentity.TEProcessingCenter.UserData;
import com.pinball3d.zone.util.Util;
import com.pinball3d.zone.util.WorldPos;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;

public class SubscreenManageUser extends Subscreen {
	private static final ResourceLocation ICONS = new ResourceLocation("zone:textures/gui/sphinx/icons.png");
	private static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/gui/sphinx/ui_border.png");
	private static final ResourceLocation TEXTURE_4 = new ResourceLocation("zone:textures/gui/sphinx/icons_4.png");
	private static final ResourceLocation TEXTURE_5 = new ResourceLocation("zone:textures/gui/sphinx/icons_5.png");
	private ScrollingEdgeList list;
	private ThreadDownloadImageData image;
	private UUID jumpTo;

	public SubscreenManageUser(IHasSubscreen parent) {
		this(parent, getDisplayWidth() / 2 - 210, getDisplayHeight() / 2 - 100);
	}

	public SubscreenManageUser(IHasSubscreen parent, int x, int y) {
		super(parent, x, y, 360, 200, true);
		jumpTo = mc.player.getUniqueID();
		addComponent(list = new ScrollingEdgeList(this, this.x, this.y + 9, 195).setOnChange(i -> {
			image = null;
			return true;
		}));
		addComponent(new TexturedButton(this, x + 83, y + 73, TEXTURE_4, 60, 120, 60, 60, 0.25F, () -> {
			WorldPos pos = ((UserData) list.get().getData()).pos;
			MapHandler.focus(pos.getPos().getX(), pos.getPos().getZ());
			while (!parent.getSubscreens().empty()) {
				parent.removeScreen(parent.getSubscreens().peek());
			}
		}).setEnable(() -> {
			ListBar bar = list.get();
			if (bar != null) {
				UserData data = (UserData) bar.getData();
				if (!data.reviewing && data.online) {
					return true;
				}
			}
			return false;
		}));
		addComponent(
				new TexturedButton(this, x + 100, y + 73, TEXTURE_4, 180, 120, 60, 60, 0.25F,
						() -> parent.putScreen(new SubscreenConfirmBox(parent, I18n.format("sphinx.transfer_admin"),
								I18n.format("sphinx.confirm_transfer_admin"),
								() -> NetworkHandler.instance.sendToServer(MessageTransferAdmin.newMessage(
										ConnectHelperClient.getInstance().getNetworkPos(),
										((UserData) list.get().getData()).uuid))))).setEnable(() -> {
											if (!ConnectHelperClient.getInstance().isAdmin()) {
												return false;
											}
											ListBar bar = list.get();
											if (bar != null) {
												UserData data = (UserData) bar.getData();
												if (!data.admin && !data.reviewing) {
													return true;
												}
											}
											return false;
										}).setXSupplier(
												() -> ((UserData) list.get().getData()).online ? x + 100 : x + 83));
		addComponent(
				new TexturedButton(this, x + 117, y + 73, TEXTURE_4, 0, 180, 60, 60, 0.25F,
						() -> parent
								.putScreen(new SubscreenConfirmBox(parent, I18n.format("sphinx.remove_user"),
										I18n.format("sphinx.confirm_remove_user"),
										() -> NetworkHandler.instance.sendToServer(MessageDeleteUser.newMessage(
												ConnectHelperClient.getInstance().getNetworkPos(),
												((UserData) list.get().getData()).uuid))))).setEnable(() -> {
													if (!ConnectHelperClient.getInstance().isAdmin()) {
														return false;
													}
													ListBar bar = list.get();
													if (bar != null) {
														UserData data = (UserData) bar.getData();
														if (!data.admin && !data.reviewing) {
															return true;
														}
													}
													return false;
												}).setXSupplier(() -> ((UserData) list.get().getData()).online ? x + 117
														: x + 100));
		addComponent(new TexturedButton(this, x + 83, y + 73, TEXTURE_5, 0, 0, 60, 60, 0.25F,
				() -> NetworkHandler.instance.sendToServer(MessageReviewUser.newMessage(
						ConnectHelperClient.getInstance().getNetworkPos(), ((UserData) list.get().getData()).uuid)))
								.setEnable(() -> {
									if (!ConnectHelperClient.getInstance().isAdmin()) {
										return false;
									}
									ListBar bar = list.get();
									if (bar != null) {
										UserData data = (UserData) bar.getData();
										if (data.reviewing) {
											return true;
										}
									}
									return false;
								}));
		addComponent(new TexturedButton(this, x + 100, y + 73, TEXTURE_4, 0, 180, 60, 60, 0.25F,
				() -> NetworkHandler.instance.sendToServer(MessageDeleteUser.newMessage(
						ConnectHelperClient.getInstance().getNetworkPos(), ((UserData) list.get().getData()).uuid)))
								.setEnable(() -> {
									if (!ConnectHelperClient.getInstance().isAdmin()) {
										return false;
									}
									ListBar bar = list.get();
									if (bar != null) {
										UserData data = (UserData) bar.getData();
										if (data.reviewing) {
											return true;
										}
									}
									return false;
								}));
	}

	public SubscreenManageUser setUser(UUID uuid) {
		jumpTo = uuid;
		return this;
	}

	@Override
	public Set<Type> getDataTypes() {
		Set<Type> s = super.getDataTypes();
		s.add(Type.USERS);
		return s;
	}

	public void updateList() {
		int s = list.list.size();
		list.clear();
		if (ConnectHelperClient.getInstance().hasData()) {
			List<UserData> l = ConnectHelperClient.getInstance().getUsers();
			l.forEach(e -> {
				ListBar bar = new ListBar(e.name, () -> {
				});
				bar.setData(e);
				list.addBar(bar);
			});
		}
		if (list.list.size() != s) {
			list.change(0);
		}
		if (jumpTo != null && !list.list.isEmpty()) {
			for (int i = 0; i < list.list.size(); i++) {
				if (((UserData) list.list.get(i).getData()).uuid.equals(jumpTo)) {
					list.change(i);
				}
			}
			jumpTo = null;
		}
	}

	@Override
	public void onClick(int x, int y, boolean isLeft) {
		super.onClick(x, y, isLeft);
		if (x - this.x >= 83 && x - this.x <= 123 && y - this.y >= 28 && y - this.y <= 68) {
			parent.putScreen(new SubscreenTextInputBox(parent, I18n.format("sphinx.set_gravatar"),
					I18n.format("sphinx.set_gravatar_email"), s -> {
						NetworkHandler.instance.sendToServer(
								MessageChangeGravatar.newMessage(ConnectHelperClient.getInstance().getNetworkPos(), s));
						image = new ThreadDownloadImageData(null, Util.genGravatarUrl(s), null,
								new ImageBufferDownload());
						Minecraft.getMinecraft().getTextureManager().loadTexture(
								new ResourceLocation("zone:gravatars/" + StringUtils.stripControlCodes(s)), image);
					}, x, y).setText(list.get() == null ? "" : ((UserData) list.get().getData()).email));
		}
	}

	@Override
	public void doRenderBackground(int mouseX, int mouseY) {
		super.doRenderBackground(mouseX, mouseY);
		updateList();
		Util.drawTexture(TEXTURE, x + 55, y - 5, 0, 0, 99, 99, 0.5F);
		Util.drawTexture(TEXTURE, x + 315, y - 5, 99, 0, 99, 99, 0.5F);
		Util.drawTexture(TEXTURE, x + 55, y + 155, 0, 99, 99, 99, 0.5F);
		Util.drawTexture(TEXTURE, x + 315, y + 155, 99, 99, 99, 99, 0.5F);
		Gui.drawRect(x + 104, y, x + 315, y + 44, 0x2F000000);
		Gui.drawRect(x + 60, y + 44, x + 360, y + 155, 0x2F000000);
		Gui.drawRect(x + 104, y + 155, x + 315, y + 200, 0x2F000000);
		Util.renderGlowHorizonLine(x + 70, y + 20, 280);
		Gui.drawRect(x + 76, y + 24, x + 344, y + 194, 0x651CC3B5);
		Util.renderGlowString(I18n.format("sphinx.manage_user"), x + 75, y + 8);
		Util.renderGlowBorder(x + 75, y + 23, 270, 172);
		FontRenderer fr = Util.getFontRenderer();
		ListBar bar = list.get();
		if (bar != null) {
			UserData user = (UserData) bar.getData();
			Util.drawBorder(x + 83, y + 28, 40, 40, 1, 0xFF1ECCDE);
			drawGravatar(user.email, x + 84, y + 29);
			if (mouseX >= x + 83 && mouseX <= x + 123 && mouseY >= y + 28 && mouseY <= y + 68) {
				Util.drawTexture(ICONS, x + 112, y + 57, 216, 0, 40, 40, 0.25F);
			}
			Util.renderGlowString(user.name, x + 128, y + 30);
			if (user.reviewing) {
				fr.drawString(I18n.format("sphinx.reviewing"), x + 128, y + 37, 0xFFBFBFBF);
			} else {
				Util.renderGlowString(user.admin ? I18n.format("sphinx.admin") : I18n.format("sphinx.user"), x + 128,
						y + 37);
			}
			if (user.online) {
				Util.renderGlowString(I18n.format("sphinx.online"), x + 128, y + 44);
			} else {
				fr.drawString(I18n.format("sphinx.offline"), x + 128, y + 44, 0xFFBFBFBF);
			}

		}
	}

	private void drawGravatar(String mail, int x, int y) {
		Util.resetOpenGl();
		if (image == null) {
			image = new ThreadDownloadImageData(null, Util.genGravatarUrl(mail), null, new ImageBufferDownload());
			Minecraft.getMinecraft().getTextureManager()
					.loadTexture(new ResourceLocation("zone:gravatars/" + StringUtils.stripControlCodes(mail)), image);
		}
		Minecraft.getMinecraft().getTextureManager()
				.bindTexture(new ResourceLocation("zone:gravatars/" + StringUtils.stripControlCodes(mail)));
		Gui.drawScaledCustomSizeModalRect(x, y, 0, 0, 256, 256, 38, 38, 256, 256);
	}

	@Override
	public boolean isBlockOtherSubscreen() {
		return true;
	}
}
