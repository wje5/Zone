package com.pinball3d.zone.sphinx.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.pinball3d.zone.sphinx.IHasComponents;
import com.pinball3d.zone.util.Util;

import net.minecraft.client.gui.FontRenderer;

public class MultilineText extends Component {
	protected List<String> texts;

	public MultilineText(IHasComponents parent, int x, int y, int width, String texts) {
		super(parent, x, y, 100, 100);
		this.x = x;
		this.y = y;
		this.width = width;
		this.texts = splixString(Util.getFontRenderer(), texts, width);
	}

	@Override
	public void doRender(int mouseX, int mouseY) {
		super.doRender(mouseX, mouseY);
		for (int i = 0; i < texts.size(); i++) {
			String text = texts.get(i);
			Util.getFontRenderer().drawString(text, x, y + i * 10, 0xFF1ECCDE);
		}
	}

	public static List<String> splixString(FontRenderer renderer, String input, int width) {
		List<String> list = Arrays.asList(input.split("\\|"));
		List<String> temp = new ArrayList<String>();
		list.forEach(e -> temp.addAll(splixLine(renderer, e, width)));
		return temp;
	}

	public static List<String> splixLine(FontRenderer renderer, String input, int width) {
		List<String> list = Arrays.asList(input.split(" "));
		List<String> temp = new ArrayList<String>();
		String s = "";
		Iterator<String> it = list.iterator();
		while (it.hasNext()) {
			String i = it.next();
			if (renderer.getStringWidth(s + i) <= width) {
				s = s + i + " ";
			} else {
				temp.add(s);
				s = i + " ";
			}
		}
		temp.add(s);
		return temp;
	}
}