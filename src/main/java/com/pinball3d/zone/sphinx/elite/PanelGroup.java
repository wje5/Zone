package com.pinball3d.zone.sphinx.elite;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.GlStateManager;

public class PanelGroup {
	private EliteMainwindow parent;
	private int dragX, dragY, chosenIndex, hoverIndex, dragIndex, dragToIndex, hoverPanelIndex;
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
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y + 7, 0.0F);
		panels.get(chosenIndex).doRenderPre(mouseX, mouseY);
		GlStateManager.popMatrix();
	}

	public void doRender(int mouseX, int mouseY) {
		float xOffset = 0;
		for (int i = 0; i < panels.size(); i++) {
			Panel e = panels.get(i);
			String name = e.getName();
			float w = FontHandler.getStringWidth(name, FontHandler.NORMAL) + 9;
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
			FontHandler.renderText(x + xOffset + 2.75F, y + 1.75F, name, chosenIndex == i ? 0xFFF0F0F0 : 0xFFA0A0A0,
					FontHandler.NORMAL);
			xOffset += w + 0.25F;
		}
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y + 7, 0.0F);
		panels.get(chosenIndex).doRender(mouseX, mouseY);
		GlStateManager.popMatrix();
		EliteRenderHelper.drawBorder(x, y, width, height, 0.25F, 0xFF383838);
		EliteRenderHelper.drawRect(x, y + 7.25F, width, 0.25F, 0xFF383838);
	}

	public void doRenderPost(int mouseX, int mouseY) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y + 7, 0.0F);
		panels.get(chosenIndex).doRenderPost(mouseX, mouseY);
		GlStateManager.popMatrix();
		if (dragging) {
			if (dragToIndex >= 0) {
				float xOffset = 0;
				PanelGroup p = parent.getPanels().get(hoverPanelIndex);
				for (int i = 0; i < p.panels.size(); i++) {
					Panel e = p.panels.get(i);
					if (i == dragToIndex) {
						EliteRenderHelper.drawRect(p.x + xOffset, p.y, 0.5F, 7.5F, 0xFF6F6F6F);
					}
					xOffset += FontHandler.getStringWidth(e.getName(), FontHandler.NORMAL) + 9.25F;
				}
				if (dragToIndex == p.panels.size()) {
					EliteRenderHelper.drawRect(p.x + xOffset, p.y, 0.5F, 7.5F, 0xFF6F6F6F);
				}
			} else {
				List<PanelGroup> list = parent.getPanels();
				for (PanelGroup g : list) {
					if (g == this && g.panels.size() == 1) {
						continue;
					}
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
	}

	public void expandToRect(Rect rect) {
		List<PanelGroup> l = new ArrayList<PanelGroup>();
		Side side = null;
		for (Side s : Side.values()) {
			Edge edge = rect.getEdge(s);
			float total = 0;
			for (PanelGroup group : parent.getPanels()) {
				Rect r = group.getRect();
				if (rect.isAdjoin(r) == s) {
					Edge e = r.getEdge(s.opposite());
					if (e.getMin() >= edge.getMin() && e.getMax() <= edge.getMax()) {
						l.add(group);
						total += e.getLength();
					}
				}
			}
			if (total == edge.getLength()) {
				side = s;
				break;
			}
		}
		switch (side) {
		case UP:
			l.forEach(e -> e.height += rect.height);
			break;
		case DOWN:
			l.forEach(e -> {
				e.y -= rect.height;
				e.height += rect.height;
			});
			break;
		case LEFT:
			l.forEach(e -> e.width += rect.width);
			break;
		case RIGHT:
			l.forEach(e -> {
				e.x -= rect.width;
				e.width += rect.width;
			});
			break;
		}
	}

	public void adhere(int mouseX, int mouseY, int dragIndex) {
		for (PanelGroup g : parent.getPanels()) {
			float gX = g.getX(), gY = g.getY(), gW = g.getWidth(), gH = g.getHeight();
			if (dragX >= gX && dragX <= gX + gW && dragY >= gY && dragY <= gY + gH) {
				boolean isRow = dragX >= gX + gW / 3.0F && dragX <= gX + gW / 1.5F;
				if (isRow) {
					boolean isUp = dragY <= gY + gH / 2;
					if (isUp) {
						parent.getPanels()
								.add(new PanelGroup(parent, gX, gY, gW, gH / 2).addPanel(panels.get(dragIndex)));
						panels.remove(dragIndex);
						g.height = gH / 2;
						g.y = gY + gH / 2;
					} else {
						parent.getPanels().add(
								new PanelGroup(parent, gX, gY + gH / 2, gW, gH / 2).addPanel(panels.get(dragIndex)));
						panels.remove(dragIndex);
						g.height = gH / 2;
					}
				} else {
					boolean isLeft = dragX <= gX + gW / 2;
					if (isLeft) {
						parent.getPanels()
								.add(new PanelGroup(parent, gX, gY, gW / 2, gH).addPanel(panels.get(dragIndex)));
						panels.remove(dragIndex);
						g.width = gW / 2;
						g.x = gX + gW / 2;
					} else {
						parent.getPanels().add(
								new PanelGroup(parent, gX + gW / 2, gY, gW / 2, gH).addPanel(panels.get(dragIndex)));
						panels.remove(dragIndex);
						g.width = gW / 2;
					}
				}
				if (panels.isEmpty()) {
					parent.getPanels().remove(this);
					expandToRect(getRect());
				}
				break;
			}
		}
	}

	public Drag onMouseClicked(int mouseX, int mouseY, int mouseButton) {
		if (mouseButton != 0) {
			return null;
		}
		if (hoverIndex >= 0 && parent.getPanels().get(hoverPanelIndex) == this) {
			pressing = true;
			dragIndex = hoverIndex;
			return new Drag((x, y, mX, mY) -> {
				dragX = x;
				dragY = y;
				if (Math.abs(mouseX - dragX) + Math.abs(mouseY - dragY) > 3) {
					dragging = true;
				}
			}, cancel -> {
				if (!cancel) {
					if (dragging) {
						if (dragToIndex >= 0) {
							PanelGroup p = parent.getPanels().get(hoverPanelIndex);
							if (p == this) {
								if (dragToIndex > dragIndex) {
									dragToIndex--;
								}
								if (dragToIndex != dragIndex) {
									Panel e = panels.get(dragIndex);
									panels.remove(dragIndex);
									panels.add(dragToIndex, e);
								}
								chosenIndex = dragToIndex;
							} else {
								Panel e = panels.get(dragIndex);
								panels.remove(dragIndex);
								p.panels.add(dragToIndex, e);
								if (panels.isEmpty()) {
									parent.getPanels().remove(this);
									expandToRect(getRect());
								} else {
									chosenIndex = panels.size() - 1;
								}
							}
						} else {
							adhere(dragX, dragY, dragIndex);
						}
						dragX = 0;
						dragY = 0;
					} else if (hoverIndex >= 0) {
						chosenIndex = hoverIndex;
					}
				}
				pressing = false;
				dragging = false;
				dragIndex = -1;
				dragToIndex = -1;
			});
		}
		return null;
	}

	public void onMouseReleased(int mouseX, int mouseY, int mouseButton) {

	}

	public void onMouseMoved(int mouseX, int mouseY, int moveX, int moveY) {
		hoverIndex = -1;
		dragToIndex = -1;
		hoverPanelIndex = -1;
		List<PanelGroup> l = parent.getPanels();
		for (int index = 0; index < l.size(); index++) {
			PanelGroup p = l.get(index);
			if (mouseX >= p.getX() && mouseX <= p.getX() + p.getWidth() && mouseY >= p.getY()
					&& mouseY <= p.getY() + 7) {
				hoverPanelIndex = index;
				float xOffset = 0;
				List<Panel> pl = p.getPanels();
				for (int i = 0; i < pl.size(); i++) {
					Panel e = pl.get(i);
					String name = e.getName();
					float w = FontHandler.getStringWidth(name, FontHandler.NORMAL) + 9;
					if (mouseX >= p.x + xOffset && mouseX <= p.x + xOffset + w) {
						hoverIndex = i;
						if (dragIndex >= 0) {
							dragToIndex = hoverIndex;
							if (dragToIndex < pl.size() && mouseX >= p.x + xOffset + w / 2) {
								dragToIndex++;
							}
						}
						return;
					}
					xOffset += w;
				}
				dragToIndex = pl.size();
			}
		}
	}

	public PanelGroup addPanel(Panel panel) {
		panel.setParentGroup(this);
		panels.add(panel);
		return this;
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

	public List<Panel> getPanels() {
		return panels;
	}

	public Rect getRect() {
		return new Rect(x, y, width, height);
	}

	public static class Rect {
		private float x, y, width, height;

		public Rect(float x, float y, float width, float height) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}

		public Rect merge(Rect rect) {
			if (rect.x == x && rect.width == width && (rect.height + height) / 2 == Math.abs(rect.y - y)) {
				return new Rect(x, Math.min(rect.y, y), width,
						Math.max(rect.y + rect.height, y + height) - Math.min(rect.y, y));
			}
			if (rect.y == y && rect.height == height && (rect.width + width) / 2 == Math.abs(rect.x - x)) {
				return new Rect(Math.min(rect.x, x), y, Math.max(rect.x + rect.width, x + width) - Math.min(rect.x, x),
						height);
			}
			return null;
		}

		public Side isAdjoin(Rect rect) {
			if ((rect.height + height) / 2 == Math.abs((rect.y + rect.height / 2) - (y + height / 2))) {
				if (rect.y > y) {
					return Side.DOWN;
				}
				return Side.UP;
			}
			if ((rect.width + width) / 2 == Math.abs((rect.x + rect.width / 2) - (x + width / 2))) {
				if (rect.x > x) {
					return Side.RIGHT;
				}
				return Side.LEFT;
			}
			return null;
		}

		public Edge getEdge(Side side) {
			switch (side) {
			case UP:
				return new Edge(side, x, y, x + width, y);
			case DOWN:
				return new Edge(side, x, y + height, x + width, y + height);
			case LEFT:
				return new Edge(side, x, y, x, y + height);
			case RIGHT:
				return new Edge(side, x + width, y, x + width, y + height);
			}
			return null;
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

		@Override
		public String toString() {
			return "{" + x + "|" + y + "|" + width + "|" + height + "}";
		}
	}

	public static class Edge {
		private Side side;
		private float x1, y1, x2, y2;

		public Edge(Side side, float x1, float y1, float x2, float y2) {
			this.side = side;
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			this.y2 = y2;
		}

		public float getX1() {
			return x1;
		}

		public float getX2() {
			return x2;
		}

		public float getY1() {
			return y1;
		}

		public float getY2() {
			return y2;
		}

		public Side getSide() {
			return side;
		}

		public float getMin() {
			switch (side) {
			case UP:
			case DOWN:
				return x1;
			case LEFT:
			case RIGHT:
				return y1;
			}
			return 0;
		}

		public float getMax() {
			switch (side) {
			case UP:
			case DOWN:
				return x2;
			case LEFT:
			case RIGHT:
				return y2;
			}
			return 0;
		}

		public float getLength() {
			return getMax() - getMin();
		}
	}

	public static enum Side {
		UP, DOWN, LEFT, RIGHT;

		public Side opposite() {
			switch (this) {
			case UP:
				return DOWN;
			case DOWN:
				return UP;
			case LEFT:
				return RIGHT;
			case RIGHT:
				return LEFT;
			}
			return null;
		}
	}
}
