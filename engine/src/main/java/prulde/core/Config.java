package prulde.core;

public class Config {
	public static boolean logPerformance = true;
	public static int currentRenderer = Renderer.TILESET_RENDERER;

	public static final class Window {
		public static int width = 800;
		public static int height = 600;
		public static boolean vsync = true;
		public static boolean resizable = true;
		public static boolean scaleContentToWindow = true;
	}

	public static final class Renderer {
		public static final int TTF_RENDERER = 0;
		public static final int TILESET_RENDERER = 1;
	}

	public static final class Font {
		public static String fontPath = "/fonts/Deferral-Square.ttf";//FiraCode-Regular.ttf //PerfectDOSVGA437.ttf
		public static float fontSize = 20;
		public static int tileWidth = 20;
		public static int tileHeight = 20;
	}

	public static final class Tileset {
		public static String tilesetPath = "/tilesets/cp437_12x12.png";
		public static int tileWidth = 12;
		public static int tileHeight = 12;

		// 1 means no scaling
		public static int widthScaling = 2;
		public static int heightScaling = 2;
	}

	private static final String dataPath = System.getProperty("user.dir") + "/data";

	public static String getFilePath(String path) {
		return dataPath + path;
	}
}
