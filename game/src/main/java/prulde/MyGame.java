package prulde;

import prulde.core.Game;
import prulde.render.Color;

public class MyGame extends Game {

	@Override
	protected void create() {
	}

	@Override
	protected void update() {

	}

	@Override
	protected void render() {
		renderer.drawCell('@', 0, 0, Color.blue, Color.green);
		renderer.drawCell(',', 1, 0, Color.green, Color.red);
	}

	public static void main(String[] args) {
		Game game = new MyGame();
	}
}