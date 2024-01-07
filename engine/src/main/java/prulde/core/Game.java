package prulde.core;

import lombok.extern.log4j.Log4j2;
import prulde.input.KeyboardInput;
import prulde.input.MouseInput;
import prulde.render.Renderer;
import prulde.lwjgl.LwjglWindow;


@Log4j2
public abstract class Game {
	private final LwjglWindow window = Injector.provideWindow();
	protected final Renderer renderer = Injector.provideRenderer();
	protected final MouseInput mouseInput = Injector.provideMouseInput();
	protected final KeyboardInput keyboardInput = Injector.provideKeyInput();

	private long timer = System.currentTimeMillis();
	private int updates = 0;
	private int frames = 0;

	public Game() {
		create();
		loop();
	}

	public void loop() {
		int fps = 60;
		double tick = 1000000000d / fps;
		double lag = 0d;
		long lastTime = System.nanoTime();

		while (!window.shouldClose()) {
			long currentTime = System.nanoTime();
			long elapsed = currentTime - lastTime;
			lastTime = currentTime;

			lag += elapsed;

			while (lag >= tick) {
				update();
				updates++;
				lag -= tick;
			}
			render();
			renderer.render();
			window.pollEvents();
			frames++;

			if (Config.logPerformance)
				logPerformance();
		}

		window.destroy();
	}

	protected abstract void create();

	protected abstract void update();

	protected abstract void render();

	private void logPerformance() {
		if (System.currentTimeMillis() - timer > 1000) {
			timer += 1000;
			log.info(updates + " ups, " + frames + " frames");
			frames = 0;
			updates = 0;
		}
	}
}
