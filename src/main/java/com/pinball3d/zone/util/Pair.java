package com.pinball3d.zone.util;

import java.util.Objects;

public class Pair<K, V> {
	private final K key;
	private final V value;

	public Pair(K key, V value) {
		this.key = key;
		this.value = value;
	}

	public K key() {
		return key;
	}

	public V value() {
		return value;
	}

	@Override
	public String toString() {
		return "{" + key.toString() + "=" + value.toString() + "}";
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		return obj instanceof Pair && Objects.equals(((Pair) obj).key, key)
				&& Objects.equals(((Pair) obj).value, value);
	}

	@Override
	public int hashCode() {
		return (key == null ? 0 : key.hashCode()) * 31 + (value == null ? 0 : value.hashCode());
	}
}