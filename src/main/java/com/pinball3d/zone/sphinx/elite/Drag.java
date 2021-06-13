package com.pinball3d.zone.sphinx.elite;

import java.util.function.Consumer;

public class Drag {
	private OnDrag onDrag;
	private Consumer<Boolean> onStop;

	public static Drag EMPTY = new Drag((x, y, moveX, moveY) -> {
	}, cancel -> {
	});

	public Drag(OnDrag onDrag, Consumer<Boolean> onStop) {
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
