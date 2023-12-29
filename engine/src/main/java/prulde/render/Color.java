package prulde.render;

public class Color {
	public float r, g, b, a;

	public static Color clean = new Color(0.0f, 0.0f, 0.0f, 1.0f);
	public static Color red = new Color(0.8f, 0.0f, 0.0f, 1.0f);
	public static Color green = new Color(0.0f, 0.8f, 0.0f, 1.0f);
	public static Color blue = new Color(0.0f, 0.0f, 0.8f, 1.0f);

	public Color(float r, float g, float b, float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}
}
