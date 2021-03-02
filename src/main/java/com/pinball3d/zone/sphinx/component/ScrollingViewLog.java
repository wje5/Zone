package com.pinball3d.zone.sphinx.component;

import java.util.Iterator;

import com.pinball3d.zone.gui.Component;
import com.pinball3d.zone.sphinx.IHasComponents;
import com.pinball3d.zone.sphinx.log.FormattedLog;
import com.pinball3d.zone.sphinx.log.LogComponent;
import com.pinball3d.zone.util.Util;

import net.minecraft.client.gui.FontRenderer;

public class ScrollingViewLog extends Component {
	protected int length, scrollingDistance;
	protected FormattedLog log;

	public ScrollingViewLog(IHasComponents parent, FormattedLog log, int x, int y, int width, int height) {
		super(parent, x, y, width, height);
		this.parent = parent;
		this.log = log;
		FontRenderer fr = Util.getFontRenderer();
		Iterator<LogComponent> it = log.getComponents().iterator();
		int xOffset = 0;
		int yOffset = 3;
		while (it.hasNext()) {
			LogComponent c = it.next();
			String text = c.toString();
			int w = c.getWidth();
			if (w > width - xOffset) {
				do {
					String s = Util.cutStringToWidth(text, width - xOffset);
					xOffset = 0;
					yOffset += 10;
					text = text.substring(s.length());
				} while (fr.getStringWidth(text) > width - xOffset);
				if (!text.isEmpty()) {
					xOffset += fr.getStringWidth(text);
				}
			} else {
				xOffset += w;
			}
		}
		length = yOffset + 10;
	}

	@Override
	public void doRender(int mouseX, int mouseY) {
		super.doRender(mouseX, mouseY);
		if (enable != null && !enable.getAsBoolean()) {
			return;
		}
		Iterator<LogComponent> it = log.getComponents().iterator();
		int xOffset = 0;
		int yOffset = 3;
		FontRenderer fr = Util.getFontRenderer();
		while (it.hasNext()) {
			LogComponent c = it.next();
			String text = c.toString();
			int w = c.getWidth();
			if (w > width - xOffset) {
				do {
					String s = Util.cutStringToWidth(text, width - xOffset);
					if (yOffset >= scrollingDistance && yOffset - scrollingDistance + fr.FONT_HEIGHT <= height) {
						fr.drawString(s, getX() + xOffset, getY() + yOffset - scrollingDistance, c.getColor());
					}
					xOffset = 0;
					yOffset += 10;
					text = text.substring(s.length());
				} while (fr.getStringWidth(text) > width - xOffset);
				if (!text.isEmpty()) {
					if (yOffset >= scrollingDistance && yOffset - scrollingDistance + fr.FONT_HEIGHT <= height) {
						fr.drawString(text, getX() + xOffset, getY() + yOffset - scrollingDistance, c.getColor());
					}
					xOffset += fr.getStringWidth(text);
				}
			} else {
				if (yOffset >= scrollingDistance && yOffset - scrollingDistance + fr.FONT_HEIGHT <= height) {
					fr.drawString(text, getX() + xOffset, getY() + yOffset - scrollingDistance, c.getColor());
				}
				xOffset += w;
			}
		}
	}

	@Override
	public boolean onDrag(int mouseX, int mouseY, int moveX, int moveY) {
		if (super.onDrag(mouseX, mouseY, moveX, moveY)) {
			return true;
		}
		if (enable != null && !enable.getAsBoolean()) {
			return false;
		}
		scrollingDistance -= moveY;
		scrollingDistance = scrollingDistance > length - height ? length - height : scrollingDistance;
		scrollingDistance = scrollingDistance < 0 ? 0 : scrollingDistance;
		return true;
	}

	@Override
	public boolean onLeftClick(int x, int y) {
		if (super.onLeftClick(x, y)) {
			return true;
		}
		if (enable != null && !enable.getAsBoolean()) {
			return false;
		}
		FontRenderer fr = Util.getFontRenderer();
		Iterator<LogComponent> it = log.getComponents().iterator();
		int xOffset = 0;
		int yOffset = 3;
		while (it.hasNext()) {
			LogComponent c = it.next();
			String text = c.toString();
			int w = c.getWidth();
			if (w > width - xOffset) {
				do {
					if (x >= xOffset && x <= width && y >= yOffset && y <= yOffset + 10) {
						onClick(c);
						return true;
					}
					String s = Util.cutStringToWidth(text, width - xOffset);
					xOffset = 0;
					yOffset += 10;
					text = text.substring(s.length());
				} while (fr.getStringWidth(text) > width - xOffset);
				if (!text.isEmpty()) {
					w = fr.getStringWidth(text);
					if (x >= xOffset && x <= xOffset + w && y >= yOffset && y <= yOffset + 10) {
						onClick(c);
						return true;
					}
					xOffset += w;
				}
			} else {
				if (x >= xOffset && x <= xOffset + w && y >= yOffset && y <= yOffset + 10) {
					onClick(c);
					return true;
				}
				xOffset += w;
			}
		}
		return false;
	}

	public void onClick(LogComponent c) {
		c.onClick();
	}
}
