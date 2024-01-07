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
		switch (keyboardInput.getState().getKey()) {
			case Input.Keys.ARROW_UP -> y -= 1;
			case Input.Keys.ARROW_DOWN -> y += 1;
			case Input.Keys.ARROW_LEFT -> x -= 1;
			case Input.Keys.ARROW_RIGHT -> x += 1;
		}
	}

	@Override
	protected void render() {
		renderer.drawTile('@', x, y, Color.original, Color.green);
		renderer.drawTile(35, 1, 0, Color.original);
		renderer.drawTile(31, 5, 6, Color.original, Color.green);
		renderer.drawTile(36, 7, 8, Color.original, Color.green);
	}

	public static void main(String[] args) {
		Game game = new MyGame();
	}
}