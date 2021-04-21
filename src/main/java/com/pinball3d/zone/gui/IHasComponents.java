package com.pinball3d.zone.gui;

import java.util.List;

import com.pinball3d.zone.gui.component.Component;

public interface IHasComponents {
	public List<Component> getComponents();

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
