package com.pinball3d.zone;

import java.util.function.Predicate;

import com.pinball3d.zone.item.ItemFluid;
import com.pinball3d.zone.item.ItemLoader;
import com.pinball3d.zone.item.ItemVanillaFluid;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.registries.IForgeRegistry;

public class FluidHandler {
	public static Item fluid, water, lava;

	public static void registerFluidItems(IForgeRegistry<Item> registry) {
//		Block.REGISTRY.forEach(e -> {
//			if (e instanceof IFluidBlock) {
//				Item item = new ItemFluid((IFluidBlock) e);
//				ResourceLocation name = item.getRegistryName();
//				if (!registry.containsKey(name)) {
//					ItemLoader.register(registry, item, false);
//					if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
//						ItemLoader.registerRender(item, new ModelResourceLocation("zone:fluid", null));
//					}
//				}
//			}
//		});
		ItemLoader.register(registry, fluid = new ItemFluid(), false);
		ItemLoader.register(registry, water = new ItemVanillaFluid(Blocks.FLOWING_WATER, "water"));
		ItemLoader.register(registry, lava = new ItemVanillaFluid(Blocks.FLOWING_LAVA, "lava") {
			@Override
			public int getItemBurnTime(ItemStack itemStack) {
				return 20000;
			}
		});
	}

	public static ItemStack getFluidFromBlock(Block block) {
		ItemStack stack = ItemStack.EMPTY;
		if ((block == Blocks.WATER || block == Blocks.FLOWING_WATER)) {
			stack = new ItemStack(water);
		} else if ((block == Blocks.LAVA || block == Blocks.FLOWING_LAVA)) {
			stack = new ItemStack(lava);
		}
		if (block instanceof IFluidBlock) {
			stack = ItemFluid.createStack(((IFluidBlock) block).getFluid());
		}
		return stack;
	}

	public static ItemStack tryDrainFluidFromWorld(World world, BlockPos pos, Predicate<ItemStack> doDrain) {
		ItemStack stack = ItemStack.EMPTY;
		IBlockState s = world.getBlockState(pos);
		if (!s.getMaterial().isLiquid()) {
			return stack;
		}
		Block block = s.getBlock();
		if ((block == Blocks.WATER || block == Blocks.FLOWING_WATER) && s.getValue(BlockLiquid.LEVEL).intValue() == 0) {
			stack = new ItemStack(water);
		} else if ((block == Blocks.LAVA || block == Blocks.FLOWING_LAVA)
				&& s.getValue(BlockLiquid.LEVEL).intValue() == 0) {
			stack = new ItemStack(lava);
		} else if (block instanceof IFluidBlock) {
			IFluidBlock f = (IFluidBlock) block;
			FluidStack fluidstack = f.drain(world, pos, false);
			if (fluidstack != null && fluidstack.amount >= 1000) {
				stack = new ItemStack(Item.getByNameOrId("zone:" + block.getRegistryName().getResourcePath()));
				if (!stack.isEmpty() && doDrain.test(stack)) {
					f.drain(world, pos, true);
				}
			}
		}
		return stack;
	}
}
