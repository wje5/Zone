package com.pinball3d.zone.sphinx.elite;

import java.util.function.Consumer;

public class Drag {
	public final int button;
	private OnDrag onDrag;
	private Consumer<Boolean> onStop;
	public final boolean grab;

	public Drag(int button) {
		this(button, (x, y, moveX, moveY) -> {
		}, cancel -> {
		});
	}

	public Drag(int button, OnDrag onDrag, Consumer<Boolean> onStop) {
		this(button, onDrag, onStop, false);
	}

	public Drag(int button, OnDrag onDrag, Consumer<Boolean> onStop, boolean grab) {
		this.button = button;
		this.onDrag = onDrag;
		this.onStop = onStop;
		this.grab = grab;
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
