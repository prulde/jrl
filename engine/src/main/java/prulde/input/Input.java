package prulde.input;

public interface Input {
	class Keys {
		public static final int NO_INPUT = -1;
		public static final int ARROW_LEFT = 1;
		public static final int ARROW_UP = 2;
		public static final int ARROW_RIGHT = 3;
		public static final int ARROW_DOWN = 4;

		public static String rawTyped(int k) {
			return switch (k) {
				case ARROW_LEFT -> "ArrowLeft";
				case ARROW_UP -> "ArrowUp";
				case ARROW_RIGHT -> "ArrowRight";
				case ARROW_DOWN -> "ArrowDown";
				default -> "NoInput";
			};
		}
	}
}