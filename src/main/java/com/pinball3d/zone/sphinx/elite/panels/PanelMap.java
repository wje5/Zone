package com.pinball3d.zone.sphinx.elite.panels;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Map;
import java.util.TreeMap;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Quaternion;

import com.pinball3d.zone.core.LoadingPluginZone;
import com.pinball3d.zone.sphinx.elite.Color;
import com.pinball3d.zone.sphinx.elite.Drag;
import com.pinball3d.zone.sphinx.elite.EliteMainwindow;
import com.pinball3d.zone.sphinx.elite.EliteRenderHelper;
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

				renderManager.cameraX += moveX * Math.cos(renderManager.cameraYaw / 180F * Math.PI);
				renderManager.cameraZ += moveX * Math.sin(renderManager.cameraYaw / 180F * Math.PI);
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

		drawRotaryBall(getParentGroup().getWidth() - 90, 10, mouseX, mouseY);
//		drawRotaryBall3D(getParentGroup().getWidth() - 90, 10, mouseX, mouseY);
		super.doRender(mouseX, mouseY, partialTicks);
	}

	public void drawRotaryBall(int x, int y, int mouseX, int mouseY) {
		int length = 33;
		int x1 = (int) (-MathHelper.cos(renderManager.cameraYaw * 0.017453292F) * length);
		int y1 = (int) (MathHelper.sin(renderManager.cameraPitch * 0.017453292F)
				* MathHelper.sin(renderManager.cameraYaw * 0.017453292F) * length);
		float z1 = MathHelper.cos((renderManager.cameraYaw + 90) * 0.017453292F)
				* MathHelper.cos((renderManager.cameraPitch + 180) * 0.017453292F) * length;
		int x2 = 0;
		int y2 = (int) (MathHelper.cos((renderManager.cameraPitch + 180) * 0.017453292F) * length);
		float z2 = -MathHelper.sin((renderManager.cameraPitch + 180) * 0.017453292F) * length;
		int x3 = (int) (MathHelper.cos((renderManager.cameraYaw + 90) * 0.017453292F) * length);
		int y3 = (int) (-MathHelper.sin(renderManager.cameraPitch * 0.017453292F)
				* MathHelper.sin((renderManager.cameraYaw + 90) * 0.017453292F) * length);
		float z3 = MathHelper.cos((renderManager.cameraYaw) * 0.017453292F)
				* MathHelper.cos((renderManager.cameraPitch + 180) * 0.017453292F) * length;

		Map<Float, Runnable> map = new TreeMap<Float, Runnable>((a, b) -> a < b ? 1 : -1);
		map.put(z1, () -> {
			EliteRenderHelper.drawTexture(EliteMainwindow.ELITE, x - x1 + 33, y - y1 + 33,
					renderManager.cameraYaw >= 0 ? 0 : 15, 100, 15, 15);
			EliteRenderHelper.drawLine(x + 40, y + 40, x + 40 + x1, y + 40 + y1,
					renderManager.cameraYaw >= 0 ? new Color(0xFFFF3352) : new Color(0xFF9C3645));
		});
		map.put(z2, () -> {
			EliteRenderHelper.drawTexture(EliteMainwindow.ELITE, x - x2 + 33, y - y2 + 33,
					renderManager.cameraPitch >= 0 ? 0 : 15, 130, 15, 15);
			EliteRenderHelper.drawLine(x + 40, y + 40, x + 40 + x2, y + 40 + y2,
					renderManager.cameraPitch >= 0 ? new Color(0xFF2890FF) : new Color(0xFF30649C));
		});
		map.put(z3, () -> {
			EliteRenderHelper.drawTexture(EliteMainwindow.ELITE, x - x3 + 33, y - y3 + 33,
					Math.abs(renderManager.cameraYaw) >= 90 ? 0 : 15, 115, 15, 15);
			EliteRenderHelper.drawLine(x + 40, y + 40, x + 40 + x3, y + 40 + y3,
					Math.abs(renderManager.cameraYaw) >= 90 ? new Color(0xFF8BDC00) : new Color(0xFF628A1C));
		});
		map.put(-z1, () -> EliteRenderHelper.drawTexture(EliteMainwindow.ELITE, x + x1 + 33, y + y1 + 33,
				renderManager.cameraYaw >= 0 ? 15 : 0, 100, 15, 15));
		map.put(-z2, () -> EliteRenderHelper.drawTexture(EliteMainwindow.ELITE, x + x2 + 33, y + y2 + 33,
				renderManager.cameraPitch >= 0 ? 15 : 0, 130, 15, 15));
		map.put(-z3, () -> EliteRenderHelper.drawTexture(EliteMainwindow.ELITE, x + x3 + 33, y + y3 + 33,
				Math.abs(renderManager.cameraYaw) >= 90 ? 15 : 0, 115, 15, 15));
		map.forEach((a, b) -> b.run());
	}

	public void drawRotaryBall3D(int x, int y, int mouseX, int mouseY) {
		if (Math.sqrt((mouseX - x - 40) * (mouseX - x - 40) + (mouseY - y - 40) * (mouseY - y - 40)) < 40) {
			EliteRenderHelper.drawTexture(EliteMainwindow.ELITE, x, y, 78, 85, 80, 80);
		}

		GlStateManager.enableDepth();
		GL11.glPushMatrix();
		GL11.glTranslatef(x + 40, y + 40, 0);
		Quaternion q = MapRenderManager.makeQuaternion(-renderManager.cameraPitch, renderManager.cameraYaw + 180F, 0);
		GlStateManager.rotate(q);
		q.x = -q.x;
		q.y = -q.y;
		q.z = -q.z;

		int length = 33;
		EliteRenderHelper.drawLine(0, 0, 0, length, 0, 0,
				renderManager.cameraYaw >= 0 ? new Color(0xFFFF3352) : new Color(0xFF9C3645));
		EliteRenderHelper.drawLine(0, 0, 0, 0, -length, 0,
				renderManager.cameraPitch >= 0 ? new Color(0xFF2890FF) : new Color(0xFF30649C));
		EliteRenderHelper.drawLine(0, 0, 0, 0, 0, length,
				Math.abs(renderManager.cameraYaw) >= 90 ? new Color(0xFF8BDC00) : new Color(0xFF628A1C));

		GlStateManager.disableDepth();
		GL11.glPushMatrix();
		GL11.glTranslatef(-length, 0, 0);
		GlStateManager.rotate(q);
		EliteRenderHelper.drawTexture(EliteMainwindow.ELITE, -7, -7, renderManager.cameraYaw >= 0 ? 0 : 15, 100, 15,
				15);
		GL11.glPopMatrix();

		GL11.glPushMatrix();
		GL11.glTranslatef(length, 0, 0);
		GlStateManager.rotate(q);
		EliteRenderHelper.drawTexture(EliteMainwindow.ELITE, -7, -7, renderManager.cameraYaw < 0 ? 0 : 15, 100, 15, 15);
		GL11.glPopMatrix();

		GL11.glPushMatrix();
		GL11.glTranslatef(0, length, 0);
		GlStateManager.rotate(q);
		EliteRenderHelper.drawTexture(EliteMainwindow.ELITE, -7, -7, renderManager.cameraPitch >= 0 ? 0 : 15, 130, 15,
				15);
		GL11.glPopMatrix();

		GL11.glPushMatrix();
		GL11.glTranslatef(0, -length, 0);
		GlStateManager.rotate(q);
		EliteRenderHelper.drawTexture(EliteMainwindow.ELITE, -7, -7, renderManager.cameraPitch < 0 ? 0 : 15, 130, 15,
				15);
		GL11.glPopMatrix();

		GL11.glPushMatrix();
		GL11.glTranslatef(0, 0, -length);
		GlStateManager.rotate(q);
		EliteRenderHelper.drawTexture(EliteMainwindow.ELITE, -7, -7, Math.abs(renderManager.cameraYaw) >= 90 ? 0 : 15,
				115, 15, 15);
		GL11.glPopMatrix();

		GL11.glPushMatrix();
		GL11.glTranslatef(0, 0, length);
		GlStateManager.rotate(q);
		EliteRenderHelper.drawTexture(EliteMainwindow.ELITE, -7, -7, Math.abs(renderManager.cameraYaw) < 90 ? 0 : 15,
				115, 15, 15);
		GL11.glPopMatrix();
		GlStateManager.enableDepth();

		GL11.glPopMatrix();
	}

	public static void printMatrix() {
		ByteBuffer bb = ByteBuffer.allocateDirect(64);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		FloatBuffer fb = bb.asFloatBuffer();
		GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, fb);
		System.out.println(fb.get() + "|" + fb.get() + "|" + fb.get() + "|" + fb.get());
		System.out.println(fb.get() + "|" + fb.get() + "|" + fb.get() + "|" + fb.get());
		System.out.println(fb.get() + "|" + fb.get() + "|" + fb.get() + "|" + fb.get());
		System.out.println(fb.get() + "|" + fb.get() + "|" + fb.get() + "|" + fb.get());
		System.out.println(fb);
	}
}
