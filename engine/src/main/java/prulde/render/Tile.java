package prulde.render;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Tile {
	private char glyph;
	private Color fgc;
	private Color bgc;
}
