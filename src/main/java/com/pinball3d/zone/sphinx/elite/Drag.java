package com.pinball3d.zone.sphinx.elite;

public class Drag {
	private OnDrag onDrag;
	private Runnable onStop;

	public Drag(OnDrag onDrag, Runnable onStop) {
		this.onDrag = onDrag;
		this.onStop = onStop;
	}

	public void setOnStop(Runnable onStop) {
		this.onStop = onStop;
	}

	public void drag(int x, int y, int moveX, int moveY) {
		onDrag.drag(x, y, moveX, moveY);
	}

	public void stop() {
		onStop.run();
	}

	@FunctionalInterface
	public static interface OnDrag {
		public void drag(int x, int y, int moveX, int moveY);
	}
}
