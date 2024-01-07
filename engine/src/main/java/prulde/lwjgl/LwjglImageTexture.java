package prulde.lwjgl;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;
import prulde.core.Config;
import prulde.core.Injector;
import prulde.util.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static java.lang.Math.round;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.stb.STBImageResize.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.memAlloc;
import static org.lwjgl.system.MemoryUtil.memFree;

@Log4j2
public final class LwjglImageTexture {
	private ByteBuffer image;
	@Getter
	private int width;
	@Getter
	private int height;
	private int comp;
	@Getter
	private int textureId;
	@Getter
	private int rows;
	@Getter
	private int cols;
	private int[] sx;
	private int[] sy;

	public void loadFromPng(String path) {
		ByteBuffer imageBuffer;
		try {
			imageBuffer = FileUtils.readFile(path);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		log.info("Loaded image: " + path);
		getImageData(imageBuffer);
		generateTexture();
	}

	public int getSx(int pos) {
		return sx[pos];
	}

	public int getSy(int pos) {
		return sy[pos];
	}

	private void generateTexture() {
		textureId = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, textureId);

		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image);

		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST_MIPMAP_NEAREST);

		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

		glGenerateMipmap(GL_TEXTURE_2D);

		int format;
		if (comp == 3) {
			if ((width & 3) != 0) {
				glPixelStorei(GL_UNPACK_ALIGNMENT, 2 - (width & 1));
			}
			format = GL_RGB;
		} else {
			premultiplyAlpha();

			glEnable(GL_BLEND);
			glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

			format = GL_RGBA;
		}

		ByteBuffer input_pixels = image;
		int input_w = width;
		int input_h = height;
		int mipmapLevel = 0;
		while (1 < input_w || 1 < input_h) {
			int output_w = Math.max(1, input_w >> 1);
			int output_h = Math.max(1, input_h >> 1);

			ByteBuffer output_pixels = memAlloc(output_w * output_h * comp);
			stbir_resize_uint8_generic(
					input_pixels, input_w, input_h, input_w * comp,
					output_pixels, output_w, output_h, output_w * comp,
					comp, comp == 4 ? 3 : STBIR_ALPHA_CHANNEL_NONE, STBIR_FLAG_ALPHA_PREMULTIPLIED,
					STBIR_EDGE_CLAMP,
					STBIR_FILTER_MITCHELL,
					STBIR_COLORSPACE_SRGB
			);

			if (mipmapLevel == 0) {
				stbi_image_free(image);
			} else {
				memFree(input_pixels);
			}

			glTexImage2D(GL_TEXTURE_2D, ++mipmapLevel, format, output_w, output_h, 0, format, GL_UNSIGNED_BYTE, output_pixels);

			input_pixels = output_pixels;
			input_w = output_w;
			input_h = output_h;
		}
		if (mipmapLevel == 0) {
			stbi_image_free(image);
		} else {
			memFree(input_pixels);
		}
	}

	private void premultiplyAlpha() {
		int stride = width * 4;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int i = y * stride + x * 4;

				float alpha = (image.get(i + 3) & 0xFF) / 255.0f;
				image.put(i + 0, (byte) round(((image.get(i + 0) & 0xFF) * alpha)));
				image.put(i + 1, (byte) round(((image.get(i + 1) & 0xFF) * alpha)));
				image.put(i + 2, (byte) round(((image.get(i + 2) & 0xFF) * alpha)));
			}
		}
	}

	private void getImageData(ByteBuffer imageBuffer) {
		try (MemoryStack stack = stackPush()) {
			IntBuffer w = stack.mallocInt(1);
			IntBuffer h = stack.mallocInt(1);
			IntBuffer c = stack.mallocInt(1);

			// Use info to read image metadata without decoding the entire image.
			// We don't need this for this demo, just testing the API.
			if (!stbi_info_from_memory(imageBuffer, w, h, c)) {
				throw new RuntimeException("Failed to read image information: " + stbi_failure_reason());
			} else {
				log.info("OK with reason: " + stbi_failure_reason());
			}

			log.info("Image width: " + w.get(0));
			log.info("Image height: " + h.get(0));
			log.info("Image components: " + c.get(0));
			log.info("Image HDR: " + stbi_is_hdr_from_memory(imageBuffer));

			// Decode the image
			image = stbi_load_from_memory(imageBuffer, w, h, c, 0);
			if (image == null) {
				throw new RuntimeException("Failed to load image: " + stbi_failure_reason());
			}

			width = w.get(0);
			height = h.get(0);
			comp = c.get(0);

			cols = width / Config.Tileset.tileWidth;
			rows = height / Config.Tileset.tileHeight;

			sx = new int[cols * rows];
			sy = new int[cols * rows];

			// calculate and cache x, y coordinates of the top left corner for each tile in tileset
			for (int i = 0; i < rows * cols; i++) {
				sx[i] = (i % cols) * Config.Tileset.tileWidth;
				sy[i] = (i / rows) * Config.Tileset.tileHeight;
			}
		}
	}
}
