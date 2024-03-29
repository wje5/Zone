package com.pinball3d.zone.sphinx.elite.panels;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Map;
import java.util.TreeMap;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.pinball3d.zone.core.LoadingPluginZone;
import com.pinball3d.zone.sphinx.elite.Color;
import com.pinball3d.zone.sphinx.elite.Drag;
import com.pinball3d.zone.sphinx.elite.DropDownList;
import com.pinball3d.zone.sphinx.elite.EliteMainwindow;
import com.pinball3d.zone.sphinx.elite.EliteRenderHelper;
import com.pinball3d.zone.sphinx.elite.FontHandler;
import com.pinball3d.zone.sphinx.elite.FormattedString;
import com.pinball3d.zone.sphinx.elite.PanelGroup;
import com.pinball3d.zone.sphinx.elite.map.MapRenderManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockFluidRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.MathHelper;

public class PanelMap extends Panel {
	private static BlockFluidRenderer fluidRenderer;
	private MapRenderManager renderManager;
	private boolean inited;

	public PanelMap(EliteMainwindow parent, PanelGroup parentGroup) {
		super(parent, parentGroup, "map", new FormattedString(I18n.format("elite.panel.map")));
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
		if (!isMouseInPanel(mouseX, mouseY)) {
			return null;
		}
		if (mouseButton == 0) {
			int dx = getParentGroup().getWidth() - 50 - mouseX;
			int dy = 50 - mouseY;
			if (Math.sqrt(dx * dx + dy * dy) < 40) {
				return new Drag(mouseButton, (x, y, moveX, moveY) -> {
					if (inited) {
						float speed = 0.5F;
						renderManager.cameraPitch = (renderManager.cameraPitch + moveY * speed) % 360;
						renderManager.cameraPitch = renderManager.cameraPitch > 180 ? renderManager.cameraPitch - 360F
								: renderManager.cameraPitch < -180 ? renderManager.cameraPitch + 360F
										: renderManager.cameraPitch;
						renderManager.cameraYaw = (renderManager.cameraYaw + moveX * speed) % 360;
						renderManager.cameraYaw = renderManager.cameraYaw > 180 ? renderManager.cameraYaw - 360F
								: renderManager.cameraYaw < -180 ? renderManager.cameraYaw + 360F
										: renderManager.cameraYaw;
					}
				}, (x, y, cancel) -> {
				}, true);
			}
			return new Drag(mouseButton, (x, y, moveX, moveY) -> {
			}, (x, y, cancel) -> {
				if (!cancel) {
					if (inited) {
						renderManager.selectedRayTraceResult = renderManager.rayTraceResult;
					}
				}
			});
		} else if (mouseButton == 1) {
			getParent().setDropDownList(createDropDownList(mouseX, mouseY));
			return new Drag(mouseButton);
		} else if (mouseButton == 2) {
			return isMouseInPanel(mouseX, mouseY) ? new Drag(mouseButton, (x, y, moveX, moveY) -> {
				if (inited) {
					renderManager.cameraPitch = (renderManager.cameraPitch + moveY * 0.1F) % 360;
					renderManager.cameraPitch = renderManager.cameraPitch > 180 ? renderManager.cameraPitch - 360F
							: renderManager.cameraPitch < -180 ? renderManager.cameraPitch + 360F
									: renderManager.cameraPitch;
					renderManager.cameraYaw = (renderManager.cameraYaw + moveX * 0.1F) % 360;
					renderManager.cameraYaw = renderManager.cameraYaw > 180 ? renderManager.cameraYaw - 360F
							: renderManager.cameraYaw < -180 ? renderManager.cameraYaw + 360F : renderManager.cameraYaw;
				}
			}, (x, y, cancel) -> {
			}) : null;
		} else {
			return new Drag(mouseButton);
		}
	}

	public DropDownList createDropDownList(int mouseX, int mouseY) {
		DropDownList list = new DropDownList(getParent(), null, getX() + mouseX, getY() + mouseY);
		list.addBar(new DropDownList.ButtonBar(
				new FormattedString(I18n.format("elite.panel.map.droplist.connect_to_network")),
				new FormattedString("")));
		return list;
	}

	@Override
	public void mouseScrolled(int mouseX, int mouseY, int distance) {
		if (isMouseInPanel(mouseX, mouseY)) {
			if (inited) {
				renderManager.scale += distance / 120F;
			}
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
			renderManager.cameraX = getParent().getTerminalPos().getPos().getX();
			renderManager.cameraZ = getParent().getTerminalPos().getPos().getZ();
			renderManager.setWorldAndLoadRenderers(getParent().mc.world);
			inited = true;
		}
		if (isFocus()) {
			float speed = 0.3F * partialTicks;
			int moveForward = (Keyboard.isKeyDown(Keyboard.KEY_W) ? 1 : 0)
					- (Keyboard.isKeyDown(Keyboard.KEY_S) ? 1 : 0);
			int moveLeft = (Keyboard.isKeyDown(Keyboard.KEY_A) ? 1 : 0) - (Keyboard.isKeyDown(Keyboard.KEY_D) ? 1 : 0);
			if (moveForward != 0) {
				renderManager.cameraX += moveForward * speed * Math.sin(Math.toRadians(renderManager.cameraYaw));
				renderManager.cameraZ -= moveForward * speed * Math.cos(Math.toRadians(renderManager.cameraYaw));
			}
			if (moveLeft != 0) {
				renderManager.cameraX += moveLeft * speed * Math.sin(Math.toRadians(renderManager.cameraYaw - 90));
				renderManager.cameraZ -= moveLeft * speed * Math.cos(Math.toRadians(renderManager.cameraYaw - 90));
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
				renderManager.cameraY += speed;
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
				renderManager.cameraY -= speed;
			}
		}
		renderManager.doRender(getWidth(), getHeight(), mouseX, mouseY, partialTicks,
				getParent().doMouseHover() && !isMouseHoverRotaryBall(mouseX, mouseY));

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

		FontHandler
				.renderText(
						0, 100, new FormattedString("pitch:" + renderManager.cameraPitch + " yaw:"
								+ renderManager.cameraYaw + " scale:" + renderManager.scale),
						Color.TEXT_LIGHT, getParentGroup().getWidth());
		FontHandler.renderText(0, 120,
				new FormattedString(
						"x:" + renderManager.cameraX + " y:" + renderManager.cameraY + " z:" + renderManager.cameraZ),
				Color.TEXT_LIGHT, getParentGroup().getWidth());
		drawRotaryBall(mouseX, mouseY);
		super.doRender(mouseX, mouseY, partialTicks);
	}

	public boolean isMouseHoverRotaryBall(int mouseX, int mouseY) {
		int x = getParentGroup().getWidth() - 90;
		int y = 10;
		int dx = x + 40 - mouseX;
		int dy = y + 40 - mouseY;
		return Math.sqrt(dx * dx + dy * dy) < 40;
	}

	public void drawRotaryBall(int mouseX, int mouseY) {
		int x = getParentGroup().getWidth() - 90;
		int y = 10;
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
		});
		map.put(z2, () -> {
			EliteRenderHelper.drawTexture(EliteMainwindow.ELITE, x - x2 + 33, y - y2 + 33,
					renderManager.cameraPitch >= 0 ? 0 : 15, 130, 15, 15);
		});
		map.put(z3, () -> {
			EliteRenderHelper.drawTexture(EliteMainwindow.ELITE, x - x3 + 33, y - y3 + 33,
					Math.abs(renderManager.cameraYaw) >= 90 ? 0 : 15, 115, 15, 15);
		});
		map.put(-z1, () -> {
			EliteRenderHelper.drawLine(x + 40, y + 40, x + 40 + x1, y + 40 + y1,
					renderManager.cameraYaw >= 0 ? new Color(0xFFFF3352) : new Color(0xFF9C3645));
			EliteRenderHelper.drawTexture(EliteMainwindow.ELITE, x + x1 + 33, y + y1 + 33,
					renderManager.cameraYaw >= 0 ? 15 : 0, 100, 15, 15);
			FontHandler.renderTextCenter(x + 40 + x1, y + 32 + y1, new FormattedString("X"), new Color(0xFF2E2E2E));
		});
		map.put(-z2, () -> {
			EliteRenderHelper.drawLine(x + 40, y + 40, x + 40 + x2, y + 40 + y2,
					renderManager.cameraPitch >= 0 ? new Color(0xFF2890FF) : new Color(0xFF30649C));
			EliteRenderHelper.drawTexture(EliteMainwindow.ELITE, x + x2 + 33, y + y2 + 33,
					renderManager.cameraPitch >= 0 ? 15 : 0, 130, 15, 15);
			FontHandler.renderTextCenter(x + 40 + x2, y + 32 + y2, new FormattedString("Y"), new Color(0xFF2E2E2E));
		});
		map.put(-z3, () -> {
			EliteRenderHelper.drawLine(x + 40, y + 40, x + 40 + x3, y + 40 + y3,
					Math.abs(renderManager.cameraYaw) >= 90 ? new Color(0xFF8BDC00) : new Color(0xFF628A1C));
			EliteRenderHelper.drawTexture(EliteMainwindow.ELITE, x + x3 + 33, y + y3 + 33,
					Math.abs(renderManager.cameraYaw) >= 90 ? 15 : 0, 115, 15, 15);
			FontHandler.renderTextCenter(x + 40 + x3, y + 32 + y3, new FormattedString("Z"), new Color(0xFF2E2E2E));
		});
		if (getParent().doMouseHover() && isMouseHoverRotaryBall(mouseX, mouseY)) {
			EliteRenderHelper.drawTexture(EliteMainwindow.ELITE, x, y, 176, 176, 80, 80);
		}
		map.forEach((a, b) -> b.run());
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
