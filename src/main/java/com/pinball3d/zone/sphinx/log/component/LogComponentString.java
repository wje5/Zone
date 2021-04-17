package com.pinball3d.zone.sphinx.log.component;

public class LogComponentString extends LogComponent {
	private String s;

	public LogComponentString(String s) {
		super(Type.STRING);
		this.s = s;
	}

	@Override
	public String toString() {
		return s;
	}

	@Override
	public int getColor() {
		return 0xFFE0E0E0;
	}
}
