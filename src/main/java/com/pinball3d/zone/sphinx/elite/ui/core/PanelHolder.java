package com.pinball3d.zone.sphinx.elite.ui.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.pinball3d.zone.math.Pos2i;
import com.pinball3d.zone.sphinx.elite.Color;
import com.pinball3d.zone.sphinx.elite.EliteMainwindow;
import com.pinball3d.zone.sphinx.elite.EliteRenderHelper;
import com.pinball3d.zone.sphinx.elite.ui.core.layout.BoxLayout.Type;

public class PanelHolder extends Subpanel {
	private List<Boolean> flags = new ArrayList<Boolean>();

	public PanelHolder(EliteMainwindow parent, Subpanel parentPanel) {
		super(parent, parentPanel, new DoubleColumnLayout());
	}

	@Override
	public void doRender(int mouseX, int mouseY, float partialTicks) {
		super.doRender(mouseX, mouseY, partialTicks);
		Iterator<Entry<Component, Pos2i>> it = components.entrySet().iterator();
		Iterator<Boolean> it2 = flags.iterator();
		boolean isLeft = true;
		int lineY = 0, lineY2 = 0;
		while (it.hasNext()) {
			Entry<Component, Pos2i> e = it.next();
			Component c = e.getKey();
			Pos2i pos = e.getValue();
			boolean flag = it2.next();
			if (!flag) {
				if (isLeft) {
					lineY = pos.y;
					lineY2 = pos.y + c.getHeight() + c.getMarginDown();
					isLeft = false;
				} else {
					lineY2 = Math.max(lineY2, pos.y + c.getHeight() + c.getMarginDown());
					EliteRenderHelper.drawLine(getRenderWidth() / 2, Math.min(lineY, pos.y), getRenderWidth() / 2,
							lineY2, Color.COMP_BG_DARK);
					EliteRenderHelper.drawLine(0, lineY2, getRenderWidth(), lineY2, Color.COMP_BG_DARK);
					isLeft = true;
				}
			} else {
				if (!isLeft) {
					EliteRenderHelper.drawLine(getRenderWidth() / 2, lineY, getRenderWidth() / 2, lineY2,
							Color.COMP_BG_DARK);
					EliteRenderHelper.drawLine(0, lineY2, getRenderWidth(), lineY2, Color.COMP_BG_DARK);
				}
				EliteRenderHelper.drawLine(0, pos.y + c.getHeight() + c.getMarginDown(), getRenderWidth(),
						pos.y + c.getHeight() + c.getMarginDown(), Color.COMP_BG_DARK);
				isLeft = true;
			}
		}
		if (!isLeft) {
			EliteRenderHelper.drawLine(getRenderWidth() / 2, lineY, getRenderWidth() / 2, lineY2, Color.COMP_BG_DARK);
			EliteRenderHelper.drawLine(0, lineY2, getRenderWidth(), lineY2, Color.COMP_BG_DARK);
		}
	}

	private static class DoubleColumnLayout implements ILayout {
		@Override
		public Map<Component, Pos2i> arrange(Subpanel subpanel, Map<Component, List<Object>> origin, int width,
				int height, boolean isPreArrange) {
			Map<Component, Pos2i> m = new LinkedHashMap<Component, Pos2i>();

			int offset = 0;
			int lineHeight = 0;
			List<Boolean> flags = ((PanelHolder) subpanel).flags;
			flags.clear();
			List<Entry<Component, List<Object>>> list = new ArrayList<>(origin.entrySet());
			int maxW = 0;
			for (int i = 0; i < list.size(); i++) {
				Entry<Component, List<Object>> e = list.get(i);
				Type type = Type.NW;
				boolean isSingle = true;
				for (Object o : e.getValue()) {
					if (o instanceof Type) {
						type = (Type) o;
					} else if (o instanceof Boolean) {
						isSingle = (Boolean) o;
					}
				}
				flags.add(isSingle);
				int mLeft = 6;
				Component c = e.getKey();
				if (isSingle) {
					type = type.getHorizontal();
					offset += c.getMarginTop();
					if (type == Type.WEST) {
						c.setRenderWidth(width - c.getMarginLeft() - mLeft >= c.getMinWidth()
								? Math.min(c.getWidth(), width - c.getMarginLeft() - mLeft)
								: 0);
						m.put(c, new Pos2i(c.getMarginLeft() + mLeft, offset));
					} else if (type == Type.CENTER) {
						c.setRenderWidth(width >= c.getMinWidth() ? Math.min(c.getWidth(), width) : 0);
						m.put(c, new Pos2i(isPreArrange ? 0 : (width - c.getRenderWidth()) / 2, offset));
					} else {
						c.setRenderWidth(width - c.getMarginRight() >= c.getMinWidth()
								? Math.min(c.getWidth(), width - c.getMarginRight())
								: 0);
						m.put(c, new Pos2i(isPreArrange ? 0 : width - c.getRenderWidth() - c.getMarginRight(), offset));
					}
					offset += c.getHeight() + c.getMarginDown() + 1;
					maxW = Math.max(maxW, mLeft + c.getMarginLeft() + c.getRenderWidth() + c.getMarginRight());
				} else {
					Entry<Component, List<Object>> e2 = ++i < list.size() ? list.get(i) : null;
					Type type2 = Type.NW;
					if (e2 != null) {
						for (Object o : e2.getValue()) {
							if (o instanceof Type) {
								type2 = (Type) o;
							} else if (o instanceof Boolean) {
								isSingle = (Boolean) o;
								flags.add(isSingle);
								if (isSingle) {
									i--;
									e2 = null;
									break;
								}
							}
						}
					}
					Type h = type.getHorizontal();
					Type v = type.getVertical();
					Type h2 = e2 == null ? null : type2.getHorizontal();
					Type v2 = e2 == null ? null : type2.getVertical();
					Component c2 = e2 == null ? null : e2.getKey();
					int w = 0;
					if (isPreArrange) {
						w += c.getMarginLeft() + mLeft;
						c.setRenderWidth(c.getWidth());
						w += c.getRenderWidth() + c.getMarginRight();
						int w2 = 0;
						if (e2 != null) {
							w2 += c2.getMarginLeft() + mLeft;
							c2.setRenderWidth(c2.getWidth());
							w2 += c2.getRenderWidth() + c2.getMarginRight();
						}
						m.put(c, new Pos2i(c.getMarginLeft() + mLeft, offset));
						if (e2 != null) {
							m.put(c2, new Pos2i(c2.getMarginLeft() + mLeft, offset));
						}
						w = Math.max(w, w2);
					} else {
						w = (width - 1) / 2;
					}
					maxW = Math.max(maxW, w * 2 + 1);
					int maxH = Math.max(c.getMarginTop() + c.getHeight() + c.getMarginDown(),
							e2 == null ? 0 : c2.getMarginTop() + c2.getHeight() + c2.getMarginDown());
					if (h == Type.WEST) {
						c.setRenderWidth(w - c.getMarginLeft() - mLeft >= c.getMinWidth()
								? Math.min(c.getWidth(), w - c.getMarginLeft() - mLeft)
								: 0);
						m.put(c, new Pos2i(c.getMarginLeft() + mLeft, offset));
					} else if (h == Type.CENTER) {
						c.setRenderWidth(w >= c.getMinWidth() ? Math.min(c.getWidth(), w) : 0);
						m.put(c, new Pos2i(isPreArrange ? 0 : (w - c.getRenderWidth()) / 2, offset));
					} else {
						c.setRenderWidth(w - c.getMarginRight() >= c.getMinWidth()
								? Math.min(c.getWidth(), w - c.getMarginRight())
								: 0);
						m.put(c, new Pos2i(isPreArrange ? 0 : width - c.getRenderWidth() - c.getMarginRight(), offset));
					}
					if (e2 != null) {
						if (h2 == Type.WEST) {
							c2.setRenderWidth(w - c2.getMarginLeft() - mLeft >= c2.getMinWidth()
									? Math.min(c2.getWidth(), w - c2.getMarginLeft() - mLeft)
									: 0);
							m.put(c2, new Pos2i(c2.getMarginLeft() + mLeft + w + 1, offset));
						} else if (h2 == Type.CENTER) {
							c2.setRenderWidth(w >= c2.getMinWidth() ? Math.min(c2.getWidth(), w) : 0);
							m.put(c2, new Pos2i((isPreArrange ? 0 : (w - c2.getRenderWidth()) / 2) + w + 1, offset));
						} else {
							c2.setRenderWidth(w - c2.getMarginRight() >= c2.getMinWidth()
									? Math.min(c2.getWidth(), w - c2.getMarginRight())
									: 0);
							m.put(c2, new Pos2i(
									(isPreArrange ? 0 : width - c.getRenderWidth() - c2.getMarginRight()) + w + 1,
									offset));
						}
					}
					offset += maxH + 1;
				}
			}
			if (isPreArrange) {
				subpanel.setWidth(maxW);
				subpanel.setLength(m.isEmpty() ? 0
						: m.entrySet().stream()
								.mapToInt(e -> e.getKey().getHeight() + e.getValue().y + e.getKey().getMarginDown())
								.max().getAsInt());
			}
			return m;
		}
	}
}
