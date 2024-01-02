package prulde.core.dagger;

import dagger.Component;
import prulde.core.Game;
import prulde.lwjgl.LwjglFontRenderer;
import prulde.lwjgl.LwjglKeyboardInput;
import prulde.lwjgl.LwjglMouseInput;

import javax.inject.Singleton;

@Singleton
@Component(modules = {EngineModule.class})
public interface EngineComponent {
	void inject(LwjglFontRenderer renderer);

	void inject(LwjglMouseInput mouseInput);

	void inject(LwjglKeyboardInput keyInput);

	void inject(Game game);
}
