package com.pinball3d.zone.sphinx.elite.history;

public class EventTyping extends HistoryEvent {
	public final int index;
	public final String old, text;

	public EventTyping(int index, String old, String text) {
		super(Type.TYPING);
		this.index = index;
		this.old = old;
		this.text = text;
	}
}