package com.pinball3d.zone.sphinx.elite;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.GlStateManager;

public class PanelGroup {
	private EliteMainwindow parent;
	private int dragX, dragY;
	private float x, y, width, height;
	private List<Panel> panels = new ArrayList<Panel>();

	public PanelGroup(EliteMainwindow parent, float x, float y, float width, float height) {
		this.parent = parent;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public void doRenderPre(int mouseX, int mouseY) {
		panels.forEach(e -> {
			GlStateManager.pushMatrix();
			GlStateManager.translate(e.getX(), e.getY(), 0.0F);
			e.doRenderPre(mouseX, mouseY);
			GlStateManager.popMatrix();
		});
	}

	public void doRender(int mouseX, int mouseY) {
		panels.forEach(e -> {
			GlStateManager.pushMatrix();
			GlStateManager.translate(e.getX(), e.getY(), 0.0F);
			e.doRender(mouseX, mouseY);
			GlStateManager.popMatrix();
		});
//		EliteRenderHelper.drawBorder(x, y, width, height, 0.25F, 0xFF383838);
		EliteRenderHelper.drawBorder(x, y, width, height, 0.25F, 0xFFFFFFFF);
	}

	public void doRenderPost(int mouseX, int mouseY) {
		panels.forEach(e -> {
			GlStateManager.pushMatrix();
			GlStateManager.translate(e.getX(), e.getY(), 0.0F);
			e.doRenderPost(mouseX, mouseY);
			GlStateManager.popMatrix();
		});
		if (dragX != 0 || dragY != 0) {
			List<PanelGroup> list = parent.getPanels();
			for (PanelGroup g : list) {
				if (g == this) {
					continue;
				}
				float gX = g.getX(), gY = g.getY(), gW = g.getWidth(), gH = g.getHeight();
				if (dragX >= gX && dragX <= gX + gW && dragY >= gY && dragY <= gY + gH) {
					boolean flag = dragX >= gX + gW / 3.0F && dragX <= gX + gW / 1.5F;
					if (flag) {
						EliteRenderHelper.drawBorder(gX, gY, gW, gH / 2 - 0.25F, 0.5F, 0xFFA0A0A0);
						EliteRenderHelper.drawBorder(gX, gY + gH / 2 + 0.25F, gW, gH / 2 - 0.25F, 0.5F, 0xFFA0A0A0);
					} else {
						EliteRenderHelper.drawBorder(gX, gY, gW / 2 - 0.25F, gH, 0.5F, 0xFFA0A0A0);
						EliteRenderHelper.drawBorder(gX + gW / 2 + 0.25F, gY, gW / 2 - 0.25F, gH, 0.5F, 0xFFA0A0A0);
					}
				}
			}
		}
	}

	public Drag onMouseClicked(int mouseX, int mouseY, int mouseButton) {
		System.out.println("click" + mouseX + "|" + mouseY + "|" + mouseButton);
		if (mouseButton == 0 && mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
			return new Drag((x, y, mX, mY) -> {
				dragX = x;
				dragY = y;
				System.out.println(x + "|" + y + "|" + mX + "|" + mY);
			}, () -> {
				if (dragX != 0 || dragY != 0) {
					x = dragX;
					y = dragY;
					dragX = 0;
					dragY = 0;
				}
				System.out.println("stop");
			});
		}
		return null;
	}

	public void onMouseReleased(int mouseX, int mouseY, int mouseButton) {
		System.out.println("release" + mouseX + "|" + mouseY + "|" + mouseButton);
	}

	public void addPanel(Panel panel) {
		panel.setParentGroup(this);
		panel.setPos(x + 0.25F, y + 0.25F);
		panel.setSize(width - 0.5F, height - 0.5F);
		panels.add(panel);
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}
}
