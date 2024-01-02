package prulde.lwjgl;

import lombok.extern.log4j.Log4j2;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import prulde.core.Injector;
import prulde.input.MouseInput;

import javax.inject.Inject;

import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.GLFW.*;

@Log4j2
public class LwjglMouseInput implements MouseInput {
	@Inject
	LwjglWindow window;
	private double mx = 0;
	private double my = 0;

	public LwjglMouseInput() {
		Injector.getEngineComponent().inject(this);
		// set callbacks
		window.setMouseButtonCallback(mouseButtonCallback);
		window.setCursorPosCallback(cursorPosCallback);
	}

	private final GLFWMouseButtonCallback mouseButtonCallback = new GLFWMouseButtonCallback() {
		@Override
		public void invoke(long window, int button, int action, int mods) {
			int lmbState = glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_LEFT);
			int rmbState = glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_RIGHT);
			if (lmbState == GLFW_PRESS) {
				log.info("lmb pressed");
			} else if (rmbState != GLFW_PRESS && lmbState == GLFW_RELEASE) {
				log.info("lmb released");
			}

			if (rmbState == GLFW_PRESS) {
				log.info("rmb pressed");
			} else if (lmbState != GLFW_PRESS && rmbState == GLFW_RELEASE) {
				log.info("rmb released");
			}
		}
	};

	private final GLFWCursorPosCallback cursorPosCallback = new GLFWCursorPosCallback() {
		@Override
		public void invoke(long window, double x, double y) {
			mx = x;
			my = y;
		}
	};

	@Override
	public int getMouseX() {
		return (int) mx;
	}

	@Override
	public int getMouseY() {
		return (int) my;
	}
}
