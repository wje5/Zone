package com.pinball3d.zone.sphinx.elite.panels;

import java.lang.reflect.Field;

import com.pinball3d.zone.core.LoadingPluginZone;
import com.pinball3d.zone.sphinx.elite.Color;
import com.pinball3d.zone.sphinx.elite.Drag;
import com.pinball3d.zone.sphinx.elite.EliteMainwindow;
import com.pinball3d.zone.sphinx.elite.FontHandler;
import com.pinball3d.zone.sphinx.elite.FormattedString;
import com.pinball3d.zone.sphinx.elite.PanelGroup;
import com.pinball3d.zone.sphinx.elite.map.MapRenderManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockFluidRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.resources.I18n;

public class PanelMap extends Panel {
	private static BlockFluidRenderer fluidRenderer;
	private MapRenderManager renderManager;
	private boolean inited;

	public PanelMap(EliteMainwindow parent, PanelGroup parentGroup) {
		super(parent, parentGroup, new FormattedString(I18n.format("elite.panel.map")));
		if (fluidRenderer == null) {
			BlockRendererDispatcher dispatcher = parent.mc.getBlockRendererDispatcher();
			try {
				Field f = BlockRendererDispatcher.class
						.getDeclaredField(LoadingPluginZone.runtimeDeobf ? "field_175025_e" : "fluidRenderer");
				f.setAccessible(true);
				fluidRenderer = (BlockFluidRenderer) f.get(dispatcher);
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public Drag mouseClicked(int mouseX, int mouseY, int mouseButton) {
		return isMouseInPanel(mouseX, mouseY) ? new Drag((x, y, moveX, moveY) -> {
			if (mouseButton == 0) {
//				renderManager.cameraX += moveX;
//				renderManager.cameraZ += moveY;// FIXME
				renderManager.cameraY += moveY * Math.cos(renderManager.cameraPitch / 180F * Math.PI);
				double d = moveY * Math.sin(renderManager.cameraPitch / 180F * Math.PI);
				renderManager.cameraZ += d * Math.cos(renderManager.cameraYaw / 180F * Math.PI);
				renderManager.cameraX -= d * Math.sin(renderManager.cameraYaw / 180F * Math.PI);
				System.out.println(d * Math.cos(renderManager.cameraYaw / 180F * Math.PI) + "|"
						+ d * Math.sin(renderManager.cameraYaw / 180F * Math.PI));
				System.out.println(d);
			} else {
				renderManager.cameraPitch = (renderManager.cameraPitch + moveY * 0.1F) % 360;
				renderManager.cameraPitch = renderManager.cameraPitch > 180 ? renderManager.cameraPitch - 360F
						: renderManager.cameraPitch < -180 ? renderManager.cameraPitch + 360F
								: renderManager.cameraPitch;
				renderManager.cameraYaw = (renderManager.cameraYaw + moveX * 0.1F) % 360;
				renderManager.cameraYaw = renderManager.cameraYaw > 180 ? renderManager.cameraYaw - 360F
						: renderManager.cameraYaw < -180 ? renderManager.cameraYaw + 360F : renderManager.cameraYaw;
			}
		}, cancel -> {

		}) : null;
	}

	@Override
	public void onMouseScrolled(int mouseX, int mouseY, int distance) {
		if (isMouseInPanel(mouseX, mouseY)) {
			renderManager.scale += distance;
		}
	}

	@Override
	public void close() {
		if (inited) {
			renderManager.setWorldAndLoadRenderers(null);
		}
		return;
	}

	public MapRenderManager getRenderManager() {
		return renderManager;
	}

	@Override
	public void doRender(int mouseX, int mouseY, float partialTicks) {
		if (!inited) {
			renderManager = new MapRenderManager();
			renderManager.setWorldAndLoadRenderers(getParent().mc.world);
			inited = true;
		}
		PanelGroup group = getParentGroup();
		renderManager.doRender(group.getPanelX(), group.getPanelY(), group.getPanelWidth(), group.getPanelHeight(),
				mouseX, mouseY, partialTicks);

		FontHandler.renderText(10, 0, new FormattedString("(123中(文测试）AaBbCc"), Color.TEXT_LIGHT,
				getParentGroup().getWidth());
		FontHandler.renderText(10, 20, getName(), Color.TEXT_LIGHT, getParentGroup().getWidth());
		FontHandler.renderText(10, 40, new FormattedString("§o我§n能吞§l下玻璃而§r不伤身§l体("), Color.TEXT_LIGHT,
				getParentGroup().getWidth());
		FontHandler.renderText(10, 60, new FormattedString("FPS:" + Minecraft.getDebugFPS()), Color.TEXT_LIGHT,
				getParentGroup().getWidth());
		FontHandler.renderText(0, 80,
				new FormattedString("§o我§n能吞§l下玻璃而§r不伤身§l体(KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK"),
				Color.TEXT_LIGHT, getParentGroup().getWidth());

		FontHandler.renderText(0, 100,
				new FormattedString("pitch:" + renderManager.cameraPitch + " yaw:" + renderManager.cameraYaw),
				Color.TEXT_LIGHT, getParentGroup().getWidth());
		FontHandler.renderText(0, 120,
				new FormattedString(
						"x:" + renderManager.cameraX + " y:" + renderManager.cameraY + " z:" + renderManager.cameraZ),
				Color.TEXT_LIGHT, getParentGroup().getWidth());

//		int length = 33;
//		int x = getParentGroup().getWidth() - 90;
//		int y = 10;
//		int x1 = (int) (MathHelper.cos(renderManager.cameraYaw * 0.017453292F) * length);
//		int y1 = (int) (MathHelper.sin(renderManager.cameraPitch * 0.017453292F)
//				* MathHelper.sin(renderManager.cameraYaw * 0.017453292F) * length);
//		EliteRenderHelper.drawLine(x + 40, y + 40, x + 40 + x1, y + 40 + y1, new Color(0xFF9C3645));
//		int x2 = 0;
//		int y2 = (int) (-MathHelper.cos(renderManager.cameraPitch * 0.017453292F) * length);
//		EliteRenderHelper.drawLine(x + 40, y + 40, x + 40 + x2, y + 40 + y2, new Color(0xFF2890FF));
//		int x3 = (int) (-MathHelper.cos((renderManager.cameraYaw + 90) * 0.017453292F) * length);
//		int y3 = (int) (-MathHelper.sin(renderManager.cameraPitch * 0.017453292F)
//				* MathHelper.sin((renderManager.cameraYaw + 90) * 0.017453292F) * length);
//		EliteRenderHelper.drawLine(x + 40, y + 40, x + 40 + x3, y + 40 + y3, new Color(0xFF628A1C));
//
//		EliteRenderHelper.drawTexture(EliteMainwindow.ELITE, x - x1 + 33, y - y1 + 33,
//				renderManager.cameraYaw < 0 ? 0 : 15, 100, 15, 15);
//		EliteRenderHelper.drawTexture(EliteMainwindow.ELITE, x - x2 + 33, y - y2 + 33,
//				renderManager.cameraPitch < 0 ? 0 : 15, 130, 15, 15);
//		EliteRenderHelper.drawTexture(EliteMainwindow.ELITE, x - x3 + 33, y - y3 + 33,
//				Math.abs(renderManager.cameraYaw) < 90 ? 0 : 15, 115, 15, 15);
//
//		EliteRenderHelper.drawTexture(EliteMainwindow.ELITE, x + x1 + 33, y + y1 + 33,
//				renderManager.cameraYaw < 0 ? 15 : 0, 100, 15, 15);
//		EliteRenderHelper.drawTexture(EliteMainwindow.ELITE, x + x2 + 33, y + y2 + 33,
//				renderManager.cameraPitch < 0 ? 15 : 0, 130, 15, 15);
//		EliteRenderHelper.drawTexture(EliteMainwindow.ELITE, x + x3 + 33, y + y3 + 33,
//				Math.abs(renderManager.cameraYaw) < 90 ? 15 : 0, 115, 15, 15);
//
//		if (Math.sqrt((mouseX - x - 40) * (mouseX - x - 40) + (mouseY - y - 40) * (mouseY - y - 40)) < 40) {
//			EliteRenderHelper.drawTexture(EliteMainwindow.ELITE, x, y, 78, 85, 80, 80);
//		}
		super.doRender(mouseX, mouseY, partialTicks);
	}
}
