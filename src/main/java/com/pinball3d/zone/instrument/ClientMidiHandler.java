package com.pinball3d.zone.instrument;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

public class ClientMidiHandler {
	private static Sequencer sequencer;
	private static Track track;

	public static void init() {
		try {
			Sequence seq = new Sequence(Sequence.PPQ, 4);
			track = seq.createTrack();
			sequencer = MidiSystem.getSequencer();
			sequencer.open();
			sequencer.setSequence(seq);
			sequencer.setTempoInBPM(1200);
			track.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 0, 48, 65), 50000000));
		} catch (MidiUnavailableException | InvalidMidiDataException e) {
			e.printStackTrace();
		}
	}

	public static void onMessage(int command, int channel, int pitch, int keystroke) {
		if (track != null) {
			try {
				sequencer.start();
				track.add(new MidiEvent(new ShortMessage(command, channel, pitch, keystroke),
						sequencer.getTickPosition()));
			} catch (InvalidMidiDataException e) {
				e.printStackTrace();
			}
		}
	}
}
