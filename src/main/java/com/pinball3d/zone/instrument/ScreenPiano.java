package com.pinball3d.zone.instrument;

import java.io.IOException;

import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.pinball3d.zone.KeyBindingHandler;
import com.pinball3d.zone.network.MessageCloseChannel;
import com.pinball3d.zone.network.MessageMidiToServer;
import com.pinball3d.zone.network.NetworkHandler;
import com.pinball3d.zone.sphinx.elite.MouseHandler;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
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
	private int mousePrevX, mousePrevY;

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
		if (showGui) {
			mouseX = MouseHandler.getX();
			mouseY = MouseHandler.getY();
			if (mouseX != mousePrevX || mouseY != mousePrevY) {
				onMouseMoved(mouseX, mouseY);
				mousePrevX = mouseX;
				mousePrevY = mouseY;
			}
			GlStateManager.matrixMode(GL11.GL_PROJECTION);
			GlStateManager.pushMatrix();
			GlStateManager.loadIdentity();
			GlStateManager.ortho(0, mc.displayWidth, mc.displayHeight, 0, 1000.0D, 3000.0D);
			GlStateManager.matrixMode(GL11.GL_MODELVIEW);
			GlStateManager.pushMatrix();
			drawGradientRect(0, 0, mc.displayWidth, mc.displayHeight, -1072689136, -804253680);
			int x = 0;
			int y = 0;
			mc.getTextureManager().bindTexture(PIANO);
			drawTexturedModalRect(x, y, 0, 0, 13, 92);
			drawTexturedModalRect(x + 11, y, 131, 0, 8, 61);
			drawTexturedModalRect(x += 13, y, 13, 0, 13, 92);
			for (int i = 0; i < 7; i++) {
				drawTexturedModalRect(x += 13, y, 26, 0, 13, 92);
				drawTexturedModalRect(x + 9, y, 131, 0, 8, 61);
				drawTexturedModalRect(x += 13, y, 39, 0, 13, 92);
				drawTexturedModalRect(x + 10, y, 131, 0, 8, 61);
				drawTexturedModalRect(x += 13, y, 52, 0, 13, 92);
				drawTexturedModalRect(x += 13, y, 65, 0, 13, 92);
				drawTexturedModalRect(x + 8, y, 131, 0, 8, 61);
				drawTexturedModalRect(x += 13, y, 78, 0, 13, 92);
				drawTexturedModalRect(x + 9, y, 131, 0, 8, 61);
				drawTexturedModalRect(x += 13, y, 91, 0, 13, 92);
				drawTexturedModalRect(x + 11, y, 131, 0, 8, 61);
				drawTexturedModalRect(x += 13, y, 104, 0, 13, 92);
			}
			drawTexturedModalRect(x += 13, y, 117, 0, 14, 92);

			GlStateManager.popMatrix();
			GlStateManager.matrixMode(GL11.GL_PROJECTION);
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
		if (button != 0) {
			return;
		}
		int k = getKeyFromPos(MouseHandler.getX(), MouseHandler.getY());
		if (k >= 0 && !KeyBindingHandler.nodes[k].isKeyDown()) {
			noteOn(k);
		}
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int button) {
		if (button != 0) {
			return;
		}
		int k = getKeyFromPos(MouseHandler.getX(), MouseHandler.getY());
		if (k >= 0 && !KeyBindingHandler.nodes[k].isKeyDown()) {
			noteOff(k);
		}
	}

	private void onMouseMoved(int mouseX, int mouseY) {
		if (!MouseHandler.isButtonPressed(0)) {
			return;
		}
		int k = getKeyFromPos(mousePrevX, mousePrevY);
		int k2 = getKeyFromPos(mouseX, mouseY);
		if (k != k2) {
			if (k >= 0 && !KeyBindingHandler.nodes[k].isKeyDown()) {
				noteOff(k);
			}
			if (k2 >= 0 && !KeyBindingHandler.nodes[k2].isKeyDown()) {
				noteOn(k2);
			}
		}
	}

	private static int getKeyFromPos(int mouseX, int mouseY) {
		int x = 0;
		int y = 0;
		if (mouseX >= x && mouseX <= x + 677 && mouseY >= y && mouseY <= 92) {
			if (mouseY <= y + 61) {
				if (mouseX >= x + 11 && mouseX <= x + 19) {
					return 1;
				}
				for (int i = 0; i < 7; i++) {
					if (mouseX >= x + 35 + i * 91 && mouseX <= x + 43 + i * 91) {
						return i * 12 + 4;
					}
					if (mouseX >= x + 49 + i * 91 && mouseX <= x + 57 + i * 91) {
						return i * 12 + 6;
					}
					if (mouseX >= x + 73 + i * 91 && mouseX <= x + 81 + i * 91) {
						return i * 12 + 9;
					}
					if (mouseX >= x + 87 + i * 91 && mouseX <= x + 95 + i * 91) {
						return i * 12 + 11;
					}
					if (mouseX >= x + 102 + i * 91 && mouseX <= x + 110 + i * 91) {
						return i * 12 + 13;
					}
				}
			}
			int k = (mouseX - x) / 13;
			if (k == 52) {
				return 87;
			}
			if (k >= 2) {
				int t = k + 1;
				k -= 2;
				t += k / 7 * 5;
				k %= 7;
				if (k >= 1) {
					t++;
					if (k >= 2) {
						t++;
						if (k >= 4) {
							t++;
							if (k >= 5) {
								t++;
								if (k >= 6) {
									t++;
								}
							}
						}
					}
				}
				return t;
			} else if (k >= 1) {
				k++;
			}
			return k;
		}
		return -1;
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
//					try {
//						track.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 0, i + 23, keystroke),
//								sequencer.getTickPosition()));
					noteOn(i);
//					} catch (InvalidMidiDataException e) {
//						e.printStackTrace();
//					}
				} else {
//					try {
//						track.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, 0, i + 23, 0),sequencer.getTickPosition()));
					noteOff(i);
//					} catch (InvalidMidiDataException e) {
//						e.printStackTrace();
//					}
				}
			}
		}
	}

	private void noteOn(int key) {
		if (states[key] == 0) {
			System.out.println("on" + key);
			int keystroke = 127;
			states[key] = keystroke;
			ClientMidiHandler.onMessage(ShortMessage.NOTE_ON, 0, key + 23, keystroke);
			NetworkHandler.instance.sendToServer(new MessageMidiToServer(ShortMessage.NOTE_ON, key + 23, keystroke));
		}
	}

	private void noteOff(int key) {
		if (states[key] > 0) {
			System.out.println("off" + key);
			states[key] = 0;
			ClientMidiHandler.onMessage(ShortMessage.NOTE_OFF, 0, key + 23, 0);
			NetworkHandler.instance.sendToServer(new MessageMidiToServer(ShortMessage.NOTE_OFF, key + 23, 0));
		}
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		Keyboard.enableRepeatEvents(true);
		NetworkHandler.instance.sendToServer(new MessageCloseChannel());
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
}
