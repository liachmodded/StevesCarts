package vswe.stevescarts.helpers;

import java.util.Collection;
import java.util.HashMap;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import vswe.stevescarts.blocks.tileentities.TileEntityDetector;
import vswe.stevescarts.entitys.EntityMinecartModular;

public class OperatorObject {
	private static HashMap<Byte, OperatorObject> allOperators;
	private byte ID;
	private Localization.GUI.DETECTOR name;
	private int childs;

	public static Collection<OperatorObject> getOperatorList(DetectorType type) {
		return type.getOperators().values();
	}

	public static HashMap<Byte, OperatorObject> getAllOperators() {
		return OperatorObject.allOperators;
	}

	public OperatorObject(final HashMap<Byte, OperatorObject> operators, final int ID, final Localization.GUI.DETECTOR name, final int childs) {
		this.ID = (byte) ID;
		this.name = name;
		this.childs = childs;
		operators.put(this.ID, this);
		OperatorObject.allOperators.put(this.ID, this);
	}

	public byte getID() {
		return this.ID;
	}

	public String getName() {
		return this.name.translate();
	}

	public int getChildCount() {
		return this.childs;
	}

	public boolean inTab() {
		return true;
	}

	public boolean isChildValid(final OperatorObject child) {
		return true;
	}

	public boolean evaluate(final TileEntityDetector detector, final EntityMinecartModular cart, final int depth, final LogicObject A, final LogicObject B) {
		return false;
	}

	static {
		OperatorObject.allOperators = new HashMap<>();
		final HashMap<Byte, OperatorObject> operators = new HashMap<>();
		new OperatorObject(operators, 0, Localization.GUI.DETECTOR.OUTPUT, 1) {
			@Override
			public boolean inTab() {
				return false;
			}

			@Override
			public boolean evaluate(final TileEntityDetector detector, final EntityMinecartModular cart, final int depth, final LogicObject A, final LogicObject B) {
				return A.evaluateLogicTree(detector, cart, depth);
			}
		};
		new OperatorObject(operators, 1, Localization.GUI.DETECTOR.AND, 2) {
			@Override
			public boolean evaluate(final TileEntityDetector detector, final EntityMinecartModular cart, final int depth, final LogicObject A, final LogicObject B) {
				return A.evaluateLogicTree(detector, cart, depth) && B.evaluateLogicTree(detector, cart, depth);
			}
		};
		new OperatorObject(operators, 2, Localization.GUI.DETECTOR.OR, 2) {
			@Override
			public boolean evaluate(final TileEntityDetector detector, final EntityMinecartModular cart, final int depth, final LogicObject A, final LogicObject B) {
				return A.evaluateLogicTree(detector, cart, depth) || B.evaluateLogicTree(detector, cart, depth);
			}
		};
		new OperatorObject(operators, 3, Localization.GUI.DETECTOR.NOT, 1) {
			@Override
			public boolean isChildValid(final OperatorObject child) {
				return this.getID() != child.ID;
			}

			@Override
			public boolean evaluate(final TileEntityDetector detector, final EntityMinecartModular cart, final int depth, final LogicObject A, final LogicObject B) {
				return !A.evaluateLogicTree(detector, cart, depth);
			}
		};
		new OperatorObject(operators, 4, Localization.GUI.DETECTOR.XOR, 2) {
			@Override
			public boolean evaluate(final TileEntityDetector detector, final EntityMinecartModular cart, final int depth, final LogicObject A, final LogicObject B) {
				return A.evaluateLogicTree(detector, cart, depth) != B.evaluateLogicTree(detector, cart, depth);
			}
		};
		new OperatorObjectRedirector(operators, 5, Localization.GUI.DETECTOR.TOP, 0, 1, 0);
		new OperatorObjectRedirector(operators, 6, Localization.GUI.DETECTOR.BOT, 0, -1, 0);
		new OperatorObjectRedirector(operators, 7, Localization.GUI.DETECTOR.NORTH, 0, 0, -1);
		new OperatorObjectRedirector(operators, 8, Localization.GUI.DETECTOR.WEST, -1, 0, 0);
		new OperatorObjectRedirector(operators, 9, Localization.GUI.DETECTOR.SOUTH, 0, 0, 1);
		new OperatorObjectRedirector(operators, 10, Localization.GUI.DETECTOR.EAST, 1, 0, 0);
		for (final DetectorType type : DetectorType.values()) {
			type.initOperators(new HashMap<>(operators));
		}
	}

	public static class OperatorObjectRedirector extends OperatorObject {
		private int x;
		private int y;
		private int z;

		public OperatorObjectRedirector(final HashMap<Byte, OperatorObject> operators, final int ID, final Localization.GUI.DETECTOR name, final int x, final int y, final int z) {
			super(operators, ID, name, 0);
			this.x = x;
			this.y = y;
			this.z = z;
		}

		@Override
		public boolean evaluate(final TileEntityDetector detector, final EntityMinecartModular cart, final int depth, final LogicObject A, final LogicObject B) {
			final int x = this.x + detector.getPos().getX();
			final int y = this.y + detector.getPos().getY();
			final int z = this.z + detector.getPos().getZ();
			final TileEntity tileentity = detector.getWorld().getTileEntity(new BlockPos(x, y, z));
			return tileentity != null && tileentity instanceof TileEntityDetector && ((TileEntityDetector) tileentity).evaluate(cart, depth);
		}
	}

	public static class OperatorObjectRedstone extends OperatorObject {
		private int x;
		private int y;
		private int z;

		public OperatorObjectRedstone(final HashMap<Byte, OperatorObject> operators, final int ID, final Localization.GUI.DETECTOR name, final int x, final int y, final int z) {
			super(operators, ID, name, 0);
			this.x = x;
			this.y = y;
			this.z = z;
		}

		@Override
		public boolean evaluate(final TileEntityDetector detector, final EntityMinecartModular cart, final int depth, final LogicObject A, final LogicObject B) {
			final int x = this.x + detector.getPos().getX();
			final int y = this.y + detector.getPos().getY();
			final int z = this.z + detector.getPos().getZ();
			if (this.x == 0 && this.y == 0 && this.z == 0) {
				return detector.getWorld().isBlockPowered(new BlockPos(x, y, z));
			}
			int direction; //TODO EnumFacing
			if (this.y > 0) {
				direction = 0;
			} else if (this.y < 0) {
				direction = 1;
			} else if (this.x > 0) {
				direction = 4;
			} else if (this.x < 0) {
				direction = 5;
			} else if (this.z > 0) {
				direction = 2;
			} else {
				direction = 3;
			}
			return detector.getWorld().getRedstonePower(new BlockPos(x, y, z), EnumFacing.getFront(direction)) > 0;
		}
	}
}
