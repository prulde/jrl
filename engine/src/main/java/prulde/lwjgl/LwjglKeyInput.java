package prulde.lwjgl;

import lombok.extern.log4j.Log4j2;
import org.lwjgl.glfw.GLFWKeyCallback;

import static org.lwjgl.glfw.GLFW.*;

@Log4j2
public class LwjglKeyInput extends GLFWKeyCallback {
	private boolean[] keys = new boolean[65536];

	@Override
	public void invoke(long window, int key, int scancode, int action, int mods) {
		if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
			//glfwSetWindowShouldClose(window, true);
		}
	}
}
