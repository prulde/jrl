package prulde;

import prulde.core.Game;
import prulde.input.Input;
import prulde.render.Color;

public class MyGame extends Game {
	int x = 0;
	int y = 0;

	@Override
	protected void create() {
	}

	@Override
	protected void update() {
		switch (keyboardInput.getState().key) {
			case Input.Keys.ARROW_UP -> y -= 1;
			case Input.Keys.ARROW_DOWN -> y += 1;
			case Input.Keys.ARROW_LEFT -> x -= 1;
			case Input.Keys.ARROW_RIGHT -> x += 1;
		}
	}

	@Override
	protected void render() {
		renderer.drawTile('@', x, y, Color.blue, Color.green);
		renderer.drawTile(',', 1, 0, Color.green, Color.red);
		renderer.drawTile('#', 2, 0, Color.green, Color.red);
		renderer.drawTile(9827, 3, 0, Color.green, Color.red);
	}

	public static void main(String[] args) {
		Game game = new MyGame();
	}
}