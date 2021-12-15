package com.pinball3d.zone.sphinx.elite;

import java.util.function.Consumer;

public class Drag {
	public final int button;
	private OnDrag onDrag;
	private Consumer<Boolean> onStop;

	public static Drag emptyDrag(int button) {
		return new Drag(-1, (x, y, moveX, moveY) -> {
		}, cancel -> {
		});
	}

	public Drag(int button, OnDrag onDrag, Consumer<Boolean> onStop) {
		this.button = button;
		this.onDrag = onDrag;
		this.onStop = onStop;
	}

	public void setOnStop(Consumer<Boolean> onStop) {
		this.onStop = onStop;
	}

	public void drag(int x, int y, int moveX, int moveY) {
		onDrag.drag(x, y, moveX, moveY);
	}

	public void stop(boolean cancel) {
		onStop.accept(cancel);
	}

	@FunctionalInterface
	public static interface OnDrag {
		public void drag(int x, int y, int moveX, int moveY);
	}
}
