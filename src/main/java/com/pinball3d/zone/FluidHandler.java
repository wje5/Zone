package com.pinball3d.zone;

import java.util.function.Predicate;

import com.pinball3d.zone.item.ItemFluid;
import com.pinball3d.zone.item.ItemLoader;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.registries.IForgeRegistry;

public class FluidHandler {
	public static Item fluid;

	public static void registerFluidItems(IForgeRegistry<Item> registry) {
		ItemLoader.register(registry, fluid = new ItemFluid(), false);
	}

	public static ItemStack getFluidFromBlock(Block block) {
		ItemStack stack = ItemStack.EMPTY;
		if ((block == Blocks.WATER || block == Blocks.FLOWING_WATER)) {
			stack = ItemFluid.createStack(FluidRegistry.WATER);
		} else if ((block == Blocks.LAVA || block == Blocks.FLOWING_LAVA)) {
			stack = ItemFluid.createStack(FluidRegistry.LAVA);
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
			stack = ItemFluid.createStack(FluidRegistry.WATER);
			if (doDrain.test(stack)) {
				world.setBlockToAir(pos);
			} else {
				return ItemStack.EMPTY;
			}
		} else if ((block == Blocks.LAVA || block == Blocks.FLOWING_LAVA)
				&& s.getValue(BlockLiquid.LEVEL).intValue() == 0) {
			stack = ItemFluid.createStack(FluidRegistry.LAVA);
			if (doDrain.test(stack)) {
				world.setBlockToAir(pos);
			} else {
				return ItemStack.EMPTY;
			}
		} else if (block instanceof IFluidBlock) {
			System.out.println(block);
			IFluidBlock f = (IFluidBlock) block;
			FluidStack fluidstack = f.drain(world, pos, false);
			System.out.println(fluidstack);
			if (fluidstack != null && fluidstack.amount >= 1000) {
				stack = getFluidFromBlock(block);
				System.out.println(stack);
				if (!stack.isEmpty() && doDrain.test(stack)) {
					f.drain(world, pos, true);
					world.setBlockToAir(pos);
					return stack;
				} else {
					return ItemStack.EMPTY;
				}
			} else {
				return ItemStack.EMPTY;
			}
		}
		return stack;
	}
}
