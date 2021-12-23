package com.pinball3d.zone.math;

public class ZoneMathHelper {
	public static int mid(int a, int b, int c) {
		return a > b ? a > c ? (b > c ? b : c) : a : b > c ? (a > c ? a : c) : b;
	}
}
