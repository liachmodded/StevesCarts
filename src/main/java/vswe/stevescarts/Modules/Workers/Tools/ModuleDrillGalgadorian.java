package vswe.stevescarts.Modules.Workers.Tools;

import net.minecraft.item.ItemStack;
import vswe.stevescarts.Carts.MinecartModular;

public class ModuleDrillGalgadorian extends ModuleDrill {
	public ModuleDrillGalgadorian(final MinecartModular cart) {
		super(cart);
	}

	@Override
	protected int blocksOnTop() {
		return 9;
	}

	@Override
	protected int blocksOnSide() {
		return 4;
	}

	@Override
	protected float getTimeMult() {
		return 0.0f;
	}

	@Override
	public int getMaxDurability() {
		return 1;
	}

	@Override
	public String getRepairItemName() {
		return null;
	}

	@Override
	public int getRepairItemUnits(final ItemStack item) {
		return 0;
	}

	@Override
	public boolean useDurability() {
		return false;
	}

	@Override
	public int getRepairSpeed() {
		return 1;
	}
}