package com.pinball3d.zone.gui;

import java.util.Set;

import com.pinball3d.zone.gui.component.Component;

public interface IHasComponents {
	public Set<Component> getComponents();

	public void addComponent(Component c);

	public void removeComponents();

	public int getX();

	public int getY();

	public default void addComponents() {
	};

	public default void refreshComponents() {
		removeComponents();
		addComponents();
	}
}
