package com.pinball3d.zone.sphinx.subscreen;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.pinball3d.zone.gui.IHasSubscreen;
import com.pinball3d.zone.gui.Subscreen;
import com.pinball3d.zone.gui.component.ScrollingEdgeList;
import com.pinball3d.zone.gui.component.ScrollingEdgeList.ListBar;
import com.pinball3d.zone.gui.component.TexturedButton;
import com.pinball3d.zone.network.ConnectHelperClient;
import com.pinball3d.zone.network.ConnectionHelper.Type;
import com.pinball3d.zone.network.MessageChangeGravatar;
import com.pinball3d.zone.network.MessageDeleteUser;
import com.pinball3d.zone.network.MessageReviewUser;
import com.pinball3d.zone.network.MessageTransferAdmin;
import com.pinball3d.zone.network.NetworkHandler;
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
	private ScrollingEdgeList list;
	private ThreadDownloadImageData image;
	private UUID jumpTo;
	public static File dir;

	public SubscreenManageUser(IHasSubscreen parent) {
		this(parent, getDisplayWidth() / 2 - 210, getDisplayHeight() / 2 - 100);
	}

	public SubscreenManageUser(IHasSubscreen parent, int x, int y) {
		super(parent, x, y, 360, 200, true);
		jumpTo = mc.player.getUniqueID();
		addComponent(list = new ScrollingEdgeList(this, 0, 9, 195).setOnChange(i -> {
			image = null;
			return true;
		}));
		addComponent(new TexturedButton(this, 83, 73, ICONS_4, 60, 120, 60, 60, 0.25F, () -> {
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
				new TexturedButton(this, 100, 73, ICONS_4, 180, 120, 60, 60, 0.25F,
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
												() -> !list.isEmpty() && ((UserData) list.get().getData()).online ? 100
														: 83));
		addComponent(
				new TexturedButton(this, 117, 73, ICONS_4, 0, 180, 60, 60, 0.25F,
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
												}).setXSupplier(() -> !list.isEmpty()
														&& ((UserData) list.get().getData()).online ? 117 : 100));
		addComponent(new TexturedButton(this, 83, 73, ICONS_5, 0, 0, 60, 60, 0.25F,
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
		addComponent(new TexturedButton(this, 100, 73, ICONS_4, 0, 180, 60, 60, 0.25F,
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
				list.addBar(new ListBar(e.name, () -> {
				}).setData(e));
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
		if (isLeft && x >= 83 && x <= 123 && y >= 28 && y <= 68) {
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
		Util.drawTexture(UI_BORDER, 55, -5, 0, 0, 99, 99, 0.5F);
		Util.drawTexture(UI_BORDER, 315, -5, 99, 0, 99, 99, 0.5F);
		Util.drawTexture(UI_BORDER, 55, 155, 0, 99, 99, 99, 0.5F);
		Util.drawTexture(UI_BORDER, 315, 155, 99, 99, 99, 99, 0.5F);
		Gui.drawRect(104, 0, 315, 44, 0x2F000000);
		Gui.drawRect(60, 44, 360, 155, 0x2F000000);
		Gui.drawRect(104, 155, 315, 200, 0x2F000000);
		Util.renderGlowHorizonLine(70, 20, 280);
		Gui.drawRect(76, 24, 344, 194, 0x651CC3B5);
		Util.renderGlowString(I18n.format("sphinx.manage_user"), 75, 8);
		Util.renderGlowBorder(75, 23, 270, 172);
		FontRenderer fr = Util.getFontRenderer();
		ListBar bar = list.get();
		if (bar != null) {
			UserData user = (UserData) bar.getData();
			Util.drawBorder(83, 28, 40, 40, 1, 0xFF1ECCDE);
			drawGravatar(user.email, 84, 29);
			if (mouseX >= 83 && mouseX <= 123 && mouseY >= 28 && mouseY <= 68) {
				Util.drawTexture(ICONS, 112, 57, 216, 0, 40, 40, 0.25F);
			}
			Util.renderGlowString(user.name, 128, 30);
			if (user.reviewing) {
				fr.drawString(I18n.format("sphinx.reviewing"), 128, 37, 0xFFBFBFBF);
			} else {
				Util.renderGlowString(user.admin ? I18n.format("sphinx.admin") : I18n.format("sphinx.user"), 128, 37);
			}
			if (user.online) {
				Util.renderGlowString(I18n.format("sphinx.online"), 128, 44);
			} else {
				fr.drawString(I18n.format("sphinx.offline"), 128, 44, 0xFFBFBFBF);
			}

		}
	}

	private void drawGravatar(String mail, int x, int y) {
		Util.resetOpenGl();
		if (image == null) {
			if (dir == null) {
				try {
					Field[] fields = Minecraft.class.getDeclaredFields();
					for (Field f : fields) {
						if (f.getName().equals("field_110446_Y") || f.getName().equals("fileAssets")) {
							f.setAccessible(true);
							dir = new File((File) f.get(Minecraft.getMinecraft()), "gravatars");
						}
					}
				} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			String md5 = Util.genEmailMd5(mail);
			if (md5 == null) {
				md5 = "default";
			}
			image = new ThreadDownloadImageData(new File(dir, md5), Util.genGravatarUrl(mail), null,
					new ImageBufferDownload());
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
