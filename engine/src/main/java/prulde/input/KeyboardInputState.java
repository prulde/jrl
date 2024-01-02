package prulde.input;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class KeyboardInputState {
	public final boolean shiftDown;
	public final boolean ctrlDown;
	public final boolean altDown;
	public final int key;
}
