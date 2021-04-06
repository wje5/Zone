package com.pinball3d.zone.sphinx.component;

import java.util.Iterator;

import org.lwjgl.opengl.GL11;

import com.pinball3d.zone.gui.IHasComponents;
import com.pinball3d.zone.gui.component.Component;
import com.pinball3d.zone.sphinx.log.FormattedLog;
import com.pinball3d.zone.sphinx.log.LogComponent;
import com.pinball3d.zone.util.Util;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

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
		GlStateManager.pushMatrix();
		GlStateManager.enableDepth();
		GlStateManager.translate(0, 0, -400F);
		GlStateManager.depthFunc(GL11.GL_GEQUAL);
		Gui.drawRect(0, 0, width, height, 0x651CC3B5);
		GlStateManager.depthFunc(GL11.GL_LEQUAL);
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
					if (yOffset + fr.FONT_HEIGHT >= scrollingDistance && yOffset - scrollingDistance <= height) {
						fr.drawString(s, xOffset, yOffset - scrollingDistance, c.getColor());
					}
					xOffset = 0;
					yOffset += 10;
					text = text.substring(s.length());
				} while (fr.getStringWidth(text) > width - xOffset);
				if (!text.isEmpty()) {
					if (yOffset + fr.FONT_HEIGHT >= scrollingDistance && yOffset - scrollingDistance <= height) {
						fr.drawString(text, xOffset, yOffset - scrollingDistance, c.getColor());
					}
					xOffset += fr.getStringWidth(text);
				}
			} else {
				if (yOffset + fr.FONT_HEIGHT >= scrollingDistance && yOffset - scrollingDistance <= height) {
					fr.drawString(text, xOffset, yOffset - scrollingDistance, c.getColor());
				}
				xOffset += w;
			}
		}
		GlStateManager.popMatrix();
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
