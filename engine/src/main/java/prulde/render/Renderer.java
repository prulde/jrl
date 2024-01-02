package prulde.render;

public interface Renderer {
	void changeClearColor(Color c);

	void render();

	void drawGlyph(int glyph, int x, int y, Color c);

	void drawTile(int glyph, int x, int y, Color fgc, Color bgc);

	void drawRect(int x, int y, int w, int h, Color c);
}
