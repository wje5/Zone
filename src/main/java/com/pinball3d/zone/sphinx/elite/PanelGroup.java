package com.pinball3d.zone.sphinx.elite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.pinball3d.zone.sphinx.elite.panels.Panel;

import net.minecraft.client.renderer.GlStateManager;

public class PanelGroup {
	private EliteMainwindow parent;
	private int dragX, dragY, chosenIndex, hoverIndex = -1, dragIndex = -1, dragToIndex = -1, hoverPanelIndex = -1,
			remain;
	private float x, y, width, height;
	private boolean pressing, dragging, hoverQuit, hoverRemain;
	private List<Panel> panels = new ArrayList<Panel>();
	private int[] panelsWidth;
	public static int MIN_PANEL_WIDTH = 100, MIN_PANEL_HEIGHT = 100;

	public PanelGroup(EliteMainwindow parent, float x, float y, float width, float height) {
		this.parent = parent;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public void doRenderPre(int mouseX, int mouseY, float partialTicks) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(getX() + 1, getY() + 30, 0.0F);
		panels.get(chosenIndex).doRenderPre(mouseX - getX() - 1, mouseY - getY() - 31, partialTicks);
		GlStateManager.popMatrix();
	}

	public void doRender(int mouseX, int mouseY, float partialTicks) {
		calcPanelsWidth();
		int xOffset = 0;
		EliteRenderHelper.drawRect(getX(), getY(), getWidth(), 30, Color.COMP_BG_DARK);
		for (int i = 0; i < panels.size(); i++) {
			Panel e = panels.get(i);
			int w = panelsWidth[i] - 1;
			if (panels.size() - remain <= i) {
				continue;
			}
			Color color = Color.COMP_BG_DARK;
			if (chosenIndex == i) {
				color = Color.COMP_BG_LIGHT;
			} else if (pressing || dragging) {
				if (dragIndex == i) {
					color = Color.DIVIDER_BG;
				}
			} else if (hoverIndex == i) {
				color = Color.COMP_BG_CHOSEN;
			}
			EliteRenderHelper.drawRect(getX() + xOffset + 1, getY() + 1, w, 28, color);
			EliteRenderHelper.drawBorder(getX() + xOffset, getY(), w + 2, 30, 1, Color.DIVIDER_BG);
			FontHandler.renderText(getX() + xOffset + 11, getY() + 7, e.getName(),
					chosenIndex == i ? Color.TEXT_LIGHT : Color.TEXT_DARK, w - 36);
			if (hoverIndex == i && hoverQuit) {
				EliteRenderHelper.drawTexture(EliteMainwindow.ELITE, getX() + xOffset + w - 22, getY() + 9, 8, 40, 14,
						11);
			} else {
				EliteRenderHelper.drawTexture(EliteMainwindow.ELITE, getX() + xOffset + w - 18, getY() + 12, 12, 43, 6,
						6);
			}
			xOffset += w + 1;
		}
		if (remain > 0) {
			int w = FontHandler.renderText(getX() + xOffset + 32, getY() + 6, new FormattedString("" + remain),
					Color.TEXT_LIGHT, xOffset);
			if (hoverRemain) {
				EliteRenderHelper.drawTexture(EliteMainwindow.ELITE, getX() + xOffset + 11, getY() + 6, 32, 40, 22 + w,
						17);
				EliteRenderHelper.drawTexture(EliteMainwindow.ELITE, getX() + xOffset + 33 + w, getY() + 6, 255, 40, 1,
						17);
			} else {
				EliteRenderHelper.drawTexture(EliteMainwindow.ELITE, getX() + xOffset + 19, getY() + 11, 40, 45, 11, 7);
			}
		}
		GlStateManager.pushMatrix();
		GlStateManager.translate(getX() + 1, getY() + 30, 0.0F);
		panels.get(chosenIndex).doRender(mouseX - getX() - 1, mouseY - getY() - 31, partialTicks);
		GlStateManager.popMatrix();
		EliteRenderHelper.drawBorder(getX(), getY(), getWidth(), getHeight(), 1, Color.DIVIDER_BG);
		EliteRenderHelper.drawRect(getX(), getY() + 29, getWidth(), 1, Color.DIVIDER_BG);
	}

	public void doRenderPost(int mouseX, int mouseY, float partialTicks) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(getX() + 1, getY() + 30, 0.0F);
		panels.get(chosenIndex).doRenderPost(mouseX - getX() - 1, mouseY - getY() - 31, partialTicks);
		GlStateManager.popMatrix();
		if (dragging) {
			if (dragToIndex >= 0) {
				int xOffset = 0;
				PanelGroup p = parent.getPanels().get(hoverPanelIndex);
				for (int i = 0; i < p.panels.size(); i++) {
					Panel e = p.panels.get(i);
					if (i == dragToIndex) {
						EliteRenderHelper.drawRect(p.getX() + xOffset, p.getY(), 2, 30, Color.FF6F6F6F);
					}
					xOffset += FontHandler.getStringWidth(e.getName()) + 37;
				}
				if (dragToIndex == p.panels.size()) {
					EliteRenderHelper.drawRect(p.getX() + xOffset, p.getY(), 2, 30, Color.FF6F6F6F);
				}
			} else {
				List<PanelGroup> list = parent.getPanels();
				for (PanelGroup g : list) {
					if (g == this && g.panels.size() == 1) {
						continue;
					}
					float gX = g.getXF(), gY = g.getYF(), gW = g.getWidthF(), gH = g.getHeightF();
					if (dragX >= gX && dragX <= gX + gW && dragY >= gY && dragY <= gY + gH) {
						boolean isRow = dragX >= gX + gW / 3.0F && dragX <= gX + gW / 1.5F;
						if (isRow) {
							if (gH / 2 >= MIN_PANEL_HEIGHT) {
								EliteRenderHelper.drawBorder((int) gX, (int) gY, (int) gW, (int) (gH / 2 - 1), 2,
										Color.TEXT_DARK);
								EliteRenderHelper.drawBorder((int) gX, (int) (gY + gH / 2 + 1), (int) gW,
										(int) (gH / 2 - 1), 2, Color.TEXT_DARK);
							}
						} else if (gW / 2 >= MIN_PANEL_WIDTH) {
							EliteRenderHelper.drawBorder((int) gX, (int) gY, (int) (gW / 2 - 1), (int) gH, 2,
									Color.TEXT_DARK);
							EliteRenderHelper.drawBorder((int) (gX + gW / 2 + 1), (int) gY, (int) (gW / 2 - 1),
									(int) gH, 2, Color.TEXT_DARK);
						}
					}
				}
			}
		}
	}

	public void calcPanelsWidth() {
		panelsWidth = panels.stream().mapToInt(e -> FontHandler.getStringWidth(e.getName()) + 37).toArray();
		int maxWidth = getWidth();
		int totalWidth = Arrays.stream(panelsWidth).sum();
		while (totalWidth > maxWidth) {
			List<Integer> l = new ArrayList<Integer>();
			int max = MIN_PANEL_WIDTH;
			int second = MIN_PANEL_WIDTH;
			for (int i = 0; i < panelsWidth.length; i++) {
				if (panelsWidth[i] > max) {
					second = max;
					max = panelsWidth[i];
					l.clear();
					l.add(i);
				} else if (panelsWidth[i] == max) {
					if (panelsWidth[i] > MIN_PANEL_WIDTH) {
						l.add(i);
					}
				} else if (panelsWidth[i] > second) {
					second = panelsWidth[i];
				}
			}
			if (!l.isEmpty()) {
				int remain = totalWidth - maxWidth;
				int a = (int) Math.ceil(remain * 1.0F / l.size());
				for (int i : l) {
					int k = Math.min(Math.min(a, remain), max - second);
					remain -= k;
					totalWidth -= k;
					panelsWidth[i] -= k;
				}
			} else {
				break;
			}
		}
	}

	public void expandToRect(Rect rect) {
		if (parent.getPanels().isEmpty()) {
			return;
		}
		List<PanelGroup> l = new ArrayList<PanelGroup>();
		Side side = null;
		for (Side s : Side.values()) {
			Edge edge = rect.getEdge(s);
			int total = 0;
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
			float gX = g.getXF(), gY = g.getYF(), gW = g.getWidthF(), gH = g.getHeightF();
			if (dragX >= gX && dragX <= gX + gW && dragY >= gY && dragY <= gY + gH) {
				boolean isRow = dragX >= gX + gW / 3.0F && dragX <= gX + gW / 1.5F;
				if (isRow) {
					if (gH / 2 < MIN_PANEL_HEIGHT) {
						return;
					}
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
					if (gW / 2 < MIN_PANEL_WIDTH) {
						return;
					}
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
		if (mouseButton == 0) {
			if (hoverIndex >= 0 && parent.getPanels().get(hoverPanelIndex) == this) {
				dragX = mouseX;
				dragY = mouseY;
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
						} else if (hoverIndex >= 0) {
							if (hoverQuit) {
								if (panels.get(hoverIndex).canQuit()) {
									panels.get(hoverIndex).close();
									panels.remove(hoverIndex);
									if (chosenIndex > panels.size() - 1) {
										if (panels.isEmpty()) {
											parent.getPanels().remove(this);
											expandToRect(getRect());
										} else {
											chosenIndex--;
										}
									}
								}
							} else {
								chosenIndex = hoverIndex;
							}
						}
					}
					pressing = false;
					dragging = false;
					dragIndex = -1;
					dragToIndex = -1;
					calcPanelGroup(dragX, dragY);
					dragX = 0;
					dragY = 0;

				});
			} else if (hoverRemain) {
				dragX = mouseX;
				dragY = mouseY;
				return new Drag((x, y, mX, mY) -> {
					dragX = x;
					dragY = y;
				}, cancel -> {
					if (!cancel && hoverRemain) {
						PanelGroupList l = new PanelGroupList(parent, this,
								Arrays.stream(Arrays.copyOf(panelsWidth, panels.size() - remain)).sum() + getX() + 11,
								51);
						if (l.getX() + l.getWidth() > parent.getWidth()) {
							int lX = parent.getWidth() - l.getWidth();
							l.setX(lX < 0 ? 0 : lX);
						}
						if (l.getY() + l.getHeight() > parent.getHeight()) {
							int lY = parent.getHeight() - l.getHeight();
							l.setY(lY < 0 ? 0 : lY);
						}
						parent.setDropDownList(l);
					}
					dragX = 0;
					dragY = 0;
				});
			}
		}
		return panels.get(chosenIndex).mouseClicked(mouseX - getX() - 1, mouseY - getY() - 31, mouseButton);
	}

	public void onMouseReleased(int mouseX, int mouseY, int mouseButton) {

	}

	public void calcPanelGroup(int mouseX, int mouseY) {
		hoverIndex = -1;
		dragToIndex = -1;
		hoverPanelIndex = -1;
		hoverQuit = false;
		hoverRemain = false;
		List<PanelGroup> l = parent.getPanels();
		for (int index = 0; index < l.size(); index++) {
			int xOffset = 0;
			boolean flag = false, flag2 = false;
			PanelGroup p = l.get(index);
			p.calcPanelsWidth();
			p.remain = 0;
			if (!(mouseX >= p.getX() && mouseX <= p.getX() + p.getWidth() && mouseY >= p.getY()
					&& mouseY <= p.getY() + 28)) {
				flag2 = true;
			}
			if (mouseX >= p.getX() && mouseX <= p.getX() + p.getWidth() && mouseY >= p.getY()
					&& mouseY <= p.getY() + p.getHeight()) {
				hoverPanelIndex = index;
			}
			List<Panel> pl = p.getPanels();
			for (int i = 0; i < pl.size(); i++) {
				int w = p.panelsWidth[i] - 1;
				if (i == pl.size() - 1 && !flag) {
					if (xOffset + w > p.getWidth()) {
						flag = true;
						p.remain++;
						continue;
					}
				} else if (xOffset + w > p.getWidth() - 61) {
					flag = true;
					p.remain++;
					continue;
				}
				if (!flag2 && mouseX >= p.x + xOffset && mouseX <= p.x + xOffset + w) {
					hoverIndex = i;
					if (dragIndex >= 0) {
						dragToIndex = hoverIndex;
						if (dragToIndex < pl.size() && mouseX >= p.x + xOffset + w / 2) {
							dragToIndex++;
						}
					}
					if (mouseX >= p.x + xOffset + w - 22 && mouseX <= p.x + xOffset + w - 8 && mouseY >= p.y + 9
							&& mouseY <= p.y + 20) {
						hoverQuit = true;
					}
					flag2 = true;
				}
				xOffset += w + 1;
			}
			if (!flag2 && dragIndex >= 0 && dragToIndex < 0) {
				dragToIndex = pl.size() - p.remain;
			}
			int w = FontHandler.getStringWidth(new FormattedString("" + p.remain));
			if (mouseX >= p.getX() + xOffset + 11 && mouseX <= p.getX() + xOffset + 34 + w && mouseY >= p.getY() + 6
					&& mouseY <= p.getY() + 23) {
				p.hoverRemain = true;
			}
		}
	}

	public void onMouseMoved(int mouseX, int mouseY, int moveX, int moveY) {
		calcPanelGroup(mouseX, mouseY);
	}

	public PanelGroup addPanel(Panel panel) {
		panel.setParentGroup(this);
		panels.add(panel);
		return this;
	}

	public int getX() {
		return (int) x;
	}

	public int getY() {
		return (int) y;
	}

	public int getWidth() {
		return (int) width;
	}

	public int getHeight() {
		return (int) height;
	}

	public int getPanelX() {
		return (int) x + 1;
	}

	public int getPanelY() {
		return (int) y + 30;
	}

	public int getPanelWidth() {
		return (int) width - 2;
	}

	public int getPanelHeight() {
		return (int) height - 31;
	}

	public float getXF() {
		return x;
	}

	public float getYF() {
		return y;
	}

	public float getWidthF() {
		return width;
	}

	public float getHeightF() {
		return height;
	}

	public void setXF(float xF) {
		x = xF;
	}

	public void setYF(float yF) {
		y = yF;
	}

	public void setWidthF(float widthF) {
		width = widthF;
	}

	public void setHeightF(float heightF) {
		height = heightF;
	}

	public List<Panel> getPanels() {
		return panels;
	}

	public int getChosenIndex() {
		return chosenIndex;
	}

	public void setChosenIndex(int index) {
		chosenIndex = index;
	}

	public int getRemain() {
		return remain;
	}

	public Rect getRect() {
		return new Rect(x, y, width, height);
	}

	@Override
	public String toString() {
		String s = "{";
		for (int i = 0; i < panels.size(); i++) {
			s += panels.get(i);
			if (i < panels.size() - 1) {
				s += " ,";
			} else {
				s += "}";
			}
		}
		return s;
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
				return new Edge(x, y, x + width, y);
			case DOWN:
				return new Edge(x, y + height, x + width, y + height);
			case LEFT:
				return new Edge(x, y, x, y + height);
			case RIGHT:
				return new Edge(x + width, y, x + width, y + height);
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
		private float x1, y1, x2, y2;

		public Edge(float x1, float y1, float x2, float y2) {
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

		public boolean isRow() {
			return y1 == y2;
		}

		public float getMin() {
			return isRow() ? x1 : y1;
		}

		public float getMax() {
			return isRow() ? x2 : y2;
		}

		public float getLength() {
			return getMax() - getMin();
		}

		public Edge connect(Edge edge) {
			if (isRow() == edge.isRow()) {
				if (isRow()) {
					if (y1 == edge.y1
							&& Math.abs((x1 + x2) / 2 - (edge.x1 + edge.x2) / 2) <= (x2 - x1 + edge.x2 - edge.x1) / 2) {
						return new Edge(Math.min(x1, edge.x1), y1, Math.max(x2, edge.x2), y2);
					}
				} else {
					if (x1 == edge.x1
							&& Math.abs((y1 + y2) / 2 - (edge.y1 + edge.y2) / 2) <= (y2 - y1 + edge.y2 - edge.y1) / 2) {
						return new Edge(x1, Math.min(y1, edge.y1), x2, Math.max(y2, edge.y2));
					}
				}
			}
			return null;
		}

		public boolean isCross(Edge edge) {
			if (isRow() != edge.isRow()) {
				Edge r = null, c = null;
				if (isRow()) {
					r = this;
					c = edge;
				} else {
					r = edge;
					c = this;
				}
				return r.x1 <= c.x1 && r.x2 >= c.x1 && c.y1 <= r.y1 && c.y2 >= r.y1;
			}
			return false;
		}

		@Override
		public String toString() {
			return "{x1:" + x1 + " y1:" + y1 + " x2:" + x2 + " y2:" + y2 + "}";
		}

		@Override
		public boolean equals(Object o) {
			return o instanceof Edge && ((Edge) o).x1 == x1 && ((Edge) o).y1 == y1 && ((Edge) o).x2 == x2
					&& ((Edge) o).y2 == y2;
		}

		@Override
		public int hashCode() {
			return (((Float.floatToIntBits(x1) * 31 + Float.floatToIntBits(y1)) * 31 + Float.floatToIntBits(x2)) * 31
					+ Float.floatToIntBits(y2));
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

		public boolean isRow() {
			return this == UP || this == DOWN;
		}
	}
}
