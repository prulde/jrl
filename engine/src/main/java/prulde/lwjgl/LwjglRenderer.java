package prulde.lwjgl;

import lombok.extern.log4j.Log4j2;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.stb.STBTTAlignedQuad;
import prulde.core.Config;
import prulde.core.Injector;
import prulde.render.Color;
import prulde.render.Renderer;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_SCISSOR_TEST;

@Log4j2
public abstract class LwjglRenderer implements Renderer {
	private final LwjglWindow window = Injector.provideWindow();
	private final FloatBuffer fb = BufferUtils.createFloatBuffer(16);
	private final Matrix4f matrix = new Matrix4f();
	private final Matrix4f dummyMatrix = new Matrix4f();

	// data to draw textured triangles
	private int tris = 0;
	private final int maxTris = 65536; // THIS CAN BE HIGHER IF NEEDED
	private final float[] verts_pos = new float[3 * maxTris * 3];
	private final float[] verts_col = new float[3 * maxTris * 4];
	private final float[] verts_uv = new float[3 * maxTris * 3];

	// data for drawing solid triangles
	private int solidTris = 0;
	private final int maxSolidTris = 65536; // THIS CAN BE HIGHER IF NEEDED
	private final float[] solid_verts_pos = new float[3 * maxSolidTris * 3];
	private final float[] solid_verts_col = new float[3 * maxSolidTris * 4];

	public LwjglRenderer() {
		GL.createCapabilities();
		GLUtil.setupDebugMessageCallback();

		//glOrtho(0, Config.Window.width, Config.Window.height, 0, -1, 1);
		glViewport(0, 0, Config.Window.width, Config.Window.height);
		glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		//The 2d stuff we're doing doesn't need depth, or culling of faces.
		glDisable(GL_DEPTH_TEST);
		glDisable(GL_CULL_FACE);

		// We use scissors to handle view clipping
		glEnable(GL_SCISSOR_TEST);
		//Flush a blank image to the screen quickly.
		window.swapBuffers();
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

		// render solid rectangles before texture binding
		renderSolidBuffer();

		// enable texture and draw the main buffer
		glEnable(GL_TEXTURE_2D);
		renderBuffer();

		glFinish();
		glDisable(GL_TEXTURE_2D);

		window.swapBuffers();
	}
	
	@Override
	public void drawTile(int glyph, int x, int y, Color fc, Color bc) {
	}

	@Override
	public void drawTile(int glyph, int x, int y, Color fc) {
	}

	@Override
	public void drawRect(int x, int y, int w, int h, Color c) {
		addSolidQuad(x, y, w, h, c);
	}

	private void renderSolidBuffer() {
		if (solidTris == 0) return;

		FloatBuffer v = BufferUtils.createFloatBuffer(solidTris * 3 * 3);
		FloatBuffer c = BufferUtils.createFloatBuffer(solidTris * 3 * 4);

		v.put(solid_verts_pos, 0, v.capacity());
		c.put(solid_verts_col, 0, c.capacity());

		v.flip();
		c.flip();

		glEnableClientState(GL_VERTEX_ARRAY);
		glEnableClientState(GL_COLOR_ARRAY);

		glVertexPointer(3, GL_FLOAT, 0, v);
		glColorPointer(4, GL_FLOAT, 0, c);

		glDrawArrays(GL_TRIANGLES, 0, solidTris * 3);
		solidTris = 0;
	}

	private void renderBuffer() {
		if (tris == 0) return;

		FloatBuffer v = BufferUtils.createFloatBuffer(tris * 3 * 3);
		FloatBuffer c = BufferUtils.createFloatBuffer(tris * 3 * 4);
		FloatBuffer u = BufferUtils.createFloatBuffer(tris * 3 * 2);

		v.put(verts_pos, 0, v.capacity());
		c.put(verts_col, 0, c.capacity());
		u.put(verts_uv, 0, u.capacity());

		v.flip();
		c.flip();
		u.flip();

		glEnableClientState(GL_VERTEX_ARRAY);
		glEnableClientState(GL_COLOR_ARRAY);
		glEnableClientState(GL_TEXTURE_COORD_ARRAY);

		glVertexPointer(3, GL_FLOAT, 0, v);
		glColorPointer(4, GL_FLOAT, 0, c);
		glTexCoordPointer(2, GL_FLOAT, 0, u);

		glDrawArrays(GL_TRIANGLES, 0, tris * 3);
		tris = 0;
	}

	// for textured tile or font glyph
	protected void addQuad(float x, float y, float w, float h, Color color, float u0, float v0, float u1, float v1) {
		//   1---2
		//   | / |
		//   3---4
		if (tris + 2 >= maxTris) {
			log.info("Can not draw more than: " + maxTris + " triangles");
			return;
		}

		// Current indices for our data
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

		//texture UVs, tri 0
		//v1
		verts_uv[iu++] = u0;
		verts_uv[iu++] = v0;
		//v2
		verts_uv[iu++] = u1;
		verts_uv[iu++] = v0;
		//v3
		verts_uv[iu++] = u0;
		verts_uv[iu++] = v1;
		//UVs, tri 1
		//v2
		verts_uv[iu++] = u1;
		verts_uv[iu++] = v0;
		//v4
		verts_uv[iu++] = u1;
		verts_uv[iu++] = v1;
		//v3
		verts_uv[iu++] = u0;
		verts_uv[iu++] = v1;

		tris += 2;
	}

	// for solid quads
	protected void addSolidQuad(float x, float y, float w, float h, Color color) {
		//   1---2
		//   | / |
		//   3---4
		if (solidTris + 2 >= maxSolidTris) {
			log.info("Can not draw more than: " + maxSolidTris + " solid triangles");
			return;
		}

		// Current indices for our data
		int ip = 3 * 3 * solidTris;
		int ic = 4 * 3 * solidTris;

		//pos, tri0
		//v1
		solid_verts_pos[ip++] = x;
		solid_verts_pos[ip++] = y;
		solid_verts_pos[ip++] = 0;
		//v2
		solid_verts_pos[ip++] = x + w;
		solid_verts_pos[ip++] = y;
		solid_verts_pos[ip++] = 0;
		//v3
		solid_verts_pos[ip++] = x;
		solid_verts_pos[ip++] = y + h;
		solid_verts_pos[ip++] = 0;
		//pos, tri1
		//v2
		solid_verts_pos[ip++] = x + w;
		solid_verts_pos[ip++] = y;
		solid_verts_pos[ip++] = 0;
		//v4
		solid_verts_pos[ip++] = x + w;
		solid_verts_pos[ip++] = y + h;
		solid_verts_pos[ip++] = 0;
		//v3
		solid_verts_pos[ip++] = x;
		solid_verts_pos[ip++] = y + h;
		solid_verts_pos[ip++] = 0;

		//cols;
		int i;
		for (i = 0; i < 3 * 2; i++) {
			solid_verts_col[ic++] = color.r;
			solid_verts_col[ic++] = color.g;
			solid_verts_col[ic++] = color.b;
			solid_verts_col[ic++] = color.a;
		}

		solidTris += 2;
	}

	private void setMatrixOrtho2D() {
		if (Config.Window.scaleContentToWindow) {
			matrix.setOrtho2D(0, Config.Window.width, Config.Window.height, 0);
		} else {
			matrix.setOrtho2D(0, Config.Window.width * window.getHorizontalScaleFactor(), Config.Window.height * window.getVerticalScaleFactor(), 0);
		}
	}
}
