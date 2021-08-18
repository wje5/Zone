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
				renderManager.cameraX += moveX;
				renderManager.cameraZ += moveY;// FIXME
			} else {
				renderManager.cameraRotX += moveY / 10F;
				renderManager.cameraRotY += moveX / -10F;
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
		super.doRender(mouseX, mouseY, partialTicks);
	}
}
