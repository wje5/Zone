package com.pinball3d.zone.instrument;

import java.io.IOException;

import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.pinball3d.zone.KeyBindingHandler;
import com.pinball3d.zone.network.MessageCloseChannel;
import com.pinball3d.zone.network.MessageMidiToServer;
import com.pinball3d.zone.network.NetworkHandler;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ScreenPiano extends GuiScreen {
	public static final ResourceLocation PIANO = new ResourceLocation("zone:textures/gui/piano.png");

	private boolean showGui = true;
	private int[] states = new int[88];
	private Sequencer sequencer;
	private Track track;

	public ScreenPiano() {
		super();
		Keyboard.enableRepeatEvents(false);
//		try {
//			Sequence seq = new Sequence(Sequence.PPQ, 4);
//			track = seq.createTrack();
//			for (int i = 24; i < 48; i++) {
////				track.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 6, i + 24, 30 + i * 2), i));
////				track.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, 6, i + 24, 30 + i * 2), i));
//			}
//			sequencer = MidiSystem.getSequencer();
//			sequencer.open();
//			sequencer.setSequence(seq);
//			sequencer.setTempoInBPM(1200);
//			sequencer.start();
//			track.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 8, 48, 65), 50000000));
//			sequencer.getTickPosition();
//		} catch (MidiUnavailableException | InvalidMidiDataException e) {
//			e.printStackTrace();
//		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {

	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (keyCode == mc.gameSettings.keyBindSneak.getKeyCode()) {
			mc.displayGuiScreen((GuiScreen) null);
			if (mc.currentScreen == null) {
				mc.setIngameFocus();
			}
		} else if (keyCode == Keyboard.KEY_ESCAPE) {
			if (showGui) {
				showGui = false;
				Mouse.setGrabbed(true);
			} else {
				showGui = true;
				Mouse.setGrabbed(false);
			}
		}
	}

	@Override
	public void handleKeyboardInput() throws IOException {
		super.handleKeyboardInput();
		boolean isPress = Keyboard.getEventKeyState();
		int code = Keyboard.getEventKey();
		for (int i = 0; i < 88; i++) {
			if (KeyBindingHandler.nodes[i].getKeyCode() == code && code != 0) {
				if (isPress) {
					int keystroke = 127;
					states[i] = keystroke;
//					try {
//						track.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 0, i + 23, keystroke),
//								sequencer.getTickPosition()));
					ClientMidiHandler.onMessage(ShortMessage.NOTE_ON, 0, i + 23, keystroke);
//					} catch (InvalidMidiDataException e) {
//						e.printStackTrace();
//					}
					NetworkHandler.instance
							.sendToServer(new MessageMidiToServer(ShortMessage.NOTE_ON, i + 23, keystroke));
				} else {
					states[i] = 0;
//					try {
//						track.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, 0, i + 23, 0),sequencer.getTickPosition()));
					ClientMidiHandler.onMessage(ShortMessage.NOTE_OFF, 0, i + 23, 0);
//					} catch (InvalidMidiDataException e) {
//						e.printStackTrace();
//					}
					NetworkHandler.instance.sendToServer(new MessageMidiToServer(ShortMessage.NOTE_OFF, i + 23, 0));
				}
			}
		}
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		Keyboard.enableRepeatEvents(true);
		NetworkHandler.instance.sendToServer(new MessageCloseChannel());
	}
}
