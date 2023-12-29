package prulde.lwjgl;

import lombok.extern.log4j.Log4j2;
import org.lwjgl.glfw.GLFWMouseButtonCallback;

import static org.lwjgl.glfw.GLFW.*;

@Log4j2
public class LwjglMouseInput extends GLFWMouseButtonCallback {

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
}
