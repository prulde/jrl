package prulde.util;

import org.apache.commons.io.IOUtils;
import org.lwjgl.BufferUtils;
import prulde.core.Config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class FileUtils {
	public static ByteBuffer readFile(String filePath) throws IOException {
		File file = new File(Config.getFilePath(filePath));
		InputStream is = new FileInputStream(file);
		byte[] bytes = IOUtils.toByteArray(is);
		ByteBuffer buffer = BufferUtils.createByteBuffer(bytes.length + 1);
		buffer.put(bytes);
		buffer.flip();
		return buffer;
	}
}
