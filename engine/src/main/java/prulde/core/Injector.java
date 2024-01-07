package prulde.core;

import prulde.input.KeyboardInput;
import prulde.input.MouseInput;
import prulde.lwjgl.*;
import prulde.render.Renderer;

public class Injector {
	private final static LwjglWindow window;
	private final static Renderer renderer;
	private final static MouseInput mouseInput;
	private final static KeyboardInput keyboardInput;

	static {
		window = new LwjglWindow();

		if (Config.currentRenderer == Config.Renderer.TTF_RENDERER)
			renderer = new LwjglFontRenderer();
		else
			renderer = new LwjglImageRenderer();
		
		mouseInput = new LwjglMouseInput();
		keyboardInput = new LwjglKeyboardInput();
	}

	public static LwjglWindow provideWindow() {
		return window;
	}

	public static KeyboardInput provideKeyInput() {
		return keyboardInput;
	}

	public static MouseInput provideMouseInput() {
		return mouseInput;
	}

	public static Renderer provideRenderer() {
		return renderer;
	}
}
