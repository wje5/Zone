package com.pinball3d.zone.gui;

import java.util.Set;

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
