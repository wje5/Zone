package com.pinball3d.zone.sphinx.log;

import com.pinball3d.zone.sphinx.SerialNumber;
import com.pinball3d.zone.util.Util;

import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class LogComponent {
	private Type type;

	protected LogComponent(Type type) {
		this.type = type;
	}

	public Type getType() {
		return type;
	}

	@SideOnly(Side.CLIENT)
	public void doRender(int x, int y, int width) {
		Util.getFontRenderer().drawString(Util.cutStringToWidth(toString(), width), x, y,
				type == Type.STRING ? 0xFFE0E0E0 : 0xFF3AFAFD);
	}

	@SideOnly(Side.CLIENT)
	public int getWidth() {
		FontRenderer fr = Util.getFontRenderer();
		return fr.getStringWidth(toString());
	}

	public static LogComponent of(Object o) {
		if (o instanceof LogComponent) {
			return (LogComponent) o;
		}
		if (o instanceof SerialNumber) {
			return new LogComponentNeedNetwork((SerialNumber) o);
		}
		return new LogComponentString(o.toString());
	}

	public static enum Type {
		STRING, NEEDNETWORK, PACK;
	}
}
