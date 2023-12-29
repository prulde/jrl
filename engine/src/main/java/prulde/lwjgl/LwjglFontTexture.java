package prulde.lwjgl;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;
import prulde.core.Config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBTruetype.*;
import static org.lwjgl.system.MemoryStack.stackPush;

@Log4j2
public class LwjglFontTexture {
	// These numbers are somewhat arbitrary.  They should work for a 16 point font
	// with less than 1000 glyphs.  If you are using larger or more populated fonts
	// you may need to make more room in the bitmap for them.
	private int bitmapWidth = 512;
	private int bitmapHeight = 512;

	// The number of glyphs is set this high to capture all possible unicode values.
	// This will use a lot of memory, so if you are constrained you may want to limit
	// it to the first 128 glyphs.
	private int numberOfGlyphs = 65536;

	// Vertical stats for the loaded font
	@Getter
	private int ascent;
	@Getter
	private int descent;
	@Getter
	private int lineGap;

	private int textureId;

	// These will be filled with the most recently looked up glyph's width and height, in pixels
	private final float[] glyphWidth = new float[1];
	private final float[] glyphHeight = new float[1];

	@Getter
	private final SolidColorData solidColorData = new SolidColorData();

	// This buffer holds the info for each glyph in the font
	private final STBTTBakedChar.Buffer glyphData = STBTTBakedChar.malloc(numberOfGlyphs);

	// This buffer holds the most recently looked up aligned quad.
	private final STBTTAlignedQuad.Buffer alignedQuad = STBTTAlignedQuad.malloc(1);

	public STBTTAlignedQuad glyphInfo(int c) {
		glyphWidth[0] = 0;
		glyphHeight[0] = 0;
		STBTruetype.stbtt_GetBakedQuad(glyphData, bitmapWidth, bitmapHeight, c - 32, glyphWidth, glyphHeight, alignedQuad.get(0), true);
		return alignedQuad.get(0);
	}

	public void loadFromTTF() {
		ByteBuffer ttfData;
		try {
			ttfData = readFile(Config.Font.fontPath);
		} catch (IOException e) {
			throw new RuntimeException("Can't load font resource at path " + Config.Font.fontPath, e);
		}
		generateTexture(ttfData);
		log.info("Loaded font: " + Config.Font.fontPath);
	}

	private void generateTexture(ByteBuffer ttfData) {
		readFontInfo(ttfData);
		ByteBuffer fontBitmap = BufferUtils.createByteBuffer(bitmapWidth * bitmapHeight);
		STBTruetype.stbtt_BakeFontBitmap(ttfData, Config.Font.fontSize, fontBitmap, bitmapWidth, bitmapHeight, 32, glyphData);
		// Add a white pixel in the bottom right corner of our bitmap to use as the texture for drawRect
		fontBitmap.put(bitmapWidth * bitmapHeight - 1, (byte) 0xFF);

		// can free the ttfData now
		textureId = glGenTextures();
		stbtt_FreeBitmap(ttfData);
		glBindTexture(GL_TEXTURE_2D, textureId);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_ALPHA, bitmapWidth, bitmapHeight, 0, GL_ALPHA, GL_UNSIGNED_BYTE, fontBitmap);
		// can free fontBitmap now
		stbtt_FreeBitmap(fontBitmap);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
	}

	private ByteBuffer readFile(String filePath) throws IOException {
		File file = new File(Config.getFilePath(filePath));
		InputStream is = new FileInputStream(file);
		byte[] bytes = IOUtils.toByteArray(is);
		ByteBuffer buffer = BufferUtils.createByteBuffer(bytes.length + 1);
		buffer.put(bytes);
		buffer.flip();
		return buffer;
	}

	private void readFontInfo(ByteBuffer ttfData) {
		STBTTFontinfo fontInfo = STBTTFontinfo.create();
		if (!stbtt_InitFont(fontInfo, ttfData)) {
			throw new IllegalStateException("Failed to initialize font information.");
		}

		try (MemoryStack stack = stackPush()) {
			IntBuffer pAscent = stack.mallocInt(1);
			IntBuffer pDescent = stack.mallocInt(1);
			IntBuffer pLineGap = stack.mallocInt(1);

			stbtt_GetFontVMetrics(fontInfo, pAscent, pDescent, pLineGap);

			ascent = pAscent.get(0);
			descent = pDescent.get(0);
			lineGap = pLineGap.get(0);

			float scaleFactor = stbtt_ScaleForPixelHeight(fontInfo, Config.Font.fontSize);
			ascent *= scaleFactor;
			descent *= scaleFactor;
			lineGap *= scaleFactor;
		}
	}

	public class SolidColorData {
		float u, v, uw, vh;

		public SolidColorData() {
			u = (float) (bitmapWidth - 1) / (float) bitmapWidth;
			v = (float) (bitmapHeight - 1) / (float) bitmapHeight;
			uw = 1f - u;
			vh = 1f - v;
		}
	}
}
