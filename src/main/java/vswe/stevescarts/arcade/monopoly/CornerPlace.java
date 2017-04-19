package vswe.stevescarts.arcade.monopoly;

import vswe.stevescarts.guis.GuiMinecart;

import java.util.EnumSet;

public class CornerPlace extends Place {
	private int texture;

	public CornerPlace(final ArcadeMonopoly game, final int texture) {
		super(game);
		this.texture = texture;
	}

	@Override
	public void draw(final GuiMinecart gui, final EnumSet<PLACE_STATE> states) {
		game.loadTexture(gui, 2);
		applyColorFilter(gui, states);
		game.getModule().drawImage(gui, 0, 0, 122 * (texture % 2), 122 * (texture / 2), 122, 122);
	}
}
