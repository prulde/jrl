package prulde.lwjgl;

import lombok.extern.log4j.Log4j2;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glLoadMatrixf;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.stb.STBTTAlignedQuad;
import prulde.core.Config;
import prulde.core.Injector;
import prulde.render.Color;
import prulde.render.Renderer;

import javax.inject.Inject;
import java.nio.FloatBuffer;

@Log4j2
public class LwjglRendererOGL implements Renderer {
	@Inject
	LwjglWindow window;
	private final LwjglFontTexture fontTexture = new LwjglFontTexture();
	private final FloatBuffer fb = BufferUtils.createFloatBuffer(16);
	private final Matrix4f matrix = new Matrix4f();
	private final Matrix4f dummyMatrix = new Matrix4f();
	private int tris = 0;
	private int maxTris = 65536; // THIS CAN BE HIGHER IF NEEDED
	private final float[] verts_pos = new float[3 * maxTris * 3];
	private final float[] verts_col = new float[3 * maxTris * 4];
	private final float[] verts_uv = new float[3 * maxTris * 3];

	public LwjglRendererOGL() {
		Injector.getEngineComponent().inject(this);
	}

	public void init() {
		GL.createCapabilities();
		GLUtil.setupDebugMessageCallback();

		//Lets quickly blank the screen.
		glViewport(0, 0, Config.Window.width, Config.Window.height);
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glEnable(GL_TEXTURE_2D);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		//The 2d stuff we're doing doesn't need depth, or culling of faces.
		glDisable(GL_DEPTH_TEST);
		glDisable(GL_CULL_FACE);

		// We use scissors to handle view clipping
		glEnable(GL_SCISSOR_TEST);
		//Flush a blank image to the screen quickly.
		window.swapBuffers();

		fontTexture.loadFromTTF();
	}

	@Override
	public void changeClearColor(Color c) {
		glClearColor(c.r, c.g, c.b, c.a);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}

	@Override
	public void render() {
		// Make sure to set our clip rect to the current screen size.  It might have changed since the last frame.
		glScissor(0, 0, Config.Window.width, Config.Window.height);

		this.setMatrixOrtho2D();

		glViewport(0, 0, Config.Window.width, Config.Window.height);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		glLoadIdentity();

		glMatrixMode(GL_PROJECTION);
		glLoadMatrixf(matrix.get(fb));

		glMatrixMode(GL_MODELVIEW);
		glLoadMatrixf(dummyMatrix.get(fb));
		//
		renderBuffer(); // Make sure we draw anything left in the buffer
		glFinish();
		window.swapBuffers();
	}

	// free placement with height adjustment
	@Override
	public void drawGlyph(int glyph, int x, int y, Color c) {
		STBTTAlignedQuad quad = fontTexture.glyphInfo(glyph);
		// Adjust the y value so that we move the character down enough to align its baseline
		y += fontTexture.getAscent() + quad.y0();
		addQuad(x * (int) Config.Font.cellWidth, y * (int) Config.Font.cellHeight, quad, c);
	}

	// draw centered glyph with background color
	@Override
	public void drawCell(int glyph, int x, int y, Color fgc, Color bgc) {
		// add step for x and y
		x *= (int) Config.Font.fontSize;
		y *= (int) Config.Font.fontSize;
		// draw background rect
		drawRect(x, y, (int) Config.Font.fontSize, (int) Config.Font.fontSize, bgc);
		STBTTAlignedQuad quad = fontTexture.glyphInfo(glyph);
		// center glyph
		float charWidth = quad.x1() - quad.x0();
		float charHeight = quad.y1() - quad.y0();
		x += (Config.Font.fontSize / 2) - (charWidth / 2);
		y += (Config.Font.fontSize / 2) - (charHeight / 2) - ((fontTexture.getAscent() + quad.y0()) / 2); // subtract half the ascent to compensate for centering
		// Adjust the y value so that we move the character down enough to align its baseline
		y += fontTexture.getAscent() + quad.y0();
		addQuad(x, y, quad, fgc);
	}

	// draw centered glyph
	@Override
	public void drawCell(int glyph, int x, int y, Color c) {
		// add step for x and y
		x *= (int) Config.Font.fontSize;
		y *= (int) Config.Font.fontSize;
		STBTTAlignedQuad quad = fontTexture.glyphInfo(glyph);
		// center glyph
		float charWidth = quad.x1() - quad.x0();
		float charHeight = quad.y1() - quad.y0();
		x += (Config.Font.fontSize / 2) - (charWidth / 2);
		y += (Config.Font.fontSize / 2) - (charHeight / 2) - ((fontTexture.getAscent() + quad.y0()) / 2); // subtract half the ascent to compensate for centering
		// Adjust the y value so that we move the character down enough to align its baseline
		y += fontTexture.getAscent() + quad.y0();
		addQuad(x, y, quad, c);
	}

	@Override
	public void drawRect(int x, int y, int w, int h, Color c) {
		addQuad(x, y, w, h, c);
	}

	private void renderBuffer() {
		if (tris == 0) return;

		FloatBuffer v = BufferUtils.createFloatBuffer(tris * 3 * 3);
		FloatBuffer c = BufferUtils.createFloatBuffer(tris * 3 * 4);
		FloatBuffer u = BufferUtils.createFloatBuffer(tris * 3 * 2);

		v.put(verts_pos, 0, v.capacity());
		c.put(verts_col, 0, c.capacity());
		u.put(verts_uv, 0, u.capacity());

		// read
		v.flip();
		c.flip();
		u.flip();

		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
		GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);

		GL11.glVertexPointer(3, GL_FLOAT, 0, v);
		GL11.glColorPointer(4, GL_FLOAT, 0, c);
		GL11.glTexCoordPointer(2, GL_FLOAT, 0, u);

		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, tris * 3);
		tris = 0;
	}

	// for drawRect
	private void addQuad(int x, int y, int w, int h, Color color) {
		LwjglFontTexture.SolidColorData d = fontTexture.getSolidColorData();
		addQuad(x, y, w, h, color, d.u, d.v, d.uw, d.vh);
	}

	// for glyph
	private void addQuad(int x, int y, STBTTAlignedQuad quad, Color color) {
		addQuad(x, y, quad.x1() - quad.x0(), quad.y1() - quad.y0(), color, quad.s0(), quad.t0(), quad.s1() - quad.s0(), quad.t1() - quad.t0());
	}

	private void addQuad(float x, float y, float w, float h, Color color, float u, float v, float uw, float vh) {
		//   1---2
		//   | / |
		//   3---4
		if (tris + 2 >= maxTris) return; // Should probably dump a message here, we're all full up!

		// Current indices for our data;
		int ip = 3 * 3 * tris;
		int ic = 4 * 3 * tris;
		int iu = 2 * 3 * tris;


		//pos, tri0
		//v1
		verts_pos[ip++] = x;
		verts_pos[ip++] = y;
		verts_pos[ip++] = 0;
		//v2
		verts_pos[ip++] = x + w;
		verts_pos[ip++] = y;
		verts_pos[ip++] = 0;
		//v3
		verts_pos[ip++] = x;
		verts_pos[ip++] = y + h;
		verts_pos[ip++] = 0;
		//pos, tri1
		//v2
		verts_pos[ip++] = x + w;
		verts_pos[ip++] = y;
		verts_pos[ip++] = 0;
		//v4
		verts_pos[ip++] = x + w;
		verts_pos[ip++] = y + h;
		verts_pos[ip++] = 0;
		//v3
		verts_pos[ip++] = x;
		verts_pos[ip++] = y + h;
		verts_pos[ip++] = 0;

		//cols;
		int i;
		for (i = 0; i < 3 * 2; i++) {
			verts_col[ic++] = color.r;
			verts_col[ic++] = color.g;
			verts_col[ic++] = color.b;
			verts_col[ic++] = color.a;
		}

		//UVs, tri 0
		//v1
		verts_uv[iu++] = u;
		verts_uv[iu++] = v;
		//v2
		verts_uv[iu++] = u + uw;
		verts_uv[iu++] = v;
		//v3
		verts_uv[iu++] = u;
		verts_uv[iu++] = v + vh;
		//UVs, tri 1
		//v2
		verts_uv[iu++] = u + uw;
		verts_uv[iu++] = v;
		//v4
		verts_uv[iu++] = u + uw;
		verts_uv[iu++] = v + vh;
		//v3
		verts_uv[iu++] = u;
		verts_uv[iu++] = v + vh;

		tris += 2;
	}

	private void setMatrixOrtho2D() {
		if (Config.Window.scaleContentToWindow) {
			matrix.setOrtho2D(0, Config.Window.width, Config.Window.height, 0);
		} else {
			matrix.setOrtho2D(0, Config.Window.width * window.getHorizontalScaleFactor(), Config.Window.height * window.getVerticalScaleFactor(), 0);
		}
	}
}
