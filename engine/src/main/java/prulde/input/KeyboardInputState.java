package prulde.input;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import prulde.core.Reusable;

@Setter
@Getter
@AllArgsConstructor
public class KeyboardInputState implements Reusable {
	private boolean shiftDown;
	private boolean ctrlDown;
	private boolean altDown;
	private int key;

	@Override
	public void reset() {
		shiftDown = false;
		ctrlDown = false;
		altDown = false;
		key = Input.Keys.NO_INPUT;
	}
}
