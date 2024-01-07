package prulde.lwjgl;

import prulde.core.Config;
import prulde.render.Color;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;

public final class LwjglImageRenderer extends LwjglRenderer {
	private final LwjglImageTexture texture = new LwjglImageTexture();

	public LwjglImageRenderer() {
		super();
		texture.loadFromPng(Config.Tileset.tilesetPath);
		glBindTexture(GL_TEXTURE_2D, texture.getTextureId());
	}

	@Override
	public void drawTile(int glyph, int x, int y, Color fc, Color bc) {
		// apply scaling
		int w = Config.Tileset.tileWidth * Config.Tileset.widthScaling;
		int h = Config.Tileset.tileHeight * Config.Tileset.heightScaling;
		x *= Config.Tileset.tileWidth * Config.Tileset.widthScaling;
		y *= Config.Tileset.tileHeight * Config.Tileset.heightScaling;

		addSolidQuad(x, y, w, h, bc);
		addQuad(glyph, x, y, w, h, fc);
	}

	@Override
	public void drawTile(int glyph, int x, int y, Color fc) {
		if (Config.currentRenderer == Config.Renderer.TILESET_RENDERER) {
			// apply scaling
			x *= Config.Tileset.tileWidth * Config.Tileset.widthScaling;
			y *= Config.Tileset.tileHeight * Config.Tileset.heightScaling;
			int w = Config.Tileset.tileWidth * Config.Tileset.widthScaling;
			int h = Config.Tileset.tileHeight * Config.Tileset.heightScaling;

			addQuad(glyph, x, y, w, h, fc);
		}
	}

	private void addQuad(int tpos, int x, int y, int w, int h, Color color) {
		// get top left x,y coord of the tile
		//   s---*
		//   | / |
		//   *---*
		int sx = texture.getSx(tpos);
		int sy = texture.getSy(tpos);

		float u0 = (sx / (float) texture.getWidth());
		float v0 = (sy / (float) texture.getHeight());
		float u1 = ((sx + Config.Tileset.tileWidth) / (float) texture.getWidth());
		float v1 = ((sy + Config.Tileset.tileHeight) / (float) texture.getHeight());
		addQuad(x, y, w, h, color, u0, v0, u1, v1);
	}
}
