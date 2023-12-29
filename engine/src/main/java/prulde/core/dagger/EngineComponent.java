package prulde.core.dagger;

import dagger.Component;
import prulde.core.Game;
import prulde.lwjgl.LwjglRendererOGL;

import javax.inject.Singleton;

@Singleton
@Component(modules = {EngineModule.class})
public interface EngineComponent {
	void inject(LwjglRendererOGL renderer);

	void inject(Game game);
}
