package com.pinball3d.zone.sphinx.component;

import java.util.Iterator;

import com.pinball3d.zone.gui.Component;
import com.pinball3d.zone.sphinx.IHasComponents;
import com.pinball3d.zone.sphinx.log.FormattedLog;
import com.pinball3d.zone.sphinx.log.LogComponent;
import com.pinball3d.zone.sphinx.log.LogComponent.Type;
import com.pinball3d.zone.util.Util;

import net.minecraft.client.gui.FontRenderer;

public class ScrollingViewLog extends Component {
	protected int length, scrollingDistance;
	protected FormattedLog log;

	public ScrollingViewLog(IHasComponents parent, FormattedLog log, int x, int y, int width, int height) {
		super(parent, x, y, width, height);
		this.parent = parent;
		this.log = log;
	}

	@Override
	public void doRender(int mouseX, int mouseY) {
		super.doRender(mouseX, mouseY);
		Iterator<LogComponent> it = log.getComponents().iterator();
		int xOffset = 0;
		int yOffset = 0;
		FontRenderer fr = Util.getFontRenderer();
		while (it.hasNext()) {
			LogComponent c = it.next();
			String text = c.toString();
			int w = c.getWidth();
			if (w > width - xOffset) {
				do {
					String s = Util.cutStringToWidth(text, width - xOffset);
					fr.drawString(s, x + xOffset, y + yOffset - scrollingDistance,
							c.getType() == Type.STRING ? 0xFFE0E0E0 : 0xFF3AFAFD);
					xOffset = 0;
					yOffset += 10;
					text = text.substring(s.length());
				} while (fr.getStringWidth(text) > width - xOffset);
				if (!text.isEmpty()) {
					fr.drawString(text, x + xOffset, y + yOffset - scrollingDistance,
							c.getType() == Type.STRING ? 0xFFE0E0E0 : 0xFF3AFAFD);
					xOffset += fr.getStringWidth(text);
				}
			} else {
				fr.drawString(text, x + xOffset, y + yOffset - scrollingDistance,
						c.getType() == Type.STRING ? 0xFFE0E0E0 : 0xFF3AFAFD);
				xOffset += w;
			}
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
//		Iterator<LogBar> it = list.iterator();
//		int yOffset = 0;
//		while (it.hasNext()) {
//			LogBar bar = it.next();
//			if (y + scrollingDistance >= yOffset && y + scrollingDistance < yOffset + bar.height) {
//				onClickBar(bar.log);
//				return true;
//			}
//			yOffset += bar.height;
//		}
		return false;
	}
}
