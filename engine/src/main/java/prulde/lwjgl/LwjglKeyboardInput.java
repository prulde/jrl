package prulde.lwjgl;

import lombok.extern.log4j.Log4j2;
import org.lwjgl.glfw.GLFWKeyCallback;
import prulde.core.Injector;
import prulde.input.Input;
import prulde.input.KeyboardInputState;
import prulde.input.KeyboardInput;

import javax.inject.Inject;

import java.util.concurrent.LinkedBlockingQueue;

import static org.lwjgl.glfw.GLFW.*;

@Log4j2
public class LwjglKeyboardInput implements KeyboardInput {
	@Inject
	LwjglWindow window;
	private final boolean[] keys = new boolean[65536];
	private final LinkedBlockingQueue<KeyboardInputState> buffer = new LinkedBlockingQueue<>();
	private int bufferSize = 2;

	public LwjglKeyboardInput() {
		Injector.getEngineComponent().inject(this);
		// set callback
		window.setKeyCallback(keyCallback);
	}

	private final GLFWKeyCallback keyCallback = new GLFWKeyCallback() {
		@Override
		public void invoke(long window, int key, int scancode, int action, int mods) {
			if (key < 0 || key >= keys.length) return;
			keys[key] = action != GLFW_RELEASE;
			if (action == GLFW_PRESS || action == GLFW_REPEAT) {
				keyPressed(new KeyboardInputState(keys[GLFW_KEY_LEFT_SHIFT] || keys[GLFW_KEY_RIGHT_SHIFT],
						keys[GLFW_KEY_LEFT_CONTROL] || keys[GLFW_KEY_RIGHT_CONTROL],
						keys[GLFW_KEY_LEFT_ALT] || keys[GLFW_KEY_RIGHT_ALT],
						getKeyCode(key)));
			}
		}
	};

	@Override
	public KeyboardInputState getState() {
		if (!buffer.isEmpty())
			return buffer.remove();
		return new KeyboardInputState(false, false, false, Input.Keys.NO_INPUT);
	}

	private void keyPressed(KeyboardInputState k) {
		if (buffer.size() < bufferSize) {
			buffer.add(k);
		}
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
