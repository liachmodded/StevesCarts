package vswe.stevescarts.compat.forestry;

import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.ITreeRoot;
import forestry.api.genetics.AlleleManager;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import vswe.stevescarts.api.farms.ITreeModule;

/**
 * Created by modmuss50 on 15/11/16.
 */
public class ForestryTreeModule implements ITreeModule {

	public ITreeRoot treeRoot;
	public Item sapling;
	public Block leaves;

	public ForestryTreeModule() {
		treeRoot = (ITreeRoot) AlleleManager.alleleRegistry.getSpeciesRoot("rootTrees");
		sapling = Item.REGISTRY.getObject(new ResourceLocation("forestry", "sapling"));
		leaves = Block.REGISTRY.getObject(new ResourceLocation("forestry", "leaves"));
	}

	@Override
	public boolean isLeaves(IBlockState blockState, BlockPos pos, EntityMinecart cart) {
		return blockState.getBlock() == leaves;
	}

	@Override
	public boolean isWood(IBlockState blockState, BlockPos pos, EntityMinecart cart) {
		return blockState.getBlock().getRegistryName().getResourceDomain().equals("forestry") && blockState.getBlock().getRegistryName().getResourcePath().startsWith("logs");
	}

	@Override
	public boolean isSapling(ItemStack itemStack) {
		return itemStack.getItem() == sapling;
	}

	@Override
	public boolean plantSapling(World world, BlockPos pos, ItemStack stack, FakePlayer fakePlayer) {
		if(stack.getItem() == sapling && world.isAirBlock(pos.up())){
			ITree tree = treeRoot.getMember(stack);
			if (tree != null && tree.canStay(world, pos)) {
				stack.stackSize--;
				treeRoot.plantSapling(world, tree, fakePlayer.getGameProfile(), pos.up());
				return true;
			}
		}
		return false;
	}
}
