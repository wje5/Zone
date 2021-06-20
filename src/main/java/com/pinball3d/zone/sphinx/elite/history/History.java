package com.pinball3d.zone.sphinx.elite.history;

import java.util.Stack;

public class History {
	private Stack<HistoryEvent> stack = new Stack<HistoryEvent>();
	private int position = -1;

	public void push(HistoryEvent event) {
		while (position < stack.size() - 1) {
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
}
