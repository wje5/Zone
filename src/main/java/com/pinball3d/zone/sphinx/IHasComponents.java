package com.pinball3d.zone.sphinx;

import java.util.Set;

import com.pinball3d.zone.sphinx.component.Component;

public interface IHasComponents {
	public Set<Component> getComponents();

	public void addComponent(Component c);

	public void removeComponents();

	public default void addComponents() {
	};

	public default void refreshComponents() {
		removeComponents();
		addComponents();
	}
}
