package com.pinball3d.zone.sphinx.elite.history;

public class HistoryEvent {
	public final Type type;

	public HistoryEvent(Type type) {
		this.type = type;
	}

	public static enum Type {
		TYPING;
	}
}
