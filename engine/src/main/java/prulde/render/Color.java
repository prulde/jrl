package prulde.render;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Color {
	public final float r, g, b, a;

	// original tile color
	public static Color original = new Color(1.0f, 1.0f, 1.0f, 1.0f);
	public static Color clean = new Color(0.0f, 0.0f, 0.0f, 1.0f);
	public static Color red = new Color(0.8f, 0.0f, 0.0f, 1.0f);
	public static Color green = new Color(0.0f, 0.8f, 0.0f, 1.0f);
	public static Color blue = new Color(0.0f, 0.0f, 0.8f, 1.0f);
}