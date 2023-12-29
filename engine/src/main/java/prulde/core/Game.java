package prulde.core;

import lombok.extern.log4j.Log4j2;
import prulde.render.Renderer;
import prulde.lwjgl.LwjglWindow;

import javax.inject.Inject;

@Log4j2
public abstract class Game {
	@Inject
	protected Renderer renderer;

	@Inject
	LwjglWindow window;

	public Game() {
		Injector.getEngineComponent().inject(this);
		init();
		create();
		loop();
	}

	public void loop() {
		int fps = 60;
		double tick = 1000000000d / fps;
		double lag = 0d;
		long lastTime = System.nanoTime();
		long timer = System.currentTimeMillis();
		int updates = 0;
		int frames = 0;
		while (!window.shouldClose()) {
			long currentTime = System.nanoTime();
			long elapsed = currentTime - lastTime;
			lastTime = currentTime;

			lag += elapsed;

			while (lag >= tick) {
				updates++;
				update();
				lag -= tick;
			}
			render();
			renderer.render();
			window.pollEvents();

			frames++;
			if (System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				log.info(updates + " ups, " + frames + " frames");
				frames = 0;
				updates = 0;
			}
		}

		window.destroy();
	}

	protected abstract void create();

	protected abstract void update();

	protected abstract void render();

	private void init() {
		window.init();
		renderer.init();
	}
}
