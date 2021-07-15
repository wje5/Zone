package com.pinball3d.zone.sphinx.elite.panels;

import java.lang.reflect.Field;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Quaternion;

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
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.MathHelper;

public class PanelMap extends Panel {
	private static BlockFluidRenderer fluidRenderer;
	private float xOffset, yOffset, zOffset, rotX = -45, rotY = 45, rotZ = 0, scale = 4F;
	private MapRenderManager renderManager;
	private boolean inited;

	public PanelMap(EliteMainwindow parent, PanelGroup parentGroup) {
		super(parent, parentGroup, new FormattedString(I18n.format("elite.panel.map")));
		xOffset = (float) -parent.mc.player.posX + parentGroup.getPanelWidth() / 2;
		yOffset = (float) -parent.mc.player.posZ + parentGroup.getPanelHeight() / 2;
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
				xOffset += moveX;
				yOffset += moveY;
			} else {
				rotX += moveY / 10F;
				rotY += moveX / -10F;
			}
		}, cancel -> {

		}) : null;
	}

	@Override
	public void close() {
		if (inited) {
			renderManager.setWorldAndLoadRenderers(null);
		}
		return;
	}

	@Override
	public void doRender(int mouseX, int mouseY, float partialTicks) {
		if (!inited) {
			renderManager = new MapRenderManager();
			renderManager.setWorldAndLoadRenderers(getParent().mc.world);
			inited = true;
		}
		GlStateManager.pushMatrix();
		GlStateManager.matrixMode(GL11.GL_PROJECTION);
		GlStateManager.pushMatrix();
		PanelGroup group = getParentGroup();
		GlStateManager.viewport(group.getPanelX(),
				getParent().getHeight() - (group.getPanelY() + group.getPanelHeight()), group.getPanelWidth(),
				group.getPanelHeight());
		GlStateManager.matrixMode(GL11.GL_PROJECTION);
		GlStateManager.loadIdentity();
		GlStateManager.ortho(0, group.getPanelWidth(), group.getPanelHeight(), 0, 0.0D, 5000.0D);
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		GlStateManager.translate(xOffset, yOffset, 500.0F);
		GlStateManager.rotate(makeQuaternion(rotX, rotY, rotZ));
		GlStateManager.scale(scale, -scale, scale);
		renderManager.doRender(mouseX, mouseY, partialTicks);
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		GlStateManager.popMatrix();
		GlStateManager.matrixMode(GL11.GL_PROJECTION);
		GlStateManager.popMatrix();
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		GlStateManager.viewport(0, 0, getParent().getWidth(), getParent().getHeight());
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

	private static Quaternion makeQuaternion(float p_188035_0_, float p_188035_1_, float p_188035_2_) {
		float f = p_188035_0_ * 0.017453292F;
		float f1 = p_188035_1_ * 0.017453292F;
		float f2 = p_188035_2_ * 0.017453292F;
		float f3 = MathHelper.sin(0.5F * f);
		float f4 = MathHelper.cos(0.5F * f);
		float f5 = MathHelper.sin(0.5F * f1);
		float f6 = MathHelper.cos(0.5F * f1);
		float f7 = MathHelper.sin(0.5F * f2);
		float f8 = MathHelper.cos(0.5F * f2);
		return new Quaternion(f3 * f6 * f8 + f4 * f5 * f7, f4 * f5 * f8 - f3 * f6 * f7, f3 * f5 * f8 + f4 * f6 * f7,
				f4 * f6 * f8 - f3 * f5 * f7);
	}
}
