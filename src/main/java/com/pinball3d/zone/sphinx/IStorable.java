package com.pinball3d.zone.sphinx;

public interface IStorable {
	public StorageWrapper getStorges();

	public StorageWrapper extract(StorageWrapper request);

	public StorageWrapper insert(StorageWrapper request, boolean simulate);
}
