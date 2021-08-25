package com.pinball3d.zone.sphinx.elite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FormattedString {
	private StringComponent[] components;
	public static final FormattedString EMPTY = new FormattedString("", false);

	public FormattedString(String text) {
		this(text, true);
	}

	public FormattedString(String text, boolean isEscape) {
		boolean underLine = false, italic = false, bold = false, alt = false;
		Color color = null;
		char[] a = text.toCharArray();
		List<StringComponent> l = new ArrayList<StringComponent>();
		String s = "";
		for (int i = 0; i < a.length; i++) {
			char c = a[i];
			if (c == '§') {
				if (a.length > i + 1) {
					char d = a[i + 1];
					if (d == 'n') {
						if (!s.isEmpty() && !underLine) {
							l.add(new StringComponent(s, color, bold, italic, underLine ? 1 : 0));
							s = "";
						}
						underLine = true;
						i++;
						continue;
					} else if (d == 'o') {
						if (!s.isEmpty() && !italic) {
							l.add(new StringComponent(s, color, bold, italic, underLine ? 1 : 0));
							s = "";
						}
						italic = true;
						i++;
						continue;
					} else if (d == 'l') {
						if (!s.isEmpty() && !bold) {
							l.add(new StringComponent(s, color, bold, italic, underLine ? 1 : 0));
							s = "";
						}
						bold = true;
						i++;
						continue;
					} else if (d == 'a') {
						if (!s.isEmpty() && !alt) {
							l.add(new StringComponent(s, color, bold, italic, underLine ? 1 : 0));
							s = "";
						}
						alt = true;
						i++;
						continue;
					} else if (d == 'r') {
						if (!s.isEmpty() && (italic || bold || underLine)) {
							l.add(new StringComponent(s, color, bold, italic, underLine ? 1 : 0));
							s = "";
						}
						italic = false;
						bold = false;
						underLine = false;
						alt = false;
						i++;
						continue;
					}
				}
			}
			s += c;
			if (alt) {
				l.add(new StringComponent(s, color, bold, italic, 2));
				alt = false;
				s = "";
			}
		}
		if (!s.isEmpty()) {
			l.add(new StringComponent(s, color, bold, italic, alt ? 2 : underLine ? 1 : 0));
		}
		components = l.toArray(new StringComponent[] {});
	}

	public FormattedString(StringComponent... components) {
		this.components = components;
	}

	@Override
	public String toString() {
		return Arrays.stream(components).map(e -> e.text).collect(Collectors.joining());
	}

	public int getComponentsSize() {
		return components.length;
	}

	public StringComponent get(int index) {
		return components[index];
	}

	public static class StringComponent {
		public final String text;
		public final Color color;
		public final boolean bold;
		public final boolean italic;
		public final int underline;

		public StringComponent(String text, Color color, boolean bold, boolean italic, int underline) {
			this.text = text;
			this.color = color;
			this.bold = bold;
			this.italic = italic;
			this.underline = underline;
		}
	}
}
