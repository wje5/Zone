package com.pinball3d.zone.sphinx.log;

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
}
