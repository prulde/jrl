package prulde.core;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public final class Config {
	public static final class Window {
		public static int width = 800;
		public static int height = 600;
		public static boolean vsync = true;
		public static boolean resizable = true;
		public static boolean scaleContentToWindow = false;
	}

	public static final class Font {
		public static String fontPath = "/fonts/Deferral-Square.ttf";//FiraCode-Regular.ttf
		public static float fontSize = 20;
		public static int cellWidth = 20;
		public static int cellHeight = 20;
	}

	private static final String dataPath = System.getProperty("user.dir") + "/data";

	public static String getFilePath(String path) {
		return dataPath + path;
	}
}
