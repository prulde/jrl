package prulde.core.dagger;

import dagger.Module;
import dagger.Provides;
import prulde.core.Config;
import prulde.render.Renderer;
import prulde.lwjgl.LwjglRendererOGL;
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
	public static Renderer provideRenderer() {
		return new LwjglRendererOGL();
	}
}
