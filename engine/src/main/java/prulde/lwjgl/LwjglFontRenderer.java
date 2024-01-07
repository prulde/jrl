package prulde.lwjgl;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.stb.STBTTAlignedQuad;
import prulde.core.Config;
import prulde.render.Color;

public final class LwjglFontRenderer extends LwjglRenderer {
	private final LwjglFontTexture fontTexture = new LwjglFontTexture();

	public LwjglFontRenderer() {
		super();
		fontTexture.loadFromTTF(Config.Font.fontPath);
		glBindTexture(GL_TEXTURE_2D, fontTexture.getTextureId());
	}

	// draw centered glyph
	@Override
	public void drawTile(int glyph, int x, int y, Color fc) {
		int w = Config.Font.tileWidth;
		int h = Config.Font.tileHeight;
		STBTTAlignedQuad quad = fontTexture.glyphInfo(glyph);
		// center glyph
		float charWidth = quad.x1() - quad.x0();
		float charHeight = quad.y1() - quad.y0();
		x += (Config.Font.tileWidth / 2) - (charWidth / 2);
		// subtract half the ascent to compensate for centering
		y += (Config.Font.tileHeight / 2) - (charHeight / 2) - ((fontTexture.getAscent() + quad.y0()) / 2);
		// Adjust the y value so that we move the character down enough to align its baseline
		y += fontTexture.getAscent() + quad.y0();
		addQuad(x, y, quad, fc);
	}

	// draw centered glyph with background color
	@Override
	public void drawTile(int glyph, int x, int y, Color fc, Color bc) {
		int w = Config.Font.tileWidth;
		int h = Config.Font.tileHeight;
		// add step for x and y
		x *= Config.Font.tileWidth;
		y *= Config.Font.tileHeight;

		// draw background rect
		addSolidQuad(x, y, w, h, bc);

		STBTTAlignedQuad quad = fontTexture.glyphInfo(glyph);
		// center glyph
		float charWidth = quad.x1() - quad.x0();
		float charHeight = quad.y1() - quad.y0();
		x += (Config.Font.tileWidth / 2) - (charWidth / 2);
		// subtract half the ascent to compensate for centering
		y += (Config.Font.tileHeight / 2) - (charHeight / 2) - ((fontTexture.getAscent() + quad.y0()) / 2);
		// Adjust the y value so that we move the character down enough to align its baseline
		y += fontTexture.getAscent() + quad.y0();
		addQuad(x, y, quad, fc);
	}

	private void addQuad(int x, int y, STBTTAlignedQuad quad, Color color) {
		addQuad(x, y, quad.x1() - quad.x0(), quad.y1() - quad.y0(), color, quad.s0(), quad.t0(), quad.s1(), quad.t1());
	}
}
