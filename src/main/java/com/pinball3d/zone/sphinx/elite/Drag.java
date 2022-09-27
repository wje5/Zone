package com.pinball3d.zone.sphinx.elite;

public class Drag {
	public final int button;
	private OnDrag onDrag;
	private OnStop onStop;
	public final boolean grab;

	public Drag(int button) {
		this(button, (x, y, moveX, moveY) -> {
		}, (x, y, cancel) -> {
		});
	}

	public Drag(int button, OnDrag onDrag, OnStop onStop) {
		this(button, onDrag, onStop, false);
	}

	public Drag(int button, OnDrag onDrag, OnStop onStop, boolean grab) {
		this.button = button;
		this.onDrag = onDrag;
		this.onStop = onStop;
		this.grab = grab;
	}

	public void setOnStop(OnStop onStop) {
		this.onStop = onStop;
	}

	public void drag(int x, int y, int moveX, int moveY) {
		onDrag.drag(x, y, moveX, moveY);
	}

	public void stop(boolean cancel) {
		onStop.stop(MouseHandler.getX(), MouseHandler.getY(), cancel);
	}

	@FunctionalInterface
	public static interface OnDrag {
		public void drag(int x, int y, int moveX, int moveY);
	}

	@FunctionalInterface
	public static interface OnStop {
		public void stop(int x, int y, boolean cancel);
	}
}
