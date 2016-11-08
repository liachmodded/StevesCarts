package vswe.stevescarts.Arcade;

public class Utility extends Property {
	private int utilId;

	public Utility(final ArcadeMonopoly game, final PropertyGroup group, final int utilId, final String name) {
		super(game, group, name, 150);
		this.utilId = utilId;
	}

	@Override
	protected int getTextureId() {
		return 6 + this.utilId;
	}

	@Override
	protected int getTextY() {
		return 10;
	}

	@Override
	public int getRentCost() {
		return this.getRentCost(this.getOwnedInGroup());
	}

	public int getId() {
		return this.utilId;
	}

	public int getRentCost(final int owned) {
		return this.game.getTotalDieEyes() * getMultiplier(owned);
	}

	public static int getMultiplier(final int i) {
		switch (i) {
			default: {
				return 0;
			}
			case 1: {
				return 6;
			}
			case 2: {
				return 15;
			}
			case 3: {
				return 50;
			}
		}
	}
}