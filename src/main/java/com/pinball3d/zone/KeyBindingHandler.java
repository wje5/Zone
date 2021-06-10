package com.pinball3d.zone;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class KeyBindingHandler {
	public static KeyBinding[] nodes = new KeyBinding[88];

	public KeyBindingHandler() {
		String[] names = new String[] { "A2", "♭B2", "B2", "C1", "♭D1", "D1", "♭E1", "E1", "F1", "♭G1", "G1", "♭A1",
				"A1", "♭B1", "B1", "C", "♭D", "D", "♭E", "E", "F", "♭G", "G", "♭A", "A", "♭B", "B", "c", "♭d", "d",
				"♭e", "e", "f", "♭g", "g", "♭a", "a", "♭b", "b", "c1", "♭d1", "d1", "♭e1", "e1", "f1", "♭g1", "g1",
				"♭a1", "a1", "♭b1", "b1", "c2", "♭d2", "d2", "♭e2", "e2", "f2", "♭g2", "g2", "♭a2", "a2", "♭b2", "b2",
				"c3", "♭d3", "d3", "♭e3", "e3", "f3", "♭g3", "g3", "♭a3", "a3", "♭b3", "b3", "c4", "♭d4", "d4", "♭e4",
				"e4", "f4", "♭g4", "g4", "♭a4", "a4", "♭b4", "b4", "c5" };
		for (int i = 0; i < 88; i++) {
			nodes[i] = new KeyBinding("key." + names[i], KeyConflictContext.GUI, Keyboard.KEY_NONE,
					"key.category.zone");
			ClientRegistry.registerKeyBinding(nodes[i]);
		}
	}
}
