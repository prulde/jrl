package prulde.render;

public interface Renderer {
	void init();

	void changeClearColor(Color c);

	void render();

	void drawGlyph(int glyph, int x, int y, Color c);

	void drawCell(int glyph, int x, int y, Color c);

	void drawCell(int glyph, int x, int y, Color fgc, Color bgc);

	void drawRect(int x, int y, int w, int h, Color c);
}
