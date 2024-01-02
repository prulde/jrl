package prulde.core;

public class Config {

	public static boolean logPerformance = true;

	public static final class Window {
		public static int width = 800;
		public static int height = 600;
		public static boolean vsync = true;
		public static boolean resizable = true;
		public static boolean scaleContentToWindow = true;
	}

	public static final class Font {
		public static String fontPath = "/fonts/Deferral-Square.ttf";//FiraCode-Regular.ttf //PerfectDOSVGA437.ttf
		public static float fontSize = 20;
		public static int tileWidth = 20;
		public static int tileHeight = 20;
	}

	private static final String dataPath = System.getProperty("user.dir") + "/data";

	public static String getFilePath(String path) {
		return dataPath + path;
	}
}
