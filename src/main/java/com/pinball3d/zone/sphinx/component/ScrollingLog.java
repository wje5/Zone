package com.pinball3d.zone.sphinx.component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

import org.lwjgl.opengl.GL11;

import com.pinball3d.zone.gui.IHasComponents;
import com.pinball3d.zone.gui.component.Component;
import com.pinball3d.zone.sphinx.log.FormattedLog;
import com.pinball3d.zone.sphinx.log.Log;
import com.pinball3d.zone.sphinx.log.component.LogComponent;
import com.pinball3d.zone.sphinx.subscreen.SubscreenViewLog;
import com.pinball3d.zone.util.Util;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

public class ScrollingLog extends Component {
	protected int length, lineHeight, scrollingDistance;
	protected List<LogBar> list = new ArrayList<LogBar>();

	public ScrollingLog(IHasComponents parent, int x, int y, int width, int height) {
		super(parent, x, y, width, height);
		this.parent = parent;
		this.lineHeight = 10;
	}

	public void addLogBar(Log log) {
		list.add(new LogBar(width, lineHeight, log));
		length += lineHeight;
	}

	public void setLogs(Queue<Log> logs) {
		list.clear();
		length = 0;
		logs.forEach(e -> addLogBar(e));
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
		Iterator<LogBar> it = list.iterator();
		int yOffset = 0;
		while (it.hasNext()) {
			LogBar bar = it.next();
			int renderY = yOffset - scrollingDistance;
			if (renderY <= height && renderY + bar.height >= 0) {
				int renderUpCut = renderY < 0 ? -renderY : 0;
				int renderDownCut = renderY + bar.height - height > 0 ? renderY + bar.height - height : 0;
				bar.doRender(0, renderY, renderUpCut, renderDownCut);
			}
			yOffset += bar.height;
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
	public boolean onMouseScroll(int mouseX, int mouseY, boolean isUp) {
		if (super.onMouseScroll(mouseX, mouseY, isUp)) {
			return true;
		}
		if (enable != null && !enable.getAsBoolean()) {
			return false;
		}
		scrollingDistance += isUp ? 15 : -15;
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
		Iterator<LogBar> it = list.iterator();
		int yOffset = 0;
		while (it.hasNext()) {
			LogBar bar = it.next();
			if (y + scrollingDistance >= yOffset && y + scrollingDistance < yOffset + bar.height) {
				onClickBar(bar.log);
				return true;
			}
			yOffset += bar.height;
		}
		return false;
	}

	public void onClickBar(Log log) {
		Util.getRoot().putScreen(new SubscreenViewLog(Util.getRoot(), log.format()));
	}

	public class LogBar {
		protected Log log;
		protected FormattedLog formattedLog;
		protected int width;
		protected int height;

		public LogBar(int width, int height, Log log) {
			this.width = width;
			this.height = height;
			this.log = log;
			this.formattedLog = log.format();
		}

		public void doRender(int x, int y, int upCut, int downCut) {
			int a = y + upCut;
			int b = y + height - downCut;
			if (a < b) {
				int color = 0;
				switch (log.getLevel()) {
				case IMPORTANT:
					color = 0x99EF2020;
					break;
				case INFO:
					color = 0x99556FB5;
					break;
				case DEBUG:
					color = 0x99032323;
					break;
				case CHAT:
					color = 0x991ECCDE;
					break;
				}
				Gui.drawRect(x, a, x + width, b, color);
			}
			renderLog(x + 5, y + 1, width - 5, upCut, downCut, formattedLog);
		}

		public void renderLog(int x, int y, int width, int upCut, int downCut, FormattedLog log) {
			for (LogComponent c : log.getComponents()) {
				Util.getFontRenderer().drawString(Util.cutStringToWidth(c.toString(), width), x, y, c.getColor());
				int w = c.getWidth();
				x += w;
				width -= w;
				if (width <= 0) {
					return;
				}
			}
		}
	}
}
