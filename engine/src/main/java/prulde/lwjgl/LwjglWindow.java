package prulde.lwjgl;

import lombok.Getter;
import org.lwjgl.glfw.*;
import prulde.core.Config;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class LwjglWindow {
	private long window;

	@Getter
	private float horizontalScaleFactor = 1f;

	@Getter
	private float verticalScaleFactor = 1f;
	private final GLFWErrorCallback errorCallback = GLFWErrorCallback.createPrint(System.err).set();

	public LwjglWindow() {
		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if (!glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");

		glfwSetErrorCallback(errorCallback);

		// Configure GLFW
		glfwDefaultWindowHints(); // optional, the current window hints are already the default

		// causes Enum error if GLFW_CONTEXT_VERSION_MINOR=2 or glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
		//glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		//glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 0);
		//glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_ANY_PROFILE);//GLFW_OPENGL_CORE_PROFILE
		//glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);

		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, Config.Window.resizable ? GLFW_TRUE : GLFW_FALSE); // the window will be resizable

		// glfwGetPrimaryMonitor()
		// Create the window
		window = glfwCreateWindow(Config.Window.width, Config.Window.height, "Hello World!", NULL, NULL);

		if (window == NULL) {
			glfwTerminate();
			throw new RuntimeException("Failed to create the GLFW window");
		}

		// set callback
		glfwSetFramebufferSizeCallback(window, framebufferSizeCallback);

		GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		glfwSetWindowPos(window, (vidmode.width() - Config.Window.width) / 2, (vidmode.height() - Config.Window.height) / 2);

		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		// Will let lwjgl know we want to use this context as the context to draw with
		glfwSwapInterval(Config.Window.vsync ? 1 : 0);
		// Make the window visible
		glfwShowWindow(window);

		glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);

		// Get the actual framebuffer width/height in order to deal with different pixel densities
		int[] width = new int[1];
		int[] height = new int[1];
		glfwGetFramebufferSize(window, width, height);
		// Get the window width/height
		int[] windowWidth = new int[1];
		int[] windowHeight = new int[1];
		glfwGetWindowSize(window, windowWidth, windowHeight);
		horizontalScaleFactor = (float) windowWidth[0] / (float) width[0];
		verticalScaleFactor = (float) windowHeight[0] / (float) height[0];

		Config.Window.width = width[0];
		Config.Window.height = height[0];
	}

	public boolean shouldClose() {
		return glfwWindowShouldClose(window);
	}

	public void pollEvents() {
		glfwPollEvents();
	}

	public void swapBuffers() {
		glfwSwapBuffers(window);
	}

	public void setKeyCallback(GLFWKeyCallback keyCallback) {
		glfwSetKeyCallback(window, keyCallback);
	}

	public void setMouseButtonCallback(GLFWMouseButtonCallback mouseButtonCallback) {
		glfwSetMouseButtonCallback(window, mouseButtonCallback);
	}

	public void setCursorPosCallback(GLFWCursorPosCallback cursorPosCallback) {
		glfwSetCursorPosCallback(window, cursorPosCallback);
	}

	public void destroy() {
		// free input callbacks
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);

		// Terminate GLFW and free the error callback
		glfwTerminate();
		errorCallback.free();
	}

	private final GLFWFramebufferSizeCallback framebufferSizeCallback = new GLFWFramebufferSizeCallback() {
		@Override
		public void invoke(long window, int w, int h) {
			if (w > 0 && h > 0) {
				Config.Window.width = w;
				Config.Window.height = h;
			}
		}
	};
}
