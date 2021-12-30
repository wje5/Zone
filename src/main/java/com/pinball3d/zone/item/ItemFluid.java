package com.pinball3d.zone.item;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.pinball3d.zone.FluidHandler;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class ItemFluid extends ZoneItem {
	public ItemFluid() {
		super("fluid");

	}

	public static FluidStack getFluid(ItemStack container) {
		return FluidStack.loadFluidStackFromNBT(container.getTagCompound());
	}

	public static ItemStack createStack(Fluid fluid) {
		return createStack(fluid, 1);
	}

	public static ItemStack createStack(Fluid fluid, int count) {
		ItemStack stack = new ItemStack(FluidHandler.fluid);
		stack.setTagCompound(new FluidStack(fluid, 1000).writeToNBT(new NBTTagCompound()));
		stack.setCount(count);
		return stack;
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		FluidStack fluidStack = getFluid(stack);
		if (fluidStack == null) {
			return super.getItemStackDisplayName(stack);
		}
		return fluidStack.getLocalizedName();
	}

	@Override
	public void getSubItems(@Nullable CreativeTabs tab, @Nonnull NonNullList<ItemStack> subItems) {
		if (!this.isInCreativeTab(tab)) {
			return;
		}
		for (Fluid fluid : FluidRegistry.getRegisteredFluids().values()) {
			if (fluid != FluidRegistry.WATER && fluid != FluidRegistry.LAVA && !fluid.getName().equals("milk")) {
				subItems.add(createStack(fluid, 1));
			}
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack itemstack = player.getHeldItem(hand);
		FluidStack fluidStack = getFluid(itemstack);
		if (fluidStack == null) {
			return ActionResult.newResult(EnumActionResult.PASS, itemstack);
		}
		RayTraceResult mop = this.rayTrace(world, player, false);
		if (mop == null || mop.typeOfHit != RayTraceResult.Type.BLOCK) {
			return ActionResult.newResult(EnumActionResult.PASS, itemstack);
		}
		BlockPos clickPos = mop.getBlockPos();
		if (world.isBlockModifiable(player, clickPos)) {
			BlockPos targetPos = clickPos.offset(mop.sideHit);
			if (player.canPlayerEdit(targetPos, mop.sideHit, itemstack)) {
				FluidActionResult result = FluidUtil.tryPlaceFluid(player, world, targetPos, itemstack, fluidStack);
				if (result.isSuccess() && !player.capabilities.isCreativeMode) {
					player.addStat(StatList.getObjectUseStats(this));
					itemstack.shrink(1);
					ItemStack drained = result.getResult();
					return ActionResult.newResult(EnumActionResult.SUCCESS, drained);
				}
			}
		}
		return ActionResult.newResult(EnumActionResult.FAIL, itemstack);
	}

	@Nullable
	@Override
	public String getCreatorModId(@Nonnull ItemStack itemStack) {
		FluidStack fluidStack = getFluid(itemStack);
		String modId = FluidRegistry.getModId(fluidStack);
		return modId != null ? modId : super.getCreatorModId(itemStack);
	}

	@Override
	public ICapabilityProvider initCapabilities(@Nonnull ItemStack stack, NBTTagCompound nbt) {
		return new FluidWrapper(stack);
	}

	public static class FluidWrapper implements IFluidHandlerItem, ICapabilityProvider {
		protected ItemStack stack;

		public FluidWrapper(ItemStack stack) {
			this.stack = stack;
		}

		@Override
		public IFluidTankProperties[] getTankProperties() {
			return new FluidTankProperties[] { new FluidTankProperties(ItemFluid.getFluid(stack), 1000) };
		}

		@Override
		public int fill(FluidStack resource, boolean doFill) {
			return 0;
		}

		@Override
		public FluidStack drain(FluidStack resource, boolean doDrain) {
			if (resource == null || resource.amount < 1000) {
				return null;
			}
			FluidStack fs = ItemFluid.getFluid(stack);
			if (fs != null && fs.isFluidEqual(resource)) {
				if (doDrain) {
					stack.shrink(1);
				}
				return fs;
			}
			return null;
		}

		@Override
		public FluidStack drain(int maxDrain, boolean doDrain) {
			if (maxDrain < 1000) {
				return null;
			}
			FluidStack fs = ItemFluid.getFluid(stack);
			if (fs != null) {
				if (doDrain) {
					stack.shrink(1);
				}
				return fs;
			}
			return null;
		}

		@Override
		public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
			return capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY;
		}

		@Override
		public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
			if (capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY) {
				return CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY.cast(this);
			}
			return null;
		}

		@Override
		public ItemStack getContainer() {
			return stack;
		}
	}
}
