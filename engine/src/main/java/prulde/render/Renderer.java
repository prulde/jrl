package prulde.render;

public interface Renderer {
	void changeClearColor(Color c);

	void render();

	void drawTile(int glyph, int x, int y, Color c);

	void drawTile(int glyph, int x, int y, Color fc, Color bc);

	void drawRect(int x, int y, int w, int h, Color c);
}
