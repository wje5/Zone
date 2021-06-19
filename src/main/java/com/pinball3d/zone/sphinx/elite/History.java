package com.pinball3d.zone.sphinx.elite;

import java.util.Stack;

public class History {
	private Stack<HistoryEvent> stack = new Stack<HistoryEvent>();
	private int position = -1;

	public void push(HistoryEvent event) {
		while (position < stack.size()) {
			stack.pop();
		}
		stack.push(event);
		position++;
	}

	public HistoryEvent undo() {
		if (position == -1) {
			return null;
		}
		return stack.get(position--);
	}

	public HistoryEvent redo() {
		if (position == stack.size() - 1) {
			return null;
		}
		return stack.get(++position);
	}

	public static class HistoryEvent {

	}

	public static class EventTyping {
		public final int min, max;
		public final String old, text;

		public EventTyping(int min, int max, String old, String text) {
			this.min = min;
			this.max = max;
			this.old = old;
			this.text = text;
		}
	}
}
