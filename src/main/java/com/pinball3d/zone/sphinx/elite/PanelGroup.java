package com.pinball3d.zone.sphinx.elite;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.GlStateManager;

public class PanelGroup {
	private EliteMainwindow parent;
	private int dragX, dragY, chosenIndex, hoverIndex, dragIndex;
	private float x, y, width, height;
	private boolean pressing, dragging;
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
		float xOffset = 0;
		for (int i = 0; i < panels.size(); i++) {
			Panel e = panels.get(i);
			String name = e.getName();
			float w = FontHelper.getStringWidth(name) + 9;
			int color = 0xFF424242;
			if (chosenIndex == i) {
				color = 0xFF535353;
			} else if (pressing || dragging) {
				if (dragIndex == i) {
					color = 0xFF383838;
				}
			} else if (hoverIndex == i) {
				color = 0xFF4F4F4F;
			}
			EliteRenderHelper.drawRect(x + xOffset + 0.25F, y + 0.25F, w, 7, color);
			EliteRenderHelper.drawBorder(x + xOffset, y, w + 0.5F, 7.5F, 0.25F, 0xFF383838);
			FontHelper.renderText(x + xOffset + 2.75F, y + 1.75F, name, chosenIndex == i ? 0xFFF0F0F0 : 0xFFA0A0A0);
			xOffset += w + 0.25F;
			GlStateManager.pushMatrix();
			GlStateManager.translate(e.getX(), e.getY(), 0.0F);
			e.doRender(mouseX, mouseY);
			GlStateManager.popMatrix();
		}
		EliteRenderHelper.drawBorder(x, y, width, height, 0.25F, 0xFF383838);
		EliteRenderHelper.drawRect(x, y + 7.25F, width, 0.25F, 0xFF383838);
	}

	public void doRenderPost(int mouseX, int mouseY) {
		panels.forEach(e -> {
			GlStateManager.pushMatrix();
			GlStateManager.translate(e.getX(), e.getY(), 0.0F);
			e.doRenderPost(mouseX, mouseY);
			GlStateManager.popMatrix();
		});
		if (dragging) {
			if (hoverIndex >= 0) {
				return;
			}
			List<PanelGroup> list = parent.getPanels();
			for (PanelGroup g : list) {
				float gX = g.getX(), gY = g.getY(), gW = g.getWidth(), gH = g.getHeight();
				if (dragX >= gX && dragX <= gX + gW && dragY >= gY && dragY <= gY + gH) {
					boolean isRow = dragX >= gX + gW / 3.0F && dragX <= gX + gW / 1.5F;
					if (isRow) {
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
		if (mouseButton != 0) {
			return null;
		}
		if (hoverIndex >= 0) {
			pressing = true;
			dragIndex = hoverIndex;
			return new Drag((x, y, mX, mY) -> {
				dragX = x;
				dragY = y;
				if (Math.abs(mouseX - dragX) + Math.abs(mouseY - dragY) > 3) {
					dragging = true;
				}
				System.out.println(x + "|" + y + "|" + mX + "|" + mY);
			}, () -> {
				if (dragging) {
					List<PanelGroup> list = parent.getPanels();
					if (hoverIndex >= 0 && hoverIndex != dragIndex) {
						Panel p = panels.get(dragIndex);
						panels.remove(dragIndex);
						panels.add(hoverIndex, p);
					} else {
						for (PanelGroup g : list) {
							float gX = g.getX(), gY = g.getY(), gW = g.getWidth(), gH = g.getHeight();
							if (dragX >= gX && dragX <= gX + gW && dragY >= gY && dragY <= gY + gH) {
								boolean isRow = dragX >= gX + gW / 3.0F && dragX <= gX + gW / 1.5F;
								System.out.println(isRow);
							}
						}
					}
					dragX = 0;
					dragY = 0;
				} else if (hoverIndex >= 0) {
					chosenIndex = hoverIndex;
				}
				System.out.println("stop");
				pressing = false;
				dragging = false;
				dragIndex = -1;
			});
		}
		return null;
	}

	public void onMouseReleased(int mouseX, int mouseY, int mouseButton) {
		System.out.println("release" + mouseX + "|" + mouseY + "|" + mouseButton);
	}

	public void onMouseMoved(int mouseX, int mouseY, int moveX, int moveY) {
		hoverIndex = -1;
		if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + 7) {
			float xOffset = 0;
			for (int i = 0; i < panels.size(); i++) {
				Panel e = panels.get(i);
				String name = e.getName();
				float w = FontHelper.getStringWidth(name) + 9;
				if (mouseX >= x + xOffset && mouseX <= x + xOffset + w) {
					hoverIndex = i;
					break;
				}
				xOffset += w;
			}
		}
	}

	public void addPanel(Panel panel) {
		panel.setParentGroup(this);
		panel.setPos(x + 0.25F, y + 7.5F);
		panel.setSize(width - 0.5F, height - 7.75F);
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
