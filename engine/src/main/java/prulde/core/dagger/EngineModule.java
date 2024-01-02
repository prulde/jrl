package prulde.core.dagger;

import dagger.Module;
import dagger.Provides;
import prulde.input.KeyboardInput;
import prulde.input.MouseInput;
import prulde.lwjgl.LwjglKeyboardInput;
import prulde.lwjgl.LwjglMouseInput;
import prulde.render.Renderer;
import prulde.lwjgl.LwjglFontRenderer;
import prulde.lwjgl.LwjglWindow;

import javax.inject.Singleton;

@Module
public class EngineModule {
	@Provides
	@Singleton
	public static LwjglWindow provideWindow() {
		return new LwjglWindow();
	}

	@Provides
	@Singleton
	public static KeyboardInput provideKeyInput() {
		return new LwjglKeyboardInput();
	}

	@Provides
	@Singleton
	public static MouseInput provideMouseInput() {
		return new LwjglMouseInput();
	}

	@Provides
	@Singleton
	public static Renderer provideRenderer() {
		return new LwjglFontRenderer();
	}
}
