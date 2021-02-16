package com.pinball3d.zone.sphinx.component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

import com.pinball3d.zone.gui.Component;
import com.pinball3d.zone.gui.Subscreen;
import com.pinball3d.zone.sphinx.IHasComponents;
import com.pinball3d.zone.sphinx.log.FormattedLog;
import com.pinball3d.zone.sphinx.log.Log;
import com.pinball3d.zone.sphinx.subscreen.SubscreenViewLog;
import com.pinball3d.zone.util.Util;

import net.minecraft.client.gui.Gui;

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
		logs.forEach(e -> list.add(new LogBar(width, lineHeight, e)));
	}

	@Override
	public void doRender(int mouseX, int mouseY) {
		super.doRender(mouseX, mouseY);
		Iterator<LogBar> it = list.iterator();
		int yOffset = 0;
		while (it.hasNext()) {
			LogBar bar = it.next();
			int renderY = y + yOffset - scrollingDistance;
			if (renderY <= y + height && renderY + bar.height >= y) {
				int upCut = y - renderY > 0 ? y - renderY : 0;
				int downCut = renderY + bar.height - (y + height) > 0 ? renderY + bar.height - (y + height) : 0;
				bar.doRender(x, renderY, upCut, downCut);
			}
			yOffset += bar.height;
		}
	}

	@Override
	public boolean onDrag(int mouseX, int mouseY, int moveX, int moveY) {
		super.onDrag(mouseX, mouseY, moveX, moveY);
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
		Util.getRoot().removeScreen((Subscreen) parent);
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
			if (upCut < 10 && downCut < 10) {
				formattedLog.doRender(x + 5, y + 1, width - 5);
			}
		}
	}
}
