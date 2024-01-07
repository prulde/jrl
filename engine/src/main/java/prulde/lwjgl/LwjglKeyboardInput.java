package prulde.lwjgl;

import lombok.extern.log4j.Log4j2;
import org.lwjgl.glfw.GLFWKeyCallback;
import prulde.core.Injector;
import prulde.input.Input;
import prulde.input.KeyboardInputState;
import prulde.input.KeyboardInput;

import static org.lwjgl.glfw.GLFW.*;

@Log4j2
public class LwjglKeyboardInput implements KeyboardInput {
	private final LwjglWindow window = Injector.provideWindow();
	private final boolean[] keys = new boolean[65536];

	// instead of allocating new objects for the buffer
	private KeyboardInputState dummy = new KeyboardInputState(false, false, false, Input.Keys.NO_INPUT);
	private KeyboardInputState state = new KeyboardInputState(false, false, false, Input.Keys.NO_INPUT);

	public LwjglKeyboardInput() {
		// set callback
		window.setKeyCallback(keyCallback);
	}

	private final GLFWKeyCallback keyCallback = new GLFWKeyCallback() {
		@Override
		public void invoke(long window, int key, int scancode, int action, int mods) {
			if (key < 0 || key >= keys.length) return;
			keys[key] = action != GLFW_RELEASE;
			if (action == GLFW_PRESS || action == GLFW_REPEAT) {
				state.setKey(getKeyCode(key));
				state.setShiftDown(keys[GLFW_KEY_LEFT_SHIFT] || keys[GLFW_KEY_RIGHT_SHIFT]);
				state.setCtrlDown(keys[GLFW_KEY_LEFT_CONTROL] || keys[GLFW_KEY_RIGHT_CONTROL]);
				state.setAltDown(keys[GLFW_KEY_LEFT_ALT] || keys[GLFW_KEY_RIGHT_ALT]);
			}
		}
	};

	@Override
	public KeyboardInputState getState() {
		dummy.setKey(state.getKey());
		dummy.setShiftDown(state.isShiftDown());
		dummy.setCtrlDown(state.isCtrlDown());
		dummy.setAltDown(state.isAltDown());
		state.reset();
		return dummy;
	}

	private int getKeyCode(int k) {
		return switch (k) {
			case GLFW_KEY_RIGHT -> Input.Keys.ARROW_RIGHT;
			case GLFW_KEY_LEFT -> Input.Keys.ARROW_LEFT;
			case GLFW_KEY_DOWN -> Input.Keys.ARROW_DOWN;
			case GLFW_KEY_UP -> Input.Keys.ARROW_UP;
			default -> Input.Keys.NO_INPUT;
		};
	}
}
